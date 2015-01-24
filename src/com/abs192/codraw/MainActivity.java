package com.abs192.codraw;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.abs192.codraw.adapters.ChatListAdapter;
import com.abs192.codraw.game.State;
import com.abs192.codraw.tools.Circle;
import com.abs192.codraw.tools.NetworkTool;
import com.abs192.codraw.tools.Pen;
import com.abs192.codraw.tools.Tool;
import com.abs192.codraw.ui.DrawingView;
import com.abs192.codraw.ui.dialogs.ColorPickerDialog;
import com.abs192.codraw.ui.dialogs.OnlineOfflineSelect;
import com.abs192.codraw.ui.dialogs.RoomLogin;
import com.abs192.codraw.ui.dialogs.StrokeWidthPickerDialog;
import com.abs192.codraw.util.NetworkStateReceiver;
import com.abs192.codraw.util.NetworkStateReceiver.NetworkStateReceiverListener;
import com.abs192.codraw.util.ChatHandler;
import com.abs192.codraw.util.Radio;
import com.abs192.codraw.util.Utilities;

public class MainActivity extends ActionBarActivity implements
		NetworkStateReceiverListener {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mLeftDrawerView, mRightDrawerView;

	public AlertDialog ad;
	public String room, uname;
	private TextView chatCounter; // status;
	private NetworkStateReceiver networkStateReceiver;
	private LinearLayout toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		CoDrawApplication.drawingView = (DrawingView) findViewById(R.id.drawing);
		CoDrawApplication.drawingView.setVisibility(View.GONE);

		toolbar = (LinearLayout) findViewById(R.id.toolbar);
		// status = (TextView) findViewById(R.id.textInternet);

		toolbar.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mDrawerLayout == null || mLeftDrawerView == null
				|| mRightDrawerView == null || mDrawerToggle == null) {
			// Configure navigation drawer
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mLeftDrawerView = (ListView) findViewById(R.id.left_drawer);
			mRightDrawerView = (ListView) findViewById(R.id.right_drawer);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.string.drawer_open, R.string.drawer_close) {

				public void onDrawerClosed(View drawerView) {
					if (drawerView.equals(mLeftDrawerView)) {
						getSupportActionBar().setTitle(
								CoDrawApplication.currentStateName);
						supportInvalidateOptionsMenu();
						mDrawerToggle.syncState();
					}
				}

				public void onDrawerOpened(View drawerView) {
					if (drawerView.equals(mLeftDrawerView)) {
						getSupportActionBar().setTitle(
								CoDrawApplication.currentStateName);
						supportInvalidateOptionsMenu();
						mDrawerToggle.syncState();
					}
				}

				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
				}

			};

			// mRightDrawerView.set
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}

		switch (CoDrawApplication.currentState) {
		case State.NONE:
			new OnlineOfflineSelect(this).showDialog();
			break;

		case State.OFFLINE:
			startDrawing("", "");
			break;

		case State.JOINING:
			new RoomLogin(this).showDialog();
			break;

		case State.JOINED:
			break;
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem mItem = menu.findItem(R.id.action_chat);

		if (CoDrawApplication.currentState != State.JOINED) {
			mItem.setVisible(true);
			View badgeLayout = null;
			if (mItem != null)
				badgeLayout = mItem.getActionView();
			if (badgeLayout != null) {
				chatCounter = (TextView) badgeLayout.findViewById(R.id.counter);
				badgeLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!mDrawerLayout.isDrawerOpen(mRightDrawerView))
							mDrawerLayout.openDrawer(mRightDrawerView);
						else
							mDrawerLayout.closeDrawer(mRightDrawerView);

						if (mDrawerLayout.isDrawerOpen(mLeftDrawerView))
							mDrawerLayout.closeDrawer(mLeftDrawerView);

					}
				});
			}
			CoDrawApplication.refreshChatCounter(chatCounter);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mDrawerToggle.onOptionsItemSelected(item);

			if (mDrawerLayout.isDrawerOpen(mRightDrawerView))
				mDrawerLayout.closeDrawer(mRightDrawerView);

			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	public void showDialogOnlineOffline(boolean b) {
		if (b) {
			// online selected from dialog
			ad.dismiss();
			CoDrawApplication.updateCurrentState(State.JOINING);
			new RoomLogin(this).showDialog();
		} else {
			// offline selected from dialog
			ad.dismiss();
			Utilities.toast(this, "Offline mode");
			CoDrawApplication.updateCurrentState(State.OFFLINE);
			startDrawing("", "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ad != null)
			ad.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CoDrawApplication.updateCurrentState(State.NONE);
		if (networkStateReceiver != null)
			unregisterReceiver(networkStateReceiver);
	}

	public void cancelRoomLogin() {
		CoDrawApplication.updateCurrentState(State.NONE);
		new OnlineOfflineSelect(this).showDialog();
	}

	public void startDrawing(String uname, String room) {
		CoDrawApplication.uname = uname;
		CoDrawApplication.room = room;
		if (!CoDrawApplication.isOffline())
			CoDrawApplication.currentStateName = "Room " + room;
		setTitle(CoDrawApplication.currentStateName);
		if (ad != null)
			ad.dismiss();
		initPaper();
	}

	private void initPaper() {

		if (CoDrawApplication.isJoined()) {
			Radio.getInstance().connect(new NetworkTool(this));
		}
		new ChatHandler(this);
		mRightDrawerView.setAdapter(new ChatListAdapter(this));

		CoDrawApplication.drawingView.setVisibility(View.VISIBLE);
		toolbar.setVisibility(View.VISIBLE);
		CoDrawApplication.paintAndStroke = CoDrawApplication.drawingView
				.getPaintAndStroke();

		if (CoDrawApplication.isJoined()) {
			networkStateReceiver = new NetworkStateReceiver();
			networkStateReceiver.addListener(this);
			registerReceiver(networkStateReceiver, new IntentFilter(
					android.net.ConnectivityManager.CONNECTIVITY_ACTION));
		}

		ImageButton im = (ImageButton) findViewById(R.id.buttonTool);
		im.setImageResource(CoDrawApplication.currentTool.getResourceID());
		im.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (CoDrawApplication.currentTool instanceof Pen)
					CoDrawApplication.currentTool = new Circle();
				else
					CoDrawApplication.currentTool = new Pen();
				((ImageButton) v)
						.setImageResource(CoDrawApplication.currentTool
								.getResourceID());
			}
		});

		final ImageButton imB = (ImageButton) findViewById(R.id.buttonColor);
		imB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ColorPickerDialog(MainActivity.this, imB).showDialog();
			}
		});

		final Button b = (Button) findViewById(R.id.buttonStrokeWidth);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new StrokeWidthPickerDialog(MainActivity.this, b).showDialog();
			}
		});

	}

	private void updateStatusBar(boolean b) {
		// Animation anim;
		// if (b) {
		// anim = AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.navout);
		// anim.setDuration(300);
		// // fade out and hide
		// status.setAnimation(anim);
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// status.setVisibility(View.GONE);
		// }
		// }, 300);
		//
		// } else {
		//
		// anim = AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.navin);
		// // fade in and show
		// anim.setDuration(300);
		// status.setAnimation(anim);
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// status.setVisibility(View.VISIBLE);
		// }
		// }, 300);
		// }
		//
	}

	public void switchTool(Tool mPreviousTool) {

	}

	@Override
	public void networkAvailable() {
		CoDrawApplication.connected = true;
		updateStatusBar(true);
	}

	@Override
	public void networkUnavailable() {
		CoDrawApplication.connected = false;
		updateStatusBar(false);
	}

}
