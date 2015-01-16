package com.abs192.codraw;

import java.util.ArrayList;

import com.abs192.codraw.DrawingView.DrawConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	// drawing path
	private Path drawPath;
	// drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	// initial color
	private int paintColor = 0xFF33B5E5;
	// canvas
	private Canvas drawCanvas;
	// canvas bitmap
	private Bitmap canvasBitmap;

	public static float height, width;

	ArrayList<Integer> listX, listY;

	private float strokeWidth = 18f;
	String room;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		listX = new ArrayList<Integer>();
		listY = new ArrayList<Integer>();
		setupDrawing();
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setDrawConfig(DrawConfig dc) {
		setPaintColor(dc.color);
		setStrokeWidth(dc.strokeWidth);
	}

	private void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		drawPaint.setColor(paintColor);
	}

	private void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		drawPaint.setStrokeWidth(strokeWidth);
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	private void setupDrawing() {
		// get drawing area setup for interaction
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(strokeWidth);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		height = getHeight();
		width = getWidth();
	}

	public void drawClick(float x, float y, DrawConfig temp) {
		// setDrawConfig(temp);
		// float radius = strokeWidth / 2 - (strokeWidth / 4);
		// drawCanvas.drawCircle(x, y, radius, drawPaint);
		// invalidate();
	}

	public void drawDrag(float px, float py, float cx, float cy, DrawConfig temp) {
		setDrawConfig(temp);
		Path drawPath2 = new Path();
		drawPath2.moveTo(px, py);
		drawPath2.lineTo(cx, cy);
		drawCanvas.drawPath(drawPath2, drawPaint);
		drawPath2.reset();
		invalidate();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		listX.add((int) touchX);
		listY.add((int) touchY);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(touchX, touchY);
			if (listX.size() == 50) {
				sendData(listX, listY);
				listX.clear();
				listY.clear();
			}
			break;
		case MotionEvent.ACTION_UP:
			drawCanvas.drawPath(drawPath, drawPaint);
			drawPath.reset();
			sendData(listX, listY);
			listX.clear();
			listY.clear();
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}

	private void sendData(ArrayList<Integer> listX, ArrayList<Integer> listY) {
		final ArrayList<Integer> lX = new ArrayList<Integer>(listX);
		final ArrayList<Integer> lY = new ArrayList<Integer>(listY);
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (lX.size() == 1) {
					int x = lX.get(0);
					int y = lY.get(0);
					Radio.getInstance().emitJustClick(room, x, y,
							(int) strokeWidth, paintColor, 'p');

				} else {
					if (lX.size() != 0 && lY.size() != 0) {

						for (int i = 0; i < lX.size() - 1; i++) {

							int x = lX.get(i);
							int y = lY.get(i);
							int X = lX.get(i + 1);
							int Y = lY.get(i + 1);

							System.out
									.println(x + "," + y + "  " + X + "," + Y);
							float pX = x / width;
							float pY = y / height;
							float cX = X / width;
							float cY = Y / height;
							double s = strokeWidth / (height * width);

							Radio.getInstance().emitDragClick(room, pX, pY, cX,
									cY, s, paintColor, 'p');
							// Radio.getInstance().emitJustClick(x, y,
							// (int) strokeWidth, paintColor, 'p');
						}

					}
				}

			}
		}).start();

	}

	public class DrawConfig {
		public int color;
		public float strokeWidth;

		public DrawConfig(int color, float f) {
			this.color = color;
			this.strokeWidth = f;
		}

	}

}