# agent
agent动态热更
将项目打包成jar，在项目启动后同时启动HotLoadWatch.start(jarPath, hotDirect)
jarPath是jar包的绝对路径，hotDirect是热更class的绝对路径
修改java后，将编译后的class复制到hotDirect即可
并不是所有的class都能热更，具体看Instrumentation#redefineClasses()方法的限制
注意：pom.xml最后几行的配置
