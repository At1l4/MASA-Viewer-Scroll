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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.GapSequence;
import br.unb.cic.av.alignment.SequenceModifiers;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This class is responsible to render the 2D plot with a given alignment. In
 * this panel, the alignment is draw as a line path across the 2D matrix. The
 * gaps are shown as a vertical or horizontal lines inside the path.
 * 
 * @author edans
 */
public class PlotRenderer {
	/* Drawing constants */

	/**
	 * The default preferred size of the drawing
	 */
	private static final float IMAGE_SIZE = 500.0f;

	/**
	 * Minimum distance between two major ticks
	 */
	private static final float MIN_TICK_STEP = 5.0f;

	/**
	 * Left drawing margin.
	 */
	private static final float MARGIN_LEFT = 50.0f;

	/**
	 * Top drawing margin.
	 */
	private static final float MARGIN_TOP = 50.0f;

	/**
	 * Right drawing margin.
	 */
	private static final float MARGIN_RIGHT = 50.0f;

	/**
	 * Bottom drawing margin.
	 */
	private static final float MARGIN_BOTTOM = 50.0f;

	/**
	 * The height of the sequence labels around the grid.
	 */
	private static final float ACCESSION_TEXT_HEIGHT = 20.0f;

	/**
	 * The height of the sequence labels around the grid.
	 */
	private static final float TICK_TEXT_HEIGHT = 14.0f;

	/**
	 * The alignment being shown.
	 */
	private Alignment alignment;

	/**
	 * Drawing rectangle used to map the coordinates of the sequences into the
	 * 2D Matrix.
	 */
	private DrawRectangle rect = new DrawRectangle();

	/**
	 * Indicates if the selection is being shown.
	 */
	private boolean showSelection;

	/**
	 * The start coordinate of the selection considering the x-axis (seq[1]).
	 */
	private int selectionX0;

	/**
	 * The end coordinate of the selection considering the x-axis (seq[1]).
	 */
	private int selectionX1;

	/**
	 * The start coordinate of the selection considering the y-axis (seq[0]).
	 */
	private int selectionY0;

	/**
	 * The end coordinate of the selection considering the y-axis (seq[0]).
	 */
	private int selectionY1;

	/**
	 * Indicates if the highlight is being shown.
	 */
	private boolean showHighlight;

	/**
	 * The start coordinate of the highlight considering the y-axis (seq[1]).
	 */
	private int highlightI0;

	/**
	 * The end coordinate of the highlight considering the y-axis (seq[1]).
	 */
	private int highlightI1;

	/**
	 * The start coordinate of the highlight considering the x-axis (seq[0]).
	 */
	private int highlightJ0;

	/**
	 * The end coordinate of the highlight considering the x-axis (seq[0]).
	 */
	private int highlightJ1;

	/**
	 * The first i-coordinate shown in the y-axis (seq[0]).
	 */
	private int viewportI0;

	/**
	 * The last i-coordinate shown in the y-axis (seq[0]).
	 */
	private int viewportI1;

	/**
	 * The first j-coordinate shown in the x-axis (seq[1]).
	 */
	private int viewportJ0;

	/**
	 * The last j-coordinate shown in the x-axis (seq[1]).
	 */
	private int viewportJ1;

	/**
	 * The minimum block result.
	 */
	private int minBlockResult;

	/**
	 * The maximum block result.
	 */
	private int maxBlockResult;

	/**
	 * Defines if the grid is visible.
	 */
	private boolean gridVisible = false;
	
	/**
	 * Defines if the ticks are visible.
	 */
	private boolean ticksVisible = true;
	
	/**
	 * Defines if the sequence labels are visible.
	 */
	private boolean labelsVisible = false;
	
	/**
	 * The maximum block result.
	 */
	private Color[][] gridColors;

	
	/**
	 * Initializes the structures of the renderer with default values.
	 */
	public PlotRenderer() {
		viewportJ0 = 0;
		viewportJ1 = 5000;
		viewportI0 = 0;
		viewportI1 = 5000;
	}

