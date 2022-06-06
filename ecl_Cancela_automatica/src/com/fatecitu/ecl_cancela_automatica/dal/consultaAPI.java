package com.fatecitu.ecl_cancela_automatica.dal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class consultaAPI {
	
	private static String token="iP75MYyoNQ4bKkgDmPa3tThPCtAtQ0OB6xAQUy";
	
	public static String verificar(String tagID,boolean isEntrando) {
		
		String body = "";
		URL url;
		HttpURLConnection conex;
		JSONObject json;
		
		try {
			url = new URL("https://adacontrol.000webhostapp.com/api/v1/api.php?token="+token
	 						  +"&tagid="+tagID
	 						  +"&acao="+(isEntrando?"1":"0"));
			conex = (HttpURLConnection) url.openConnection();
			conex.setRequestMethod("GET");
			conex.connect();
			if(conex.getResponseCode() != 200) {
				return "not200";
			}else {		
				Scanner scanner = new Scanner(url.openStream());
				while(scanner.hasNextLine()) {
					body += scanner.nextLine();
				}
				scanner.close();
			}
			JSONParser jsnParser = new JSONParser();
			JSONObject data = (JSONObject) jsnParser.parse(body);
			JSONArray jsnArr = (JSONArray) data.get("Response");
			json = (JSONObject) jsnArr.get(0);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		return json.get("code").toString();
		
	}
	
}
