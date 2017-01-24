package arbalest;

import org.eclipse.aether.graph.DependencyNode;

import java.util.Iterator;

class TreePrinter {
  static void print(DependencyNode node, String prefix, boolean isTail) {
    System.out.println(prefix + (isTail ? "└── " : "├── ") + node.getArtifact());
    for (Iterator<DependencyNode> iterator = node.getChildren().iterator(); iterator.hasNext(); ) {
      print(iterator.next(), prefix + (isTail ? "    " : "│   "), !iterator.hasNext());
    }
  }
}
