package com.unclezs.gradle.sass;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/03/31 0:03
 */
@Data
public class SassExtension {
  /**
   * css文件相对resources路径
   */
  private String cssPath = "css";
  /**
   * scss/sass文件相对resource路径
   */
  private String sassPath = "scss";
}
