package com.example.brts_map_demo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabSample extends TabActivity {

	GoogleMap googleMap;
	public static int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_main);

		final TabHost tabHost = getTabHost();
		Resources res = getResources();
		TabSpec spec, spec1;
		Intent intent;

		intent = new Intent().setClass(this, MainActivity.class);

		spec = tabHost.newTabSpec("home")
				.setIndicator("", res.getDrawable(R.drawable.home_1))
				.setContent(intent);
		tabHost.addTab(spec);

		Log.e("TAB ", "TAb excuted");

		intent = new Intent().setClass(this, Search_map.class);
		spec1 = tabHost.newTabSpec("Search")
				.setIndicator("", res.getDrawable(R.drawable.search1))
				.setContent(intent);
		tabHost.addTab(spec1);

		intent = new Intent().setClass(this, All_brts_station.class);
		spec1 = tabHost.newTabSpec("Add")
				.setIndicator("", res.getDrawable(R.drawable.route))
				.setContent(intent);
		tabHost.addTab(spec1);

		intent = new Intent().setClass(this, Route_Brts_activity.class);
		spec1 = tabHost.newTabSpec("Route")
				.setIndicator("", res.getDrawable(R.drawable.plus))
				.setContent(intent);

		tabHost.getTabWidget().setBackgroundColor(Color.CYAN);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub

				setTabColor(tabHost);

			}
		});

		tabHost.addTab(spec1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.custom_setting_menu, menu);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.map_type) {

			View menuItemView = findViewById(R.id.map_type);

			PopupMenu popup = new PopupMenu(TabSample.this, menuItemView);

			popup.getMenuInflater().inflate(R.menu.select_map_type,
					popup.getMenu());

			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem subitem) {
					// TODO Auto-generated method stub

					if (subitem.getItemId() == R.id.Normal
							&& TabSample.flag == 1) {

						if (MainActivity.googleMap != null)
							MainActivity.googleMap
									.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						if (All_brts_station.googleMap != null)
							All_brts_station.googleMap
									.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					} else if (subitem.getItemId() == R.id.Hybrid
							&& TabSample.flag == 1) {
						if (MainActivity.googleMap != null)
							MainActivity.googleMap
									.setMapType(GoogleMap.MAP_TYPE_HYBRID);

						if (All_brts_station.googleMap != null)
							All_brts_station.googleMap
									.setMapType(GoogleMap.MAP_TYPE_HYBRID);

					} else if (subitem.getItemId() == R.id.Satellite
							&& TabSample.flag == 1) {
						if (MainActivity.googleMap != null)
							MainActivity.googleMap
									.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

						if (All_brts_station.googleMap != null)
							All_brts_station.googleMap
									.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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
	
		super.onResume();
		
	}

	@Override
	public void onBackPressed() {
	

		backButtonHandler();
		
		return;

	}

	public void setTabColor(TabHost tabhost) {

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
			tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.CYAN); // unselected

		if (tabhost.getCurrentTab() == 0)
			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
					.setBackgroundColor(Color.LTGRAY); // 1st tab selected
		else if (tabhost.getCurrentTab() == 1)
			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
					.setBackgroundColor(Color.LTGRAY); // 2nd tab selected
		else if (tabhost.getCurrentTab() == 2)
			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
					.setBackgroundColor(Color.LTGRAY); // 3nd tab selected
		else if (tabhost.getCurrentTab() == 3)
			tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
					.setBackgroundColor(Color.LTGRAY); // 4nd tab selected

	}

	private void backButtonHandler() {
	

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				TabSample.this);

		alertDialog.setTitle("Leave application?");

		alertDialog
				.setMessage("Are you sure you want to leave the application?");

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//finish();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						dialog.cancel();
					}
				});

		alertDialog.show();
	}

}
