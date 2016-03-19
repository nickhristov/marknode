package org.marknode.ext.front.matter.internal;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import org.marknode.ext.front.matter.YamlFrontMatterBlock;
import org.marknode.ext.front.matter.YamlFrontMatterNode;
import org.marknode.internal.DocumentBlockParser;
import org.marknode.node.Block;
import org.marknode.parser.InlineParser;
import org.marknode.parser.block.AbstractBlockParser;
import org.marknode.parser.block.AbstractBlockParserFactory;
import org.marknode.parser.block.BlockContinue;
import org.marknode.parser.block.BlockParser;
import org.marknode.parser.block.BlockStart;
import org.marknode.parser.block.MatchedBlockParser;
import org.marknode.parser.block.ParserState;

import java.util.ArrayList;
import java.util.List;

public class YamlFrontMatterBlockParser extends AbstractBlockParser {

  private static final RegExp
      REGEX_METADATA = RegExp.compile("^[ ]{0,3}([A-Za-z0-9_-]+):\\s*(.*)");
  private static final RegExp REGEX_METADATA_LIST = RegExp.compile("^[ ]+-\\s*(.*)");
  private static final RegExp REGEX_METADATA_LITERAL = RegExp.compile("^\\s*(.*)");
  private static final RegExp REGEX_BEGIN = RegExp.compile("^-{3}(\\s.*)?$");
  private static final RegExp REGEX_END = RegExp.compile("^(-{3}|\\.{3})(\\s.*)?");

  private boolean inYAMLBlock;
  private boolean inLiteral;
  private String currentKey;
  private List<String> currentValues;
  private YamlFrontMatterBlock block;

  public YamlFrontMatterBlockParser() {
    inYAMLBlock = true;
    inLiteral = false;
    currentKey = null;
    currentValues = new ArrayList<>();
    block = new YamlFrontMatterBlock();
  }

  @Override
  public Block getBlock() {
    return block;
  }

  @Override
  public void addLine(CharSequence line) {
  }

  @Override
  public BlockContinue tryContinue(ParserState parserState) {
    final CharSequence line = parserState.getLine();

    if (inYAMLBlock) {
      if (REGEX_END.exec(line.toString()) != null) {
        if (currentKey != null) {
          block.appendChild(new YamlFrontMatterNode(currentKey, currentValues));
        }
        return BlockContinue.finished();
      }

      MatchResult matcher = REGEX_METADATA.exec(line.toString());
      if (matcher != null) {
        if (currentKey != null) {
          block.appendChild(new YamlFrontMatterNode(currentKey, currentValues));
        }

        inLiteral = false;
        currentKey = matcher.getGroup(1);
        currentValues = new ArrayList<>();
        if ("|".equals(matcher.getGroup(2))) {
          inLiteral = true;
        } else if (!"".equals(matcher.getGroup(2))) {
          currentValues.add(matcher.getGroup(2));
        }

        return BlockContinue.atIndex(parserState.getIndex());
      } else {
        if (inLiteral) {
          matcher = REGEX_METADATA_LITERAL.exec(line.toString());
          if (matcher != null) {
            if (currentValues.size() == 1) {
              currentValues.set(0, currentValues.get(0) + "\n" + matcher.getGroup(1).trim());
            } else {
              currentValues.add(matcher.getGroup(1).trim());
            }
          }
        } else {
          matcher = REGEX_METADATA_LIST.exec(line.toString());
          if (matcher != null) {
            currentValues.add(matcher.getGroup(1));
          }
        }

        return BlockContinue.atIndex(parserState.getIndex());
      }
    } else if (REGEX_BEGIN.test(line.toString())) {
      inYAMLBlock = true;
      return BlockContinue.atIndex(parserState.getIndex());
    }

    return BlockContinue.none();
  }

  @Override
  public void parseInlines(InlineParser inlineParser) {
  }

  public static class Factory extends AbstractBlockParserFactory {

    @Override
    public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
      CharSequence line = state.getLine();
      BlockParser parentParser = matchedBlockParser.getMatchedBlockParser();

      // check whether this line is the first line of whole document or not
      if (parentParser instanceof DocumentBlockParser &&
          parentParser.getBlock().getFirstChild() == null &&
          REGEX_BEGIN.test(line.toString())) {
        return BlockStart.of(new YamlFrontMatterBlockParser())
            .atIndex(state.getNextNonSpaceIndex());
      }

      return BlockStart.none();
    }
  }
}
