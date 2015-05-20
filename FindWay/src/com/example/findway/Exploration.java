package com.example.findway;

import android.R.color;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Exploration extends Activity implements OnCheckedChangeListener {
	StartFinding startfinding = new StartFinding();
	public static TextView text_S,text_E;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(color.transparent));
		setContentView(R.layout.exploration); // 레이아웃 불러오기

		text_S = (TextView)findViewById(R.id.text_S);
		text_E = (TextView)findViewById(R.id.text_E);
		
		text_S.setText("     -"+startfinding.StartLocationName);
		text_E.setText("     -"+startfinding.EndLocationName);
		
		Button button_ok = (Button) findViewById(R.id.button_ok); // 버튼 선택
		button_ok.setOnClickListener(buttonClickListener);

		Button button_cancel = (Button) findViewById(R.id.button_cancel); // 버튼 선택
		button_cancel.setOnClickListener(buttonClickListener2);

		//returnData = Algorithm(StartLocationName,EndLocationName); 
	}

	private OnClickListener buttonClickListener = new OnClickListener() {
		public void onClick(View v) {
			startfinding.GoNDK = true;
			finish();			
		}
	};

	private OnClickListener buttonClickListener2 = new OnClickListener() {
		public void onClick(View v) {
			finish();

		}
	};

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}
}
