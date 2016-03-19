package org.marknode.ext.autolink.internal;

import com.google.common.collect.Sets;

import org.marknode.ext.autolink.LinkExtractor;
import org.marknode.ext.autolink.LinkSpan;
import org.marknode.ext.autolink.LinkType;
import org.marknode.node.AbstractVisitor;
import org.marknode.node.Link;
import org.marknode.node.Node;
import org.marknode.node.Text;
import org.marknode.parser.PostProcessor;

public class AutolinkPostProcessor implements PostProcessor {

  private LinkExtractor linkExtractor = LinkExtractor.builder()
      .linkTypes(Sets.newHashSet(LinkType.URL, LinkType.EMAIL))
      .build();

  @Override
  public Node process(Node node) {
    AutolinkVisitor autolinkVisitor = new AutolinkVisitor();
    node.accept(autolinkVisitor);
    return node;
  }

  private void linkify(Text text) {
    String literal = text.getLiteral();
    Iterable<LinkSpan> links = linkExtractor.extractLinks(literal);

    Node lastNode = text;
    int last = 0;
    for (LinkSpan link : links) {
      String linkText = literal.substring(link.getBeginIndex(), link.getEndIndex());
      if (link.getBeginIndex() != last) {
        lastNode = insertNode(new Text(literal.substring(last, link.getBeginIndex())), lastNode);
      }
      Text contentNode = new Text(linkText);
      String destination = getDestination(link, linkText);
      Link linkNode = new Link(destination, null);
      linkNode.appendChild(contentNode);
      lastNode = insertNode(linkNode, lastNode);
      last = link.getEndIndex();
    }
    if (last != literal.length()) {
      insertNode(new Text(literal.substring(last)), lastNode);
    }
    text.unlink();
  }

  private static String getDestination(LinkSpan linkSpan, String linkText) {
    if (linkSpan.getType() == LinkType.EMAIL) {
      return "mailto:" + linkText;
    } else {
      return linkText;
    }
  }

  private static Node insertNode(Node node, Node insertAfterNode) {
    insertAfterNode.insertAfter(node);
    return node;
  }

  private class AutolinkVisitor extends AbstractVisitor {

    int inLink = 0;

    @Override
    public void visit(Link link) {
      inLink++;
      super.visit(link);
      inLink--;
    }

    @Override
    public void visit(Text text) {
      if (inLink == 0) {
        linkify(text);
      }
    }
  }
}
