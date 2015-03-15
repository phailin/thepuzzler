package com.netra.thepuzzler;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AboutActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_about);
		
		TextView tvAboutContent = (TextView)findViewById(R.id.tvAbtContent);
		tvAboutContent.setText(Html.fromHtml(getResources().getString(R.string.about_text)));
		Linkify.addLinks(tvAboutContent, Linkify.WEB_URLS);
		
	}
}