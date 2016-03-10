package org.marknode.ext.gfm.strikethrough;

import org.marknode.node.CustomNode;
import org.marknode.node.Delimited;

/**
 * A strikethrough node containing text and other inline nodes nodes as children.
 */
public class Strikethrough extends CustomNode implements Delimited {

  private static final String DELIMITER = "~~";

  @Override
  public String getOpeningDelimiter() {
    return DELIMITER;
  }

  @Override
  public String getClosingDelimiter() {
    return DELIMITER;
  }
}
