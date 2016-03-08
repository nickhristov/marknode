package org.marknode.parser;

import org.marknode.node.Block;
import org.marknode.node.BulletList;
import org.marknode.node.CustomBlock;
import org.marknode.node.Node;
import org.marknode.node.Paragraph;
import org.marknode.node.Text;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ParserTest {

  @Test
  public void customBlockParserFactory() {
    Parser parser = Parser.builder().customBlockParserFactory(new DashBlockParserFactory()).build();

    // The dashes would normally be a ThematicBreak
    Node document = parser.parse("hey\n\n---\n");

    assertTrue(document.getFirstChild() instanceof Paragraph);
    assertEquals("hey", ((Text) document.getFirstChild().getFirstChild()).getLiteral());
    assertTrue(document.getLastChild() instanceof DashBlock);
  }

  @Test
  public void indentation() {
    String given = " - 1 space\n   - 3 spaces\n     - 5 spaces\n\t - tab + space";
    Parser parser = Parser.builder().build();
    Node document = parser.parse(given);

    assertTrue(document.getFirstChild() instanceof BulletList);

    Node list = document.getFirstChild(); // first level list
    assertEquals(list.getFirstChild(), list.getLastChild(), "expect one child");
    assertEquals(firstText(list.getFirstChild()), "1 space");

    list = list.getFirstChild().getLastChild(); // second level list
    assertEquals(list.getFirstChild(), list.getLastChild(), "expect one child");
    assertEquals(firstText(list.getFirstChild()), "3 spaces");

    list = list.getFirstChild().getLastChild(); // third level list
    assertEquals(firstText(list.getFirstChild()), "5 spaces");
    assertEquals(firstText(list.getFirstChild().getNext()), "tab + space");
  }

  private String firstText(Node n) {
    while (!(n instanceof Text)) {
      assertTrue(n != null);
      n = n.getFirstChild();
    }
    return ((Text) n).getLiteral();
  }

  private static class DashBlock extends CustomBlock {

  }

  private static class DashBlockParser extends AbstractBlockParser {

    private DashBlock dash = new DashBlock();

    @Override
    public Block getBlock() {
      return dash;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {
      return BlockContinue.none();
    }
  }

  private static class DashBlockParserFactory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      if (state.getLine().equals("---")) {
        return BlockStart.of(new DashBlockParser());
      }
      return BlockStart.none();
    }
  }
}
