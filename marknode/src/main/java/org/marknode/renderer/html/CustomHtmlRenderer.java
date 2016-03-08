package org.marknode.renderer.html;

import org.marknode.node.Node;
import org.marknode.node.Visitor;

public interface CustomHtmlRenderer {

  // TODO: maybe pass renderer instead of visitor?
  boolean render(Node node, HtmlWriter htmlWriter, Visitor visitor);
}
