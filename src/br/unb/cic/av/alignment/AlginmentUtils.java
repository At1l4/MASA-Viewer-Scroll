/*******************************************************************************
 * *
 * * Copyright (c) 2010-2015   Edans Sandes
 * *
 * * This file is part of MASA-Viewer.
 * * 
 * * MASA-Viewer is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * * 
 * * MASA-Viewer is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * * GNU General Public License for more details.
 * * 
 * * You should have received a copy of the GNU General Public License
 * * along with MASA-Viewer.  If not, see <http://www.gnu.org/licenses/>.
 * *
 ******************************************************************************/
package br.unb.cic.av.alignment;

/**
 * Class with some utility functions
 * @author edans
 */
public class AlginmentUtils {

	/**
	 * Converts a sequence size to a human readable format with suffix BP,
	 * KBP, MBP or GBP.
	 * @param length the length of the sequences.
	 * @return the length in human readable form.
	 */
	public static final String formatSequenceSize(int length) {
		if (length < 1000) {
			return String.format("%d BP", length);
		} else if (length < 1000000) {
			return String.format("%.1f KBP", length/1000.0);
		} else if (length < 1000000000) {
			return String.format("%.1f MBP", length/1000000.0);
		} else {
			return String.format("%.1f GBP", length/1000000000.0);
		}
	}
}
