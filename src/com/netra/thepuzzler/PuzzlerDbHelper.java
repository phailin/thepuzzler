/*
 * This is the Helper class for the batabase operations. It extended the SQLiteAssetHelper
 * class provided by readyState Software Ltd to use existing sqlite db in the project.
 * It in turns extends the SQLiteOpenHelper class provided by Android.
 * 
 * Usage: Create an instance of this helper class. Call open() on the object to get the db opened. 
 * This create an instance of the db as a private member of this class. Then call various helper methods on
 * the helper object. This helper object utilizes the private db instance for the db operations.
 */

package com.netra.thepuzzler;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class PuzzlerDbHelper extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "puzzledb";
	private static final int DATABASE_VERSION = 1; // used to update db

	public static String strSeparator = "__,__"; // helper function to store
													// ArrayList as String
	
	private SQLiteDatabase sqldb;
	
	public static final String KEY_LEVEL_ID = "level";
	public static final String KEY_IS_OPEN = "is_open";
	public static final String KEY_IS_COMPLETE = "is_complete";
	public static final String KEY_IS_CLEARED = "is_cleared";
	public static final String KEY_SOLVED = "solved";
	public static final String KEY_HINTS_USED = "hints_used";
	public static final String KEY_LETTERS_HINTS_USED = "letters_hints_used";
	public static final String KEY_ANS_VIEWED = "ans_viewed";
	public static final String KEY_ARR_ANS_VIEWED = "arr_ans_viewed";
	public static final String KEY_SCORE = "score";
	public static final String KEY_ARR_HINTS_USED = "arr_hints_used";
	public static final String KEY_ARR_LETTERS_HINTS_USED = "arr_letters_hints_used";
	public static final String KEY_ARR_PUZ_SOLVED = "arr_puz_solved";
	public static final String KEY_CUR_PUZ_ID = "cur_puz_id";	
	private static final String SQLITE_TABLE = "scores";
	private static final String TAG = "PuzzlerDbHelper"; // to show in logs

	public PuzzlerDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// you can use an alternate constructor to specify a database location
		// (such as a folder on the sd card)
		// you must ensure that this folder is available and you have permission
		// to write to it
		// super(context, DATABASE_NAME,
		// context.getExternalFilesDir(null).getAbsolutePath(), null,
		// DATABASE_VERSION);
		// @N: do it Later
	}
	
	
	// open the db 
	public PuzzlerDbHelper open() throws SQLException {
		sqldb = getWritableDatabase();
		return this;
	}
	
	//close the db
	 public void close() {
		 if (sqldb != null) {
			 sqldb.close(); // the close is called on helper that close the actual db
		 }
	 }
	
	// fetch data for all levels
	public Cursor fetchDataForAllLevels() {
		 Cursor mCursor = sqldb.query(SQLITE_TABLE, new String[] { "_id", KEY_LEVEL_ID, KEY_IS_OPEN, KEY_IS_COMPLETE, 
				 KEY_IS_CLEARED, KEY_SOLVED, KEY_SCORE, KEY_HINTS_USED, KEY_LETTERS_HINTS_USED, KEY_ANS_VIEWED, 
				 KEY_ARR_HINTS_USED, KEY_ARR_LETTERS_HINTS_USED, KEY_ARR_ANS_VIEWED, KEY_ARR_PUZ_SOLVED },
				 null, null, null, null, null);		 
		  if (mCursor != null) {
			  mCursor.moveToFirst();
		  }
		  return mCursor;
	}
	
	//fetch data for a particular level
	public Cursor fetchDataForLevel(String levelid) throws SQLException {
		
		// Log.w(TAG, inputText);
		 Cursor mCursor = null;
		 if (levelid == null  ||  levelid.length () == 0)  {
			 mCursor = sqldb.query(SQLITE_TABLE, new String[] { "_id", KEY_LEVEL_ID, KEY_IS_OPEN, KEY_IS_COMPLETE, 
					 KEY_IS_CLEARED, KEY_SOLVED, KEY_SCORE, KEY_HINTS_USED, KEY_LETTERS_HINTS_USED, KEY_ANS_VIEWED, 
					 KEY_ARR_HINTS_USED, KEY_ARR_LETTERS_HINTS_USED, KEY_ARR_ANS_VIEWED, KEY_ARR_PUZ_SOLVED },
					 null, null, null, null, null);	
		 
		 }
		  else {
		   mCursor = sqldb.query(true, SQLITE_TABLE, new String[] { "_id", KEY_LEVEL_ID, KEY_IS_OPEN, KEY_IS_COMPLETE, 
					 KEY_IS_CLEARED, KEY_SOLVED, KEY_SCORE, KEY_HINTS_USED, KEY_LETTERS_HINTS_USED, KEY_ANS_VIEWED, 
					 KEY_ARR_HINTS_USED, KEY_ARR_LETTERS_HINTS_USED, KEY_ARR_ANS_VIEWED, KEY_ARR_PUZ_SOLVED }, 
					 KEY_LEVEL_ID + " = " + levelid , null, null, null, null, null);
		  }
		  if (mCursor != null) {
			 mCursor.moveToFirst();
		  }
		  return mCursor;
		 
	}

	// fetch a puzzle with all its data from the db by the puzzle id
	public Puzzle getPuzzle(int puzId) {
		Puzzle puz = new Puzzle();
		//SQLiteDatabase db = getReadableDatabase();
		String[] fields = { "puzzle_content", "puzzle_answer", "puzzle_hint", "puzzle_letters_hint"  };
		String[] parms = { String.valueOf(puzId) };
		Cursor c = sqldb.query("puzzles", fields, "puzzle_id=?", parms, null,
				null, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			puz.setContent(c.getString(0));
			puz.setAnswer(c.getString(1));
			puz.setHint(c.getString(2));
			puz.setLettersHint(c.getString(3));
			//android.util.Log.v("DATABSE: ","id: " + puzId + " answer: " + c.getString(1));
			c.moveToNext();
		}
		return puz;
	}
	
	//get the data for a level as an Level object by Level Id
	public Level getLevelData(int level_Id) {
		Level lvl = new Level();
		//SQLiteDatabase db = getReadableDatabase();
		String[] fields = { "level", "is_open", "solved", "hints_used","letters_hints_used", 
				"score", "arr_hints_used","arr_letters_hints_used", "arr_puz_solved", "cur_puz_id", "is_complete",
				"ans_viewed", "arr_ans_viewed", "is_cleared" };
		String[] parms = { String.valueOf(level_Id) }; // setting parameters list
														
		Cursor c = sqldb.query("scores", fields, "level=?", parms, null, null,null);
		// table name, fields, where_condition, parameters to replace ? marks,
		// null, null, null
		c.moveToFirst();

		while (!c.isAfterLast()) {
			lvl.setLevel_id(c.getInt(0));
			lvl.setIsOpen(c.getInt(1));
			lvl.setSolved(c.getInt(2));
			lvl.setHintsUsed(c.getInt(3));
			lvl.setLettersHintsUsed(c.getInt(4));
			lvl.setScore(c.getInt(5));
			
			String listHintsUsed = c.getString(6);
			ArrayList<Integer> list = convertStringToArrayList(listHintsUsed); 
			lvl.setListHintsUsed(list);	
			
			String listLettersHintsUsed = c.getString(7);
			ArrayList<Integer> list1 = convertStringToArrayList(listLettersHintsUsed); 
			lvl.setListLettersHintsUsed(list1);
			
			String listPuzSolved = c.getString(8);
			ArrayList<Integer> list2 = convertStringToArrayList(listPuzSolved);
			lvl.setListPuzSolved(list2);
			
			lvl.setCur_puz_id(c.getInt(9));
			lvl.setIsComplete(c.getInt(10));
			
			lvl.setAnsViewed(c.getInt(11));
			String listAnsViewed = c.getString(12);
			ArrayList<Integer> list3 = convertStringToArrayList(listAnsViewed);
			lvl.setListAnsViewed(list3);
			
			lvl.setIsCleared(c.getInt(13));
			
			c.moveToNext();
		}
		return lvl;
	}

	// setLevelData
	public void setLevelData(Level lvl) throws Exception {
		
		//SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();		
		values.put("is_open", lvl.getIsOpen());
		values.put("is_complete", lvl.getIsComplete());	
		values.put("is_cleared", lvl.getIsCleared());	
		values.put("score", lvl.getScore());
		
		values.put("hints_used", lvl.getHintsUsed());		
		String usedHints = convertArrayListToString(lvl.getListHintsUsed());
		values.put("arr_hints_used", usedHints);
		
		values.put("letters_hints_used", lvl.getLettersHintsUsed());		
		String usedLettersHints = convertArrayListToString(lvl.getListLettersHintsUsed());
		values.put("arr_letters_hints_used", usedLettersHints);	
		
		values.put("ans_viewed", lvl.getAnsViewed());		
		String viewedAnswers = convertArrayListToString(lvl.getListAnsViewed());
		values.put("arr_ans_viewed", viewedAnswers);
			
		values.put("solved", lvl.getSolved());
		String solvedList = convertArrayListToString(lvl.getListPuzSolved());
		values.put("arr_puz_solved", solvedList);		
		
		
		values.put("cur_puz_id", lvl.getCur_puz_id());

		String whereClause = "level=" + lvl.getLevel_id();
		
		sqldb.update("scores", values, whereClause, null);
	}
	
	public void setIsOpenForLevel(int next_lvl_id)throws Exception {
		ContentValues values = new ContentValues();
		values.put("is_open",1);
		
		String whereClause = "level=" + next_lvl_id;
		sqldb.update("scores", values, whereClause, null);
	}
	

	/**
	 * Helper function to covert ArrayList to String to insert into sqlite.
	 * @param solvedPuzIds
	 * @return
	 */
	public static String convertArrayListToString(
			ArrayList<Integer> solvedPuzIds) {
		String str = "";
		int size = solvedPuzIds.size();
		for (int i = 0; i < size; i++) {
			str = str + solvedPuzIds.get(i);
			// Do not append comma at the end of last element
			if (i < size - 1) {
				str = str + strSeparator;
			}
		}
		return str;
	}
	/**
	 * convert String to ArrayList
	 * @param str
	 * @return
	 */
	public static ArrayList<Integer> convertStringToArrayList(String str) {
		String[] arr = str.split(strSeparator);
		ArrayList<Integer> solvedPuzIds = new ArrayList<Integer>();
		for (int i = 0; i < arr.length; i++) {
			solvedPuzIds.add(Integer.parseInt(arr[i]));
		}
		return solvedPuzIds;
	}
	
}
