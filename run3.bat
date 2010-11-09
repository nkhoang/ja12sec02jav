#copy "%JAVA_HOME%\lib\tools.jar" "E:\Softs\appengine-java-sdk-1.3.7\lib\shared"
call mvn clean
call mvn package -o -Dmaven.test.skip=true
call "E:\Softs\appengine-java-sdk-1.3.7\bin\dev_appserver.cmd" --disable_update_check "%~dp0target\ShopNow-1.0"
