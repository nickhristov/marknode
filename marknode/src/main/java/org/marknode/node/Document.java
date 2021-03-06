package org.marknode.node;

public class Document extends Block {

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
