package com.unclezs.gradle.sass;

import lombok.Getter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;

import java.io.File;
import java.util.Set;

/**
 * sass 编译插件
 * <p>
 * 默认将 /resources/scss下的.scss或.sass 编译到 /resources/css
 * <p>
 * 以_开头的文件scss/sass将不会被编译
 *
 * @author blog.unclezs.com
 * @date 2021/3/29 22:55
 */
@Getter
public class SassCompilePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    SassExtension extension = project.getExtensions().create("sass", SassExtension.class);
    project.getPlugins().apply(JavaPlugin.class);
    project.afterEvaluate(c -> project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets()
      .all(sourceSet -> {
        String taskName = sourceSet.getTaskName("compile", "Sass");
        Set<File> srcDirs = sourceSet.getResources().getSrcDirs();
        int i = 1;
        for (File srcDir : srcDirs) {
          SassCompile sassCompile = project.getTasks().create(i == 1 ? taskName : taskName + i, SassCompile.class, project);
          i++;
          Copy processResources = (Copy) project.getTasks().getByName(sourceSet.getProcessResourcesTaskName());
          sassCompile.setSourceDir(srcDir);
          sassCompile.setDestinationDir(srcDir);
          sassCompile.setSassPath(extension.getSassPath());
          sassCompile.setCssPath(extension.getCssPath());
          processResources.dependsOn(sassCompile);
        }
      }));
  }
}
