package org.marknode.internal;

import org.marknode.node.Block;
import org.marknode.node.Document;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.ParserState;

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
