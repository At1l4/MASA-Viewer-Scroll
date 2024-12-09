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

public class GapSequence {
	private int i0;
	private int j0;
	private int i1;
	private int j1;
	private GapType gapType;
	
	public enum GapType {SEQUENCE_0, SEQUENCE_1}

	public GapSequence(int i0, int j0, int i1, int j1) {
		super();
		this.i0 = i0;
		this.j0 = j0;
		this.i1 = i1;
		this.j1 = j1;
	}
	
	public GapSequence(Gap gap, int i, int j, int dir, GapType gapType) {
		this.gapType = gapType;
		if (gapType == GapType.SEQUENCE_0) {
			this.i0 = gap.getPosition();
			this.i1 = this.i0;
			int diff = Math.abs(gap.getPosition() - i);
			this.j0 = j + diff*dir;
			this.j1 = j + (diff + gap.getLength())*dir;
		} else {
			this.j0 = gap.getPosition();
			this.j1 = this.j0;
			int diff = Math.abs(gap.getPosition() - j);
			this.i0 = i + diff*dir;
			this.i1 = i + (diff + gap.getLength())*dir;
		}
	}

	public int getI0() {
		return i0;
	}

	public int getJ0() {
		return j0;
	}

	public int getI1() {
		return i1;
	}

	public int getJ1() {
		return j1;
	}

	public GapType getGapType() {
		return gapType;
	} 
	
	public int getDist(int i, int j) {
		return Math.max(Math.abs(i-i0), Math.abs(j-j0));
	}
}
