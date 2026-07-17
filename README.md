<div align="center">

<img src="assets/unigrade.ico" alt="UniGrade Logo" width="80"/>

# UniGrade

**Real-Time Academic Performance Tracker & Grade Predictor**

[![Version](https://img.shields.io/badge/version-1.0.0-7C3AED?style=for-the-badge)](https://github.com/Haseeb-Hassan66/UniGrade/releases)
![Java](https://img.shields.io/badge/Java-26-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-26-007396?style=for-the-badge&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge)

</div>

---

## 🎯 Why UniGrade?

Most university portals are entirely **static**. They hand you a transcript at the end of the semester — when it's already too late to change anything.

**UniGrade solves this.** It's a desktop application that puts students back in control *during* the semester. Enter your marks as they happen — midterms, quizzes, sessionals — and UniGrade instantly calculates your current standing. It tells you exactly how many marks you need in your finals to reach your target grade.

**Stop guessing. Start planning.**

---

## ✨ Features

| Feature | Description |
|---|---|
| 📊 **Real-Time Grade Prediction** | See your live grade letter, percentage, and GPA the moment you enter a mark |
| 🏫 **Multi-University Support** | Isolated grading & assessment policies per institution |
| ⚙️ **Customizable Policies** | Define your exact grading scale (A = 85%+) and assessment weights (30% Mid, 50% Final) |
| 📁 **Full Academic History** | Organize semesters, subjects, theory & practical components with credit hours |
| 📈 **CGPA Report** | Instant cumulative GPA across all semesters |
| 🌍 **Internationalization** | UI strings fully driven by resource bundles for multi-language support |
| 🔒 **100% Local & Private** | All data stored in a local SQLite database — no internet, no cloud |

---

## 💾 Installation (For Users)

> **No Java installation required.** The installer bundles everything you need.

1. Go to the **[Releases](https://github.com/Haseeb-Hassan66/UniGrade/releases)** page.
2. Download the latest **`UniGrade-1.0.0.exe`** installer.
3. Run the installer and follow the on-screen steps.
4. Launch **UniGrade** from your Desktop or Start Menu.

> **Note:** Windows SmartScreen may show a security warning for first-time runs since the app is not yet code-signed. Click **"More info" → "Run anyway"** to proceed. This is safe — UniGrade is fully open-source and you can inspect every line of code in this repository.

---

## 📖 First-Time Usage Guide

1. **Create Your Profile** — On first launch, enter your name and department.
2. **Select Your University** — Choose from built-in universities or configure your own.
3. **Configure Policies** — In Settings, verify your university's Grading Policy (A, B+, B scales) and Assessment Policy (Mids, Finals, Sessional weights) are correct.
4. **Add a Semester** — Click "Add Semester" on the Dashboard (e.g., "Fall 2024").
5. **Add Subjects** — Open the semester and add your enrolled subjects with their credit hours.
6. **Enter Marks** — Click a subject to enter marks for each assessment component.
7. **View Your Report** — Click "Analysis" on the Dashboard to see your full GPA & CGPA report.

---

## 🏗️ Technical Architecture

UniGrade is built using standard Java enterprise patterns adapted for a self-contained desktop environment.

- **Language:** Java 26
- **UI Framework:** JavaFX 26 (FXML for strict view/logic separation)
- **Database:** SQLite via JDBC (local, embedded)
- **Design Pattern:** MVC + Data Access Object (DAO)
- **Packaging:** `jpackage` (JDK built-in tool) — produces a self-contained native installer with a bundled JRE

### Project Structure

```text
UniGrade/
├── assets/                         # App icon (unigrade.ico, unigrade.png)
├── lib/                            # External JARs (sqlite-jdbc)
├── src/
│   └── main/
│       ├── java/
│       │   ├── dao/                # Database access layer (SemesterDAO, SubjectDAO...)
│       │   ├── model/              # Data entities (Semester, Subject, UserProfile...)
│       │   ├── ui/                 # JavaFX Controllers & SceneManager
│       │   └── util/               # DBInitializer, DBUtil, UIUtil, ResultCalculator...
│       └── resources/
│           ├── fxml/               # All UI layouts (Dashboard, SplashScreen, Dialogs...)
│           └── Messages.properties # All translatable UI strings (i18n)
├── package.ps1                     # Build script → produces dist/UniGrade-1.0.0.exe
├── run.ps1                         # Dev runner script (for development only)
├── compile_i18n.bat                # Helper for i18n compilation
└── LICENSE
```

---

## 🛠️ Developer Setup

To build UniGrade from source, you need the following tools installed:

### Prerequisites

| Tool | Version | Download |
|---|---|---|
| JDK | 26+ | [oracle.com/java](https://www.oracle.com/java/technologies/downloads/) |
| JavaFX SDK | 26 | [gluonhq.com](https://gluonhq.com/products/javafx/) |
| JavaFX jmods | 26 | [gluonhq.com](https://gluonhq.com/products/javafx/) |

### Environment Variables

Set these before running any scripts:

```powershell
# Point to the /lib folder inside your JavaFX SDK
$env:PATH_TO_FX = "C:\JavaFX\javafx-sdk-26.0.1\lib"

# Point to the root of your JavaFX jmods package
$env:PATH_TO_FX_JMODS = "C:\JavaFX\javafx-jmods-26.0.1"
```

### Run in Development Mode

```powershell
.\run.ps1
```

This compiles all Java source files and launches the app directly — no installer needed.

### Build the Windows Installer

```powershell
.\package.ps1
```

This runs `jpackage` to produce a fully self-contained installer at `dist\UniGrade-1.0.0.exe`. Requires WiX Toolset v3 for the `.exe` packaging format.

> **WiX Toolset:** Download from [wixtoolset.org](https://wixtoolset.org/) or [GitHub Releases](https://github.com/wixtoolset/wix3/releases). After installation, restart PowerShell.

---

## 🤝 Contributing

Contributions are welcome! Here's how to get started:

1. **Fork** the repository.
2. **Create** a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit** your changes with a clear message:
   ```bash
   git commit -m "feat: add your feature description"
   ```
4. **Push** to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Open** a Pull Request against the `main` branch.

Please follow the existing code style (MVC separation, DAO pattern for all DB queries).

---

## 👨‍💻 Developer

**Haseeb Hassan**
- GitHub: [@Haseeb-Hassan66](https://github.com/Haseeb-Hassan66)

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
