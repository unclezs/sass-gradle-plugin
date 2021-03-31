# sass-gradle-plugin
![gradle build](https://img.shields.io/github/workflow/status/unclezs/sass-gradle-plugin/gradle) [![GitHub license](https://img.shields.io/github/license/unclezs/sass-gradle-plugin?color=%2340C0D0&label=License)](https://github.com/unclezs/sass-gradle-plugin/blob/master/LICENSE) [![GitHub issues](https://img.shields.io/github/issues/unclezs/sass-gradle-plugin?color=orange&label=Issues)](https://github.com/unclezs/sass-gradle-plugin/issues)

基于 [jsass](https://github.com/bit3/jsass) 的sass编译插件

# 基本使用

```gradle
buildscript {
  dependencies {
    classpath 'com.unclezs:sass-gradle-plugin:1.0.4'
  }
}
apply plugin: "com.unclezs.gradle.sass"
```

- 在/resources/scss下编写scss和sass即可，会自动编译到/resources/css目录
- 以_开头的文件将不会被编译.

# 自定义路径

```groovy
sass {
  cssPath = "css"
  scssPath = "scss"
}
```
