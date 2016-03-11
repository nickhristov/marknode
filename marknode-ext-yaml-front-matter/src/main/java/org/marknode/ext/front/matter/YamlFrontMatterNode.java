package org.marknode.ext.front.matter;

import org.marknode.node.CustomNode;

import java.util.List;

public class YamlFrontMatterNode extends CustomNode {

  private String key;
  private List<String> values;

  public YamlFrontMatterNode(String key, List<String> values) {
    this.key = key;
    this.values = values;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }
}
