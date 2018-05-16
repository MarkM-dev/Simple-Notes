package com.markm.simplenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	public final static String TABLE_NAME = "notesTable";
	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_SUBJECT = "subject";
	public final static String COLUMN_CONTENTS = "contents";
	public final static String NOTES_DB_NAME = "notes.db";
	

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String cmd = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY , " + COLUMN_SUBJECT + " TEXT , " + COLUMN_CONTENTS + " TEXT );";
		
		try {
			db.execSQL(cmd);
		} catch (SQLiteException e) {
			e.getCause();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
