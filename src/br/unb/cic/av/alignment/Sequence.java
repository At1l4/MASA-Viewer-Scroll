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


public class Sequence {
	private SequenceInfo info;
	private SequenceModifiers modifiers;
	private SequenceData data;
	
	public Sequence(SequenceInfo info, SequenceModifiers modifiers) {
		this.info = info;
		this.modifiers = modifiers;
	}
	/**
	 * @return the info
	 */
	public SequenceInfo getInfo() {
		return info;
	}
	/**
	 * @param info the info to set
	 */
	public void setInfo(SequenceInfo info) {
		this.info = info;
	}
	/**
	 * @return the modifiers
	 */
	public SequenceModifiers getModifiers() {
		return modifiers;
	}
	/**
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(SequenceModifiers modifiers) {
		this.modifiers = modifiers;
	}
	/**
	 * @return the data
	 */
	public SequenceData getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(SequenceData data) {
		this.data = data;
	}
}
