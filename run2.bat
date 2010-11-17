# copy "%JAVA_HOME%\lib\tools.jar" "%TEMP%\gaej1.3.3\appengine-java-sdk-1.3.3\lib\shared"
call mvn clean
call mvn package -o
call "E:\Softs\appengine-java-sdk-1.3.7\bin\dev_appserver.cmd" --disable_update_check --jvm_flag=-Xdebug --jvm_flag=-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 "%~dp0target\ShopNow-1.0"
