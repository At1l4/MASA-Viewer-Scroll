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
package br.unb.cic.av.alignment;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unb.cic.av.alignment.AlignmentParams.AlignmentMethod;
import br.unb.cic.av.alignment.AlignmentParams.PenaltySystem;
import br.unb.cic.av.alignment.AlignmentParams.ScoreSystem;
import br.unb.cic.av.alignment.SequenceInfo.SequenceType;

/**
 * This class handles the CUDAlign binary alignment format.
 * 
 * @author edans
 */
public class AlignmentBinaryFile {
	private static final int MAX_STRING_LEN = 1000;

	private static final String MAGIC_HEADER	= "CGFF";
	private static final int MAGIC_HEADER_LEN	= 4; //in bytes
	private static final int FILE_VERSION_MAJOR	= 0;
	private static final int FILE_VERSION_MINOR	= 1;


	private static final int END_OF_FIELDS					= 0;

	private static final int FIELD_ALIGNMENT_METHOD			= 1;
	private static final int FIELD_SCORING_SYSTEM			= 2;
	private static final int FIELD_PENALTY_SYSTEM			= 3;
	private static final int FIELD_SEQUENCE_PARAMS			= 4;


	private static final int FIELD_SEQUENCE_DESCRIPTION		= 1;
	private static final int FIELD_SEQUENCE_TYPE			= 2;
	private static final int FIELD_SEQUENCE_SIZE			= 3;
	private static final int FIELD_SEQUENCE_HASH			= 4;
	private static final int FIELD_SEQUENCE_DATA_PLAIN		= 5;
	private static final int FIELD_SEQUENCE_DATA_COMPRESSED	= 6;

	private static final int FIELD_RESULT_RAW_SCORE			= 1;
	private static final int FIELD_RESULT_BIT_SCORE			= 2;
	private static final int FIELD_RESULT_E_VALUE			= 3;
	private static final int FIELD_RESULT_SCORE_STATISTICS	= 4;
	private static final int FIELD_RESULT_GAP_LIST			= 5;
	private static final int FIELD_RESULT_BLOCKS			= 6;
	private static final int FIELD_RESULT_CELLS				= 7;
	
	
	private static final int SEQUENCE_TYPE_DNA 		= 1;
	private static final int SEQUENCE_TYPE_RNA 		= 2;
	private static final int SEQUENCE_TYPE_PROTEIN 	= 3;
	private static final int SEQUENCE_TYPE_UNKNOWN 	= 255;	
	private static Map<Integer, SequenceType> sequenceType = new HashMap<Integer, SequenceType>();
	static {
		sequenceType.put(SEQUENCE_TYPE_DNA, SequenceType.DNA);
		sequenceType.put(SEQUENCE_TYPE_RNA, SequenceType.RNA);
		sequenceType.put(SEQUENCE_TYPE_PROTEIN, SequenceType.PROTEIN);
		sequenceType.put(SEQUENCE_TYPE_UNKNOWN, SequenceType.UNKNOWN);
	}
	
	
	
	public static final int ALIGNMENT_METHOD_GLOBAL = 1;
	public static final int ALIGNMENT_METHOD_LOCAL = 2;
	private static Map<Integer, AlignmentMethod> alignmentMethod = new HashMap<Integer, AlignmentMethod>();
	static {
		alignmentMethod.put(ALIGNMENT_METHOD_GLOBAL, AlignmentMethod.GLOBAL);
		alignmentMethod.put(ALIGNMENT_METHOD_LOCAL, AlignmentMethod.LOCAL);
	}

	
	public static final int PENALTY_LINEAR_GAP = 1;
	public static final int PENALTY_AFFINE_GAP = 2;
	private static Map<Integer, PenaltySystem> penaltySystem = new HashMap<Integer, PenaltySystem>();
	static {
		penaltySystem.put(PENALTY_LINEAR_GAP, PenaltySystem.LINEAR_GAP);
		penaltySystem.put(PENALTY_AFFINE_GAP, PenaltySystem.AFFINE_GAP);
	}
	
	
	public static final int SCORE_MATCH_MISMATCH = 1;
	public static final int SCORE_SIMILARITY_MATRIX = 2;	
	private static Map<Integer, ScoreSystem> scoreSystem = new HashMap<Integer, ScoreSystem>();
	static {
		scoreSystem.put(SCORE_MATCH_MISMATCH, ScoreSystem.MATCH_MISMATCH);
		scoreSystem.put(SCORE_SIMILARITY_MATRIX, ScoreSystem.SIMILARITY_MATRIX);
	}	
	
	
	public static Alignment read(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		DataInputStream ds = new DataInputStream(is);
		
		fread_header(ds);

		List<SequenceInfo> sequences = fread_sequences(ds);

		AlignmentParams params = fread_alignment_params(sequences, ds);
		Alignment alignment = new Alignment(params);
		fread_alignment_result(alignment, ds);

		ds.close();
		is.close();
		
		return alignment;
	}

