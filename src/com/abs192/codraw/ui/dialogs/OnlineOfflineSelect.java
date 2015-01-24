package com.abs192.codraw.ui.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.abs192.codraw.MainActivity;
import com.abs192.codraw.R;

public class OnlineOfflineSelect extends AlertDialog.Builder {

	MainActivity act;

	public OnlineOfflineSelect(MainActivity act) {
		super(act);
		this.act = act;
	}

	public void showDialog() {

		LayoutInflater factory = LayoutInflater.from(act);
		View e = factory.inflate(R.layout.dialog_online_offline, null);
		setView(e);

		final TextView tvOn = (TextView) e.findViewById(R.id.selectOnline);
		final TextView tvOff = (TextView) e.findViewById(R.id.selectOffline);
		tvOn.setClickable(true);
		tvOff.setClickable(true);

		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				act.showDialogOnlineOffline(false);
			}
		});

		tvOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				act.showDialogOnlineOffline(true);
			}
		});

		tvOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				act.showDialogOnlineOffline(false);
			}
		});

		act.ad = create();
		act.ad.show();
	}
}
