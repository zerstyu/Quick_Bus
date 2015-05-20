package com.example.findway;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class CheckActivity2 extends Activity implements OnCheckedChangeListener {
	StartFinding startfinding = new StartFinding();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(color.transparent));
		setContentView(R.layout.checkactivity2); // ���̾ƿ� �ҷ�����

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
			startfinding.mapView.removePOIItem(startfinding.poiItem_E);
			startfinding.poiItem_E.setItemName(startfinding.EndLocationName);
			startfinding.poiItem_E.setTag(10011);
			startfinding.poiItem_E.setMapPoint(MapPoint.mapPointWithGeoCoord(startfinding.E_Location_x, startfinding.E_Location_y)); //��ǥ		
			startfinding.poiItem_E.setMarkerType(MapPOIItem.MarkerType.CustomImage); //��Ŀ�� ����� �̹����� ��ȯ
			startfinding.poiItem_E.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround); // �𸣰���
			startfinding.poiItem_E.setShowCalloutBalloonOnTouch(false); //��Ŀ ��ġ�ϸ� Ÿ��Ʋǳ���� ��
			startfinding.poiItem_E.setCustomImageResourceId(R.drawable.custom_poi_marker_end);// ����� ���� ��Ŀ ȣ��
			//startfinding.poiItem_E.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2)); // �𸣰���
			startfinding.mapView.addPOIItem(startfinding.poiItem_E); // ȭ�鿡 ���
			startfinding.mapView.addPOIItem(startfinding.poiItem_S); // ȭ�鿡 ���
			startfinding.E_checking = true;
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
