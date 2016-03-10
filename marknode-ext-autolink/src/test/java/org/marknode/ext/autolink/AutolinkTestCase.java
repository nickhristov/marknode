package org.marknode.ext.autolink;

import static org.testng.Assert.assertEquals;

public abstract class AutolinkTestCase {

  protected void assertLinked(LinkExtractor linkExtractor, String input, String expected,
                              LinkType expectedLinkType) {
    String result = link(linkExtractor, input, "|", expectedLinkType);
    assertEquals(expected, result);
  }

  protected void assertNotLinked(LinkExtractor linkExtractor, String input) {
    String result = link(linkExtractor, input, "|", null);
    assertEquals(input, result);
  }

  protected String link(LinkExtractor linkExtractor, String input, final String marker,
                        final LinkType expectedLinkType) {
    Iterable<LinkSpan> links = linkExtractor.extractLinks(input);
    return Autolink.renderLinks(input, links, new LinkRenderer() {
      @Override
      public void render(LinkSpan link, CharSequence text, StringBuilder sb) {
        if (expectedLinkType != null) {
          assertEquals(expectedLinkType, link.getType());
        }
        sb.append(marker);
        sb.append(text, link.getBeginIndex(), link.getEndIndex());
        sb.append(marker);
      }
    });
  }

}
