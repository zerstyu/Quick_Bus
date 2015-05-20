package com.example.findway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.R.color;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnCheckedChangeListener {
	StartFinding startfinding = new StartFinding();
	EditText edittext;
	RadioGroup rd;
	ProgressDialog progressDialog;
	String input = "";
	Context con;
	Activity act;
	
	public static Activity Activityfinish;

	private Location LocationS = new Location("pointS");
	private Location LocationE = new Location("pointE");
	public int num;// ������ ����
	public boolean Notdata = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		con = this;
		act = this;
		Activityfinish = DialogActivity.this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(color.transparent));
		setContentView(R.layout.dialogactivity); // ���̾ƿ� �ҷ�����

		edittext = (EditText) findViewById(R.id.edittext);

		rd = (RadioGroup) findViewById(R.id.radioGroup);
		rd.setOnCheckedChangeListener(this);

		Button button_ok = (Button) findViewById(R.id.button_ok); // ��ư ����
		button_ok.setOnClickListener(buttonClickListener);

		Button button_cancel = (Button) findViewById(R.id.button_cancel); // ��ư ����
		button_cancel.setOnClickListener(buttonClickListener2);
		
		Button button_etc = (Button) findViewById(R.id.button_etc); // ��ư ����
		button_etc.setOnClickListener(buttonClickListener3);
		
		
		edittext.setOnKeyListener(new OnKeyListener() { //����Ű ���� 
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // TODO Auto-generated method stub
		        if(keyCode == event.KEYCODE_ENTER)
		        {
		            return true;
		        }
		        if(keyCode == event.KEYCODE_SPACE)
		        {
		            return true;
		        }
		        return false;
		    }
		});

	}
	
	protected void onRestart()
	{
		super.onRestart();
		finish();
	}
	
	private OnClickListener buttonClickListener = new OnClickListener() {
		public void onClick(View v) {
			startfinding.Station_X.clear();
			startfinding.Station_Y.clear();
			startfinding.StationName.clear();
			startfinding.OutputData.clear();
			if (edittext.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "��Ҹ� �Է��ϼ���!",
						Toast.LENGTH_SHORT).show();
			} 
			else if (edittext.getText().toString().equals("��")||edittext.getText().toString().equals("��")){
					Toast.makeText(getApplicationContext(), "�Է� �� �� ���� �����Դϴ�.",
								Toast.LENGTH_SHORT).show();				
			}
			else if (edittext.getText().toString().equals("���")||edittext.getText().toString().equals("����õ")
					||edittext.getText().toString().equals("��õ")||edittext.getText().toString().equals("��õ")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("�Ȼ�")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("�ȼ�")
					||edittext.getText().toString().equals("������")||edittext.getText().toString().equals("�Ⱦ�")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("�ǿ�")
					||edittext.getText().toString().equals("������")||edittext.getText().toString().equals("��õ")
					||edittext.getText().toString().equals("��õ")||edittext.getText().toString().equals("�ϳ�")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("ȭ��")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("����")
					||edittext.getText().toString().equals("����")||edittext.getText().toString().equals("��õ")){
				Toast.makeText(getApplicationContext(), "�á������δ� �˻� �� �� �����ϴ�.",
							Toast.LENGTH_SHORT).show();				
			}	
			else {
				input = edittext.getText().toString();
				progressDialog = ProgressDialog.show(DialogActivity.this,
						"Ž����...", "��ø���ٷ��ּ���");
				final Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {

						act.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
								GetbaseData();
								if(Notdata == false)
									new Information(con).show();
							}

						});

						mHandler.removeMessages(0);
					}
				}, 200);

			}
		}		
	};

	private OnClickListener buttonClickListener2 = new OnClickListener() {
		public void onClick(View v) {				
			finish();

		}
	};
	
	private OnClickListener buttonClickListener3 = new OnClickListener() {
		public void onClick(View v) {
			startfinding.click = true;
			if(startfinding.selRadio == true){				
				for(int i=0;i<20;i++){
					startfinding.mapView.removePOIItem(startfinding.poiItem_S);
					startfinding.mapView.removePOIItem(startfinding.poiItem2[i]);
					}
				startfinding.mapView.addPOIItem(startfinding.poiItem_E); // ȭ�鿡 ���
				}
			else{								
				for(int i=0;i<20;i++){
					startfinding.mapView.removePOIItem(startfinding.poiItem_E);
					startfinding.mapView.removePOIItem(startfinding.poiItem2[i]);
					}  		
				startfinding.mapView.addPOIItem(startfinding.poiItem_S); // ȭ�鿡 ���
			}
			
			Toast.makeText(getApplicationContext(), "��ġ�� ������ �ּ���.",
					Toast.LENGTH_SHORT).show();	
			finish();
		}
	};

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radioButton_Start:
			startfinding.selRadio = true;
			break;
		case R.id.radioButton_End:
			startfinding.selRadio = false;
			break;
		}

	}

	public void GetbaseData()
	{
		/*BufferedReader in;
		Resources myResources = getResources();
		InputStream myFile = myResources.openRawResource(R.raw.station20130415);
		StringBuffer strBuffer = new StringBuffer();
		String str = null;
		try {
			in = new BufferedReader(new InputStreamReader(myFile,
					"KSC5601")); //KSC5601 ���� ���� 
			while ((str = in.readLine()) != null)
			{
				strBuffer.append(str + "\n");
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		// ������ �̸�, ID , ��Ʈ ID ã��
		int len = 0, j =0;
		int temp_outS, temp_outE;
		double temp_y,temp_x;
		num = 0;
		String Search_data_temp = startfinding.strBuffer.toString().substring(0);
		while (Search_data_temp.contains(input)) {	
			len = Search_data_temp.indexOf(input);
			for (j=0; Search_data_temp.charAt(len-j)!='|';j++)
				;
			len = len -j+1;
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			if(startfinding.StationName.contains(Search_data_temp.substring(len,j)))
				;
			else{
				startfinding.StationName.add(new String(Search_data_temp.substring(len,j)));
			}
			
			temp_outS = len;
			temp_outE = j;
			len = j + 1 + 11;
			
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			startfinding.Station_Y.add(num, Double.valueOf(Search_data_temp.substring(len, j))
					.doubleValue());
			len = j + 1;
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			startfinding.Station_X.add(num, Double.valueOf(Search_data_temp.substring(len, j))
					.doubleValue());
			
			if(startfinding.selRadio == false&&startfinding.S_Location_x>0){
				LocationS.setLatitude(startfinding.S_Location_x);
				LocationS.setLongitude(startfinding.S_Location_y);
				LocationE.setLatitude(startfinding.Station_X.get(num));
				LocationE.setLongitude(startfinding.Station_Y.get(num));
					
				int distance = (int)LocationS.distanceTo(LocationE);
				
				startfinding.OutputData.add(new String("[���� �Ÿ� : "+ distance + "m ]"));
				
				//startfinding.OutputData.add(num,Search_data_temp
				//		.substring(temp_outS,temp_outE)+"\n[���� �Ÿ� : "+ distance + "m ]");
			}
			else{				
				startfinding.OutputData.add(new String("[ �Ÿ��� ���� �� �����ϴ�. ]"));
				
				//startfinding.OutputData.add(num,Search_data_temp
				//		.substring(temp_outS,temp_outE)+"\n"+"[ �Ÿ��� ���� �� �����ϴ�. ]");
			}
			
			for (j = len; Search_data_temp.charAt(j) != '^'; j++)
				;
			len = j + 1;
			Search_data_temp = Search_data_temp.substring(len);
			
			num ++;
			Notdata = false; 
		}
		
		if(!startfinding.strBuffer.toString().contains(input))
		{
			Notdata = true;
			Toast toast = Toast.makeText(DialogActivity.this, "�˻� ����� �����ϴ�.",Toast.LENGTH_SHORT); toast.show();
		}
	}
}
