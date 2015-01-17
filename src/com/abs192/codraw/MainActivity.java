package com.abs192.codraw;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.abs192.codraw.DrawingView.DrawConfig;
import com.abs192.codraw.DrawingView.PEN_TYPE;
import com.abs192.codraw.game.Player;
import com.abs192.codraw.util.ConnectionUpdateReceiver;
import com.abs192.codraw.util.Store;
import com.abs192.codraw.util.URLStore;
import com.abs192.codraw.util.Utilities;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.OpacityBar.OnOpacityChangedListener;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.SaturationBar.OnSaturationChangedListener;
import com.larswerkman.holocolorpicker.ValueBar;
import com.larswerkman.holocolorpicker.ValueBar.OnValueChangedListener;

public class MainActivity extends Activity implements DrawListener {

	private ConnectionUpdateReceiver cur;
	boolean isInternetConnected;
	TextView status;
	String uname, room;
	ProgressBar pg;
	private Player me;
	private ArrayList<Player> enemies;

	private AlertDialog ad;
	int currentColor = 0xFF33B5E5;
	ImageButton b, bPens;
	LinearLayout toolbar;
	SeekBar sb;
	DrawingView dv;
	DrawConfig config;
	ArrayList<JSONObject> pointsToDraw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dv = (DrawingView) findViewById(R.id.drawing);
		toolbar = (LinearLayout) findViewById(R.id.toolbar);
		dv.setVisibility(View.INVISIBLE);
		toolbar.setVisibility(View.INVISIBLE);

		status = (TextView) findViewById(R.id.textInternet);
		pg = (ProgressBar) findViewById(R.id.progressBar);

		cur = new ConnectionUpdateReceiver(this);
		IntentFilter intfilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		intfilter.setPriority(Integer.MAX_VALUE);
		registerReceiver(cur, intfilter);
		isInternetConnected = ConnectionUpdateReceiver
				.checkConnection(getApplicationContext());

