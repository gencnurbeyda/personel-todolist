package todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gorevleri hafizada tutar ve bir metin dosyasina kaydeder.
 * Uygulamayi kapatip actiginda gorevlerin kaybolmamasinin sebebi bu sinif.
 */
public class GorevDeposu {

    private final Path dosya;
    private final List<Gorev> gorevler = new ArrayList<>();

    public GorevDeposu(Path dosya) {
        this.dosya = dosya;
        oku();
    }

    /** Dosyadaki her satiri okuyup Gorev nesnesine cevirir. */
    private void oku() {
        gorevler.clear();
        if (!Files.exists(dosya)) {
            return;
        }
        try {
            List<String> satirlar = Files.readAllLines(dosya, StandardCharsets.UTF_8);
            for (String satir : satirlar) {
                if (satir.isBlank()) {
                    continue;
                }
                Gorev gorev = Gorev.satirdanOku(satir);
                if (gorev != null) {
                    gorevler.add(gorev);
                }
            }
        } catch (IOException hata) {
            System.out.println("Gorevler okunamadi: " + hata.getMessage());
        }
    }

    /** Hafizadaki butun gorevleri dosyanin uzerine yazar. */
    public void kaydet() {
        try {
            Path klasor = dosya.getParent();
            if (klasor != null) {
                Files.createDirectories(klasor);
            }
            List<String> satirlar = new ArrayList<>();
            for (Gorev gorev : gorevler) {
                satirlar.add(gorev.satiraCevir());
            }
            Files.write(dosya, satirlar, StandardCharsets.UTF_8);
        } catch (IOException hata) {
            System.out.println("Gorevler kaydedilemedi: " + hata.getMessage());
        }
    }

    public void ekle(Gorev gorev) {
        gorevler.add(gorev);
        kaydet();
    }

    public void sil(Gorev gorev) {
        gorevler.remove(gorev);
        kaydet();
    }

    /** Belirli bir gune ait gorevleri dondurur. */
    public List<Gorev> gununGorevleri(LocalDate tarih) {
        List<Gorev> sonuc = new ArrayList<>();
        for (Gorev gorev : gorevler) {
            if (gorev.getTarih().equals(tarih)) {
                sonuc.add(gorev);
            }
        }
        return sonuc;
    }

    /** Iki tarih arasindaki (iki uc dahil) gorevleri tarih sirasiyla dondurur. */
    public List<Gorev> aradakiGorevler(LocalDate baslangic, LocalDate bitis) {
        List<Gorev> sonuc = new ArrayList<>();
        for (Gorev gorev : gorevler) {
            LocalDate t = gorev.getTarih();
            if (!t.isBefore(baslangic) && !t.isAfter(bitis)) {
                sonuc.add(gorev);
            }
        }
        sonuc.sort((a, b) -> a.getTarih().compareTo(b.getTarih()));
        return sonuc;
    }

    /** Takvimde gunun altina nokta koyup koymayacagimizi anlamak icin. */
    public int gorevSayisi(LocalDate tarih) {
        return gununGorevleri(tarih).size();
    }
}
