package com.phyzicsz.milo.geo.shape;

import java.awt.Shape;

import com.phyzicsz.milo.geo.GeoEllipse;

public class Circle extends APivot {
	@Override
	protected Shape createShape() {
		GeoEllipse e = new GeoEllipse(pivot, radiusMeters * 2, radiusMeters * 2, maxDistanceMeters,
				flatnessDistanceMeters, limit);
		return e;
	}
}
