package todo;

import java.time.LocalDate;

/**
 * Tek bir gorevi temsil eder: hangi gune ait, ne yazyor, yapildi mi.
 */
public class Gorev {

    private final LocalDate tarih;
    private final String metin;
    private boolean tamamlandi;

    public Gorev(LocalDate tarih, String metin, boolean tamamlandi) {
        this.tarih = tarih;
        this.metin = metin;
        this.tamamlandi = tamamlandi;
    }

    public LocalDate getTarih() {
        return tarih;
    }

    public String getMetin() {
        return metin;
    }

    public boolean isTamamlandi() {
        return tamamlandi;
    }

    public void setTamamlandi(boolean tamamlandi) {
        this.tamamlandi = tamamlandi;
    }

    /** Gorevi dosyaya yazilabilecek tek bir satira cevirir. */
    public String satiraCevir() {
        return tarih + "|" + (tamamlandi ? "1" : "0") + "|" + metin;
    }

    /**
     * Dosyadan okunan bir satiri tekrar Gorev nesnesine cevirir.
     * Satir bozuksa null dondurur; boylece program cokmez.
     */
    public static Gorev satirdanOku(String satir) {
        String[] parcalar = satir.split("\\|", 3);
        if (parcalar.length < 3) {
            return null;
        }
        try {
            LocalDate tarih = LocalDate.parse(parcalar[0]);
            boolean tamamlandi = parcalar[1].equals("1");
            return new Gorev(tarih, parcalar[2], tamamlandi);
        } catch (Exception hata) {
            return null;
        }
    }
}
