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
            //ʹ��HttpURLConnection������  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
            //�õ���ȡ������(��)  
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());  
            // Ϊ�������BufferedReader  
            BufferedReader buffer = new BufferedReader(in);  
            String inputLine = null;  
            //ʹ��ѭ������ȡ��õ�����  
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
