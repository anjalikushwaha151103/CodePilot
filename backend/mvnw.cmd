@echo off
setlocal
set "DIRNAME=%~dp0"
set "WRAPPER_JAR=%DIRNAME%.mvn\wrapper\maven-wrapper.jar"
java "-Dmaven.multiModuleProjectDirectory=%DIRNAME:~0,-1%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
