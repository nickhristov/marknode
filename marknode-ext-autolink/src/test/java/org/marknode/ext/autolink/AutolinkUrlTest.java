package org.marknode.ext.autolink;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;

import static org.testng.Assert.assertEquals;

public class AutolinkUrlTest extends AutolinkTestCase {

  @DataProvider(name = "data")
  public Object[][] data() {
    return new Object[][]{
        {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.URL)).build(), "URL"},
        {LinkExtractor.builder().build(), "all"}
    };
  }

  @Test(dataProvider = "data")
  public void notLinked(LinkExtractor linkExtractor, String description) {
    assertNotLinked(linkExtractor, "");
    assertNotLinked(linkExtractor, "foo");
    assertNotLinked(linkExtractor, ":");
    assertNotLinked(linkExtractor, "://");
    assertNotLinked(linkExtractor, ":::");
  }

  @Test(dataProvider = "data")
  public void schemes(LinkExtractor linkExtractor, String description) {
    assertNotLinked(linkExtractor, "://foo");
    assertNotLinked(linkExtractor, "1://foo");
    assertNotLinked(linkExtractor, "123://foo");
    assertNotLinked(linkExtractor, "+://foo");
    assertNotLinked(linkExtractor, "-://foo");
    assertNotLinked(linkExtractor, ".://foo");
    assertNotLinked(linkExtractor, "1abc://foo");
    assertLinked(linkExtractor, "a://foo", "|a://foo|");
    assertLinked(linkExtractor, "a123://foo", "|a123://foo|");
    assertLinked(linkExtractor, "a123b://foo", "|a123b://foo|");
    assertLinked(linkExtractor, "a+b://foo", "|a+b://foo|");
    assertLinked(linkExtractor, "a-b://foo", "|a-b://foo|");
    assertLinked(linkExtractor, "a.b://foo", "|a.b://foo|");
    assertLinked(linkExtractor, "ABC://foo", "|ABC://foo|");
  }

  @Test(dataProvider = "data")
  public void hostTooShort(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "ab://", "ab://");
  }

  @Test(dataProvider = "data")
  public void linking(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "ab://c", "|ab://c|");
    assertLinked(linkExtractor, "http://example.org/", "|http://example.org/|");
    assertLinked(linkExtractor, "http://example.org/123", "|http://example.org/123|");
    assertLinked(linkExtractor, "http://example.org/?foo=test&bar=123",
                 "|http://example.org/?foo=test&bar=123|");
  }

  @Test(dataProvider = "data")
  public void schemeSeparatedByNonAlphanumeric(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, ".http://example.org/", ".|http://example.org/|");
  }

  @Test(dataProvider = "data")
  public void spaceSeparation(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "foo http://example.org/", "foo |http://example.org/|");
    assertLinked(linkExtractor, "http://example.org/ bar", "|http://example.org/| bar");
  }

  @Test(dataProvider = "data")
  public void delimiterSeparation(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org/.", "|http://example.org/|.");
    assertLinked(linkExtractor, "http://example.org/..", "|http://example.org/|..");
    assertLinked(linkExtractor, "http://example.org/,", "|http://example.org/|,");
    assertLinked(linkExtractor, "http://example.org/:", "|http://example.org/|:");
    assertLinked(linkExtractor, "http://example.org/?", "|http://example.org/|?");
    assertLinked(linkExtractor, "http://example.org/!", "|http://example.org/|!");
    assertLinked(linkExtractor, "http://example.org/;", "|http://example.org/|;");
  }

  @Test(dataProvider = "data")
  public void matchingPunctuation(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org/a(b)", "|http://example.org/a(b)|");
    assertLinked(linkExtractor, "http://example.org/a[b]", "|http://example.org/a[b]|");
    assertLinked(linkExtractor, "http://example.org/a{b}", "|http://example.org/a{b}|");
    assertLinked(linkExtractor, "http://example.org/a<b>", "|http://example.org/a<b>|");
    assertLinked(linkExtractor, "http://example.org/a\"b\"", "|http://example.org/a\"b\"|");
    assertLinked(linkExtractor, "http://example.org/a'b'", "|http://example.org/a'b'|");
    assertLinked(linkExtractor, "(http://example.org/)", "(|http://example.org/|)");
    assertLinked(linkExtractor, "[http://example.org/]", "[|http://example.org/|]");
    assertLinked(linkExtractor, "{http://example.org/}", "{|http://example.org/|}");
    assertLinked(linkExtractor, "\"http://example.org/\"", "\"|http://example.org/|\"");
    assertLinked(linkExtractor, "'http://example.org/'", "'|http://example.org/|'");
  }

  @Test(dataProvider = "data")
  public void matchingPunctuationTricky(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "((http://example.org/))", "((|http://example.org/|))");
    assertLinked(linkExtractor, "((http://example.org/a(b)))", "((|http://example.org/a(b)|))");
    assertLinked(linkExtractor, "[(http://example.org/)]", "[(|http://example.org/|)]");
    assertLinked(linkExtractor, "(http://example.org/).", "(|http://example.org/|).");
    assertLinked(linkExtractor, "(http://example.org/.)", "(|http://example.org/|.)");
    assertLinked(linkExtractor, "http://example.org/>", "|http://example.org/|>");
    // not sure about these
    assertLinked(linkExtractor, "http://example.org/(", "|http://example.org/|(");
    assertLinked(linkExtractor, "http://example.org/(.", "|http://example.org/|(.");
    assertLinked(linkExtractor, "http://example.org/]()", "|http://example.org/|]()");
  }

  @Test(dataProvider = "data")
  public void quotes(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org/\"_(foo)", "|http://example.org/\"_(foo)|");
    assertLinked(linkExtractor, "http://example.org/\"_(foo)\"", "|http://example.org/\"_(foo)\"|");
    assertLinked(linkExtractor, "http://example.org/\"\"", "|http://example.org/\"\"|");
    assertLinked(linkExtractor, "http://example.org/\"\"\"", "|http://example.org/\"\"|\"");
    assertLinked(linkExtractor, "http://example.org/\".", "|http://example.org/|\".");
    assertLinked(linkExtractor, "http://example.org/\"a", "|http://example.org/\"a|");
    assertLinked(linkExtractor, "http://example.org/it's", "|http://example.org/it's|");
  }

  @Test(dataProvider = "data")
  public void html(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org\">", "|http://example.org|\">");
    assertLinked(linkExtractor, "http://example.org'>", "|http://example.org|'>");
    assertLinked(linkExtractor, "http://example.org\"/>", "|http://example.org|\"/>");
    assertLinked(linkExtractor, "http://example.org'/>", "|http://example.org|'/>");
  }

  @Test(dataProvider = "data")
  public void css(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org\");", "|http://example.org|\");");
    assertLinked(linkExtractor, "http://example.org');", "|http://example.org|');");
  }

  @Test(dataProvider = "data")
  public void slash(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://example.org/", "|http://example.org/|");
    assertLinked(linkExtractor, "http://example.org/a/", "|http://example.org/a/|");
    assertLinked(linkExtractor, "http://example.org//", "|http://example.org//|");
  }

  @Test(dataProvider = "data")
  public void multiple(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor,
                 "http://one.org/ http://two.org/", "|http://one.org/| |http://two.org/|");
    assertLinked(linkExtractor,
                 "http://one.org/ : http://two.org/", "|http://one.org/| : |http://two.org/|");
    assertLinked(linkExtractor,
                 "(http://one.org/)(http://two.org/)", "(|http://one.org/|)(|http://two.org/|)");
  }

  @Test(dataProvider = "data")
  public void international(LinkExtractor linkExtractor, String description) {
    assertLinked(linkExtractor, "http://üñîçøðé.com/ä", "|http://üñîçøðé.com/ä|");
  }

  @Test(dataProvider = "data")
  public void linkToString(LinkExtractor linkExtractor, String description) {
    Iterable<LinkSpan> links = linkExtractor.extractLinks("wow, so example: http://test.com");
    assertEquals("Link{type=URL, beginIndex=17, endIndex=32}", links.iterator().next().toString());
  }

  private void assertLinked(LinkExtractor linkExtractor, String input, String expected) {
    super.assertLinked(linkExtractor, input, expected, LinkType.URL);
  }
}
