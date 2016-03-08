package org.wisepersist.marknode.internal;

import org.wisepersist.node.Block;
import org.wisepersist.node.Document;
import org.wisepersist.parser.block.AbstractBlockParser;
import org.wisepersist.parser.block.BlockContinue;
import org.wisepersist.parser.block.ParserState;

public class DocumentBlockParser extends AbstractBlockParser {

    private final Document document = new Document();

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean canContain(Block block) {
        return true;
    }

    @Override
    public Document getBlock() {
        return document;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        return BlockContinue.atIndex(state.getIndex());
    }

    @Override
    public void addLine(CharSequence line) {
    }

}
