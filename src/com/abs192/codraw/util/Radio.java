package com.abs192.codraw.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.DrawListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class Radio {

	private static Radio instance;
	public static boolean connected;
	private Socket socket;

	private Radio() {

	}

	public static Radio getInstance() {
		if (instance == null)
			instance = new Radio();
		return instance;
	}

	public void get(String url, Listener<String> rl, ErrorListener el) {
		if (checkConnection()) {
			StringRequest jsObjRequest = new StringRequest(Request.Method.GET,
					url, rl, el);
			CoDrawApplication.queue.add(jsObjRequest);
		}
	}

	public void postRoom(Listener<String> rl, ErrorListener el,
			final String... parameters) {
		if (checkConnection()) {

			StringRequest jsObjRequest = new StringRequest(Request.Method.POST,
					URLStore.POST_URL, rl, el) {
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("uname", parameters[0]);
					params.put("roomId", parameters[1]);
					return params;
				}
			};

			// Add the request to the RequestQueue.
			CoDrawApplication.queue.add(jsObjRequest);
		}
	}

	public void connect(final DrawListener dl) {
		try {
			socket = IO.socket(URLStore.BASEURL);
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					System.out.println("Connection established");
				}

			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {

					System.out.println("Connection disconnected");
				}

			}).on(Socket.EVENT_ERROR, new Emitter.Listener() {

				@Override
				public void call(Object... args) {

					System.out.println("Error");
				}

			}).on("drawClick", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {

						dl.drawClick(obj);
					}
				}

			}).on("drawDrag", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {
						dl.drawDrag(obj);
					}
				}

			}).on("status", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {
						dl.onStatus(obj);
					}
				}

			}).on("messageReceived", new Emitter.Listener() {

				@Override
				public void call(Object... args) {

					JSONObject obj = (JSONObject) args[0];
					if (obj != null) {
						dl.chatMessage(obj);
					}
				}
			});
			socket.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void emitJustClick(String room, float x, float y, int size,
			int color, char type) {

		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		if (socket != null) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("room", room);
				obj.put("x", x + "");
				obj.put("y", y + "");
				obj.put("penSize", size + "");
				obj.put("penColor", hexColor);
				obj.put("type", type + "");
				System.out.println(obj.toString());
				socket.emit("justClick", obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("socket null");
		}

	}

	public void emitDragClick(String room, double x, double y, double X,
			double Y, double size, int color, char type) {

		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		if (socket != null) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("room", room);
				obj.put("prevRatioX", x + "");
				obj.put("prevRatioY", y + "");
				obj.put("currRatioX", X + "");
				obj.put("currRatioY", Y + "");
				obj.put("penRatio", size + "");
				obj.put("penColor", hexColor);
				obj.put("type", type + "");

				System.out.println(obj.toString());
				socket.emit("dragDraw", obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("socket null");
		}

	}
	
	

	public void disconnect() {
		if (socket != null)
			socket.disconnect();
	}

	public void emitJoin(JSONObject a) {
		if (socket != null) {
			socket.emit("join", a);
		}
	}

	public static boolean checkConnection() {

		NetworkInfo info = ((ConnectivityManager) CoDrawApplication.applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}
}
