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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.unb.cic.av.alignment.AlginmentUtils;
import br.unb.cic.av.alignment.Sequence;
import br.unb.cic.av.alignment.SequenceData;
import br.unb.cic.av.alignment.SequenceInfo;
import br.unb.cic.av.alignment.SequenceModifiers;
import br.unb.cic.av.repository.LocalFileRespositoryStream;
import br.unb.cic.av.repository.LocalRepository;
import br.unb.cic.av.repository.NCBIRespositoryStream;
import br.unb.cic.av.repository.RepositoryStream;

public class SequencePanel extends JPanel {

	private SequenceInfo info;

	private static Image IMAGE_GLOBE;
	private static Image IMAGE_OK;
	private static Image IMAGE_NOT_OK;
	private static Image FOLDER_GLOBE;
	private JLabel iconLabel;
	private JComboBox localRepositoryCombo;
	private JButton importButton;
	private JLabel importStatusLabel;

	static {
		try {
			IMAGE_OK = ImageIO.read(SequencePanel.class
					.getResourceAsStream("/ok.png"));
			IMAGE_NOT_OK = ImageIO.read(SequencePanel.class
					.getResourceAsStream("/not_ok.png"));
			IMAGE_GLOBE = ImageIO.read(SequencePanel.class
					.getResourceAsStream("/globe.png"));
			FOLDER_GLOBE = ImageIO.read(SequencePanel.class
					.getResourceAsStream("/hd.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SequencePanel(SequenceInfo info) {
		this.info = info;
		//setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		iconLabel = new JLabel();
		iconLabel.setHorizontalAlignment(JLabel.CENTER);
		iconLabel.setMinimumSize(new Dimension(80,80));		
		iconLabel.setPreferredSize(new Dimension(80,80));		
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		add(iconLabel, c);		
		
		c.gridheight = 1;
		
		c.anchor = GridBagConstraints.LINE_START;
		//c.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = getInfoLabel(info);
		c.gridx = 1;
		c.gridy = 0;
		add(label, c);

		
		
		final JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		localRepositoryCombo = new JComboBox();
		updateLocalRepository();
		
		importButton = new JButton("Import...");
		importStatusLabel = new JLabel("");
		//importStatusLabel.setPreferredSize(new Dimension(100,15));

		
		actionPanel.add(new JLabel("Repository: "));
		actionPanel.add(localRepositoryCombo);
		actionPanel.add(importButton);
		actionPanel.add(importStatusLabel);
		
		localRepositoryCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Action PErformne");
				updateIconLabel();
			}
		});
		importButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (localRepositoryCombo.getSelectedIndex() == 0) {
					importFromNCBI();
				} else if (localRepositoryCombo.getSelectedIndex() == 1) {
					importFromLocalFile();
				}
			}
		});
		updateIconLabel();

		c.gridx = 1;
		c.gridy = 1;
		add(actionPanel, c);
	}

	public File getSequenceFile() throws IOException {
		File file = null;
		int selectedOptionPanel = localRepositoryCombo.getSelectedIndex();
		if (selectedOptionPanel >= 3) {
			file = LocalRepository.findSequence(info);
		}
		return file;
	}
	
	private void updateLocalRepository() {
		Vector<String> accessionNumbers = LocalRepository
				.getAllAccessionNumbers();
		accessionNumbers.add(0, "--- Import from NCBI");
		accessionNumbers.add(1, "--- Import local file");
		accessionNumbers.add(2, "--- Empty Sequence");
		localRepositoryCombo.setModel(new DefaultComboBoxModel(accessionNumbers));
		localRepositoryCombo.setSelectedIndex(2); //Empty Sequence
		File file = LocalRepository.findSequence(info);
		if (file != null) {
			localRepositoryCombo.setSelectedItem(info.getAccessionNumber());
		}
	}
	
	private void updateIconLabel() {
		Image image = IMAGE_NOT_OK;
		int selectedOptionPanel = localRepositoryCombo.getSelectedIndex();
		if (selectedOptionPanel >= 3) {
			String str = localRepositoryCombo.getSelectedItem().toString();
			System.out.println(str);
			if (info.getAccessionNumber().equals(str)) {
				image = IMAGE_OK;
				importStatusLabel.setText("Found");
			} else {
				importStatusLabel.setText("<html><font color=red>Sequence Mismatch!</font></html>");
			}
		} else if (selectedOptionPanel == 0) {
			image = IMAGE_GLOBE;
			if (LocalRepository.findSequence(info) != null) {
				importStatusLabel.setText("");
			} else {
				importStatusLabel.setText("<html><font color=red>Sequence Missing</font></html>");
			}
		} else if (selectedOptionPanel == 1) {
			image = FOLDER_GLOBE;
			if (LocalRepository.findSequence(info) != null) {
				importStatusLabel.setText("");
			} else {
				importStatusLabel.setText("<html><font color=red>Sequence Missing</font></html>");
			}
		} else if (selectedOptionPanel == 2) {
			image = IMAGE_NOT_OK;
			importStatusLabel.setText("");
		}
		iconLabel.setIcon(new ImageIcon(image));
		iconLabel.revalidate();
		iconLabel.repaint();
		importButton.setVisible(selectedOptionPanel < 2);
	}

	private JLabel getInfoLabel(SequenceInfo info) {
		// Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

		String html = "<html>" + "<table>"
				+ "<tr><td colspan=\"3\"><b>Name</b>: <font color=\"#0000C0\">"
				+ info.getDescription() + "</font></td></tr>" 
				+ "<tr>"
				+ "<td><b>Access#</b>: <font color=\"#0000C0\">"
				+ info.getAccessionNumber() + "</font></td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td><b>Length</b>: <font color=\"#0000C0\">"
				+ info.getSize() + "</font></td>"
				+ "<td><b>Hash</b>: <font color=\"#0000C0\">" + info.getHash()
				+ "</font></td>" + "</tr>" + "</table>" + "</html>";
		System.out.println(html);
		JLabel label = new JLabel(html);
		// label.setFont(font);
		return label;
	}


	
	private void importFromNCBI() {
		try {
			final NCBIRespositoryStream stream = new NCBIRespositoryStream(
					info.getAccessionNumber());
			LocalRepository.storeSequenceAsync(stream);
			monitorDownload(stream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void importFromLocalFile() {
		JFileChooser fc = new JFileChooser();

		fc.showOpenDialog(this);
		File sequenceFile = fc.getSelectedFile();
		try {
			if (sequenceFile != null) {
				final LocalFileRespositoryStream stream 
						= new LocalFileRespositoryStream(sequenceFile);
				LocalRepository.storeSequenceAsync(stream);
				monitorDownload(stream);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}		
	}
	
	private void monitorDownload(final RepositoryStream stream) {
		new Thread() {
			@Override
			public void run() {
				try {
					while (!stream.isDone()) {
						int downloadLength = stream.getReadCount();
						//System.out.println("" + stream.getReadCount());
						importStatusLabel.setText(
								AlginmentUtils.formatSequenceSize(downloadLength));
						SequencePanel.this.revalidate();
						Thread.sleep(500);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateLocalRepository();
				updateIconLabel();
			}
		}.start();
	}


	

}
