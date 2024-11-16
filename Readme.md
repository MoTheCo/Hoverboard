# VirtualKeyboard README

## Übersicht

Dieses Projekt implementiert eine virtuelle Tastatur mit Java Swing. Es ermöglicht die Simulation von Tastatureingaben über ein grafisches Benutzerinterface (GUI). Die Tastatur unterstützt Buchstaben, Zahlen und Sonderzeichen, die durch Hovering über die Tasten ausgewählt werden können.

## Features

- **Virtuelle Tasten**: Umschaltung zwischen Kleinbuchstaben, Großbuchstaben, Zahlen und Sonderzeichen.
- **Automatische Tastenwahl**: Die Tasten werden durch Hovering aktiviert.
- **Navigation**: Automatisches Blättern durch die Tasten mit Timer.
- **Steuerungstasten**: Umschalten zwischen verschiedenen Tastenlayouts.

## Installation

1. **Voraussetzungen**: Stellen Sie sicher, dass Java installiert ist.
2. **Kompilierung**: Kompilieren Sie die Java-Datei `Hoverboard.java` mit einem geeigneten Java-Compiler.
3. **Ausführung**: Führen Sie die kompilierte Datei aus:

   ```bash
   java Hoverboard
   ```

## Nutzung

- **Tastenlayouts**:
  - **CAPS**: Umschaltet zwischen Groß- und Kleinbuchstaben.
  - **123**: Schaltet auf Zahlenlayout um.
  - **#@!**: Schaltet auf Sonderzeichenlayout um.

- **Navigation**:
  - Verwenden Sie die Pfeiltasten (◄ und ►) zum Bewegen durch die Tasten.
  - Durch Hovering über eine Taste wird diese nach einer kurzen Verzögerung aktiviert.

## Funktionen im Detail

- **Tastenwechsel**: Über `JToggleButton` können verschiedene Layouts ausgewählt werden.
- **Timer**: Verwendet für die automatische Navigation und Tastenaktivierung.
- **Robot-Klasse**: Simuliert Tastatureingaben.

## Code-Struktur

- **Konstruktor `VirtualKeyboard()`**: Initialisiert die GUI-Komponenten und Timer.
- **`processKey(String key)`**: Führt die Aktion für die aktivierte Taste aus.
- **`updateKeyLabels()`**: Aktualisiert die Tastenbeschriftungen basierend auf dem Layout.
- **`sendKey(char character)`**: Simuliert die Eingabe eines Zeichens.
- **`getKeyCode(char character)`**: Bestimmt den KeyCode für ein Zeichen basierend auf deutschem Tastaturlayout.

## Anpassungen

- **Layout ändern**: Um das Layout zu ändern, passen Sie die Arrays `lettersLower`, `lettersUpper`, `numbers`, und `specialChars` an.
- **Verzögerungszeiten**: Passen Sie die Timer-Delays für das Tastenhovering oder die Navigation an.

## Bekannte Einschränkungen

- Die Anwendung ist für das deutsche Tastaturlayout optimiert.
- Die GUI ist auf Desktop-Anwendungen beschränkt und benötigt Java Swing.

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz. 
