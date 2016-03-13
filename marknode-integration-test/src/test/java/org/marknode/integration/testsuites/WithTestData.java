package org.marknode.integration.testsuites;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.apache.commons.io.FileUtils;
import org.marknode.node.Node;
import org.marknode.parser.Parser;
import org.marknode.renderer.html.HtmlRenderer;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author jiakuanwang
 */
public class WithTestData {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WithTestData.class);

  @DataProvider(name = "markdown-test-data")
  public Object[][] markdownTestData() {
    return prepareTestData("Markdown.mdtest");
  }

  @DataProvider(name = "markdown-complementary-test-data")
  public Object[][] markdownComplementaryTestData() {
    return prepareTestData("MarkdownComplementary.mdtest");
  }

  @DataProvider(name = "markdown-extra-test-data")
  public Object[][] markdownExtraTestData() {
    return prepareTestData("MarkdownExtra.mdtest");
  }

  private Object[][] prepareTestData(String testSuiteFolder) {
    String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    File testResourcesDir = new File(
        new File(path).getParentFile().getParentFile(), "resources/test");
    File markdownTestDir = new File(testResourcesDir, testSuiteFolder);
    log.debug("markdownTestDir={}", markdownTestDir);
    if (!markdownTestDir.exists()) {
      String msg = "markdownTestDir cannot be found";
      log.error(msg, new IllegalStateException(msg));
      throw new IllegalStateException(msg);
    }
    Collection<File> textFiles = FileUtils.listFiles(markdownTestDir, new String[]{"text"}, true);
    Object[][] data = new Object[textFiles.size()][2];
    int index = 0;
    for (File textFile : textFiles) {
      File[] dataPair = new File[2];
      dataPair[0] = textFile;
      String htmlName =
          textFile.getName().substring(0, textFile.getName().lastIndexOf(".")) + ".html";
      dataPair[1] = new File(textFile.getParentFile(), htmlName);
      data[index++] = dataPair;
      log.debug("[test file pair]: {} => {}", textFile.getName(), htmlName);
    }
    return data;
  }

  protected void testParsing(File textFile, File htmlFile) throws IOException {
    String markdownText = FileUtils.readFileToString(textFile, Charsets.UTF_8);
    String expectedHtml = FileUtils.readFileToString(htmlFile);

    Parser parser = Parser.builder().build();
    Node document = parser.parse(markdownText);
    HtmlRenderer renderer = HtmlRenderer.builder().build();

    final String actualHtml = renderer.render(document);

    assertEquals(removeEmptyLines(actualHtml), removeEmptyLines(expectedHtml));
  }

  private String removeEmptyLines(String str) {
    StringBuilder cleaned = new StringBuilder();
    final List<String> lines = Splitter.on('\n').splitToList(str);
    for (String line : lines) {
      if (!Strings.isNullOrEmpty(line)) {
        cleaned.append(line.trim()).append('\n');
      }
    }
    return cleaned.toString();
  }
}
