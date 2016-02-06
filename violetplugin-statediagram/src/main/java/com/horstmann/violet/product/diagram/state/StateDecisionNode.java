package com.horstmann.violet.product.diagram.state;

import java.awt.Graphics2D;

import com.horstmann.violet.product.diagram.common.DecisionNode;

public class StateDecisionNode extends DecisionNode {

	@Override
	public void draw(Graphics2D g2) {
		// Translate g2 if node has parent
		double dx = getLocationOnGraph().getX() - getLocation().getX();
		double dy = getLocationOnGraph().getY() - getLocation().getY();
		g2.translate(dx, dy);

		super.draw(g2);

		// Restore g2 original location
		g2.translate(-dx, -dy);

	}

}
