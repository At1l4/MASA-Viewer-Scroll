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


public class AlignmentParams {
	public enum AlignmentMethod {GLOBAL, LOCAL};
	public enum PenaltySystem {LINEAR_GAP, AFFINE_GAP};
	public enum ScoreSystem {MATCH_MISMATCH, SIMILARITY_MATRIX};
	
	private List<Sequence> sequences = new ArrayList<Sequence>();

	private AlignmentMethod alignmentMethod;
	private PenaltySystem penaltySystem;
	private ScoreSystem scoreSystem;

	private int match;
	private int mismatch;

	private int gapOpen;
	private int gapExtension;
	
	public AlignmentParams() {
		
	}
	
	public Sequence getSequence(int id) {
		return sequences.get(id);
	}
	
	public int getSequencesCount() {
		if (sequences == null) {
			return 0;
		} else {
			return sequences.size();
		}
	}

	public int getMatch() {
		return match;
	}
	public void setMatch(int match) {
		this.match = match;
	}
	public int getMismatch() {
		return mismatch;
	}
	public void setMismatch(int mismatch) {
		this.mismatch = mismatch;
	}
	public int getGapOpen() {
		return gapOpen;
	}
	public void setGapOpen(int gapOpen) {
		this.gapOpen = gapOpen;
	}
	public int getGapExtension() {
		return gapExtension;
	}
	public void setGapExtension(int gapExtension) {
		this.gapExtension = gapExtension;
	}

	public void setAffineGapPenalties(int gapOpen, int gapExtension) {
		this.gapOpen = gapOpen;
		this.gapExtension = gapExtension;
	}

	public void setMatchMismatchScores(int match, int mismatch) {
		this.match = match;
		this.mismatch = mismatch;
	}

	public AlignmentMethod getAlignmentMethod() {
		return alignmentMethod;
	}

	public void setAlignmentMethod(AlignmentMethod alignmentMethod) {
		this.alignmentMethod = alignmentMethod;
	}

	public PenaltySystem getPenaltySystem() {
		return penaltySystem;
	}

	public void setPenaltySystem(PenaltySystem penaltySystem) {
		this.penaltySystem = penaltySystem;
	}

	public ScoreSystem getScoreSystem() {
		return scoreSystem;
	}

	public void setScoreSystem(ScoreSystem scoreSystem) {
		this.scoreSystem = scoreSystem;
	}

	public void addSequence(Sequence sequence) {
		sequences.add(sequence);
	}

	public List<Sequence> getSequences() {
		return sequences;
	}

	
	
}
