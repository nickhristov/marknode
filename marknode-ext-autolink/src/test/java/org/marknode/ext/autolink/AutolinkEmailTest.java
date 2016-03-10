package org.marknode.ext.autolink;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;

public class AutolinkEmailTest extends AutolinkTestCase {

  @DataProvider(name = "data")
  public Object[][] data() {
    return new Object[][]{
        {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.EMAIL)).build(), true, "email"},
        {LinkExtractor.builder().build(), true, "all"},
        {LinkExtractor.builder().emailDomainMustHaveDot(false).build(), false,
         "all, single part domain"}
    };
  }

  @Test(dataProvider = "data")
  public void notLinked(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertNotLinked(linkExtractor, "");
    assertNotLinked(linkExtractor, "foo");
    assertNotLinked(linkExtractor, "@");
    assertNotLinked(linkExtractor, "a@");
    assertNotLinked(linkExtractor, "@a");
    assertNotLinked(linkExtractor, "@@@");
  }

  @Test(dataProvider = "data")
  public void simple(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertLinked(linkExtractor, "foo@example.com", "|foo@example.com|");
    assertLinked(linkExtractor, "foo.bar@example.com", "|foo.bar@example.com|");
  }

  @Test(dataProvider = "data")
  public void allowedText(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    // I know, I know...
    assertLinked(linkExtractor, "#!$%&'*+-/=?^_`{}|~@example.org",
                 "|#!$%&'*+-/=?^_`{}|~@example.org|");
  }

  @Test(dataProvider = "data")
  public void spaceSeparation(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertLinked(linkExtractor, "foo a@b.com", "foo |a@b.com|");
    assertLinked(linkExtractor, "a@b.com foo", "|a@b.com| foo");
    assertLinked(linkExtractor, "\na@b.com", "\n|a@b.com|");
    assertLinked(linkExtractor, "a@b.com\n", "|a@b.com|\n");
  }

  @Test(dataProvider = "data")
  public void specialSeparation(LinkExtractor linkExtractor, boolean domainMustHaveDot,
                                String desc) {
    assertLinked(linkExtractor, "(a@example.com)", "(|a@example.com|)");
    assertLinked(linkExtractor, "\"a@example.com\"", "\"|a@example.com|\"");
    assertLinked(linkExtractor, "\"a@example.com\"", "\"|a@example.com|\"");
    assertLinked(linkExtractor, ",a@example.com,", ",|a@example.com|,");
    assertLinked(linkExtractor, ":a@example.com:", ":|a@example.com|:");
    assertLinked(linkExtractor, ";a@example.com;", ";|a@example.com|;");
  }

  @Test(dataProvider = "data")
  public void dots(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertNotLinked(linkExtractor, ".@example.com");
    assertNotLinked(linkExtractor, "foo.@example.com");
    assertLinked(linkExtractor, ".foo@example.com", ".|foo@example.com|");
    assertLinked(linkExtractor, ".foo@example.com", ".|foo@example.com|");
    assertLinked(linkExtractor, "a..b@example.com", "a..|b@example.com|");
    assertLinked(linkExtractor, "a@example.com.", "|a@example.com|.");
  }

  @Test(dataProvider = "data")
  public void domainWithoutDot(LinkExtractor linkExtractor, boolean domainMustHaveDot,
                               String desc) {
    if (domainMustHaveDot) {
      assertNotLinked(linkExtractor, "a@b");
      assertNotLinked(linkExtractor, "a@b.");
      assertLinked(linkExtractor, "a@b.com.", "|a@b.com|.");
    } else {
      assertLinked(linkExtractor, "a@b", "|a@b|");
      assertLinked(linkExtractor, "a@b.", "|a@b|.");
    }
  }

  @Test(dataProvider = "data")
  public void dashes(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertLinked(linkExtractor, "a@example.com-", "|a@example.com|-");
    assertLinked(linkExtractor, "a@foo-bar.com", "|a@foo-bar.com|");
    assertNotLinked(linkExtractor, "a@-foo.com");
    if (domainMustHaveDot) {
      assertNotLinked(linkExtractor, "a@b-.");
    } else {
      assertLinked(linkExtractor, "a@b-.", "|a@b|-.");
    }
  }

  @Test(dataProvider = "data")
  public void multiple(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertLinked(linkExtractor, "a@example.com b@example.com", "|a@example.com| |b@example.com|");
    assertLinked(linkExtractor, "a@example.com @ b@example.com",
                 "|a@example.com| @ |b@example.com|");
  }

  @Test(dataProvider = "data")
  public void international(LinkExtractor linkExtractor, boolean domainMustHaveDot, String desc) {
    assertLinked(linkExtractor, "üñîçøðé@example.com", "|üñîçøðé@example.com|");
    assertLinked(linkExtractor, "üñîçøðé@üñîçøðé.com", "|üñîçøðé@üñîçøðé.com|");
  }

  private void assertLinked(LinkExtractor linkExtractor, String input, String expected) {
    super.assertLinked(linkExtractor, input, expected, LinkType.EMAIL);
  }

}
