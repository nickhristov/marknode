package org.marknode.internal;

import com.google.gwt.regexp.shared.RegExp;

import org.marknode.internal.util.Parsing;
import org.marknode.node.Block;
import org.marknode.node.HtmlBlock;
import org.marknode.node.Paragraph;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

public class HtmlBlockParser extends AbstractBlockParser {

  private static final RegExp[][] BLOCK_PATTERNS = new RegExp[][]{
      {null, null}, // not used (no type 0)
      {
          RegExp.compile("^<(?:script|pre|style)(?:\\s|>|$)", "i"),
          RegExp.compile("</(?:script|pre|style)>", "i")
      },
      {
          RegExp.compile("^<!--"),
          RegExp.compile("-->")
      },
      {
          RegExp.compile("^<[?]"),
          RegExp.compile("\\?>")
      },
      {
          RegExp.compile("^<![A-Z]"),
          RegExp.compile(">")
      },
      {
          RegExp.compile("^<!\\[CDATA\\["),
          RegExp.compile("\\]\\]>")
      },
      {
          RegExp.compile("^</?(?:" +
                         "address|article|aside|" +
                         "base|basefont|blockquote|body|" +
                         "caption|center|col|colgroup|" +
                         "dd|details|dialog|dir|div|dl|dt|" +
                         "fieldset|figcaption|figure|footer|form|frame|frameset|" +
                         "h1|head|header|hr|html|" +
                         "iframe|" +
                         "legend|li|link|" +
                         "main|menu|menuitem|meta|" +
                         "nav|noframes|" +
                         "ol|optgroup|option|" +
                         "p|param|" +
                         "section|source|summary|" +
                         "table|tbody|td|tfoot|th|thead|title|tr|track|" +
                         "ul" +
                         ")(?:\\s|[/]?[>]|$)", "i"),
          null // terminated by blank line
      },
      {
          RegExp.compile("^(?:" + Parsing.OPENTAG + '|' + Parsing.CLOSETAG + ")\\s*$", "i"),
          null // terminated by blank line
      }
  };

  private final HtmlBlock block = new HtmlBlock();
  private final RegExp closingPattern;

  private boolean finished = false;
  private BlockContent content = new BlockContent();

  private HtmlBlockParser(RegExp closingPattern) {
    this.closingPattern = closingPattern;
  }

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    if (finished) {
      return BlockContinue.none();
    }

    // Blank line ends type 6 and type 7 blocks
    if (state.isBlank() && closingPattern == null) {
      return BlockContinue.none();
    } else {
      return BlockContinue.atIndex(state.getIndex());
    }
  }

  @Override
  public void addLine(CharSequence line) {
    content.add(line);

    if (closingPattern != null && closingPattern.test(line.toString())) {
      finished = true;
    }
  }

  @Override
  public void closeBlock() {
    block.setLiteral(content.getString());
    content = null;
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      int nextNonSpace = state.getNextNonSpaceIndex();
      CharSequence line = state.getLine();

      if (state.getIndent() < 4 && line.charAt(nextNonSpace) == '<') {
        for (int blockType = 1; blockType <= 7; blockType++) {
          // Type 7 can not interrupt a paragraph
          if (blockType == 7 && matchedBlockParser.getMatchedBlockParser()
              .getBlock() instanceof Paragraph) {
            continue;
          }
          RegExp opener = BLOCK_PATTERNS[blockType][0];
          RegExp closer = BLOCK_PATTERNS[blockType][1];
          boolean matches = opener.test(line.subSequence(nextNonSpace, line.length()).toString());
          if (matches) {
            return BlockStart.of(new HtmlBlockParser(closer)).atIndex(state.getIndex());
          }
        }
      }
      return BlockStart.none();
    }
  }
}
