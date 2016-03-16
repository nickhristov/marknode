package org.marknode.internal.util.jre;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.io.FileUtils;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jiakuanwang
 */
public class EntitiesPropertiesCreator {

  private static final Logger log = LoggerFactory.getLogger(EntitiesPropertiesCreator.class);

  public static class Entity {

    private String name;
    private String value;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("name", name)
          .add("value", value)
          .toString();
    }
  }

  public List<Entity> loadEntitiesFromFile() throws IOException {
    String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    File entitiesFile = new File(
        new File(path).getParentFile().getParentFile(),
        "resources/main/org/marknode/internal/util/entities.properties");
    log.debug("entitiesFile: {}", entitiesFile);

    List<Entity> entities = Lists.newArrayList();
    final List<String> lines = FileUtils.readLines(entitiesFile);
    for (String line : lines) {
      if (Strings.isNullOrEmpty(line)) {
        continue;
      }
      int equal = line.indexOf("=");
      String key = line.substring(0, equal);
      String value = line.substring(equal + 1);

      Entity entity = new Entity();
      entity.setName(key);
      value = value.replace("\\", "\\\\");
      value = value.replace("\"", "\\" + "\"");
      entity.setValue(value);
      entities.add(entity);
    }
    return entities;
  }

  public String loadTemplateFromFile() throws IOException {
    String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    log.debug("path: {}", path);
    File templateFile = new File(
        new File(path).getParentFile().getParentFile(),
        "resources/main/org/marknode/internal/util/jre/EntitiesProperties.mvel");
    log.debug("templateFile: {}", templateFile);
    return FileUtils.readFileToString(templateFile, Charsets.UTF_8);
  }

  public void saveJavaSource(String javaSource) throws IOException {
    String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    File javaFile = new File(
        new File(path).getParentFile().getParentFile().getParentFile(),
        "src/main/java/org/marknode/internal/util/EntitiesProperties.java");
    log.debug("javaFile: {}", javaFile);
    FileUtils.writeStringToFile(javaFile, javaSource, Charsets.UTF_8);
  }

  public static void main(String[] args) throws IOException {
    EntitiesPropertiesCreator creator = new EntitiesPropertiesCreator();
    final List<Entity> entities = creator.loadEntitiesFromFile();
    log.debug("Loaded entities: size={}", entities.size());
    final String template = creator.loadTemplateFromFile();

    Map<String, Object> vars = Maps.newHashMap();
    vars.put("entities", entities);

    final Object generated = TemplateRuntime.eval(template, vars);
    log.debug("Generated: {}", generated);
    creator.saveJavaSource(generated.toString());
  }
}
