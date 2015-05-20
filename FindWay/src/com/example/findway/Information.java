package com.example.findway;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Information extends Dialog {
	Context con;
	ListView lv;
	StationInformation sta;
	StartFinding startfinding;
	DialogActivity activityLogin = (DialogActivity)DialogActivity.Activityfinish;
	
	public Information(Context context) {
		super(context);
		con = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.station_information);
		lv = (ListView) findViewById(R.id.lv);
		//Log.d("lueseypid", "size : " + StartFinding.OutputData.size());

		sta = new StationInformation(con, StartFinding.StationName);
		lv.setAdapter(sta);	
		//lv.setOnItemClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> parent, View v, int position,
			     long id) {
				   
				  	startfinding.Index = position;
					startfinding.maker = true;
					activityLogin.finish();
					dismiss();
					
					}
			            
			  });

	}

}
