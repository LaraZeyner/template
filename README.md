# Template für Minecraft-Plugins
Dieses Projekt dient dazu, um leichter Minecraft-Plugins schreiben zu können. Dies ist eine Vorlage für ein solches Plugin. Eine genauere Anleitung findest du hier:

1. Erstelle in deiner IDE ein Maven-Projekt und setze eine SDK (mindestens Java 1.8).
2. Kopiere alle Klassen und Packages in den src/main/java Ordner.
3. Kopiere alle Resourcen in den src/main/resources Ordner. DefaultServerFiles sollte unverändert bleiben.
4. Ändere nun Template in allen Dateien in den Namen deines Plugins um.
5. Vorgefertigte Methoden zur Erstellung von Tabellen müssen auch noch modifiziert werden.
6. Aktualisiere die MySQL-Datenbank in sql.properties.
7. Wenn du nicht Minecraft-1.8.8 nutzen willst, änder dies in der pom.xml.
8. Zum Testen erstelle den Maven-Befehl: ``clean package``
9. Führe die Datei ``out/testserver/start.bat`` aus.
10. Die Remote Funktion kannst du über Port ``5005`` mit den Argumenten ``-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`` nutzen.
<br>
Zum Aktualisieren des Plugins führe ``clean package`` erneut aus und reloade den Server.

Ordnerstruktur:
- 
``src/main/java``: Alle Klassen und Packages <br>
``src/main/resources/config``: Plugin Konfigurationsdateien <br>
``src/main/resources/defaultServerFiles/start.bat``: Server-Startdatei <br>
``out/testserver``: Erstellter Testserver ohne Spigot-API


Weitere Fragen?
-

Bei weiteren Fragen wende dich an die Entwicklerin oder erstelle einen Beitag unter Issue unter diesem Repository.