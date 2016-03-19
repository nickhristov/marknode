package org.marknode.ext.front.matter;

import org.marknode.Extension;
import org.marknode.node.Node;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;
import org.marknode.test.RenderingTestCase;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class YamlFrontMatterTest extends RenderingTestCase {

  private static final Set<Extension> EXTENSIONS =
      Collections.singleton(YamlFrontMatterExtension.create());
  private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();
  private static final HtmlRenderer RENDERER =
      HtmlRenderer.builder().extensions(EXTENSIONS).build();

  @Test
  public void simpleValue() {
    final String input = "---" +
                         "\nhello: world" +
                         "\n..." +
                         "\n" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 1);
    assertEquals(data.keySet().iterator().next(), "hello");
    assertEquals(data.get("hello").size(), 1);
    assertEquals(data.get("hello").get(0), "world");

    assertRendering(input, rendered);
  }

  @Test
  public void emptyValue() {
    final String input = "---" +
                         "\nkey:" +
                         "\n---" +
                         "\n" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 1);
    assertEquals(data.keySet().iterator().next(), "key");
    assertEquals(data.get("key").size(), 0);

    assertRendering(input, rendered);
  }

  @Test
  public void listValues() {
    final String input = "---" +
                         "\nlist:" +
                         "\n  - value1" +
                         "\n  - value2" +
                         "\n..." +
                         "\n" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 1);
    assertTrue(data.containsKey("list"));
    assertEquals(data.get("list").size(), 2);
    assertEquals(data.get("list").get(0), "value1");
    assertEquals(data.get("list").get(1), "value2");

    assertRendering(input, rendered);
  }

  @Test
  public void literalValue1() {
    final String input = "---" +
                         "\nliteral: |" +
                         "\n  hello markdown!" +
                         "\n  literal thing..." +
                         "\n---" +
                         "\n" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 1);
    assertTrue(data.containsKey("literal"));
    assertEquals(data.get("literal").size(), 1);
    assertEquals(data.get("literal").get(0), "hello markdown!\nliteral thing...");

    assertRendering(input, rendered);
  }

  @Test
  public void literalValue2() {
    final String input = "---" +
                         "\nliteral: |" +
                         "\n  - hello markdown!" +
                         "\n---" +
                         "\n" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 1);
    assertTrue(data.containsKey("literal"));
    assertEquals(data.get("literal").size(), 1);
    assertEquals(data.get("literal").get(0), "- hello markdown!");

    assertRendering(input, rendered);
  }

  @Test
  public void complexValues() {
    final String input = "---" +
                         "\nsimple: value" +
                         "\nliteral: |" +
                         "\n  hello markdown!" +
                         "\n" +
                         "\n  literal literal" +
                         "\nlist:" +
                         "\n    - value1" +
                         "\n    - value2" +
                         "\n---" +
                         "\ngreat";
    final String rendered = "<p>great</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertEquals(data.size(), 3);

    assertTrue(data.containsKey("simple"));
    assertEquals(data.get("simple").size(), 1);
    assertEquals(data.get("simple").get(0), "value");

    assertTrue(data.containsKey("literal"));
    assertEquals(data.get("literal").size(), 1);
    assertEquals(data.get("literal").get(0), "hello markdown!\n\nliteral literal");

    assertTrue(data.containsKey("list"));
    assertEquals(data.get("list").size(), 2);
    assertEquals(data.get("list").get(0), "value1");
    assertEquals(data.get("list").get(1), "value2");

    assertRendering(input, rendered);
  }

  @Test
  public void yamlInParagraph() {
    final String input = "# hello\n" +
                         "\nhello markdown world!" +
                         "\n---" +
                         "\nhello: world" +
                         "\n---";
    final String
        rendered = "<h1>hello</h1>\n<h2>hello markdown world!</h2>\n<h2>hello: world</h2>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertTrue(data.isEmpty());

    assertRendering(input, rendered);
  }

  @Test
  public void nonMatchedStartTag() {
    final String input = "----\n" +
                         "test";
    final String rendered = "<hr />\n<p>test</p>\n";

    YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
    Node document = PARSER.parse(input);
    document.accept(visitor);

    Map<String, List<String>> data = visitor.getData();

    assertTrue(data.isEmpty());

    assertRendering(input, rendered);
  }

  @Override
  protected String render(String source) {
    return RENDERER.render(PARSER.parse(source));
  }
}
