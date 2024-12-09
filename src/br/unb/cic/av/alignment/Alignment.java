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
import java.util.List;

/**
 * This class represents an alignment.
 * 
 * @author edans
 */
public class Alignment {

	/**
	 * Number of sequences. Currently, only 2 are supported.
	 */
	private static final int SEQUENCES_NUMBER = 2;

	/*
	 * Parameters
	 */

	/**
	 * Alignment parameters.
	 */
	private AlignmentParams alignmentParams;

	/*
	 * Results
	 */

	/**
	 * Raw Score considering the punctuation defined in {@link #alignmentParams}
	 * .
	 */
	private int rawScore;

	/**
	 * Number of matches in this alignment.
	 */
	private int matches;

	/**
	 * Number of mismatches in this alignment.
	 */
	private int mismatches;

	/**
	 * Number of gap openings. Remark that a gap sequence with a single gap
	 * represents 1 gap open and 1 gap extension.
	 */
	private int gapOpen;

	/**
	 * Number of gap extensions.
	 */
	private int gapExtensions;

	/**
	 * The start position of each sequence.
	 */
	private int[] startPosition = new int[SEQUENCES_NUMBER];

	/**
	 * The end position of each sequence.
	 */
	private int[] endPosition = new int[SEQUENCES_NUMBER];

	/**
	 * The start offset of each sequence in the alignment.
	 */
	private int[] startOffset = new int[SEQUENCES_NUMBER];

	/**
	 * The end offset of each sequence in the alignment.
	 */
	private int[] endOffset = new int[SEQUENCES_NUMBER];

	/**
	 * The gap list presented in each sequence.
	 */
	private GapList[] gaps = new GapList[SEQUENCES_NUMBER];

	/**
	 * The direction of each sequence. +1 represent forward direction and -1
	 * represent reverse direction.
	 */
	private int[] dir = new int[SEQUENCES_NUMBER];

	/**
	 * The gap sequence that defines the alignment path in the DP matrix.
	 */
	private List<GapSequence> gapSequence;

	/*
	 * Sequences
	 */

	/**
	 * Sequences with gaps inside the data.
	 */
	private SequenceWithGaps[] sequenceWithGaps = new SequenceWithGaps[SEQUENCES_NUMBER];

	/**
	 * The score of each block. This data is optional and may be present in the
	 * datafile.
	 */
	private int[][] blocks;

	/*
	 * Constructors
	 */

	/**
	 * Initialize an alignment with some parameters.
	 * 
	 * @param params the parameters of the alignment.
	 */
	public Alignment(AlignmentParams params) {
		this.alignmentParams = params;
		this.startOffset[0] = 0;
		this.endOffset[0] = -1;
		this.startOffset[1] = 0;
		this.endOffset[1] = -1;
	}

	/**
	 * Private method that truncates the cloned alignment and creates a new
	 * alignment. All the original data are preserved, except for the boundary
	 * positions and boundary offsets. This is useful to avoid memory usage.
	 * 
	 * @param clone the alignment to be cloned.
	 * @param i0 the start position of sequence[0].
	 * @param i1 the end position of sequence[0] .
	 * @param j0 the start position of sequence[1].
	 * @param j1 the end position of sequence[1].
	 * @param offset0 the start offset of both sequences.
	 * @param offset1 the end offset of both sequences.
	 */
	private Alignment(Alignment clone, int i0, int i1, int j0, int j1,
			int offset0, int offset1) {
		this.alignmentParams = clone.alignmentParams;
		this.setBoundaryPositions(0, i0, i1);
		this.setBoundaryPositions(1, j0, j1);
		this.setBoundaryOffset(0, offset0, offset1);
		this.setBoundaryOffset(1, offset0, offset1);

		gaps = clone.gaps;
		gapSequence = clone.gapSequence;
		sequenceWithGaps = clone.sequenceWithGaps;
	}

	/**
	 * @return the alignment parameters
	 */
	public AlignmentParams getAlignmentParams() {
		return alignmentParams;
	}

	/**
	 * Defines the raw score of this alignment
	 * 
	 * @param rawScore the raw score.
	 */
	public void setRawScore(int rawScore) {
		this.rawScore = rawScore;
	}

