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

import java.util.Arrays;

/**
 * This class store the sequence data with all the gaps inserted. The way to
 * retrieve the data is using the {@link #reset(int, int)} and
 * {@link #getNextChunk(int)} methods.
 * 
 * @author edans
 */
public class SequenceWithGaps {
	/**
	 * The gap list that was inserted in this sequence.
	 */
	private GapList gaps;

	/**
	 * The offset at which the chunks will stop.
	 */
	private int endOffset;

	/**
	 * The offset of the next chunk.
	 */
	private int offset;

	/**
	 * Indicates if the chunks has reached the endOffset.
	 */
	private boolean done = true;

	/**
	 * The string buffer that holds the sequence data with gaps.
	 */
	private StringBuffer sb = new StringBuffer();

	/**
	 * Creates a new sequence with gaps.
	 * 
	 * @param sequence the original sequence data.
	 * @param gaps the gap list to be inserted.
	 */
	public SequenceWithGaps(SequenceData sequence, GapList gaps) {
		this.gaps = gaps;

		/*if (sequence.getData() == null) {
			return;
		}*/

		// Identifies the boundaries (regardless of the direction)
		int start = Math.min(gaps.getStartPosition(), gaps.getEndPosition());
		int end = Math.max(gaps.getStartPosition(), gaps.getEndPosition());

		// Buffer with gaps to insert.
		final int SIZE = 1024;
		char[] chars = new char[SIZE];
		Arrays.fill(chars, '-');

		int pos = start;
		for (Gap gap : gaps) {
			// Append sequence data from last position until this gap position.
			int nextPos = gap.getPosition();
			sb.append(sequence.getData(pos - 1, nextPos - 1));
			pos = nextPos;

			// Insert gaps in blocks of 1024.
			int count = gap.getLength();
			while (count > 0) {
				int len = Math.min(count, SIZE);
				sb.append(chars, 0, len);
				count -= len;
			}
		}

		// Append the remaining sequence data.
		sb.append(sequence.getData(pos - 1, end));
		if (gaps.getStartPosition() > gaps.getEndPosition()) {
			sb.reverse();
		}
	}

	/**
	 * Reset the chunk look.
	 * 
	 * @param startOffset the first offset to be returned in the chunks.
	 * @param endOffset the last offset to be returned in the cunks.
	 */
	public void reset(int startOffset, int endOffset) {
		this.offset = startOffset;
		this.endOffset = endOffset;
		this.done = false;

	}

	/**
	 * @return the current position of the sequence.
	 */
	public int getCurrentPosition() {
		return gaps.getPosition(offset);
	}

	/**
	 * @return true when the chunks has ended.
	 */
	public boolean isDone() {
		return (done);
	}

	/**
	 * Returns the next chunk from the current position up to length offsets.
	 * 
	 * @param length the length of the chunk to be returned.
	 * @return the chunk string.
	 */
	public String getNextChunk(int length) {
		if (isDone()) {
			return "";
		} else {

			int nextOffset = Math.min(endOffset + 1, offset + length);

			String chunk = sb.substring(offset, nextOffset);
			if (nextOffset == endOffset + 1) {
				done = true;
			}

			offset = nextOffset;
			return chunk;
		}
	}

}
