package com.netra.thepuzzler;

import java.util.ArrayList;

import com.netra.thepuzzler.test.TestUtil;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatsActivity extends Activity {
	
	private PuzzlerDbHelper dbHelper;
	
	private TextView  tvTotScore, tvTotPuzSolved, tvHint1Viewed, tvHint2Viewed, tvAnsViewed, tvLvlCleared, tvLvlCompleted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_stats);
		
		dbHelper = new PuzzlerDbHelper(this);
		dbHelper.open();
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();	
		
		
		/*TestUtil tu = new TestUtil(this);
		tu.dumpDb();*/
		
		//get data from the db
		int totalscore = 0;
		int totalpuzsolved = 0;
		int totallvlcleared = 0;
		int totallvlcompleted = 0;
		int tot_hint_1_viewed = 0;
		int tot_hint_2_viewed = 0;
		int tot_ans_viewed = 0;
		
		
		Cursor c = dbHelper.fetchDataForAllLevels();
		
		while (!c.isAfterLast()) {
			
			totalscore = totalscore + c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_SCORE));
			totalpuzsolved = totalpuzsolved + c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_SOLVED));
			
			//int value =  c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_IS_COMPLETE));
			int clear_value =  c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_IS_CLEARED));			
			if( clear_value == 1 )
			{
				++totallvlcleared;
			}
			
			int complete_value =  c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_IS_COMPLETE));			
			if( complete_value == 1 )
			{
				++totallvlcompleted;
			}
			
			//get the list containing hints used. their size will tell how many times hints are used for a level
			String str_hints1_used = c.getString(c.getColumnIndex(PuzzlerDbHelper.KEY_ARR_HINTS_USED));	
			ArrayList<Integer> list_hints1_used = dbHelper.convertStringToArrayList(str_hints1_used);
			int lvl_hint_1_viewed = list_hints1_used.size() - 1; // -1 because an extra '0' is added to the list while creating it  
																 // to avoid exception
			tot_hint_1_viewed = tot_hint_1_viewed + lvl_hint_1_viewed;
			
			String str_hints2_used = c.getString(c.getColumnIndex(PuzzlerDbHelper.KEY_ARR_LETTERS_HINTS_USED));	
			ArrayList<Integer> list_hints2_used = dbHelper.convertStringToArrayList(str_hints2_used);
			int lvl_hint_2_viewed = list_hints2_used.size() - 1;			
			tot_hint_2_viewed = tot_hint_2_viewed + lvl_hint_2_viewed;
			
			String str_ans_viewed = c.getString(c.getColumnIndex(PuzzlerDbHelper.KEY_ARR_ANS_VIEWED));	
			ArrayList<Integer> list_ans_viewed = dbHelper.convertStringToArrayList(str_ans_viewed);
			int lvl_ans_viewed = list_ans_viewed.size() - 1;
			tot_ans_viewed = tot_ans_viewed + lvl_ans_viewed;
			
			c.moveToNext();
		}
		

		tvTotScore = (TextView) findViewById(R.id.tvStatTotalScore);
		tvTotScore.setText(String.valueOf(totalscore))	;
		
		tvTotPuzSolved = (TextView) findViewById(R.id.tvStatTotalPuzSolved);
		tvTotPuzSolved.setText(String.valueOf(totalpuzsolved));		
		
		tvAnsViewed = (TextView) findViewById(R.id.tvStatAnsViewed);
		tvAnsViewed.setText(String.valueOf(tot_ans_viewed));		
		
		tvHint1Viewed = (TextView) findViewById(R.id.tvStatHint1Viewed);
		tvHint1Viewed.setText(String.valueOf(tot_hint_1_viewed));
		
		tvHint2Viewed = (TextView) findViewById(R.id.tvStatHint2Viewed);
		tvHint2Viewed.setText( String.valueOf(tot_hint_2_viewed));
		
		tvLvlCleared = (TextView) findViewById(R.id.tvStatLvlCleared);
		tvLvlCleared.setText(String.valueOf(totallvlcleared));
		
		
		tvLvlCompleted = (TextView) findViewById(R.id.tvStatLvlCompleted);
		tvLvlCompleted.setText(String.valueOf(totallvlcompleted));
		
		
	}
}
