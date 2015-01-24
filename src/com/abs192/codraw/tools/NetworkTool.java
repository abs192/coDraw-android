package com.abs192.codraw.tools;

import static com.abs192.codraw.CoDrawApplication.drawingView;
import static com.abs192.codraw.CoDrawApplication.paintAndStroke;

import org.json.JSONObject;

import android.graphics.Color;

import com.abs192.codraw.DrawListener;
import com.abs192.codraw.MainActivity;
import com.abs192.codraw.ui.DrawingView;
import com.abs192.codraw.ui.DrawingView.PaintandStroke;
import com.abs192.codraw.util.Radio;

public class NetworkTool implements DrawListener {

	private MainActivity act;

	public NetworkTool(MainActivity act) {
		this.act = act;
	}

	@Override
	public void onStatus(JSONObject a) {
		Radio.getInstance().emitJoin(a);
	}

	@Override
	public void drawClick(JSONObject a) {

	}

	@Override
	public void drawDrag(final JSONObject a) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
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

					PaintandStroke ps = drawingView.new PaintandStroke(c,
							(int) s);
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
					drawingView.drawDrag(px, py, cx, cy, ps);

				} catch (Exception e) {

				}
				drawingView.setPaintAndStroke(paintAndStroke);
			}
		});
	}

	@Override
	public void chatMessage(JSONObject a) {

	}

}
