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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.renderer.PlotRenderer;


@SuppressWarnings("serial")
public class AlignmentPlot extends JPanel {
	private PlotRenderer renderer;
	private MainController controller;
	private Alignment alignment;
	
	private class MyMouseMotionListener extends MouseAdapter {
		private int x0;
		private int y0;
		@Override
		public void mouseDragged(MouseEvent ev) {
			//if (ev.getButton()==1) {
				renderer.showSelection(x0, y0, ev.getX(), ev.getY());
				repaint();
			//}
		}

		@Override
		public void mousePressed(MouseEvent ev) {
			if (ev.getButton()==1) {
				x0 = ev.getX();
				y0 = ev.getY();
			}
			System.out.println("P:" + ev.getButton() + ":" + ev.getX());
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			if (ev.getButton()==1) {
				System.out.println("R:" + ev.getButton() + ":" + ev.getX());
				controller.highlightAlignment(renderer.getSelectedAlignment());
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			float zoomAmount;
			if (e.getWheelRotation() > 0) {
				zoomAmount = 1.08f;
			} else {
				zoomAmount = 1/1.08f;
			}
			renderer.zoomXY(zoomAmount, e.getX(), e.getY());
			repaint();
		}

	}

	public AlignmentPlot(MainController controller) {
		this.controller = controller;
		setPreferredSize(new Dimension(500, 500));
		
		renderer = new PlotRenderer();
		
		MyMouseMotionListener mouseListener = new MyMouseMotionListener();
		addMouseMotionListener(mouseListener);
		addMouseListener(mouseListener);
		addMouseWheelListener(mouseListener);
	}
	
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
		renderer.setAlignment(alignment);
		renderer.unzoom();
	}
	
	public void highlightAlignment(Alignment subAlignment) {
		//getRenderer().zoom(subAlignment);
		getRenderer().highlightAlignment(subAlignment);
		getRenderer().hideSelection();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		renderer.draw((Graphics2D)g, getSize());
	}

	public PlotRenderer getRenderer() {
		return renderer;
	}

	public void setGridVisible(boolean visible) {
		renderer.setGridVisible(visible);
		repaint();
	}

	public boolean isGridVisible() {
		return renderer.isGridVisible();
	}
	
	public void setTicksVisible(boolean visible) {
		renderer.setTicksVisible(visible);
		repaint();
	}
	
	public boolean isTicksVisible() {
		return renderer.isTicksVisible();
	}
	
	public void setLabelsVisible(boolean visible) {
		renderer.setLabelsVisible(visible);
		repaint();
	}
	
	public boolean isLabelsVisible() {
		return renderer.isLabelsVisible();
	}
}
