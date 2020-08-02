package com.phyzicsz.milo.geo.shape;

import java.awt.Shape;

import com.phyzicsz.milo.geo.GeoPath;

public class Polygon extends APath {
	@Override
	protected Shape createShape() {
		GeoPath path = new GeoPath(maxDistanceMeters, flatnessDistanceMeters, limit);
		for (int i = 0; i < points.size(); i++) {
			if (i > 0) {
				path.lineTo(points.get(i));
			} else {
				path.moveTo(points.get(i));
			}
		}
		path.closePath();
		return path;
	}
}
