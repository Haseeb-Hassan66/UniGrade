<div align="center">
  <h1>🎓 UniGrade</h1>
  <p><b>Real-Time Academic Performance Tracker & Grade Predictor</b></p>
  
  ![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![JavaFX](https://img.shields.io/badge/JavaFX-UI-007396?style=for-the-badge&logo=java&logoColor=white)
  ![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
  ![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
</div>

<br/>

## 🎯 Why UniGrade?

Most university portals and academic systems are completely **static**. They wait until the end of the semester to hand you a transcript, at which point it's entirely too late to change your academic trajectory. 

**UniGrade is built to solve this problem.** 

UniGrade is a highly interactive, real-time application designed to put students back in control *during* the semester. By allowing you to enter your midterm, quiz, and sessional marks as they happen, UniGrade instantly live-calculates your current standing. It eliminates the guesswork, helping you pinpoint exactly how many marks you need in your final exams to secure your target grade. 

Stop waiting for the end of the semester to see how you did. Track your progress, predict your grades, and study smarter.

---

## ✨ Key Features & Capabilities

Based entirely on real-time, interactive tracking, UniGrade provides:

### 📊 Real-Time Marks Entry & Live Prediction
- Enter marks component-by-component (e.g., *Midterm, Sessional, Final, Lab, Viva*).
- Support for splitting subjects into **Theory** and **Practical** components.
- Instantly updates your live-calculated percentage, grade letter (e.g., A, B+), and grade points without requiring a completed semester.

### ⚙️ Completely Customizable Policies
Every university is different. UniGrade molds to fit your institution's specific rules:
- **Assessment Policies:** Define exactly how a subject is graded. If your syllabus states 30% Mids, 20% Sessionals, and 50% Finals, you can configure those exact input fields.
- **Grading Policies:** Define your university's exact grading scale (e.g., >= 85% equals 'A', which represents a 4.0 GPA).

### 📁 Comprehensive Academic Management
- **Semesters & Subjects:** Organize your academic history. Create semesters, populate them with subjects, and assign independent credit hours for Theory and Practical work.
- **Dynamic Reports:** Instantly generate Semester Results (GPA) and an overall Academic Report (CGPA) the moment you save your marks. 
- **Multi-University Support:** Seamlessly track academic progress across different universities with completely isolated policies and semesters.

### 🌍 Global & Secure
- **Internationalization (i18n):** Full multi-language UI support. Interface strings are dynamically loaded via resource bundles.
- **Local Privacy:** UniGrade uses a local SQLite database. All your academic data is securely stored on your own machine. No cloud, no internet required.

---

## 🏗️ Technical Architecture

UniGrade is built using robust Java enterprise patterns adapted for a desktop environment:
- **Language:** Java (JDK 11+)
- **UI Framework:** JavaFX (with FXML for view separation)
- **Database:** SQLite (via JDBC)
- **Design Pattern:** Model-View-Controller (MVC) combined with Data Access Objects (DAO) for clean database interactions.

### Project Structure
```text
UniGrade/
├── lib/                        # External dependencies (sqlite-jdbc)
├── src/
│   └── main/
│       ├── java/               
│       │   ├── dao/            # Database query layers
│       │   ├── model/          # Entities (Semester, Subject, Policy)
│       │   ├── service/        # Business logic (Result calculation)
│       │   ├── ui/             # JavaFX Controllers 
│       │   └── util/           # App utilities (DB init, UI helpers)
│       └── resources/          
│           ├── fxml/           # User Interface layouts
│           ├── icons/          # Visual assets
│           └── Messages.properties # Translation & i18n keys
├── compile_i18n.bat            # Helper script
├── run.ps1                     # Build & Execution script
└── LICENSE                     # MIT License
```

---

## 🚀 Getting Started

### 1. Prerequisites
To run UniGrade locally, you need the following installed:
- **Java Development Kit (JDK):** Version 11 or higher.
- **JavaFX SDK:** Download and extract the JavaFX SDK matching your JDK version.

### 2. Environment Setup
You must define where your JavaFX libraries are located so the application can compile and run.
- Set an environment variable named `PATH_TO_FX` pointing to the `lib` folder inside your JavaFX SDK.
  - *Windows PowerShell Example:* `$env:PATH_TO_FX="C:\path\to\javafx-sdk\lib"`

### 3. Build & Run
UniGrade includes an automated PowerShell script that cleans the build directory, compiles all Java files, copies resources, and launches the app.
1. Open Windows PowerShell.
2. Navigate to the root directory of the project: `cd "path/to/UniGrade"`
3. Execute the run script:
   ```powershell
   .\run.ps1
   ```

---

## 📖 Usage Guide

Here is a quick workflow of how to use UniGrade from scratch:

1. **First Run Setup:** When you launch the app, you will be prompted to create your User Profile and set up your first University.
2. **Configure Policies (Crucial):** Navigate to Settings. Ensure your University's **Grading Policy** (A, B, C scales) and **Assessment Policy** (Mids, Finals components) are set up correctly. This dictates how your marks are calculated.
3. **Add Semesters:** Go to the Dashboard and create a new Semester (e.g., "Fall 2024").
4. **Add Subjects:** Open the semester and add your enrolled subjects, allocating the correct Credit Hours.
5. **Enter Marks:** Click on a subject to open the interactive Marks Entry dialog. As you take exams during the semester, input your scores here to instantly see your standing.
6. **View Reports:** Click "Analysis" on the Dashboard to see your semester GPA, total credits earned, and a visual representation of your academic standing.

---

## 👨‍💻 About the Developer

**Haseeb Hassan**  
*Software Developer & Creator of UniGrade*

UniGrade was created out of a personal need for a better way to track academic performance dynamically. I built this application to empower students like myself to take control of their grades before it's too late. I am passionate about software development and solving real-world problems with elegant code.

---

## 🤝 Contributing

Contributions to improve UniGrade are welcome!
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/NewFeature`
3. Commit your changes: `git commit -m 'Add NewFeature'`
4. Push to the branch: `git push origin feature/NewFeature`
5. Submit a Pull Request.

## 📄 License
This project is available under the standard **MIT License**. Feel free to use, modify, and distribute it. See the [LICENSE](LICENSE) file for more details.
