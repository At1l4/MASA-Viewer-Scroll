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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SequenceData {
	private StringBuffer sb;

	private int reverseData;
	private int offset0;
	private int offset1;
	
	public SequenceData() {
		sb = null;
	}
	
	public SequenceData(File file, SequenceModifiers modifiers) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		reader.readLine(); // description

		char[] complement_map = new char[256];
		for (int i=0; i<256; i++) {
			complement_map[i] = Character.toUpperCase((char)i);
		}
		if (modifiers.isComplement()) {
			complement_map['A'] = complement_map['a'] = 'T';
			complement_map['T'] = complement_map['t'] = 'A';
			complement_map['C'] = complement_map['c'] = 'G';
			complement_map['G'] = complement_map['g'] = 'C';
		}
		
		sb = new StringBuffer();
		int i;
		while ((i = reader.read()) != -1) {
			if (i == '\r' || i == '\n' || i == ' ')
				continue;
			if (modifiers.isCleanN() && ((char)i == 'N' || (char)i == 'n')) continue;
			sb.append(complement_map[i]);
		}
	}
	
	public StringBuffer getSb() {
		return sb;
	}

	public int getReverseData() {
		return reverseData;
	}

	public int getOffset0() {
		return offset0;
	}

	public int getOffset1() {
		return offset1;
	}

	public void setSb(StringBuffer sb) {
		this.sb = sb;
	}

	public void setReverseData(int reverseData) {
		this.reverseData = reverseData;
	}

	public String getData() {
		if (sb == null) {
			return null;
		}
		return sb.toString();
	}

	public String getData(int beginIndex, int endIndex) {
		if (sb == null || beginIndex > endIndex || beginIndex < 0) {
			char[] chars = new char[endIndex - beginIndex];
			Arrays.fill(chars, '?');
			return new String(chars);			
		} else {
			return sb.substring(beginIndex, endIndex);
		}
	}

}
