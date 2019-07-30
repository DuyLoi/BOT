package com.tpb.bot.citad.util;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tpb.bot.citad.config.Config;

public class Util {

	public static void main(String[] args) {

	}

	public static JSONObject getFlowById(String flow) {
		if (flow == null)
			return null;

		@SuppressWarnings("unchecked")
		Iterator<Object> iterator = Config.getJsonConfig().iterator();

		JSONObject jsonObject = null;
		while (iterator.hasNext()) {
			jsonObject = (JSONObject) iterator.next();

			if (flow.equals(jsonObject.get("flowId"))) {
				break;
			}
		}

		return jsonObject;
	}

	public static JSONArray getStepsOfFlow(JSONObject flow) {

		if (flow == null)
			return null;

		JSONArray arr = null;
		try {
			arr = (JSONArray) flow.get("steps");
		} catch (Exception e) {
			System.out.println(e.toString());
		//	ScreeningJob.displayAndWriteLogError(e);
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getStep(JSONObject flow, String stepNo) {

		if (flow == null || stepNo == null)
			return null;

		JSONArray steps = getStepsOfFlow(flow);

		Iterator<Object> iterator = steps.iterator();

		JSONObject jsonObject = null;
		while (iterator.hasNext()) {
			jsonObject = (JSONObject) iterator.next();

			if (stepNo.equals(jsonObject.get("stepNo"))) {
				break;
			}
		}

		return jsonObject;
	}

	public static JSONArray getStepElements(JSONObject step) {
		if (step == null)
			return null;

		JSONArray arr = null;
		try {
			arr = (JSONArray) step.get("stepElements");
		} catch (Exception e) {
			System.out.println(e.toString());
		//	ScreeningJob.displayAndWriteLogError(e);
		}
		return arr;
	}

	public static String getValue(JSONObject obj, String key) {

		if (obj == null || key == null)
			return null;

		String val = null;
		try {
			val = (String) obj.get(key);
		} catch (Exception e) {
			System.out.println(e.toString());
		//	ScreeningJob.displayAndWriteLogError(e);
		}
		return val;

	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getStepElementByOrder(JSONObject step, String orderNo){
		
		if (step == null || orderNo == null)
			return null;

		JSONArray steps = getStepElements(step);

		Iterator<Object> iterator = steps.iterator();
		JSONObject jsonObject = null;
		while (iterator.hasNext()) {
			jsonObject = (JSONObject) iterator.next();

			if (orderNo.equals(jsonObject.get("order"))) {
				break;
			}
		}

		return jsonObject;
	}

}
