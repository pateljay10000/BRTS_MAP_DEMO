package com.example.brts_map_demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.example.common.AppConstants;
import com.example.db.DBAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class Fix_route_activity extends Activity {

	GoogleMap googleMap;
	public double lat[] = new double[25];
	public double lon[] = new double[25];
	String[] title = new String[20];
	LatLng route_marker_Latlng;

	LatLng first_Latlng;
	LatLng last_Latlng;

	ArrayList<LatLng> markerPointsArrayList = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_list_select_map);
		setUpMapIfNeeded();

		AppConstants.Dbobject = DBAdapter
				.getDBAdapterInstance(Fix_route_activity.this);
		copydb();
		AppConstants.Dbobject.openDataBase();

		Intent intent = getIntent();

		int itempos = intent.getIntExtra("position", 0);
		String routename = intent.getStringExtra("Routename");

		if (itempos == 0) {

			String query = "select * from route_tbl where route_id=1";

			Cursor c = AppConstants.Dbobject.selectquery(query);

			int i = 0;
			String waypoints = "waypoints=";

			if (c.getCount() > 0) {
				while (c.moveToNext()) {

					lat[i] = c.getDouble(1);
					lon[i] = c.getDouble(2);
					title[i] = c.getString(3);

					drawMarker(
							route_marker_Latlng = new LatLng(lat[i], lon[i]),
							title[i]);

					markerPointsArrayList.add(new LatLng(lat[i], lon[i]));

					if (i == 0) {

						first_Latlng = new LatLng(lat[i], lon[i]);

					}

					if (i == markerPointsArrayList.size() - 1) {
						last_Latlng = new LatLng(lat[i], lon[i]);

					}
					i++;
				}

				Log.e("SIZE", markerPointsArrayList.size() + "");
				String aa = "";
				for (int j = 1; j < markerPointsArrayList.size() - 1; j++) {

					LatLng point = (LatLng) markerPointsArrayList.get(j);

					waypoints += point.latitude + "," + point.longitude + "|";

				}
				Log.e("Waypoint", waypoints);

			}

			String direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin="
					+ first_Latlng.latitude
					+ ","
					+ first_Latlng.longitude
					+ "&destination="
					+ last_Latlng.latitude
					+ ","
					+ last_Latlng.longitude
					+ "&"
					+ waypoints
					+ "&key=AIzaSyAv0lXH_kWG9rXx7AnZgvyJyT4s2g9hHl0";

			new DrawRoute().execute(direction_url);

		} else if (itempos == 1) {
			String query = "select * from route_tbl where tilte like ";

			Cursor c = AppConstants.Dbobject.selectquery(query);

		}

	}

	private class DrawRoute extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		ProgressDialog p;
		InputStream iStream = null;
		String data = "";
		HttpURLConnection urlConnection = null;
		JSONObject jObject;
		List<List<HashMap<String, String>>> routes = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			p = new ProgressDialog(Fix_route_activity.this);
			p.setMessage("Finding Route..");
			p.show();
		}

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... dire_url) {

			try {

				URL url = new URL(dire_url[0]);

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				urlConnection.setRequestMethod("POST");

				iStream = urlConnection.getInputStream();

				urlConnection.connect();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						iStream));

				StringBuffer sb = new StringBuffer();

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				data = sb.toString();

				br.close();

				jObject = new JSONObject(data);

				DirectionsJSONParser parser = new DirectionsJSONParser();

				routes = parser.parse(jObject);

			} catch (Exception e) {
				Log.e("Exception ", e.toString());
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {

			super.onPostExecute(result);

			ArrayList<LatLng> points = new ArrayList<LatLng>();
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {

				lineOptions = new PolylineOptions();
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {

					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5);
				lineOptions.color(Color.GREEN);

				googleMap.addPolyline(lineOptions);
				p.dismiss();
			}

		}

	}

	private void copydb() {
		// TODO Auto-generated method stub
		DBAdapter dbAdapter = DBAdapter
				.getDBAdapterInstance(Fix_route_activity.this);

		try {
			dbAdapter.createDataBase();
		} catch (Exception e) {
			e.printStackTrace();
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

	private void setUpMap() {

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (googleMap == null) {

			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.fragment1)).getMap();

			if (googleMap != null) {
				setUpMap();
			}
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
