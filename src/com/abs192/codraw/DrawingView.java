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

/**
 * @author Aditya
 *
 */
public class DrawingView extends View {

	public enum PEN_TYPE {
		PEN, BRUSH, ERASER, CIRCLE, RECTANGLE
	};

	PEN_TYPE penType = PEN_TYPE.PEN;
	int penTypeType;

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

	private boolean isConnected;
	private float cX;
	private float cY;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		listX = new ArrayList<Integer>();
		listY = new ArrayList<Integer>();
		setupDrawing();
	}

	public PEN_TYPE getPenType() {
		return penType;
	}

	public void setPenType(PEN_TYPE penType) {
		this.penType = penType;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getPaintColor() {
		return paintColor;
	}

	@Override
	public String toString() {
		return "DrawingView [penType=" + penType + ", penTypeType="
				+ penTypeType + "]";
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
		if (isConnected) {

			switch (penType) {

			case PEN: {

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

			case CIRCLE: {
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					cX = touchX;
					cY = touchY;
					System.out
							.println("circle center " + touchX + "," + touchY);
					drawPath.moveTo(touchX, touchY);
					break;
				case MotionEvent.ACTION_MOVE:
					System.out.println("circle move " + touchX + "," + touchY);
					drawPath.reset();
					drawPath.addCircle(cX, cY, (float) radius(touchX, touchY),
							Path.Direction.CW);
					break;

				case MotionEvent.ACTION_UP:
					drawPath.reset();
					drawPath.addCircle(cX, cY, (float) radius(touchX, touchY),
							Path.Direction.CW);
					break;
				}

				invalidate();
				return true;
			}
			}
		}
		return false;
	}

	private double radius(float touchX, float touchY) {
		return Math.sqrt((touchX - cX) * (touchX - cX) + (touchY - cY)
				* (touchY - cY));
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

	public static class CircleArea {
		int radius;
		int centerX;
		int centerY;

		CircleArea(int centerX, int centerY, int radius) {
			this.radius = radius;
			this.centerX = centerX;
			this.centerY = centerY;
		}

		@Override
		public String toString() {
			return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
		}
	}

	public void setCoDrawable(boolean isConnected) {
		this.isConnected = isConnected;
	}

}