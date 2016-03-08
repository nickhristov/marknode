package org.marknode.internal;

import org.marknode.internal.util.Parsing;
import org.marknode.node.Block;
import org.marknode.node.Paragraph;
import org.marknode.parser.InlineParser;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.ParserState;

public class ParagraphParser extends AbstractBlockParser {

  private final Paragraph block = new Paragraph();
  private BlockContent content = new BlockContent();

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public BlockContinue tryContinue(ParserState state) {
    if (!state.isBlank()) {
      return BlockContinue.atIndex(state.getIndex());
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
  }

  public void closeBlock(InlineParserImpl inlineParser) {
    String contentString = content.getString();
    boolean hasReferenceDefs = false;

    int pos;
    // try parsing the beginning as link reference definitions:
    while (contentString.length() > 3 && contentString.charAt(0) == '[' &&
           (pos = inlineParser.parseReference(contentString)) != 0) {
      contentString = contentString.substring(pos);
      hasReferenceDefs = true;
    }
    if (hasReferenceDefs && Parsing.isBlank(contentString)) {
      block.unlink();
      content = null;
    } else {
      content = new BlockContent(contentString);
    }
  }

  @Override
  public void parseInlines(InlineParser inlineParser) {
    if (content != null) {
      inlineParser.parse(content.getString(), block);
    }
  }

  public String getContentString() {
    return content.getString();
  }
}
