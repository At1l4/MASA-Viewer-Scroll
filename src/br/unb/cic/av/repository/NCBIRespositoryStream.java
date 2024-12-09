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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NCBIRespositoryStream extends RepositoryStream {

	public NCBIRespositoryStream(String accessionNumber) throws IOException {
		super(getFetchStream(accessionNumber));
	}
	
	public static InputStream getFetchStream(String id) {
	      HttpURLConnection connection = null;
	    
	      URL serverAddress = null;
	    
	      try {
	    	  String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";
	    	  String db = "nucleotide";
	    	  String fullUrl = baseUrl + "/efetch.cgi?db="+db+"&id="+id+"&rettype=fasta&retmode=text";
	          serverAddress = new URL(fullUrl);
	          //set up out communications stuff
	          connection = null;
	        
	          //Set up the initial connection
	          connection = (HttpURLConnection)serverAddress.openConnection();
	          connection.setRequestMethod("GET");
	          connection.setDoOutput(true);
	          connection.setReadTimeout(10000);
	                    
	          connection.connect();
	          System.out.println(connection.getContentLength());

	          return connection.getInputStream();
	      } catch (MalformedURLException e) {
	          e.printStackTrace();
	      } catch (ProtocolException e) {
	          e.printStackTrace();
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      return null;
	}
		
}
