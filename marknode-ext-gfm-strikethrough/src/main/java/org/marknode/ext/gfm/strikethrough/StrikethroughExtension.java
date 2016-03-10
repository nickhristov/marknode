package org.marknode.ext.gfm.strikethrough;

import org.marknode.Extension;
import org.marknode.ext.gfm.strikethrough.internal.StrikethroughDelimiterProcessor;
import org.marknode.ext.gfm.strikethrough.internal.StrikethroughHtmlRenderer;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;

/**
 * Extension for GFM strikethrough using ~~ (GitHub Flavored Markdown).
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link org.marknode.parser.Parser.Builder#extensions(Iterable)},
 * {@link org.marknode.renderer.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed strikethrough text regions are turned into {@link Strikethrough} nodes.
 * </p>
 */
public class StrikethroughExtension
    implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

  private StrikethroughExtension() {
  }

  public static Extension create() {
    return new StrikethroughExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customDelimiterProcessor(new StrikethroughDelimiterProcessor());
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder) {
    rendererBuilder.customHtmlRenderer(new StrikethroughHtmlRenderer());
  }

}
