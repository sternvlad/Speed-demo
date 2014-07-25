package com.samples.speeddemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.samples.interfaces.Constants;
import com.samples.interfaces.GPSCallback;
import com.samples.managers.GPSManager;

public class MapActivityView extends Activity implements LocationListener,
		GPSCallback {
	private AbsoluteSizeSpan sizeSpanLarge = null;
	private AbsoluteSizeSpan sizeSpanSmall = null;
	GoogleMap googleMap;
	LocationManager locationManager;
	LatLng myPosition;
	long currentTime2;
	private int measurement_index = Constants.INDEX_KM;
	private double speed = 0.0;
	double lastLong = 0, lastLat = 0;
	private GPSManager gpsManager = null;
	Boolean reset;
	Punct ob1 = null, ob2 = null;
	Button button1;
	ActionBar aBar;
	ArrayList<Location> _recentLocations = new ArrayList<Location>();
	public static Track myTrack;
	ArrayList<Punct> Puncte = new ArrayList<Punct>();
	TextView textLat, textLong;
	boolean calculeaza = true;
	boolean startPointWasSet = false;
	ArrayList<MarkerOptions> pins = new ArrayList<MarkerOptions>();
	Boolean finishPoint = true, startPoint = true, middlePoint = true;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map2_lay);
		Window window = getWindow();

		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		googleMap.setMyLocationEnabled(true);

		gpsManager = new GPSManager();

		gpsManager.startListening(getApplicationContext());
		gpsManager.setGPSCallback(this);

		((TextView) findViewById(R.id.info_message))
				.setText(getString(R.string.info));
		((TextView) findViewById(R.id.info_message2))
				.setText(getString(R.string.info));

		((Button) findViewById(R.id.button1))
				.setBackgroundResource(R.drawable.start);
		reset = true;

		textLat = (TextView) findViewById(R.id.text_lat);
		textLong = (TextView) findViewById(R.id.text_long);
		button1 = (Button) findViewById(R.id.button1);
		Puncte.clear();
		aBar = getActionBar();
		aBar.hide();
		currentTime2 = System.currentTimeMillis();
		Puncte.clear();
		Set<Punct> s = new LinkedHashSet<Punct>(myTrack.pins);
		for (Punct pin : myTrack.pins) {
			addPunct(pin.latitudine, pin.longitudine, pin.isCheckpoint);
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Closing Activity")
				.setMessage("Are you sure you want to close this activity?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}

						}).setNegativeButton("No", null).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

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
		case R.id.menu_remove_pins:
			googleMap.clear();
			startPoint = true;
			middlePoint = true;
			finishPoint = true;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addPunct(double lat, double lon, boolean isCheckpoint) {
		Punct Punct = new Punct();
		try {
			Punct.latitudine = lat;
			Punct.longitudine = lon;
			Punct.isCheckpoint = isCheckpoint;
			Punct.point = new LatLng(Punct.latitudine, Punct.longitudine);

			if (Puncte.size() == 0)
				ob1 = Punct;
			if (Puncte.size() >= 1) {
				ob2 = Punct;
				PolylineOptions line = new PolylineOptions()
						.add(ob1.point, ob2.point).width(10).color(Color.BLUE);

				googleMap.addPolyline(line);
				ob1 = ob2;
			}
			if (isCheckpoint) {
				addPoint(lat, lon);
			}
			Puncte.add(Punct);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addPoint(double lat, double lon) {
		Toast msg = Toast.makeText(MapActivityView.this, "", Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
				msg.getYOffset() / 2);
		if (!startPointWasSet) {
			try {
				LatLng point = new LatLng(lat, lon);
				msg.setText("Start point was set");
				MarkerOptions mPin = new MarkerOptions().position(point);
				mPin.draggable(false);
				startPoint = false;
				startPointWasSet = true;
				mPin.title("Start");
				mPin.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				googleMap.addMarker(mPin);

				msg.setText("Start point was set");
			} catch (Exception e) {
				// TODO: handle exception
				msg.setText("You cannot add start point !!!");

			}
			msg.show();

		} else {
			try {
				LatLng point = new LatLng(lat, lon);
				MarkerOptions mPin = new MarkerOptions().position(point);
				mPin.draggable(false);
				mPin.title("Check Point");
				mPin.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
				googleMap.addMarker(mPin);

			} catch (Exception e) {
				// TODO: handle exception
				msg.setText("You cannot add this point !!!");

				msg.show();
			}
		}

	}

	public void changeMap(int type) {
		if (type == 1)
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		else if (type == 2)
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		else if (type == 3)
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

	long currentTime = System.currentTimeMillis();
	Location _bestLocation = null;

	@Override
	public void onGPSUpdate(Location location) {
		location.getLatitude();
		location.getLongitude();
		speed = location.getSpeed();
		if (System.currentTimeMillis() > currentTime + 300) {
			currentTime = System.currentTimeMillis();
			String speedString = "" + roundDecimal(convertSpeed(speed), 2);
			String unitString = measurementUnitString(measurement_index);
			setSpeedText(R.id.info_message2, speedString + " " + unitString);
		}

		if (location.getAccuracy() < 25f) {
			_recentLocations.add(location);
			if (_recentLocations.size() > 5)
				_recentLocations.remove(0);

			if (_bestLocation == null
					|| location.getAccuracy() <= _bestLocation.getAccuracy())
				_bestLocation = location;
		}

		if ((_bestLocation != null && _bestLocation.getAccuracy() < 10f && _recentLocations
				.size() >= 3) || _recentLocations.size() >= 4) {
			float TotalSpeed = 0f;
			float AverageSpeed = 0f;
			for (int i = 0; i < _recentLocations.size(); i++) {
				TotalSpeed += _recentLocations.get(i).getSpeed();
			}

			if (_recentLocations.size() > 0)
				AverageSpeed = TotalSpeed / _recentLocations.size();
			String speedString = ""
					+ roundDecimal(convertSpeed(AverageSpeed), 2);
			String unitString = measurementUnitString(measurement_index);
			setSpeedText(R.id.info_message, speedString + " " + unitString);
		}
	}

	@Override
	protected void onDestroy() {
		gpsManager.stopListening();
		gpsManager.setGPSCallback(null);

		gpsManager = null;

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return false;
	}
	
	private double convertSpeed(double speed) {
		return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS[measurement_index]);
	}

	private String measurementUnitString(int unitIndex) {
		String string = "";

		switch (unitIndex) {
		case Constants.INDEX_KM:
			string = "km/h";
			break;
		case Constants.INDEX_MILES:
			string = "mi/h";
			break;
		}

		return string;
	}

	private double roundDecimal(double value, final int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);

		bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
		value = bd.doubleValue();

		return value;
	}

	private void setSpeedText(int textid, String text) {
		Spannable span = new SpannableString(text);
		int firstPos = text.indexOf(32);

		span.setSpan(sizeSpanLarge, 0, firstPos,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(sizeSpanSmall, firstPos + 1, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		TextView tv = ((TextView) findViewById(textid));

		tv.setText(span);
	}

}