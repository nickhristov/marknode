package org.marknode.internal;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import org.marknode.node.Block;
import org.marknode.node.FencedCodeBlock;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

import static org.marknode.internal.util.Escaping.unescapeString;

public class FencedCodeBlockParser extends AbstractBlockParser {

  private static final RegExp OPENING_FENCE = RegExp.compile("^`{3,}(?!.*`)|^~{3,}(?!.*~)", "g");
  private static final RegExp CLOSING_FENCE = RegExp.compile("^(?:`{3,}|~{3,})(?= *$)", "g");

  private final FencedCodeBlock block = new FencedCodeBlock();
  private BlockContent content = new BlockContent();

  public FencedCodeBlockParser(char fenceChar, int fenceLength, int fenceIndent) {
    block.setFenceChar(fenceChar);
    block.setFenceLength(fenceLength);
    block.setFenceIndent(fenceIndent);
  }

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    int nextNonSpace = state.getNextNonSpaceIndex();
    int newIndex = state.getIndex();
    CharSequence line = state.getLine();
    MatchResult matcher = null;
    CLOSING_FENCE.setLastIndex(0);
    boolean matches = (state.getIndent() <= 3 &&
                       nextNonSpace < line.length() &&
                       line.charAt(nextNonSpace) == block.getFenceChar() &&
                       (matcher = CLOSING_FENCE.exec(
                           line.subSequence(nextNonSpace, line.length()).toString())) != null);
    if (matches && matcher.getGroup(0).length() >= block.getFenceLength()) {
      // closing fence - we're at end of line, so we can finalize now
      return BlockContinue.finished();
    } else {
      // skip optional spaces of fence indent
      int i = block.getFenceIndent();
      while (i > 0 && newIndex < line.length() && line.charAt(newIndex) == ' ') {
        newIndex++;
        i--;
      }
    }
    return BlockContinue.atIndex(newIndex);
  }

  @Override
  public void addLine(CharSequence line) {
    content.add(line);
  }

  @Override
  public void closeBlock() {
    boolean singleLine = content.hasSingleLine();
    // add trailing newline
    content.add("");
    String contentString = content.getString();
    content = null;

    // first line becomes info string
    int firstNewline = contentString.indexOf('\n');
    String firstLine = contentString.substring(0, firstNewline);
    block.setInfo(unescapeString(firstLine.trim()));
    if (singleLine) {
      block.setLiteral("");
    } else {
      String literal = contentString.substring(firstNewline + 1);
      block.setLiteral(literal);
    }
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      int nextNonSpace = state.getNextNonSpaceIndex();
      CharSequence line = state.getLine();
      OPENING_FENCE.setLastIndex(0);
      MatchResult matcher;
      if (state.getIndent() < 4 && (matcher = OPENING_FENCE.exec(
          line.subSequence(nextNonSpace, line.length()).toString())) != null) {
        int fenceLength = matcher.getGroup(0).length();
        char fenceChar = matcher.getGroup(0).charAt(0);
        FencedCodeBlockParser blockParser = new FencedCodeBlockParser(
            fenceChar, fenceLength, state.getIndent());
        return BlockStart.of(blockParser).atIndex(nextNonSpace + fenceLength);
      } else {
        return BlockStart.none();
      }
    }
  }
}
