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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Vector;

import br.unb.cic.av.alignment.SequenceInfo;

public class LocalRepository {
	private static final String FILE_SUFFIX = ".fasta";
	private static File repositoryPath;
	
	static {
		repositoryPath = ApplicationPreferences.getRepositoryPath();
		repositoryPath.mkdirs();
	}
	
	public static void storeSequenceAsync(final RepositoryStream stream) {
		new Thread() {
			public void run() {
				try {
					storeSequence(stream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public static int storeSequence(RepositoryStream stream) throws IOException {
		
		File tmpFile = File.createTempFile("sequence", FILE_SUFFIX, repositoryPath);
		FileOutputStream out = new FileOutputStream(tmpFile);
		System.out.println("Temp File: " + tmpFile.getAbsolutePath());
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(">"+stream.getFastaDescription()+"\n");
		
		/*try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			String line;
			while ((line = stream.readLine()) != null) {
				sha1.update(line.getBytes());
				writer.write(line+"\n");
			}
			writer.close();
			String filename = byteArray2Hex(sha1.digest());
			File file = getRepositoryFile(filename);
			if (tmpFile.renameTo(file)) {
				System.out.println("Storing at: " + file.getAbsolutePath());
			} else {
				System.out.println("ERROR storing at: " + file.getAbsolutePath());
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
		
		String line;
		while ((line = stream.readLine()) != null) {
			writer.write(line+"\n");
		}
		out.flush();
		writer.close();
		out.close();
		
		String filename = SequenceInfo.getAccessionNumber(stream.getFastaDescription());
		File file = getRepositoryFile(filename);
		if (tmpFile.renameTo(file)) {
			System.out.println("Storing at: [" + file.getAbsolutePath() + "]");
		} else {
			System.out.println("ERROR storing at: [" + file.getAbsolutePath() + "]");
		}
		
		
		/*if (file.exists()) {
			file.delete();
		}*/
		
		return stream.getReadCount();
	}

	private static String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    return formatter.toString();
	}
	
	public static Vector<String> getAllAccessionNumbers() {
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(FILE_SUFFIX)) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		File[] files = repositoryPath.listFiles(textFilter);
		Vector<String> fileStr = new Vector<String>();
		for (File file : files) {
			String str = file.getName();
			fileStr.add(str.substring(0, str.lastIndexOf(FILE_SUFFIX)));
		}
		return fileStr;
	}

	public static File findSequence(SequenceInfo info) {
		return findSequence(info.getAccessionNumber());
	}
	
	public static File findSequence(String accessionNumber) {
		File file = getRepositoryFile(accessionNumber);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	private static File getRepositoryFile(String accessionNumber) {
		return new File(repositoryPath, accessionNumber + FILE_SUFFIX);
	}
}