	/**
	 * Defines the alignment to be shown and zoom out in order to show all the
	 * 2D matrix.
	 * 
	 * @param alignment the alignment to be shown.
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
		if (alignment.getBlocks() != null) {
			processBlockResults();
		}
		unzoom();
	}

	/**
	 * Defines if the grid is visible
	 * @param state visibility state
	 */
	public void setGridVisible(boolean visible) {
		gridVisible = visible;
	}

	/**
	 * @return the grid visibility state.
	 */
	public boolean isGridVisible() {
		return gridVisible;
	}
	
	/**
	 * Defines if the ticks are visible
	 * @param state visibility state
	 */
	public void setTicksVisible(boolean visible) {
		ticksVisible = visible;
	}

	/**
	 * @return the ticks visibility state.
	 */
	public boolean isTicksVisible() {
		return ticksVisible;
	}

	/**
	 * Defines if the lables are visible
	 * @param state visibility state
	 */
	public void setLabelsVisible(boolean labelsVisible) {
		this.labelsVisible = labelsVisible;
	}

	/**
	 * @return the lables visibility state.
	 */
	public boolean isLabelsVisible() {
		return labelsVisible;
	}
	
	/**
	 * Shows a selection rectangle in the grid.
	 * 
	 * @param x0 coordinate x0.
	 * @param x1 coordinate x1.
	 * @param y0 coordinate y0.
	 * @param y1 coordinate y1.
	 */
	public void showSelection(int x0, int y0, int x1, int y1) {
		if (x0 > x1) {
			int aux = x0;
			x0 = x1;
			x1 = aux;
		}
		if (y0 > y1) {
			int aux = y0;
			y0 = y1;
			y1 = aux;
		}

		this.selectionX0 = (int) Math.max(rect.x0, Math.min(rect.x1, x0));
		this.selectionX1 = (int) Math.max(rect.x0, Math.min(rect.x1, x1));
		this.selectionY0 = (int) Math.max(rect.y0, Math.min(rect.y1, y0));
		this.selectionY1 = (int) Math.max(rect.y0, Math.min(rect.y1, y1));

		showSelection = true;
	}

	/**
	 * Hides the selection rectangle.
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

		/* Retrieving the offsets of the selection */
		int offsetY0 = alignment.getSequenceOffset(0,
				(int) rect.toI(selectionY0));
		int offsetY1 = alignment.getSequenceOffset(0,
				(int) rect.toI(selectionY1));
		int offsetX0 = alignment.getSequenceOffset(1,
				(int) rect.toJ(selectionX0));
		int offsetX1 = alignment.getSequenceOffset(1,
				(int) rect.toJ(selectionX1));

		/* Asserting that offsetX0 < offsetX1 */
		if (offsetX0 > offsetX1) {
			int tmp = offsetX0;
			offsetX0 = offsetX1;
			offsetX1 = tmp;
		}
		/* Asserting that offsetY0 < offsetY1 */
		if (offsetY0 > offsetY1) {
			int tmp = offsetY0;
			offsetY0 = offsetY1;
			offsetY1 = tmp;
		}

		/* Intersecting the selected rectangle with the alignment */
		int offset0 = Math.max(offsetY0, offsetX0);
		int offset1 = Math.min(offsetY1, offsetX1);
		if (offset0 < 0) {
			offset0 = 0;
		}
		if (offset1 < 0) {
			offset1 = 0;
		}
		
		if (offset0 > offset1) {
			return null;
		}

