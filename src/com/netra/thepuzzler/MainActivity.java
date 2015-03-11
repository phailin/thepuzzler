package com.netra.thepuzzler;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener{
	//private static final boolean DEVELOPER_MODE = true;
	Button btnPlay, btnStats, btnInstructions, btnAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_main);
        
        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnPlay.setOnClickListener(this);
        btnStats = (Button) findViewById(R.id.buttonStats);
        btnStats.setOnClickListener(this);
        btnInstructions = (Button) findViewById(R.id.buttonInstructions);
        btnInstructions.setOnClickListener(this);
        btnAbout = (Button) findViewById(R.id.buttonAbout);
        btnAbout.setOnClickListener(this);
        
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.buttonPlay: 
        	Intent intent = new Intent(this, LevelActivity.class);
            startActivity(intent);
	         break;
        case R.id.buttonStats:
        	Intent intent1 = new Intent(this, StatsActivity.class);
            startActivity(intent1);
	         break;
       case R.id.buttonInstructions:
        	Intent intent2 = new Intent(this, InstructActivity.class);
            startActivity(intent2);
	         break;
        case R.id.buttonAbout:
        	Intent intent3 = new Intent(this, AboutActivity.class);
            startActivity(intent3);
	        break;
		}
		
	}

}
