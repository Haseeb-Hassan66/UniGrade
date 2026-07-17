# ============================================================
# UniGrade - Native Windows Installer Build Script
# Produces: dist\UniGrade-1.0.0.exe (self-contained, no Java needed)
# ============================================================

$APP_NAME      = "UniGrade"
$APP_VERSION   = "1.0.0"
$MAIN_CLASS    = "ui.App"
$APP_VENDOR    = "Haseeb Hassan"
$APP_DESC      = "Real-Time Academic Performance Tracker and Grade Predictor"

$FX_PATH       = $env:PATH_TO_FX
$FX_JMODS_PATH = $env:PATH_TO_FX_JMODS
$LIB_PATH      = "lib\sqlite-jdbc-3.36.0.3.jar"
$SRC_DIR       = "src\main\java"
$RESOURCES_DIR = "src\main\resources"
$BIN_DIR       = "bin"
$JARS_DIR      = "jars"
$DIST_DIR      = "dist"
$ICON_PATH     = "assets\unigrade.ico"

# ============================================================
# Pre-flight checks
# ============================================================
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   UniGrade Installer Build v$APP_VERSION" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (-not $FX_PATH) {
    Write-Error "ERROR: PATH_TO_FX environment variable is not set."
    Write-Error "Set it to the lib folder of your JavaFX SDK, e.g.:"
    Write-Error '  $env:PATH_TO_FX = "C:\JavaFX\javafx-sdk-26.0.1\lib"'
    exit 1
}

if (-not (Test-Path $FX_PATH)) {
    Write-Error "ERROR: JavaFX SDK not found at: $FX_PATH"
    exit 1
}

if (-not $FX_JMODS_PATH) {
    # Default guess next to the SDK folder
    $guessedJmods = "C:\JavaFX\javafx-jmods-26.0.1"
    if (Test-Path $guessedJmods) {
        $FX_JMODS_PATH = $guessedJmods
        Write-Host "      Auto-detected jmods at: $FX_JMODS_PATH" -ForegroundColor DarkGray
    } else {
        Write-Error "ERROR: PATH_TO_FX_JMODS environment variable is not set."
        Write-Error "Download the JavaFX jmods package from: https://gluonhq.com/products/javafx/"
        Write-Error "Then set: `$env:PATH_TO_FX_JMODS = 'C:\JavaFX\javafx-jmods-26.0.1'"
        exit 1
    }
}

if (-not (Test-Path $FX_JMODS_PATH)) {
    Write-Error "ERROR: JavaFX jmods not found at: $FX_JMODS_PATH"
    Write-Error "Download the jmods package from: https://gluonhq.com/products/javafx/"
    exit 1
}

if (-not (Test-Path $LIB_PATH)) {
    Write-Error "ERROR: SQLite JDBC not found at: $LIB_PATH"
    exit 1
}

if (-not (Test-Path $ICON_PATH)) {
    Write-Warning "WARNING: Icon not found at $ICON_PATH - installer will use default icon."
}

# Locate jpackage - first try PATH, then fall back via java.exe location
$jpackageExe = (Get-Command jpackage -ErrorAction SilentlyContinue)
if (-not $jpackageExe) {
    $javaOnPath = (Get-Command java -ErrorAction SilentlyContinue)
    if ($javaOnPath) {
        $detectedJdkBin = Split-Path -Parent $javaOnPath.Source
        $detectedHome   = Split-Path -Parent $detectedJdkBin
        if (Test-Path "$detectedHome\bin\jpackage.exe") {
            $env:PATH = "$detectedHome\bin;" + $env:PATH
            $jpackageExe = Get-Command jpackage -ErrorAction SilentlyContinue
            Write-Host "      Found jpackage at: $detectedHome\bin" -ForegroundColor DarkGray
        }
    }
}
if (-not $jpackageExe) {
    Write-Error "ERROR: jpackage not found."
    Write-Error "Fix: Add your JDK bin folder to PATH permanently via System Environment Variables."
    exit 1
}

# ============================================================
# Step 1: Clean
# ============================================================
Write-Host "[1/5] Cleaning old build directories..." -ForegroundColor Yellow
foreach ($dir in @($BIN_DIR, $JARS_DIR, $DIST_DIR)) {
    if (Test-Path $dir) {
        Remove-Item -Recurse -Force $dir
        Write-Host "      Removed: $dir"
    }
}
New-Item -ItemType Directory -Force -Path $BIN_DIR  | Out-Null
New-Item -ItemType Directory -Force -Path $JARS_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $DIST_DIR | Out-Null

