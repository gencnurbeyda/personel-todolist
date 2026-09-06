#!/bin/bash
# Bu dosyaya cift tiklayinca uygulama derlenir ve acilir.

cd "$(dirname "$0")" || exit 1

mkdir -p out

javac -encoding UTF-8 -d out src/todo/*.java
if [ $? -ne 0 ]; then
  echo ""
  echo "Derleme hatasi. Yukaridaki mesaji oku."
  read -n 1 -s -r -p "Kapatmak icin bir tusa bas..."
  exit 1
fi

java -cp out -Xdock:name="Todo" todo.Uygulama
