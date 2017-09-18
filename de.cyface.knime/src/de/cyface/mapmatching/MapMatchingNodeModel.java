package de.cyface.mapmatching;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.bmwcarit.barefoot.matcher.Matcher;
import com.bmwcarit.barefoot.matcher.MatcherKState;
import com.bmwcarit.barefoot.matcher.MatcherSample;
import com.bmwcarit.barefoot.roadmap.Loader;
import com.bmwcarit.barefoot.roadmap.Road;
import com.bmwcarit.barefoot.roadmap.RoadMap;
import com.bmwcarit.barefoot.roadmap.RoadPoint;
import com.bmwcarit.barefoot.roadmap.TimePriority;
import com.bmwcarit.barefoot.spatial.Geography;
import com.bmwcarit.barefoot.topology.Dijkstra;
import com.esri.core.geometry.Point;

public final class MapMatchingNodeModel extends NodeModel {

	private static final String GPS_POINT_QUERY = "SELECT * FROM gps_points";

	private final SettingsModelString mapMatchingFrameworkModel;
	private final SettingsModelString serverAddressInputModel;
	private final SettingsModelString latInputColumnModel;
	private final SettingsModelString lonInputColumnModel;
	private final SettingsModelString headingInputColumnModel;
	private final SettingsModelString idInputColumnModel;
	private final SettingsModelString timestampInputColumnModel;

	public MapMatchingNodeModel(final SettingsModelString mapMatchingFrameworkModel,
			final SettingsModelString serverAddressInputModel, final SettingsModelString latInputColumnModel,
			final SettingsModelString lonInputColumnModel, final SettingsModelString headingInputColumnModel,
			final SettingsModelString idInputColumnModel, final SettingsModelString timestampInputColumnModel) {
		super(1, 1);
		this.mapMatchingFrameworkModel = mapMatchingFrameworkModel;
		this.serverAddressInputModel = serverAddressInputModel;
		this.latInputColumnModel = latInputColumnModel;
		this.lonInputColumnModel = lonInputColumnModel;
		this.headingInputColumnModel = headingInputColumnModel;
		this.idInputColumnModel = idInputColumnModel;
		this.timestampInputColumnModel = timestampInputColumnModel;
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		mapMatchingFrameworkModel.saveSettingsTo(settings);
		serverAddressInputModel.saveSettingsTo(settings);
		latInputColumnModel.saveSettingsTo(settings);
		lonInputColumnModel.saveSettingsTo(settings);
		headingInputColumnModel.saveSettingsTo(settings);
		idInputColumnModel.saveSettingsTo(settings);
		timestampInputColumnModel.saveSettingsTo(settings);

	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		mapMatchingFrameworkModel.validateSettings(settings);
		serverAddressInputModel.validateSettings(settings);
		latInputColumnModel.validateSettings(settings);
		lonInputColumnModel.validateSettings(settings);
		headingInputColumnModel.validateSettings(settings);
		idInputColumnModel.validateSettings(settings);
		timestampInputColumnModel.validateSettings(settings);

	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		mapMatchingFrameworkModel.loadSettingsFrom(settings);
		serverAddressInputModel.loadSettingsFrom(settings);
		latInputColumnModel.loadSettingsFrom(settings);
		lonInputColumnModel.loadSettingsFrom(settings);
		headingInputColumnModel.loadSettingsFrom(settings);
		idInputColumnModel.loadSettingsFrom(settings);
		timestampInputColumnModel.loadSettingsFrom(settings);

	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {

//		RoadMap map = Loader.roadmap("config/sachsen.properties", true);
//
//		Matcher matcher = new Matcher(map, new Dijkstra<Road, RoadPoint>(), new TimePriority(), new Geography());
//
//		List<MatcherSample> samples = readSamplesFromSQLite("/home/muthmann/Projekte/Cyface/Goldstandard/measures");
//		map.construct();
//
//		MatcherKState state = matcher.mmatch(samples, 1, 500);
//
//		System.out.println(state.size());

		// System.out.println(state.toDebugJSON());
		// for (MatcherCandidate cand : state.sequence()) {
		// System.out.println(cand.point().edge().base().refid());
		// System.out.println(cand.point().edge().base().id());
		// System.out.println(cand.point().edge().heading());
		// System.out.println(cand.point().geometry().getX());
		// System.out.println(cand.point().geometry().getY());
		// if (cand.transition() != null) {
		// System.out.println(cand.transition().route().geometry());
		// }
		// }

		return super.execute(inData, exec);
	}

	private static List<MatcherSample> readSamplesFromSQLite(final String databaseFilePath) {
		List<MatcherSample> ret = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFilePath));
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(GPS_POINT_QUERY)) {

			double latitude = 0.0;
			double longitude = 0.0;
			long time = 0L;
			long id = 0L;
			boolean isFirstIteration = true;

			while (rs.next()) {
				double nextLatitude = rs.getDouble(rs.findColumn("lat"));
				double nextLongitude = rs.getDouble(rs.findColumn("lon"));
				long nextTime = rs.getLong(rs.findColumn("gps_time"));
				long nextId = rs.getLong(rs.findColumn("_id"));

				if (!isFirstIteration) {
					// Calculate azimuth
					double opposite = Math.abs(nextLatitude - latitude);
					double adjacent = Math.abs(nextLongitude - longitude);
					double tangens = opposite / adjacent;
					double angleInRadians = Math.atan(tangens);
					double angleInDegrees = angleInRadians * 180. / Math.PI;
					double azimuth = 360. - angleInDegrees;

					Point point = new Point(longitude, latitude);
					ret.add(new MatcherSample(String.valueOf(id), time, point, azimuth));
				} else {
					isFirstIteration = false;
				}

				latitude = nextLatitude;
				longitude = nextLongitude;
				time = nextTime;
				id = nextId;
			}
			Point point = new Point(longitude, latitude);
			// Since there is no heading information, last point always points
			// north.
			ret.add(new MatcherSample(String.valueOf(id), time, point, 360.));

			return ret;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[]{};
	}

}