# ============================================================
# Step 2: Compile
# ============================================================
Write-Host ""
Write-Host "[2/5] Compiling Java source files..." -ForegroundColor Yellow

$sources = Get-ChildItem -Recurse $SRC_DIR -Filter *.java | Select-Object -ExpandProperty FullName
if (-not $sources) {
    Write-Error "ERROR: No .java source files found in $SRC_DIR"
    exit 1
}

javac `
    --module-path $FX_PATH `
    --add-modules javafx.controls,javafx.fxml `
    -cp $LIB_PATH `
    -d $BIN_DIR `
    $sources

if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: Compilation failed."
    exit $LASTEXITCODE
}
Write-Host "      Compilation successful." -ForegroundColor Green

# ============================================================
# Step 3: Copy resources + build app JAR
# ============================================================
Write-Host ""
Write-Host "[3/5] Packaging resources and creating app JAR..." -ForegroundColor Yellow

if (Test-Path $RESOURCES_DIR) {
    Copy-Item -Recurse -Force "$RESOURCES_DIR\*" $BIN_DIR
}

$manifestDir = "$BIN_DIR\META-INF"
New-Item -ItemType Directory -Force -Path $manifestDir | Out-Null
$manifestContent = "Manifest-Version: 1.0`r`nMain-Class: $MAIN_CLASS`r`nClass-Path: sqlite-jdbc-3.36.0.3.jar`r`n"
Set-Content -Path "$manifestDir\MANIFEST.MF" -Value $manifestContent -Encoding ASCII -NoNewline

$appJar = "$JARS_DIR\$APP_NAME.jar"
jar --create --file=$appJar --manifest="$manifestDir\MANIFEST.MF" -C $BIN_DIR .
if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: JAR creation failed."
    exit $LASTEXITCODE
}

Copy-Item $LIB_PATH $JARS_DIR
Write-Host "      JAR created: $appJar" -ForegroundColor Green

# ============================================================
# Step 4: Run jpackage
# ============================================================
Write-Host ""
Write-Host "[4/5] Running jpackage to create native installer..." -ForegroundColor Yellow
Write-Host "      (This may take 1-3 minutes, please wait...)" -ForegroundColor DarkGray

$jpackageArgs = @(
    "--type", "exe",
    "--name", $APP_NAME,
    "--app-version", $APP_VERSION,
    "--vendor", $APP_VENDOR,
    "--description", $APP_DESC,
    "--dest", $DIST_DIR,
    "--input", $JARS_DIR,
    "--main-jar", "$APP_NAME.jar",
    "--main-class", $MAIN_CLASS,
    "--module-path", $FX_JMODS_PATH,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base,java.sql",
    "--java-options", "--add-opens=javafx.graphics/com.sun.glass.utils=ALL-UNNAMED",
    "--java-options", "--enable-native-access=ALL-UNNAMED",
    "--win-menu",
    "--win-shortcut",
    "--win-dir-chooser",
    "--win-menu-group", $APP_NAME
)

if (Test-Path $ICON_PATH) {
    $jpackageArgs += "--icon"
    $jpackageArgs += (Resolve-Path $ICON_PATH).Path
}

& jpackage $jpackageArgs

if ($LASTEXITCODE -ne 0) {
    Write-Error "ERROR: jpackage failed. See output above for details."
    Write-Host ""
    Write-Host "Common fix:" -ForegroundColor Yellow
    Write-Host "  Install WiX Toolset v3 from: https://github.com/wixtoolset/wix3/releases" -ForegroundColor White
    Write-Host "  Then restart PowerShell and try again." -ForegroundColor White
    exit $LASTEXITCODE
}

# ============================================================
# Step 5: Done!
# ============================================================
Write-Host ""
Write-Host "[5/5] Build complete!" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

$installer = Get-ChildItem -Path $DIST_DIR -Filter "*.exe" | Select-Object -First 1
if ($installer) {
    $sizeMB = [math]::Round($installer.Length / 1MB, 1)
    Write-Host "  Installer : $($installer.FullName)" -ForegroundColor White
    Write-Host "  Size      : $sizeMB MB" -ForegroundColor White
} else {
    $any = Get-ChildItem -Path $DIST_DIR | Select-Object -First 1
    if ($any) {
        Write-Host "  Output    : $($any.FullName)" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "  Next steps:" -ForegroundColor Yellow
Write-Host "  1. Test the installer on a machine without Java installed" -ForegroundColor White
Write-Host "  2. Upload the .exe to your GitHub Releases page" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
