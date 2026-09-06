package todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.Timer;

/**
 * Saati takip eder ve zamani gelince masaustunde bildirim karti acar.
 *
 * Iki bildirim var:
 *  - Her sabah 09:00: o gunun gorevleri
 *  - Pazar aksami 20:00: gelecek haftanin gorevleri
 */
public class HatirlatmaServisi {

    private static final Locale TR = Locale.forLanguageTag("tr-TR");
    private static final LocalTime SABAH_SAATI = LocalTime.of(9, 0);
    private static final LocalTime PAZAR_SAATI = LocalTime.of(20, 0);
    private static final int EN_FAZLA_SATIR = 5;

    private final GorevDeposu depo;
    private final Path durumDosyasi;
    private final Timer zamanlayici;

    private LocalDate sonGunlukBildirim;
    private LocalDate sonHaftalikBildirim;

    public HatirlatmaServisi(GorevDeposu depo, Path durumDosyasi) {
        this.depo = depo;
        this.durumDosyasi = durumDosyasi;
        durumuOku();
        // 30 saniyede bir saate bakan bir zamanlayici.
        this.zamanlayici = new Timer(30_000, olay -> kontrolEt());
        this.zamanlayici.setInitialDelay(4000);
    }

    public void baslat() {
        zamanlayici.start();
    }

    private void kontrolEt() {
        LocalDate bugun = LocalDate.now();
        LocalTime simdi = LocalTime.now();

        if (!bugun.equals(sonGunlukBildirim) && !simdi.isBefore(SABAH_SAATI)) {
            gunlukKartiGoster();
            sonGunlukBildirim = bugun;
            durumuKaydet();
        }

        if (bugun.getDayOfWeek() == DayOfWeek.SUNDAY
                && !bugun.equals(sonHaftalikBildirim)
                && !simdi.isBefore(PAZAR_SAATI)) {
            haftalikKartiGoster(bugun);
            sonHaftalikBildirim = bugun;
            durumuKaydet();
        }
    }

    /** "bildirimi dene" yazisina tiklandiginda calisir. */
    public void gunlukKartiniZorla() {
        gunlukKartiGoster();
    }

    private void gunlukKartiGoster() {
        LocalDate bugun = LocalDate.now();
        List<String> satirlar = new ArrayList<>();
        int sayac = 0;
        int kalan = 0;

        for (Gorev gorev : depo.gununGorevleri(bugun)) {
            if (gorev.isTamamlandi()) {
                continue;
            }
            if (sayac < EN_FAZLA_SATIR) {
                satirlar.add("—  " + gorev.getMetin());
                sayac++;
            } else {
                kalan++;
            }
        }
        if (satirlar.isEmpty()) {
            satirlar.add("Bugün için kayıtlı görev yok.");
        } else if (kalan > 0) {
            satirlar.add("+ " + kalan + " tane daha");
        }

        String ayAdi = bugun.getMonth().getDisplayName(TextStyle.FULL, TR).toUpperCase(TR);
        BildirimKarti.goster("Bugün · " + bugun.getDayOfMonth() + " " + ayAdi, satirlar);
    }

    private void haftalikKartiGoster(LocalDate pazar) {
        LocalDate baslangic = pazar.plusDays(1);
        LocalDate bitis = pazar.plusDays(7);

        List<String> satirlar = new ArrayList<>();
        int sayac = 0;
        int kalan = 0;

        for (Gorev gorev : depo.aradakiGorevler(baslangic, bitis)) {
            if (gorev.isTamamlandi()) {
                continue;
            }
            if (sayac < EN_FAZLA_SATIR) {
                String gunKisa = gorev.getTarih().getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, TR);
                satirlar.add(gunKisa + "  ·  " + gorev.getMetin());
                sayac++;
            } else {
                kalan++;
            }
        }
        if (satirlar.isEmpty()) {
            satirlar.add("Gelecek hafta boş görünüyor.");
        } else if (kalan > 0) {
            satirlar.add("+ " + kalan + " tane daha");
        }

        BildirimKarti.goster("Gelecek hafta", satirlar);
    }

    /**
     * Ayni bildirimi gun icinde tekrar tekrar gostermemek icin
     * en son ne zaman bildirim verdigimizi kucuk bir dosyaya yaziyoruz.
     */
    private void durumuOku() {
        if (!Files.exists(durumDosyasi)) {
            return;
        }
        try {
            for (String satir : Files.readAllLines(durumDosyasi, StandardCharsets.UTF_8)) {
                if (satir.startsWith("gunluk=")) {
                    sonGunlukBildirim = LocalDate.parse(satir.substring(7));
                } else if (satir.startsWith("haftalik=")) {
                    sonHaftalikBildirim = LocalDate.parse(satir.substring(9));
                }
            }
        } catch (Exception hata) {
            // Dosya bozuksa onemli degil, bastan olusur.
            sonGunlukBildirim = null;
            sonHaftalikBildirim = null;
        }
    }

    private void durumuKaydet() {
        try {
            Path klasor = durumDosyasi.getParent();
            if (klasor != null) {
                Files.createDirectories(klasor);
            }
            List<String> satirlar = new ArrayList<>();
            satirlar.add("gunluk=" + sonGunlukBildirim);
            satirlar.add("haftalik=" + sonHaftalikBildirim);
            Files.write(durumDosyasi, satirlar, StandardCharsets.UTF_8);
        } catch (IOException hata) {
            System.out.println("Bildirim durumu kaydedilemedi: " + hata.getMessage());
        }
    }
}
