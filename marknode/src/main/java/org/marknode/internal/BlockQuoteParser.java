package org.marknode.internal;

import org.marknode.node.Block;
import org.marknode.node.BlockQuote;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.ParserState;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;

public class BlockQuoteParser extends AbstractBlockParser {

  private final BlockQuote block = new BlockQuote();

  @Override
  public boolean isContainer() {
    return true;
  }

  @Override
  public boolean canContain(Block block) {
    return true;
  }

  @Override
  public BlockQuote getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    int nextNonSpace = state.getNextNonSpaceIndex();
    CharSequence line = state.getLine();
    if (state.getIndent() <= 3 && nextNonSpace < line.length()
        && line.charAt(nextNonSpace) == '>') {
      int newIndex = nextNonSpace + 1;
      if (newIndex < line.length() && line.charAt(newIndex) == ' ') {
        newIndex++;
      }
      return BlockContinue.atIndex(newIndex);
    } else {
      return BlockContinue.none();
    }
  }

  public static class Factory extends AbstractBlockParserFactory {

    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      CharSequence line = state.getLine();
      int nextNonSpace = state.getNextNonSpaceIndex();
      if (state.getIndent() < 4 && line.charAt(nextNonSpace) == '>') {
        int newOffset = nextNonSpace + 1;
        // optional following space
        if (newOffset < line.length() && line.charAt(newOffset) == ' ') {
          newOffset++;
        }
        return BlockStart.of(new BlockQuoteParser()).atIndex(newOffset);
      } else {
        return BlockStart.none();
      }
    }
  }
}
