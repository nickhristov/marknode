package org.marknode.renderer.html;


import org.marknode.node.FencedCodeBlock;
import org.marknode.node.Node;
import org.marknode.parser.Parser;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;

public class HtmlRendererTest {

  @Test
  public void htmlAllowingShouldNotEscapeInlineHtml() {
    String rendered = htmlAllowingRenderer().render(
        parse("paragraph with <span id='foo' class=\"bar\">inline &amp; html</span>"));
    assertEquals(rendered,
                 "<p>paragraph with <span id='foo' class=\"bar\">inline &amp; html</span></p>\n");
  }

  @Test
  public void htmlAllowingShouldNotEscapeBlockHtml() {
    String rendered = htmlAllowingRenderer().render(
        parse("<div id='foo' class=\"bar\">block &amp;</div>"));
    assertEquals(rendered, "<div id='foo' class=\"bar\">block &amp;</div>\n");
  }

  @Test
  public void htmlEscapingShouldEscapeInlineHtml() {
    String rendered = htmlEscapingRenderer().render(
        parse("paragraph with <span id='foo' class=\"bar\">inline &amp; html</span>"));
    // Note that &amp; is not escaped, as it's a normal text node, not part of the inline HTML.
    assertEquals(rendered,
                 "<p>paragraph with &lt;span id='foo' class=&quot;bar&quot;&gt;inline &amp; html&lt;/span&gt;</p>\n");
  }

  @Test
  public void htmlEscapingShouldEscapeHtmlBlocks() {
    String rendered = htmlEscapingRenderer().render(
        parse("<div id='foo' class=\"bar\">block &amp;</div>"));
    assertEquals(rendered,
                 "&lt;div id='foo' class=&quot;bar&quot;&gt;block &amp;amp;&lt;/div&gt;\n");
  }

  @Test
  public void textEscaping() {
    String rendered = defaultRenderer().render(parse("escaping: & < > \" '"));
    assertEquals(rendered, "<p>escaping: &amp; &lt; &gt; &quot; '</p>\n");
  }

  @Test
  public void percendEncodeUrlDisabled() {
    assertEquals(defaultRenderer().render(parse("[a](foo&amp;bar)")),
                 "<p><a href=\"foo&amp;bar\">a</a></p>\n");
    assertEquals(defaultRenderer().render(parse("[a](ä)")),
                 "<p><a href=\"ä\">a</a></p>\n");
    assertEquals(defaultRenderer().render(parse("[a](foo%20bar)")),
                 "<p><a href=\"foo%20bar\">a</a></p>\n");
  }

  @Test
  public void percentEncodeUrl() {
    // Entities are escaped anyway
    assertEquals(percentEncodingRenderer().render(parse("[a](foo&amp;bar)")),
                 "<p><a href=\"foo&amp;bar\">a</a></p>\n");
    // Existing encoding is preserved
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%20bar)")),
                 "<p><a href=\"foo%20bar\">a</a></p>\n");
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%61)")),
                 "<p><a href=\"foo%61\">a</a></p>\n");
    // Invalid encoding is escaped
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%)")),
                 "<p><a href=\"foo%25\">a</a></p>\n");
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%a)")),
                 "<p><a href=\"foo%25a\">a</a></p>\n");
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%a_)")),
                 "<p><a href=\"foo%25a_\">a</a></p>\n");
    assertEquals(percentEncodingRenderer().render(parse("[a](foo%xx)")),
                 "<p><a href=\"foo%25xx\">a</a></p>\n");
    // Reserved characters are preserved, except for '[' and ']'
    assertEquals(percentEncodingRenderer().render(parse("[a](!*'();:@&=+$,/?#[])")),
                 "<p><a href=\"!*'();:@&amp;=+$,/?#%5B%5D\">a</a></p>\n");
    // Unreserved characters are preserved
    assertEquals(percentEncodingRenderer().render(
        parse("[a](ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~)")),
                 "<p><a href=\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~\">a</a></p>\n");
    // Other characters are percent-encoded (LATIN SMALL LETTER A WITH DIAERESIS)
    assertEquals("<p><a href=\"%C3%A4\">a</a></p>\n",
                 percentEncodingRenderer().render(parse("[a](ä)")));
    // Other characters are percent-encoded (MUSICAL SYMBOL G CLEF, surrogate pair in UTF-16)
    assertEquals("<p><a href=\"%F0%9D%84%9E\">a</a></p>\n",
                 percentEncodingRenderer().render(parse("[a](\uD834\uDD1E)")));
  }

  @Test
  public void attributeProvider() {
    AttributeProvider custom = new AttributeProvider() {
      @Override
      public void setAttributes(Node node, Map<String, String> attributes) {
        if (node instanceof FencedCodeBlock) {
          FencedCodeBlock fencedCodeBlock = (FencedCodeBlock) node;
          // Remove the default attribute for info
          attributes.remove("class");
          // Put info in custom attribute instead
          attributes.put("data-custom", fencedCodeBlock.getInfo());
        }
      }
    };

    HtmlRenderer renderer = HtmlRenderer.builder().attributeProvider(custom).build();
    String rendered = renderer.render(parse("```info\ncontent\n```"));
    assertEquals("<pre><code data-custom=\"info\">content\n</code></pre>\n", rendered);

    String rendered2 = renderer.render(parse("```evil\"\ncontent\n```"));
    assertEquals("<pre><code data-custom=\"evil&quot;\">content\n</code></pre>\n", rendered2);
  }

  @Test
  public void orderedListStartZero() {
    assertEquals("<ol start=\"0\">\n<li>Test</li>\n</ol>\n",
                 defaultRenderer().render(parse("0. Test\n")));
  }

  private static HtmlRenderer defaultRenderer() {
    return HtmlRenderer.builder().build();
  }

  private static HtmlRenderer htmlAllowingRenderer() {
    return HtmlRenderer.builder().escapeHtml(false).build();
  }

  private static HtmlRenderer htmlEscapingRenderer() {
    return HtmlRenderer.builder().escapeHtml(true).build();
  }

  private static HtmlRenderer percentEncodingRenderer() {
    return HtmlRenderer.builder().percentEncodeUrls(true).build();
  }

  private static Node parse(String source) {
    return Parser.builder().build().parse(source);
  }
}
