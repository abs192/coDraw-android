package com.abs192.codraw.tools;

import static com.abs192.codraw.CoDrawApplication.drawingView;
import static com.abs192.codraw.CoDrawApplication.room;

import java.util.ArrayList;

import android.graphics.PointF;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.R;
import com.abs192.codraw.ui.DrawingView;
import com.abs192.codraw.util.Radio;

public class Pen extends Tool {

	private ArrayList<Float> listX, listY;

	public Pen() {
		listX = new ArrayList<Float>();
		listY = new ArrayList<Float>();
	}

	@Override
	public void init(int color, int strokeWidth) {
	}

	@Override
	public boolean onTouchDown(PointF p) {
		if (p != null) {
			listX.add(p.x);
			listY.add(p.y);
			drawingView.drawPath.moveTo(p.x, p.y);
			drawingView.invalidate();
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchMove(PointF p) {
		if (p != null) {
			listX.add(p.x);
			listY.add(p.y);
			if (listX.size() == 50) {
				sendData(listX, listY);
				listX.clear();
				listY.clear();
			}
			drawingView.drawPath.lineTo(p.x, p.y);
			drawingView.invalidate();
			return true;
		}
		return false;

	}

	@Override
	public boolean onTouchUp(PointF p) {
		if (p != null) {
			listX.add(p.x);
			listY.add(p.y);
			drawingView.drawCanvas.drawPath(drawingView.drawPath,
					drawingView.drawPaint);
			drawingView.drawPath.reset();
			sendData(listX, listY);
			listX.clear();
			listY.clear();
			drawingView.invalidate();
			return true;
		}
		return false;
	}

	private void sendData(ArrayList<Float> listX, ArrayList<Float> listY) {
		final ArrayList<Float> lX = new ArrayList<Float>(listX);
		final ArrayList<Float> lY = new ArrayList<Float>(listY);
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (lX.size() == 1) {
					Float x = lX.get(0);
					Float y = lY.get(0);

					Radio.getInstance().emitJustClick(CoDrawApplication.room,
							x, y,
							(int) CoDrawApplication.drawingView.strokeWidth,
							CoDrawApplication.drawingView.paintColor, 'p');

				} else {
					if (lX.size() != 0 && lY.size() != 0) {

						for (int i = 0; i < lX.size() - 1; i++) {

							float x = lX.get(i);
							float y = lY.get(i);
							float X = lX.get(i + 1);
							float Y = lY.get(i + 1);

							System.out
							.println(x + "," + y + "  " + X + "," + Y);
							float pX = x / DrawingView.width;
							float pY = y / DrawingView.height;
							float cX = X / DrawingView.width;
							float cY = Y / DrawingView.height;
							double s = CoDrawApplication.drawingView.strokeWidth
									/ (DrawingView.height * DrawingView.width);

							Radio.getInstance().emitDragClick(room, pX, pY, cX,
									cY, s,
									CoDrawApplication.drawingView.paintColor,
									'p');
							// Radio.getInstance().emitJustClick(x, y,
							// (int) strokeWidth, paintColor, 'p');
						}

					}
				}
			}
		}).start();
	}

	@Override
	public int getResourceID() {
		return R.drawable.icon_menu_brush;
	}

}
