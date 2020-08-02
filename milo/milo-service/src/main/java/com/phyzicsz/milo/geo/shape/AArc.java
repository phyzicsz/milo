package com.phyzicsz.milo.geo.shape;

public abstract class AArc extends APivot {
	protected double leftAzimuthDegrees, rightAzimuthDegrees;
	
	public void setRightAzimuthDegrees(double rightAzimuthDegrees) {
		this.rightAzimuthDegrees = rightAzimuthDegrees;
		shapeChanged();
	}
	
	public void setLeftAzimuthDegrees(double leftAzimuthDegrees) {
		this.leftAzimuthDegrees = leftAzimuthDegrees;
		shapeChanged();
	}
}
