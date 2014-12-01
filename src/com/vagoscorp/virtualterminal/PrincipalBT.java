package com.vagoscorp.virtualterminal;

import android.annotation.TargetApi;
import android.app.ActionBar;
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
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.ComunicBT;

public class PrincipalBT extends Activity implements OnComunicationListener,OnConnectionListener {

	public TextView RX;// Received Data
    public TextView RXn;// Received Data
	public EditText TX;// Data to Send
	public Button Conect;
	public Button Chan_Ser;
	public Button Send;
	public TextView SD;
    public TextView inputTyp;
//	private CheckBox TN;
    ScrollView scro;
    ScrollView scron;
    LinearLayout commander;

    Button comm1;
    Button comm2;
    Button comm3;
    Button comm4;
    Button comm5;
    Button comm6;
    Button comm7;
    Button comm8;
    Button comm9;
    Button comm10;
    Button comm11;
    Button comm12;

	public boolean N;
    public boolean RN;
    public boolean CM;
	public int SC;
    public int sendTyp = TXTSEND;

	public BluetoothAdapter BTAdapter;
	public BluetoothDevice[] BondedDevices;
	public BluetoothDevice mDevice;
	public int mDeviceIndex;
	ComunicBT comunic;

	public int index;
	public String[] DdeviceNames;
	public String myName;
	public String myAddress;

	private final int REQUEST_ENABLE_BT = 1;
	private final int SEL_BT_DEVICE = 2;
	private final int defIndex = 0;

	public static final String LD = "LD";
	public static final String indev = "indev";
	public static final String comm = "comm";
	public static final String commN = "commN";
	public static final String commT = "commT";
	public static final int defNcomm = 0;
	public static final boolean defBcomm = false;
    public static final int TXTSEND = 0;
    public static final int NUMSEND = 1;
    public static final int BINSEND = 2;
    public static final int HEXSEND = 3;
    public boolean lowLvl = true;
	boolean pro = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		BTAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent tip = getIntent();
		if (BTAdapter == null) {
			Toast.makeText(PrincipalBT.this, R.string.NB, Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}
		RX = (TextView) findViewById(R.id.RX);
        RXn = (TextView) findViewById(R.id.RXn);
		TX = (EditText) findViewById(R.id.TX);
//		TN = (CheckBox) findViewById(R.id.TN);
		SD = (TextView) findViewById(R.id.label_ser);
		Conect = (Button) findViewById(R.id.Conect);
		Chan_Ser = (Button) findViewById(R.id.chan_ser);
		Send = (Button) findViewById(R.id.Send);
        scro = (ScrollView)findViewById(R.id.scro);
        scron = (ScrollView)findViewById(R.id.scron);
        commander = (LinearLayout)findViewById(R.id.commander);
        inputTyp = (TextView)findViewById(R.id.inputTyp);
        OnLongClickListener oLClistener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
				int n = nComm(view);
                if(n != 0 && TX.length() > 0) {
                    String Message = TX.getText().toString();
                    SharedPreferences shapre = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = shapre.edit();
                    N = true;
                    switch(sendTyp) {
                        case(TXTSEND): {
                            editor.putString(comm + n, Message);
                            editor.putString(commN + n, Message);
                            N = false;
                            break;
                        }
                        case(NUMSEND): {
                            int Messagen = Integer.parseInt(Message);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, "#" + Message);
                            break;
                        }
                        case(BINSEND): {
                            int Messagen = Integer.parseInt(Message, 2);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, "0b" + Message);
                            break;
                        }
                        case(HEXSEND): {
                            int Messagen = Integer.parseInt(Message, 16);
                            editor.putInt(comm + n, Messagen);
                            editor.putString(commN + n, "0x" + Message);
                            break;
                        }
                    }
//					if(!N) {
//                        editor.putString(comm + n, Message);
//                        editor.putString(commN + n, Message);
//					}else {
//						int Messagen = Integer.parseInt(Message);
//						editor.putInt(comm + n, Messagen);
//						editor.putString(commN + n, "#" + Messagen);
//					}
                    editor.putBoolean(commT + n, N);
                    editor.commit();
                    UcommUI();
                }
				return true;
			}
		};
        comm1  = (Button)findViewById(R.id.comm1);
        comm2  = (Button)findViewById(R.id.comm2);
        comm3  = (Button)findViewById(R.id.comm3);
        comm4  = (Button)findViewById(R.id.comm4);
        comm5  = (Button)findViewById(R.id.comm5);
        comm6  = (Button)findViewById(R.id.comm6);
        comm7  = (Button)findViewById(R.id.comm7);
        comm8  = (Button)findViewById(R.id.comm8);
        comm9  = (Button)findViewById(R.id.comm9);
        comm10 = (Button)findViewById(R.id.comm10);
        comm11 = (Button)findViewById(R.id.comm11);
        comm12 = (Button)findViewById(R.id.comm12);
        comm1.setOnLongClickListener(oLClistener);
        comm2.setOnLongClickListener(oLClistener);
        comm3.setOnLongClickListener(oLClistener);
        comm4.setOnLongClickListener(oLClistener);
        comm5.setOnLongClickListener(oLClistener);
        comm6.setOnLongClickListener(oLClistener);
        comm7.setOnLongClickListener(oLClistener);
        comm8.setOnLongClickListener(oLClistener);
        comm9.setOnLongClickListener(oLClistener);
        comm10.setOnLongClickListener(oLClistener);
        comm11.setOnLongClickListener(oLClistener);
        comm12.setOnLongClickListener(oLClistener);
