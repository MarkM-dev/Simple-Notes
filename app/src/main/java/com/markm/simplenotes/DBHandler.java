package com.markm.simplenotes;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBHandler {
	
	private DBHelper helper;
	
	public DBHandler(Context con) {
		helper = new DBHelper(con, DBHelper.NOTES_DB_NAME, null, 1);
	}
	
	public void addNote(String subject, String contents) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			ContentValues cv = new ContentValues();
			cv.put(DBHelper.COLUMN_SUBJECT, subject);
			cv.put(DBHelper.COLUMN_CONTENTS, contents);
			
			db.insertOrThrow(DBHelper.TABLE_NAME, null, cv);
		} catch (SQLiteException e) {
			e.getCause();
		}finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public void updateNote (String id, String subject, String contents) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			ContentValues cv = new ContentValues();
			cv.put(DBHelper.COLUMN_SUBJECT, subject);
			cv.put(DBHelper.COLUMN_CONTENTS, contents);
			
			db.update(DBHelper.TABLE_NAME, cv, DBHelper.COLUMN_ID + "=?", new String [] {id});
		} catch (SQLiteException e) {
			e.getCause();
		}finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public void deleteNote (String id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			db.delete(DBHelper.TABLE_NAME, DBHelper.COLUMN_ID + "=?", new String [] {id});
			
		} catch (SQLiteException e) {
			e.getCause();
		}finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public void deleteAll () {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			db.delete(DBHelper.TABLE_NAME, null, null);
		} catch (SQLiteException e) {
			
		}finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public ArrayList<Note> getAllNotes(){
		Cursor cursor = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		try {
			cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			e.getCause();
		}
		
		ArrayList<Note> list = new ArrayList<Note>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String subject = cursor.getString(1);
			String contents = cursor.getString(2);
			
			list.add(new Note(id, subject, contents));
		}
		return list;
	}
}
