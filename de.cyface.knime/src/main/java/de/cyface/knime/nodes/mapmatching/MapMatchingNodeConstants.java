/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface Nodes.
 *
 * The Cyface Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.nodes.mapmatching;

public final class MapMatchingNodeConstants {
	
	public final static String MAP_MATCHING_FRAMEWORK_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.framework";
	public final static String SERVER_ADDRESS_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.server.address";
	public final static String SERVER_PORT_INPUT_OPTION_CONFIG_IDENTIFIER ="de.cyface.mapmatching.id.server.port";
	public final static String DATABASE_NAME_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.database.name";
	public final static String DATABASE_TABLE_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.database.table";
	public final static String DATABASE_USER_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.database.user";
	public final static String DATABASE_PASSWORD_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.database.password";
	public final static String DATABASE_ROAD_TYPES_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.database.road-types";
	
	public final static String LAT_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.latinput";
	public final static String LON_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.loninput";
	public final static String HEADING_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.headinginput";
	public final static String ID_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.idinput";
	public final static String TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.timestampinput";

	public final static String MAP_MATCHING_FRAMEWORK_OPTION_LABEL = "Select Framework to use";
	public final static String SERVER_ADDRESS_INPUT_OPTION_LABEL = "Provide Matching Server Address";
	public final static String SERVER_PORT_INPUT_OPTION_LABEL ="Provide Matching Server Port";
	public final static String DATABASE_NAME_INPUT_OPTION_LABEL = "Provide name of database with matching data";
	public final static String DATABASE_TABLE_INPUT_OPTION_LABEL = "Provide name of database table";
	public final static String DATABASE_USER_INPUT_OPTION_LABEL = "Provide user with access to matching data table";
	public final static String DATABASE_PASSWORD_INPUT_OPTION_LABEL = "Provide password for user with access to matching data table";
	public final static String DATABASE_ROAD_TYPES_INPUT_OPTION_LABEL = "Provide path of configuration file for road type priority";
	
	public final static String LAT_INPUT_COLUMN_SELECTOR_LABEL = "Latitude Column";
	public final static String LON_INPUT_COLUMN_SELECTOR_LABEL = "Longitude Column";
	public final static String HEADING_INPUT_COLUMN_SELECTOR_LABEL = "Heading Column (Optional)";
	public final static String ID_INPUT_COLUMN_SELECTOR_LABEL = "Identifier Column";
	public final static String TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL = "Timestamp Column";

	private MapMatchingNodeConstants() {
		// Nothing to do here.
	}

}
