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
 * Contains all classes required for a node calculating the envelope function for a numeric input column.
 * The envelope function is either an upper envelope function or a lower envelope function.
 * An upper envelope function is a function between the local maxima of a function, while a lower envelope function is
 * the function connecting the minima.
 * 
 * The node provides the envelope function as a filter on all the rows of the input, providing only the ones constituting the envelope function.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
package de.cyface.knime.nodes.envelope;