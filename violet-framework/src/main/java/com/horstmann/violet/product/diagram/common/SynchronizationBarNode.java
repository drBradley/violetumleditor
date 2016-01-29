/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.product.diagram.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.AbstractEdge;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;

/**
 * A synchronization bar node in an activity diagram.
 */
public class SynchronizationBarNode extends RectangularNode {
	
	private Class<? extends AbstractEdge> supportedEdge;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 4;
	private static final int EXTRA_WIDTH = 12;

	private double width;
	private double height;
	private boolean isHorizontal;
	
	/**
	 * Creates synchronization bar
	 * 
	 * @param isHorizontal
	 * @param clazz 
	 */
	public SynchronizationBarNode(boolean isHorizontal, Class<? extends AbstractEdge> edgeClazz) {
		if (isHorizontal) {
			width = DEFAULT_WIDTH;
			height = DEFAULT_HEIGHT;
		} else {
			width = DEFAULT_HEIGHT;
			height = DEFAULT_WIDTH;
		}
		this.isHorizontal = isHorizontal;
		this.supportedEdge = edgeClazz;
	}
	
	@Override
	public boolean addConnection(IEdge edge) {
		if (edge.getStart() instanceof SynchronizationBarNode && edge.getEnd() instanceof SynchronizationBarNode) {
			return false;
		}
		return edge.getEnd() != null && this != edge.getEnd();
	}

	@Override
	public Point2D getConnectionPoint(IEdge edge) {
		Point2D defaultConnectionPoint = super.getConnectionPoint(edge);
		if (!isAllowed(edge)) {
			return defaultConnectionPoint;
		}

		INode end = edge.getEnd();
		INode start = edge.getStart();
		if (this == start) {
			return getConnectionPointForNode(edge, defaultConnectionPoint, end);
		}
		if (this == end) {
			return getConnectionPointForNode(edge, defaultConnectionPoint, start);
		}

		return defaultConnectionPoint;
	}

	private boolean isAllowed(IEdge edge) {
		return supportedEdge.isInstance(edge);
	}

	private Point2D getConnectionPointForNode(IEdge edge, Point2D defaultConnectionPoint, INode bar) {
		Point2D connectionPoint = bar.getConnectionPoint(edge);
		if (isHorizontal) {
			double y = defaultConnectionPoint.getY();
			double x = connectionPoint.getX();
			return new Point2D.Double(x, y);
		}
		double y = connectionPoint.getY();
		double x = defaultConnectionPoint.getX();
		return new Point2D.Double(x, y);
	}

	@Override
	public Rectangle2D getBounds() {
		Rectangle2D bounds = getDefaultBounds();
		List<INode> connectedNodes = getConnectedNodes();
		if (connectedNodes.size() > 0) {
			double[] size = getSize();
			if (isHorizontal) {
				translate(size[0] - bounds.getX(), 0);
				width = size[1] - size[0];
				bounds = new Rectangle2D.Double(size[0], bounds.getY(), width, height);
			} else {
				translate(0, size[0] - bounds.getY());
				height = size[1] - size[0];
				bounds = new Rectangle2D.Double(bounds.getX(), size[0], width, height);
			}
		}
		return bounds;
	}
	
	private double[] getSize() {
		List<INode> connectedNodes = getConnectedNodes();
		if (connectedNodes.isEmpty()) {
			return new double[] {};
		}
		double minLength = Double.MAX_VALUE;
		double maxLength = Double.MIN_VALUE;
		for (INode each : connectedNodes) {
			Rectangle2D bounds = each.getBounds();
			minLength = Math.min(minLength, isHorizontal ? bounds.getMinX() : bounds.getMinY());
			maxLength = Math.max(maxLength, isHorizontal ? bounds.getMaxX() : bounds.getMaxY());
		}

		minLength = minLength == 0 ? 0 : minLength - EXTRA_WIDTH;
		maxLength = maxLength == 0 ? 0 : maxLength + EXTRA_WIDTH;

		return new double[] { minLength, maxLength };
	}

	/**
	 * 
	 * @return minimal bounds (location + default width and default height
	 */
	private Rectangle2D getDefaultBounds() {
		Point2D currentLocation = getLocation();
		double x = currentLocation.getX();
		double y = currentLocation.getY();
		double w = width;
		double h = height;
		Rectangle2D currentBounds = new Rectangle2D.Double(x, y, w, h);
		return currentBounds;
	}

	/**
	 * 
	 * @return nodes which are connected (with edges) to this node
	 */
	private List<INode> getConnectedNodes() {
		List<INode> connectedNodes = new ArrayList<INode>();
		// needs to contain all incoming and outgoing edges
		for (IEdge edge : getGraph().getAllEdges()) {
			if (edge.getStart() == this)
				connectedNodes.add(edge.getEnd());
			if (edge.getEnd() == this)
				connectedNodes.add(edge.getStart());
		}
		return connectedNodes;
	}

	@Override
	public void draw(Graphics2D graphics) {
		super.draw(graphics);

		// Backup current color;
		Color oldColor = graphics.getColor();

		// Perform drawing
		graphics.setColor(getBorderColor());
		graphics.fill(getShape());

		// Restore first color
		graphics.setColor(oldColor);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SynchronizationBarNode clone() {
		SynchronizationBarNode cloned = (SynchronizationBarNode) super.clone();
		cloned.width = width;
		cloned.height = height;
		cloned.isHorizontal = isHorizontal;
		return cloned;
	}

}
