package com.abs192.codraw;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.abs192.codraw.util.ConnectionUpdateReceiver;
import com.abs192.codraw.util.URLStore;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
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

	public void get(Context context, String url, Listener<String> rl,
			ErrorListener el) {
		
		if (ConnectionUpdateReceiver.checkConnection(context)) {
		
			RequestQueue queue = Volley.newRequestQueue(context);
			StringRequest jsObjRequest = new StringRequest(Request.Method.GET,
					url, rl, el);
			// Add the request to the RequestQueue.
			queue.add(jsObjRequest);
		}
	}

	public void postRoom(Context context, Listener<String> rl,
			ErrorListener el, final String... parameters) {
		if (ConnectionUpdateReceiver.checkConnection(context)) {

			RequestQueue queue = Volley.newRequestQueue(context);

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
			queue.add(jsObjRequest);
		}
	}

	public void connect(final DrawListener dl) {
		try {
			socket = IO.socket(URLStore.BASEURL);
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					System.out.println("Connection established");
					System.out
							.println("BOOOOOOOOOOOOOM BAEEEEEEEEEEEEEEEEEEEEEE");
				}

			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
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

			});
			socket.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void emitJustClick(int x, int y, int size, int color, char type) {

		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		if (socket != null) {
			JSONObject obj = new JSONObject();
			try {
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

	public void emitDragClick(double x, double y, double X, double Y,
			double size, int color, char type) {

		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		if (socket != null) {
			JSONObject obj = new JSONObject();
			try {
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
}
