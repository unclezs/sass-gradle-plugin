## sass-gradle-plugin

基于 [jsass](https://github.com/bit3/jsass) 的sass编译插件

## 基本使用

```gradle
buildscript {
  dependencies {
    classpath 'com.unclezs:sass-gradle-plugin:1.0.0-SNAPSHOT'
  }
}
apply plugin: "com.unclezs.gradle.sass"
```

- 在/resources/scss下编写scss和sass即可，会自动编译到/resources/css目录
- 以_开头的文件将不会被编译.

## 自定义路径

```groovy
task withType(SassCompile) {
  cssPath = "css"
  scssPath = "scss"
}
```
