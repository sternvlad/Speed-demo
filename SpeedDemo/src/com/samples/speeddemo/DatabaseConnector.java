package com.samples.speeddemo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConnector extends SQLiteOpenHelper {

	private static String DB_NAME = "circuitDB.sqlite";
	@SuppressLint("SdCardPath")
	public static String DB_PATH = "/data/data/com.samples.speeddemo/databases/";
	private SQLiteDatabase circuit;
	public Cursor curs;
	private final Context myContext;
	public static boolean de;

	public DatabaseConnector(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[2048];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void open() throws Exception {
		boolean dbExist = checkDataBase();

		if (dbExist) {
			circuit = getWritableDatabase();
		} else {
			String myPath = DB_PATH + DB_NAME;
			circuit = getWritableDatabase();
			copyDataBase();
			circuit = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
	}

	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	@Override
	public synchronized void close() {
		if (circuit != null)
			circuit.close();
		if (curs != null)
			curs.close();
		super.close();
	}

	public void insertTrack(Track tr) {
		// insert Traseu in database
		ContentValues newCon = new ContentValues();
		newCon.put("nume", tr.name);
		newCon.put("traseu", tr.trackToJSON().toString());

		try {
			open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		circuit.insert("traseu", null, newCon);
		close();
	}
	
	public void deleteAllCircuite()
	{
		try {
			open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			curs = circuit.rawQuery("delete from traseu",	null);
			curs.moveToFirst();
		}catch (Exception ex){
			ex.printStackTrace();
		}
		close();
	}
	
	
	public ArrayList<Track> loadTrasee() {
		ArrayList<Track> trasee = new ArrayList<Track>();
		try {
			open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			curs = circuit.rawQuery("select * from traseu",	null);
			curs.moveToFirst();
			do {
				Track tr = new Track();
				tr = tr.trackFromJson(curs.getString(2),curs.getString(1));
				trasee.add(tr);
			} while (curs.moveToNext());
			curs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		close();
		
		return trasee;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
