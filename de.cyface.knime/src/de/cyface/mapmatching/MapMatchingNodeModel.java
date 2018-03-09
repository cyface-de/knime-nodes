package de.cyface.mapmatching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.node.BufferedDataContainer;
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
import com.bmwcarit.barefoot.matcher.MatcherCandidate;
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

import de.cyface.smoothing.dialog.TextFieldNodeOption;

public final class MapMatchingNodeModel extends NodeModel {

	private final SettingsModelString mapMatchingFrameworkModel;
	private final SettingsModelString serverAddressInputModel;
	private final SettingsModelString serverPortInputModel;
	private final SettingsModelString databaseNameInputModel; 
	private final SettingsModelString databaseTableInputModel;
	private final SettingsModelString databaseUserInputModel;
	private final SettingsModelString databasePasswordInputModel;
	private final SettingsModelString databaseRoadTypesInputModel;
	private final SettingsModelString latInputColumnModel;
	private final SettingsModelString lonInputColumnModel;
	private final SettingsModelString headingInputColumnModel;
	private final SettingsModelString idInputColumnModel;
	private final SettingsModelString timestampInputColumnModel;

	public MapMatchingNodeModel(final SettingsModelString mapMatchingFrameworkModel,
			final SettingsModelString serverAddressInputModel, final SettingsModelString serverPortInputModel,
			final SettingsModelString databaseNameInputModel, final SettingsModelString databaseTableInputModel,
			final SettingsModelString databaseUserInputModel, final SettingsModelString databasePasswordInputModel,
			final SettingsModelString databaseRoadTypesInputModel, final SettingsModelString latInputColumnModel,
			final SettingsModelString lonInputColumnModel, final SettingsModelString headingInputColumnModel,
			final SettingsModelString idInputColumnModel, final SettingsModelString timestampInputColumnModel) {
		super(1, 1);
		this.mapMatchingFrameworkModel = mapMatchingFrameworkModel;
		this.serverAddressInputModel = serverAddressInputModel;
		this.serverPortInputModel = serverPortInputModel;
		this.databaseNameInputModel = databaseNameInputModel; 
		this.databaseTableInputModel = databaseTableInputModel;
		this.databaseUserInputModel = databaseUserInputModel;
		this.databasePasswordInputModel = databasePasswordInputModel;
		this.databaseRoadTypesInputModel = databaseRoadTypesInputModel;
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
		this.serverPortInputModel.saveSettingsTo(settings);
		this.databaseNameInputModel.saveSettingsTo(settings); 
		this.databaseTableInputModel.saveSettingsTo(settings);
		this.databaseUserInputModel.saveSettingsTo(settings);
		this.databasePasswordInputModel.saveSettingsTo(settings);
		this.databaseRoadTypesInputModel.saveSettingsTo(settings);
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
		this.serverPortInputModel.validateSettings(settings);
		this.databaseNameInputModel.validateSettings(settings); 
		this.databaseTableInputModel.validateSettings(settings);
		this.databaseUserInputModel.validateSettings(settings);
		this.databasePasswordInputModel.validateSettings(settings);
		this.databaseRoadTypesInputModel.validateSettings(settings);
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
		this.serverPortInputModel.loadSettingsFrom(settings);
		this.databaseNameInputModel.loadSettingsFrom(settings); 
		this.databaseTableInputModel.loadSettingsFrom(settings);
		this.databaseUserInputModel.loadSettingsFrom(settings);
		this.databasePasswordInputModel.loadSettingsFrom(settings);
		this.databaseRoadTypesInputModel.loadSettingsFrom(settings);
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

		Properties dbProperties = new Properties();
		dbProperties.put("database.host", serverAddressInputModel.getStringValue());//"localhost");
		dbProperties.put("database.port", serverPortInputModel.getStringValue());
		dbProperties.put("database.name", databaseNameInputModel.getStringValue());//"postgres");
		dbProperties.put("database.table", databaseTableInputModel.getStringValue());//"bfmap_ways");
		dbProperties.put("database.user", databaseUserInputModel.getStringValue());//"postgres");
		dbProperties.put("database.password", databasePasswordInputModel.getStringValue());//"postgres");
		dbProperties.put("database.road-types", databaseRoadTypesInputModel.getStringValue());//"road-types.json");

		RoadMap map = Loader.roadmap(dbProperties, true);

		Matcher matcher = new Matcher(map, new Dijkstra<Road, RoadPoint>(), new TimePriority(), new Geography());

		map.construct();

		// TODO Loads everything into memory. Should be some intelligent
		// iterator leaving not required data in memory.
		List<MatcherSample> samples = loadInputData(inData);

		MatcherKState state = matcher.mmatch(samples, 1, 500);

		DataColumnSpec latColSpec = new DataColumnSpecCreator("Latitude", DoubleCell.TYPE).createSpec();
		DataColumnSpec lonColSpec = new DataColumnSpecCreator("Longitude", DoubleCell.TYPE).createSpec();
		DataTableSpec outSpec = new DataTableSpec(latColSpec, lonColSpec);
		BufferedDataContainer container = exec.createDataContainer(outSpec);

		for (MatcherCandidate cand : state.sequence()) {
			RowKey key = new RowKey(cand.id());

			DataCell latCell = new DoubleCell(cand.point().geometry().getX());
			DataCell lonCell = new DoubleCell(cand.point().geometry().getY());
			DataRow row = new DefaultRow(key, latCell, lonCell);

			container.addRowToTable(row);
		}

		// System.out.println(state.size());

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
		container.close();
		return new BufferedDataTable[] { container.getTable() };
	}

	private List<MatcherSample> loadInputData(BufferedDataTable[] inData) {
		// Reading input data.
		List<MatcherSample> samples = new ArrayList<>();// readSamplesFromSQLite("/home/muthmann/Projekte/Cyface/Goldstandard/measures");

		BufferedDataTable input = inData[0];
		double latitude = 0.0;
		double longitude = 0.0;
		long time = 0L;
		long id = 0L;
		boolean isFirstIteration = true;
		DataTableSpec inputSpec = input.getDataTableSpec();
		int latIndex = inputSpec.findColumnIndex("lat");
		int lonIndex = inputSpec.findColumnIndex("lon");
		int gpsTimeIndex = inputSpec.findColumnIndex("gps_time");

		for (DataRow row : input) {
			double nextLatitude = ((DoubleCell) row.getCell(latIndex)).getDoubleValue();
			double nextLongitude = ((DoubleCell) row.getCell(lonIndex)).getDoubleValue();
			long nextTime = ((LongCell) row.getCell(gpsTimeIndex)).getLongValue();
			long nextId = Long.valueOf(row.getKey().getString());

			if (!isFirstIteration) {
				// Calculate azimuth
				double opposite = Math.abs(nextLatitude - latitude);
				double adjacent = Math.abs(nextLongitude - longitude);
				double tangens = opposite / adjacent;
				double angleInRadians = Math.atan(tangens);
				double angleInDegrees = angleInRadians * 180. / Math.PI;
				double azimuth = 360. - angleInDegrees;

				Point point = new Point(longitude, latitude);
				samples.add(new MatcherSample(String.valueOf(id), time, point, azimuth));
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
		samples.add(new MatcherSample(String.valueOf(id), time, point, 360.));
		return samples;
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] {};
	}

}
