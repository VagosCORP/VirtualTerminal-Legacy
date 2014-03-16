package com.vagoscorp.virtualterminal;

import libraries.vagoscorp.comunication.Eventos.OnComunicationListener;
import libraries.vagoscorp.comunication.Eventos.OnConnectionListener;
import libraries.vagoscorp.comunication.android.Comunic;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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
	Comunic comunic;

	public static final String SI = "SIP";
	public static final String SP = "SPort";
	public static final String defIP = "10.0.0.6";
	public static final int defPort = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent tip = getIntent();
		setContentView(R.layout.principal);
		WFM = (WifiManager) getSystemService(WIFI_SERVICE);
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
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
		setupActionBar();
		super.onCreate(savedInstanceState);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principal, menu);
		return true;
	}

	
	 /*@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 Cancelar(item);
		return true;
	 }*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			finish();
			//NavUtils.navigateUpFromSameTask(this);
			//overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	 
	@Override
	protected void onStart() {
		if (!WFM.isWifiEnabled()) {
			WFM.setWifiEnabled(true);
		}
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		serverip = shapre.getString(SI, defIP);
		serverport = shapre.getInt(SP, defPort);
		SD.setText(serverip + ":" + serverport);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
//		if(comunic.estado == comunic.EN_SPERA) {
//			comunic.Detener_Espera();
//		}else if(comunic.estado == comunic.CONECTED) {
//			comunic.Cortar_Conexion();
//		}
		comunic.Detener_Actividad();
		super.onDestroy();
	}

	public void Chan_Ser(View view) {
		Intent CS = new Intent(this, Set_Server.class);
		CS.putExtra(SI, serverip);
		CS.putExtra(SP, serverport);
		startActivityForResult(CS, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			serverip = data.getStringExtra(Set_Server.NSI);
			serverport = data.getIntExtra(Set_Server.NSP, defPort);
			SharedPreferences shapre = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = shapre.edit();
			editor.putString(SI, serverip);
			editor.putInt(SP, serverport);
			editor.commit();
			SD.setText(serverip + ":" + serverport);
		}
	}

	public void conect(View view) {
		if(comunic.estado == comunic.NULL) {
			if(SC == MainActivity.CLIENT) {
				comunic = new Comunic(serverip, serverport, this);
			}else if(SC == MainActivity.SERVER) {
				comunic = new Comunic(serverport, this);
			}
			comunic.setComunicationListener(this);
			comunic.setConnectionListener(this);
			Chan_Ser.setEnabled(false);
			Conect.setText("Conectando...");
			comunic.execute();
		}else {
//			if(comunic.estado == comunic.EN_SPERA) {
//				comunic.Detener_Espera();
//			}else if(comunic.estado == comunic.CONECTED) {
//				comunic.Cortar_Conexion();
//			}
			comunic.Detener_Actividad();
		}
	}

	public void Cancelar(MenuItem mi) {
//		if(comunic.estado == comunic.EN_SPERA) {
//			comunic.Detener_Espera();
//		}else if(comunic.estado == comunic.CONECTED) {
//			comunic.Cortar_Conexion();
//		}
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
				/*Toast.makeText(PrincipalW.this, Cases[5],
						Toast.LENGTH_SHORT).show();*/
				int Messagen = Integer.parseInt(Message);
				comunic.enviar(Messagen);
			} else {
				/*Toast.makeText(PrincipalW.this, Cases[6],
						Toast.LENGTH_SHORT).show();*/
				comunic.enviar(Message);
			}
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
		Conect.setText("Desconectar");
		Conect.setEnabled(true);
		Send.setEnabled(true);
	}

	@Override
	public void onConnectionfinished() {
		Conect.setText("Conectar");
		Conect.setEnabled(true);
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
	}
}
