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
package br.unb.cic.av;

import java.io.File;
import java.io.IOException;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.AlignmentBinaryFile;
import br.unb.cic.av.gui.MainController;
import br.unb.cic.av.renderer.PlotRenderer;

/**
 * Startup Class
 * 
 * @author edans
 */
public class Main {

	/**
	 * Main method.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			for (String fileIn : args) {
				String fileOut = fileIn + "_.pdf";
				Alignment alignment = AlignmentBinaryFile.read(new File(fileIn));
				PlotRenderer renderer = new PlotRenderer();
				renderer.setAlignment(alignment);
				renderer.savePdf(new File(fileOut));
				System.out.println("Created alignment plot: " + fileOut);
			}
		} else {
			new MainController();
		}
	}

}
