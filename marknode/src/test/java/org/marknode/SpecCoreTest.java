package org.marknode;

import org.marknode.node.AbstractVisitor;
import org.marknode.node.Node;
import org.marknode.node.Text;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;
import org.marknode.spec.SpecExample;
import org.marknode.test.SpecTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class SpecCoreTest extends SpecTestCase {

  private static final Parser PARSER = Parser.builder().build();
  // The spec says URL-escaping is optional, but the examples assume that it's enabled.
  private static final HtmlRenderer
      RENDERER =
      HtmlRenderer.builder().percentEncodeUrls(true).build();

  public SpecCoreTest(SpecExample example) {
    super(example);
  }

  @Test
  public void testTextNodesContiguous() {
    final String source = example.getSource();
    Node node = PARSER.parse(source);
    node.accept(new AbstractVisitor() {
      @Override
      protected void visitChildren(Node parent) {
        if (parent instanceof Text && parent.getFirstChild() != null) {
          fail("Text node is not allowed to have children, literal is \"" + ((Text) parent)
              .getLiteral() + "\"");
        }
        boolean lastText = false;
        Node node = parent.getFirstChild();
        while (node != null) {
          if (node instanceof Text) {
            if (lastText) {
              fail("Adjacent text nodes found, second node literal is \"" + ((Text) node)
                  .getLiteral() + "\", source:\n" + source);
            }
            lastText = true;
          } else {
            lastText = false;
          }
          node = node.getNext();
        }
        super.visitChildren(parent);
      }
    });
  }

  @Override
  protected String render(String source) {
    return RENDERER.render(PARSER.parse(source));
  }
}
