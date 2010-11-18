#cp "$JAVA_HOME/lib/tools.jar" "/Personal/Working/Development/Runtime/appengine-java-sdk-1.3.3/lib/shared"
#mvn clean
mvn package -o
#sh "/Personal/Working/Development/Runtime/appengine-java-sdk-1.3.3/bin/dev_appserver.sh" --disable_update_check --port=7777 "./target/ShopNow-1.0"
#sh "/Personal/Working/Development/Runtime/appengine-java-sdk-1.3.3/bin/dev_appserver.sh" --disable_update_check --jvm_flag=-Xdebug --jvm_flag=-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 --port=8888 "./target/ShopNow-1.0"
sh "/Development/appengine-java-sdk-1.3.8/bin/dev_appserver.sh" --disable_update_check --port=7070 "./target/ShopNow-1.0"
