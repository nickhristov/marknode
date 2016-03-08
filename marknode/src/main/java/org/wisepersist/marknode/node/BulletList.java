package org.wisepersist.marknode.node;

public class BulletList extends ListBlock {

  private char bulletMarker;

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public char getBulletMarker() {
    return bulletMarker;
  }

  public void setBulletMarker(char bulletMarker) {
    this.bulletMarker = bulletMarker;
  }

}
