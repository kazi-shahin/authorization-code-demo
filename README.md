# Build jar
    mvn clean compile assembly:single

# Run jar
    java --module-path /usr/lib/oauthdemo/javafx --add-modules javafx.controls,javafx.fxml -jar /usr/lib/oauthdemo/OAuthDemo-1.0-SNAPSHOT-jar-with-dependencies.jar "$1"

# build dpkg
    dpkg-deb --build package oauthdemo_1.2.deb
# install dpkg
    sudo dpkg -i oauthdemo_1.2.deb