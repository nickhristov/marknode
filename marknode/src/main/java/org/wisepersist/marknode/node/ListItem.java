package org.wisepersist.marknode.node;

public class ListItem extends Block {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
