package com.vagoscorp.virtualterminal;

import libraries.vagoscorp.comunication.Eventos.OnComunicationListener;
import libraries.vagoscorp.comunication.Eventos.OnConnectionListener;
import libraries.vagoscorp.comunication.android.ComunicBT;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class PrincipalBT extends Activity implements OnComunicationListener,OnConnectionListener {

	public TextView RX;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
	private CheckBox TN;


	public boolean N;
	public int SC;

	public BluetoothAdapter BTAdapter;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;
	ComunicBT comunic;

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
		index = defIndex;
		comunic = new ComunicBT();
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
//		if(comunic.estado == comunic.EN_SPERA) {
//			comunic.Detener_Espera();
//		}else if(comunic.estado == comunic.CONECTED) {
//			comunic.Cortar_Conexion();
//		}
		comunic.Detener_Actividad();
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
		if(comunic.estado == comunic.NULL) {
			if(SC == MainActivity.CLIENT) {
				comunic = new ComunicBT(mDevice, this);
			}else if(SC == MainActivity.SERVER) {
				comunic = new ComunicBT(BTAdapter, this);
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
