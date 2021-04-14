package com.unclezs.gradle.sass;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author blog.unclezs.com
 * @since 2021/3/30 2:45 下午
 */
@Setter
@Getter
@NonNullApi
public class SassCompile extends DefaultTask {
  public static final String CHARSET = "@charset \"UTF-8\";";
  public static final String UNDER_LINE = "_";
  public static final String SCSS = ".scss";
  public static final String SASS = ".sass";
  public static final String CSS = ".css";
  @Internal
  private final Project project;
  @Internal
  private final Compiler compiler = new Compiler();
  @Internal
  private final Options options = new Options();
  @Internal
  private File sourceDir;
  @Internal
  private File destinationDir;
  /**
   * 输出的css目录
   */
  @Input
  @Optional
  private String cssPath;
  /**
   * sass目录
   */
  @Input
  @Optional
  private String sassPath;

  @Inject
  public SassCompile(Project project) {
    setGroup(BasePlugin.BUILD_GROUP);
    setDescription("compile sass");
    this.project = project;
  }

  @InputFiles
  protected FileTree getSourceFiles() {
    ConfigurableFileTree files = getProject().fileTree(new File(sourceDir, sassPath));
    files.include("**/*.scss");
    files.include("**/*.sass");
    return files;
  }

  @OutputFiles
  protected FileTree getOutputFiles() {
    ConfigurableFileTree files = getProject().fileTree(new File(getProject().getBuildDir(), cssPath));
    files.include("**/*.css");
    return files;
  }

  /**
   * 编译一个文件夹下的sass为css
   */
  @TaskAction
  private void compileSass() {
    SassExtension extension = project.getExtensions().getByType(SassExtension.class);
    setSassPath(extension.getSassPath());
    setCssPath(extension.getCssPath());
    File sassDir = new File(sourceDir, sassPath);
    File cssDir = new File(sourceDir, cssPath);
    project.fileTree(sassDir).visit(file -> {
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
          throw new TaskExecutionException(SassCompile.this, e);
        } catch (IOException e) {
          getLogger().error(e.getLocalizedMessage());
          throw new TaskExecutionException(SassCompile.this, e);
        }
      }
    });
  }
}
