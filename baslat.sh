#!/bin/bash
# Todo.app bu betigi calistirir. Isi: projeyi derleyip uygulamayi acmak.

PROJE="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJE" || exit 1

mkdir -p out
# Kod degistiyse yeniden derle. Hata olursa eldeki derlenmis surumle devam et.
/usr/bin/javac -encoding UTF-8 -d out src/todo/*.java > /dev/null 2>&1

IKON="$PROJE/Todo.app/Contents/Resources/applet.icns"

# nohup + & : uygulama arka planda acilsin, betik hemen bitsin.
nohup /usr/bin/java -cp out \
  -Xdock:name=Todo \
  -Xdock:icon="$IKON" \
  todo.Uygulama > /dev/null 2>&1 &

exit 0
