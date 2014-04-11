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
            //使用HttpURLConnection打开连接  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(); 
            //设置是否向connection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true
            urlConn.setDoOutput(true);
            //Post方式请求
            urlConn.setRequestMethod("POST");
            //配置本次连接的Content-type，配置为application/x-www-form-urlencoded的意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode进行编码
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
            //连接，从postUrl.openConnection()至此的配置必须要在connect之前完成， 要注意的是connection.getOutputStream会隐含的进行connect。
            urlConn.connect();
//            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            Writer out = new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8");
            for (String item : params.keySet()) {
				content += item + "=" + params.get(item) + "&";
			}
            content = urlBuilder(context, content);
            //DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
            out.write(content); 
            out.flush();
            out.close(); 
            //得到读取的内容(流)  
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());  
            // 为输出创建BufferedReader  
            BufferedReader buffer = new BufferedReader(in);  
            String inputLine = null;  
            //使用循环来读取获得的数据  
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
