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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import br.unb.cic.av.alignment.Sequence;

public class NCBIFetcher {

	public static void fetchStr(String id) {
		try {
			BufferedReader rd = fetchStream(id);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			System.out.println(sb.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedReader fetchStream(String id) {
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;

		URL serverAddress = null;

		try {
			String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";
			String db = "nucleotide";
			String fullUrl = baseUrl + "/efetch.cgi?db=" + db + "&id=" + id
					+ "&rettype=fasta&retmode=text";
			serverAddress = new URL(fullUrl);
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();
			System.out.println(connection.getContentLength());

			// get the output stream writer and write the output to the server
			// not needed in this example
			// wr = new OutputStreamWriter(connection.getOutputStream());
			// wr.write("");
			// wr.flush();

			// read the result from the server
			return new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * finally { //close the connection, set all objects to null
		 * connection.disconnect(); rd = null; sb = null; wr = null; connection
		 * = null; }
		 */
		return null;
	}
}
