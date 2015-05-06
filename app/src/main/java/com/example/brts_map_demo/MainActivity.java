package com.example.brts_map_demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.common.AppConstants;
import com.example.db.DBAdapter;
import com.google.android.gms.common.api.g;
import com.google.android.gms.drive.internal.n;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements LocationListener,
		ActionBar.TabListener {

	GPS_TRACK_CURRENT_LOCATION gps;
	public static GoogleMap googleMap;

	public static int pass;
	LatLng currentlatlng;
	Location location;
	ArrayList<LatLng> mMarkerPoints = new ArrayList<LatLng>();
	Button btn;
	String type = "BUS";
	ProgressDialog progressdialog;
	MarkerOptions m = new MarkerOptions();

	public double lati[] = new double[25];
	public double longi[] = new double[25];
	float[] result = new float[25];
	String[] param = new String[20];
	double Minimum_latitude;
	double Miniimum_logitutde;
	String destination_address;

	ImageView navigate;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TabSample.flag = 1;

		navigate = (ImageView) findViewById(R.id.imageView1);

		AppConstants.Dbobject = DBAdapter
				.getDBAdapterInstance(MainActivity.this);
		copydb();
		googleMap = null;
		AppConstants.Dbobject.openDataBase();

		gps = new GPS_TRACK_CURRENT_LOCATION(MainActivity.this);

		if (gps.isGPSEnabled) {

			if (isConnectingToInternet()) {

				final double latitude = gps.getLatitude();
				final double longitude = gps.getLongitude();

				try {

					Log.i("Enter in the google map", "ENTERDDDDD");

					if (googleMap == null) {

						googleMap = ((MapFragment) getFragmentManager()
								.findFragmentById(R.id.fragment1)).getMap();

						currentlatlng = new LatLng(latitude, longitude);

						m.position(currentlatlng);

						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								currentlatlng, 14.0f));

						new ReverseGecoding().execute(currentlatlng);

						Cursor c = AppConstants.Dbobject
								.selectquery("select lat,lag,title from route_tbl");

						int i = 0;
						if (c.getCount() > 0) {
							while (c.moveToNext()) {
								lati[i] = c.getDouble(0);
								longi[i] = c.getDouble(1);

								Log.e("Lat,Lng", String.valueOf(lati[i]) + ","
										+ String.valueOf(longi[i]));

								float distances[] = new float[3];
								Location.distanceBetween(
										currentlatlng.latitude,
										currentlatlng.longitude, lati[i],
										longi[i], distances);
								result[i] = distances[0];
								Log.e("Result ", String.valueOf(result[i]));

								i++;

							}
							String IndexAndMinimum = FindIndexAndMinimumFromArray(result);

							float smallestDistance = Float
									.parseFloat(IndexAndMinimum
											.substring(IndexAndMinimum
													.indexOf("#") + 1));
							int index = Integer
									.parseInt(IndexAndMinimum.substring(0,
											IndexAndMinimum.indexOf("#")));

							Log.e("Minimum Distance", smallestDistance + "");

							Log.e("Index", index + "");
							Minimum_latitude = lati[index];
							Miniimum_logitutde = longi[index];

							Log.e("Minimum Latitude", Minimum_latitude + "");
							Log.e("Maximum Logitutde", Miniimum_logitutde + "");

							String url = ("https://maps.googleapis.com/maps/api/distancematrix/json?origins="
									+ currentlatlng.latitude
									+ ","
									+ currentlatlng.longitude
									+ "&destinations="
									+ Minimum_latitude
									+ ","
									+ Miniimum_logitutde + "&key=AIzaSyAv0lXH_kWG9rXx7AnZgvyJyT4s2g9hHl0");

							Log.e("URL", url);

							new getDistanceData().execute(url);

							String direction_url = "https://maps.googleapis.com/maps/api/directions/json?origin="
									+ currentlatlng.latitude
									+ ","
									+ currentlatlng.longitude
									+ "&destination="
									+ Minimum_latitude
									+ ","
									+ Miniimum_logitutde
									+ "&key=AIzaSyAv0lXH_kWG9rXx7AnZgvyJyT4s2g9hHl0";

							Log.e("Direction_Url", direction_url);

							new Findroute().execute(direction_url);

							navigate.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									final Intent intent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://maps.google.com/maps?"
													+ "saddr="
													+ currentlatlng.latitude
													+ ","
													+ currentlatlng.longitude
													+ "&daddr="
													+ Minimum_latitude
													+ ","
													+ Miniimum_logitutde));
									intent.setClassName(
											"com.google.android.apps.maps",
											"com.google.android.maps.MapsActivity");
									startActivity(intent);
								}
							});
						}
						c.close();
						AppConstants.Dbobject.close();
					}

					if (googleMap != null) {
						String url = ("https://maps.googleapis.com/maps/api/distancematrix/json?origins="
								+ currentlatlng.latitude
								+ ","
								+ currentlatlng.longitude
								+ "&destinations="
								+ Minimum_latitude + "," + Miniimum_logitutde + "&key=AIzaSyAv0lXH_kWG9rXx7AnZgvyJyT4s2g9hHl0");
						Log.e("URL", url);
					}
				} catch (Exception e) {
					Log.e("ERROR ", e.toString());
				}
			} else {

				gps.showAlertDialogforinternet(MainActivity.this,
						"No Internet Connection",
						"You don't have internet connection.", false);
			}
		}

	}

	private String FindIndexAndMinimumFromArray(float[] result2) {
		if (result2.length == 0) {
			return "-1#0.0";
		}

		float minimum = result2[0];
		int index = 0;

		for (int i = 1; i < result2.length; i++) {
			if (result2[i] != 0.0f)
				if (result2[i] < minimum) {
					minimum = result2[i];
					index = i;
				}
		}
		return index + "#" + minimum;
	}

	private class getDistanceData extends AsyncTask<String, Void, String> {

		ProgressDialog p;
		String data = "";
		InputStream iStream = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			p = new ProgressDialog(MainActivity.this);
			p.setMessage("PLease Wait for Distance");
			p.show();
		}

		@Override
		protected String doInBackground(String... strurl) {

			try {

				URL url = new URL(strurl[0]);

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
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("Exception ", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {

				JSONObject jObj = new JSONObject(result);

				JSONArray jdesti = jObj.getJSONArray("destination_addresses");

				destination_address = jdesti.getString(0);

				JSONArray jOrigin = jObj.getJSONArray("origin_addresses");

				String origin_address = jOrigin.getString(0);

				JSONArray distance_row = jObj.getJSONArray("rows");

				for (int i = 0; i < distance_row.length(); i++) {

					JSONObject obj = distance_row.getJSONObject(i);

					JSONArray elements = obj.getJSONArray("elements");

					Log.e("element", elements + "");

					for (int j = 0; j < elements.length(); j++) {

						JSONObject distance = elements.getJSONObject(j);
						JSONObject d = distance.getJSONObject("distance");
						String kiloMeter = d.getString("text");
						String value1 = d.getString("value");
						Log.e("distance_KM", kiloMeter);

					}
				}
				p.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class Findroute extends
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
			p = new ProgressDialog(MainActivity.this);
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

				// Starts parsing data
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
				lineOptions.width(10);
				lineOptions.color(Color.RED);
				MarkerOptions nearbrts = new MarkerOptions();
				LatLng latlng = new LatLng(Minimum_latitude, Miniimum_logitutde);
				nearbrts.position(latlng);
				nearbrts.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				nearbrts.title(destination_address);
				googleMap.addMarker(nearbrts);
				googleMap.addPolyline(lineOptions);
				p.dismiss();
			}

		}
	}

	public void copydb() {
		DBAdapter dbAdapter = DBAdapter.getDBAdapterInstance(MainActivity.this);

		try {
			dbAdapter.createDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ReverseGecoding extends AsyncTask<LatLng, Void, String> {
		@Override
		protected void onPreExecute() {
			progressdialog = new ProgressDialog(MainActivity.this);
			progressdialog.setMessage("Please Wait..");
			progressdialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(LatLng... params) {
			// TODO Auto-generated method stub

			Geocoder geocoder = new Geocoder(MainActivity.this);
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
					addressText = String.format("%s, %s , %s",
							address.getAddressLine(0), address.getLocality(),
							address.getCountryName());
				}
			}
			Log.i("ADDRESS ", addressText);
			return addressText;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("RESULT ", result);

			m.title(result);
			googleMap.addMarker(m);
			progressdialog.dismiss();

		}

	}

	@Override
	public void onLocationChanged(Location location) {

		Double lati = location.getLatitude();
		Double log = location.getLongitude();

		LatLng point = new LatLng(lati, log);
		currentlatlng = point;

		googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatlng));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.custom_setting_menu, menu);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.map_type) {
			View menuItemView = findViewById(R.id.map_type);

			PopupMenu popup = new PopupMenu(MainActivity.this, menuItemView);

			popup.getMenuInflater().inflate(R.menu.select_map_type,
					popup.getMenu());

			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem subitem) {
					// TODO Auto-generated method stub

					if (subitem.getItemId() == R.id.Normal) {
						googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					} else if (subitem.getItemId() == R.id.Hybrid) {
						googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

					} else if (subitem.getItemId() == R.id.Satellite) {

						googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
					}

					return true;
				}
			});

			popup.show();

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialogOnBackPress();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void dialogOnBackPress() {

		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.this.finish();
							}
						}).setNegativeButton("No", null).show();

	}

}
