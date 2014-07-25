package com.samples.speeddemo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Track {

	public ArrayList<Punct> pins;
	public String name;
	public Double distance;

	public Track() {
		name = "";
		pins = new ArrayList<Punct>();
		distance = 0.00;

	}

	public Track trackFromJson(String strJA, String name) {
		JSONArray ja = null;
		JSONObject json = null;
		try {
			json = new JSONObject(strJA);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			ja = new JSONArray(json.getString("tracks"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<Punct> puncte = new ArrayList<Punct>();
		for (int i = 0; i < ja.length(); i++) {
			Punct pct = new Punct();
			JSONObject jo = null;
			try {
				jo = ja.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				pct.latitudine = jo.getDouble("lat");
				pct.longitudine = jo.getDouble("long");
				pct.isCheckpoint = jo.getString("isCheck").equals("true") ? true
						: false;
				pct.point = new LatLng(pct.latitudine, pct.longitudine);
				if (puncte.size() > 0) {
					if (puncte.get(0).latitudine != pct.latitudine && puncte.get(0).longitudine != pct.longitudine)
					puncte.add(pct);
				} else
					puncte.add(pct);
			} catch (Exception ex) {
				Log.d("ErrorJson", "Eroare Parsare");
			}

			this.pins.addAll(puncte);
			this.name = name;
		}

		return this;
	}

	public StringBuilder trackToJSON() {
		StringBuilder str = new StringBuilder();
		str.append("{\"tracks\": [");
		for (Punct p : this.pins) {
			str.append(p.punctToJson());
			str.append(",");
		}
		str.deleteCharAt(str.length() - 1);
		str.append("]}");
		return str;
	}

	public void save(Context ctx) {
		DatabaseConnector dbCon = new DatabaseConnector(ctx);
		dbCon.insertTrack(this);
	}

}