	private static void fread_header(DataInputStream is) throws IOException {
		byte[] header = fread_array(MAGIC_HEADER_LEN, is);
		if (!Arrays.equals(header, MAGIC_HEADER.getBytes())) {
			throw new IOException("Wrong File Format. Header Error.\n");
		}
		int file_version_major = fread_int1(is);
		int file_version_minor = fread_int1(is);
		if (file_version_major > FILE_VERSION_MAJOR) {
			throw new IOException(
					String.format("File Version not supported (%d.%d > %d.%d).\n",
					file_version_major, file_version_minor,
					FILE_VERSION_MAJOR, FILE_VERSION_MINOR));
		}
	}


	private static List<SequenceInfo> fread_sequences(DataInputStream is) throws IOException {
		int count = fread_int4(is);
		List<SequenceInfo> sequences = new ArrayList<SequenceInfo>();
		
		for (int i=0; i<count; i++) {
			SequenceInfo seq = new SequenceInfo();
			sequences.add(seq);
			int field;
			while ( (field = fread_int1(is)) != END_OF_FIELDS) {
				int field_len;
				switch (field) {
					case FIELD_SEQUENCE_DESCRIPTION:
						seq.setDescription(fread_str(is));
						break;
					case FIELD_SEQUENCE_TYPE:
						seq.setType(sequenceType.get(fread_int1(is)));
						break;
					case FIELD_SEQUENCE_SIZE:
						seq.setSize(fread_int4(is));
						break;
					case FIELD_SEQUENCE_HASH:
						// TODO dummy
						seq.setHash(fread_str(is));
						break;
					case FIELD_SEQUENCE_DATA_PLAIN:
						field_len = fread_int4(is);
						seq.setData(fread_array(field_len, is));
						break;
					case FIELD_SEQUENCE_DATA_COMPRESSED:
						field_len = fread_int4(is);
						fread_dummy(field_len, is);
						break;					
						default:
							throw new IOException(
									String.format("Sanity Check: Unknown Field (%d).\n", field));
					}				
			}
		}
		
		return sequences;
	}

	private static AlignmentParams fread_alignment_params(List<SequenceInfo> sequences, DataInputStream is) throws IOException {
		AlignmentParams params = new AlignmentParams();
		int field;
		while ((field = fread_int1(is)) != END_OF_FIELDS) {
			switch (field) {
				case FIELD_ALIGNMENT_METHOD:
					params.setAlignmentMethod(alignmentMethod.get(fread_int1(is)));
					break;
				case FIELD_SCORING_SYSTEM:
					params.setScoreSystem(scoreSystem.get(fread_int1(is)));
					switch (params.getScoreSystem()) {
						case MATCH_MISMATCH: {
							int match = fread_int4(is);
							int mismatch = fread_int4(is);
							params.setMatchMismatchScores(match, mismatch);
							break;
						}
						case SIMILARITY_MATRIX: {
							throw new IOException("Score Matrix not supported yet.\n");

						}
						default:
							throw new IOException("Unknown Score System.\n");

					}
					break;
				case FIELD_PENALTY_SYSTEM:
					params.setPenaltySystem(penaltySystem.get(fread_int1(is)));
					switch (params.getPenaltySystem()) {
						case LINEAR_GAP: {
							int gapOpen = 0;
							int gapExtension = fread_int4(is);
							params.setAffineGapPenalties(gapOpen, gapExtension);
							break;
						}
						case AFFINE_GAP: {
							int gapOpen = fread_int4(is);
							int gapExtension = fread_int4(is);
							params.setAffineGapPenalties(gapOpen, gapExtension);
							break;
						}
						default:
							throw new IOException("Unknown Penalty System.\n");

					}
					break;
				case FIELD_SEQUENCE_PARAMS: {
					int count = fread_int4(is);
					for (int i=0; i<count; i++) {
						int id = fread_int4(is);
						
						int flags = fread_int4(is);
						int trimStart = fread_int4(is);
						int trimEnd = fread_int4(is);
						SequenceModifiers modifiers = new SequenceModifiers();
						modifiers.setFlags(flags);
						modifiers.setTrimPositions(trimStart, trimEnd);
						
						params.addSequence(new Sequence(sequences.get(id), modifiers));
					}
					break;
				}
				default:
					throw new IOException(
							String.format("Sanity Check: Unknown Field (%d).\n", field));
			}
		}
		return params;
	}


