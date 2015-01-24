package com.abs192.codraw.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.game.Player;
import com.abs192.codraw.game.State;

public class Store {

	static SharedPreferences sp;

	static {
		sp = PreferenceManager
				.getDefaultSharedPreferences(CoDrawApplication.applicationContext);
	}

	public static void setUsername(String uname) {
		sp.edit().putString("uname", uname).commit();
	}

	public static String getUsername() {
		return sp.getString("uname", "");
	}

	public static String getRoomId() {
		return sp.getString("roomID", "");
	}

	public static void setRoomId(String roomId) {
		sp.edit().putString("roomID", roomId).commit();
	}

	public static void setGameStatus(boolean b) {
		sp.edit().putBoolean("gamestatus", b).commit();
	}

	public static boolean getGameStatus() {
		return sp.getBoolean("gamestatus", false);
	}

	public static void setPlayer(Player p) {
		overwriteObject("player", p);
	}

	public static Player getPlayer() {
		return (Player) readObject("player");
	}

	private static void overwriteObject(String name, Object obj) {
		try {
			FileOutputStream fos = CoDrawApplication.applicationContext
					.openFileOutput(name, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
		} catch (FileNotFoundException f) {
			Toast.makeText(CoDrawApplication.applicationContext,
					"File Not Found Exception", Toast.LENGTH_SHORT).show();
			f.printStackTrace();

		} catch (IOException e) {
			Toast.makeText(CoDrawApplication.applicationContext,
					"IO Exception", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	private static Object readObject(String name) {

		Object obj = null;
		FileInputStream fis;
		ObjectInputStream ois;
		try {
			fis = CoDrawApplication.applicationContext.openFileInput(name);
			ois = new ObjectInputStream(fis);
			try {
				obj = ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (StreamCorruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return obj;
	}

	public static void setCurrentState(int s) {
		sp.edit().putInt("state", s).commit();
	}

	public static int getCurrentState() {
		return sp.getInt("state", State.NONE);
	}

}
