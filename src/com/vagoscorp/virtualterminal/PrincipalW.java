package com.vagoscorp.virtualterminal;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.Comunic;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrincipalW extends Activity implements OnComunicationListener,OnConnectionListener {

	public TextView RX;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
	private CheckBox TN;

	public String serverip;// IP to Connect
	public int serverport;// Port to Connect
	
	public boolean N = false;
	public int SC;

	WifiManager WFM;
	ConnectivityManager CTM;
	String myIP = "";
	Comunic comunic;

	private final int REQUEST_ENABLE_WIFI = 15;
	private final int REQUEST_CHANGE_SERVER = 12;
	public static final String SI = "SIP";
	public static final String SP = "SPort";
	public static final String defIP = "10.0.0.6";
	public static final int defPort = 2000;
	boolean pro = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		Intent tip = getIntent();
		WFM = (WifiManager) getSystemService(WIFI_SERVICE);
		CTM = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		RX = (TextView) findViewById(R.id.RX);
		TX = (EditText) findViewById(R.id.TX);
		TN = (CheckBox) findViewById(R.id.TN);
		SD = (TextView) findViewById(R.id.label_ser);
		Conect = (Button) findViewById(R.id.Conect);
		Chan_Ser = (Button) findViewById(R.id.chan_ser);
		Send = (Button) findViewById(R.id.Send);
		N = false;
		comunic = new Comunic();
		serverip = defIP;
		serverport = defPort;
		SC = tip.getIntExtra(MainActivity.typ, MainActivity.CLIENT);
		pro = tip.getBooleanExtra(MainActivity.lvl, false);
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
		setupActionBar();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar aB = getActionBar();//Support
			if(aB != null)
				aB.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principal, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		NetworkInfo nWI = CTM.getActiveNetworkInfo();
		if (!WFM.isWifiEnabled() || !(nWI != null && nWI.getState() ==
				NetworkInfo.State.CONNECTED)) {
//			WFM.setWifiEnabled(true);
			Intent enableIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
			startActivityForResult(enableIntent, REQUEST_ENABLE_WIFI);
		}else {
			int ipAddress = WFM.getConnectionInfo().getIpAddress();
//			myIP = Formatter.formatIpAddress(ipAddress);
			if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
		        ipAddress = Integer.reverseBytes(ipAddress);
			byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
			try {
				myIP = InetAddress.getByAddress(ipByteArray).getHostAddress();
		    }catch (UnknownHostException ex) {
		        Log.e("WIFIIP", "Unable to get host address.");
		        myIP = "0.0.0.0";
		    }
		}
		if(SC == MainActivity.SERVER)
			Chan_Ser.setVisibility(View.GONE);//Enabled(false);
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		serverip = shapre.getString(SI, defIP);
		serverport = shapre.getInt(SP, defPort);
		SD.setText(serverip + ":" + serverport);
		if(SC == MainActivity.SERVER)
			SD.setText(myIP + ":" + serverport);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		comunic.Detener_Actividad();
		super.onDestroy();
	}

	public void Chan_Ser(View view) {
		Intent CS = new Intent(this, Set_Server.class);
		CS.putExtra(SI, serverip);
		CS.putExtra(SP, serverport);
		startActivityForResult(CS, REQUEST_CHANGE_SERVER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_ENABLE_WIFI: {
			if(!WFM.isWifiEnabled()/*resultCode != Activity.RESULT_OK*/) {
				Toast.makeText(this, R.string.EnWF, Toast.LENGTH_SHORT).show();
				finish();
			}else {
				NetworkInfo nWI = CTM.getActiveNetworkInfo();
				if(!(nWI != null && nWI.getType() == ConnectivityManager.TYPE_WIFI &&
						nWI.getState() == NetworkInfo.State.CONNECTED)){
					Toast.makeText(this, R.string.EWF, Toast.LENGTH_SHORT).show();
					finish();
				}
			}
			break;
		}
		case REQUEST_CHANGE_SERVER: {
			if (resultCode == Activity.RESULT_OK) {
				serverip = data.getStringExtra(Set_Server.NSI);
				serverport = data.getIntExtra(Set_Server.NSP, defPort);
				SharedPreferences shapre = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = shapre.edit();
				editor.putString(SI, serverip);
				editor.putInt(SP, serverport);
				editor.commit();
				if(SC == MainActivity.CLIENT)
					SD.setText(serverip + ":" + serverport);
				else if(SC == MainActivity.SERVER)
					SD.setText(myIP + ":" + serverport);
			}
			break;
		}
		}
	}

	public void conect(View view) {
		if(comunic.estado == comunic.NULL) {
			if(SC == MainActivity.CLIENT) {
				comunic = new Comunic(this, serverip, serverport);
			}else if(SC == MainActivity.SERVER) {
				comunic = new Comunic(this, serverport);
			}
			comunic.setComunicationListener(this);
			comunic.setConnectionListener(this);
			Chan_Ser.setEnabled(false);
			Conect.setText(getResources().getString(R.string.Button_Conecting));
			comunic.execute();
		}else
			comunic.Detener_Actividad();
	}

	public void Cancelar(MenuItem mi) {
		comunic.Detener_Actividad();
	}

	public void tnum(View view) {
		BTX(view);
		if (TN.isChecked()) {
			TX.setHint(R.string.Text_TXn);
			TX.setInputType(InputType.TYPE_CLASS_NUMBER);
			N = true;
		} else {
			TX.setHint(R.string.Text_TX);
			TX.setInputType(InputType.TYPE_CLASS_TEXT);
			N = false;
		}
	}

	public void enviar(View view) {
		if (TX.length() > 0) {
			String Message = TX.getText().toString();
			if (N) {
				int Messagen = Integer.parseInt(Message);
				comunic.enviar(Messagen);
			} else
				comunic.enviar(Message);
		}
	}

	public void BTX(View view) {
		TX.setText("");
	}

	public void BRX(View view) {
		RX.setText("");
	}

	@Override
	public void onDataReceived(String dato) {
		RX.append(dato);
	}

	@Override
	public void onConnectionstablished() {
		Chan_Ser.setEnabled(false);
		Conect.setText(getResources().getString(R.string.Button_DisConect));
		Conect.setEnabled(true);
		Send.setEnabled(true);
	}

	@Override
	public void onConnectionfinished() {
		Conect.setText(getResources().getString(R.string.Button_Conect));
		Conect.setEnabled(true);
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
	}
}
