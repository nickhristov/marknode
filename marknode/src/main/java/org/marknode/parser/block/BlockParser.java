package org.marknode.parser.block;

import org.marknode.node.Block;
import org.marknode.parser.InlineParser;

/**
 * Parser for a specific block node.
 * <p>
 * Implementations should subclass {@link AbstractBlockParser} instead of implementing
 * this directly.
 */
public interface BlockParser {

  /**
   * Return true if the block that is parsed is a container (contains other blocks), or
   * false if it's a leaf.
   */
  boolean isContainer();

  boolean canContain(Block block);

  Block getBlock();

  BlockContinue tryContinue(ParserState parserState);

  void addLine(CharSequence line);

  void closeBlock();

  void parseInlines(InlineParser inlineParser);
}
