# AlgoVerse

**AlgoVerse** is an interactive learning platform for the **Analysis and Design of Algorithms (ADA)**. It is designed to help learners explore algorithmic concepts through visualizations, experiment with custom inputs, test their understanding, and track their progress.

> Project description based on the repository metadata. Update the topic list and screenshots as the app's implemented modules evolve.

## ✨ Highlights

- **Interactive algorithm learning** — explore algorithm concepts through interactive visualizations.
- **Custom inputs** — experiment with your own input values where supported.
- **Predict mode** — make a prediction before observing an algorithm's behavior.
- **Mastery tracking** — keep track of learning progress.
- **AI capability** — the project metadata lists a server-side Gemini API capability.

## 🛠️ Tech Stack

- **Platform:** Android
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Build system:** Gradle with Kotlin DSL
- **AI capability:** Google Gemini API (server-side capability indicated in project metadata)

## 📁 Project Structure

```text
AlgoVerse/
├── app/                    # Android application module
├── gradle/                 # Gradle wrapper and version catalog
├── build.gradle.kts        # Root Gradle build configuration
├── settings.gradle.kts     # Project/module settings
├── gradle.properties       # Gradle configuration
├── metadata.json           # Project metadata and capabilities
├── LICENSE                 # Project license
└── README.md               # Project documentation
```

## 🚀 Getting Started

### Prerequisites

- Android Studio with Android SDK support
- A compatible JDK for the project's Gradle/Android Gradle Plugin versions
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Nirjala7-11/AlgoVerse.git
cd AlgoVerse
```

### 2. Open the project

Open the `AlgoVerse` directory in Android Studio and allow Gradle to sync.

### 3. Configure required services

If you use features that depend on Gemini or another external service, configure the required credentials using the project's intended secure configuration. **Do not commit API keys, passwords, or other secrets to Git.** Check the app's configuration and build files for the exact setup required by this version of the project.

### 4. Build and run

Select the app configuration in Android Studio, choose an emulator or connected Android device, and click **Run**.

You can also build from a terminal with the Gradle wrapper:

```bash
# macOS / Linux
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The first build may take some time while Gradle downloads dependencies.

## 🧭 Using AlgoVerse

1. Launch the app.
2. Explore the available algorithm topics.
3. Open an interactive visualization.
4. Try custom inputs when available.
5. Use Predict mode to reason about the expected result before running an example.
6. Review your learning or mastery progress where the feature is available.

The exact screens and available algorithms depend on the current implementation in the repository.

## 🔐 Security Notes

- Keep API credentials out of source control.
- Use the project's secure server-side configuration for service credentials.
- Before publishing, verify that secrets are not present in source files, build logs, or committed configuration.

## 🧪 Testing

Run the tests configured in the project from Android Studio, or use the Gradle wrapper:

```bash
# Unit tests
./gradlew test

# Android instrumented tests (requires a configured emulator/device)
./gradlew connectedAndroidTest
```

These commands run the corresponding tasks if test sources and configurations are present.

## 🤝 Contributing

Contributions and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Make your changes and test them.
4. Commit your work: `git commit -m "Describe your change"`.
5. Push your branch and open a pull request.

For larger changes, consider opening an issue first to discuss the proposed improvement.

## 📄 License

This repository includes a `LICENSE` file. Review that file for the applicable license terms before reusing or redistributing the project.

## 👩‍💻 Repository

**GitHub:** https://github.com/Nirjala7-11/AlgoVerse
