package com.phyzicsz.milo.geo.shape;

import java.awt.Shape;
import java.awt.geom.Area;

import com.phyzicsz.milo.geo.GeoBlock;
import com.phyzicsz.milo.geo.GeoEllipse;
import com.phyzicsz.milo.geo.GeoPoint;

public class Orbit extends APath {
	private double widthMeters;
	
	public void setWidth(double widthMeters) {
		this.widthMeters = widthMeters;
		shapeChanged();
	}
	
	@Override
	protected Shape createShape() {
		Area orbit = new Area();
		GeoPoint previousPoint = null;
		for (GeoPoint point : points) {
			GeoEllipse ellipse = new GeoEllipse(point, widthMeters, widthMeters, maxDistanceMeters,
					flatnessDistanceMeters, limit);
			orbit.add(new Area(ellipse));
			if (previousPoint != null) {
				// Draw rectangle connection
				GeoBlock block = new GeoBlock(previousPoint, point, widthMeters, maxDistanceMeters,
						flatnessDistanceMeters, limit);
				orbit.add(new Area(block));
			}
			previousPoint = point;
		}
		return orbit;
	}
}
