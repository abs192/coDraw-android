package com.abs192.codraw.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.MainActivity;
import com.abs192.codraw.R;

public class StrokeWidthPickerDialog extends AlertDialog.Builder {

	private MainActivity act;
	String room = "", uname = "";
	private Button b;

	public StrokeWidthPickerDialog(MainActivity act, Button b) {
		super(act);
		this.act = act;
		this.b = b;
	}

	@SuppressLint("NewApi")
	public void showDialog() {

		LayoutInflater factory = LayoutInflater.from(act);
		View e = factory.inflate(R.layout.stroke_width_picker, null);
		setView(e);
		SeekBar sb = (SeekBar) e.findViewById(R.id.seekBarStrokeWidth);
		sb.getProgressDrawable().setColorFilter(
				CoDrawApplication.drawingView.paintColor, Mode.SRC_IN);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			sb.getThumb().setColorFilter(
					CoDrawApplication.drawingView.paintColor, Mode.SRC_IN);
		int curr = ((int) CoDrawApplication.drawingView.getStrokeWidth()) - 1;
		sb.setProgress(curr);
		if (b != null)
			b.setText("" + (curr + 1));

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
				CoDrawApplication.drawingView.setStrokeWidth(progress + 1);
				if (b != null)
					b.setText("" + (progress + 1));
				CoDrawApplication.paintAndStroke = CoDrawApplication.drawingView
						.getPaintAndStroke();
			}
		});

		act.ad = create();
		act.ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams wmlp = act.ad.getWindow().getAttributes();
		wmlp.gravity = Gravity.BOTTOM;
		// wmlp.x = 20; // x position
		// wmlp.y = CoDrawApplication.displaySize.y + 50; // y position
		act.ad.show();
	}

	public void setInitialColor(int color) {

	}

}
