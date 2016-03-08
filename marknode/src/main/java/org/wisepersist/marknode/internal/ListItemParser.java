package org.wisepersist.marknode.internal;

import org.wisepersist.node.Block;
import org.wisepersist.node.ListItem;
import org.wisepersist.parser.block.AbstractBlockParser;
import org.wisepersist.parser.block.BlockContinue;
import org.wisepersist.parser.block.ParserState;

public class ListItemParser extends AbstractBlockParser {

    private final ListItem block = new ListItem();

    private int itemIndent;

    public ListItemParser(int itemIndent) {
        this.itemIndent = itemIndent;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean canContain(Block block) {
        return true;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        if (state.isBlank() && block.getFirstChild() != null) {
            return BlockContinue.atIndex(state.getNextNonSpaceIndex());
        }

        if (state.getIndent() >= itemIndent) {
            return BlockContinue.atColumn(state.getColumn() + itemIndent);
        } else {
            return BlockContinue.none();
        }
    }

}