	/**
	 * @return the raw score.
	 */
	public int getRawScore() {
		return rawScore;
	}

	/**
	 * Defines a gap list for one of the sequences.
	 * 
	 * @param id The index of the sequence.
	 * @param gapList the gap list to be assigned to sequence[id].
	 */
	public void setGaps(int id, GapList gapList) {
		this.gaps[id] = gapList;
		this.gaps[id].computeOffsets(startPosition[id], endPosition[id]);

		startOffset[id] = 0;
		endOffset[id] = Math.abs(endPosition[id] - startPosition[id])
				+ gapList.getGapsCount();

		if (this.gaps[0] != null && this.gaps[1] != null) {
			createGapSequence();
		}
	}

	/**
	 * @param id The index of the sequence.
	 * @return the gap list of the sequence[id]
	 */
	public GapList getGaps(int id) {
		return this.gaps[id];
	}

	/**
	 * Defines the boundary positions (start..end) of a given sequence in this
	 * alignment. If start is greater than end, so the sequence is in reverse
	 * direction. Otherwise it is in the normal forward direction.
	 * 
	 * @param id The index of the sequence.
	 * @param start the start position of sequence[id] in the alignment.
	 * @param end the end position of sequence[id] in the alignment.
	 */
	public void setBoundaryPositions(int id, int start, int end) {
		this.startPosition[id] = start;
		this.endPosition[id] = end;

		this.dir[id] = (start < end) ? 1 : -1;
	}

	/**
	 * Defines the boundary offset (start..end) of a given sequence in this
	 * alignment. The boundary offset is different than the boundary position,
	 * since the offset considers the boundaries of the alignment in the
	 * sequence with gaps.
	 * 
	 * @param id The index of the sequence.
	 * @param start the start offset of sequence[id] in the alignment.
	 * @param end the end offset of sequence[id] in the alignment.
	 */
	public void setBoundaryOffset(int i, int start, int end) {
		this.startOffset[i] = start;
		this.endOffset[i] = end;
	}

	/**
	 * @param id The index of the sequence.
	 * @return the start position of sequence[id] in the alignment.
	 */
	public int getSequenceStartPosition(int id) {
		return startPosition[id];
	}

	/**
	 * @param id The index of the sequence.
	 * @return the end position of sequence[id] in the alignment.
	 */
	public int getSequenceEndPosition(int id) {
		return endPosition[id];
	}

	/**
	 * @param id The index of the sequence.
	 * @return the start offset of sequence[id] in the alignment.
	 */
	public int getSequenceStartOffset(int id) {
		return startOffset[id];
	}

	/**
	 * @param id The index of the sequence.
	 * @return the end offset of sequence[id] in the alignment.
	 */
	public int getSequenceEndOffset(int id) {
		return endOffset[id];
	}

	/**
	 * @param id The index of the sequence.
	 * @return the direction of sequence[id] in the alignment. +1 represent the
	 *         forward direction and -1 represent the reverse direction.
	 */
	public int getSequenceDirection(int id) {
		return dir[id];
	}

	/**
	 * Converts from the position coordinates and the offset coordinates in
	 * sequence[id].
	 * 
	 * @param id The index of the sequence.
	 * @param position the position to be converted.
	 * @return the offset of the given position in sequence[id].
	 */
	public int getSequenceOffset(int id, int position) {
		return gaps[id].getOffset(position);
	}

	/**
	 * Converts from the offset coordinates and the position coordinates in
	 * sequence[id].
	 * 
	 * @param id The index of the sequence.
	 * @param offset the offset to be converted.
	 * @return the position of the given offset in sequence[id].
	 */
	public int getSequencePosition(int id, int offset) {
		return gaps[id].getOffset(offset);
	}