//		N = false;
        RN =  false;
        CM =  false;
		index = defIndex;
		comunic = new ComunicBT();
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
        MenuItem mItem = menu.findItem(R.id.commMode);
        if(pro)
            mItem.setTitle(R.string.commMode);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.commMode: {
                if(pro) {
                    if(!CM) {
                        CM = true;
                        item.setTitle(R.string.exitCommMode);
                        commander.setVisibility(View.VISIBLE);
                    }else {
                        CM = false;
                        item.setTitle(R.string.commMode);
                        commander.setVisibility(View.GONE);
                    }
                }else
                    Toast.makeText(this, R.string.noPro, Toast.LENGTH_LONG).show();
                return true;
            }
            case R.id.rcvTyp: {
                if(!RN) {
                    RN = true;
                    item.setTitle(R.string.defRCV);
                    scro.setVisibility(View.GONE);
                    scron.setVisibility(View.VISIBLE);
                }else {
                    RN = false;
                    item.setTitle(R.string.numRCV);
                    scro.setVisibility(View.VISIBLE);
                    scron.setVisibility(View.GONE);
                }
                return true;
            }
            case R.id.endCOM: {
                comunic.Detener_Actividad();
                return true;
            }
            case R.id.txtSend: {
                sendTyp = TXTSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TX);
                TX.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.txt);
                return true;
            }
            case R.id.numSend: {
                sendTyp = NUMSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXn);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputTyp.setText(R.string.num);
                return true;
            }
            case R.id.binSend: {
                sendTyp = BINSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXb);
                TX.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputTyp.setText(R.string.bin);
                return true;
            }
            case R.id.hexSend: {
                sendTyp = HEXSEND;
                TX.setText("");
                TX.setHint(R.string.Text_TXh);
                TX.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTyp.setText(R.string.hex);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

	private void initBTD(BluetoothDevice[] BonDev) {
		myName = BTAdapter.getName();
		myAddress = BTAdapter.getAddress();
		if (BonDev.length > 0) {
			// RX.append("no gut");
			if (BonDev.length < index)
				index = 0;
			mDevice = BondedDevices[index];
			SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
			if(SC == MainActivity.SERVER)
				SD.setText(myName + "\n" + myAddress);
			Conect.setEnabled(true);
		} else {
			// RX.append("gut");
			SD.setText(R.string.NoPD);
			Conect.setEnabled(false);
		}
	}
	
	@Override
	protected void onResume() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		index = shapre.getInt(indev, defIndex);
		if(SC == MainActivity.SERVER)
			Chan_Ser.setVisibility(View.GONE);//Enabled(false);
		if (BTAdapter.isEnabled()) {
			BondedDevices = BTAdapter.getBondedDevices().toArray(
				new BluetoothDevice[0]);
			initBTD(BondedDevices);
		} else {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		UcommUI();
		super.onResume();
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
		comunic.Detener_Actividad();
		super.onDestroy();
	}
	
	int nComm(View view) {
		int num = 0;
		switch (view.getId()) {
		case R.id.comm1:{
			num = 1;
			break;
		}
		case R.id.comm2:{
			num = 2;			
			break;
		}
		case R.id.comm3:{
			num = 3;
			break;
		}
		case R.id.comm4:{
			num = 4;
			break;
		}
		case R.id.comm5:{
			num = 5;
			break;
		}
		case R.id.comm6:{
			num = 6;
			break;
		}
		case R.id.comm7:{
			num = 7;
			break;
		}
		case R.id.comm8:{
			num = 8;
			break;
		}
		case R.id.comm9:{
			num = 9;
			break;
		}
		case R.id.comm10:{
			num = 10;
			break;
		}
		case R.id.comm11:{
			num = 11;			
			break;
						}
		case R.id.comm12:{
			num = 12;
			break;
		}
		}
		return num;
	}
	
	public void commClick(View view) {
		int n = nComm(view);
		if(n != 0) {
			SharedPreferences shapre = getPreferences(MODE_PRIVATE);
			if(shapre.getBoolean(commT + n, defBcomm))
				comunic.enviar(shapre.getInt(comm + n, defNcomm));
			else
				comunic.enviar(shapre.getString(comm + n, getResources().getString(R.string.commDVal)));
		}
	}
	
	void UcommUI() {
		SharedPreferences shapre = getPreferences(MODE_PRIVATE);
		comm1.setText(shapre.getString(commN + 1, getResources().getString(R.string.commDVal)));
		comm2.setText(shapre.getString(commN + 2, getResources().getString(R.string.commDVal)));
		comm3.setText(shapre.getString(commN + 3, getResources().getString(R.string.commDVal)));
		comm4.setText(shapre.getString(commN + 4, getResources().getString(R.string.commDVal)));
		comm5.setText(shapre.getString(commN + 5, getResources().getString(R.string.commDVal)));
		comm6.setText(shapre.getString(commN + 6, getResources().getString(R.string.commDVal)));
		comm7.setText(shapre.getString(commN + 7, getResources().getString(R.string.commDVal)));
		comm8.setText(shapre.getString(commN + 8, getResources().getString(R.string.commDVal)));
		comm9.setText(shapre.getString(commN + 9, getResources().getString(R.string.commDVal)));
		comm10.setText(shapre.getString(commN + 10, getResources().getString(R.string.commDVal)));
		comm11.setText(shapre.getString(commN + 11, getResources().getString(R.string.commDVal)));
		comm12.setText(shapre.getString(commN + 12, getResources().getString(R.string.commDVal)));
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
			Toast.makeText(this, R.string.NoPD, Toast.LENGTH_SHORT).show();
	}

	public void conect(View view) {
		if(comunic.estado == comunic.NULL) {
			if(SC == MainActivity.CLIENT) {
				comunic = new ComunicBT(this, mDevice);
			}else if(SC == MainActivity.SERVER) {
				comunic = new ComunicBT(this, BTAdapter);
			}
			comunic.setComunicationListener(this);
			comunic.setConnectionListener(this);
			Chan_Ser.setEnabled(false);
			Conect.setText(getResources().getString(R.string.Button_Conecting));
			comunic.execute();
		}else
			comunic.Detener_Actividad();
	}

//	public void tnum(View view) {
//		BTX(view);
//		if (TN.isChecked()) {
//			TX.setHint(R.string.Text_TXn);
//			TX.setInputType(InputType.TYPE_CLASS_NUMBER);
//			N = true;
//		} else {
//			TX.setHint(R.string.Text_TX);
//			TX.setInputType(InputType.TYPE_CLASS_TEXT);
//			N = false;
//		}
//	}

	public void enviar(View view) {
		if (TX.length() > 0) {
			String Message = TX.getText().toString();
            switch(sendTyp) {
                case(TXTSEND):{
                    comunic.enviar(Message);
                    break;
                }
                case(NUMSEND):{
                    try {
                        int Messagen = Integer.parseInt(Message);
                        comunic.enviar(Messagen);
                    }catch(NumberFormatException nEx) {
                        nEx.printStackTrace();
                        Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case(BINSEND):{
                    try {
                        int Messagen = Integer.parseInt(Message, 2);
                        comunic.enviar(Messagen);
                    }catch(NumberFormatException nEx) {
                        nEx.printStackTrace();
                        Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case(HEXSEND):{
                    try {
                        int Messagen = Integer.parseInt(Message, 16);
				        comunic.enviar(Messagen);
                    }catch(NumberFormatException nEx) {
                        nEx.printStackTrace();
                        Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
//			if (N) {
//				int Messagen = Integer.parseInt(Message);
//				comunic.enviar(Messagen);
//			} else
//                comunic.enviar(Message);
		}
	}

	public void BTX(View view) {
		TX.setText("");
	}

	public void BRX(View view) {
        if(!RN)
            RX.setText("");
        else
            RXn.setText("");
	}

	@Override
	public void onDataReceived(String dato, int[] ndato) {
        RX.append(dato);
        if(RN) {
            for(int val:ndato)
                RXn.append(val + " ");
        }
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
