package com.example.findway;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class StartFinding extends Activity implements
		MapView.OpenAPIKeyAuthenticationResultListener,
		MapView.MapViewEventListener, MapView.CurrentLocationEventListener,
		MapView.POIItemEventListener,
		MapReverseGeoCoder.ReverseGeoCodingResultListener {
	
	private DownHtml downhtml = new DownHtml();
	private Location LocationS = new Location("pointS");
	private Location LocationE = new Location("pointE");
	
	public static  MapView mapView; // Map 관련 변수
	public LocationManager locManager; // 위치 정보 프로바이더
	public LocationListener locationListener; // 위치 정보가 업데이트시 동작
	public static MapPOIItem[] poiItem = new MapPOIItem[20]; //출발 관련 
	public static MapPOIItem[] poiItem2 = new MapPOIItem[20]; // 도착 관련
	public int count_S =0 ,count_E = 0;
	
	public static MapPOIItem poiItem_S = new MapPOIItem();
	public static MapPOIItem poiItem_E = new MapPOIItem();
	public static double myLocation_x,myLocation_y; //시작 위치에서 사용자 좌표
	public static double S_Location_x,S_Location_y; //출발 정류장 좌표
	public static double E_Location_x,E_Location_y; //도착 정류장 좌표
	
	public boolean findmylocation  = false; // 내위치 찾기 
	
	public static boolean selRadio = true;
	
	public static ArrayList<String> StationName = new ArrayList<String>();
	public static ArrayList<String>	OutputData = new ArrayList<String>();
	public static ArrayList<Double> Station_X = new ArrayList<Double>();
	public static ArrayList<Double>	Station_Y = new ArrayList<Double>();
	
	public ArrayList<String> Station_S = new ArrayList<String>();
	public ArrayList<String> Station_E = new ArrayList<String>();
	public ArrayList<Integer> S_StationID = new ArrayList<Integer>();
	public ArrayList<Integer> E_StationID = new ArrayList<Integer>();
	public static int Index;
	public int index = 0;
	public int S_index = 0;
	public int E_index = 0;
	public static boolean maker = false;
	public static boolean click = false;
	public static boolean GoNDK = false;
	
	public static boolean S_checking = false;
	public static boolean E_checking = false;
	ProgressDialog progressDialog;
	View viewresult;
	
	public static String StartLocationName = "";
	public static String EndLocationName = "";
	public static int StartLocationID = 0;
	public static int EndLocationID = 0;
	
	public boolean slidebool = true;
	//public static byte[] returnData;
	public static String returnData = "" ;
	public native String Algorithm(int StartLocationName,int EndLocationName, int Time, int Delay_Time);
	//public native String Algorithm();
	public LayoutParams params= new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.MATCH_PARENT);
	
	public LayoutParams param = new	LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	public LinearLayout linearLayout;
	public String RouteID = "";
	public int Send_Time = 0;
	public String StationID_F="",StationID_S="";
	public SlidingDrawer drawer ;
	public boolean slide = false;
	public boolean notbus = false;
	public static StringBuffer strBuffer;
	public String LastOutput  = "";
	public String EndTime = "";
	static {
		System.loadLibrary("mixed_sample");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL); // 세로 방향 고정
		

		MapView.setMapTilePersistentCacheEnabled(true); // 다운받은 지도 타일들을 단말의 플래쉬
													// 메모리에 캐쉬하여 재활용
		mapView = new MapView(this);
		for(int i=0;i<20;i++)
			poiItem[i] = new MapPOIItem();
		for(int i=0;i<20;i++)
			poiItem2[i] = new MapPOIItem();
		//mapView.setDaumMapApiKey("2599043c1b3686585cf1d997ff5b8ff540976472");
		mapView.setDaumMapApiKey("DAUM_MMAPS_ANDROID_DEMO_APIKEY");
		mapView.setOpenAPIKeyAuthenticationResultListener(this);
		mapView.setMapViewEventListener(this);
		mapView.setCurrentLocationEventListener(this);
		mapView.setPOIItemEventListener(this);
		mapView.setMapType(MapView.MapType.Standard); // 지도 유형 선택
		
		
		linearLayout.addView(mapView,param); // 맵 사이즈
		
		setContentView(linearLayout);		
		
		View v = getLayoutInflater().inflate(R.layout.button, null);
		this.addContentView(v, new LayoutParams(LayoutParams.FILL_PARENT,
				250));
		
		BufferedReader in;
		Resources myResources = getResources();
		InputStream myFile = myResources.openRawResource(R.raw.station20130415);
		strBuffer = new StringBuffer();
		String str = null;
		try {
			in = new BufferedReader(new InputStreamReader(myFile, "KSC5601")); // KSC5601 파일 형태
			while ((str = in.readLine()) != null) {
				strBuffer.append(str + "\n");
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // txt 파일 읽기 완료
		
		
		
		
		Button button1 = (Button)findViewById(R.id.button_search);
		button1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(final View v) {	
				mapView.removeAllPolylines();
				if(slide==true&&drawer.isOpened())
					drawer.close();
				selRadio = true;
				for(int i=0;i<20;i++){
					mapView.removePOIItem(poiItem[i]);
					mapView.removePOIItem(poiItem2[i]);
				}
				mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);				
				 progressDialog = ProgressDialog.show(StartFinding.this,
			 				"위치 탐색 중..", "잠시만기다려주세요");
			 		Handler mHandler = new Handler() {
			 			public void handleMessage(Message msg) {
			 				if (msg.what == 1) { // 타임아웃이 발생하면
			 					progressDialog.dismiss(); // ProgressDialog를 종료
			 					}
			 				findmylocation = true;			 	
			 				Toast toast = Toast.makeText(StartFinding.this,"원하는 장소의 마커를 선택해 주세요",Toast.LENGTH_LONG); toast.show();
			 				long downTime = SystemClock.uptimeMillis();
			 				long eventTime = SystemClock.uptimeMillis();
			 				MotionEvent down_event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, 200,600, 0);
			 				MotionEvent darg = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, 200,601, 0);
			 				MotionEvent up_event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, 200, 600, 0);
			 				dispatchTouchEvent(down_event);
			 				dispatchTouchEvent(darg);
			 				dispatchTouchEvent(up_event);	
			 			}
			 		};
			 		mHandler.sendEmptyMessageDelayed(1, 5000);
			}
		});
		
		Button button2 = (Button)findViewById(R.id.button_exploration);
		button2.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				mapView.removeAllPolylines();
				if(slide==true&&drawer.isOpened())
					drawer.close();
				//drawer.animateClose();
				selRadio = false;
				Intent intent = new Intent(StartFinding.this, DialogActivity.class);
				startActivity(intent);	
			}
		});

		
		Button button3 = (Button)findViewById(R.id.button_option);
		button3.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	
				mapView.removeAllPolylines();
				click=false;
				if(S_checking==true&&E_checking==true){
					
					LocationS.setLatitude(S_Location_x);
					LocationS.setLongitude(S_Location_y);
					LocationE.setLatitude(E_Location_x);
					LocationE.setLongitude(E_Location_y);
						
					int distance = (int)LocationS.distanceTo(LocationE);
					if(distance>700){
						StartLocationName = Station_S.get(S_index);
						EndLocationName = Station_E.get(E_index);
						StartLocationID = S_StationID.get(S_index);
						EndLocationID = E_StationID.get(E_index);
						Intent intent = new Intent(StartFinding.this, Exploration.class);
						startActivity(intent);
						}
					else{
						Toast toast = Toast.makeText(StartFinding.this, "버스로 이동하기에 너무 가까운 거리입니다." ,Toast.LENGTH_LONG); toast.show();
					}
				}
				else{
				Toast toast = Toast.makeText(StartFinding.this, "경로를 탐색 할 수 없습니다." ,Toast.LENGTH_LONG); toast.show();
				}
				
			}
		});	
		
    } 
	
	protected void onResume() {
		super.onResume();
		if (maker == true) {
			if(selRadio == true){
				for(int i=0;i<20;i++){
					mapView.removePOIItem(poiItem[i]);
					mapView.removePOIItem(poiItem2[i]);
					}
				}
			else{
				for(int i=0;i<20;i++){
					mapView.removePOIItem(poiItem[i]);
					mapView.removePOIItem(poiItem2[i]);
					}  				
			}
			mapView.setMapCenterPointAndZoomLevel(MapPoint
					.mapPointWithGeoCoord(Station_X.get(Index),
							Station_Y.get(Index)), 2, true);
			findNearBusStation(Station_X.get(Index), Station_Y.get(Index));
			maker = false;
		}

		if (GoNDK == true) {
			LastOutput = "";
			String Search_data_temp = strBuffer.toString().substring(0);
			returnData = Algorithm(StartLocationID,EndLocationID, -1, 0);
					
			if(slidebool == true){
			viewresult = getLayoutInflater().inflate(R.layout.startfinding,
			 null);
			 StartFinding.this.addContentView(viewresult,params);
			 slidebool = false;
			}
			 if(slide==false)
				 drawer = (SlidingDrawer) findViewById(R.id.slide);
			 slide = true;
			 //drawer.animateOpen();
		    String temp_returnDataA = "";
			int tttemp = 0;
			int temp_time_f = 0, temp_time_s = 0;
			String temp_SatationID_F = "";
			String url_f = "",url_s = "";
			String xml_f = "",xml_s = "";
			double temp_x_f=0,temp_y_f=0,temp_x_s=0,temp_y_s=0;
			MapPolyline polyline = new MapPolyline();	
			MapPolyline polyline_s = new MapPolyline();
			MapPolyline polyline_e = new MapPolyline();
			int bus_distance =0;
			String stid1 = "n",stid2 = "n",stid3 = "n",stid4 = "";
			String xml1 = "",xml2 = "",xml3 = "",xml4 = "";
			GoNDK = false;
			while (returnData.charAt(0) != 'E') {
				if (returnData.charAt(0) == 'A') {
					if(temp_returnDataA!=returnData){
						RouteID = returnData.substring(1, 10);
						StationID_F = returnData.substring(10,  19);
						StationID_S = returnData.substring(19, 28);
	
						if(temp_SatationID_F!=StationID_F){
						url_f = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
								+ StationID_F;
						xml_f = downhtml.DownloadHtml(url_f);
						}				
						
						temp_time_f = FindBusInfo(xml_f);
						temp_SatationID_F = StationID_F;
						
						url_s = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
								+ StationID_S;
						xml_s = downhtml.DownloadHtml(url_s);			
						temp_time_s = FindBusInfo(xml_s); 
						
						Send_Time = temp_time_s - temp_time_f;
						temp_returnDataA = returnData;
					}					
				} else if (returnData.charAt(0) == 'B') {
					RouteID = returnData.substring(1, 10);
					StationID_F = returnData.substring(10,  19);
					if(stid1.equals(StationID_F))
					{	
						temp_time_f = FindBusInfo(xml1);
					}else if(stid2.equals(StationID_F))
					{
						temp_time_f = FindBusInfo(xml2);
					}else if(stid3.equals(StationID_F))
					{
						temp_time_f = FindBusInfo(xml3);
					}else{										
						if(stid1.equals("n"))
						{
							stid1 = StationID_F;
							url_f = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
									+ StationID_F;
							xml1 = downhtml.DownloadHtml(url_f);
							
							temp_time_f = FindBusInfo(xml1);
						}else if(stid2.equals("n")){
							stid2 = StationID_F;
							url_f = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
									+ StationID_F;
							xml2 = downhtml.DownloadHtml(url_f);
							temp_time_f = FindBusInfo(xml2);
						}else if(stid3.equals("n")){
							stid3 = StationID_F;
							url_f = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
									+ StationID_F;
							xml3 = downhtml.DownloadHtml(url_f);
							temp_time_f = FindBusInfo(xml3);
						}
						else{
							stid4 = StationID_F;
							url_f = "http://openapi.gbis.go.kr/ws/rest/busarrivalservice/station?serviceKey=u+R9hPkZ2ZnqYeXsLx9EET8Fl3fftRCwUXMhrKoxQRaiOCjs9/OTVzVM73nQmgZfAimwY0ot6uvMnFKkr/avmA==&stationId="
									+ StationID_F;
							xml4 = downhtml.DownloadHtml(url_f);
							temp_time_f = FindBusInfo(xml4);
						}						
					}	
				} else if (returnData.charAt(0) == 'C') {
					if(tttemp==0&&!Integer.toString(StartLocationID).equals(returnData.substring(1,10)))
					{
						double temp_sx=0,temp_sy=0, temp_ex=0,temp_ey=0;
						int temp_len = 0, z=0;					
						
						temp_len = Search_data_temp.indexOf(Integer.toString(StartLocationID));
						temp_len = temp_len +10;						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						
						temp_len = z + 1 + 11;	
						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						temp_sy = Double.valueOf(Search_data_temp.substring(temp_len,z))
								.doubleValue();	
						
						temp_len = z + 1;	
						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						temp_sx = Double.valueOf(Search_data_temp.substring(temp_len,z))
								.doubleValue();
						
						temp_len = 0;
						z=0;
						temp_len = Search_data_temp.indexOf(returnData.substring(1,10));
						temp_len = temp_len +10;						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						temp_len = z + 1 + 11;						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						temp_ey = Double.valueOf(Search_data_temp.substring(temp_len,z))
								.doubleValue();						
						temp_len = z + 1;						
						for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
							;
						temp_ex = Double.valueOf(Search_data_temp.substring(temp_len,z))
								.doubleValue();
						
						polyline_s.setLineColor(Color.argb(128, 200 , 0, 0));
						polyline_s.setTag(2000);
						polyline_s.addPoint(MapPoint.mapPointWithGeoCoord(temp_sx,temp_sy));
						polyline_s.addPoint(MapPoint.mapPointWithGeoCoord(temp_ex,temp_ey));		
						mapView.addPolyline(polyline_s);	
					}
					
					int len = 0, j =0;
						len = Search_data_temp.indexOf(returnData.substring(1,10));
						len = len + 9 + 1;
						for (j = len; Search_data_temp.charAt(j) != '|'; j++)
							;
						LastOutput += Search_data_temp.substring(len,j);
						if(returnData.contains("&")){
							LastOutput += "(도착)";
							
							double temp_sx=0,temp_sy=0, temp_ex=0,temp_ey=0;
							int temp_len = 0, z=0;					
							
							temp_len = Search_data_temp.indexOf(Integer.toString(EndLocationID));
							temp_len = temp_len +10;						
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							
							temp_len = z + 1 + 11;	
							
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							temp_sy = Double.valueOf(Search_data_temp.substring(temp_len,z))
									.doubleValue();	
							
							temp_len = z + 1;	
							
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							temp_sx = Double.valueOf(Search_data_temp.substring(temp_len,z))
									.doubleValue();
							
							temp_len = 0;
							z=0;
							temp_len = Search_data_temp.indexOf(returnData.substring(1,10));
							temp_len = temp_len +10;						
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							temp_len = z + 1 + 11;						
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							temp_ey = Double.valueOf(Search_data_temp.substring(temp_len,z))
									.doubleValue();						
							temp_len = z + 1;						
							for (z = temp_len; Search_data_temp.charAt(z) != '|'; z++)
								;
							temp_ex = Double.valueOf(Search_data_temp.substring(temp_len,z))
									.doubleValue();
							
							polyline_e.setLineColor(Color.argb(128, 200 , 0, 0));
							polyline_e.setTag(2000);
							polyline_e.addPoint(MapPoint.mapPointWithGeoCoord(temp_sx,temp_sy));
							polyline_e.addPoint(MapPoint.mapPointWithGeoCoord(temp_ex,temp_ey));		
							mapView.addPolyline(polyline_e);	
									
						}
						else
							LastOutput += "("+returnData.substring(11)+")\n         ▼\n";
						tttemp =1;
				} else if (returnData.charAt(0) == 'D') {
					int len = 1, j =0;					
					Location temp_LocationS = new Location("pointS");
					Location temp_LocationE = new Location("pointE");
					
					while(true){
					for (j = len; returnData.charAt(j) != '|'; j++)
						;
					temp_y_s = Double.valueOf(returnData.substring(len,j))
							.doubleValue(); 
					len = j +1;
					for (j = len; returnData.charAt(j) != '|'; j++)
						;
					temp_x_s = Double.valueOf(returnData.substring(len,j))
							.doubleValue();	
					mapView.addPolyline(polyline);	
					if(temp_x_f!=0){
							polyline.setLineColor(Color.argb(128, 0, 128, 200));
							polyline.setTag(2000);
							polyline.addPoint(MapPoint.mapPointWithGeoCoord(temp_x_f,temp_y_f));
							polyline.addPoint(MapPoint.mapPointWithGeoCoord(temp_x_s,temp_y_s));
							mapView.addPolyline(polyline);											
							
					temp_LocationS.setLatitude(temp_x_f);
					temp_LocationS.setLongitude(temp_y_f);
					temp_LocationE.setLatitude(temp_x_s);
					temp_LocationE.setLongitude(temp_y_s);				
					bus_distance += (int) temp_LocationS.distanceTo(temp_LocationE);
					}				
					temp_x_f = temp_x_s;
					temp_y_f = temp_y_s;
					
					
					len = j + 1;
						if(returnData.charAt(len) == '&')
							break;
					}
					
				}else if (returnData.charAt(0) == 'F') {
					Toast toast = Toast.makeText(StartFinding.this,"데이터에 정류소가 없습니다.",Toast.LENGTH_LONG); toast.show();
					return ;				
					
				}else if (returnData.charAt(0) == 'G') {
					Toast toast = Toast.makeText(StartFinding.this,"정류장 거리가 너무 가깝습니다.",Toast.LENGTH_LONG); toast.show();
					return ;				
					
				}else if (returnData.charAt(0) == 'H') {
					Toast toast = Toast.makeText(StartFinding.this,"경로를 찾을 수 없습니다.",Toast.LENGTH_LONG); toast.show();
					return ;				
					
				}
				if (returnData.charAt(0) != 'F'&&returnData.charAt(0) != 'G'&&returnData.charAt(0) != 'H'){
					returnData = Algorithm(StartLocationID,EndLocationID, Send_Time, temp_time_f);
					//returnData = Algorithm(StartLocationID,EndLocationID, Send_Time, 0);
				}
			}
			
			EndTime = returnData.substring(1);
			
			if(slide==true&&!drawer.isOpened())
				drawer.open();
			Outdata(bus_distance);
		}
	}
	
	public void Gomyxy() //현재 자신의 위치 이동 및 좌표 획득
	{
		mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
	}

	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (KeyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage("QuickBus를 종료하시겠습니까?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									System.exit(0);
								}
							}).setNegativeButton("No", null).show();
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
		double distance = 1000;
		double temp_dis = 0;
		index = 0;
		if(selRadio == true){
			S_Location_x=arg1.getMapPoint().getMapPointGeoCoord().latitude;
			S_Location_y=arg1.getMapPoint().getMapPointGeoCoord().longitude;			
			LocationS.setLatitude(S_Location_x);
			LocationS.setLongitude(S_Location_y);
			for(int i=0;i<count_S;i++){
				LocationE.setLatitude(poiItem[i].getMapPoint().getMapPointGeoCoord().latitude);
				LocationE.setLongitude(poiItem[i].getMapPoint().getMapPointGeoCoord().longitude);				
				temp_dis = LocationS.distanceTo(LocationE);
				if(distance>temp_dis){
					distance = temp_dis;
					//Index = i0
					index = i;
					S_index = i;
				}
			}if(count_S!=0){
			S_Location_x = poiItem[index].getMapPoint().getMapPointGeoCoord().latitude;
			S_Location_y = poiItem[index].getMapPoint().getMapPointGeoCoord().longitude;
			}
			Intent intent = new Intent(StartFinding.this, CheckActivity.class);
				startActivity(intent);
		}
		else
		{
			E_Location_x=arg1.getMapPoint().getMapPointGeoCoord().latitude;
			E_Location_y=arg1.getMapPoint().getMapPointGeoCoord().longitude;
			
			LocationS.setLatitude(E_Location_x);
			LocationS.setLongitude(E_Location_y);
			for(int i=0;i<count_E;i++){
				LocationE.setLatitude(poiItem2[i].getMapPoint().getMapPointGeoCoord().latitude);
				LocationE.setLongitude(poiItem2[i].getMapPoint().getMapPointGeoCoord().longitude);				
				temp_dis = LocationS.distanceTo(LocationE);
				if(distance>temp_dis){
					distance = temp_dis;
					//Index = i;
					index = i;
					E_index = i;
				}
			}
			if(count_E!=0){
			E_Location_x = poiItem2[index].getMapPoint().getMapPointGeoCoord().latitude;
			E_Location_y = poiItem2[index].getMapPoint().getMapPointGeoCoord().longitude;
			}
			Intent intent = new Intent(StartFinding.this, CheckActivity2.class);
				startActivity(intent);
		}
	}

	@Override
	public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1,
			MapPoint arg2) {
		// 마커 드래그 해서 움직였을때
	}

	@Override
	public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
		
		if(arg1!=poiItem_E&&arg1!=poiItem_S){
			 Toast toast = Toast.makeText(StartFinding.this,"말풍선을 선택해 주세요",Toast.LENGTH_LONG); toast.show();
		}
	}

	@Override
	public void onCurrentLocationDeviceHeadingUpdate(MapView arg0, float arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCurrentLocationUpdate(MapView arg0, MapPoint arg1, float arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCurrentLocationUpdateCancelled(MapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurrentLocationUpdateFailed(MapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewCenterPointMoved(final MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		if(findmylocation == true){
			myLocation_x = arg0.getMapCenterPoint().getMapPointGeoCoord().latitude;
			myLocation_y = arg0.getMapCenterPoint().getMapPointGeoCoord().longitude;					
			findNearBusStation(myLocation_x,myLocation_y);
		}
		findmylocation = false;
	}

	@Override
	public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		// Toast toast = Toast.makeText(this, "더블 클릭", Toast.LENGTH_SHORT);
		// toast.show();
	}

	@Override
	public void onMapViewInitialized(final MapView arg0) {
		Gomyxy();				
	    progressDialog = ProgressDialog.show(StartFinding.this,
	 				"위치 탐색 중...", "잠시만기다려주세요");
	 		Handler mHandler = new Handler() {
	 			public void handleMessage(Message msg) {
	 				if (msg.what == 1) { // 타임아웃이 발생하면
	 					progressDialog.dismiss(); // ProgressDialog를 종료
	 					}
	 				myLocation_x = arg0.getMapCenterPoint().getMapPointGeoCoord().latitude;
	 				myLocation_y = arg0.getMapCenterPoint().getMapPointGeoCoord().longitude;
	 				findNearBusStation(myLocation_x,myLocation_y);		 	
	 				Toast toast = Toast.makeText(StartFinding.this,"원하는 장소의 마커를 선택해 주세요",Toast.LENGTH_LONG); toast.show();
	 			}
	 		};
	 		mHandler.sendEmptyMessageDelayed(1, 5000);
	}

	@Override
	public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		// Toast toast = Toast.makeText(this, "오래 클릭", Toast.LENGTH_SHORT);
		// toast.show();
	}

	@Override
	public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
		if(click==true){						
			if(selRadio == true){
				for(int i=0;i<20;i++){
					mapView.removePOIItem(poiItem[i]);
					mapView.removePOIItem(poiItem2[i]);
					}
				}
			else{
				for(int i=0;i<20;i++){
					mapView.removePOIItem(poiItem[i]);
					mapView.removePOIItem(poiItem2[i]);
					}  				
			}
			mapView.addPOIItem(poiItem_S); // 화면에 출력
			mapView.addPOIItem(poiItem_E); // 화면에 출력
			findNearBusStation(arg1.getMapPointGeoCoord().latitude, arg1.getMapPointGeoCoord().longitude);		
			if(notbus == false)
			{
				Toast toast = Toast.makeText(StartFinding.this,"원하는 장소의 마커를 선택해 주세요",Toast.LENGTH_LONG); toast.show();
			}
				
		}
	}

	@Override
	public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDaumMapOpenAPIKeyAuthenticationResult(MapView arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder rGeoCoder,
			String addressString) {

		/*
		 * String alertMessage = String.format("[%s]", addressString);
		 * AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		 * alertDialog.setTitle("출발 주소"); alertDialog.setMessage(alertMessage);
		 * alertDialog.setPositiveButton("OK", null); alertDialog.show();
		 */

	}

	@Override
	public void onReverseGeoCoderFailedToFindAddress(
			MapReverseGeoCoder rGeoCoder) {

	}
	
	public void findNearBusStation(double lat, double lon){
		// txt 파일 읽기 시작
		/*if(selRadio == true){
			for(int i=0;i<20;i++){
			mapView.removePOIItem(poiItem[i]);
			}
		}
		else{
			for(int i=0;i<20;i++){
			mapView.removePOIItem(poiItem2[i]);
			}
		}*/
		if(selRadio == true){
			mapView.removePOIItem(poiItem_S);
		}
		else{
			mapView.removePOIItem(poiItem_E);
		}
	

		

		// 읽은 파일에서 데이터 추출 시작
		// 정류장 이름, ID , 루트 ID 찾기
		int len = 0, S_len = 0, E_len = 0, out = 0;
		int t = 0, i = 0, j = 0;
		String StationName = "";
		int StationID=0;
		double temp_myX = 0, temp_myY = 0; // double형 좌표를 string 형으로 변환
		String Search_data_temp = strBuffer.toString().substring(0);

		while (true) {				
			//StationID = Integer.valueOf(Search_data_temp.substring(len,9)).intValue();
			len = 0;
			StationID = Integer.parseInt(Search_data_temp.substring(len,len+9));
			len = 10;
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			StationName = Search_data_temp.substring(len,j); 			
			//for (j = len; Search_data_temp.charAt(j) != '|'; j++)
			//	;
			len = j + 1 + 11;
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			temp_myX = Double.valueOf(Search_data_temp.substring(len, j))
					.doubleValue();
			len = j + 1;
			for (j = len; Search_data_temp.charAt(j) != '|'; j++)
				;
			temp_myY = Double.valueOf(Search_data_temp.substring(len, j))
					.doubleValue();
			len = j + 1;
			if ((lat + 0.003) > temp_myY
					&& temp_myY > (lat - 0.003)) {
				if ((lon + 0.0045) > temp_myX
						&& temp_myX > (lon - 0.0045)) {
					Selmaker(temp_myY,temp_myX,StationID,StationName,t);
					t++;
				}
			}
			for (j = len; Search_data_temp.charAt(j) != '^'; j++)
				;

			if (Search_data_temp.charAt(j + 1) == '*')
				break;
			else {
				len = j + 1;
				Search_data_temp = Search_data_temp.substring(len);
			}
		}

		if(t==0)
		{			
			Toast toast = Toast.makeText(StartFinding.this, "주변에 버스가 없습니다. 다른 교통수단을 이용해주세요.",Toast.LENGTH_LONG); toast.show();
			notbus = true;			
		}
		else 
			notbus = false;
		mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
		click=true;
	}
	
	public void Selmaker(double lat, double lon, int StationID, String StationName, int t)
	{			
		if(selRadio == true){
		S_StationID.add(t,StationID);
		Station_S.add(t,StationName);
		poiItem[t].setItemName(StationName);
		//poiItem[t].setTag(t);
		poiItem[t].setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon)); //좌표
		poiItem[t].setMarkerType(MapPOIItem.MarkerType.CustomImage); //마커를 사용자 이미지로 변환
		poiItem[t].setShowAnimationType(MapPOIItem.ShowAnimationType.DropFromHeaven); // 모르겠음
		poiItem[t].setShowCalloutBalloonOnTouch(true); //마커 터치하면 타이틀풍선이 뜸
		poiItem[t].setCustomImageResourceId(R.drawable.custom_poi_marker);// 사용자 지정 마커 호출
		poiItem[t].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2)); // 모르겠음
		mapView.addPOIItem(poiItem[t]); // 화면에 출력
		count_S = t; 
		}
		else{			
		E_StationID.add(t,StationID);
		Station_E.add(t,StationName);
		poiItem2[t].setItemName(StationName);
		//poiItem2[t].setTag(100+t);
		poiItem2[t].setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon)); //좌표
		poiItem2[t].setMarkerType(MapPOIItem.MarkerType.CustomImage); //마커를 사용자 이미지로 변환
		poiItem2[t].setShowAnimationType(MapPOIItem.ShowAnimationType.DropFromHeaven); // 모르겠음
		poiItem2[t].setShowCalloutBalloonOnTouch(true); //마커 터치하면 타이틀풍선이 뜸
		poiItem2[t].setCustomImageResourceId(R.drawable.custom_poi_marker_e);// 사용자 지정 마커 호출
		poiItem2[t].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2)); // 모르겠음
		mapView.addPOIItem(poiItem2[t]); // 화면에 출력
		count_E = t;  
		}		
	}
	
	public int FindBusInfo(String xml) {
		String tagName = "";
		int return_time =0;
		//ArrayList<String> Busname_1 = new ArrayList<String>();
		//ArrayList<String> Busname_2 = new ArrayList<String>();
		//ArrayList<String> Time_1 = new ArrayList<String>();
		//ArrayList<String> Time_2 = new ArrayList<String>();
		String Time1 = "";
		
		try {			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			XmlPullParser parser = factory.newPullParser(); 
			parser.setInput(new StringReader(xml));
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					break;
				case XmlPullParser.TEXT:	
					if (tagName.equals("predictTime1")) {
						Time1 = parser.getText();
					}
					if (tagName.equals("routeId")) {						
						if(parser.getText().equals(RouteID))
						{
							return_time =  Integer.parseInt(Time1);
							Log.e("위",Time1);
							return return_time;
						}
					}/*if (tagName.equals("plateNo1")) {
						Busname_1.add(n, parser.getText());
					}if (tagName.equals("plateNo2")) {
						Busname_2.add(n, parser.getText());
					}if (tagName.equals("predictTime1")) {
						Time_1.add(n, parser.getText());
					}if (tagName.equals("predictTime2")) {
						Time_2.add(n,parser.getText());
					}*/
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					break;
				}
				eventType = parser.next(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	void Outdata(int bus_distance)
	{
		TextView text_outdata,time_S,time_E,text_dis;
		
		text_outdata = (TextView)findViewById(R.id.textView1);		
		text_dis = (TextView)findViewById(R.id.text_dis);
		text_outdata.setText(LastOutput);
		long now = System.currentTimeMillis();
		SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
		String strNow = sdfNow.format(new Date(now));
		int tempTimeH = Integer.parseInt(strNow.substring(0,2));
		int tempTimeM = Integer.parseInt(strNow.substring(3));
		if(tempTimeM+Integer.parseInt(EndTime)>=60)
		{
			tempTimeH++;
			tempTimeM = tempTimeM+Integer.parseInt(EndTime)-60;
		}
		else
		{
			tempTimeM = tempTimeM + Integer.parseInt(EndTime);
		}
		text_dis.setText(Html.fromHtml("관련 검색어 " +  "<FONT color=" +"#00a2d5"+">" +"6개"+ "</FONT>"+ " 더보기" ));
		
		if(tempTimeH<10&&tempTimeM<10)
			text_dis.setText(Html.fromHtml("\0*이동거리 : "+  "<FONT color=" +"#FA0000"+">" +Integer.toString(bus_distance)+"m  "+ "</FONT>"+"\0*소요시간 : "+  "<FONT color=" +"#FA0000"+">" +EndTime+"분"+ "</FONT>"+"  *\0예상 도착시간 : " +  "<FONT color=" +"#FA0000"+">" +"0"+ tempTimeH +":"+ "0"+tempTimeM+ "</FONT>"));
		else if(tempTimeH>9&&tempTimeM<10){
			text_dis.setText(Html.fromHtml("\0*이동거리 : "+  "<FONT color=" +"#FA0000"+">" +Integer.toString(bus_distance)+"m  "+ "</FONT>"+"\0*소요시간 : "+  "<FONT color=" +"#FA0000"+">" +EndTime+"분"+ "</FONT>"+"  *\0예상 도착시간 : " +  "<FONT color=" +"#FA0000"+">" + tempTimeH +":"+ "0"+tempTimeM+ "</FONT>"));
		}
		else if(tempTimeH<10&&tempTimeM>9)
		{
			text_dis.setText(Html.fromHtml("\0*이동거리 : "+  "<FONT color=" +"#FA0000"+">" +Integer.toString(bus_distance)+"m  "+ "</FONT>"+"\0*소요시간 : "+  "<FONT color=" +"#FA0000"+">" +EndTime+"분"+ "</FONT>"+"  *\0예상 도착시간 : " +  "<FONT color=" +"#FA0000"+">" +"0"+ tempTimeH +":"+ tempTimeM+ "</FONT>"));
		}
		else
			text_dis.setText(Html.fromHtml("\0*이동거리 : "+  "<FONT color=" +"#FA0000"+">" +Integer.toString(bus_distance)+"m  "+ "</FONT>"+"\0*소요시간 : "+  "<FONT color=" +"#FA0000"+">" +EndTime+"분"+ "</FONT>"+"  *\0 예상 도착시간 : " +  "<FONT color=" +"#FA0000"+">" + tempTimeH +":"+ tempTimeM+ "</FONT>"));
		//text_dis.setText("  이동 거리 : "+Integer.toString(bus_distance)+"m  "+"현재 시간 : "+ strNow+"  예상 도착 시간 : "+ strNow);
	}
}