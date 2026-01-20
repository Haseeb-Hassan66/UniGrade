$FX_PATH = $env:PATH_TO_FX
if (-not $FX_PATH) {
    Write-Error "PATH_TO_FX environment variable is not set."
    exit 1
}
$LIB_PATH = "lib/sqlite-jdbc-3.36.0.3.jar"
$SRC_DIR = "src\main\java"
$RESOURCES_DIR = "src\main\resources"
$BIN_DIR = "bin"
# Clean bin
if (Test-Path $BIN_DIR) {
    Remove-Item -Recurse -Force $BIN_DIR
}
New-Item -ItemType Directory -Force -Path $BIN_DIR | Out-Null
# Compile
Write-Host "Compiling..."
$sources = Get-ChildItem -Recurse $SRC_DIR -Filter *.java | Select-Object -ExpandProperty FullName
if ($sources) {
    javac --module-path $FX_PATH --add-modules javafx.controls,javafx.fxml -cp $LIB_PATH -d $BIN_DIR $sources
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Compilation failed."
        exit $LASTEXITCODE
    }
} else {
    Write-Error "No Java source files found."
    exit 1
}
# Copy Resources
Write-Host "Copying resources..."
if (Test-Path $RESOURCES_DIR) {
    Copy-Item -Recurse -Force "$RESOURCES_DIR\*" $BIN_DIR
}
# Run
Write-Host "Running..."
java --module-path $FX_PATH --add-modules javafx.controls,javafx.fxml -cp "$BIN_DIR;$LIB_PATH" ui.App