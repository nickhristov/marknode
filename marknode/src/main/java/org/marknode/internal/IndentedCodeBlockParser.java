package org.marknode.internal;

import com.google.gwt.regexp.shared.RegExp;

import org.marknode.node.Block;
import org.marknode.node.IndentedCodeBlock;
import org.marknode.node.Paragraph;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

public class IndentedCodeBlockParser extends AbstractBlockParser {

  public static int INDENT = 4;

  private static final RegExp TRAILING_BLANK_LINES = RegExp.compile("(?:\n[ \t]*)+$");

  private final IndentedCodeBlock block = new IndentedCodeBlock();
  private BlockContent content = new BlockContent();

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    if (state.getIndent() >= INDENT) {
      return BlockContinue.atColumn(state.getColumn() + INDENT);
    } else if (state.isBlank()) {
      return BlockContinue.atIndex(state.getNextNonSpaceIndex());
    } else {
      return BlockContinue.none();
    }
  }

  @Override
  public void addLine(CharSequence line) {
    content.add(line);
  }

  @Override
  public void closeBlock() {
    // add trailing newline
    content.add("");
    String contentString = content.getString();
    content = null;

    String literal = TRAILING_BLANK_LINES.replace(contentString, "\n");
    block.setLiteral(literal);
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      // An indented code block cannot interrupt a paragraph.
      if (state.getIndent() >= INDENT && !state.isBlank() &&
          !(state.getActiveBlockParser().getBlock() instanceof Paragraph)) {
        return BlockStart.of(new IndentedCodeBlockParser()).atColumn(state.getColumn() + INDENT);
      } else {
        return BlockStart.none();
      }
    }
  }
}
