package com.abs192.codraw;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.abs192.codraw.adapters.ChatMessage;
import com.abs192.codraw.game.State;
import com.abs192.codraw.tools.Tool;
import com.abs192.codraw.ui.DrawingView;
import com.abs192.codraw.ui.DrawingView.PaintandStroke;
import com.abs192.codraw.util.HttpsTrustManager;
import com.abs192.codraw.util.Radio;
import com.abs192.codraw.util.Store;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CoDrawApplication extends Application {

	public static final String TAG = "coDraw";
	public static Context applicationContext;
	public static float density = 1;

	public static RequestQueue queue;
	public static int currentState;
	public static String currentStateName;
	public static DrawingView drawingView;
	public static Tool currentTool;
	public static Point displaySize = new Point();
	public static int unreadChatCount;
	public static boolean connected;
	public static String uname, room;
	public static PaintandStroke paintAndStroke;
	public static ArrayList<ChatMessage> chatList;

	@Override
	public void onCreate() {
		super.onCreate();

		applicationContext = this;
		connected = Radio.checkConnection();
		density = getResources().getDisplayMetrics().density;
		HttpsTrustManager.allowAllSSL();
		queue = Volley.newRequestQueue(applicationContext);
		currentState = Store.getCurrentState();
		updateCurrentState(currentState);
		checkDisplaySize();

		// initialize chatList.. from Store
	}

	public static void updateCurrentState(int state) {
		currentState = state;
		Store.setCurrentState(state);
		if (state == State.NONE)
			currentStateName = "coDraw";
		else if (state == State.OFFLINE)
			currentStateName = "Offline";
		else
			currentStateName = "coDraw";

	}

	public static int dp(float value) {
		return (int) Math.ceil(density * value);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void checkDisplaySize() {
		try {
			WindowManager manager = (WindowManager) applicationContext
					.getSystemService(Context.WINDOW_SERVICE);
			if (manager != null) {
				Display display = manager.getDefaultDisplay();
				if (display != null) {
					if (android.os.Build.VERSION.SDK_INT < 13) {
						displaySize
								.set(display.getWidth(), display.getHeight());
					} else {
						display.getSize(displaySize);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public static void refreshChatCounter(TextView chatCounter) {
		if (chatCounter != null)
			if (CoDrawApplication.unreadChatCount > 0) {
				chatCounter.setVisibility(View.VISIBLE);
				chatCounter.setText("" + CoDrawApplication.unreadChatCount);
			} else
				chatCounter.setVisibility(View.GONE);
	}

	public static boolean isOffline() {
		return currentState == State.OFFLINE;
	}

	public static boolean isJoined() {
		return currentState == State.JOINED;
	}

}
