package com.example.brts_map_demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.common.AppConstants;
import com.example.db.DBAdapter;
import com.google.android.gms.drive.internal.m;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class All_brts_station extends Activity {

	public double lati[] = new double[25];
	public double longi[] = new double[25];
	String title[] = new String[25];
	public static GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_brts);

		AppConstants.Dbobject = DBAdapter
				.getDBAdapterInstance(All_brts_station.this);
		copydb();
		AppConstants.Dbobject.openDataBase();

		if (googleMap == null) {

			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.fragment1)).getMap();

			googleMap.setMyLocationEnabled(true);

			googleMap.getUiSettings().setZoomControlsEnabled(true);

			Log.e("DB OPen", "DB Open");

			String query = "select * from route_tbl ";

			Cursor all_brts = AppConstants.Dbobject.selectquery(query);

			if (all_brts.getCount() > 0) {

				int i = 0;
				while (all_brts.moveToNext()) {

					lati[i] = all_brts.getDouble(1);
					longi[i] = all_brts.getDouble(2);
					title[i] = all_brts.getString(3);

					Log.e("LAT and LONGI and TITLE", lati[i] + "  " + longi[i]
							+ "  " + title[i]);

					drawMarker(new LatLng(lati[i], longi[i]), title[i]);

					i++;

				}
			}

		} else {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.fragment1)).getMap();

			googleMap.setMyLocationEnabled(true);

			googleMap.getUiSettings().setZoomControlsEnabled(true);

			Log.e("DB OPen", "DB Open");

			String query = "select * from route_tbl ";

			Cursor all_brts = AppConstants.Dbobject.selectquery(query);

			if (all_brts.getCount() > 0) {

				int i = 0;
				while (all_brts.moveToNext()) {

					lati[i] = all_brts.getDouble(1);
					longi[i] = all_brts.getDouble(2);
					title[i] = all_brts.getString(3);

					Log.e("LAT and LONGI and TITLE", lati[i] + "  " + longi[i]
							+ "  " + title[i]);

					drawMarker(new LatLng(lati[i], longi[i]), title[i]);

					i++;

				}
			}
			all_brts.close();
			AppConstants.Dbobject.close();
		}

	}

	private void drawMarker(LatLng point, String title) {

		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.position(point);

		point = new LatLng(point.latitude, point.longitude);

		markerOptions.title(title);

		Log.e("marker", point.latitude + "," + point.longitude);

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));

		googleMap.addMarker(markerOptions);

	}

	private void copydb() {

		try {
			DBAdapter adapter = DBAdapter
					.getDBAdapterInstance(getApplicationContext());
			adapter.createDataBase();

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("EXCEPTION", e.toString());
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
	                    All_brts_station.this.finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();

	}
	
}
