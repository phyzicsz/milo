package com.phyzicsz.milo.geo.shape;

import com.phyzicsz.milo.geo.GeoPoint;

public abstract class APivot extends AExtrusion implements ICircle {
	protected GeoPoint pivot;
	protected double radiusMeters;
	
	public APivot() {
		pivot = new GeoPoint();
	}
	
	@Override
	public void setRadius(double radiusMeters) {
		this.radiusMeters = radiusMeters;
		shapeChanged();
	}
	
	@Override
	public void setPivot(GeoPoint pivot) {
		this.pivot = pivot;
		shapeChanged();
	}
}
