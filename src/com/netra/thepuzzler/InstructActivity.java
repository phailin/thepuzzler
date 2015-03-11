package com.netra.thepuzzler;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class InstructActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_instruct);	
		
		TextView txt = (TextView)findViewById(R.id.tvInstructContent);
		txt.setText(Html.fromHtml(getResources().getString(R.string.instructions_text)));
		
	}
}