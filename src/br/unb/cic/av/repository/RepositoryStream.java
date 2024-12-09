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
package br.unb.cic.av.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class RepositoryStream {
	private String fastaDescription;
	private BufferedReader reader;
	private int count;
	private boolean done;
	
	protected RepositoryStream(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(stream));
		this.fastaDescription = reader.readLine();
		if (this.fastaDescription.startsWith(">")) {
			this.fastaDescription = this.fastaDescription.substring(1);
		} else {
			throw new IOException("File is not in fasta format");
		}
		this.done = false;
		this.count = 0;
	}
	
	public String getFastaDescription() {
		return fastaDescription;
	}

	public String readLine() throws IOException {
		String line = reader.readLine();
		if (line != null) {
			count += line.length();
		} else {
			reader.close();
			done = true;
		}
		return line;
	}


	public boolean isDone() {
		return done;
	}	
	
	public int getReadCount() {
		return count;
	}
}
