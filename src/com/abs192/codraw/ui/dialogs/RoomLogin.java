package com.abs192.codraw.ui.dialogs;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.MainActivity;
import com.abs192.codraw.R;
import com.abs192.codraw.ui.DotsProgressBar;
import com.abs192.codraw.util.Radio;
import com.abs192.codraw.util.Store;
import com.abs192.codraw.util.URLStore;
import com.abs192.codraw.util.Utilities;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class RoomLogin extends AlertDialog.Builder {

	MainActivity act;
	String room = "", uname = "";
	Button submit;

	public RoomLogin(MainActivity act) {
		super(act);
		this.act = act;
	}

	public void showDialog() {

		LayoutInflater factory = LayoutInflater.from(act);
		View e = factory.inflate(R.layout.dialog_room_enter, null);
		setView(e);

		final EditText etU = (EditText) e.findViewById(R.id.etUser);
		final EditText etR = (EditText) e.findViewById(R.id.etRoom);
		final TextView dots = (TextView) e.findViewById(R.id.dots);

		final DotsProgressBar dotsDrawabale = new DotsProgressBar();
		dots.setCompoundDrawablesWithIntrinsicBounds(dotsDrawabale, null, null,
				null);
		dots.setCompoundDrawablePadding(CoDrawApplication.dp(4));
		dotsOnOff(dotsDrawabale, false);

		String savedName = Store.getUsername();
		etU.setText(savedName);
		String savedRoom = Store.getRoomId();
		etR.setText(savedRoom);
		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				act.cancelRoomLogin();
			}
		});

		etU.addTextChangedListener(new TextWatcher() {

			private Timer timer = new Timer();
			private final long DELAY = 500; // in ms

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && s.toString() != null
						&& !s.toString().trim().equals("")) {
					uname = s.toString();
					timer.cancel();
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {

							new Thread() {
								public void run() {
									act.runOnUiThread(new Runnable() {
										public void run() {
											if (!room.trim().equals(""))
												dataChanged(dotsDrawabale);
										}
									});
								}
							}.start();

						}

					}, DELAY);

				}

			}
		});
		etR.addTextChangedListener(new TextWatcher() {

			private Timer timer = new Timer();
			private final long DELAY = 500; // in ms

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s != null && s.toString() != null
						&& !s.toString().trim().equals("")) {
					room = s.toString();
					timer.cancel();
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {

							new Thread() {
								public void run() {
									act.runOnUiThread(new Runnable() {
										public void run() {
											dataChanged(dotsDrawabale);
										}
									});
								}
							}.start();

						}

					}, DELAY);

				}

			}
		});

		submit = (Button) e.findViewById(R.id.buttonCreateRoom);
		checkUnR();
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String url = URLStore.USERCHECK_URL + "?username=" + uname
						+ "&room=" + room;
				dotsOnOff(dotsDrawabale, true);
				updateSubmitButton("", false);
				Radio.getInstance().get(url, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("usercheck 2 response " + response);
						dotsOnOff(dotsDrawabale, false);
						if (response.equals("-1")) {
							// room exists, username taken
							updateSubmitButton("Username taken in room" + room,
									false);
							return;
						} else if (response.equals("0")) {
							// no room, create one
							updateSubmitButton("Creating room", true);

						} else if (response.equals("1")) {
							// room exists, user free
							updateSubmitButton("Joining room", true);
						}

						Radio.getInstance().postRoom(new Listener<String>() {

							@Override
							public void onResponse(String response) {
								Utilities.toast(act, "Initializing room");
								act.startDrawing(uname, room);
							}

						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								updateSubmitButton("Creating", true);
							}

						}, uname, room);

					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						dotsOnOff(dotsDrawabale, false);
						updateSubmitButton("Error. Try again", true);
					}

				});
			}
		});

		act.ad = create();
		act.ad.show();
	}

	protected void dataChanged(final DotsProgressBar dotsDrawabale) {

		if (checkUnR()) {
			dotsOnOff(dotsDrawabale, true);
			System.out.println(" checking " + uname + " " + room);
			String url = URLStore.USERCHECK_URL + "?username=" + uname
					+ "&room=" + room;
			Radio.getInstance().get(url, new Listener<String>() {

				@Override
				public void onResponse(String response) {
					System.out.println("usercheck response " + response);
					dotsOnOff(dotsDrawabale, false);
					if (response.equals("-1")) {
						// room exists, username taken
						updateSubmitButton("Username taken in room" + room,
								false);

					} else if (response.equals("0")) {
						// no room, create one
						updateSubmitButton("Create room and join", true);

					} else if (response.equals("1")) {
						// room exists, user free
						updateSubmitButton("Join room", true);
					}

				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

					dotsOnOff(dotsDrawabale, false);
					updateSubmitButton("Error Checking. Submit anyway", true);
				}

			});
		}

	}

	private void dotsOnOff(DotsProgressBar dotsDrawabale, boolean b) {
		if (dotsDrawabale != null && b)
			dotsDrawabale.start();
		else
			dotsDrawabale.stop();
	}

	public boolean checkUnR() {

		if (room.trim().equals("") && uname.trim().equals("")) {
			updateSubmitButton("Enter details", false);
			return false;
		} else if (uname.trim().equals("")) {
			updateSubmitButton("Enter username", false);
			return false;
		} else if (uname.trim().equals("")) {
			updateSubmitButton("Enter room", false);
			return false;
		}
		updateSubmitButton("Checking", false);
		return true;
	}

	private void updateSubmitButton(String text, boolean b) {
		submit.setText(text);
		submit.setClickable(b);
	}
}
