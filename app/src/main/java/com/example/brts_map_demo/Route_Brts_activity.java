package com.example.brts_map_demo;

import java.util.ArrayList;

import com.example.common.AppConstants;
import com.example.db.DBAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Route_Brts_activity extends Activity {

	ListView listroute;
	GoogleMap googleMap = null;

	String route_title[] = { "Soni ni Chal - Dani Limda",
			"Soni ni Chal – Odhav" };

	ArrayList<String> route = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route__brts_activity);

		AppConstants.Dbobject = DBAdapter
				.getDBAdapterInstance(Route_Brts_activity.this);

		copydb();

		AppConstants.Dbobject.openDataBase();

		listroute = (ListView) findViewById(R.id.route_list);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				Route_Brts_activity.this, android.R.layout.simple_list_item_1,
				route_title);

		listroute.setAdapter(adapter);

		listroute.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String routename = listroute.getItemAtPosition(position)
						.toString();

				Intent intent = new Intent(getApplicationContext(),
						Fix_route_activity.class);

				Log.e("position", position + "");
				intent.putExtra("position", position);
				intent.putExtra("Routename", routename);

				startActivity(intent);

			}
		});

		AppConstants.Dbobject.close();

	}

	private void copydb() {
		// TODO Auto-generated method stub
		DBAdapter dbAdapter = DBAdapter
				.getDBAdapterInstance(Route_Brts_activity.this);

		try {
			dbAdapter.createDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// MainActivity m= new MainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
