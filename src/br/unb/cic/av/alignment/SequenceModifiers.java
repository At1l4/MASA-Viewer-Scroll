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

public class SequenceModifiers {
	private static final int FLAG_CLEAR_N = 0x0004;
	private static final int FLAG_COMPLEMENT = 0x0002;
	private static final int FLAG_REVERSE = 0x0001;
	
	private int trimStart;
	private int trimEnd;
	private boolean cleanN;
	private boolean complement;
	private boolean reverse;
	
	public void setTrimPositions(int trimStart, int trimEnd) {
		this.trimStart = trimStart;
		this.trimEnd = trimEnd;
	}
	
	public void setFlags(int flags) {
		cleanN = (flags & FLAG_CLEAR_N) > 0;
		complement = (flags & FLAG_COMPLEMENT) > 0;
		reverse = (flags & FLAG_REVERSE) > 0;
	}

	public int getTrimStart() {
		return trimStart;
	}

	public int getTrimEnd() {
		return trimEnd;
	}

	public boolean isCleanN() {
		return cleanN;
	}

	public boolean isComplement() {
		return complement;
	}

	public boolean isReverse() {
		return reverse;
	}
}

