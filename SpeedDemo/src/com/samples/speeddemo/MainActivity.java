package com.samples.speeddemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();

		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.button3))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MainActivity.this,
								MapActivityRecord.class);
						startActivity(intent);
					}
				});

		((Button) findViewById(R.id.button2))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MainActivity.this,
								MyTracks.class);
						startActivity(intent);
					}
				});

	}

	public void onResume() {
		super.onResume();
		GetObiecte.tracks = new ArrayList<Track>();
		DatabaseConnector dbConnector = new DatabaseConnector(MainActivity.this);
		GetObiecte.tracks.clear();
		GetObiecte.tracks = dbConnector.loadTrasee();
	}

}