# Music Streaming 

## Project Description

This project is a simplified music streaming application that allows users to search for songs, create playlists, and manage a personal music library. The system models core features of modern music streaming platforms while storing all data locally in CSV files. It also includes separate roles for users and administrators, with admins able to manage the song database.

## Technologies Used

Primary Language: Java

Data Storage: CSV Files

IDE: [IntelliJ / Eclipse / VSCode / Other]

Version Control: Git & GitHub

## Data Handling (CSV)

All persistent data is stored in CSV files.
The program reads from CSV files at startup.
Any changes made during runtime are written back to the CSV files.
CSV files are located in: /data

## Features

User account login and management

Search and browse songs in the music library

View song lyrics

Create and manage playlists

Sort songs and playlists alphabetically or by genre

Admin controls for adding and removing songs from the system

## How to Run (Development)

1. **Clone/Download** this repository to your local machine.
2. Open the project in your preferred IDE (e.g, IntelliJ IDEA or Eclipse).
3. Locate and run `PlaceHolderName`, which is located in `src/main/java/org/example/frontend/PlaceHolderName.java`.
4. Once the window opens, click any of the options in the menu bar.

## Build & Run (Windows - packaged)

This project includes an Ant-based build that produces a runnable JAR and simple helper scripts for Windows.

Prerequisites
- Java 17+ installed and on PATH (java and javac commands available).
- Apache Ant installed and on PATH (for building with the provided script).

Files added
- `build.xml` – Ant build file; targets: `clean`, `init`, `compile`, `jar`, `dist`.
- `build.ps1` – PowerShell helper that runs Ant to create `dist/PlaceHolderName.jar`.
- `run.ps1` – PowerShell helper that runs the packaged JAR (or falls back to running compiled classes).
- `run.bat` – Windows batch helper that runs the packaged JAR (or falls back to running compiled classes).

Build steps (PowerShell)
1. Open PowerShell in the project root.
2. Run:

```powershell
.\build.ps1
```

This will run Ant and produce `dist/PlaceHolderName.jar`.

Run steps (PowerShell)
- If you built the project, run:

```powershell
.\run.ps1
```

- Or in cmd.exe, run:

```bat
run.bat
```

## Run the JAR directly

If you already built the project (see Build steps) the runnable JAR will be at `dist/PlaceHolderName.jar`.

PowerShell (Windows):

```powershell
java -jar dist\PlaceHolderName.jar
```

Command Prompt (Windows):

```bat
REM run the packaged jar
java -jar dist\PlaceHolderName.jar
```

Unix / macOS:

```bash
# run the packaged jar
java -jar dist/PlaceHolderName.jar
```

Notes:
- Double-clicking the JAR file in Explorer will run it if your system associates .jar files with Java, but the terminal is preferred so you can see logs and errors.

- If the JAR fails to start, ensure you have a compatible JDK/JRE installed (Java 17+). If you built the jar with Ant and it doesn't exist, run `./build.ps1` first to produce `dist/PlaceHolderName.jar`.

Troubleshooting
- If `build.ps1` reports that Ant is not found, install Apache Ant (https://ant.apache.org/) and ensure `ant` is on your PATH.
- Make sure Java (javac/java) is available on the PATH and is version 17 or later.
- If the app cannot find CSV files, ensure `src/main/resources/data` exists and contains the expected CSVs (the app also creates files when needed).

## How to Use

1. Start the app and log in (the admin account is `admin` / `admin`).
2. Admins can add/remove songs (which are saved to the root CSV).
3. Users can handle playlists via the Add-to-Playlist dialogue.

## References

Can be found in the Information folder
