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

import java.util.ArrayList;

public class GapList extends ArrayList<Gap> {
	private static final long serialVersionUID = 1L;

	private boolean initialized = false;
	private int startPosition;
	private int endPosition;
	private int startOffset;
	private int endOffset;
	private int gapsCount;

	public void computeOffsets(int startPosition, int endPosition) {
		computeOffsets(startPosition, endPosition, 0);
	}
	
	public void computeOffsets(int startPosition, int endPosition, int startOffset) {
		if (!initialized) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.startOffset = startOffset;
			
			int pos = Math.min(startPosition, endPosition);
			int offset = this.startOffset;
			gapsCount = 0;

			for (Gap gap : this) {
				// System.out.printf("%d %d : %d %d\n", gap.getPosition(),
				// gap.getLength(), pos, offset);
				offset += (gap.getPosition() - pos);
				gap.setOffset(offset);
				offset += gap.getLength();
				gapsCount += gap.getLength();
				pos = gap.getPosition();
			}
			this.endOffset = startOffset + Math.abs(startPosition-endPosition) + gapsCount;
			if (startPosition > endPosition) {
				for (Gap gap : this) {
					gap.setOffset(endOffset - gap.getOffset() - gap.getLength());
				}
			}
			this.initialized = true;
		}
	}

	public int getOffset(int pos) {
		if (pos < 1) {
			pos = 1;
		} else if (pos > Math.max(startPosition, endPosition)) {
			pos = Math.max(startPosition, endPosition);
		}
		
		int i0 = 0;
		int i1 = size();

		int offset;

		if (size() == 0 || pos < get(0).getPosition()) {
			if (startPosition < endPosition) {
				offset = startOffset + (pos - startPosition);
			} else {
				offset = endOffset - (pos - endPosition);
			}
		} else {

			while (Math.abs(i1-i0) > 1) {
				int im = (i0 + i1) / 2;

				int gapPos = get(im).getPosition();
				if (gapPos > pos) {
					i1 = im;
				} else if (gapPos < pos) {
					i0 = im;
				} else {
					i0 = im;
					i1 = im;
				}
			}
			if (startPosition < endPosition) {
				int diffPos = pos-get(i0).getPosition();
				if (diffPos == 0) {
					offset = get(i0).getOffset();
				} else {
					offset = get(i0).getOffset() + get(i0).getLength() + diffPos;
				}
			} else {
				int diffPos = pos-get(i0).getPosition();
				offset = get(i0).getOffset() - diffPos;
			}
		}
		return offset;
	}

	public int getPosition(int offset) {
		return getPositionInfo(offset)[0];
	}
	
	public int getPositionRemainer(int offset) {
		return getPositionInfo(offset)[1];
	}
	
	private int[] getPositionInfo(int offset) {
		int i0 = 0;
		int i1 = size()-1;
		
		int dir = (startPosition > endPosition) ? -1 : +1;
		if (dir < 0) {
			int tmp = i0;
			i0 = i1;
			i1 = tmp;
		}
		
		int gapIndex = -1;

		int position;
		int remaining;
		if (size() == 0 || offset < get(i0).getOffset()) {
			gapIndex = -1;
		} else if (offset > get(i1).getOffset()) {
			gapIndex = i1;
		} else {

			while (Math.abs(i1-i0) > 1) {
				int im = (i0 + i1) / 2;

				int gapOffset = get(im).getOffset();
				if (gapOffset > offset) {
					i1 = im;
				} else if (gapOffset < offset) {
					i0 = im;
				} else {
					i0 = im;
					i1 = im;
				}
			}
			gapIndex = i0;
		}
		
		if (gapIndex == -1) {
			if (dir > 0) {
				position = startPosition + offset;
			} else {
				position = startPosition - offset;
			}
			remaining = 0;
		} else {
			int diffOffset = offset - (get(gapIndex).getOffset()+get(gapIndex).getLength());
			if (diffOffset > 0) {
				position = get(gapIndex).getPosition() + diffOffset*dir;
				remaining = 0;
			} else {
				position = get(gapIndex).getPosition();
				remaining = -diffOffset;
			}		
		}
		return new int[]{position, remaining};
	}

	public int getGapsCount() {
		return gapsCount;
	}

	/**
	 * @return the startPosition
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * @return the startOffset
	 */
	public int getStartOffset() {
		return startOffset;
	}

	/**
	 * @return the endOffset
	 */
	public int getEndOffset() {
		return endOffset;
	}

}
