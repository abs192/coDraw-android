package com.abs192.codraw.ui.dialogs;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.MainActivity;
import com.abs192.codraw.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.OpacityBar.OnOpacityChangedListener;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.SaturationBar.OnSaturationChangedListener;
import com.larswerkman.holocolorpicker.ValueBar;
import com.larswerkman.holocolorpicker.ValueBar.OnValueChangedListener;

public class ColorPickerDialog extends AlertDialog.Builder {

	private MainActivity act;
	String room = "", uname = "";
	private ImageButton im;

	public ColorPickerDialog(MainActivity act, ImageButton im) {
		super(act);
		this.act = act;
		this.im = im;
	}

	public void showDialog() {

		LayoutInflater factory = LayoutInflater.from(act);
		View e = factory.inflate(R.layout.color_picker, null);

		setView(e);

		ColorPicker picker = (ColorPicker) e.findViewById(R.id.picker);
		SVBar svBar = (SVBar) e.findViewById(R.id.svbar);
		OpacityBar opacityBar = (OpacityBar) e.findViewById(R.id.opacitybar);
		SaturationBar saturationBar = (SaturationBar) e
				.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) e.findViewById(R.id.valuebar);

		picker.addSVBar(svBar);
		picker.setColor(CoDrawApplication.drawingView.getPaintColor());
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
				if (im != null)
					im.setImageDrawable(new ColorDrawable(color));
				CoDrawApplication.drawingView.setPaintColor(color);
				CoDrawApplication.paintAndStroke = CoDrawApplication.drawingView
						.getPaintAndStroke();
				// b.setBackgroundColor(color);
				// sb.getProgressDrawable().setColorFilter(color, Mode.SRC_IN);
				// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				// sb.getThumb().setColorFilter(color, Mode.SRC_IN);
			}
		});

		// to turn of showing the old color
		picker.setShowOldCenterColor(false);

		// adding onChangeListeners to bars
		opacityBar.setOnOpacityChangedListener(new OnOpacityChangedListener() {

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
		act.ad = create();
		act.ad.show();
	}

	public void setInitialColor(int color) {

	}

}
