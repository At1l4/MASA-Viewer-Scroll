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

public class TextChunk {
	private String chunk0;
	private String chunk1;
	private String matchString;
	private int i0;
	private int i1;
	private int j0;
	private int j1;
	private int size;
	private String suffix;
	
	public TextChunk() {
		// TODO Auto-generated constructor stub
	}

	public void setStartPositions(int i, int j) {
		this.i0 = i;
		this.j0 = j;
	}
	
	public void setEndPositions(int i, int j) {
		this.i1 = i;
		this.j1 = j;
	}
	
	public void setChunks(String chunk0, String chunk1) {
		this.chunk0 = chunk0;
		this.chunk1 = chunk1;
		
		size = Math.min(chunk0.length(), chunk1.length());
		if (chunk0.length() != chunk1.length()) {
			chunk0 = chunk0.substring(0, size);
			chunk1 = chunk1.substring(0, size);
		}
	}

	public String getTextString() {
		final String FORMAT = "%5s: %8d %s %8d\n" +
				"                %s %s\n" +
				"%5s: %8d %s %8d\n";
		
		return String.format(FORMAT, 
				"Query", i0, chunk0, i1,
				getMatchString(), suffix,
				"Sbjct", j0, chunk1, j1
				);
	}
	
	public String getHTMLString() {
		final String FORMAT = "<pre>" +
				"%5s: %8d %s %8d<br>" +
				"                %s %s<br>" +
				"%5s: %8d %s %8d<br>" + 
				"</pre>";
		
		String c0 = chunk0.replaceAll("-", "<font bgcolor=\"#FF9090\">-</font>");
		String c1 = chunk1.replaceAll("-", "<font bgcolor=\"#FF9090\">-</font>");
		
		return String.format(FORMAT, 
				"Query", i0, c0, i1,
				getMatchString(), suffix,
				"Sbjct", j0, c1, j1
				);		
		//"<html><p>" + string.replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;") + "<br><br></p></html>"
		// TODO Auto-generated method stub
	}
	
	private String getMatchString() {
		if (matchString == null) {
			StringBuffer sb = new StringBuffer();
			for (int k=0; k<size; k++) {
				char q = chunk0.charAt(k);
				char s = chunk1.charAt(k);
				sb.append((q==s)?'|':' ');
			}
			matchString = sb.toString();
		}
		return matchString;
	}

	public int getSize() {
		return size;
	}

	public String getChunk0() {
		return chunk0;
	}
	
	public String getChunk1() {
		return chunk1;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


}
