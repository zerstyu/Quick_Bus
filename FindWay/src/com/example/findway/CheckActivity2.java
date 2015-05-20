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
		setContentView(R.layout.checkactivity2); // 레이아웃 불러오기

		Button button_ok = (Button) findViewById(R.id.button_ok); // 버튼 선택
		button_ok.setOnClickListener(buttonClickListener);

		Button button_cancel = (Button) findViewById(R.id.button_cancel); // 버튼 선택
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
			startfinding.poiItem_E.setMapPoint(MapPoint.mapPointWithGeoCoord(startfinding.E_Location_x, startfinding.E_Location_y)); //좌표		
			startfinding.poiItem_E.setMarkerType(MapPOIItem.MarkerType.CustomImage); //마커를 사용자 이미지로 변환
			startfinding.poiItem_E.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround); // 모르겠음
			startfinding.poiItem_E.setShowCalloutBalloonOnTouch(false); //마커 터치하면 타이틀풍선이 뜸
			startfinding.poiItem_E.setCustomImageResourceId(R.drawable.custom_poi_marker_end);// 사용자 지정 마커 호출
			//startfinding.poiItem_E.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2)); // 모르겠음
			startfinding.mapView.addPOIItem(startfinding.poiItem_E); // 화면에 출력
			startfinding.mapView.addPOIItem(startfinding.poiItem_S); // 화면에 출력
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
