package org.marknode.parser;

import org.marknode.node.CustomNode;
import org.marknode.node.Node;
import org.marknode.node.Text;
import org.marknode.node.Visitor;
import org.marknode.renderer.html.CustomHtmlRenderer;
import org.marknode.renderer.html.HtmlRenderer;
import org.marknode.renderer.html.HtmlWriter;
import org.marknode.test.RenderingTestCase;
import org.testng.annotations.Test;

import java.util.Locale;

public class DelimiterProcessorTest extends RenderingTestCase {

  private static final Parser PARSER =
      Parser.builder().customDelimiterProcessor(new AsymmetricDelimiterProcessor()).build();
  private static final HtmlRenderer RENDERER =
      HtmlRenderer.builder().customHtmlRenderer(new UpperCaseNodeRenderer()).build();

  @Test
  public void asymmetricDelimiter() {
    assertRendering("{foo} bar", "<p>FOO bar</p>\n");
    assertRendering("f{oo ba}r", "<p>fOO BAr</p>\n");
    assertRendering("{{foo} bar", "<p>{FOO bar</p>\n");
    assertRendering("{foo}} bar", "<p>FOO} bar</p>\n");
    assertRendering("{{foo} bar}", "<p>FOO BAR</p>\n");
    assertRendering("{foo bar", "<p>{foo bar</p>\n");
    assertRendering("foo} bar", "<p>foo} bar</p>\n");
    assertRendering("}foo} bar", "<p>}foo} bar</p>\n");
    assertRendering("{foo{ bar", "<p>{foo{ bar</p>\n");
    assertRendering("}foo{ bar", "<p>}foo{ bar</p>\n");
  }

  @Override
  protected String render(String source) {
    Node node = PARSER.parse(source);
    return RENDERER.render(node);
  }

  private static class AsymmetricDelimiterProcessor implements DelimiterProcessor {

    @Override
    public char getOpeningDelimiterChar() {
      return '{';
    }

    @Override
    public char getClosingDelimiterChar() {
      return '}';
    }

    @Override
    public int getMinDelimiterCount() {
      return 1;
    }

    @Override
    public int getDelimiterUse(int openerCount, int closerCount) {
      return 1;
    }

    @Override
    public void process(Text opener, Text closer, int delimiterUse) {
      UpperCaseNode content = new UpperCaseNode();
      Node tmp = opener.getNext();
      while (tmp != null && tmp != closer) {
        Node next = tmp.getNext();
        content.appendChild(tmp);
        tmp = next;
      }
      opener.insertAfter(content);
    }
  }

  private static class UpperCaseNode extends CustomNode {

  }

  private static class UpperCaseNodeRenderer implements CustomHtmlRenderer {

    @Override
    public boolean render(Node node, HtmlWriter htmlWriter, Visitor visitor) {
      if (node instanceof UpperCaseNode) {
        UpperCaseNode upperCaseNode = (UpperCaseNode) node;
        for (Node child = upperCaseNode.getFirstChild(); child != null; child = child.getNext()) {
          if (child instanceof Text) {
            Text text = (Text) child;
            text.setLiteral(text.getLiteral().toUpperCase(Locale.ENGLISH));
          }
          child.accept(visitor);
        }
        return true;
      }
      return false;
    }
  }
}
