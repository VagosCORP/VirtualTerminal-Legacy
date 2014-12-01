package com.vagoscorp.virtualterminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button CB;
	Button CW;
	Button SB;
	Button SW;
	
	public static final String typ = "type";
	public static final String lvl = "level";
	public static final int CLIENT = 1;
	public static final int SERVER = 2;
	
	private Intent Init;
	
	boolean SDread = false;
	boolean SDwrite = false;
	File path;
	File[] fileList;
	String[] fileNames;
	int nfil = 0;
	boolean pro = false;
	static String config = "config";
	static String ext = ".vtconfig";
	String bSel;
	String nPro;
	String baseVer = "1\n1\n0\n0\n0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CB = (Button)findViewById(R.id.Sel_BT);
		CW = (Button)findViewById(R.id.Sel_W);
		SB = (Button)findViewById(R.id.Sel_SBT);
		SW = (Button)findViewById(R.id.Sel_SW);
		bSel = getResources().getString(R.string.Button_Sel);
		nPro = getResources().getString(R.string.needPro);
		path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.vagoscorp.vcvt");
		path.mkdirs();
		getNames();
	}
	
	@Override
	protected void onResume() {
		if(nfil == 0) {
			write(config, baseVer);
			getNames();
		}
		procesar(read(config));
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void InitBT(View view) {
		Init = new Intent(this, PrincipalBT.class);
		Init.putExtra(typ, CLIENT);
		Init.putExtra(lvl, pro);
		//type = CLIENT;
		startActivity(Init);
	}

	public void InitW(View view) {
		Init = new Intent(this, PrincipalW.class);
		Init.putExtra(typ, CLIENT);
		Init.putExtra(lvl, pro);
		//type = CLIENT;
		startActivity(Init);
	}

	public void InitBTs(View view) {
		Init = new Intent(this, PrincipalBT.class);
		Init.putExtra(typ, SERVER);
		Init.putExtra(lvl, pro);
		//type = SERVER;
		startActivity(Init);
	}

	public void InitWs(View view) {
		Init = new Intent(this, PrincipalW.class);
		Init.putExtra(typ, SERVER);
		Init.putExtra(lvl, pro);
		//type = SERVER;
		startActivity(Init);
	}
	
	void checkSD() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			SDread = true;
			SDwrite = true;
		}else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			SDread = true;
			SDwrite = false;
		}else {
//			oops
			SDread = false;
			SDwrite = false;
		}
	}
	
	void procesar(String file) {
		String[] enab;
		enab = file.split("\n");
		if(enab.length != 5) {
			write(config, baseVer);
			getNames();
		}else {
			if(enab[0].equals("1")) {
				CB.setEnabled(true); CB.setText(bSel);
			}else {
				CB.setEnabled(false); CB.setText(nPro);
			}
			if(enab[1].equals("1")) {
				CW.setEnabled(true); CW.setText(bSel);
			}else {
				CW.setEnabled(false); CW.setText(nPro);
			}
			if(enab[2].equals("1")) {
				SB.setEnabled(true); SB.setText(bSel);
			}else {
				SB.setEnabled(false); SB.setText(nPro);
			}
			if(enab[3].equals("1")) {
				SW.setEnabled(true); SW.setText(bSel);
			}else {
				SW.setEnabled(false); SW.setText(nPro);
			}
			if(enab[4].equals("1")) pro = true; else pro = false;
		}
	}
	
	void write(String name, String data) {
		checkSD();
		if(SDread && SDwrite) {
			byte[] buff = data.getBytes();
			File file = new File(path, name + ext);
			OutputStream os;
			try {
				os = new FileOutputStream(file);
				os.write(buff);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	String read(String name) {
		checkSD();
		String val = "";
		if(SDread) {
			byte[] buff;
			File file = new File(path, name + ext);
			InputStream is;
			try {
				is = new FileInputStream(file);
				buff = new byte[is.available()];
				is.read(buff);
				is.close();
				val = new String(buff);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return val;
	}
	
	void getNames() {
		checkSD();
		if(SDread && SDwrite) {
			fileList = path.listFiles();
			int i = 0;
			if(fileList != null)
				nfil = fileList.length;
			else
				nfil = 0;
			if(nfil > 0) {
				fileNames = new String[nfil];
				for(File file:fileList) {
					fileNames[i] = file.getName().split(ext)[0];
				}
			}
		}
	}
}
