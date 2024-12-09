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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import br.unb.cic.av.alignment.Alignment;
import br.unb.cic.av.alignment.SequenceWithGaps;
import br.unb.cic.av.alignment.TextChunk;
import br.unb.cic.av.alignment.TextChunkSum;

public class AlignmentList extends JPanel {
	private static final int COLS = 60;
	private Vector<String> chunks;
	private JLabel label;
	private JScrollBar scroll;
	private TextChunkSum textChunkSum;
	Alignment alignment;
	
	public AlignmentList(MainController controller) {
		//JTextArea textArea = new JTextArea();
		//this.setEditable(false);
		//setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		//JScrollPane scroll = new JScrollPane(alignmentList);
		//scroll.setPreferredSize(new Dimension(500,100));
		
		label = new JLabel();
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 8));
		label.setPreferredSize(new Dimension(500,100));
		label.setText("");
		label.setVerticalAlignment(JLabel.TOP);
		scroll = new JScrollBar(JScrollBar.VERTICAL);
		scroll.setUnitIncrement(1);
		scroll.setVisibleAmount(1);
		scroll.setBlockIncrement(10);
		
		scroll.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				updateScrollBar();
			}
		});
		
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
		add(scroll, BorderLayout.EAST);
	}

	public void setAlignment(Alignment alignment) {
		if (alignment != null && alignment.getAlignmentParams().getSequence(0).getData() != null && alignment.getAlignmentParams().getSequence(1).getData() != null) {
			this.alignment = alignment;
			resetChunks();
			
			chunks = new Vector<String>();
			System.out.println("*");
			while (hasMoreChunks()) {
				TextChunk chunk = getNextChunk(COLS);
				chunks.add(chunk.getHTMLString());
			}
			System.out.println("**");
			
			chunks.add(textChunkSum.getHTMLString());
			
			
			scroll.setMaximum(Math.max(chunks.size()-5, 0));
			//if (htmlChunks.size() > 100) {
			//	setListData(new Vector<String>(htmlChunks.subList(0, 100)));
			//} else {
			//	setListData(htmlChunks);
			//}
			scroll.setValue(0);
			updateScrollBar();
			
			
		} else {
			//label.setText("");
		}
	}
	
	private void updateScrollBar() {
		if (chunks != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("<html>");
			int start = scroll.getValue();
			System.out.println(start + " - " + scroll.getMaximum());
			int end = Math.min(start + 20, chunks.size());
			for (int i=start; i<end; i++) {
				sb.append(chunks.get(i));
			}
			sb.append("</html>");
			label.setText(sb.toString());
			//System.out.println("***: " + sb);
		}
	}

	
	private void resetChunks() {

		getSeq0WithGaps().reset(alignment.getSequenceStartOffset(0), alignment.getSequenceEndOffset(0));
		getSeq1WithGaps().reset(alignment.getSequenceStartOffset(1), alignment.getSequenceEndOffset(1));
		//getSeq0WithGaps().reset(alignment.getGaps(0).getStartOffset(), alignment.getGaps(0).getEndOffset());
		//getSeq1WithGaps().reset(alignment.getGaps(1).getStartOffset(), alignment.getGaps(1).getEndOffset());
		//getSeq0WithGaps().reset(alignment.getSequenceStart(0), alignment.getSequenceEnd(0));
		//getSeq1WithGaps().reset(alignment.getSequenceStart(1), alignment.getSequenceEnd(1));

		textChunkSum = new TextChunkSum(
				alignment.getAlignmentParams().getMatch(), 
				alignment.getAlignmentParams().getMismatch(), 
				alignment.getAlignmentParams().getGapOpen(), 
				alignment.getAlignmentParams().getGapExtension()); 
	}

	private boolean hasMoreChunks() {
		return (!getSeq0WithGaps().isDone() && !getSeq1WithGaps().isDone());
	}

	public TextChunk getNextChunk(int cols) {
		TextChunk chunk = new TextChunk();
		if (hasMoreChunks()) {
			// int prevScore = chunkComparator.score;
			chunk.setStartPositions(getSeq0WithGaps().getCurrentPosition(),
					getSeq1WithGaps().getCurrentPosition());
			String chunk0 = getSeq0WithGaps().getNextChunk(cols);
			String chunk1 = getSeq1WithGaps().getNextChunk(cols);
			chunk.setEndPositions(getSeq0WithGaps().getCurrentPosition(),
					getSeq1WithGaps().getCurrentPosition());
			chunk.setChunks(chunk0, chunk1);
			int chunkScore = textChunkSum.sumChunk(chunk);
			chunk.setSuffix(String.format("[%d/%d]", chunkScore,
					textChunkSum.getScore()));
		}
		return chunk;
	}

	private TextChunkSum getTextChunkSum() {
		return textChunkSum;
	}

	private SequenceWithGaps getSeq0WithGaps() {
		return alignment.getAlignmentWithGaps(0);
	}

	private SequenceWithGaps getSeq1WithGaps() {
		return alignment.getAlignmentWithGaps(1);
	}

}
