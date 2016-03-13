
/*
 * Copyright (c) 2015 Wise Persist Pty Ltd. All rights reserved.
 */

package org.marknode.integration.testsuites;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author jiakuanwang
 */
public class MarkdownComplementaryTest extends WithTestData {

  @Test(dataProvider = "markdown-complementary-test-data")
  public void testParsingMarkdownTestData(File textFile, File htmlFile) throws IOException {
    testParsing(textFile, htmlFile);
  }
}
