package com.example.brts_map_demo;

import java.util.ArrayList;
import java.util.List;

import com.example.common.AppConstants;
import com.example.db.DBAdapter;
import com.google.android.gms.internal.gp;
import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.drm.DrmStore.Action;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class Search_map extends Activity {

	Button search;
	AutoCompleteTextView origin, destination;
	ArrayList<String> title = new ArrayList<String>();
	GPS_TRACK_CURRENT_LOCATION gps;
	LatLng currentlatlng;
	String current_result;
	Double source_lat, source_lag, desti_lat, desti_lag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_map);

		search = (Button) findViewById(R.id.btnsearch);
		origin = (AutoCompleteTextView) findViewById(R.id.autotxtview_source);
		destination = (AutoCompleteTextView) findViewById(R.id.autotxtview_destination);

		AppConstants.Dbobject = DBAdapter.getDBAdapterInstance(Search_map.this);
		copydb();
		AppConstants.Dbobject.openDataBase();

		String query = "select * from route_tbl ORDER BY title";

		Cursor c = AppConstants.Dbobject.selectquery(query);

		if (c.getCount() > 0) {
			while (c.moveToNext()) {

				title.add(c.getString(3));

			}
		}

		ArrayAdapter<String> origin_adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, title);

		origin.setThreshold(1);

		gps = new GPS_TRACK_CURRENT_LOCATION(Search_map.this);

		if (gps.isGPSEnabled) {

			if (isConnectTointernet()) {

				final double latitude = gps.getLatitude();
				final double longitude = gps.getLongitude();

				currentlatlng = new LatLng(latitude, longitude);

				new Reversegecoding().execute(currentlatlng);

			} else {
				gps.showAlertDialogforinternet(getApplicationContext(),
						"NO INTERNET CONNECTION",
						"PLEASE CHECK YOUR INTERNET SETTING", false);
			}

		}

		origin.setAdapter(origin_adapter);

		ArrayAdapter<String> desti_adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, title);

		destination.setThreshold(1);
		destination.setAdapter(desti_adapter);

		search.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				String source = origin.getText().toString().trim();
				String desti = destination.getText().toString().trim();

				if (source.equals(desti)) {
					gps.showAlertDialogformessage(Search_map.this, "Message",
							"Please change Source Otherwise Destination", false);
				}

				else if (source.equals(current_result) && desti.length() > 0) {

					String query = "select * from route_tbl where title ='"
							+ desti + "'";

					Cursor dest_latlng = AppConstants.Dbobject
							.selectquery(query);

					if (dest_latlng.getCount() > 0) {

						while (dest_latlng.moveToNext()) {

							Double lat = dest_latlng.getDouble(1);
							Double lag = dest_latlng.getDouble(2);

							final Intent intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://maps.google.com/maps?"
											+ "saddr=" + currentlatlng.latitude
											+ "," + currentlatlng.longitude
											+ "&daddr=" + lat + "," + lag));
							intent.setClassName("com.google.android.apps.maps",
									"com.google.android.maps.MapsActivity");
							startActivity(intent);

						}

					}

				} else if (desti.length() == 0) {
					gps.showAlertDialogformessage(Search_map.this, "Message",
							"Must be Filled destination", false);
				} else if (source.length() == 0 && desti.length() > 0) {

					String query = "select * from route_tbl where title ='"
							+ desti + "'";

					Cursor dest_latlng = AppConstants.Dbobject
							.selectquery(query);

					if (dest_latlng.getCount() > 0) {

						while (dest_latlng.moveToNext()) {

							Double lat = dest_latlng.getDouble(1);
							Double lag = dest_latlng.getDouble(2);
							Log.e("lat", lat + "");
							Log.e("lag", lag + "");

							final Intent intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://maps.google.com/maps?"
											+ "saddr=" + currentlatlng.latitude
											+ "," + currentlatlng.longitude
											+ "&daddr=" + lat + "," + lag));
							intent.setClassName("com.google.android.apps.maps",
									"com.google.android.maps.MapsActivity");
							startActivity(intent);
						}

					}

				} else if (source != current_result && desti.length() > 0) {

					String query1 = "select * from route_tbl where title ='"
							+ source + "'";

					Cursor source_latlng = AppConstants.Dbobject
							.selectquery(query1);

					if (source_latlng.getCount() > 0) {

						while (source_latlng.moveToNext()) {

							source_lat = source_latlng.getDouble(1);
							source_lag = source_latlng.getDouble(2);
							Log.e("lat_source", source_lat + "");
							Log.e("lag_source", source_lag + "");

						}

					}

					String query2 = "select * from route_tbl where title ='"
							+ desti + "'";

					Cursor desti_latlng = AppConstants.Dbobject
							.selectquery(query2);

					if (desti_latlng.getCount() > 0) {

						while (desti_latlng.moveToNext()) {

							desti_lat = desti_latlng.getDouble(1);
							desti_lag = desti_latlng.getDouble(2);
							Log.e("lat_desti", desti_lat + "");
							Log.e("lag_desti", desti_lag + "");

						}

					}
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://maps.google.com/maps?" + "saddr="
									+ source_lat + "," + source_lag + "&daddr="
									+ desti_lat + "," + desti_lag));
					intent.setClassName("com.google.android.apps.maps",
							"com.google.android.maps.MapsActivity");
					startActivity(intent);
				} else {

				}
			}
		});
	}

	private void copydb() {
		DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(Search_map.this);

		try {
			dbAdapter.createDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnectTointernet() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}

		return false;

	}

	private class Reversegecoding extends AsyncTask<LatLng, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(LatLng... params) {
			// TODO Auto-generated method stub

			Geocoder geocoder = new Geocoder(Search_map.this);
			double latitude = params[0].latitude;
			double longitude = params[0].longitude;

			Log.e("Current Location :-", latitude + "" + longitude);

			List<Address> addresses = null;
			String addressText = "";

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				if (address.getMaxAddressLineIndex() > 0) {
					addressText = String.format("%s, %s ",
							address.getAddressLine(0), address.getLocality());
				}
			}
			Log.i("ADDRESS ", addressText);
			return addressText;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			origin.setText(result);

			current_result = result.trim();

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			//MainActivity m= new MainActivity();
			dialogOnBackPress();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	protected void dialogOnBackPress() {

	    new AlertDialog.Builder(this)
	           .setMessage("Are you sure you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    Search_map.this.finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();

	}

}
