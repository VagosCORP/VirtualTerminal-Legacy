package com.vagoscorp.virtualterminal.deprecated;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.util.EncodingUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.vagoscorp.virtualterminal.R;
import com.vagoscorp.virtualterminal.Set_Server;

public class PrincipalW extends Activity {

	public TextView RX;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
	private CheckBox TN;

	public String serverip;// IP to Connect
	public int serverport;// Port to Connect
	
	boolean comunic = false;
	public boolean con = false;
	public boolean N = false;
	public int SC;

	COM_WiFi conh;
	WifiManager WFM;

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
		comunic = false;
		serverip = defIP;
		serverport = defPort;
		SC = tip.getIntExtra(C.typ, C.CLIENT);
		con = false;
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
		if(con)
			conh.chao();
//		if (conh.serverSocket != null) {
//			try {
//				conh.serverSocket.close();
//				conh.serverSocket.bind(null);
//			} catch (IOException e) {
//				
//			}
//		}
//		if(comunic) {
//			if (conh.socket != null) {
//				try {
//					conh.socket.close();
//				} catch (IOException e) {
//
//				}
//
//			} else {
//				conh.cancel(true);
//			}
//		}
		Toast.makeText(this, R.string.fin2, Toast.LENGTH_SHORT).show();
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
		if(!con) {
			if(!comunic) {
				if(SC == C.CLIENT)
					conh = new COM_WiFi(serverip, serverport);
				else if(SC == C.SERVER)
					conh = new COM_WiFi(serverport);
				conh.execute();
			}
			con = true;
		}else {
			conh.chao();
		}
	}

	public void Cancelar(MenuItem mi) {
		if(con) {
			conh.cancel(true);
			conh.chao();
		}
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
		if (TX.length() > 0 && comunic) {
			String Message = TX.getText().toString();
			if (N) {
				/*Toast.makeText(PrincipalW.this, Cases[5],
						Toast.LENGTH_SHORT).show();*/
				int Messagen = Integer.parseInt(Message);
				conh.enviar(Messagen);
			} else {
				/*Toast.makeText(PrincipalW.this, Cases[6],
						Toast.LENGTH_SHORT).show();*/
				conh.enviar(Message);
			}
		}
	}

	public void BTX(View view) {
		TX.setText("");
	}

	public void BRX(View view) {
		RX.setText("");
	}

	public class COM_WiFi extends AsyncTask<Void, byte[], Integer> {
	
		final	byte	CONECTADOs = 1;
		final	byte	IO_HABILITADOs = 2;
		final	byte	IO_EXCEPTIONs = 3;
		final	byte	DATO_RECIBIDOs = 7;
		final	byte[]	CONECTADO = {1};
		final	byte[]	IO_HABILITADO = {2};
		final	byte[]	IO_EXCEPTION = {3};
		final	byte[]	DATO_RECIBIDO = {7};
		
		int CS;
		InetSocketAddress isa;
		Socket socket = null;
		ServerSocket serverSocket = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		
		public COM_WiFi(int port) {
			// TODO Auto-generated constructor stub
			CS = C.SERVER;
			serverport = port;
			Conect.setEnabled(true);
		}
		
		public COM_WiFi(String ip, int port) {
			// TODO Auto-generated constructor stub
			CS = C.CLIENT;
			isa = new InetSocketAddress(ip, port);
			Conect.setEnabled(false);
		}
		
		public void enviar(int dato) {
			if(comunic) {
				try {
					dos.writeByte(dato);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void enviar(String dato) {
			if(comunic) {
				try {
					dos.writeBytes(dato);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			socket = null;
			serverSocket = null;
			comunic = false;
			Chan_Ser.setEnabled(false);
			Conect.setText("Conectando...");
			super.onPreExecute();
		}
	
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				if(CS == C.CLIENT) {
					socket = new Socket();
					socket.connect(isa, 7000);
				}else if(CS == C.SERVER) {
					serverSocket = new ServerSocket(serverport);
					socket = serverSocket.accept();
					serverSocket.close();
				}
				if(socket.isConnected()) {
					publishProgress(CONECTADO);
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());
					comunic = true;
					publishProgress(IO_HABILITADO);
					byte[] buffer;
					int len;
					while(comunic && socket.isConnected()) {
						buffer = new byte[1024];
						len = dis.read(buffer);
						if(len != -1) {
							publishProgress(DATO_RECIBIDO,(len+"").getBytes(),buffer);
						}else if(socket.isClosed())
							break;
					}
					comunic = false;
					dis.close();
					dos.close();
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				publishProgress(IO_EXCEPTION);
				e.printStackTrace();
			}
			return 1;
		}
	
		@Override
		protected void onProgressUpdate(byte[]... values) {
			// TODO Auto-generated method stub
			byte[] action = values[0];
			switch(action[0]) {
			case DATO_RECIBIDOs: {
				int lon = Integer.parseInt(new String(values[1]));
				RX.append(EncodingUtils.getAsciiString(values[2],0,lon));
				break;
			}
			case CONECTADOs: {
				
				break;
			}
			case IO_HABILITADOs: {
				Chan_Ser.setEnabled(false);
				Conect.setText("Desconectar");
				Conect.setEnabled(true);
				Send.setEnabled(true);
				break;
			}
			case IO_EXCEPTIONs: {
				
				break;
			}
			}
			super.onProgressUpdate(values);
		}
	
		public void chao() {
			try {
				if (comunic) {
					comunic = false;
					dis.close();
					dos.close();
				}
				if (socket != null)
					socket.close();
				if(CS == C.SERVER && serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {

			}
			Conect.setText("Conectar");
			Conect.setEnabled(true);
			Chan_Ser.setEnabled(true);
			Send.setEnabled(false);
			con = false;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			Toast.makeText(PrincipalW.this, "post execute", 
					Toast.LENGTH_SHORT).show();
			chao();
			super.onPostExecute(result);
		}
	
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			Toast.makeText(PrincipalW.this, "cancelled", 
					Toast.LENGTH_SHORT).show();
			super.onCancelled();
		}
	}
}
