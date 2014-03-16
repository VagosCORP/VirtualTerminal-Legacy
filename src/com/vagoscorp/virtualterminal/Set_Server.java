package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Set_Server extends Activity {

	public static final String NSI = "NSIP";
	public static final String NSP = "NSPort";
	// TextView Server;
	EditText Server_IP;
	EditText Server_Port;
	String IP;
	int Port;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent Data = getIntent();
		IP = Data.getStringExtra(PrincipalW.SI);
		Port = Data.getIntExtra(PrincipalW.SP, PrincipalW.defPort);
		setContentView(R.layout.set_server);
		Server_IP = (EditText) findViewById(R.id.Server_IP);
		Server_Port = (EditText) findViewById(R.id.Server_Port);
		// Show the Up button in the action bar.
		// setupActionBar();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Server.setText(IP+":"+Port);
		Server_IP.setText(IP);
		Server_Port.setText(Port + "");
	}

	public void Cambiar(View view) {
		final String SIP = Server_IP.getText().toString();
		final int SPort = Integer.parseInt(Server_Port.getText().toString());
		Intent result = new Intent("RESULT_ACTION");
		result.putExtra(NSI, SIP);
		result.putExtra(NSP, SPort);
		setResult(Activity.RESULT_OK, result);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	/*
	 * @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void setupActionBar() {
	 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	 * getActionBar().setDisplayHomeAsUpEnabled(true); } }
	 */

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.set_server, menu); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: // This ID represents the
	 * Home or Up button. In the case of this // activity, the Up button is
	 * shown. Use NavUtils to allow users // to navigate up one level in the
	 * application structure. For // more details, see the Navigation pattern on
	 * Android Design: // //
	 * http://developer.android.com/design/patterns/navigation.html#up-vs-back
	 * // NavUtils.navigateUpFromSameTask(Set_Server.this); return true; }
	 * return super.onOptionsItemSelected(item); }
	 */
}
