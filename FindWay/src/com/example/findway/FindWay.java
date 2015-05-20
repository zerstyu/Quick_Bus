package com.example.findway;

import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class FindWay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_way);
		Handler handler = new Handler();
		  handler.postDelayed(new Runnable() {
		   public void run() {
		    Intent intent = new Intent(FindWay.this, StartFinding.class);
		    startActivity(intent);
		    overridePendingTransition(android.R.anim.fade_in,
		      android.R.anim.fade_out);
		    FindWay.this.finish();
		   }
		  }, 1500);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_way, menu);
		return true;
	}
}