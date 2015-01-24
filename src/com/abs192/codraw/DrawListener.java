package com.abs192.codraw;

import org.json.JSONObject;

public interface DrawListener {

	public void onStatus(JSONObject a);

	public void drawClick(JSONObject a);

	public void drawDrag(JSONObject a);

	public void chatMessage(JSONObject a);
}
