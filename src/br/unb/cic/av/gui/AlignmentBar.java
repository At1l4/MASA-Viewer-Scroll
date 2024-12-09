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
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.renderer.BarRenderer;

/**
 * This panel presents for each sequence a bar with the gaps distributed along
 * the sequence. The user may select one sub alignment using drag-n-drop
 * operations.
 * 
 * @author edans
 */
@SuppressWarnings("serial")
public class AlignmentBar extends JPanel {
	/**
	 * Object responsible for the bar gaps rendering.
	 */
	private BarRenderer renderer;

	/**
	 * The controller that receives the GUI callbacks.
	 */
	private MainController controller;

	/**
	 * Class used to handle the mouse events.
	 */
	private class MyMouseMotionListener extends MouseAdapter implements
			MouseMotionListener {
		/* anchor position of the drag-n-drop */
		private int x0;

		@Override
		public void mouseDragged(MouseEvent ev) {
			/* Changes the selection during the drag-n-drop */
			int x1 = ev.getX();
			if (x0 < x1) {
				renderer.showSelection(x0, x1);
			} else {
				renderer.showSelection(x1, x0);
			}
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent ev) {
			/* Start the drag-n-drop operation */
			if (ev.getButton() == 1) {
				x0 = ev.getX();
				renderer.showSelection(x0, x0);
			}
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			/* Highlights the selected sub alignment in all the components */ 
			controller.highlightAlignment(renderer.getSelectedAlignment());
		}
	}

	/**
	 * Construct this panel and associates one controller to it.
	 * 
	 * @param controller the controller to be associated.
	 */
	public AlignmentBar(MainController controller) {
		this.controller = controller;
		setPreferredSize(new Dimension(500, 40));

		renderer = new BarRenderer();

		MyMouseMotionListener mouseListener = new MyMouseMotionListener();
		addMouseMotionListener(mouseListener);
		addMouseListener(mouseListener);
	}

	/**
	 * Defines the alignment to be shown.
	 * 
	 * @param alignment the alignment to be shown.
	 */
	public void setAlignment(Alignment alignment) {
		renderer.setAlignment(alignment);
	}

	/**
	 * Highlight a sub alignment in the bar and hides the selection.
	 * 
	 * @param subAlignment the sub alignment to be highlighted.
	 */
	public void highlightAlignment(Alignment subAlignment) {
		renderer.highlightAlignment(subAlignment);
		renderer.hideSelection();
		repaint();
	}

	/**
	 * Draws the component using the renderer.
	 */
	@Override
	public void paint(Graphics g) {
		renderer.draw((Graphics2D) g, getSize());
	}
}
