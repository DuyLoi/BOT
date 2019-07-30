package com.tpb.bot.citad.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unused")
public class Config {

	private static JSONArray jsonArrayConfig = null;
	private static Properties properties = null;

	public static void main(String[] args) {

//		loadConfig();
	}
	
	static {
		loadProperties();
		loadConfig();
	}

	public static void loadConfig() {
		JSONParser parser = new JSONParser();

		try {

			InputStream is = Config.class.getClassLoader().getResourceAsStream(
					"flow.json");
			Object obj = parser.parse(new InputStreamReader(is, "UTF-8"));

			jsonArrayConfig = (JSONArray) obj;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadProperties() {
		try {
			properties = new Properties();
			InputStream is = Config.class.getClassLoader().getResourceAsStream(
					"config.properties");
			properties.load(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static JSONArray getJsonConfig(){
		return jsonArrayConfig;
	}
	
	public static String getParam(String param){
		if(properties == null)
			return null;
		
		return properties.getProperty(param);
		
	}

}
