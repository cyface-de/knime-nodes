/*******************************************************************************
 * Copyright (c) 2016 German Federal Institute for Risk Assessment (BfR)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.knime.esri;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class EsriUtilsTests {

	@Test
	public void testGetSimpleGeometries() {
		GeometryFactory f = new GeometryFactory();

		Polygon p1 = f.createPolygon(new Coordinate[] { new Coordinate(0, 0), new Coordinate(10, 0),
				new Coordinate(0, 10), new Coordinate(0, 0) });
		Polygon p2 = f.createPolygon(new Coordinate[] { new Coordinate(100, 100), new Coordinate(110, 100),
				new Coordinate(100, 110), new Coordinate(100, 100) });
		MultiPolygon mp = f.createMultiPolygon(new Polygon[] { p1, p2 });

		assertEquals(2, EsriUtils.getSimpleGeometries(mp, false).size());

		LinearRing outer = f.createLinearRing(new Coordinate[] { new Coordinate(0, 0), new Coordinate(10, 0),
				new Coordinate(10, 10), new Coordinate(0, 10), new Coordinate(0, 0) });
		LinearRing inner = f.createLinearRing(new Coordinate[] { new Coordinate(2, 2), new Coordinate(8, 2),
				new Coordinate(8, 8), new Coordinate(2, 8), new Coordinate(2, 2) });
		Polygon p = f.createPolygon(outer, new LinearRing[] { inner });

		assertEquals(1, EsriUtils.getSimpleGeometries(p, false).size());
		assertEquals(2, EsriUtils.getSimpleGeometries(p, true).size());
	}

	@Test
	public void testCreatePolygon() {
		List<Coordinate> coordinates = new ArrayList<>();

		coordinates.add(new Coordinate(0, 0));
		coordinates.add(new Coordinate(10, 0));
		coordinates.add(new Coordinate(10, 10));
		coordinates.add(new Coordinate(0, 10));
		coordinates.add(new Coordinate(0, 0));
		coordinates.add(new Coordinate(2, 2));
		coordinates.add(new Coordinate(8, 2));
		coordinates.add(new Coordinate(8, 8));
		coordinates.add(new Coordinate(2, 8));
		coordinates.add(new Coordinate(2, 2));

		assertEquals(1, EsriUtils.createPolygon(coordinates).getNumInteriorRing());
	}
}
