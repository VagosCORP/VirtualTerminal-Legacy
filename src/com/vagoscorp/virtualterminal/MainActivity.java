package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	public static final String typ = "type";
	public static final int CLIENT = 1;
	public static final int SERVER = 2;
	
	private Intent Init;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void InitBT(View view) {
		Init = new Intent(this, PrincipalBT.class);
		Init. putExtra(typ, CLIENT);
		//type = CLIENT;
		startActivity(Init);
	}

	public void InitW(View view) {
		Init = new Intent(this, PrincipalW.class);
		Init. putExtra(typ, CLIENT);
		//type = CLIENT;
		startActivity(Init);
	}

	public void InitBTs(View view) {
		Init = new Intent(this, PrincipalBT.class);
		Init. putExtra(typ, SERVER);
		//type = SERVER;
		startActivity(Init);
	}

	public void InitWs(View view) {
		Init = new Intent(this, PrincipalW.class);
		Init. putExtra(typ, SERVER);
		//type = SERVER;
		startActivity(Init);
	}
}
