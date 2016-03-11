package org.marknode.ext.gfm.tables;

import org.marknode.Extension;
import org.marknode.ext.gfm.tables.internal.TableBlockParser;
import org.marknode.ext.gfm.tables.internal.TableHtmlRenderer;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;

/**
 * Extension for GFM tables using "|" pipes (GitHub Flavored Markdown).
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link org.marknode.parser.Parser.Builder#extensions(Iterable)},
 * {@link org.marknode.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed tables are turned into {@link TableBlock} blocks.
 * </p>
 */
public class TablesExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

  private TablesExtension() {
  }

  public static Extension create() {
    return new TablesExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customBlockParserFactory(new TableBlockParser.Factory());
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder) {
    rendererBuilder.customHtmlRenderer(new TableHtmlRenderer());
  }

}
