package org.marknode.integration;

import org.marknode.node.Node;
import org.marknode.parser.Parser;
import org.marknode.spec.SpecExample;
import org.marknode.spec.SpecReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * Tests various substrings of the spec examples to check for out of bounds exceptions.
 */
public class BoundsIntegrationTest {

  private static final Parser PARSER = Parser.builder().build();

  @DataProvider(name = "data")
  public Object[][] data() {
    List<SpecExample> examples = SpecReader.readExamples();
    Object[][] data = new Object[examples.size()][];
    for (int i = 0; i < examples.size(); i++) {
      final SpecExample example = examples.get(i);
      data[i] = new Object[]{example.getSource()};
    }
    return data;
  }

  @Test(dataProvider = "data")
  public void testSubstrings(String input) {
    // Check possibly truncated block/inline starts
    for (int i = 1; i < input.length() - 1; i++) {
      parse(input.substring(i));
    }
    // Check possibly truncated block/inline ends
    for (int i = input.length() - 1; i > 1; i--) {
      parse(input.substring(0, i));
    }
  }

  private void parse(String input) {
    try {
      Node parsed = PARSER.parse(input);
      // Parsing should always return a node
      assertNotNull(parsed);
    } catch (Exception e) {
      throw new AssertionError("Parsing failed, input: " + input, e);
    }
  }
}
