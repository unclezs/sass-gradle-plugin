package com.unclezs.gradle.sass;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * @author blog.uncles.com
 * @since 2021/03/30 14:27
 */
public class SassTest {

  @Test
  public void test() throws InterruptedException {
    Project project = ProjectBuilder.builder().withProjectDir(new File(".")).build();
    project.apply(c -> c.plugin("com.unclezs.gradle.sass"));
    SassExtension extension = project.getExtensions().getByType(SassExtension.class);
    extension.setCssPath("com/unclezs/css");
    extension.setSassPath("uncleA");
    Task compileSass = project.getTasks().getByName("compileTestSass");
    compileSass.getActions().forEach(action -> action.execute(compileSass));
  }
}
