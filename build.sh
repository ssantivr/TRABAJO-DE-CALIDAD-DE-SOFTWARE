#!/usr/bin/env bash
# Automatización de la construcción del proyecto Calculadora (requiere JDK 17+).
# Uso: ./build.sh [todo|limpiar|compilar|probar|jar|ejecutar|gui|dependencias|distribucion]
set -euo pipefail
cd "$(dirname "$0")"

VERSION="1.1.0"
SALIDA="build"
JAR="$SALIDA/calculadora-$VERSION.jar"
VERSION_FLATLAF="3.5.4"
OPCIONES_JAVA="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8"

compilar() {
  # src/test (JUnit) no se compila aquí: necesita el jar de JUnit (ver README).
  mkdir -p "$SALIDA/classes" "$SALIDA/hoja-classes"
  find src/main -name '*.java' > "$SALIDA/fuentes-principales.txt"
  find src/hoja-pruebas -name '*.java' > "$SALIDA/fuentes-hoja.txt"
  javac --release 17 -encoding UTF-8 -Xlint:all -d "$SALIDA/classes" @"$SALIDA/fuentes-principales.txt"
  javac --release 17 -encoding UTF-8 -Xlint:all -cp "$SALIDA/classes" -d "$SALIDA/hoja-classes" @"$SALIDA/fuentes-hoja.txt"
  echo "[compilar] OK"
}

ejecutar_pruebas() {
  # shellcheck disable=SC2086
  java $OPCIONES_JAVA -Djava.awt.headless=true -cp "$SALIDA/classes:$SALIDA/hoja-classes" com.calculadora.HojaDePruebas docs
}

crear_jar() {
  # Las bibliotecas opcionales (FlatLaf) de lib/ se referencian desde el manifiesto, relativas a build/.
  local cp=""
  for f in lib/*.jar; do [ -e "$f" ] && cp="$cp ../$f"; done
  { echo "Main-Class: com.calculadora.Principal"; [ -n "$cp" ] && echo "Class-Path:$cp"; } > "$SALIDA/manifiesto.txt"
  jar cfm "$JAR" "$SALIDA/manifiesto.txt" -C "$SALIDA/classes" .
  echo "[jar] $JAR"
}

obtener_dependencias() {
  # Opcional: apariencia moderna FlatLaf. Sin ella la GUI usa Nimbus.
  mkdir -p lib
  curl -fSL -o "lib/flatlaf-$VERSION_FLATLAF.jar" \
    "https://repo1.maven.org/maven2/com/formdev/flatlaf/$VERSION_FLATLAF/flatlaf-$VERSION_FLATLAF.jar"
  echo "[dependencias] lib/flatlaf-$VERSION_FLATLAF.jar (ejecute ./build.sh jar para actualizar el manifiesto)"
}

crear_distribucion() {
  mkdir -p dist
  local nombre="calculadora-v$VERSION"
  if command -v zip >/dev/null 2>&1; then
    zip -qr "dist/$nombre.zip" . -x 'build/*' 'dist/*' '.git/*'
    echo "[distribucion] dist/$nombre.zip"
  else
    tar --exclude=./build --exclude=./dist --exclude=./.git -czf "dist/$nombre.tar.gz" .
    echo "[distribucion] dist/$nombre.tar.gz"
  fi
}

case "${1:-todo}" in
  limpiar)      rm -rf "$SALIDA" dist; echo "[limpiar] OK" ;;
  compilar)     compilar ;;
  probar)       compilar; ejecutar_pruebas ;;
  jar)          compilar; crear_jar ;;
  # shellcheck disable=SC2086
  ejecutar)     compilar; crear_jar; java $OPCIONES_JAVA -jar "$JAR" --consola ;;
  # shellcheck disable=SC2086
  gui)          compilar; crear_jar; java $OPCIONES_JAVA -jar "$JAR" --gui ;;
  dependencias) obtener_dependencias ;;
  todo)         compilar; ejecutar_pruebas; crear_jar ;;
  distribucion) compilar; ejecutar_pruebas; crear_jar; crear_distribucion ;;
  *) echo "Uso: $0 [todo|limpiar|compilar|probar|jar|ejecutar|gui|dependencias|distribucion]"; exit 2 ;;
esac
