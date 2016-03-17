package org.marknode.internal;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import org.marknode.node.Block;
import org.marknode.node.Heading;
import org.marknode.parser.InlineParser;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

public class HeadingParser extends AbstractBlockParser {

  private static RegExp ATX_HEADING = RegExp.compile("^#{1,6}(?: +|$)");
  private static RegExp ATX_TRAILING = RegExp.compile("(^| ) *#+ *$");
  private static RegExp SETEXT_HEADING = RegExp.compile("^(?:=+|-+) *$");

  private final Heading block = new Heading();
  private final String content;

  public HeadingParser(int level, String content) {
    block.setLevel(level);
    this.content = content;
  }

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState parserState) {
    // In both ATX and Setext headings, once we have the heading markup, there's nothing
    // more to parse.
    return BlockContinue.none();
  }

  @Override
  public void parseInlines(InlineParser inlineParser) {
    inlineParser.parse(content, block);
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      if (state.getIndent() >= 4) {
        return BlockStart.none();
      }
      CharSequence line = state.getLine();
      int nextNonSpace = state.getNextNonSpaceIndex();
      CharSequence paragraph = matchedBlockParser.getParagraphContent();
      MatchResult matcher;
      if ((matcher = ATX_HEADING.exec(
          line.subSequence(nextNonSpace, line.length()).toString())) != null) {
        // ATX heading
        int newOffset = nextNonSpace + matcher.getGroup(0).length();
        int level = matcher.getGroup(0).trim().length(); // number of #s
        // remove trailing ###s:
        String content = line.subSequence(newOffset, line.length()).toString()
            .replaceAll(ATX_TRAILING.getSource(), "");
        return BlockStart.of(new HeadingParser(level, content)).atIndex(line.length());
      } else if (paragraph != null && ((matcher = SETEXT_HEADING.exec(
          line.subSequence(nextNonSpace, line.length()).toString())) != null)) {
        // setext heading line
        int level = matcher.getGroup(0).charAt(0) == '=' ? 1 : 2;
        String content = paragraph.toString();
        return BlockStart.of(new HeadingParser(level, content))
            .atIndex(line.length())
            .replaceActiveBlockParser();
      } else {
        return BlockStart.none();
      }
    }
  }
}
