package org.marknode.node;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class AbstractVisitorTest {

  @Test
  public void replacingNodeInVisitorShouldNotDestroyVisitOrder() {
    Visitor visitor = new AbstractVisitor() {
      @Override
      public void visit(Text text) {
        text.insertAfter(new Code(text.getLiteral()));
        text.unlink();
      }
    };

    Paragraph paragraph = new Paragraph();
    paragraph.appendChild(new Text("foo"));
    paragraph.appendChild(new Text("bar"));

    paragraph.accept(visitor);

    assertCode("foo", paragraph.getFirstChild());
    assertCode("bar", paragraph.getFirstChild().getNext());
    assertNull(paragraph.getFirstChild().getNext().getNext());
    assertCode("bar", paragraph.getLastChild());
  }

  private static void assertCode(String expectedLiteral, Node node) {
    assertEquals(Code.class, node.getClass(), "Expected node to be a Code node: " + node);
    Code code = (Code) node;
    assertEquals(expectedLiteral, code.getLiteral());
  }
}
