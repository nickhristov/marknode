package org.marknode.ext.front.matter;

import org.marknode.Extension;
import org.marknode.ext.front.matter.internal.YamlFrontMatterBlockParser;
import org.marknode.ext.front.matter.internal.YamlFrontMatterBlockRenderer;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;

/**
 * Extension for YAML-like metadata.
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link org.marknode.parser.Parser.Builder#extensions(Iterable)},
 * {@link org.marknode.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed metadata is turned into {@link YamlFrontMatterNode}. You can access the
 * metadata using {@link YamlFrontMatterVisitor}.
 * </p>
 */
public class YamlFrontMatterExtension
    implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

  private YamlFrontMatterExtension() {
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder) {
    rendererBuilder.customHtmlRenderer(new YamlFrontMatterBlockRenderer());
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customBlockParserFactory(new YamlFrontMatterBlockParser.Factory());
  }

  public static Extension create() {
    return new YamlFrontMatterExtension();
  }
}