	/**
	 * Create a gap sequence that defines the alignment path in the DP matrix.
	 * With the gap sequences, the alignment patch is simply made by joining the
	 * gap positions.
	 */
	private void createGapSequence() {
		gapSequence = new ArrayList<GapSequence>();


		int i = this.startPosition[0];
		int j = this.startPosition[1];
		
		int dirI = getSequenceDirection(0);
		int dirJ = getSequenceDirection(1);

		int c0 = dirI > 0 ? 0 : gaps[0].size()-1;
		int c1 = dirJ > 0 ? 0 : gaps[1].size()-1;

		while ((c0 >= 0 && c0 < gaps[0].size()) || (c1 >= 0 && c1 < gaps[1].size())) {
			GapSequence gap0 = null;
			GapSequence gap1 = null;
			
			
			if (c1 >= 0 && c1 < gaps[1].size()) {
				gap1 = new GapSequence(gaps[1].get(c1), i, j, dirI, GapSequence.GapType.SEQUENCE_1);
			}
			if (c0 >= 0 && c0 < gaps[0].size()) {
				gap0 = new GapSequence(gaps[0].get(c0), i, j, dirJ, GapSequence.GapType.SEQUENCE_0);
			}
			
			/*
			 * Determines the distance from the current point (i,j) to the next
			 * gaps at each sequence. The nearest gap is selected.
			 */
			GapSequence gap;
			if (gap1 == null || (gap0 != null && gap0.getDist(i, j) < gap1.getDist(i, j))) {
				gap = gap0;
				c0 += dirI;
			} else {
				gap = gap1;
				c1 += dirJ;
			}
			
			// Get the next gap.
			gapSequence.add(gap);
			i = gap.getI1();
			j = gap.getJ1();
		}
	}

	/**
	 * Get the gap sequences with all the gaps of the alignment. If the gap
	 * sequences has never been retrieved, this method creates the sequence
	 * (lazy creation).
	 * 
	 * @return the gap sequences.
	 */
	public List<GapSequence> getGapSequences() {
		if (gapSequence == null) {
			createGapSequence();
		}
		return gapSequence;
	}

	/**
	 * Truncates the current alignment between two offsets (inclusive).
	 * 
	 * @param cutOffset0 the start offset.
	 * @param cutOffset1 the end offset.
	 * @return a new alignment truncated in the given points. This new object is
	 *         created by the private constructor
	 *         {@link #Alignment(Alignment, int, int, int, int, int, int)}.
	 */
	public Alignment truncate(int cutOffset0, int cutOffset1) {
		int cutI0 = gaps[0].getPosition(cutOffset0);
		int cutI1 = gaps[0].getPosition(cutOffset1);

		int cutJ0 = gaps[1].getPosition(cutOffset0);
		int cutJ1 = gaps[1].getPosition(cutOffset1);

		return new Alignment(this, cutI0, cutI1, cutJ0, cutJ1, cutOffset0,
				cutOffset1);
	}

	/**
	 * Get the sequences with gaps. The returned object is created only
	 * in the first call to this method (lazy creation).
	 * 
	 * @param id The index of the sequence.
	 * @return the sequence with gaps.
	 */
	public SequenceWithGaps getAlignmentWithGaps(int id) {
		if (sequenceWithGaps[id] == null) {
			sequenceWithGaps[id] = new SequenceWithGaps(
					alignmentParams.getSequence(id).getData(),
					gaps[id]);
		}
		return sequenceWithGaps[id];
	}
	
	/**
	 * @param blocks
	 */
	public void setBlocks(int[][] blocks) {
		this.blocks = blocks;
	}

	/**
	 * @return
	 */
	public int[][] getBlocks() {
		return blocks;
	}

	/**
	 * @param matches
	 */
	public void setMatches(int matches) {
		this.matches = matches;
	}
	
	/**
	 * @return
	 */
	public int getMatches() {
		return matches;
	}

	/**
	 * @param mismatches
	 */
	public void setMismatches(int mismatches) {
		this.mismatches = mismatches;
	}
	
	/**
	 * @return
	 */
	public int getMismatches() {
		return mismatches;
	}

	/**
	 * @param gapOpen
	 */
	public void setGapOpen(int gapOpen) {
		this.gapOpen = gapOpen;
	}
	
	/**
	 * @return
	 */
	public int getGapOpen() {
		return gapOpen;
	}

	/**
	 * @param gapExtensions
	 */
	public void setGapExtensions(int gapExtensions) {
		this.gapExtensions = gapExtensions;
	}
	
	/**
	 * @return
	 */
	public int getGapExtensions() {
		return gapExtensions;
	}


}
