package org.marknode.ext.front.matter.internal;

import org.marknode.ext.front.matter.YamlFrontMatterBlock;
import org.marknode.ext.front.matter.YamlFrontMatterNode;
import org.marknode.node.Node;
import org.marknode.node.Visitor;
import org.marknode.renderer.html.CustomHtmlRenderer;
import org.marknode.renderer.html.HtmlWriter;

public class YamlFrontMatterBlockRenderer implements CustomHtmlRenderer {

  @Override
  public boolean render(Node node, HtmlWriter htmlWriter, Visitor visitor) {
    return node instanceof YamlFrontMatterBlock || node instanceof YamlFrontMatterNode;
  }
}
