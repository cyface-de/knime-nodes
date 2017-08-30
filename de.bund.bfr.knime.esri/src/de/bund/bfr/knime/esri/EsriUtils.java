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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.referencing.CRS;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.util.FileUtil;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class EsriUtils {

	public static final ImmutableList<String> CHARSETS = ImmutableList.of(StandardCharsets.UTF_8.name(),
			StandardCharsets.ISO_8859_1.name(), StandardCharsets.US_ASCII.name(), StandardCharsets.UTF_16.name(),
			StandardCharsets.UTF_16BE.name(), StandardCharsets.UTF_16LE.name());

	public static final GeometryFactory GEO_FACTORY = new GeometryFactory();

	private EsriUtils() {
	}

	public static void checkColumn(String name, SettingsModelString column, DataTableSpec spec)
			throws InvalidSettingsException {
		if (Strings.isNullOrEmpty(column.getStringValue())) {
			throw new InvalidSettingsException("No " + name + " column selected");
		}

		if (!spec.containsName(column.getStringValue())) {
			throw new InvalidSettingsException("No such column \"" + column.getStringValue() + "\"");
		}
	}

	public static List<Geometry> getSimpleGeometries(Geometry geometry, boolean removeHoles) {
		List<Geometry> simpleGeometries = new ArrayList<>();

		for (Geometry g : geometry instanceof GeometryCollection ? getGeometries((GeometryCollection) geometry)
				: Arrays.asList(geometry)) {
			if (g instanceof Polygon) {
				Polygon p = adjustOrientation((Polygon) g);

				if (removeHoles) {
					simpleGeometries.addAll(splitToRemoveHoles(p));
				} else {
					simpleGeometries.add(p);
				}
			} else {
				simpleGeometries.add(g);
			}
		}

		return simpleGeometries;
	}

	public static Polygon createPolygon(List<Coordinate> coordinates) {
		Deque<Coordinate> remaining = new LinkedList<>(coordinates);
		List<Coordinate> exterior = new ArrayList<>();

		exterior.add(remaining.removeFirst());

		while (!remaining.isEmpty()) {
			Coordinate c = remaining.removeFirst();

			exterior.add(c);

			if (c.equals2D(exterior.get(0)) && containsAllCoordinates(exterior, remaining)) {
				break;
			}
		}

		LinearRing shell = GEO_FACTORY.createLinearRing(exterior.toArray(new Coordinate[0]));
		List<LinearRing> holes = new ArrayList<>();

		while (!remaining.isEmpty()) {
			List<Coordinate> interior = new ArrayList<>();

			interior.add(remaining.removeFirst());

			while (!remaining.isEmpty()) {
				Coordinate c = remaining.removeFirst();

				interior.add(c);

				if (c.equals2D(interior.get(0))) {
					holes.add(GEO_FACTORY.createLinearRing(interior.toArray(new Coordinate[0])));
					break;
				}
			}
		}

		return GEO_FACTORY.createPolygon(shell, holes.toArray(new LinearRing[0]));
	}

	public static ShapefileDataStore getDataStore(String shpFile, String charset)
			throws InvalidPathException, IOException, UnsupportedCharsetException {
		ShapefileDataStore store = new ShapefileDataStore(EsriUtils.getFile(shpFile).toURI().toURL());

		if (charset != null) {
			store.setCharset(Charset.forName(charset));
		}

		return store;
	}

	public static CoordinateReferenceSystem getCoordinateSystem(String shpFile)
			throws InvalidPathException, MalformedURLException, IOException, FactoryException, NoSuchFileException {
		try (Stream<String> stream = Files
				.lines(EsriUtils.getFile(FilenameUtils.removeExtension(shpFile) + ".prj").toPath())) {
			return CRS.parseWKT(stream.collect(Collectors.joining()));
		}
	}

	private static File getFile(String fileName) throws InvalidPathException, MalformedURLException {
		return FileUtil.getFileFromURL(FileUtil.toURL(fileName));
	}

	private static boolean containsAllCoordinates(List<Coordinate> polygonCoordinates,
			Collection<Coordinate> coordinates) {
		Polygon p = GEO_FACTORY.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));

		return coordinates.stream().allMatch(c -> p.contains(GEO_FACTORY.createPoint(c)));
	}

	private static Polygon adjustOrientation(Polygon polygon) {
		LinearRing shell = (LinearRing) polygon.getExteriorRing();

		if (!CGAlgorithms.isCCW(shell.getCoordinates())) {
			shell = (LinearRing) shell.reverse();
		}

		List<LinearRing> holes = new ArrayList<>();

		for (LinearRing hole : getHoles(polygon)) {
			if (CGAlgorithms.isCCW(hole.getCoordinates())) {
				hole = (LinearRing) hole.reverse();
			}

			holes.add(hole);
		}

		return GEO_FACTORY.createPolygon(shell, holes.toArray(new LinearRing[0]));
	}

	private static List<Polygon> splitToRemoveHoles(Polygon polygon) {
		if (polygon.getNumInteriorRing() == 0) {
			return Arrays.asList(polygon);
		}

		LinearRing shell = (LinearRing) polygon.getExteriorRing();
		LinearRing hole = getHoleWithMinY(polygon);
		Coordinate c = getCoordinateWithMinY(hole);
		LinearRing shell1 = null;
		LinearRing shell2 = null;

		if (shell.isCoordinate(c)) {
			shell1 = combine(shell, hole, c);
		} else {
			Intersection intersect = getIntersectionWithHorizontal(shell, c);

			if (shell.getCoordinateN(intersect.getIndex1()).y < c.y
					|| shell.getCoordinateN(intersect.getIndex2()).y > c.y) {
				throw new RuntimeException("Should not happen");
			}

			shell1 = getCut(shell, intersect, c);
			shell2 = getRest(shell, hole, intersect, c);
		}

		List<LinearRing> holes1 = new ArrayList<>();
		List<LinearRing> holes2 = new ArrayList<>();

		for (LinearRing h : getHoles(polygon)) {
			if (h != hole) {
				if (GEO_FACTORY.createPolygon(shell1).contains(GEO_FACTORY.createPolygon(h))) {
					holes1.add(h);
				} else if (shell2 != null && GEO_FACTORY.createPolygon(shell2).contains(GEO_FACTORY.createPolygon(h))) {
					holes2.add(h);
				} else {
					throw new RuntimeException("Should not happen");
				}
			}
		}

		List<Polygon> polygons = new ArrayList<>();

		polygons.addAll(splitToRemoveHoles(GEO_FACTORY.createPolygon(shell1, holes1.toArray(new LinearRing[0]))));

		if (shell2 != null) {
			polygons.addAll(splitToRemoveHoles(GEO_FACTORY.createPolygon(shell2, holes2.toArray(new LinearRing[0]))));
		}

		return polygons;
	}

	private static LinearRing getHoleWithMinY(Polygon polygon) {
		return Collections.min(getHoles(polygon),
				(r1, r2) -> Double.compare(r1.getEnvelopeInternal().getMinY(), r2.getEnvelopeInternal().getMinY()));
	}

	private static Coordinate getCoordinateWithMinY(LinearRing ring) {
		return Collections.min(Arrays.asList(ring.getCoordinates()), (c1, c2) -> Double.compare(c1.y, c2.y));
	}

	private static Intersection getIntersectionWithHorizontal(LinearRing ring, Coordinate c) {
		double x1 = Double.NaN;
		double x2 = Double.NaN;
		int index1 = -1;
		int index2 = -1;
		double distance1 = Double.POSITIVE_INFINITY;
		double distance2 = Double.POSITIVE_INFINITY;
		double arctan1 = Double.POSITIVE_INFINITY;
		double arctan2 = Double.POSITIVE_INFINITY;
		LineString line = GEO_FACTORY
				.createLineString(new Coordinate[] { new Coordinate(ring.getEnvelopeInternal().getMinX(), c.y),
						new Coordinate(ring.getEnvelopeInternal().getMaxX(), c.y) });

		for (int i = 0; i < ring.getCoordinates().length - 1; i++) {
			Coordinate c1 = ring.getCoordinateN(i);
			Coordinate c2 = ring.getCoordinateN(i + 1);

			if (Math.min(c1.y, c2.y) <= c.y && Math.max(c1.y, c2.y) > c.y) {
				double x = ((Point) line.intersection(GEO_FACTORY.createLineString(new Coordinate[] { c1, c2 })))
						.getX();
				double distance = Math.abs(x - c.x);
				double arctan = Math.abs(c2.x - c1.x) / Math.abs(c2.y - c1.y);

				if (x < c.x && (distance < distance1 || (distance == distance1 && arctan < arctan1))) {
					x1 = x;
					distance1 = distance;
					arctan1 = arctan;
					index1 = i;
				} else if (x > c.x && (distance < distance2 || (distance == distance2 && arctan < arctan2))) {
					x2 = x;
					distance2 = distance;
					arctan2 = arctan;
					index2 = i;
				}
			}
		}

		if (index1 == -1 || index2 == -1) {
			throw new RuntimeException("Should not happen");
		}

		double min = Math.min(ring.getCoordinateN(index1).y, ring.getCoordinateN(index2).y);
		double max = Math.max(ring.getCoordinateN(index1).y, ring.getCoordinateN(index2).y);
		boolean valid = min <= c.y && c.y <= max;

		if (!valid) {
			throw new RuntimeException("Should not happen");
		}

		return new Intersection(x1, x2, index1, index2);
	}

	private static LinearRing getCut(LinearRing shell, Intersection intersect, Coordinate c) {
		List<Coordinate> coordinates = new ArrayList<>();
		int fromIndex = intersect.getIndex1() + 1;
		int toIndex = intersect.getIndex2();

		coordinates.add(new Coordinate(intersect.getX1(), c.y));

		if (fromIndex <= toIndex) {
			for (int i = fromIndex; i <= toIndex; i++) {
				coordinates.add(shell.getCoordinateN(i));
			}
		} else {
			for (int i = fromIndex; i < shell.getNumPoints(); i++) {
				coordinates.add(shell.getCoordinateN(i));
			}

			for (int i = 1; i <= toIndex; i++) {
				coordinates.add(shell.getCoordinateN(i));
			}
		}

		coordinates.add(new Coordinate(intersect.getX2(), c.y));
		coordinates.add(new Coordinate(intersect.getX1(), c.y));

		return GEO_FACTORY.createLinearRing(coordinates.toArray(new Coordinate[0]));
	}

	private static LinearRing getRest(LinearRing shell, LinearRing hole, Intersection intersect, Coordinate c) {
		List<Coordinate> coordinates = new ArrayList<>();
		int shellFromIndex = intersect.getIndex2() + 1;
		int shellToIndex = intersect.getIndex1();
		int holeIndex = Arrays.asList(hole.getCoordinates()).indexOf(c);

		coordinates.add(new Coordinate(intersect.getX2(), c.y));

		if (shellFromIndex <= shellToIndex) {
			for (int i = shellFromIndex; i <= shellToIndex; i++) {
				coordinates.add(shell.getCoordinateN(i));
			}
		} else {
			for (int i = shellFromIndex; i < shell.getNumPoints(); i++) {
				coordinates.add(shell.getCoordinateN(i));
			}

			for (int i = 1; i <= shellToIndex; i++) {
				coordinates.add(shell.getCoordinateN(i));
			}
		}

		coordinates.add(new Coordinate(intersect.getX1(), c.y));

		for (int i = holeIndex; i < hole.getNumPoints(); i++) {
			coordinates.add(hole.getCoordinateN(i));
		}

		for (int i = 1; i <= holeIndex; i++) {
			coordinates.add(hole.getCoordinateN(i));
		}

		coordinates.add(new Coordinate(intersect.getX2(), c.y));

		return GEO_FACTORY.createLinearRing(coordinates.toArray(new Coordinate[0]));
	}

	private static LinearRing combine(LinearRing shell, LinearRing hole, Coordinate c) {
		if (CGAlgorithms.isCCW(shell.getCoordinates()) == CGAlgorithms.isCCW(hole.getCoordinates())) {
			hole = (LinearRing) hole.reverse();
		}

		List<Coordinate> coordinates = new ArrayList<>();
		int shellIndex = Arrays.asList(shell.getCoordinates()).indexOf(c);
		int holeIndex = Arrays.asList(hole.getCoordinates()).indexOf(c);

		for (int i = shellIndex; i < shell.getNumPoints(); i++) {
			coordinates.add(shell.getCoordinateN(i));
		}

		for (int i = 0; i <= shellIndex; i++) {
			coordinates.add(shell.getCoordinateN(i));
		}

		for (int i = holeIndex + 1; i < hole.getNumPoints(); i++) {
			coordinates.add(hole.getCoordinateN(i));
		}

		for (int i = 1; i <= holeIndex; i++) {
			coordinates.add(hole.getCoordinateN(i));
		}

		return GEO_FACTORY.createLinearRing(coordinates.toArray(new Coordinate[0]));
	}

	private static List<Geometry> getGeometries(GeometryCollection collection) {
		List<Geometry> geometries = new ArrayList<>();

		for (int i = 0; i < collection.getNumGeometries(); i++) {
			geometries.add(collection.getGeometryN(i));
		}

		return geometries;
	}

	private static List<LinearRing> getHoles(Polygon polygon) {
		List<LinearRing> holes = new ArrayList<>();

		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			holes.add((LinearRing) polygon.getInteriorRingN(i));
		}

		return holes;
	}

	private static class Intersection {

		private double x1;
		private double x2;
		private int index1;
		private int index2;

		public Intersection(double x1, double x2, int index1, int index2) {
			this.x1 = x1;
			this.x2 = x2;
			this.index1 = index1;
			this.index2 = index2;
		}

		public double getX1() {
			return x1;
		}

		public double getX2() {
			return x2;
		}

		public int getIndex1() {
			return index1;
		}

		public int getIndex2() {
			return index2;
		}
	}
}
