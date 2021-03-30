package com.unclezs.gradle.sass;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.internal.impldep.com.google.gson.Gson;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;

/**
 * @author blog.unclezs.com
 * @since 2021/3/30 2:45 下午
 */
@NonNullApi
public class SassCompile extends DefaultTask {
  public static final String CHARSET = "@charset \"UTF-8\";";
  public static final String UNDER_LINE = "_";
  public static final String SCSS = ".scss";
  public static final String SASS = ".sass";
  public static final String CSS = ".css";
  private final Project project;
  private File sourceDir;
  private File destinationDir;
  private final Compiler compiler = new Compiler();
  private final Options options = new Options();
  /**
   * 输出的css目录
   */
  @Input
  @Optional
  @Getter
  @Setter
  private String cssPath = "css";
  /**
   * sass目录
   */
  @Input
  @Optional
  @Getter
  @Setter
  private String sassPath = "scss";

  @InputFiles
  protected FileTree getSourceFiles() {
    ConfigurableFileTree files = getProject().fileTree(new File(sourceDir, sassPath));
    files.include("**/*.scss");
    files.include("**/*.sass");
    return files;
  }

  @OutputFiles
  protected FileTree getOutputFiles() {
    ConfigurableFileTree files = getProject().fileTree(new File(destinationDir, cssPath));
    files.include("**/*.css");
    return files;
  }

  @Inject
  public SassCompile(Project project) {
    setGroup(BasePlugin.BUILD_GROUP);
    setDescription("compile sass");
    this.project = project;
  }

  /**
   * 遍历Java插件下的默认约束的资源目录
   */
  @TaskAction
  public void compile() {
    project.getPlugins().apply(JavaPlugin.class);
    project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().forEach(sourceSet -> {
      Set<File> files = sourceSet.getResources().getSrcDirs();
      for (File sourceDir : files) {
        this.sourceDir = sourceDir;
        this.destinationDir = sourceSet.getOutput().getResourcesDir();
        compileSass();
      }
    });
  }

  /**
   * 编译一个文件夹下的sass为css
   */
  private void compileSass() {
    File sassDir = new File(sourceDir, sassPath);
    File cssDir = new File(sourceDir, cssPath);
    File cssBuildDir = new File(destinationDir, cssPath);
    boolean empty = project.fileTree(sassDir).visit(file -> {
      String name = file.getName();
      if (name.startsWith(UNDER_LINE)) {
        return;
      }
      if (name.endsWith(SCSS) || name.endsWith(SASS)) {
        String relativeCssPath = file.getRelativePath().getPathString();
        relativeCssPath = relativeCssPath.substring(0, relativeCssPath.length() - 5).concat(CSS);
        File cssFile = new File(cssDir, relativeCssPath);
        options.setIsIndentedSyntaxSrc(name.endsWith(SASS));
        try {
          Output output = compiler.compileFile(file.getFile().toURI(), cssFile.toURI(), options);
          if (cssFile.getParentFile().exists() || cssFile.getParentFile().mkdirs()) {
            Files.write(cssFile.toPath(), output.getCss().replace(CHARSET, "").trim().getBytes(StandardCharsets.UTF_8));
          }
        } catch (CompilationException e) {
          SassError sassError = new Gson().fromJson(e.getErrorJson(), SassError.class);
          getLogger().error("{}:{}:{}", sassError.getFile(), sassError.getLine(), sassError.getColumn());
          getLogger().error(e.getErrorMessage());
          throw new TaskExecutionException(SassCompile.this, e);
        } catch (IOException e) {
          getLogger().error(e.getLocalizedMessage());
          throw new TaskExecutionException(SassCompile.this, e);
        }
      }
    }).isEmpty();
    if (!empty) {
      try {
        FileUtils.copyDirectory(cssDir, cssBuildDir);
        FileUtils.deleteDirectory(new File(destinationDir,sassPath));
      } catch (IOException e) {
        throw new RuntimeException("copy to build error", e);
      }
    }
  }
}
