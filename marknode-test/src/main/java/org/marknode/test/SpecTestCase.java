package org.marknode.test;


import org.marknode.spec.SpecExample;
import org.marknode.spec.SpecReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public abstract class SpecTestCase extends RenderingTestCase {

  @DataProvider(name = "specData")
  public static Object[][] data() {
    List<SpecExample> examples = SpecReader.readExamples();
    Object[][] data = new Object[examples.size()][1];
    for (int i = 0; i < examples.size(); i++) {
      final SpecExample example = examples.get(i);
      data[i] = new Object[]{example};
    }
    return data;
  }

  @Test(dataProvider = "specData")
  public void testHtmlRendering(SpecExample example) {
    assertRendering(example.getSource(), example.getHtml());
  }
}
