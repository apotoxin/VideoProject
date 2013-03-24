package com.mz.videorec;

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.*;

public class HomeActivity extends Activity implements OnClickListener {

	private Button videoButton;
	private Button uploadButton , accountButton , moreButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		videoButton = (Button) findViewById(R.id.buttonVideo);
		uploadButton = (Button) findViewById(R.id.buttonUpload);
		accountButton = (Button) findViewById(R.id.buttonAccount);
		moreButton = (Button) findViewById(R.id.buttonMore);

		videoButton.setOnClickListener(this);
		uploadButton.setOnClickListener(this);
		accountButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.buttonVideo) {
			startVideo();
		}
		if (id == R.id.buttonUpload) {
			startUpload();
		}
		if(id == R.id.buttonAccount){
			startAccount();  
		}
		if(id == R.id.buttonMore){
			more(); 
		}
	}

	private void more() {
		// TODO Auto-generated method stub
		
	}

	private void startAccount() {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(this,
					com.mz.videorec.sipua.ui.Settings.class);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startVideo() {
		try {
			Intent intent = new Intent(this,
					com.mz.videorec.sipua.ui.VideoCamera.class);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void startUpload() {

	}

}
