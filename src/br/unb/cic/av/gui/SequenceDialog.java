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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.AlignmentBinaryFile;
import br.unb.cic.av.alignment.Sequence;
import br.unb.cic.av.alignment.SequenceData;
import br.unb.cic.av.alignment.SequenceInfo;
import br.unb.cic.av.alignment.SequenceModifiers;

public class SequenceDialog extends JDialog {
	private List<SequencePanel> sequencePanels = new ArrayList<SequencePanel>();


	public SequenceDialog(List<Sequence> sequences) {
		setTitle("Open Sequences");
		
		GridBagConstraints c = new GridBagConstraints();		
		setLayout(new GridBagLayout());
		
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0f;

		c.gridy = 0;
		int i = 0;
		for (Sequence sequence : sequences) {
			c.gridx = 0;
			c.fill = GridBagConstraints.NONE;
			SequencePanel panel = getSequenceInfoPanel(sequence.getInfo());
			sequencePanels.add(panel);
			getContentPane().add(panel, c);
			c.gridy++;
	
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			getContentPane().add(new JSeparator(SwingConstants.HORIZONTAL), c);
			c.gridy++;
			
			i++;
		}

		
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("   Ok   ");
		bottomPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				SequenceDialog.this.dispose();
			}
		});
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		getContentPane().add(bottomPanel, c);
		c.gridy++;
		
		//setPreferredSize(new Dimension(600,230));
		//setResizable(false);
		setModal(true);
		pack();
		setResizable(false);
	}


	private SequencePanel getSequenceInfoPanel(SequenceInfo info) {
		return new SequencePanel(info);
	}
	
	public File getSequenceFile(int index) throws IOException {
		return sequencePanels.get(index).getSequenceFile();
	}

}
