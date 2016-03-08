package org.wisepersist.marknode.parser.block;

import org.wisepersist.marknode.node.Block;
import org.wisepersist.marknode.parser.InlineParser;

public abstract class AbstractBlockParser implements BlockParser {

  @Override
  public boolean isContainer() {
    return false;
  }

  @Override
  public boolean canContain(Block block) {
    return false;
  }

  @Override
  public void addLine(CharSequence line) {
  }

  @Override
  public void closeBlock() {
  }

  @Override
  public void parseInlines(InlineParser inlineParser) {
  }

}
