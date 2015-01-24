package com.abs192.codraw.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.tools.Pen;

/**
 * @author Aditya
 *
 */
public class DrawingView extends View {

	public Path drawPath;
	public Paint drawPaint, canvasPaint;

	public Canvas drawCanvas;
	public Bitmap canvasBitmap;

	public static float height, width;

	public float strokeWidth = 15f;
	public int paintColor = 0xFF33B5E5;
	public float tempStrokeWidth = 18f;
	public int tempPaintColor = 0xFF33B5E5;

	String room;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		drawPaint.setColor(paintColor);
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		drawPaint.setStrokeWidth(strokeWidth);
	}

	public void setPaintAndStroke(PaintandStroke ps) {
		setPaintColor(ps.color);
		setStrokeWidth(ps.strokeWidth);
	}

	public PaintandStroke getPaintAndStroke() {
		return new PaintandStroke(paintColor, strokeWidth);
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	private void setupDrawing() {
		// get drawing area setup for interaction
		CoDrawApplication.currentTool = new Pen();
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

	public void drawDrag(float px, float py, float cx, float cy,
			PaintandStroke ps) {

		setPaintAndStroke(ps);
		Path drawPath2 = new Path();
		drawPath2.moveTo(px, py);
		drawPath2.lineTo(cx, cy);
		drawCanvas.drawPath(drawPath2, drawPaint);
		drawPath2.reset();
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (CoDrawApplication.isOffline()
		// || (CoDrawApplication.isJoined() && CoDrawApplication.connected)) {
		float touchX = event.getX();
		float touchY = event.getY();
		PointF p = new PointF(touchX, touchY);
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			return CoDrawApplication.currentTool.onTouchDown(p);
		case MotionEvent.ACTION_MOVE:
			return CoDrawApplication.currentTool.onTouchMove(p);
		case MotionEvent.ACTION_UP:
			return CoDrawApplication.currentTool.onTouchUp(p);
		default:
			return false;
		}
		// }
		// return false;
	}

	public class PaintandStroke {
		public int color;
		public float strokeWidth;

		public PaintandStroke(int color, float f) {
			this.color = color;
			this.strokeWidth = f;
		}

	}

}