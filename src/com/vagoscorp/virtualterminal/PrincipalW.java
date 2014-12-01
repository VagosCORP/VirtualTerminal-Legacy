package com.vagoscorp.virtualterminal;

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
import android.text.InputType;
import android.util.Log;
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

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.android.Comunic;

public class PrincipalW extends Activity implements OnComunicationListener,OnConnectionListener {

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

	public String serverip;// IP to Connect
	public int serverport;// Port to Connect
	
	public boolean N;
    public boolean RN;
    public boolean CM;
	public int SC;
    public int sendTyp = TXTSEND;

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
		Intent tip = getIntent();
		WFM = (WifiManager) getSystemService(WIFI_SERVICE);
		CTM = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
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
		UcommUI();
		super.onResume();
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
		comm1.setText(shapre.getString(commT + 1, getResources().getString(R.string.commDVal)));
		comm2.setText(shapre.getString(commT + 2, getResources().getString(R.string.commDVal)));
		comm3.setText(shapre.getString(commT + 3, getResources().getString(R.string.commDVal)));
		comm4.setText(shapre.getString(commT + 4, getResources().getString(R.string.commDVal)));
		comm5.setText(shapre.getString(commT + 5, getResources().getString(R.string.commDVal)));
		comm6.setText(shapre.getString(commT + 6, getResources().getString(R.string.commDVal)));
		comm7.setText(shapre.getString(commT + 7, getResources().getString(R.string.commDVal)));
		comm8.setText(shapre.getString(commT + 8, getResources().getString(R.string.commDVal)));
		comm9.setText(shapre.getString(commT + 9, getResources().getString(R.string.commDVal)));
		comm10.setText(shapre.getString(commT + 10, getResources().getString(R.string.commDVal)));
		comm11.setText(shapre.getString(commT + 11, getResources().getString(R.string.commDVal)));
		comm12.setText(shapre.getString(commT + 12, getResources().getString(R.string.commDVal)));
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
            switch (sendTyp) {
                case (TXTSEND): {
                    comunic.enviar(Message);
                    break;
                }
                case (NUMSEND): {
                    try {
                        int Messagen = Integer.parseInt(Message);
                        comunic.enviar(Messagen);
                    } catch (NumberFormatException nEx) {
                        nEx.printStackTrace();
                        Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case (BINSEND): {
                    try {
                        int Messagen = Integer.parseInt(Message, 2);
                        comunic.enviar(Messagen);
                    } catch (NumberFormatException nEx) {
                        nEx.printStackTrace();
                        Toast.makeText(this, R.string.numFormExc, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case (HEXSEND): {
                    try {
                        int Messagen = Integer.parseInt(Message, 16);
                        comunic.enviar(Messagen);
                    } catch (NumberFormatException nEx) {
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
//            comunic.enviar(Message);
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
