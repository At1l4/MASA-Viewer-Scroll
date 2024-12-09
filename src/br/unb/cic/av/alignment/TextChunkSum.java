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


public class TextChunkSum {

	private int score = 0;
	private int gapOpeningsCount = 0;
	private int gapExtentionsCount = 0;
	private int matchesCount = 0;
	private int mismatchesCount = 0;
	
	private boolean qgap = false;
	private boolean sgap = false;
	
	private int matchScore;
	private int mismatchScore;
	private int gapOpenScore;
	private int gapExtScore;
	
	public TextChunkSum(int match, int mismatch, int gapOpen, int gapExt) {
		this.matchScore = match;
		this.mismatchScore = mismatch;
		this.gapOpenScore = gapOpen;
		this.gapExtScore = gapExt;
	}
	
	
	public int sumChunk(TextChunk chunk) {
		int temp = 0;
		for (int k = 0; k < chunk.getSize(); k++) {
			char q = chunk.getChunk0().charAt(k);
			char s = chunk.getChunk1().charAt(k);
			if (q == '-') {
				if (!qgap) {
					temp += gapOpenScore;
					gapOpeningsCount++;
				}
				temp += gapExtScore;
				gapExtentionsCount++;
				qgap = true;
				sgap = false;
			} else if (s == '-') {
				if (!sgap) {
					temp += gapOpenScore;
					gapOpeningsCount++;
				}
				temp += gapExtScore;
				gapExtentionsCount++;
				qgap = false;
				sgap = true;
			} else {
				if (q == s) {
					temp += matchScore;
					matchesCount++;
				} else {
					temp += mismatchScore;
					mismatchesCount++;
				}
				qgap = false;
				sgap = false;
			}
		}
		score += temp;
		return temp;
	}


	public int getScore() {
		return score;
	}


	public int getGapOpeningsCount() {
		return gapOpeningsCount;
	}


	public int getGapExtentionsCount() {
		return gapExtentionsCount;
	}


	public int getMatchesCount() {
		return matchesCount;
	}


	public int getMismatchesCount() {
		return mismatchesCount;
	}


	public int getMatchScore() {
		return matchScore;
	}


	public int getMismatchScore() {
		return mismatchScore;
	}


	public int getGapOpenScore() {
		return gapOpenScore;
	}


	public int getGapExtScore() {
		return gapExtScore;
	}

	public String getHTMLString() {

		final String FORMAT = "<pre>" +
				"Total Score:     %8d<br>" +
				"Matches:         %8d (+%d)<br>" +
				"Mismatches:      %8d (%d)<br>" +
				"Gap Openings:    %8d (%d)<br>" +
				"Gap Extentions:  %8d (%d)<br>" +
				"</pre>";
		
		return String.format(FORMAT, score, 
				matchesCount, matchScore,
				mismatchesCount, mismatchScore,
				gapOpeningsCount, gapOpenScore,
				gapExtentionsCount, gapExtScore);
		//"<html><p>" + string.replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;") + "<br><br></p></html>"
		// TODO Auto-generated method stub
	}
}
