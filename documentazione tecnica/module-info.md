# Analisi tecnica dettagliata e approfondita: module-info.java

## Descrizione generale
File di dichiarazione del modulo Java. Definisce i confini del modulo, le dipendenze richieste e le esportazioni dei package.

## Struttura tipica
```java
module com.retroroom {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.retroroom;
}
```

## Sezioni principali
- `module <nome>`: nome univoco del modulo
- `requires <modulo>`: dipendenze obbligatorie (es. JavaFX)
- `exports <package>`: package resi pubblici ad altri moduli

## Edge case e note
- L’omissione di una dipendenza necessaria causa errori di compilazione.
- L’esportazione di package è necessaria per l’accesso da altri moduli.
- Non contiene logica eseguibile.

## Esempio d’uso
Viene letto automaticamente dal compilatore Java. Nessun uso diretto nel codice applicativo.


## Struttura
```java
module com.retroroom {
    requires java.base;
    requires javafx.controls;
    exports com.retroroom;
}
```
**Cosa fa:**
- Definisce il modulo Java del progetto.
- Specifica le dipendenze richieste: Java base e JavaFX controls.
- Esporta il package principale per l'uso da parte di altri moduli o dal launcher JavaFX.
- Il file è conforme alla modularizzazione Java 9+.

---

# Documentazione tecnica approfondita: module-info.java

Percorso sorgente: `src/main/java/module-info.java`

Scopo del documento
-------------------
Spiegare il file `module-info.java` del progetto, il suo ruolo nel sistema di moduli Java (JPMS), come configurare il runtime (module-path) con JavaFX e risolvere errori comuni come "Module com.retroroom not found".

Contenuto del file
------------------
```java
module com.retroroom {
    requires java.base;
    requires javafx.controls;
    exports com.retroroom;
}
```

Significato delle direttive
---------------------------
- `module com.retroroom` — definisce il nome del modulo. Deve corrispondere al package che esporti/usi.
- `requires javafx.controls` — indica che il modulo dipende dal modulo JavaFX Controls. In esecuzione è necessario passare al launcher i jar JavaFX tramite `--module-path`.
- `exports com.retroroom` — rende il package `com.retroroom` visibile ad altri moduli.

Problemi comuni e risoluzioni
-----------------------------
Errore tipico:
- `java.lang.module.FindException: Module com.retroroom not found` — significa che la JVM non trova il modulo `com.retroroom` nel module-path.

Cause:
1. `target/classes` non è incluso in `--module-path` (solo classpath). Per eseguire un modulo con `-m` è necessario che la directory contenente `module-info.class` sia nel module-path.
2. Uso misto di `-classpath` e `--module-path` con `-m`. Evitare di mescolare: se esegui con `-m` usa solo `--module-path`.
3. `module-info.java` non compilato o `module-info.class` mancante in `target/classes`.

Soluzioni pratiche:
- Compilare: `mvn clean package` o `mvn -DskipTests clean package`.
- Eseguire con comando corretto (PowerShell esempio):
```powershell
java --module-path "<javafx-win-jars>;<path-to-target-classes>" --add-modules javafx.controls,javafx.fxml -m com.retroroom/com.retroroom.App
```
- In IntelliJ: configurare Run/Debug Configuration -> VM options con `--module-path` e `--add-modules` e assicurarsi che non usi `-classpath` con `-m`.

Note su JavaFX e Moduli nativi
-----------------------------
- I jar JavaFX distribuiti via Maven contengono JAR specifici per piattaforma (`*-win.jar`). È consigliabile usare i jar con suffisso `-win` su Windows.
- `--add-modules javafx.controls,javafx.fxml` abilita i moduli JavaFX richiesti.

Opzioni alternative
-------------------
- Usare il plugin `javafx-maven-plugin` e `mvn javafx:run` per evitare di costruire manualmente il module-path.
- Se non vuoi usare JPMS, rimuovere `module-info.java` e avviare in classpath mode con `java -cp` (non raccomandato per progetti modulare).

Verifiche consigliate
---------------------
- Controllare che `target/classes/module-info.class` esista dopo `mvn package`:
```powershell
Test-Path "E:\Documenti\Scuola\2025-2026\Info\Retroroom\target\classes\module-info.class"
```
- Eseguire `dir target/classes` per vedere struttura.

Conclusione
-----------
Questo modulo è configurato in modo minimale per permettere l'esecuzione con JavaFX; la cosa critica è assicurarsi che in fase di esecuzione il `--module-path` punti sia ai jar JavaFX corretti per la piattaforma che alla cartella `target/classes` contenente `module-info.class`.

Fine del documento.
