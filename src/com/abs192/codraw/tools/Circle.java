package com.abs192.codraw.tools;

import static com.abs192.codraw.CoDrawApplication.drawingView;
import android.graphics.Path;
import android.graphics.PointF;

import com.abs192.codraw.R;

public class Circle extends Tool {

	private Float cX, cY;

	@Override
	public void init(int color, int strokeWidth) {

	}

	@Override
	public boolean onTouchDown(PointF p) {
		if (p != null) {
			cX = p.x;
			cY = p.y;
			drawingView.drawPath.moveTo(cX, cY);
			drawingView.invalidate();
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchMove(PointF p) {
		if (p != null) {
			drawingView.drawPath.reset();
			drawingView.drawPath.addCircle(cX, cY, (float) getRadius(p.x, p.y),
					Path.Direction.CW);
			drawingView.invalidate();
			return true;
		}
		return false;

	}

	@Override
	public boolean onTouchUp(PointF p) {
		if (p != null) {
			drawingView.drawCanvas.drawPath(drawingView.drawPath,
					drawingView.drawPaint);
			drawingView.drawPath.reset();
			drawingView.invalidate();
			return true;
		}

		return false;
	}

	private double getRadius(float touchX, float touchY) {
		return Math.sqrt((touchX - cX) * (touchX - cX) + (touchY - cY)
				* (touchY - cY));
	}

	@Override
	public int getResourceID() {
		return R.drawable.icon_menu_ellipse;
	}
}
