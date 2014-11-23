package com.vagoscorp.virtualterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Device_List extends Activity {

	ListView LD;
	String[] LDev;
	ArrayAdapter<String> Adapter;
	public static final String SDev = "SD";
	OnItemClickListener IS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent SD = getIntent();
		LDev = SD.getStringArrayExtra(PrincipalBT.LD);
		// Show the Up button in the action bar.
		// setupActionBar();
		setContentView(R.layout.device_list);
		LD = (ListView) findViewById(R.id.LD);
		Adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, LDev);
		IS = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent SelDev = new Intent("RESULT_ACTION");
				SelDev.putExtra(SDev, position);
				setResult(Activity.RESULT_OK, SelDev);
				finish();
			}

		};
		LD.setAdapter(Adapter);
		LD.setOnItemClickListener(IS);
	}

	/*
	 * /** Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	/*
	 * @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void setupActionBar() {
	 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	 * getActionBar().setDisplayHomeAsUpEnabled(true); } }
	 */

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.device_list, menu); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: // This ID represents the
	 * Home or Up button. In the case of this // activity, the Up button is
	 * shown. Use NavUtils to allow users // to navigate up one level in the
	 * application structure. For // more details, see the Navigation pattern on
	 * Android Design: // //
	 * http://developer.android.com/design/patterns/navigation.html#up-vs-back
	 * // NavUtils.navigateUpFromSameTask(this); return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

}
