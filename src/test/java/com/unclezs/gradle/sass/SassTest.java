package com.unclezs.gradle.sass;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author blog.uncles.com
 * @since 2021/03/30 14:27
 */
public class SassTest {

  @Test
  public void test() {
    Project project = ProjectBuilder.builder().withProjectDir(new File(".")).build();
    project.apply(c -> c.plugin("com.unclezs.gradle.sass"));
    project.getTasks().withType(SassCompile.class, sassCompile -> {
      sassCompile.setSassPath("uncle");
    });
    Task compileSass = project.getTasks().getByName("compileSass");
    compileSass.getActions().forEach(action -> action.execute(compileSass));
  }
}
