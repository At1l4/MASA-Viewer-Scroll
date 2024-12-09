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

/**
 * This class is responsible to produce a linear transformation between 
 * two coordinate spaces: the drawing space and the mapped space. 
 * The drawing space are represented by a rectangular area between coordinates 
 * (x0,y0)->(x1,y1). The mapped coordinates are represented by a rectangular 
 * area between coordinates (i0,j0)->(i1,j1). The transformation procedure
 * maps the coordinates (i0,j0) to (x0,y0) and the coordinates (i1,j1) 
 * to (x1,y1). All the intermediate points are mapped using a linear mapping
 * function.
 * 
 * @author edans
 *
 */
class DrawRectangle {
	/**
	 * Drawing space coordinates.
	 */
	public float x0, y0, x1, y1;
	
	/**
	 * Mapping space coordinates.
	 */
	public float i0, j0, i1, j1;
	
	/**
	 * @return the width of the drawing space.
	 */
	public float width() {
		return x1-x0;
	}
	
	/**
	 * @return the height of the drawing space.
	 */
	public float height() {
		return y1-y0;
	}
	
	/**
	 * Transforms the coordinate <code>i</code> from the mapped space 
	 * to the coordinate <code>y</code> of the drawing space.
	 * 
	 * @param i the mapped coordinate
	 * @return the drawing coordinate
	 */
	public float toY(float i) {
		return ((i-i0)/(i1-i0))*(y1-y0)+y0;
	}
	
	/**
	 * Transforms the coordinate <code>j</code> from the mapped space 
	 * to the coordinate <code>x</code> of the drawing space.
	 * 
	 * @param j the mapped coordinate
	 * @return the drawing coordinate
	 */	
	public float toX(float j) {
		return ((j-j0)/(j1-j0))*(x1-x0)+x0;
	}
	
	/**
	 * Transforms the coordinate <code>y</code> from the drawing space 
	 * to the coordinate <code>i</code> of the mapped space.
	 * 
	 * @param y the drawing coordinate
	 * @return the mapped coordinate
	 */
	public float toI(float y) {
		return (y-y0)/(y1-y0)*(i1-i0)+i0;
	}
	
	/**
	 * Transforms the coordinate <code>x</code> from the drawing space 
	 * to the coordinate <code>j</code> of the mapped space.
	 * 
	 * @param x the drawing coordinate
	 * @return the mapped coordinate
	 */	
	public float toJ(float x) {
		return (x-x0)/(x1-x0)*(j1-j0)+j0;
	}
}