		/* Returning a new alignment truncated between two offsets */
		return alignment.truncate(offset0, offset1);
	}

	/**
	 * Highlight a sub alignment in the grid.
	 * 
	 * @param subAlignment the sub alignment to be highlighted.
	 */
	public void highlightAlignment(Alignment subAlignment) {
		this.showHighlight = (subAlignment != null);
		if (this.showHighlight) {
			/* Retrieving sequence coordinates of the sub alignment */
			highlightJ0 = subAlignment.getSequenceStartPosition(1);
			highlightJ1 = subAlignment.getSequenceEndPosition(1);
			highlightI0 = subAlignment.getSequenceStartPosition(0);
			highlightI1 = subAlignment.getSequenceEndPosition(0);
		}
	}

	/**
	 * Defines the viewport with the first and last sequence positions shown in
	 * the 2D matrix.
	 * 
	 * @param i0 the first i-coordinate
	 * @param j0 the first j-coordinate
	 * @param i1 the last i-coordinate
	 * @param j1 the last j-coordinate
	 */
	public void setViewport(int i0, int j0, int i1, int j1) {
		this.viewportI0 = i0;
		this.viewportJ0 = j0;
		this.viewportI1 = i1;
		this.viewportJ1 = j1;

		if (viewportI0 > viewportI1) {
			int tmp = viewportI0;
			viewportI0 = viewportI1;
			viewportI1 = tmp;
		}
		if (viewportJ0 > viewportJ1) {
			int tmp = viewportJ0;
			viewportJ0 = viewportJ1;
			viewportJ1 = tmp;
		}
	}

	/**
	 * Unzoom the viewport to show all the sequences.
	 */
	public void unzoom() {
		if (alignment != null) {
			viewportI0 = 0;
			viewportJ0 = 0;
			viewportI1 = alignment.getAlignmentParams()
					.getSequence(0).getInfo().getSize();
			viewportJ1 = alignment.getAlignmentParams()
					.getSequence(1).getInfo().getSize();
		} else {
			viewportI0 = 0;
			viewportJ0 = 0;
			viewportI1 = 50000;
			viewportJ1 = 50000;
		}
	}

	/**
	 * Zoom the viewport to the direction of the (x,y) coordinates.
	 * 
	 * @param zoomAmount the amount to zoom. If this value is greater than 1,
	 *            than the zoom will decrease, otherwise it will increase. The
	 *            zoom amount is a float number, and it represents the
	 *            percentage of zoom. For example, if the amount is 0.5 the zoom
	 *            will increase 50%.
	 * @param x the zooming x-coordinate.
	 * @param y the zooming y-coordinate.
	 */
	public void zoomXY(float zoomAmount, int x, int y) {
		int i = (int) rect.toI(y);
		int j = (int) rect.toJ(x);

		zoom(zoomAmount, i, j);
	}

	/**
	 * Zooms to the center coordinate. See {@link #zoomXY(float, int, int)}.
	 * 
	 * @param zoomAmount the amount to zoom.
	 */
	public void zoomCenter(float zoomAmount) {
		int ic = (viewportI0 + viewportI1) / 2;
		int jc = (viewportJ0 + viewportJ1) / 2;
		zoom(zoomAmount, ic, jc);
	}

	/**
	 * Saves the rendered plot to a pdf file.
	 * 
	 * @param filename the file to save the plot.
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public void savePdf(File filename) throws IOException {
		try {
			Dimension dimension = getIdealDimension();
			//dimension.width *= 2;
			//dimension.height *= 2;

			Document document = new Document(new Rectangle(dimension.width,
					dimension.height));
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(filename));
			document.open();
			PdfContentByte canvas = writer.getDirectContent();
			PdfGraphics2D g2 = new PdfGraphics2D(canvas, dimension.width,
					dimension.height);
			draw(g2, dimension);
			g2.dispose();
			document.close();
			System.out.println("Done pdf");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Draws the plot. The alignment is shown as a line path across the 2D
	 * matrix.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the available area to the drawing.
	 */
	public void draw(Graphics2D g, Dimension dimension) {
		// defines the drawing rectangle for mapping coordinates
		rect.x0 = MARGIN_LEFT;
		rect.x1 = dimension.width - MARGIN_RIGHT;
		rect.y0 = MARGIN_TOP;
		rect.y1 = dimension.height - MARGIN_BOTTOM;
		rect.i0 = viewportI0;
		rect.i1 = viewportI1;
		rect.j0 = viewportJ0;
		rect.j1 = viewportJ1;

		// draw the background with a solid color
		drawBackground(g, dimension);

		// draw the alignment if it is defined
		if (alignment != null) {
			// draw block results if it exists
			if (alignment.getBlocks() != null) {
				drawBlocks(g);
			}

			// draw the alignment with a line path
			drawAlignmentPath(g);

			// draw the accession numbers of the sequences
			if (labelsVisible) {
				drawAccessionNumbers(g);
			}

			// draw the selection
			if (showSelection) {
				drawSelection(g);
			}

			// shows the highlighted sub alignment
			if (showHighlight) {
				drawHighlightedAlignment(g);
			}
		}

		// draw the border and the ticks
		drawBorder(g);
		drawTicks(g);
	}

	/**
	 * Draws the background with a solid color.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the available area to the drawing.
	 */
	private void drawBackground(Graphics2D g, Dimension dimension) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, dimension.width, dimension.height);
	}

	/**
	 * Draws the selection area with a translucent color.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 * @param dimension the dimension of the panel.
	 */
	private void drawSelection(Graphics2D g) {
		g.setColor(new Color(1.0f, 1.0f, 0, 0.75f));

		g.fillRect(selectionX0, selectionY0, selectionX1 - selectionX0,
				selectionY1 - selectionY0);
	}

	/**
	 * Highlights one area of the alignment.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawHighlightedAlignment(Graphics2D g) {
		g.setClip((int) rect.x0, (int) rect.y0, (int) (rect.x1 - rect.x0),
				(int) (rect.y1 - rect.y0));

		/* Retrieving drawing coordinates of the sub alignment */
		int highlightX0 = (int) rect.toX(highlightJ0);
		int highlightX1 = (int) rect.toX(highlightJ1);
		int highlightY0 = (int) rect.toY(highlightI0);
		int highlightY1 = (int) rect.toY(highlightI1);

		/* Asserting that highlightX0 < highlightX1 */
		if (highlightX0 > highlightX1) {
			int tmp = highlightX0;
			highlightX0 = highlightX1;
			highlightX1 = tmp;
		}

		/* Asserting that highlightY0 < highlightY1 */
		if (highlightY0 > highlightY1) {
			int tmp = highlightY0;
			highlightY0 = highlightY1;
			highlightY1 = tmp;
		}

		g.setColor(new Color(1.0f, 0.0f, 0, 0.15f));
		g.fillRect(highlightX0, highlightY0, highlightX1 - highlightX0,
				highlightY1 - highlightY0);
		g.setColor(new Color(1.0f, 0.0f, 0));
		g.setStroke(new BasicStroke(1.0f));
		g.drawRect(highlightX0, highlightY0, highlightX1 - highlightX0,
				highlightY1 - highlightY0);

		g.setClip(null);
	}

	/**
	 * Draw the block results in the background.
	 * 
	 * @param graphics the {@link Graphics2D} object to be drawn.
	 */
	private void drawBlocks(Graphics2D graphics) {
		graphics.setClip((int) rect.x0, (int) rect.y0, (int) (rect.x1 - rect.x0),
				(int) (rect.y1 - rect.y0));
		int[][] blocks = alignment.getBlocks();
		int h = blocks.length;
		int w = blocks[0].length;

		// Determines the direction of each sequence
		int dir_i = alignment.getSequenceDirection(0);
		int dir_j = alignment.getSequenceDirection(1);

		// Determines the dimensions of each block
		SequenceModifiers seqModifier0 = alignment.getAlignmentParams()
				.getSequence(0).getModifiers();
		SequenceModifiers seqModifier1 = alignment.getAlignmentParams()
				.getSequence(1).getModifiers();
		float bh = ((float) (seqModifier0.getTrimEnd()
				- seqModifier0.getTrimStart() + 1)) / h;
		float bw = ((float) (seqModifier1.getTrimEnd()
				- seqModifier1.getTrimStart() + 1)) / w;

		// Determines the blocks i0..i1,j0..j1 that are visible in the viewport
		int i0 = (int) (viewportI0 / bh);
		int i1 = (int) (viewportI1 / bh);
		int j0 = (int) (viewportJ0 / bw);
		int j1 = (int) (viewportJ1 / bw);
		if (i0 > i1) {
			int tmp = i0;
			i0 = i1;
			i1 = tmp;
		}
		if (j0 > j1) {
			int tmp = j0;
			j0 = j1;
			j1 = tmp;
		}
		if (i1 > h - 1) {
			i1 = h - 1;
		}
		if (j1 > w - 1) {
			j1 = w - 1;
		}

		// draws each visible block
		for (int i = i0; i <= i1; i++) {
			for (int j = j0; j <= j1; j++) {
				/*int bi = (dir_i > 0) ? i : (h - i - 1);
				int bj = (dir_j > 0) ? j : (w - j - 1);

				int val = blocks[bi][bj];
				if (val != Integer.MIN_VALUE) {
					float c = (val-minBlockResult) / ((maxBlockResult-minBlockResult) * 0.9f);
					// float c = b/(200*bh);
					c = (float) Math.pow(c, 0.5f);
					if (c > 1) {
						c = 1;
					}
					float r = 0.5f + c * 0.5f;
					float g = 0.8f - c * 0.6f;
					float b = 0.7f - c * 0.7f; 
					if (val < 0) {
						b += 0.3f;
						r -= 0.5f;
					}
					
					graphics.setColor(new Color(r, g, b));
				} else {
					graphics.setColor(Color.BLACK);
				}*/
				graphics.setColor(gridColors[i][j]);

				int px0 = (int) rect.toX(j * bw);
				int py0 = (int) rect.toY(i * bh);
				int px1 = (int) rect.toX((j + 1) * bw);
				int py1 = (int) rect.toY((i + 1) * bh);
				graphics.fillRect(px0, py0, px1 - px0, py1 - py0);
			}
		}
		graphics.setClip(null);
	}

	/**
	 * Draw the borders of the grid.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawBorder(Graphics2D g) {
		g.setStroke(new BasicStroke(1.0f));
		g.setColor(Color.BLACK);
		g.drawRect((int) rect.x0, (int) rect.y0, (int) rect.width(),
				(int) rect.height());
	}

	/**
	 * Draw the alignment.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawAlignmentPath(Graphics2D g) {

		float width = rect.toX(alignment.getSequenceEndPosition(1))
				- rect.toX(alignment.getSequenceStartPosition(1));
		float height = rect.toX(alignment.getSequenceEndPosition(0))
				- rect.toX(alignment.getSequenceStartPosition(0));
		float area = width * height;
		if (area < 10 * 10) {
			g.setColor(Color.BLUE);
			g.drawRect(
					(int) (rect.toX(alignment.getSequenceStartPosition(1)) - 2),
					(int) (rect.toY(alignment.getSequenceStartPosition(0)) - 2),
					(int) (width + 5), (int) (height + 5));
		}

		g.setClip((int) rect.x0, (int) rect.y0, (int) (rect.x1 - rect.x0),
				(int) (rect.y1 - rect.y0));

		g.setStroke(new BasicStroke(0.5f));
		g.setColor(Color.BLUE);
		GeneralPath path = new GeneralPath();

		/*int i0 = Math.min(alignment.getSequenceStartPosition(0),
				alignment.getSequenceEndPosition(0));
		int j0 = Math.min(alignment.getSequenceStartPosition(1),
				alignment.getSequenceEndPosition(1));*/
		int i0 = alignment.getSequenceStartPosition(0);
		int j0 = alignment.getSequenceStartPosition(1);

		path.moveTo((int) rect.toX(j0), (int) rect.toY(i0));
		float lastX = 0;
		float lastY = 0;
		for (GapSequence gap : alignment.getGapSequences()) {
			if (gap.getI0() > rect.i1 || gap.getJ0() > rect.j1)
				continue;
			if (gap.getI1() < rect.i0 || gap.getJ1() < rect.j0)
				continue;
			float cx0 = rect.toX(gap.getJ0());
			float cy0 = rect.toY(gap.getI0());
			float cx1 = rect.toX(gap.getJ1());
			float cy1 = rect.toY(gap.getI1());

			if (cx1 - lastX >= 1.0f || cy1 - lastY >= 1.0f) {
				path.lineTo((int) cx0, (int) cy0);
				path.lineTo((int) cx1, (int) cy1);
				lastX = cx1;
				lastY = cy1;
			}
		}
		/*int i1 = Math.max(alignment.getSequenceStartPosition(0),
				alignment.getSequenceEndPosition(0));
		int j1 = Math.max(alignment.getSequenceStartPosition(1),
				alignment.getSequenceEndPosition(1));*/
		int i1 = alignment.getSequenceEndPosition(0);
		int j1 = alignment.getSequenceEndPosition(1);

		path.lineTo((int) rect.toX(j1), (int) rect.toY(i1));
		g.draw(path);
		g.setClip(null);
	}

	/**
	 * Draw the tick marks in the border of the grid.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawTicks(Graphics2D g) {
		g.setStroke(new BasicStroke(0.8f));
		g.setColor(Color.BLACK);

		float diff_i = rect.i1 - rect.i0;
		float diff_j = rect.j1 - rect.j0;

		int step_ticks_i = (int) Math.pow(10,
				(int) (Math.log10((int) (diff_i / 10))));
		int step_ticks_j = (int) Math.pow(10,
				(int) (Math.log10((int) (diff_j / 10))));
		if (step_ticks_i < 10) {
			step_ticks_i = 10;
		}
		if (step_ticks_j < 10) {
			step_ticks_j = 10;
		}
		if ((rect.toX(step_ticks_j) - rect.toX(0)) / 10 < MIN_TICK_STEP) {
			step_ticks_j *= 10;
		}
		if ((rect.toY(step_ticks_i) - rect.toY(0)) / 10 < MIN_TICK_STEP) {
			step_ticks_i *= 10;
		}

		int small_ticks;
		if (diff_i / step_ticks_i > 5) {
			small_ticks = 5;
		} else {
			small_ticks = 10;
		}

		int tick_i0 = (int) (Math.floor(rect.i0 / (step_ticks_i / small_ticks)
				+ 1) * (step_ticks_i / small_ticks));
		int tick_j0 = (int) (Math.floor(rect.j0 / (step_ticks_j / small_ticks)
				+ 1) * (step_ticks_j / small_ticks));

		if (ticksVisible) {
			for (int i = tick_i0; i <= rect.i1; i += (int) (step_ticks_i / small_ticks)) {
				float len = (i % (step_ticks_i)) == 0 ? ACCESSION_TEXT_HEIGHT
						: ACCESSION_TEXT_HEIGHT / 2;
				g.drawLine((int) (rect.x1 - len), (int) (rect.toY(i)),
						(int) (rect.x1), (int) (rect.toY(i)));
				g.drawLine((int) (rect.x0), (int) (rect.toY(i)),
						(int) (rect.x0 + len), (int) (rect.toY(i)));
			}
			for (int j = tick_j0; j <= rect.j1; j += (int) (step_ticks_j / small_ticks)) {
				float len = (j % (step_ticks_j)) == 0 ? ACCESSION_TEXT_HEIGHT
						: ACCESSION_TEXT_HEIGHT / 2;
				g.drawLine((int) (rect.toX(j)), (int) (rect.y1),
						(int) (rect.toX(j)), (int) (rect.y1 - len));
				g.drawLine((int) (rect.toX(j)), (int) (rect.y0),
						(int) (rect.toX(j)), (int) (rect.y0 + len));
			}
		}

		if (gridVisible) {
			g.setStroke(new BasicStroke(0.1f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER, 10.0f, new float[] { 2.0f, 4.0f }, 0.0f));
			tick_i0 = (int) (Math.floor(rect.i0 / (step_ticks_i) + 1) * (step_ticks_i));
			tick_j0 = (int) (Math.floor(rect.j0 / (step_ticks_j) + 1) * (step_ticks_j));
			for (int i = tick_i0; i <= rect.i1; i += step_ticks_i) {
				g.drawLine((int) (rect.x0), (int) (rect.toY(i)), (int) (rect.x1),
						(int) (rect.toY(i)));
			}
			for (int j = tick_j0; j <= rect.j1; j += step_ticks_j) {
				g.drawLine((int) (rect.toX(j)), (int) (rect.y0),
						(int) (rect.toX(j)), (int) (rect.y1));
			}
		}
		
		g.setColor(Color.BLACK);

		if (labelsVisible) {
			g.setFont(g.getFont().deriveFont(TICK_TEXT_HEIGHT));
			String text;
			Font oldFont = g.getFont();
			FontMetrics fm = g.getFontMetrics();
			int textHeight = fm.getAscent();
			int textLeading = textHeight / 3;
	
			text = String.format("%.0f", rect.j0);
			g.drawString(text, rect.x0, rect.y0 - textLeading);
			text = String.format("%.0f", rect.j1);
			g.drawString(text, rect.x1 - fm.stringWidth(text), rect.y0
					- textLeading);
	
			AffineTransform at = AffineTransform.getRotateInstance(Math.PI / 2);
			g.setFont(oldFont.deriveFont(at));
	
			text = String.format("%.0f", rect.i0);
			g.drawString(text, rect.x0 - textHeight, rect.y0);
	
			text = String.format("%.0f", rect.i1);
			g.drawString(text, rect.x0 - textHeight, rect.y1 - fm.stringWidth(text));
			
			g.setFont(oldFont);
		}

	}

	/**
	 * Draw the accession number of the sequences in the border of the matrix.
	 * 
	 * @param g the {@link Graphics2D} object to be drawn.
	 */
	private void drawAccessionNumbers(Graphics2D g) {
		g.setFont(g.getFont().deriveFont(ACCESSION_TEXT_HEIGHT));

		String text;
		Font oldFont = g.getFont();
		FontMetrics fm = g.getFontMetrics();
		int textHeight = fm.getAscent();

		text = String.format("%s", alignment.getAlignmentParams()
				.getSequence(1).getInfo().getAccessionNumber());
		g.drawString(text, rect.x0 + (rect.width() - fm.stringWidth(text)) / 2,
				rect.y0 - textHeight / 3);

		AffineTransform at = AffineTransform.getRotateInstance(Math.PI / 2);
		g.setFont(oldFont.deriveFont(at));

		text = String.format("%s", alignment.getAlignmentParams()
				.getSequence(0).getInfo().getAccessionNumber());
		g.drawString(text, rect.x1 + textHeight / 3, rect.y0
				+ (rect.height() - fm.stringWidth(text)) / 2);

		g.setFont(oldFont);
	}

	/**
	 * @return the preferred dimension of the plot.
	 */
	private Dimension getIdealDimension() {
//		int i0 = alignment.getSequenceStartPosition(0);
//		int j0 = alignment.getSequenceStartPosition(1);
//		int i1 = alignment.getSequenceEndPosition(0);
//		int j1 = alignment.getSequenceEndPosition(1);
//		
//		int delta_i = i1 - i0;
//		int delta_j = j1 - j0;
		
		int delta_i = viewportI1 - viewportI0;
		int delta_j = viewportJ1 - viewportJ0;

		//float size = IMAGE_SIZE;
		float size = IMAGE_SIZE*(float)(Math.sqrt((((delta_i+delta_j)/2)/100000000.0)));
		if (size < 300) {
			size = 300;
		}
		float size_x;
		float size_y;
		if (delta_i > delta_j) {
			size_y = size;
			size_x = size * delta_j / delta_i;
		} else {
			size_y = size * delta_i / delta_j;
			size_x = size;
		}
		Dimension dimension = new Dimension((int) size_x, (int) size_y);

		return dimension;
	}

	/**
	 * Zoom the viewport to the direction of the (i,j) position coordinates.
	 * 
	 * @param zoomAmount the amount to zoom. If this value is greater than 1,
	 *            than the zoom will decrease, otherwise it will increase. The
	 *            zoom amount is a float number, and it represents the
	 *            percentage of zoom. For example, if the amount is 0.5 the zoom
	 *            will increase 50%.
	 * @param i the zooming i-coordinate.
	 * @param j the zooming j-coordinate.
	 */
	private void zoom(float w, int i, int j) {

		// adjust the screen ratio to make the viewport more square
		float wxy = (viewportI1 - viewportI0)
				/ ((float) (viewportJ1 - viewportJ0));
		wxy = (float) Math.pow(wxy, 0.1);

		// calculates the new viewport
		viewportI0 = (int) Math.round(i - (i - viewportI0) * w / wxy);
		viewportJ0 = (int) Math.round(j - (j - viewportJ0) * w * wxy);
		viewportI1 = (int) Math.round(i + (viewportI1 - i) * w / wxy);
		viewportJ1 = (int) Math.round(j + (viewportJ1 - j) * w * wxy);

		// minimum size of the viewport
		final int MIN_SIZE = 20;
		if (viewportI0 > i - MIN_SIZE)
			viewportI0 = i - MIN_SIZE;
		if (viewportJ0 > j - MIN_SIZE)
			viewportJ0 = j - MIN_SIZE;
		if (viewportI1 < i + MIN_SIZE)
			viewportI1 = i + MIN_SIZE;
		if (viewportJ1 < j + MIN_SIZE)
			viewportJ1 = j + MIN_SIZE;

		// ensure that the viewport is always greater than zero
		if (viewportI0 < 0)
			viewportI0 = 0;
		if (viewportJ0 < 0)
			viewportJ0 = 0;
		if (viewportI1 < 0)
			viewportI1 = 0;
		if (viewportJ1 < 0)
			viewportJ1 = 0;

		// ensure that the viewport is always smaller than the sequences
		if (alignment != null) {
			this.viewportI0 = (int) Math.min(alignment.getAlignmentParams()
					.getSequence(0).getInfo().getSize(), viewportI0);
			this.viewportI1 = (int) Math.min(alignment.getAlignmentParams()
					.getSequence(0).getInfo().getSize(), viewportI1);
			this.viewportJ0 = (int) Math.min(alignment.getAlignmentParams()
					.getSequence(1).getInfo().getSize(), viewportJ0);
			this.viewportJ1 = (int) Math.min(alignment.getAlignmentParams()
					.getSequence(1).getInfo().getSize(), viewportJ1);
		}
	}


	/**
	 * Pre-process the block results for further drawing.
	 */
	private void processBlockResults() {
		int[][] blocks = alignment.getBlocks();
		int h = blocks.length;
		int w = blocks[0].length;
		
		minBlockResult = Integer.MAX_VALUE;
		maxBlockResult = Integer.MIN_VALUE;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int val = blocks[i][j];
				minBlockResult = Math.min(minBlockResult, val);
				maxBlockResult = Math.max(maxBlockResult, val);
			}
		}
		
		gridColors = new Color[h][w];
		
		int blockH = alignment.getAlignmentParams().getSequence(0).getInfo().getSize()/h;
		int blockW = alignment.getAlignmentParams().getSequence(1).getInfo().getSize()/w;
		
		// Determines the direction of each sequence
		int dir_i = alignment.getSequenceDirection(0);
		int dir_j = alignment.getSequenceDirection(1);

		// draws each visible block
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int bi = (dir_i > 0) ? i : (h - i - 1);
				int bj = (dir_j > 0) ? j : (w - j - 1);

				int val = blocks[bi][bj];
				if (val != Integer.MIN_VALUE) {
					float c;
					if (val > 0) {
						c = (float)Math.pow(val / (maxBlockResult*0.9f), 0.5f);
					} else {
						c = -(float)Math.pow(-val / (-minBlockResult*0.9f), 0.5f);
					}
					/*if (bi > 0 && bj > 0) {
						c = (val-Math.max(Math.max(blocks[bi-1][bj-1],blocks[bi][bj-1]),blocks[bi-1][bj]))/blockH;
						if (c < 0) c = -0.4f;
					} else {
						c = 0;
					}*/
					if (c > 1) {
						c = 1;
					}
					if (c < -1) {
						c = -1;
					}
					float r;
					float g;
					float b;
					
					if (c > 0) {
						r = 0.5f + c * 0.5f;
						g = 0.8f - c * 0.6f;
						b = 0.7f - c * 0.7f;
					} else {
						r = 0.5f + c * 0.5f;
						g = 0.8f + c * 0.6f;
						b = 0.7f - c*c * 0.5f;
					}
					gridColors[i][j] = new Color(r, g, b);
				} else {
					gridColors[i][j] = Color.BLACK;
				}
			}
		}
		
		
	}

}
