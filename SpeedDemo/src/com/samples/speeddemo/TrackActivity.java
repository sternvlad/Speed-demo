package com.samples.speeddemo;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackActivity extends Activity implements LocationListener {
	GoogleMap googleMap;
	public static Track track;
	ActionBar aBar;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Window window = getWindow();

		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		
		googleMap.setMyLocationEnabled(true);
		
		aBar = getActionBar();
		
		aBar.setTitle("Your track distance is : "+ track.distance +" km");
		
//		for (int i = 0; i<track.pins.size(); i++)
//			googleMap.addMarker(track.pins.get(i));

	}
	
	
	

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub


	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu2, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_satelyte_type:
	        	changeMap(1);
	            return true; 
	        case R.id.menu_terrain_type:
	        	changeMap(2);
	            return true; 
	        case R.id.menu_normal_type:
	        	changeMap(3);
	            return true; 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public void changeMap (int type)
	{
		if (type==1)
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		else if (type==2)
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		else if (type==3)
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	  

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}