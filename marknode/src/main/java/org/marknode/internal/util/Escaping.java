package org.marknode.internal.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class Escaping {

  public static final String ESCAPABLE = "[!\"#$%&\'()*+,./:;<=>?@\\[\\\\\\]^_`{|}~-]";

  private static final String ENTITY = "&(?:#x[a-f0-9]{1,8}|#[0-9]{1,8}|[a-z][a-z0-9]{1,31});";

  private static final RegExp BACKSLASH_OR_AMP = RegExp.compile("[\\\\&]", "g");

  private static final RegExp ENTITY_OR_ESCAPED_CHAR =
      RegExp.compile("\\\\" + ESCAPABLE + '|' + ENTITY, "gi");

  private static final String XML_SPECIAL = "[&<>\"]";

  private static final RegExp XML_SPECIAL_RE = RegExp.compile(XML_SPECIAL, "g");

  private static final RegExp XML_SPECIAL_OR_ENTITY =
      RegExp.compile(ENTITY + '|' + XML_SPECIAL, "gi");

  private static final RegExp ESCAPE_IN_URI =
      RegExp.compile("(%[a-fA-F0-9]{0,2}|[^:/?#@!$&'()*+,;=a-zA-Z0-9\\-._~])", "g");

  private static final char[] HEX_DIGITS =
      new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  private static final RegExp WHITESPACE = RegExp.compile("[ \t\r\n]+", "g");

  private static final Replacer UNSAFE_CHAR_REPLACER = new Replacer() {
    @Override
    public void replace(String input, StringBuilder sb) {
      switch (input) {
        case "&":
          sb.append("&amp;");
          break;
        case "<":
          sb.append("&lt;");
          break;
        case ">":
          sb.append("&gt;");
          break;
        case "\"":
          sb.append("&quot;");
          break;
        default:
          sb.append(input);
      }
    }
  };

  private static final Replacer UNESCAPE_REPLACER = new Replacer() {
    @Override
    public void replace(String input, StringBuilder sb) {
      if (input.charAt(0) == '\\') {
        sb.append(input, 1, input.length());
      } else {
        sb.append(Html5Entities.entityToString(input));
      }
    }
  };

  private static final Replacer URI_REPLACER = new Replacer() {
    @Override
    public void replace(String input, StringBuilder sb) {
      if (input.startsWith("%")) {
        if (input.length() == 3) {
          // Already percent-encoded, preserve
          sb.append(input);
        } else {
          // %25 is the percent-encoding for %
          sb.append("%25");
          sb.append(input, 1, input.length());
        }
      } else {
        byte[] bytes = new byte[0];
        try {
          bytes = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        for (byte b : bytes) {
          sb.append('%');
          sb.append(HEX_DIGITS[(b >> 4) & 0xF]);
          sb.append(HEX_DIGITS[b & 0xF]);
        }
      }
    }
  };

  public static String escapeHtml(String input, boolean preserveEntities) {
    RegExp p = preserveEntities ? XML_SPECIAL_OR_ENTITY : XML_SPECIAL_RE;
    return replaceAll(p, input, UNSAFE_CHAR_REPLACER);
  }

  /**
   * Replace entities and backslash escapes with literal characters.
   */
  public static String unescapeString(String s) {
    BACKSLASH_OR_AMP.setLastIndex(0);
    if (BACKSLASH_OR_AMP.test(s)) {
      return replaceAll(ENTITY_OR_ESCAPED_CHAR, s, UNESCAPE_REPLACER);
    } else {
      return s;
    }
  }

  public static String percentEncodeUrl(String s) {
    return replaceAll(ESCAPE_IN_URI, s, URI_REPLACER);
  }

  public static String normalizeReference(String input) {
    // Strip '[' and ']', then trim
    String stripped = input.substring(1, input.length() - 1).trim();
    String lowercase = stripped.toLowerCase(Locale.ROOT);
    return replaceAll(WHITESPACE, lowercase, new Replacer() {
      @Override
      public void replace(String input, StringBuilder sb) {
        sb.append(" ");
      }
    });
  }

  private static String replaceAll(RegExp pattern, String s, Replacer replacer) {
    // Reset the pattern searcher
    pattern.setLastIndex(0);

    // Matcher matcher = pattern.matcher(s);
    MatchResult matcher = pattern.exec(s);
    boolean matchFound = matcher != null;

    if (!matchFound) {
      return s;
    }

    StringBuilder sb = new StringBuilder(s.length() + 16);
    int lastEnd = 0;
    do {
      int matchStart = matcher.getIndex();
      int matcherEnd = pattern.getLastIndex();
      sb.append(s, lastEnd, matchStart);
      replacer.replace(matcher.getGroup(0), sb);
      lastEnd = matcherEnd;

      matcher = pattern.exec(s);
      matchFound = matcher != null;
    } while (matchFound);

    if (lastEnd != s.length()) {
      sb.append(s, lastEnd, s.length());
    }
    return sb.toString();
  }

  private interface Replacer {

    void replace(String input, StringBuilder sb);
  }
}
