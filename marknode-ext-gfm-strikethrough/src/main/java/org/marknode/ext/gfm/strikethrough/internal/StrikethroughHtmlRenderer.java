package org.marknode.ext.gfm.strikethrough.internal;

import org.marknode.ext.gfm.strikethrough.Strikethrough;
import org.marknode.node.Node;
import org.marknode.node.Visitor;
import org.marknode.renderer.html.CustomHtmlRenderer;
import org.marknode.renderer.html.HtmlWriter;

public class StrikethroughHtmlRenderer implements CustomHtmlRenderer {

  @Override
  public boolean render(Node node, HtmlWriter htmlWriter, Visitor visitor) {
    if (node instanceof Strikethrough) {
      htmlWriter.tag("del");
      visitChildren(node, visitor);
      htmlWriter.tag("/del");
      return true;
    } else {
      return false;
    }
  }

  private void visitChildren(Node node, Visitor visitor) {
    Node child = node.getFirstChild();
    while (child != null) {
      child.accept(visitor);
      child = child.getNext();
    }
  }

}
