package com.samples.speeddemo;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class Punct {
	
	double latitudine;
	double longitudine;
//	String time;
	LatLng point;
	boolean isCheckpoint;

	public Punct ()
	{
		isCheckpoint = false;
	}
	
	public String punctToJson ()
	{
		JSONObject object = new JSONObject();
		try {
			object.put("isCheck",this.isCheckpoint+"");
			object.put("lat",this.latitudine+"");
			object.put("long",this.longitudine+"");
		} catch (JSONException e) {
	e.printStackTrace();
	}
		return object.toString();
	}

	
}
