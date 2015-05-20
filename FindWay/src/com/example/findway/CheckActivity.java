package com.example.findway;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.R.color;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class CheckActivity extends Activity implements OnCheckedChangeListener {
	StartFinding startfinding = new StartFinding();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(color.transparent));
		setContentView(R.layout.checkactivity); // ���̾ƿ� �ҷ�����

		Button button_ok = (Button) findViewById(R.id.button_ok); // ��ư ����
		button_ok.setOnClickListener(buttonClickListener);

		Button button_cancel = (Button) findViewById(R.id.button_cancel); // ��ư ����
		button_cancel.setOnClickListener(buttonClickListener2);
		
	}

	private OnClickListener buttonClickListener = new OnClickListener() {
		public void onClick(View v) {
			
			startfinding.click=false;
			for(int i=0;i<20;i++){
			startfinding.mapView.removePOIItem(startfinding.poiItem[i]);
			startfinding.mapView.removePOIItem(startfinding.poiItem2[i]);
			}
			startfinding.mapView.removePOIItem(startfinding.poiItem_S);
			//startfinding.poiItem_S.setItemName(startfinding.StartLocationName);
			startfinding.poiItem_S.setItemName(startfinding.StartLocationName);
			startfinding.poiItem_S.setTag(10011);
			startfinding.poiItem_S.setMapPoint(MapPoint.mapPointWithGeoCoord(startfinding.S_Location_x, startfinding.S_Location_y)); //��ǥ
			startfinding.poiItem_S.setMarkerType(MapPOIItem.MarkerType.CustomImage); //��Ŀ�� ����� �̹����� ��ȯ
			startfinding.poiItem_S.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround); // �𸣰���
			startfinding.poiItem_S.setShowCalloutBalloonOnTouch(false); //��Ŀ ��ġ�ϸ� Ÿ��Ʋǳ���� ��			
			startfinding.poiItem_S.setCustomImageResourceId(R.drawable.custom_poi_marker_start);// ����� ���� ��Ŀ ȣ��
			//startfinding.poiItem_S.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2)); // �𸣰���
			startfinding.mapView.addPOIItem(startfinding.poiItem_S); // ȭ�鿡 ���
			startfinding.mapView.addPOIItem(startfinding.poiItem_E); // ȭ�鿡 ���
			startfinding.S_checking = true;
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
