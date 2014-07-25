package com.samples.speeddemo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.samples.settings.AppSettings;

public class MyTracks extends ListActivity {

	public static ListView listView;
	static ArrayList<String> sort = new ArrayList<String>();
	EditText edt;
	AppSettings set;
	TextView title_pag, textCodCaen;
	ArrayList<String> items = new ArrayList<String>();
	public static String date = "", date1, date2;
	public static String tipDate = "";
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_items);
		 
		for (int i= 0; i<GetObiecte.tracks.size(); i++)
			items.add(GetObiecte.tracks.get(i).name);
		
		listView = (ListView) findViewById(android.R.id.list);
		
		listView.setAdapter(new ArrayAdapter<String>(MyTracks.this,
				android.R.layout.simple_list_item_1, items));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
		            long id) {
				Intent inte = new Intent(MyTracks.this,MapActivityView.class);
				MapActivityView.myTrack = GetObiecte.tracks.get(position);
				startActivity(inte);
		    }
        });
		
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		TrackActivity.track = GetObiecte.tracks.get(position);
		Intent intent = new Intent(MyTracks.this,TrackActivity.class);
		startActivity(intent);
	}
}
