package com.unclezs.gradle.sass;

import lombok.Getter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

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
    project.getTasks().create("compileSass", SassCompile.class, project);
  }
}
