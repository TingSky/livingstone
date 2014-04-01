package com.joker.livingstone.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
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
	
	public static String getString(Context context ,String urlString , Map<String, String> params){
		String resultData = "";
		try{
			URL url = new URL(urlString);
            //ʹ��HttpURLConnection������  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(); 
            //�����Ƿ���connection�������Ϊ�����post���󣬲���Ҫ���� http�����ڣ������Ҫ��Ϊtrue
            urlConn.setDoOutput(true);
            //Post��ʽ����
            urlConn.setRequestMethod("POST");
            //���ñ������ӵ�Content-type������Ϊapplication/x-www-form-urlencoded����˼��������urlencoded�������form�������������ǿ��Կ������Ƕ���������ʹ��URLEncoder.encode���б���
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            //���ӣ���postUrl.openConnection()���˵����ñ���Ҫ��connect֮ǰ��ɣ� Ҫע�����connection.getOutputStream�������Ľ���connect��
            urlConn.connect();
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            String content = "";
            for (String item : params.keySet()) {
				content += item + "=" + params.get(item) + "&";
			}
            content += "imei=" + DeviceUtil.getImei(context);
            
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
	
	private static String urlBuilder(Context context ,String url){
		String imei = "imei=" + DeviceUtil.getImei(context);
		String userid = null;
		return userid;
	}
	
	
}
