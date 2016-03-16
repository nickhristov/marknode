package org.marknode.internal.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.marknode.internal.util.EntitiesProperties.getEntities;

public class Html5Entities {

  private static final Map<String, String> NAMED_CHARACTER_REFERENCES = getEntities();
  private static final Pattern NUMERIC_PATTERN = Pattern.compile("^&#[Xx]?");

  public static String entityToString(String input) {
    Matcher matcher = NUMERIC_PATTERN.matcher(input);

    if (matcher.find()) {
      int base = matcher.end() == 2 ? 10 : 16;
      try {
        int codePoint = Integer
            .parseInt(input.substring(matcher.end(), input.length() - 1), base);
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
