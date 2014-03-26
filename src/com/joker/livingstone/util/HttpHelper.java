package com.joker.livingstone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpHelper {
	public static String getString(String urlString){
		String resultData = "";
		try{
			URL url = new URL(urlString);
            //使用HttpURLConnection打开连接  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
            //得到读取的内容(流)  
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());  
            // 为输出创建BufferedReader  
            BufferedReader buffer = new BufferedReader(in);  
            String inputLine = null;  
            //使用循环来读取获得的数据  
            while (((inputLine = buffer.readLine()) != null)){  
                resultData += inputLine ;  
            }
            return resultData;
        }  
        catch (IOException e){  
            Log.e("HttpHelper", "IOException");  
        }
		return "";  
	}
}
