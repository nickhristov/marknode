package org.marknode.internal.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.Map;

import static org.marknode.internal.util.EntitiesProperties.getEntities;

public class Html5Entities {

  private static final Map<String, String> NAMED_CHARACTER_REFERENCES = getEntities();

  public static String entityToString(String input) {
    final RegExp pattern = RegExp.compile("^&#[Xx]?", "g");
    final MatchResult matcher = pattern.exec(input);
    boolean matchFound = matcher != null; // equivalent to regExp.test(inputStr);

    if (matchFound) {
      int matcherEnd = pattern.getLastIndex();
      int base = matcherEnd == 2 ? 10 : 16;
      try {
        int codePoint = Integer.parseInt(input.substring(matcherEnd, input.length() - 1), base);
        if (codePoint == 0) {
          return "\uFFFD";
        }
        return new String(Character.toChars(codePoint));
      } catch (IllegalArgumentException e) {
        return "\uFFFD";
      }
    } else {
      String name = input.substring(1, input.length() - 1);
      String s = NAMED_CHARACTER_REFERENCES.get(name);
      if (s != null) {
        return s;
      } else {
        return input;
      }
    }
  }
}
