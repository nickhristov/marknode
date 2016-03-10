package org.marknode.node;

public class Paragraph extends Block {

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
