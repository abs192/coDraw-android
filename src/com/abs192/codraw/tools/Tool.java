package com.abs192.codraw.tools;

import android.graphics.PointF;

public abstract class Tool {

	public enum ToolType {
		PEN, BRUSH, ERASER, CIRCLE, RECTANGLE
	};

	ToolType toolType;

	public ToolType getToolType() {
		return toolType;
	}

	public void setPenType(ToolType toolType) {
		this.toolType = toolType;
	}

	public abstract void init(int color, int strokeWidth);

	public abstract boolean onTouchDown(PointF p);

	public abstract boolean onTouchMove(PointF p);

	public abstract boolean onTouchUp(PointF p);

	public abstract int getResourceID();
}
