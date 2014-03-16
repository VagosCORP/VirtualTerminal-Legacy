package com.vagoscorp.virtualterminal.deprecated;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.http.util.EncodingUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.vagoscorp.virtualterminal.Device_List;
import com.vagoscorp.virtualterminal.MainActivity;
import com.vagoscorp.virtualterminal.R;

public class PrincipalBT extends Activity {

	public TextView RX;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
	private CheckBox TN;

	boolean comunic = false;
	public boolean con;
	public boolean N;
	public int SC;

	COM_Bluetooth conh;

	public BluetoothAdapter BTAdapter;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;

	public int index;
	public String[] DdeviceNames;

	private final int REQUEST_ENABLE_BT = 1;
	private final int SEL_BT_DEVICE = 2;
	private final int defIndex = 0;

	public static final String LD = "LD";
	public static final String indev = "indev";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BTAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent tip = getIntent();
		if (BTAdapter == null) {
			Toast.makeText(PrincipalBT.this, R.string.NB, Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}
		setContentView(R.layout.principal);
		RX = (TextView) findViewById(R.id.RX);
		TX = (EditText) findViewById(R.id.TX);
		TN = (CheckBox) findViewById(R.id.TN);
		SD = (TextView) findViewById(R.id.label_ser);
		Conect = (Button) findViewById(R.id.Conect);
		Chan_Ser = (Button) findViewById(R.id.chan_ser);
		Send = (Button) findViewById(R.id.Send);
		N = false;
		con = false;
		index = defIndex;
		comunic = false;
		SC = tip.getIntExtra(MainActivity.typ, MainActivity.CLIENT);
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

	private void initBTD(BluetoothDevice[] BonDev) {
		if (BonDev.length > 0) {
			// RX.append("no gut");
			if (BonDev.length < index)
				index = 0;
			mDevice = BondedDevices[index];
			SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			Conect.setEnabled(true);
		} else {
			// RX.append("gut");
			SD.setText(R.string.Ser_Dat);
			Conect.setEnabled(false);
		}
	}

	@Override
	protected void onStart() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		index = shapre.getInt(indev, defIndex);
		// If Bluetooth is not on, request that it be enabled.
		if (BTAdapter.isEnabled()) {
			BondedDevices = BTAdapter.getBondedDevices().toArray(
					new BluetoothDevice[0]);
			initBTD(BondedDevices);
		} else {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT: {
			if (resultCode != Activity.RESULT_OK) {
				Toast.makeText(this, R.string.EnBT, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				BondedDevices = BTAdapter.getBondedDevices().toArray(
						new BluetoothDevice[0]);
				initBTD(BondedDevices);
			}
			break;
		}
		case SEL_BT_DEVICE: {
			if (resultCode == Activity.RESULT_OK) {
				index = data.getIntExtra(Device_List.SDev, defIndex);
				SharedPreferences shapre = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = shapre.edit();
				editor.putInt(indev, index);
				editor.commit();
				mDevice = BondedDevices[index];
				SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			}
			break;
		}
		}
	}

	@Override
	protected void onDestroy() {
		if(con)
			conh.chao();
		Toast.makeText(this, R.string.fin2, Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	public void Chan_Ser(View view) {
		if (BondedDevices.length > 0) {
			int deviceCount = BondedDevices.length;

			if (mDeviceIndex < deviceCount)
				mDevice = BondedDevices[mDeviceIndex];
			else {
				mDeviceIndex = 0;
				mDevice = BondedDevices[0];
			}
			DdeviceNames = new String[deviceCount];
			int i = 0;
			for (BluetoothDevice device : BondedDevices) {
				DdeviceNames[i++] = device.getName() + "\n"
						+ device.getAddress();
			}
			Intent sel_dev = new Intent(PrincipalBT.this, Device_List.class);
			sel_dev.putExtra(LD, DdeviceNames);
			startActivityForResult(sel_dev, SEL_BT_DEVICE);
		} else
			Toast.makeText(this, R.string.No_Dev, Toast.LENGTH_SHORT).show();
	}

	public void conect(View view) {
		if(!con) {
			if(!comunic) {
				conh = new COM_Bluetooth(SC);
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

	public class COM_Bluetooth extends AsyncTask<Void, byte[], Integer> {
		
		final	byte	CONECTADOs = 1;
		final	byte	IO_HABILITADOs = 2;
		final	byte	IO_EXCEPTIONs = 3;
		final	byte	DATO_RECIBIDOs = 7;
		final	byte[]	CONECTADO = {1};
		final	byte[]	IO_HABILITADO = {2};
		final	byte[]	IO_EXCEPTION = {3};
		final	byte[]	DATO_RECIBIDO = {7};
		final UUID myUUID = UUID
				.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
		int CS;
		boolean Bcon = false;
		BluetoothSocket socket = null;
		BluetoothSocket tempS = null;
		BluetoothServerSocket serverSocket = null;
		BluetoothServerSocket tempSS = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		
		public COM_Bluetooth(int CoS) {
			// TODO Auto-generated constructor stub
			CS = CoS;
			Bcon = false;
			if(CS == C.SERVER)
				Conect.setEnabled(true);
			else if(CS == C.CLIENT)
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
					tempS = null;
					tempS = mDevice.createRfcommSocketToServiceRecord(myUUID);
					if (tempS != null) {
						socket = tempS;
						socket.connect();
						Bcon = true;
					} else
						Bcon = false;
				}else if(CS == C.SERVER) {
					tempSS = null;
					tempSS = BTAdapter.listenUsingRfcommWithServiceRecord(
							"sas", myUUID);
					if (tempSS != null) {
						serverSocket = tempSS;
						socket = serverSocket.accept();
						serverSocket.close();
						Bcon = true;
					} else
						Bcon = false;
				}
				if(Bcon) {
					publishProgress(CONECTADO);
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());
					comunic = true;
					publishProgress(IO_HABILITADO);
					byte[] buffer;
					int len;
					while(comunic && Bcon) {
						buffer = new byte[1024];
						len = dis.read(buffer);
						if(len != -1) {
							publishProgress(DATO_RECIBIDO,(len+"").getBytes(),buffer);
						}/*else if(socket.isClosed())
							break;*/
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
			Toast.makeText(PrincipalBT.this, "post execute", 
					Toast.LENGTH_SHORT).show();
			chao();
			super.onPostExecute(result);
		}
	
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}
}
