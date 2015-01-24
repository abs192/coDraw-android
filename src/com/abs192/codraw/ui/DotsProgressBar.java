package com.abs192.codraw.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;

import com.abs192.codraw.CoDrawApplication;

public class DotsProgressBar extends Drawable {
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float[] scales = new float[3];
	private float[] startTimes = new float[] { 0, 150, 300 };
	private float[] elapsedTimes = new float[] { 0, 0, 0 };
	private long lastUpdateTime = 0;
	private boolean started = false;
	private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

	public DotsProgressBar() {
		super();
		paint.setColor(0xff111111);
	}

	private void update() {
		long newTime = System.currentTimeMillis();
		long dt = newTime - lastUpdateTime;
		lastUpdateTime = newTime;
		if (dt > 50) {
			dt = 50;
		}

		for (int a = 0; a < 3; a++) {
			elapsedTimes[a] += dt;
			float timeSinceStart = elapsedTimes[a] - startTimes[a];
			if (timeSinceStart > 0) {
				if (timeSinceStart <= 320) {
					float diff = decelerateInterpolator
							.getInterpolation(timeSinceStart / 320.0f);
					scales[a] = 1.33f + diff;
				} else if (timeSinceStart <= 640) {
					float diff = decelerateInterpolator
							.getInterpolation((timeSinceStart - 320.0f) / 320.0f);
					scales[a] = 1.33f + (1 - diff);
				} else if (timeSinceStart >= 800) {
					elapsedTimes[a] = 0;
					startTimes[a] = 0;
					scales[a] = 1.33f;
				} else {
					scales[a] = 1.33f;
				}
			} else {
				scales[a] = 1.33f;
			}
		}

		invalidateSelf();
	}

	public void start() {
		lastUpdateTime = System.currentTimeMillis();
		started = true;
		invalidateSelf();
	}

	public void stop() {
		for (int a = 0; a < 3; a++) {
			elapsedTimes[a] = 0;
			scales[a] = 1.33f;
		}
		startTimes[0] = 0;
		startTimes[1] = 150;
		startTimes[2] = 300;
		started = false;
	}

	@Override
	public void draw(Canvas canvas) {
		int y = CoDrawApplication.dp(7);

		canvas.drawCircle(CoDrawApplication.dp(3), y, scales[0]
				* CoDrawApplication.density, paint);
		canvas.drawCircle(CoDrawApplication.dp(9), y, scales[1]
				* CoDrawApplication.density, paint);
		canvas.drawCircle(CoDrawApplication.dp(15), y, scales[2]
				* CoDrawApplication.density, paint);
		if (started) {
			update();
		}
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public int getIntrinsicWidth() {
		return CoDrawApplication.dp(18);
	}

	@Override
	public int getIntrinsicHeight() {
		return CoDrawApplication.dp(10);
	}
}
