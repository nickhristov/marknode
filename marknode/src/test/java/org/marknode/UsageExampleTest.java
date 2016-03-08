package org.marknode;

import org.marknode.node.AbstractVisitor;
import org.marknode.node.Node;
import org.marknode.node.Text;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UsageExampleTest {

  @Test
  public void parseAndRender() {
    Parser parser = Parser.builder().build();
    Node document = parser.parse("This is *Sparta*");
    HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
    assertEquals("<p>This is <em>Sparta</em></p>\n", renderer.render(document));
  }

  @Test
  public void visitor() {
    Parser parser = Parser.builder().build();
    Node node = parser.parse("Example\n=======\n\nSome more text");
    WordCountVisitor visitor = new WordCountVisitor();
    node.accept(visitor);
    assertEquals(4, visitor.wordCount);
  }

  class WordCountVisitor extends AbstractVisitor {

    int wordCount = 0;

    @Override
    public void visit(Text text) {
      // This is called for all Text nodes. Override other visit methods for other node types.

      // Count words (this is just an example, don't actually do it this way for various reasons).
      wordCount += text.getLiteral().split("\\W+").length;

      // Descend into children (could be omitted in this case because Text nodes don't have children).
      visitChildren(text);
    }
  }

}
