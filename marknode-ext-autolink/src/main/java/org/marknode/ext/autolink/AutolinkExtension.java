package org.marknode.ext.autolink;

import org.marknode.Extension;
import org.marknode.ext.autolink.internal.AutolinkPostProcessor;
import org.marknode.parser.Parser;

/**
 * Extension for automatically turning plain URLs and email addresses into links.
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link org.marknode.parser.Parser.Builder#extensions(Iterable)},
 * {@link org.marknode.renderer.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed links are turned into normal {@link org.marknode.node.Link} nodes.
 * </p>
 */
public class AutolinkExtension implements Parser.ParserExtension {

  private AutolinkExtension() {
  }

  public static Extension create() {
    return new AutolinkExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.postProcessor(new AutolinkPostProcessor());
  }

}
