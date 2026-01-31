@echo off
if not exist bin mkdir bin
javac -d bin --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp "lib/sqlite-jdbc-3.36.0.3.jar" -sourcepath src/main/java src/main/java/ui/DashboardController.java src/main/java/ui/SemesterDetailController.java src/main/java/ui/SettingsController.java src/main/java/ui/UniversitySelectionController.java
if %errorlevel% neq 0 (
    echo Compilation Successful for Dashboard, SemesterDetail, Settings, and UniversitySelection controllers.
) else (
    echo Compilation Failed!
)
