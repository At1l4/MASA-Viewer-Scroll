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
package br.unb.cic.av.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.AlignmentBinaryFile;
import br.unb.cic.av.alignment.Sequence;
import br.unb.cic.av.alignment.SequenceData;
import br.unb.cic.av.renderer.PlotRenderer;
import br.unb.cic.av.repository.ApplicationPreferences;

/**
 * This class controls the main flow of every GUI execution.
 * 
 * @author edans
 */
public class MainController {

	/**
	 * The alignment that is currently being shown in the GUI.
	 */
	private Alignment alignment;

	/* GUI components */

	/**
	 * The list containing the alignment text
	 */
	private AlignmentList alignmentList;

	/**
	 * The 2D plot of the alignment .
	 */
	private AlignmentPlot alignmentPlot;

	/**
	 * The bars showing the gap areas of the sequences.
	 */
	private AlignmentBar alignmentBar;

	/**
	 * JFrame containing all the components.
	 */
	private MainGUI mainGui;

	/**
	 * Initializes all the GUI.
	 * 
	 * @throws IOException
	 */
	public MainController() throws IOException {
		mainGui = new MainGUI(this);

		alignmentPlot = new AlignmentPlot(this);
		alignmentBar = new AlignmentBar(this);
		alignmentList = new AlignmentList(this);

		mainGui.initialize();
	}

	/**
	 * @return the alignmentList
	 */
	public AlignmentList getAlignmentList() {
		return alignmentList;
	}

	/**
	 * @return the alignmentPlot
	 */
	public AlignmentPlot getAlignmentPlot() {
		return alignmentPlot;
	}

	/**
	 * @return the alignmentBar
	 */
	public AlignmentBar getAlignmentBar() {
		return alignmentBar;
	}

	/**
	 * Shows the dialog to select the sequences.
	 * 
	 * @throws IOException
	 */
	public void showSequenceDialog(Alignment alignment) throws IOException {
		List<Sequence> sequences = alignment.getAlignmentParams()
				.getSequences();
		SequenceDialog dialog = new SequenceDialog(sequences);
		dialog.setVisible(true);

		int i = 0;
		for (Sequence sequence : sequences) {
			File file = dialog.getSequenceFile(i++);
			if (file != null) {
				SequenceData sequenceData = new SequenceData(file,
						sequence.getModifiers());
				sequence.setData(sequenceData);
			}
		}
	}

	/**
	 * Finalizes the application.
	 */
	public void exit() {
		mainGui.dispose();
	}

	/**
	 * Shows the dialog to open an alignment file.
	 */
	public void openAlignmentFile() {
		JFileChooser fc = new JFileChooser(ApplicationPreferences.getLastDir());
		fc.showOpenDialog(mainGui);
		File alignmentFile = fc.getSelectedFile();

		try {
			if (alignmentFile != null) {
				Alignment alignment = null;
				ApplicationPreferences.setLastDir(alignmentFile.getPath());
				alignment = AlignmentBinaryFile.read(alignmentFile);
				showSequenceDialog(alignment);
				mainGui.setTitleFile(alignmentFile.getName());
				setAlignment(alignment);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Highlight a sub alignment in the GUI components. A sub alignment must be
	 * derived from the main alignment.
	 * 
	 * @param subAlignment
	 */
	public void highlightAlignment(Alignment subAlignment) {
		alignmentList.setAlignment(subAlignment);
		alignmentPlot.highlightAlignment(subAlignment);
		alignmentBar.highlightAlignment(subAlignment);
		mainGui.repaint();
	}

	/**
	 * Shows a new alignment in every component.
	 * 
	 * @param alignment the alignment to be shown.
	 */
	private void setAlignment(Alignment alignment) {
		this.alignment = alignment;

		alignmentList.setAlignment(alignment);
		alignmentPlot.setAlignment(alignment);
		alignmentBar.setAlignment(alignment);

		highlightAlignment(null);

		mainGui.repaint();
	}

	public void exportPDF() {
		if (this.alignment == null) {
			JOptionPane.showMessageDialog(mainGui, "There is no alignment opened.");
			return;
		}
		JFileChooser fc = new JFileChooser(ApplicationPreferences.getLastDir());
		fc.showSaveDialog(mainGui);
		File exportFile = fc.getSelectedFile();

		try {
			if (exportFile != null) {
				PlotRenderer renderer = alignmentPlot.getRenderer();
				renderer.savePdf(exportFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
