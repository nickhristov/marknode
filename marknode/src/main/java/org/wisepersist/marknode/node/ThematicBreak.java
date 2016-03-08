package org.wisepersist.marknode.node;

public class ThematicBreak extends Block {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
