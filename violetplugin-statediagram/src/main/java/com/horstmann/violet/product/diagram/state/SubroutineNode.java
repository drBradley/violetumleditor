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

package com.horstmann.violet.product.diagram.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.CompartmentBoundsManager;
import com.horstmann.violet.product.diagram.common.DecisionNode;
import com.horstmann.violet.product.diagram.common.NoteNode;
import com.horstmann.violet.product.diagram.common.SynchronizationBarNode;

/**
 * A Subroutine node in a state diagram.
 */
public class SubroutineNode extends RectangularNode {

	private static final double ANCHOR_HEIGHT = 10.0;
	private static final double ANCHOR_WIDTH = 30.0;
	private static final int CHILD_GAP = 20;
	private static final double DEFAULT_COMPARTMENT_HEIGHT = 20;
	private static final int ARC_SIZE = 20;
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;

	private MultiLineString name;
	private CompartmentBoundsManager manager;

	/**
	 * Construct a state node with a default size
	 */
	public SubroutineNode() {
		name = new MultiLineString();
		manager = new CompartmentBoundsManager(getFields(), DEFAULT_HEIGHT, DEFAULT_COMPARTMENT_HEIGHT, DEFAULT_WIDTH);
	}

	private List<MultiLineString> getFields() {
		return Arrays.asList(name);
	}

	@Override
	public boolean addConnection(IEdge e) {
		if (e.getEnd() == null) {
			return false;
		}
		if (this.equals(e.getEnd())) {
			return false;
		}
		return super.addConnection(e);
	}

	@Override
	public Rectangle2D getBounds() {
		Rectangle2D bounds = manager.getFirstRectangleBounds(getLocation(), name, getGraph());
		Rectangle2D childrenBounds = getChildrenBounds();
		childrenBounds.setFrame(getLocation().getX(), getLocation().getY(), childrenBounds.getWidth(),
				childrenBounds.getHeight());
		bounds.add(childrenBounds);
		Rectangle2D snappedBounds = getGraph().getGridSticker().snap(bounds);
		return snappedBounds;
	}

	private Rectangle2D getChildrenBounds() {
		Rectangle2D childrenBounds = new Rectangle2D.Double(0, 0, 0, 0);
		for (INode child : getChildren()) {
			Rectangle2D childBounds = child.getBounds();
			childrenBounds.add(childBounds);
		}
		childrenBounds.setFrame(childrenBounds.getX(), childrenBounds.getY(), childrenBounds.getWidth() + CHILD_GAP,
				childrenBounds.getHeight() + CHILD_GAP);
		return childrenBounds;
	}

	@Override
	public void draw(Graphics2D g2) {
		Color oldColor = g2.getColor();

		Point2D nodeLocationOnGraph = getLocationOnGraph();
		Point2D nodeLocation = getLocation();
		Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(),
				nodeLocationOnGraph.getY() - nodeLocation.getY());
		g2.translate(g2Location.getX(), g2Location.getY());

		super.draw(g2);
		Shape shape = getShape();
		g2.setColor(getBackgroundColor());
		g2.fill(shape);
		g2.setColor(getBorderColor());
		g2.draw(shape);
		g2.setColor(getTextColor());
		name.draw(g2, getNameBounds(getBounds()));
		g2.setColor(oldColor);

		g2.translate(-g2Location.getX(), -g2Location.getY());

		for (INode node : getChildren()) {
			fixChildLocation(getAnchor(g2Location), node);
			node.draw(g2);
		}
	}

	/**
	 * Ensure that name will be display in the top middle part of the node
	 * 
	 * @param bounds
	 * @return centered rectangle
	 */
	private Rectangle2D getNameBounds(Rectangle2D bounds) {
		Rectangle2D nameBounds = name.getBounds();
		double newX = bounds.getX() + (bounds.getWidth() - nameBounds.getWidth()) / 2.0;
		return new Rectangle2D.Double(newX, bounds.getY(), nameBounds.getWidth(), nameBounds.getHeight());
	}

	private Double getAnchor(Point2D g2Location) {
		return new Rectangle2D.Double(-g2Location.getX(), -g2Location.getY(), ANCHOR_WIDTH, ANCHOR_HEIGHT);
	}

	/**
	 * Ensure that child node respects the minimum gap with package borders
	 * 
	 * @param topBounds
	 * @param node
	 */
	private void fixChildLocation(Rectangle2D topBounds, INode node) {
		Point2D childLocation = node.getLocation();
		if (childLocation.getY() <= topBounds.getHeight() + CHILD_GAP) {
			node.translate(0, topBounds.getHeight() + CHILD_GAP - childLocation.getY());
		}
		if (childLocation.getX() < CHILD_GAP) {
			node.translate(CHILD_GAP - childLocation.getX(), 0);
		}
	}

	@Override
	public Shape getShape() {
		return new RoundRectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(),
				getBounds().getHeight(), ARC_SIZE, ARC_SIZE);
	}

	@Override
	public boolean addChild(INode child, Point2D p) {
		if (isAllowed(child)) {
			child.setParent(this);
			child.setGraph(this.getGraph());
			child.setLocation(p);
			addChild(child, getChildren().size());
			return true;
		}
		return false;
	}

	private boolean isAllowed(INode node) {
		Class<?>[] allowed = new Class[] { CircularFinalStateNode.class, CircularInitialStateNode.class,
				StateNode.class, StateTransitionEdge.class, SynchronizationBarNode.class, DecisionNode.class,
				NoteNode.class };
		for (Class<?> each : allowed) {
			if (each.isInstance(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the name property value.
	 * 
	 * @param newValue
	 *            the new state name
	 */
	public void setName(MultiLineString newValue) {
		name = newValue;
		manager.setFields(getFields());
	}

	/**
	 * Gets the name property value.
	 * 
	 * @param the
	 *            state name
	 */
	public MultiLineString getName() {
		return name;
	}

	public SubroutineNode clone() {
		SubroutineNode cloned = (SubroutineNode) super.clone();
		cloned.name = name.clone();
		return cloned;
	}

}
