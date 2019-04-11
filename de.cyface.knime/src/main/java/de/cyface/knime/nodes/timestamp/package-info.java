/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface KNIME Nodes.
 *
 * The Cyface KNIME Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface KNIME Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface KNIME Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This package contains all classes required for the Cyface timestamp aligner node.
 * This node takes two input tables with time series from the same timeframe but different sources and joins them by
 * aligning the one with the higher frequency to the one with lower.
 * Values from the lower frequency one are duplicated.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 2.2.0
 */
package de.cyface.knime.nodes.timestamp;