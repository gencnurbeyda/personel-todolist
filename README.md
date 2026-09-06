# personel-todolist

Java + Swing ile yazılmış, macOS masaüstü için sade bir görev takip uygulaması.

Siyah-beyaz, dikkat dağıtmayan bir arayüz: solda aylık takvim, sağda seçili günün görevleri. Belirlenen saatlerde masaüstünün sağ üst köşesinde küçük bir hatırlatma kartı çıkar.

## Özellikler

- Aylık takvim görünümü; görevi olan günler noktayla işaretli
- Güne tıklayıp o günün görevlerini yazma, tamamlama, silme
- Her sabah 09:00 — o günün görevlerini gösteren bildirim kartı
- Pazar akşamı 20:00 — gelecek haftanın görevlerini gösteren bildirim kartı
- Görevler düz metin dosyasında saklanır, veritabanı gerekmez

## Gereksinimler

Java 17 veya üzeri. Başka hiçbir bağımlılık yok — Swing, Java ile birlikte gelir.

## Çalıştırma

```bash
javac -encoding UTF-8 -d out src/todo/*.java
java -cp out todo.Uygulama
```

macOS'ta `Todo.app`'e çift tıklamak da yeterlidir.

## Yapı

| Dosya | Görevi |
|---|---|
| `Uygulama.java` | Programın başlangıç noktası, parçaları birbirine bağlar |
| `Gorev.java` | Tek bir görevi temsil eder |
| `GorevDeposu.java` | Görevleri dosyaya kaydeder ve okur |
| `TakvimPaneli.java` | Aylık takvim, hücreleri kendisi çizer |
| `GunPaneli.java` | Seçili günün listesi ve giriş alanı |
| `HatirlatmaServisi.java` | Saati takip eder, bildirimleri tetikler |
| `BildirimKarti.java` | Masaüstünde beliren bildirim penceresi |
| `Tasarim.java` | Renkler ve yazı tipleri |

Görevler `veri/gorevler.txt` içinde `tarih|durum|metin` biçiminde tutulur.
