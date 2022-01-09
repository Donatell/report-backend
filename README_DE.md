# Medizinische Berichtsanwendung (Backend-Teil)
[English](./README.md)

Siehe die Beschreibung des Frontend-Teils hier: [Link](https://github.com/Donatell/Report-frontend)

## Beschreibung und Anwendungsbereich
Dieses Projekt wurde für eine russische Klinik entwickelt, um den Prozess der Erstellung von Berichten für medizinische Untersuchungen von Unternehmen (B2B) zu automatisieren.

In Russland sind alle Unternehmen verpflichtet, jährlich sowie bei Neueinstellungen eine medizinische Untersuchung durchzuführen. Der Gesetzgeber hat eine Liste von gefährlichen oder schädlichen Faktoren erstellt, in der festgelegt ist, welche nummerierten Faktoren zu welchen Untersuchungen verpflichten. Mit diesem Programm kann die Klinik auf der Grundlage einer Liste von Personen (Name, Geburtsdatum, Nummern der schädlichen Faktoren) verschiedene Arten von medizinischen Berichten erstellen, die vom Föderalen Dienst für Überwachung im Gesundheitswesen zur Erfüllung des Vertrags mit dem Kunden verlangt werden.

## Einfluss und Effizienz
Durch die Anwendung dieses Programms konnten die Berichtsdokumente bei Standardverträgen 6-mal schneller und bei Sonderverträgen 20-mal schneller erstellt werden. Dadurch kann das Unternehmen mehr Aufträge annehmen und die Mitarbeiter effizienter einsetzen.

## Arbeitsablauf
1. Hochladen einer Liste von Patienten als ".xlsx"-Datei 
2. Geben Sie der Datei einen Namen
3. Wählen Sie ein Modul (je nach Kunde gibt es verschiedene Module)
4. Wählen Sie die Spaltenüberschriften, um die Spalten dem Inhalt zuzuordnen
5. Geben Sie das Geschlecht an, wenn es nicht möglich war, es namentlich festzulegen
6. Berichte herunterladen

## Technische Beschreibung des Backend-Teils
Ich habe mich entschieden, **Java Spring** zu verwenden, um eine effiziente und sichere Anwendung zu entwickeln. Mit **Spring Boot** war es einfacher, das Projekt einzurichten und mit der Entwicklung zu beginnen. Ein weiterer Vorteil von Spring Boot ist seine große Kompatibilität mit **Docker** - das Docken und Hosten des Projekts auf einem Debian-Server verlief reibungslos. Neben der Bequemlichkeit der Entwicklung war ein wichtiger Grund für die Verwendung von Java in diesem Projekt **Apache POI** - die Java-API für Microsoft Documents. Diese Bibliothek ermöglichte es, Berichte im .xlsx-Format mit einem geeigneten Layout zu erstellen. Als DBMS wurde **MySQL** gewählt, weil es zuverlässig und für die geplanten Datenstrukturen geeignet ist. Um die Kommunikation abzusichern und den Entwicklungsablauf zu verbessern, habe ich **Hibernate ORM** Entitäten verwendet. Da die App im Internet verfügbar ist, habe ich **Okta** integriert, um digitale Interaktionen zu sichern und den Zugriff auf die App zu begrenzen. Die Anwendung nutzt **SSL**, um die übertragenen Daten zu sichern. Der Server verwendet **gzip** Antwortkompression, um eine schnellere Ladezeit der Seite zu erreichen.
