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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class MainGUI extends JFrame {

	private static final String VERSION = "v0.0.1.0006";

	private static final String TITLE = "MASA - Alignment Viewer - " + VERSION;

	private MainController controller;
	
	private JCheckBoxMenuItem cbMenuItemGrid;
	private JCheckBoxMenuItem cbMenuItemTicks;
	private JCheckBoxMenuItem cbMenuItemLabels;


	public MainGUI(MainController controller) {
		setTitle(TITLE);
		this.controller = controller;
		getContentPane().setLayout(new BorderLayout());
		setJMenuBar(createMenus());
	}
	
	public void initialize() {
		getContentPane().add(controller.getAlignmentPlot(), BorderLayout.CENTER);
		getContentPane().add(controller.getAlignmentBar(), BorderLayout.SOUTH);
		getContentPane().add(controller.getAlignmentList(), BorderLayout.EAST);
		
		cbMenuItemGrid.setState(controller.getAlignmentPlot().isGridVisible());
		cbMenuItemTicks.setState(controller.getAlignmentPlot().isTicksVisible());
		cbMenuItemLabels.setState(controller.getAlignmentPlot().isLabelsVisible());
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	
	private JMenuBar createMenus() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Open Alignment...", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.openAlignmentFile();
			}
		});
		menu.add(menuItem);
		
		/*menuItem = new JMenuItem("Open Sequences...", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.openSequences();
			}
		});
		menu.add(menuItem);	*/	
		
		menu.addSeparator();
		submenu = new JMenu("Export");
		submenu.setMnemonic(KeyEvent.VK_E);
		menu.add(submenu);
		
		menuItem = new JMenuItem("Plot to PDF...", KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_P, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.exportPDF();
			}
		});
		submenu.add(menuItem);

		/*menuItem = new JMenuItem("Plot to SVG...", KeyEvent.VK_V);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.exportSVG();
			}
		});
		submenu.add(menuItem);*/

		/*submenu.addSeparator();		
		
		menuItem = new JMenuItem("Alignment to TXT...", KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_T, ActionEvent.ALT_MASK));
		submenu.add(menuItem);*/
		
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.exit();
			}
		});		
		menu.add(menuItem);
		
		
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);		
		menuBar.add(menu);
		
		cbMenuItemGrid = new JCheckBoxMenuItem("Grids");
		cbMenuItemGrid.setMnemonic(KeyEvent.VK_P);
		cbMenuItemGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.getAlignmentPlot().setGridVisible(cbMenuItemGrid.getState());
			}
		});	
		menu.add(cbMenuItemGrid);
		
		cbMenuItemTicks = new JCheckBoxMenuItem("Ticks");
		cbMenuItemTicks.setMnemonic(KeyEvent.VK_P);
		cbMenuItemTicks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.getAlignmentPlot().setTicksVisible(cbMenuItemTicks.getState());
			}
		});	
		menu.add(cbMenuItemTicks);
		
		cbMenuItemLabels = new JCheckBoxMenuItem("Lables");
		cbMenuItemLabels.setMnemonic(KeyEvent.VK_P);
		cbMenuItemLabels.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.getAlignmentPlot().setLabelsVisible(cbMenuItemLabels.getState());
			}
		});	
		menu.add(cbMenuItemLabels);
		
		/*
		cbMenuItem = new JCheckBoxMenuItem("Zoom Areas");
		cbMenuItem.setMnemonic(KeyEvent.VK_P);
		menu.add(cbMenuItem);
		
		cbMenuItem = new JCheckBoxMenuItem("Selection");
		cbMenuItem.setMnemonic(KeyEvent.VK_P);
		menu.add(cbMenuItem);*/

		return menuBar;
	}

	public void setTitleFile(String name) {
		if (name == null) {
			setTitle(TITLE);
		} else {
			setTitle(TITLE + "[" + name + "]");
		}
		
	}
}
