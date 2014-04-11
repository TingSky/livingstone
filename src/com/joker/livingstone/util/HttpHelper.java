package com.joker.livingstone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
			Log.d("HttpHelper" , urlString);
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
            Log.d("url" , urlString);
            Log.d("result" ,resultData);
            return resultData;
        }  
        catch (IOException e){  
            Log.e("HttpHelper", "IOException");
            Log.d("url" , urlString);
            Log.d("result" ,resultData);
        }
		return "";  
	}
	
	public static String getString(Context context ,String urlString , Map<String, String> params){
		String resultData = "";
		String content = "";
		try{
			URL url = new URL(urlString);
            //ʹ��HttpURLConnection������  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(); 
            //�����Ƿ���connection�������Ϊ�����post���󣬲���Ҫ���� http�����ڣ������Ҫ��Ϊtrue
            urlConn.setDoOutput(true);
            //Post��ʽ����
            urlConn.setRequestMethod("POST");
            //���ñ������ӵ�Content-type������Ϊapplication/x-www-form-urlencoded����˼��������urlencoded�������form�������������ǿ��Կ������Ƕ���������ʹ��URLEncoder.encode���б���
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
            //���ӣ���postUrl.openConnection()���˵����ñ���Ҫ��connect֮ǰ��ɣ� Ҫע�����connection.getOutputStream�������Ľ���connect��
            urlConn.connect();
//            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            Writer out = new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8");
            for (String item : params.keySet()) {
				content += item + "=" + params.get(item) + "&";
			}
            content = urlBuilder(context, content);
            //DataOutputStream.writeBytes���ַ����е�16λ��unicode�ַ���8λ���ַ���ʽд��������
            out.write(content); 
            out.flush();
            out.close(); 
            //�õ���ȡ������(��)  
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());  
            // Ϊ�������BufferedReader  
            BufferedReader buffer = new BufferedReader(in);  
            String inputLine = null;  
            //ʹ��ѭ������ȡ��õ�����  
            while (((inputLine = buffer.readLine()) != null)){  
                resultData += inputLine ;  
            }
            Log.d("url" , urlString);
            Log.d("param" , content);
            Log.d("result" ,resultData);
            return resultData;
        }  
        catch (IOException e){  
            Log.e("HttpHelper", "IOException"); 
            Log.d("url" , urlString);
            Log.d("param" , content);
            Log.d("result" ,resultData);
        }
		return "";  
	}
	
	private static String urlBuilder(Context context ,String url){
		String imei = "imei=" + DeviceUtil.getImei(context);
		String userid = "userId=" + DeviceUtil.get(context, "USERID");
		return url + imei + "&" + userid;
	}
	
	
}
