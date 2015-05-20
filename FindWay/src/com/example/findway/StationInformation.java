package com.example.findway;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class StationInfo {
	public String StationName, Distance;
	public int img;
	public StationInfo(String StationName, String Distance)
	{
		this.StationName = StationName;
		this.Distance = Distance;
		
	}
}

public class StationInformation extends BaseAdapter{
	ArrayList<StationInfo> stationinfo;
	LayoutInflater inflater;
	Context con;
	
	public StationInformation(Context con, ArrayList<String> list){
		this.con = con;
		stationinfo = new ArrayList<StationInfo>();
		
		//Log.d("lueseypid", ""+list.size());
		for(int i=0; i<list.size(); i++) {
			stationinfo.add(new StationInfo(list.get(i), null));
		}
		
		
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stationinfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return stationinfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.station_lv, parent, false);
		}
		
		//StationInfo info = stationinfo.get(position);
		StationInfo info=(StationInfo)getItem(position);
		StartFinding startfinding = new StartFinding();
		TextView stationname, distance;
		//ImageView img;
		stationname = (TextView) convertView.findViewById(R.id.stationname1);
		distance = (TextView) convertView.findViewById(R.id.distance1);
		//img = (ImageView) convertView.findViewById(R.id.img);
		

		
		stationname.setText(info.StationName);
		distance.setText(startfinding.OutputData.get(position));
		//img.setImageDrawable(convertView.getResources().getDrawable(info.img));
				
		SpannableStringBuilder sp = new SpannableStringBuilder(distance.getText());
		SpannableStringBuilder sp2 = new SpannableStringBuilder(stationname.getText());
		
		sp.setSpan(new ForegroundColorSpan(Color.rgb(255, 255, 255)), 0, sp.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp2.setSpan(new ForegroundColorSpan(Color.rgb(255, 255, 255)), 0, sp2.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		
		//sp.setSpan(new ForegroundColorSpan(Color.rgb(0, 0, 255)), 0, sp.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//sp.setSpan(new AbsoluteSizeSpan(12), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		distance.append(sp);
		stationname.append(sp2);
				
		
		stationname.setText(sp2);
		distance.setText(sp);
		
		return convertView;
		
	}
	
	public void addItem(String station, String dist){
		stationinfo.add(new StationInfo(station, dist));
		notifyDataSetChanged();
	}
	
}
