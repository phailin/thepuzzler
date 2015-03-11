package com.netra.thepuzzler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;
import android.support.v4.widget.ResourceCursorAdapter;


public class LevelActivity extends Activity {	
	
	private PuzzlerDbHelper dbHelper;
	private Cursor listCursor;
	
	private TextView tvlvlCleared;	// should be static?
	private TextView tvTotPuzSolved;
	private TextView tvTotScore;
	private Button startBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.activity_level);		
		if ( customTitleSupported ) {
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_level);
	    }
		
		dbHelper = new PuzzlerDbHelper(this);
		dbHelper.open();
		
		
		listCursor = dbHelper.fetchDataForAllLevels();
		/* startManagingCursor() is deprecated but not a problem in this case. The new way of doing this 
		 * is using CursorLoder which will be essential while implementing FragmentActivity.
		 * Reason for deprecation is manager of the activity requery the db on the UI thread
		 * on activity restart which is a bad practice.
		 * 
		 * So this optimization will be done a bit later. (i have noticed performance issue with this. )
		 */
		startManagingCursor(listCursor);
		
	    ListView listView = (ListView) findViewById(R.id.listViewLevels);				
	    // Assign adapter to ListView
		listView.setAdapter(new MyListAdapter(this, listCursor));		
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();		
		//update the level cleared, total score and total puzzle solved
		updateTitleBarValues();		
		
	}



	private void updateTitleBarValues() {
		
		//get data from the db
		int totalscore = 0;
		int totalpuzsolved = 0;
		int totallvlcleared = 0;
		
		Cursor c = dbHelper.fetchDataForAllLevels();
		
		while (!c.isAfterLast()) {
			
			totalscore = totalscore + c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_SCORE));
			totalpuzsolved = totalpuzsolved + c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_SOLVED));
			
			//int value =  c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_IS_COMPLETE));
			int value =  c.getInt(c.getColumnIndex(PuzzlerDbHelper.KEY_IS_CLEARED));
			
			if( value == 1 )
			{
				++totallvlcleared;
			}
			
			c.moveToNext();
		}
		
		tvlvlCleared = (TextView) findViewById(R.id.title_tv_lev_clear);
		tvlvlCleared.setText("Cleared: " + String.valueOf(totallvlcleared));
		
		tvTotPuzSolved = (TextView) findViewById(R.id.title_tv_total_solved);
		tvTotPuzSolved.setText("Solved: " + String.valueOf(totalpuzsolved));
		
		tvTotScore = (TextView) findViewById(R.id.title_tv_total_score);
		tvTotScore.setText("Scored: " + String.valueOf(totalscore));
		
	}

	
	// custom cursor adapter class to bind the view with the data from the db
	private class MyListAdapter extends ResourceCursorAdapter {
	     
	    public MyListAdapter(Context context, Cursor cursor) {
	        super(context, R.layout.level_items, cursor);
	    }
	 
	    @Override
	    public void bindView(View view, Context context, Cursor cursor) {
	    	
	    	//set the diff background for diff listview row
	    	
	    	
	    	View tblView = view.findViewById(R.id.table_layout);
	    	
	    	int pos = cursor.getPosition();
	    	if (pos == 0 || pos == 3 || pos == 6 || pos == 9) {
	    		tblView.setBackgroundResource(R.drawable.red_rectangle);
	        } else if (pos == 1 || pos == 4 || pos == 7) {
	        	tblView.setBackgroundResource(R.drawable.green_rectangle);
	        } else {
	        	tblView.setBackgroundResource(R.drawable.yellow_rectangle);
	        }
	    	
	    	 TextView lev_id = (TextView) view.findViewById(R.id.id_level);
	    	 lev_id.setText("Level " + cursor.getString(
	                             cursor.getColumnIndex(PuzzlerDbHelper.KEY_LEVEL_ID)));
	    	 TextView solved = (TextView) view.findViewById(R.id.tv_lvl_solved);
	    	 solved.setText("Solved: " + cursor.getString(
	                             cursor.getColumnIndex(PuzzlerDbHelper.KEY_SOLVED)));
	    	 
	    	 TextView score = (TextView) view.findViewById(R.id.tv_lvl_score);
	    	 score.setText("Score: " + cursor.getString(
	                             cursor.getColumnIndex(PuzzlerDbHelper.KEY_SCORE)));
	    	 
	    	 ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.lvl_progress_bar);
	    	 int progressStatus = cursor.getInt( cursor.getColumnIndex( PuzzlerDbHelper.KEY_SOLVED ));
	    	 progressBar.setProgress(progressStatus);
	    	 
	    	 TextView progress_percnt = (TextView) view.findViewById(R.id.lvl_tv_percent);
	    	 progress_percnt.setText(progressStatus + "%");
	    	 
	    	 // Button should be enabled only if the level is activated or opened
	    	 startBtn = (Button) view.findViewById(R.id.btn_start);	    	 
	    	 int is_open =  cursor.getInt(cursor.getColumnIndex(PuzzlerDbHelper.KEY_IS_OPEN));
	    	 Log.v(" ***** &&&&&&& ******* For level " + cursor.getString(
                     cursor.getColumnIndex(PuzzlerDbHelper.KEY_LEVEL_ID)), "value of is_open: " + is_open);
	    	 
	    	 if(is_open == 0){
	    		 startBtn.setEnabled(false);
	    	 }else
	    	 {
	    		startBtn.setEnabled(true);
	    	 }
	    	 startBtn.setOnClickListener(new OnClickListener()
	    	 	{	    		 
	                 @Override
	                 public void onClick(View v)
	                 {
	                	 //pass level id and start activity
	                	 Intent intent = new Intent(v.getContext(), PuzzleActivity.class);
	                	 TableLayout tl = (TableLayout) v.getParent().getParent();
	                	 TextView tv = (TextView) tl.findViewById(R.id.id_level);
	                	 String lvl_id = tv.getText().toString();
	                	 String[] splitted = lvl_id.split(" ");	                	
	                	 intent.putExtra("levelId", splitted[1]); 
	                     startActivity(intent);	             	        
	                 }		
	             });
	    }
	}

}