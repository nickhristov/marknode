package org.marknode.node;

public abstract class CustomNode extends Node {

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