	private static void fread_alignment_result(Alignment alignment, DataInputStream is) throws IOException {
		int results = fread_int4(is);
		if (results > 1) {
			// Only 1 result is supported by now
			throw new IOException(
					String.format("Too many results %d.\n", results));
		}
		
		int count = alignment.getAlignmentParams().getSequencesCount();
		int field;
		while ((field = fread_int1(is)) != END_OF_FIELDS) {
			switch (field) {
				case FIELD_RESULT_RAW_SCORE:
					alignment.setRawScore(fread_int4(is));
					break;
				case FIELD_RESULT_SCORE_STATISTICS:
					alignment.setMatches(fread_int4(is));
					alignment.setMismatches(fread_int4(is));
					alignment.setGapOpen(fread_int4(is));
					alignment.setGapExtensions(fread_int4(is));
					break;
				case FIELD_RESULT_GAP_LIST:{
					for (int i=0; i<count; i++) {
						int start = fread_int4(is);
						int end = fread_int4(is);
						alignment.setBoundaryPositions(i, start, end);
						alignment.setGaps(i, fread_gaps(is));
					}
					break;
				}
				case FIELD_RESULT_BLOCKS:{
					int h = fread_int4(is);
					int w = fread_int4(is);
					int[][] blocks = new int[h][w]; 
					for (int i=0; i<h; i++) {
						for (int j=0; j<w; j++) {
							blocks[i][j] = fread_int4(is);
						}
					}
					alignment.setBlocks(blocks);
					break;
				}
				default:
					throw new IOException(
							String.format("Sanity Check: Unknown Field (%d).\n", field));

			}
		}
	}


	private static byte[] fread_array(int len, DataInputStream is) throws IOException {
		byte[] data = new byte[len];
		is.readFully(data);
		return data;
	}

	private static int fread_uint4_compressed(DataInputStream is) throws IOException {
		int b = fread_int1(is);
		int i = (b & 0x7F);
		while (b >= 128) {
			b = fread_int1(is);
			i <<= 7;
			i |= (b & 0x7F);
		}
		return i;		
	}
	
	private static int fread_int4(DataInputStream is) throws IOException {
		return is.readInt();
	}

	private static int fread_int2(DataInputStream is) throws IOException {
		return is.readShort();
	}
	private static int fread_int1(DataInputStream is) throws IOException {
		return (int)is.read();
	}


	private static String fread_str(DataInputStream is) throws IOException {
		int len = fread_int4(is);
		if (len > 1000) {
			throw new IOException(
					"Sanity Check: string too large during file read (" + len
							+ " > " + MAX_STRING_LEN + ").");
		}
		byte[] bytes = new byte[len];
		int pos = 0;
		while (pos < len) {
			int ret = is.read(bytes, pos, len-pos);
			if (ret == -1) {
				throw new EOFException("EOF while reading string");
			}
			pos += ret;
		}
		return new String(bytes);
	}


	private static GapList fread_gaps(DataInputStream is) throws IOException {
		int count = fread_int4(is);
		GapList gaps = new GapList();
		
		int last = 0;
		for (int i=0; i<count; i++) {
			int pos = last + fread_uint4_compressed(is);
			last = pos;
			int len = fread_uint4_compressed(is);
			Gap gap = new Gap(pos, len);
			gaps.add(gap);
		}
	
		return gaps;
	}

	private static void fread_dummy(int len, DataInputStream is) throws IOException {
		is.skip(len);
	}
}
