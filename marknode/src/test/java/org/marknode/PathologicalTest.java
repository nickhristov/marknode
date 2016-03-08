package org.marknode;

import org.testng.annotations.Test;

/**
 * Pathological input cases (from commonmark.js).
 *
 * TODO(Jake): Check and correct this class.
 */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PathologicalTest extends CoreRenderingTestCase {

  private static final int X = 10_000;

//    @Rule
//    public Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
//
//    @Rule
//    public Stopwatch stopwatch = new Stopwatch() {
//        @Override
//        protected void finished(long nanos, Description description) {
//            System.err.println(description.getDisplayName() + " took " + (nanos / 1000000) + " ms");
//        }
//    };

  @Test
  public void nestedStrongEmphasis() {
    // this is limited by the stack size because visitor is recursive
    int x = 1000;
    assertRendering(
        repeat("*a **a ", x) + "b" + repeat(" a** a*", x),
        "<p>" + repeat("<em>a <strong>a ", x) + "b" +
        repeat(" a</strong> a</em>", x) + "</p>\n");
  }

  @Test
  public void emphasisClosersWithNoOpeners() {
    assertRendering(
        repeat("a_ ", X),
        "<p>" + repeat("a_ ", X - 1) + "a_</p>\n");
  }

  @Test
  public void emphasisOpenersWithNoClosers() {
    assertRendering(
        repeat("_a ", X),
        "<p>" + repeat("_a ", X - 1) + "_a</p>\n");
  }

  @Test
  public void linkClosersWithNoOpeners() {
    assertRendering(
        repeat("a] ", X),
        "<p>" + repeat("a] ", X - 1) + "a]</p>\n");
  }

  @Test
  public void linkOpenersWithNoClosers() {
    assertRendering(
        repeat("[a ", X),
        "<p>" + repeat("[a ", X - 1) + "[a</p>\n");
  }

  @Test
  public void linkOpenersAndEmphasisClosers() {
    assertRendering(
        repeat("[ a_ ", X),
        "<p>" + repeat("[ a_ ", X - 1) + "[ a_</p>\n");
  }

  @Test
  public void mismatchedOpenersAndClosers() {
    assertRendering(
        repeat("*a_ ", X),
        "<p>" + repeat("*a_ ", X - 1) + "*a_</p>\n");
  }

  @Test
  public void nestedBrackets() {
    assertRendering(
        repeat("[", X) + "a" + repeat("]", X),
        "<p>" + repeat("[", X) + "a" + repeat("]", X) + "</p>\n");
  }

  @Test
  public void nestedBlockQuotes() {
    // this is limited by the stack size because visitor is recursive
    int x = 1000;
    assertRendering(
        repeat("> ", x) + "a\n",
        repeat("<blockquote>\n", x) + "<p>a</p>\n" +
        repeat("</blockquote>\n", x));
  }

  private static String repeat(String s, int count) {
    StringBuilder sb = new StringBuilder(s.length() * count);
    for (int i = 0; i < count; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

}
