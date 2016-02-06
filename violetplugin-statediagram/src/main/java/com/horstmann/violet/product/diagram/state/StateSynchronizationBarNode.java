package com.horstmann.violet.product.diagram.state;

import java.awt.Graphics2D;

import com.horstmann.violet.product.diagram.abstracts.edge.AbstractEdge;
import com.horstmann.violet.product.diagram.common.SynchronizationBarNode;

public class StateSynchronizationBarNode extends SynchronizationBarNode {

	public StateSynchronizationBarNode(boolean isHorizontal, Class<? extends AbstractEdge> edgeClazz) {
		super(isHorizontal, edgeClazz);
	}

	@Override
	public void draw(Graphics2D graphics) {
		// Translate g2 if node has parent
		double dx = getLocationOnGraph().getX() - getLocation().getX();
		double dy = getLocationOnGraph().getY() - getLocation().getY();
		graphics.translate(dx, dy);

		super.draw(graphics);

		// Restore g2 original location
		graphics.translate(-dx, -dy);
	}

}
