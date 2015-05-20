package com.example.findway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownHtml {
	public String DownloadHtml(String addr)
	{	
		StringBuilder html = new StringBuilder();
		try{
			//URL url = new URL("http://openapi.seoul.go.kr:8088/sample/xml/SeoulLibraryTime/1/5/");
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if(conn != null)
			{
				conn.setConnectTimeout(5000);
				conn.setUseCaches(false);
				if( conn.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					for(;;)
					{
					    String line = br.readLine();
					    if(line == null) break;
					    html.append(line+'\n');
					}
					br.close();
				}
				conn.disconnect();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return html.toString();
	}
}

