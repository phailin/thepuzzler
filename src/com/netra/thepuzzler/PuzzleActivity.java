/***************************************************************************************************************
 PuzzleActivity.java file displays a single puzzle on the screen. Also score related calculations is done here.
 Options : 1. Navigate to next puzzle 2. Navigate to previous puzzle 3. Check for Right Answer 4. Get hint
 5. Get Letter hint 6. Show Answer
 **************************************************************************************************************/

package com.netra.thepuzzler;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleActivity extends  Activity implements OnClickListener, OnTouchListener {
	
	private PuzzlerDbHelper mDb; // the helper class to get access to db
	private Puzzle mPuz;   // Puzzle object to store puzzle obtained from db
	private Level mLvl;
	
 	//puzzle related
	private int mCurPuzId=0;
	private static int mlevelId=1;
    private String mPuzAns="";
        
    //level related    
    private  int lvlScore=0;
    private  int puzSolved=0;
    private  int hints_used=0;
    private  int letters_hints_used=0;
    private  int ans_viewed=0;
    private  int isOpen=0;
    private  int isComplete=0;
    private  int isCleared=0;
    private  ArrayList<Integer> listPuzSolved; //used to track solved puzzle ids; list is stored as string in db
    private  ArrayList<Integer> listHintsUsed;  //used to track hints used, so that score not deducted again
    private  ArrayList<Integer> listLettersHintsUsed;
    private  ArrayList<Integer> listAnsViewed;
    
    
    //widgets
    private static TextView tvPuzContent;
    private EditText etPuzAns;
    private Button btnCheck;
    private Button btnNext;
    private Button btnPrev;
    private Button btnHint; // for logical hint, hint 1
    private Button btnLettersHint; // for hints with letters,  hint2 
    private Button btnAns;
    
    private TextView tvLevel;
    private TextView tvScore;
    private TextView tvSolved;
    
    private Dialog dialogHint;
    private Dialog dialogLettersHint;
    private Dialog dialogShowAns;
    
	private static boolean LVL_CLEAR_SCORE_FIRST_TIME = true; // to display message 1st time score reach 60+
	//private static boolean levelCompleted = false;
	
	private static int minPuzId, maxPuzId; // for checking the upper and lower bound of 
							//puzzle id in a particular level
	
	//for swiping puzzles
	float x1=0;
    float x2=0;
    
    private static final int LVL_CLEAR_SCORE = 45;
    private static final int ANS_DEDUCTION = 5;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_puzzle); // its imp to draw this layout before custom title layout
		
		//set custom title bar
		if ( customTitleSupported ) {			
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_puzzle);
	    }
		
		
        
        //set the listeners
        btnCheck = (Button) findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(this);   
        btnNext = (Button) findViewById(R.id.btn_next_puz);
        btnNext.setOnClickListener(this);            
        btnPrev = (Button) findViewById(R.id.btn_prev_puz);
        btnPrev.setOnClickListener(this); 
        btnHint = (Button) findViewById(R.id.btn_hint);
        btnHint.setOnClickListener(this);
        btnLettersHint = (Button) findViewById(R.id.btn_hint_letters);
        btnLettersHint.setOnClickListener(this);  
        btnAns = (Button) findViewById(R.id.btn_ans);
        btnAns.setOnClickListener(this); 
        
        //get the data from the intent
		Intent intent = getIntent();
		String id = intent.getStringExtra("levelId");
		mlevelId = Integer.parseInt(id);
		Log.v("What the vlaue of ID obtaine from Level Activity: ", id);
      					
        
        populateView(mlevelId);
        
        //to process the "Go" option in android soft key
        etPuzAns = (EditText) findViewById(R.id.te_puz_ans);
        etPuzAns.setOnKeyListener(new OnKeyListener() {
	            public boolean onKey(View view, int keyCode, KeyEvent event) {
	            	if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
	            		checkAndNotify(mPuzAns);
	            		return true;
	            	} else {
	                    return false;
	                }
	            }
        });
        
        
    }
        
        

    
	private void populateView(int lvlid) {
		
		// get data for the level from the db		
		try{
			mDb = new PuzzlerDbHelper(this);
			mDb.open();
			mLvl = mDb.getLevelData(lvlid);
		}catch (Exception e)
		{
			android.util.Log.v("Database Error in PuzzleActivity, getLevelData():", e.getMessage());			
		} 
		
		// set the right values for the following static data members from the db for activity restart		
		isComplete = mLvl.getIsComplete();	
		isCleared = mLvl.getIsCleared();	
        maxPuzId = maxPuzIdForLevel(lvlid);
        minPuzId = minPuzIdForLevel(lvlid);
        listPuzSolved = mLvl.getListPuzSolved();
        listHintsUsed = mLvl.getListHintsUsed();
        listLettersHintsUsed = mLvl.getListLettersHintsUsed();
        listAnsViewed = mLvl.getListAnsViewed();
		//isOpen = mLvl.getIsOpen();
		
		//populate the view
		tvLevel = (TextView) findViewById(R.id.title_tv_level);
    	String level = "Level: " + String.valueOf(lvlid); // no need to fetch from mLvl
    	tvLevel.setText(level);
    	
    	
    	tvScore = (TextView) findViewById(R.id.title_tv_score);
    	lvlScore = mLvl.getScore();
		String score = "Score: " + String.valueOf(lvlScore);
    	tvScore.setText(score);
    	tvScore.setTypeface(Typeface.DEFAULT_BOLD);
    	
    	if(lvlScore < LVL_CLEAR_SCORE){
    		LVL_CLEAR_SCORE_FIRST_TIME = true;
    	}
		tvSolved = (TextView) findViewById(R.id.title_tv_solved);
		puzSolved = mLvl.getSolved();
		String solved = "Solved: " + String.valueOf(puzSolved);
		tvSolved.setText(solved);	
		tvSolved.setTypeface(Typeface.DEFAULT_BOLD);
		
		
		
        mCurPuzId = mLvl.getCur_puz_id();        
        mPuz = mDb.getPuzzle(mCurPuzId);
      
                
		if(mPuz != null)
		{
			tvPuzContent = (TextView) findViewById(R.id.tv_puz_content);			
			tvPuzContent.setOnTouchListener(this);
			
			tvPuzContent.setMovementMethod(new ScrollingMovementMethod()); // for scrolling the text inside textview
			tvPuzContent.setText(mPuz.getContent().toString());
			
			mPuzAns = mPuz.getAnswer();
			// if the puzzle is in solved list disable the check and hint buttons
			// and set the answer visible in the ans edit text. same for ans viewed by the user
			if(listPuzSolved.contains(mCurPuzId) || listAnsViewed.contains(mCurPuzId) ){
				etPuzAns = (EditText) findViewById(R.id.te_puz_ans);
				etPuzAns.setText(mPuzAns);
				etPuzAns.setEnabled(false);
				btnCheck.setEnabled(false);
				btnHint.setEnabled(false);
				btnLettersHint.setEnabled(false);
				btnAns.setEnabled(false);
			}		
		
		}	 
		
		
	}

	
	@Override
	protected void onResume() {
    	super.onResume();
    	
	}

    
    @Override
    public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btn_check:
        	checkAndNotify(mPuzAns);
        	break;
        case R.id.btn_hint:
        	showHint();
        	break;
        case R.id.btn_hint_letters:
        	showLettersHint();
        	break;
        case R.id.btn_next_puz:
        	nextPuzzle(mlevelId);
        	break;
        case R.id.btn_prev_puz:
        	prevPuzzle(mlevelId);
        	break; 
        case R.id.btn_ans:
        	showAns();
        	break;
        	
       	}		
	}




	/**
	 * This function checks for right or wrong answer and notify the user accordingly.
	 * It also checks the score. When score is above 45 it then activates next level.
	 * When all 10 questions are answered says, says level is completed.
	 * @param puzAns : answer from the db, which is compared with the answer from user. 
	 */
	private void checkAndNotify(String ansFromDb)
    {
       	String ansFromUser = ((EditText)findViewById(R.id.te_puz_ans)).getText().toString();
       	
       	// check if the edittext is empty
       	if (ansFromUser.matches("")) {
       	    Toast.makeText(this, "Enter the answer!", Toast.LENGTH_SHORT).show();
       	    return;
       	}
       	       
       	
       	//when answer is right       	
       	if(ansFromUser.trim().equalsIgnoreCase(ansFromDb.trim()))
       	{
   			lvlScore = lvlScore + 10;
			puzSolved = puzSolved + 1;   
			listPuzSolved.add(mCurPuzId); 
			      		
       		if( lvlScore < LVL_CLEAR_SCORE) {
       			//text.setText("Wow! Right Answer!");
       			Toast.makeText(this, "Wow! Right Answer!", Toast.LENGTH_SHORT).show();       			
       			
       		}else {        			
       			if(listPuzSolved.size() < 11) {
           			if ( LVL_CLEAR_SCORE_FIRST_TIME == true ) {
           				Toast.makeText(this, "Cool! You have unlocked a new level!", Toast.LENGTH_SHORT).show();
           				isCleared = 1;
           				LVL_CLEAR_SCORE_FIRST_TIME = false; 
           				int next_lvl_id = mlevelId + 1;
           				unLuckLevel(next_lvl_id);
           			}else {           				
           				Toast.makeText(this, "Wow! Right Answer!", Toast.LENGTH_SHORT).show();
               			
           			}       				
       			}else if (listPuzSolved.size() == 11) {    				
           			Toast.makeText(this, "Great! Level Completed!", Toast.LENGTH_SHORT).show();
           			isComplete = 1;         			
       				
       			}else {       				
       				//Log.v("ERROR IN  RIDDLER GAME LOGIC", "size should not be greater then 11");
       				Toast.makeText(this, "LOGIC ERROR in PUZZLE ACTIVITY", Toast.LENGTH_SHORT).show();
       			}	
       			
       		}       		
       		//restart the activity with new data
       		if(isComplete == 1){
       			
       			// we do not increase the puz id before saving  as the current id will be obtained
       			// from the db for the level when the activity restart    			
       			
       			mLvl.setIsComplete(1);
       			mLvl.setIsCleared(isCleared);
       			//isComplete = 0; // set it to 0 again so that next level  
       			mLvl.setScore(lvlScore);
       			mLvl.setSolved(puzSolved);
       			mLvl.setListPuzSolved(listPuzSolved);
       			/*mLvl.setHintsUsed(hints_used);
       			mLvl.setLettersHintsUsed(letters_hints_used);
       			mLvl.setListHintsUsed(listHintsUsed);
       			mLvl.setListLettersHintsUsed(listLettersHintsUsed);
       			mLvl.setAnsViewed(ans_viewed);
       			mLvl.setListAnsViewed(listAnsViewed);*/
       			mLvl.setLevel_id(mlevelId);
       			PuzzleActivity.this.updateLevelData(mLvl); // update all changes to db
       			
       			int nextlvlid = mlevelId + 1;
       			Intent intent = getIntent();
    			intent.putExtra("levelId", Integer.toString(nextlvlid));
    			finish();		
    			startActivity(intent);
    			
    			
       		}else{  
       			
       			// here we increase the cur puz id
       			if( mCurPuzId == maxPuzId ){
       				mCurPuzId = mCurPuzId; // special case when level is incomplete and user solved the last riddle
       			}else {
       				++mCurPuzId;
       			}
       			mLvl.setCur_puz_id(mCurPuzId);
       			mLvl.setIsComplete(0);
       			mLvl.setIsCleared(isCleared);
       			mLvl.setScore(lvlScore);
       			mLvl.setSolved(puzSolved);
       			mLvl.setListPuzSolved(listPuzSolved);
       			mLvl.setLevel_id(mlevelId);
       			PuzzleActivity.this.updateLevelData(mLvl); // update all changes to db
       			
       			Intent intent = getIntent();
    			intent.putExtra("levelId", Integer.toString(mlevelId));
    			finish();		
    			startActivity(intent);
       		}
       		
       	}else{ // when answer is wrong
       		Toast.makeText(this, "Sorry! You have to rethink!!", Toast.LENGTH_SHORT).show();
       	}
    }
    
    private void unLuckLevel(int next_lvl_id) {
    	try {
			mDb.setIsOpenForLevel(next_lvl_id);
		} catch (Exception e) {
			android.util.Log.v("Database Error in PuzzleActivity, getLevelData():", e.getMessage());	
			//e.printStackTrace();
		}
		
	}

	private void showLettersHint() {
		
		if(!listLettersHintsUsed.contains(mCurPuzId)){
			lvlScore = lvlScore - 3;
			++letters_hints_used; // increment the no. of hints used
			mLvl.setLettersHintsUsed(letters_hints_used); // set the no. to Level object for db update
			listLettersHintsUsed.add(mCurPuzId); // add the id to the list
			mLvl.setListLettersHintsUsed(listLettersHintsUsed); // set the list to Level object for db update
			mLvl.setScore(lvlScore);			
			
			PuzzleActivity.this.updateLevelData(mLvl);
		}
   		
   		// custom dialog
		dialogLettersHint = new Dialog(this);   		
   		// dialog.setTitle("The Puzzler:"); no need to set the tiltle
   		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLettersHint.setContentView(R.layout.custom_dialog);
   		// need to remove default android frame so that the rounded corner shown properly
		dialogLettersHint.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
   		
   		// set the custom dialog components - text, image and button
   		TextView text = (TextView) dialogLettersHint.findViewById(R.id.dlg_text);
   		text.setText(mPuz.getLettersHint());
   		
   		
   		
    	Button dialogButton1 = (Button) dialogLettersHint.findViewById(R.id.dlg_btn);
   		dialogButton1.setOnClickListener(new OnClickListener(){
   				@Override
       			public void onClick(View v) {
   					/* Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",
   						   Toast.LENGTH_LONG).show();*/
   					dialogLettersHint.dismiss(); // need to dismiss the dialog before activity finish.
   					
   					Intent intent = getIntent();
   					intent.putExtra("levelId", Integer.toString(mlevelId));
   	    			finish();		
   	    			startActivity(intent);
       			}					
   		});       	 
   		dialogLettersHint.show();

    	
		//Toast.makeText(this, "The anser is 5 leteers word with A,E,P,L in it.", 
				//Toast.LENGTH_LONG).show();
		
	}

	private void showHint() {	
   		if(!listHintsUsed.contains(mCurPuzId)){
			lvlScore = lvlScore - 2;
			++hints_used; // increment list used
			mLvl.setHintsUsed(hints_used); // update hints used for Level obj
			listHintsUsed.add(mCurPuzId); // add the id to the list
			mLvl.setListHintsUsed(listHintsUsed); // update the list for Level Obj
			mLvl.setScore(lvlScore);
			
			PuzzleActivity.this.updateLevelData(mLvl);
		}
   		
   		// custom dialog
   		dialogHint = new Dialog(this);   		
   		// dialog.setTitle("The Puzzler:"); no need to set the tiltle
   		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
   		dialogHint.setContentView(R.layout.custom_dialog);
   		// need to remove default android frame so that the rounded corner shown properly
   		dialogHint.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
   		
   		// set the custom dialog components - text, image and button
   		TextView text = (TextView) dialogHint.findViewById(R.id.dlg_text);
   		text.setText(mPuz.getHint());
   		
   		
   		
    	Button dialogButton1 = (Button) dialogHint.findViewById(R.id.dlg_btn);
   		dialogButton1.setOnClickListener(new OnClickListener(){
   				@Override
       			public void onClick(View v) {
   					/* Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",
   						   Toast.LENGTH_LONG).show();*/
   					dialogHint.dismiss(); // need to dismiss the dialog before activity finish.
   					
   					Intent intent = getIntent();
   					intent.putExtra("levelId", Integer.toString(mlevelId));
   	    			finish();		
   	    			startActivity(intent);
       			}					
   		});       	 
   		dialogHint.show();
		
	}
	
	
	private void showAns() {
		//Toast.makeText(this, "Show ans cliscked", Toast.LENGTH_SHORT).show();
		if(!listAnsViewed.contains(mCurPuzId)){
			lvlScore = lvlScore - ANS_DEDUCTION;	// deduct for viewing ans
			++ans_viewed; // increment list used
			mLvl.setAnsViewed(ans_viewed); // update hints used for Level obj
			listAnsViewed.add(mCurPuzId); // add the id to the list
			mLvl.setListAnsViewed(listAnsViewed); // update the list for Level Obj
			mLvl.setScore(lvlScore);
			
			PuzzleActivity.this.updateLevelData(mLvl);
		}
   		
   		// custom dialog
		dialogShowAns = new Dialog(this);   		
   		// dialog.setTitle("The Puzzler:"); no need to set the tiltle
   		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogShowAns.setContentView(R.layout.custom_dialog);
   		// need to remove default android frame so that the rounded corner shown properly
		dialogShowAns.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
   		
   		// set the custom dialog components - text, image and button
   		TextView text = (TextView) dialogShowAns.findViewById(R.id.dlg_text);
   		text.setText(mPuz.getAnswer());   		
   		
   		
    	Button dialogButton1 = (Button) dialogShowAns.findViewById(R.id.dlg_btn);
   		dialogButton1.setOnClickListener(new OnClickListener(){
   				@Override
       			public void onClick(View v) {
   					/* Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",
   						   Toast.LENGTH_LONG).show();*/
   					dialogShowAns.dismiss(); // need to dismiss the dialog before activity finish.
   					
   					Intent intent = getIntent();
   					intent.putExtra("levelId", Integer.toString(mlevelId));
   	    			finish();		
   	    			startActivity(intent);
       			}					
   		});       	 
   		dialogShowAns.show();
		
	}

	private void prevPuzzle(int lvlId) {	
		
		if (mCurPuzId > minPuzId){
	    	--mCurPuzId;
	    	mLvl.setCur_puz_id(mCurPuzId);
			PuzzleActivity.this.updateLevelData(mLvl);
			
			Intent intent = getIntent();
			intent.putExtra("levelId", Integer.toString(mlevelId));
			finish();		
			startActivity(intent);
		}else
		{
			Toast.makeText(this, "This is the first riddle in this level", Toast.LENGTH_SHORT).show();
			
		}
	}

	private void nextPuzzle(int lvlId) {
		
		
		if(mCurPuzId < maxPuzId){
			++mCurPuzId;
			mLvl.setCur_puz_id(mCurPuzId);
			PuzzleActivity.this.updateLevelData(mLvl); // direct dbhelper can be called. revisit
			// no this is fine. called though this because it was inside anonymous onClicklistner. now no need 
			Intent intent = getIntent();
			intent.putExtra("levelId", Integer.toString(mlevelId));
			finish();		
			startActivity(intent);	
		}else {
			Toast.makeText(this, "This is the last riddle in this level", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
    protected void onDestroy() {
            super.onDestroy();
           // updateLevelData();
            mDb.close();
           
    }

	private void updateLevelData(Level l) {
			
		try{
			mDb.setLevelData(l);
		}
		catch(Exception e){
			//android.util.Log.d("EXCEPTION in DB UPDATE: ",  e.getMessage());
		}		
	}
	
	public int maxPuzIdForLevel(int level){
		int maxpuzid = 0;
		switch(level){
		case 1:
			maxpuzid=10;
			break;
		case 2:
			maxpuzid=20;
			break;		
		case 3:
			maxpuzid=30;
			break;
		case 4:
			maxpuzid=40;
			break;			
		case 5:
			maxpuzid=50;
			break;
		case 6:
			maxpuzid=60;
			break;		
		case 7:
			maxpuzid=70;
			break;
		case 8:
			maxpuzid=80;
			break;		
		case 9:
			maxpuzid=90;
			break;
		case 10:
			maxpuzid=100;
			break;		
		}
		return maxpuzid;
	}
	public int minPuzIdForLevel(int level){
		int minpuzid = 0;
		switch(level){
		case 1:
			minpuzid=1;
			break;
		case 2:
			minpuzid=11;
			break;		
		case 3:
			minpuzid=21;
			break;
		case 4:
			minpuzid=31;
			break;			
		case 5:
			minpuzid=41;
			break;
		case 6:
			minpuzid=51;
			break;		
		case 7:
			minpuzid=61;
			break;
		case 8:
			minpuzid=71;
			break;		
		case 9:
			minpuzid=81;
			break;
		case 10:
			minpuzid=91;
			break;		
		}
		return minpuzid;
	}
	
	// swipe the activity left, right for puzzles
	public boolean onTouch(View v, MotionEvent event)
    {			
		switch (event.getAction()) 
	    {
	    	// when user first touches the screen we get x and y coordinate
	       	case MotionEvent.ACTION_DOWN: 
			{
			     x1 = event.getX();               
			     break;
			}
	       	case MotionEvent.ACTION_UP: 
	       	{
	       		x2 = event.getX();
	       		//if left to right sweep event on screen
	       		if (x1 < x2) {
		       		//Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();
		       		prevPuzzle(mlevelId);
	       		}                
				// if right to left sweep event on screen
				if (x1 > x2) {
				     //Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
				   	 nextPuzzle(mlevelId);
				}          
				break;
	       	}
	     }
	     return true;
	}
   
}
