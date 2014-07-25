package com.samples.speeddemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.samples.interfaces.Constants;
import com.samples.interfaces.GPSCallback;
import com.samples.managers.GPSManager;
import com.samples.settings.AppSettings;

public class MapActivityRecord extends Activity implements LocationListener,
		GPSCallback {
	GoogleMap googleMap;
	int i = 0;
	LocationManager locationManager;
	LatLng myPosition;
	long currentTime2;
	double lastLong = 0, lastLat = 0;
	private GPSManager gpsManager = null;
	private double speed = 0.0;
	Boolean reset;
	Punct ob1 = null, ob2 = null;
	private int measurement_index = Constants.INDEX_KM;
	private AbsoluteSizeSpan sizeSpanLarge = null;
	private AbsoluteSizeSpan sizeSpanSmall = null;
	ArrayList<Location> _recentLocations = new ArrayList<Location>();
	Button button1;
	ActionBar aBar;
	boolean startPointWasSet = false;
	ArrayList<Punct> Puncte = new ArrayList<Punct>();
	TextView textLat, textLong;
	Button getLocation;
	boolean calculeaza = true;
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

		measurement_index = AppSettings.getMeasureUnit(this);
		reset = true;

		textLat = (TextView) findViewById(R.id.text_lat);
		textLong = (TextView) findViewById(R.id.text_long);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StringBuilder data = new StringBuilder();
				for (Punct ob : Puncte) {
					data.append("<item>\r\n\t<latitudine>" + ob.latitudine
							+ "<\\latitudine>" + "\n\t<longitudine>"
							+ ob.longitudine + "<\\longitudine>"
							 +"\n<\\item>\n");

				}
				Track tra = new Track();	
				tra.name = "Track 1";
				tra.pins.addAll(Puncte);
				if (tra.pins.size() > 0)
					tra.save(MapActivityRecord.this);
				writeToFile(data.toString());
			}
		});

		getLocation = (Button) findViewById(R.id.button2);
		getLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

//				Punct Punct = new Punct();
//				try {
//					Punct.latitudine = googleMap.getMyLocation().getLatitude();
//					Punct.longitudine = googleMap.getMyLocation()
//							.getLongitude();
//					Punct.point = new LatLng(Punct.latitudine,
//							Punct.longitudine);
//				} catch (Exception e) {
//					// TODO: handle exception
//
//				}

			}
		});

		aBar = getActionBar();
		aBar.hide();
		currentTime2 = System.currentTimeMillis();
		Puncte.clear();
//		getLocation.post(new Runnable() {
//
//			public void run() {
//				// doProcessing();
//				if (startPointWasSet) {
//					getLocation.performClick();
//					getLocation.postDelayed(this, (900));
//				}
//				// getNumbers.postDelayed(this,(1000*60));
//			}
//		});

		googleMap
				.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

					@Override
					public void onMyLocationChange(Location location) {
						// TODO Auto-generated method stub
						textLat.setText("Latitudine : "
								+ location.getLatitude());
						textLong.setText("Longitudine = "
								+ location.getLongitude());
						currentTime2 = System.currentTimeMillis();
						getLocation.performClick();
					}
				});

		googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				if (startPointWasSet)
					addPoint(2);
				else
					addPoint(1);
			}
		});

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

	private void writeToFile(String combinedString) {
		File file = null;
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			File dir = new File(root.getAbsolutePath() + "/My records");
			dir.mkdirs();
			file = new File(dir, "Data.txt");
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				out.write(combinedString.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Uri u1 = null;
		u1 = Uri.fromFile(file);

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My records");
		sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
		sendIntent.setType("text/html");
		startActivity(sendIntent);
		onBackPressed();
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
			i = 0;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addPunct(Location loc) {
		Punct Punct = new Punct();
		try {
			Punct.latitudine = loc.getLatitude();
			Punct.longitudine = loc.getLongitude();
			lastLat = loc.getLatitude();
			lastLong = loc.getLongitude();
			Punct.point = new LatLng(Punct.latitudine, Punct.longitudine);
			if (startPointWasSet) {
				if (Puncte.size() == 0)
					ob1 = Punct;
				if (Puncte.size() >= 1) {
					ob2 = Punct;
					PolylineOptions line = new PolylineOptions()
							.add(ob1.point, ob2.point).width(10)
							.color(Color.BLUE);

					googleMap.addPolyline(line);
					ob1 = ob2;
				}
				Puncte.add(Punct);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addPunct(double lat, double lon, boolean isCheckpoint) {
		Punct Punct = new Punct();
		try {
			Punct.latitudine = lat;
			Punct.longitudine = lon;
			Punct.isCheckpoint = isCheckpoint;
			Punct.point = new LatLng(Punct.latitudine, Punct.longitudine);

			if (Puncte.size() == 0){
				ob1 = Punct;
				Puncte.add(Punct);
			}
			else if (Puncte.size() >= 1) {
				ob2 = Punct;
				PolylineOptions line = new PolylineOptions()
						.add(ob1.point, ob2.point).width(10).color(Color.BLUE);

				googleMap.addPolyline(line);
				ob1 = ob2;
			}
			if (Puncte.size()>0 && Puncte.get(0).latitudine!=Punct.latitudine)
				Puncte.add(Punct);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addPoint(int color) {
		Toast msg = Toast.makeText(MapActivityRecord.this, "", Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
				msg.getYOffset() / 2);
		if (color == 1) {
			if (!startPointWasSet) {
				try {
					LatLng point;
					if (lastLat != 0)
						point = new LatLng(lastLat, lastLong);
					else
						point = new LatLng(googleMap.getMyLocation()
								.getLatitude(), googleMap.getMyLocation()
								.getLongitude());
					msg.setText("Start point was set");
					MarkerOptions mPin = new MarkerOptions().position(point);
					mPin.draggable(false);
					startPoint = false;
					startPointWasSet = true;
					mPin.title("Start");
					mPin.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					googleMap.addMarker(mPin);
					addPunct(point.latitude, point.longitude, true);
					i = 1;
					
					msg.setText("Start point was set");
				} catch (Exception e) {
					// TODO: handle exception
					msg.setText("You cannot add start point !!!");
		
				}
				msg.show();

			} else {
				msg.setText("You cannot add another point");
				msg.show();
				i = 1;
			}
		} else if (color == 2) {
			try {
				LatLng point;
				if (lastLat != 0)
					point = new LatLng(lastLat, lastLong);
				else
					point = new LatLng(googleMap.getMyLocation().getLatitude(),
							googleMap.getMyLocation().getLongitude());
				MarkerOptions mPin = new MarkerOptions().position(point);
				mPin.draggable(false);
				mPin.title("Check Point");
				mPin.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
				googleMap.addMarker(mPin);
				i = 0;
				addPunct(point.latitude, point.longitude, true);
			} catch (Exception e) {
				// TODO: handle exception
				msg.setText("You cannot add this point !!!");
				i = 0;
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
		addPunct(location);
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