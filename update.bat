call mvn clean
call mvn package -o
call "F:\Softs\Softs\Development\Java\Runtime\appengine-java-sdk-1.3.7\bin\appcfg.cmd" update "%~dp0target\ShopNow-1.0"

