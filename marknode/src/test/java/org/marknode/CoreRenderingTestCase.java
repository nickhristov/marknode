package org.marknode;

import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;
import org.marknode.test.RenderingTestCase;

public class CoreRenderingTestCase extends RenderingTestCase {

  private static final Parser PARSER = Parser.builder().build();
  private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

  @Override
  protected String render(String source) {
    return RENDERER.render(PARSER.parse(source));
  }
}
