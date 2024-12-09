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
package br.unb.cic.av.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.Gap;

/**
 * This class is responsible to render the bar panel with a given alignment. In
 * this panel, each sequence has an horizontal bar describing the gap positions.
 * 
 * @author edans
 */
public class BarRenderer {
	/**
	 * Left drawing margin.
	 */
	private static final float MARGIN_LEFT = 5.0f;

	/**
	 * Top drawing margin.
	 */
	private static final float MARGIN_TOP = 2.0f;

	/**
	 * Right drawing margin.
	 */
	private static final float MARGIN_RIGHT = 5.0f;

	/**
	 * Bottom drawing margin.
	 */
	private static final float MARGIN_BOTTOM = 2.0f;

	/**
	 * The alignment being shown.
	 */
	private Alignment alignment;

	/**
	 * Drawing rectangle used to map the coordinates of the sequences 
	 * into the gap bars.
	 */
	private DrawRectangle[] rects = new DrawRectangle[2];

	/**
	 * Indicates if the selection is being shown.
	 */
	private boolean showSelection;

	/**
	 * The start coordinate of the selection.
	 */
	private int selectionX0;

	/**
	 * The end coordinate of the selection.
	 */
	private int selectionX1;

	/**
	 * Indicates if the highlight is being shown.
	 */
	private boolean showHighlight;

	/**
	 * The start coordinate of the highlight.
	 */
	private int highlightX0;

	/**
	 * The end coordinate of the highlight.
	 */
	private int highlightX1;

	/**
	 * Creates a new Bar Renderer.
	 */
	public BarRenderer() {
		for (int i = 0; i < rects.length; i++) {
			rects[i] = new DrawRectangle();
		}
	}

	/**
	 * Defines the alignment to be shown.
	 * 
	 * @param alignment the alignment to be shown.
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * Shows a selection mark between positions <code>x0</code> and
	 * <code>x1</code>.
	 * 
	 * @param x0 start coordinate of the selection.
	 * @param x1 end coordinate of the selection.
	 */
	public void showSelection(int x0, int x1) {
		this.selectionX0 = (int) Math.max(rects[0].x0,
				Math.min(rects[0].x1, x0));
		this.selectionX1 = (int) Math.max(rects[0].x0,
				Math.min(rects[0].x1, x1));

		showSelection = true;
	}

	/**
	 * Hides the selection mark.
	 */
	public void hideSelection() {
		showSelection = false;
	}

	/**
	 * Returns the sub alignment associated with the selection.
	 * 
	 * @return the selected sub alignment.
	 */
	public Alignment getSelectedAlignment() {
		if (alignment == null) {
			return null;
		}
		
		return alignment.truncate((int) rects[0].toJ(selectionX0),
				(int) rects[0].toJ(selectionX1));
	}

	/**
	 * Highlight a sub alignment in the bar.
	 * 
	 * @param subAlignment the sub alignment to be highlighted.
	 */
	public void highlightAlignment(Alignment subAlignment) {
		this.showHighlight = (subAlignment != null);
		if (this.showHighlight) {
			highlightX0 = (int) rects[0].toX(subAlignment
					.getSequenceStartOffset(0));
			highlightX1 = (int) rects[0].toX(subAlignment
					.getSequenceEndOffset(0));
		}
	}

	/**
	 * Draw the bar in a {@link Graphics2D} object.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the available area to the drawing.
	 */
	public void draw(Graphics2D g, Dimension dimension) {
		drawBackground(g, dimension);

		// do not show anything more if there is no defined alignment.
		if (alignment == null) {
			return;
		}

		// defines the drawing rectangles for each sequence bar
		for (int i = 0; i < 2; i++) {
			float h = dimension.height - MARGIN_BOTTOM - MARGIN_TOP;
			rects[i].x0 = MARGIN_LEFT;
			rects[i].x1 = dimension.width - MARGIN_RIGHT;
			rects[i].y0 = MARGIN_TOP + h * i / 2;
			rects[i].y1 = MARGIN_TOP + h * (i + 1) / 2;
			rects[i].j0 = alignment.getGaps(i).getStartOffset();
			rects[i].j1 = alignment.getGaps(i).getEndOffset();
		}

		// draw one gap bar for each sequence
		drawGapBars(g);

		// shows the selection
		if (showSelection) {
			drawSelection(g, dimension);
		}

		// shows the highlighted sub alignment
		if (showHighlight) {
			drawHighlightedAlignment(g);
		}

	}

	/**
	 * Fills the background with a solid color.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the dimension of the background.
	 */
	private void drawBackground(Graphics2D g, Dimension dimension) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, dimension.width, dimension.height);
	}

	/**
	 * Draws the gap bars. For each sequence, the gaps are shown as a dark
	 * vertical line. The darkness of the gaps indicates how many gaps there are
	 * in some region.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawGapBars(Graphics2D g) {
		g.setStroke(new BasicStroke(1.0f));

		// the colors of each gap bar
		Color[] colors = { Color.CYAN, Color.GREEN };
		for (int i = 0; i < 2; i++) {
			// draws the horizontal long bar with a solid color
			g.setColor(colors[i]);
			g.fillRect((int) rects[i].x0, (int) rects[i].y0,
					(int) rects[i].width(), (int) rects[i].height() + 1);

			// draws the vertical lines for each gap
			for (Gap gap : alignment.getGaps(i)) {

				// calculates the width of the gaps in drawing coordinates
				float x0 = rects[i].toX(gap.getOffset());
				float x1 = (rects[i].toX(gap.getOffset() + gap.getLength()));
				float w = x1 - x0;

				/*
				 * if the width in drawing coordinates are less than a pixel,
				 * then we use the alpha channel in order to make transparency
				 * in the gap line. Otherwise, we paint the gaps with a full
				 * solid color.
				 */
				if (w < 1) {
					g.setColor(new Color(0, 0, 0, (float) Math.pow(w, 0.75)));
					w = 1;
				} else {
					g.setColor(Color.BLACK);
				}
				g.fillRect((int) x0, (int) rects[i].y0, (int) w,
						(int) rects[i].height() + 1);
			}
		}
	}

	/**
	 * Highlights one area of the alignment.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawHighlightedAlignment(Graphics2D g) {
		g.setColor(new Color(1.0f, 0.0f, 0, 0.25f));
		g.fillRect(highlightX0, (int) rects[0].y0, highlightX1 - highlightX0,
				(int) (rects[1].y1 - rects[0].y0));
		g.setColor(new Color(1.0f, 0.0f, 0));
		g.setStroke(new BasicStroke(1.0f));
		g.drawRect(highlightX0, (int) rects[0].y0, highlightX1 - highlightX0,
				(int) (rects[1].y1 - rects[0].y0));
	}

	/**
	 * Draws the selection area with a translucent color.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the dimension of the panel.
	 */
	private void drawSelection(Graphics2D g, Dimension dimension) {
		g.setColor(new Color(1.0f, 1.0f, 0, 0.75f));
		g.setStroke(new BasicStroke(2.0f));
		g.fillRect(selectionX0, 0, selectionX1 - selectionX0, dimension.height);
	}
}