		if (!new Store(getApplicationContext()).getGameStatus()
				|| new Store(getApplicationContext()).getPlayer() == null)
			showDialog();
		else
			initPaper();

	}

	public void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View e = factory.inflate(R.layout.dialog_room_enter, null);
		builder.setView(e);

		final EditText etU = (EditText) e.findViewById(R.id.etUser);
		final EditText etR = (EditText) e.findViewById(R.id.etRoom);
		String savedName = new Store(this).getUsername();
		etU.setText(savedName);
		String savedRoom = new Store(this).getRoomId();
		etR.setText(savedRoom);

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// ...
				showDialog();
			}
		});

		Button b = (Button) e.findViewById(R.id.buttonCreateRoom);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				joinOrCreate(etU, etR, 1);
			}
		});

		ad = builder.create();
		ad.show();
	}

	protected void joinOrCreate(EditText etU, EditText etR, int i) {

		if (etU != null && etR != null) {
			room = etR.getText().toString();
			uname = etU.getText().toString();
			if (room == null || room.trim().equals("")) {
				Utilities.toast(this, "Room number field empty");
				return;
			}
			if (uname == null || uname.trim().equals("")) {
				Utilities.toast(this, "Username field empty");
				return;
			}
			// all clear
			registerAndPost(uname, room);
		}
	}

	private void registerAndPost(final String uname, final String room) {

		// for incorporating direct links to rooms later. separating global and
		// local values

		if (ConnectionUpdateReceiver.checkConnection(getApplicationContext())) {
			if (ad != null)
				ad.dismiss();

			pg.setVisibility(View.VISIBLE);
			String url = URLStore.USERCHECK_URL + "?username=" + uname
					+ "&room=" + room;
			Radio.getInstance().get(getApplicationContext(), url,
					new Listener<String>() {

						@Override
						public void onResponse(String response) {

							if (response.equals("1")) {

								Utilities.toast(MainActivity.this,
										"Joining room...");
								Radio.getInstance().postRoom(
										getApplicationContext(),
										new Listener<String>() {

											@Override
											public void onResponse(
													String response) {

												Utilities.toast(
														MainActivity.this,
														"Game on!");
												Store s = new Store(
														getApplicationContext());
												s.setRoomId(room);
												s.setUsername(uname);
												System.out.println(response);
												if (pg != null)
													pg.setVisibility(View.GONE);
												initPaper();
											}
										}, new ErrorListener() {

											@Override
											public void onErrorResponse(
													VolleyError error) {

												Utilities.toast(
														MainActivity.this,
														"Couldnt join room");
												System.out.println("error");
												System.out.println(error
														.toString());
												pg.setVisibility(View.GONE);
												showDialog();
											}
										}, uname, room);

							} else {

								pg.setVisibility(View.GONE);
								Utilities.toast(MainActivity.this,
										"User with same name already in room "
												+ room);
								showDialog();
							}
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

							System.out.println("user check error");
							pg.setVisibility(View.GONE);
							Utilities.toast(MainActivity.this, "Error joining "
									+ room);
							showDialog();

						}
					});

		} else {
			pg.setVisibility(View.GONE);
			Utilities.toast(this, "No internet connection");
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void initPaper() {

		dv.setVisibility(View.VISIBLE);
		toolbar.setVisibility(View.VISIBLE);
		updateDrawingTouchable(isInternetConnected);
		dv.setRoom(room);
		config = dv.new DrawConfig(currentColor, 18);
		dv.setDrawConfig(config);
		b = (ImageButton) findViewById(R.id.buttonColorPicker);
		sb = (SeekBar) findViewById(R.id.seekBarWidth);
		b.setBackgroundColor(currentColor);
		sb.getProgressDrawable().setColorFilter(currentColor, Mode.SRC_IN);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			sb.getThumb().setColorFilter(currentColor, Mode.SRC_IN);
		sb.setProgress((int) dv.getStrokeWidth() - 10);

		// lel

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				LayoutInflater factory = LayoutInflater
						.from(getApplicationContext());
				View e = factory.inflate(R.layout.color_picker, null);

				ColorPicker picker = (ColorPicker) e.findViewById(R.id.picker);
				SVBar svBar = (SVBar) e.findViewById(R.id.svbar);
				OpacityBar opacityBar = (OpacityBar) e
						.findViewById(R.id.opacitybar);
				SaturationBar saturationBar = (SaturationBar) e
						.findViewById(R.id.saturationbar);
				ValueBar valueBar = (ValueBar) e.findViewById(R.id.valuebar);

				picker.addSVBar(svBar);
				picker.setColor(currentColor);
				picker.addOpacityBar(opacityBar);
				picker.addSaturationBar(saturationBar);
				picker.addValueBar(valueBar);

				// To get the color
				picker.getColor();

				// To set the old selected color u can do it like this
				picker.setOldCenterColor(picker.getColor());
				// adds listener to the colorpicker which is implemented
				// in the activity
				picker.setOnColorChangedListener(new OnColorChangedListener() {

					@Override
					public void onColorChanged(int color) {
						currentColor = color;

						b.setBackgroundColor(currentColor);
						config.color = currentColor;
						dv.setDrawConfig(config);
						sb.getProgressDrawable().setColorFilter(currentColor,
								Mode.SRC_IN);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
							sb.getThumb().setColorFilter(currentColor,
									Mode.SRC_IN);
					}
				});

				// to turn of showing the old color
				picker.setShowOldCenterColor(false);

				// adding onChangeListeners to bars
				opacityBar
						.setOnOpacityChangedListener(new OnOpacityChangedListener() {

							@Override
							public void onOpacityChanged(int opacity) {
							}
						});
				valueBar.setOnValueChangedListener(new OnValueChangedListener() {

					@Override
					public void onValueChanged(int value) {

					}
				});

				saturationBar
						.setOnSaturationChangedListener(new OnSaturationChangedListener() {

							@Override
							public void onSaturationChanged(int saturation) {

							}
						});

				builder.setView(e);
				ad = builder.create();
				ad.show();

			}
		});

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				config.strokeWidth = progress + 10;
				dv.setDrawConfig(config);
			}
		});

		bPens = (ImageButton) findViewById(R.id.buttonPen);
		bPens.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPenType(PEN_TYPE.PEN);
			}
		});
		bPens = (ImageButton) findViewById(R.id.buttonBrush);
		bPens.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPenType(PEN_TYPE.BRUSH);
			}
		});
		bPens = (ImageButton) findViewById(R.id.buttonCircle);
		bPens.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPenType(PEN_TYPE.CIRCLE);
			}
		});

		setupMMO();
	}

	private void setupMMO() {
		Radio.getInstance().connect(this);
		pointsToDraw = new ArrayList<JSONObject>();
	}

	@Override
	public void drawClick(final JSONObject a) {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// System.out.println(a.toString());
		// try {
		// String color = a.getString("penColor");
		// int c = Color.BLACK;
		// try {
		// c = Color.parseColor(color);
		// } catch (Exception e) {
		// }
		//
		// String size = a.getString("penSize").trim();
		// int s = Integer.parseInt(size);
		// s += 10;
		// if (s > 50)
		// s = 50;
		//
		// DrawConfig temp = dv.new DrawConfig(c, s);
		// String X = a.getString("x").trim();
		// String Y = a.getString("y").trim();
		// int x = Integer.parseInt(X);
		// int y = Integer.parseInt(Y);
		// dv.drawClick(x, y, temp);
		// } catch (Exception e) {
		//
		// }
		// dv.setDrawConfig(config);
		// }
		// });
	}

	@Override
	public void drawDrag(final JSONObject a) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				System.out.println(a.toString());
				try {
					String color = a.getString("penColor");
					int c = Color.BLACK;
					try {
						c = Color.parseColor(color);
					} catch (Exception e) {
					}

					String size = a.getString("penRatio").trim();
					double s = Double.parseDouble(size)
							* (DrawingView.height * DrawingView.width);
					s += 10;
					if (s > 50)
						s = 50;

					DrawConfig temp = dv.new DrawConfig(c, (int) s);
					String cX = a.getString("currRatioX").trim();
					String cY = a.getString("currRatioY").trim();
					String pX = a.getString("prevRatioX").trim();
					String pY = a.getString("prevRatioY").trim();

					float cx = (Float.parseFloat(cX)) * DrawingView.width;
					float cy = (Float.parseFloat(cY)) * DrawingView.height;
					float px = (Float.parseFloat(pX)) * DrawingView.width;
					float py = (Float.parseFloat(pY)) * DrawingView.height;

					System.out.println(s + ". (" + cX + "," + cY + ") (" + pX
							+ "," + pY + ")");
					dv.drawDrag(px, py, cx, cy, temp);
				} catch (Exception e) {

				}
				dv.setDrawConfig(config);
			}
		});
	}

	public void setConnectionUpdate(boolean isConnected) {
		this.isInternetConnected = isConnected;
		updateStatusBar(isConnected);
		updateDrawingTouchable(isConnected);
	}

	private void updateDrawingTouchable(boolean isConnected) {
		if (dv != null)
			dv.setCoDrawable(isConnected);
	}

	private void setPenType(PEN_TYPE type) {
		if (dv != null) {
			dv.setPenType(type);
		}
	}

	private void updateStatusBar(boolean b) {
		Animation anim;
		if (b) {
			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.navout);
			anim.setDuration(300);
			// fade out and hide
			status.setAnimation(anim);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					status.setVisibility(View.GONE);
				}
			}, 300);

		} else {

			anim = AnimationUtils.loadAnimation(getApplicationContext(),
					R.anim.navin);
			// fade in and show
			anim.setDuration(300);
			status.setAnimation(anim);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					status.setVisibility(View.VISIBLE);
				}
			}, 300);
		}

	}

	@Override
	public void onStatus(JSONObject a) {
		System.out.println("Status received: " + a.toString());
		Radio.getInstance().emitJoin(a);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Radio.getInstance().disconnect();
		new Store(getApplicationContext()).setGameStatus(false);
		new Store(getApplicationContext()).setPlayer(null);
		unregisterReceiver(cur);
	}
}
