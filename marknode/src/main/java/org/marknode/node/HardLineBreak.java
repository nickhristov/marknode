package org.marknode.node;

public class HardLineBreak extends Node {

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
