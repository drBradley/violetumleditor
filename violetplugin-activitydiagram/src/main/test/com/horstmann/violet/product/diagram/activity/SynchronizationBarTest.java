package com.horstmann.violet.product.diagram.activity;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import com.horstmann.violet.product.diagram.abstracts.node.AbstractNode;
import com.horstmann.violet.product.diagram.common.SynchronizationBarNode;

public class SynchronizationBarTest {

	private ActivityDiagramGraph graph;
	private ActivityNode activity;
	private SynchronizationBarNode bar;
	private ActivityTransitionEdge edge;

	@Before
	public void prepareGraph() {
		graph = new ActivityDiagramGraph();
		activity = new ActivityNode();
		bar = new SynchronizationBarNode(true, ActivityTransitionEdge.class);
		edge = new ActivityTransitionEdge();

		graph.addNode(activity, new Point2D.Double(43.0, 43.0));
		graph.addNode(bar, new Point2D.Double(100, 100));
		connectNodesOnGraph(graph, activity, bar, edge);
	}

	@Test
	public void testGetBoundsForHorizontalBar() {
		Rectangle2D bounds = bar.getBounds();
		assertEquals(31.0, bounds.getX(), 0.0);
		assertEquals(0.0, bounds.getY(), 0.0);
		assertEquals(104.0, bounds.getWidth(), 0.0);
		assertEquals(4.0, bounds.getHeight(), 0.0);
	}

	@Test
	public void testGetConnectionEdgeForHorizontalBar() {
		Point2D connectionPoint = bar.getConnectionPoint(edge);
		assertEquals(83.0, connectionPoint.getX(), 0.0);
		assertEquals(4.0, connectionPoint.getY(), 0.0);
	}

	private void connectNodesOnGraph(ActivityDiagramGraph graph, AbstractNode activity, AbstractNode bar,
			ActivityTransitionEdge edge) {
		Point2D startPoint = activity.getLocationOnGraph();
		Point2D endPoint = bar.getLocationOnGraph();

		Point2D relativeStartPoint = null;
		Point2D relativeEndPoint = null;

		if (activity != null) {
			Point2D startNodeLocationOnGraph = activity.getLocationOnGraph();
			double relativeStartX = startPoint.getX() - startNodeLocationOnGraph.getX();
			double relativeStartY = startPoint.getY() - startNodeLocationOnGraph.getY();
			relativeStartPoint = new Point2D.Double(relativeStartX, relativeStartY);
		}
		if (bar != null) {
			Point2D endNodeLocationOnGraph = bar.getLocationOnGraph();
			double relativeEndX = endPoint.getX() - endNodeLocationOnGraph.getX();
			double relativeEndY = endPoint.getY() - endNodeLocationOnGraph.getY();
			relativeEndPoint = new Point2D.Double(relativeEndX, relativeEndY);
		}

		graph.connect(edge, activity, relativeStartPoint, bar, relativeEndPoint, new Point2D[] {});
	}

}
