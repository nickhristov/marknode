package org.wisepersist.marknode.internal;


import org.wisepersist.marknode.node.Block;
import org.wisepersist.marknode.node.ThematicBreak;
import org.wisepersist.marknode.parser.block.AbstractBlockParser;
import org.wisepersist.marknode.parser.block.AbstractBlockParserFactory;
import org.wisepersist.marknode.parser.block.BlockContinue;
import org.wisepersist.marknode.parser.block.BlockStart;
import org.wisepersist.marknode.parser.block.MatchedBlockParser;
import org.wisepersist.marknode.parser.block.ParserState;

import java.util.regex.Pattern;

public class ThematicBreakParser extends AbstractBlockParser {

  private static Pattern PATTERN = Pattern.compile("^(?:(?:\\* *){3,}|(?:_ *){3,}|(?:- *){3,}) *$");

  private final ThematicBreak block = new ThematicBreak();

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    // a horizontal rule can never container > 1 line, so fail to match
    return BlockContinue.none();
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      if (state.getIndent() >= 4) {
        return BlockStart.none();
      }
      int nextNonSpace = state.getNextNonSpaceIndex();
      CharSequence line = state.getLine();
      if (PATTERN.matcher(line.subSequence(nextNonSpace, line.length())).matches()) {
        return BlockStart.of(new ThematicBreakParser()).atIndex(line.length());
      } else {
        return BlockStart.none();
      }
    }
  }
}
