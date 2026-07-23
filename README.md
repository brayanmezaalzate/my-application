# My Application

## Overview
MyApplication is an Android application built using the Gradle build system. This repository contains the source code, resources, and configuration files required to compile, build, and run the Android application.

## Features
*   **Modern Android Architecture:** Follows standard Android project structure conventions.
*   **Gradle Build System:** Uses Gradle for dependency management and build automation.
*   **Resource Management:** Organized resources including layouts, images, and values.

## Prerequisites
To build and run this project, ensure you have the following installed:
*   [Android Studio](https://developer.android.com/studio) (Latest version recommended)
*   Java Development Kit (JDK) 11 or higher
*   Android SDK (matching the target API level specified in `build.gradle`)

## Getting Started

### 1. Clone the Repository
Clone this project to your local machine using git:
```bash
git clone <repository-url>
```

### 2. Open in Android Studio
1. Launch Android Studio.
2. Go to **File > Open...** (or click "Open" from the welcome screen).
3. Navigate to the cloned `MyApplication2` directory and click **OK**.
4. Allow Android Studio to sync the project and download any necessary Gradle dependencies.

### 3. Build and Run
1. Connect a physical Android device via USB (with USB debugging enabled) or start an Android Virtual Device (AVD) from the Device Manager.
2. Click the **Run 'app'** button (the green play icon) in the Android Studio toolbar.
3. Select your deployment target to install and launch the application.

## Project Structure
The project is structured according to standard Android development practices:
*   `app/` - The main application module containing the source code, tests, and resources.
*   `app/build/` - Directory for auto-generated build files (e.g., APKs, intermediate files).
*   `.gradle/` - Gradle cache and configuration files.
*   `.idea/` - Workspace and project configuration files for Android Studio.

## Built With
*   [Android SDK](https://developer.android.com/sdk) - Core development framework.
*   [Gradle](https://gradle.org/) - Build automation tool.

## Versioning
This project uses standard Gradle versioning located in the module's `build.gradle` file.

## License
This project is licensed under the MIT License.

## Autor
Brayan Meza
