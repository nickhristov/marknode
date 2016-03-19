package org.marknode.internal;


import com.google.gwt.regexp.shared.RegExp;

import org.marknode.node.Block;
import org.marknode.node.ThematicBreak;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

public class ThematicBreakParser extends AbstractBlockParser {

  private static RegExp PATTERN = RegExp.compile("^(?:(?:\\* *){3,}|(?:_ *){3,}|(?:- *){3,}) *$");

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
      if (PATTERN.test(line.subSequence(nextNonSpace, line.length()).toString())) {
        return BlockStart.of(new ThematicBreakParser()).atIndex(line.length());
      } else {
        return BlockStart.none();
      }
    }
  }
}
