package todo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Sol taraftaki aylik takvim. Gunlere tiklanir, secilen gun
 * sag taraftaki panele bildirilir.
 */
public class TakvimPaneli extends JPanel {

    /** Gun secildiginde haber vermek icin kucuk bir sozlesme. */
    public interface GunDinleyici {
        void gunSecildi(LocalDate tarih);
    }

    private static final Locale TR = Locale.forLanguageTag("tr-TR");
    private static final String[] GUN_KISALTMALARI = {"Pt", "Sa", "Ça", "Pe", "Cu", "Ct", "Pz"};

    private final GorevDeposu depo;
    private YearMonth gosterilenAy = YearMonth.now();
    private LocalDate seciliGun = LocalDate.now();
    private GunDinleyici dinleyici;
    private Runnable denemeEylemi;

    private final JLabel ayBasligi = new JLabel();
    private final JPanel izgara = new JPanel(new GridLayout(6, 7, 0, 2));

    public TakvimPaneli(GorevDeposu depo) {
        this.depo = depo;
        setLayout(new BorderLayout());
        setBackground(Tasarim.BEYAZ);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 24, 30));

        add(ustBar(), BorderLayout.NORTH);

        JPanel orta = new JPanel(new BorderLayout());
        orta.setBackground(Tasarim.BEYAZ);
        orta.add(gunAdlariSatiri(), BorderLayout.NORTH);
        izgara.setBackground(Tasarim.BEYAZ);
        orta.add(izgara, BorderLayout.CENTER);
        add(orta, BorderLayout.CENTER);

        add(altBar(), BorderLayout.SOUTH);
        tazele();
    }

    public void setGunDinleyici(GunDinleyici dinleyici) {
        this.dinleyici = dinleyici;
    }

    public void setDenemeEylemi(Runnable denemeEylemi) {
        this.denemeEylemi = denemeEylemi;
    }

    public LocalDate getSeciliGun() {
        return seciliGun;
    }

    private JPanel ustBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Tasarim.BEYAZ);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        ayBasligi.setFont(Tasarim.AY_BASLIGI);
        ayBasligi.setForeground(Tasarim.SIYAH);
        bar.add(ayBasligi, BorderLayout.WEST);

        JPanel oklar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        oklar.setBackground(Tasarim.BEYAZ);
        oklar.add(tiklanabilir("‹", Tasarim.OK, Tasarim.SIYAH, () -> ayDegistir(-1)));
        oklar.add(tiklanabilir("›", Tasarim.OK, Tasarim.SIYAH, () -> ayDegistir(1)));
        bar.add(oklar, BorderLayout.EAST);
        return bar;
    }

    private JPanel gunAdlariSatiri() {
        JPanel satir = new JPanel(new GridLayout(1, 7));
        satir.setBackground(Tasarim.BEYAZ);
        satir.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        for (String kisaltma : GUN_KISALTMALARI) {
            JLabel etiket = new JLabel(kisaltma, JLabel.CENTER);
            etiket.setFont(Tasarim.GUN_KISALTMASI);
            etiket.setForeground(Tasarim.GRI);
            satir.add(etiket);
        }
        return satir;
    }

    private JPanel altBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Tasarim.BEYAZ);
        bar.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        JLabel aciklama = new JLabel("•  görev olan gün");
        aciklama.setFont(Tasarim.KUCUK);
        aciklama.setForeground(Tasarim.GRI);
        bar.add(aciklama, BorderLayout.WEST);

        bar.add(tiklanabilir("bildirimi dene", Tasarim.KUCUK, Tasarim.GRI, () -> {
            if (denemeEylemi != null) {
                denemeEylemi.run();
            }
        }), BorderLayout.EAST);
        return bar;
    }

    private void ayDegistir(int adim) {
        gosterilenAy = gosterilenAy.plusMonths(adim);
        tazele();
    }

    /** Takvimi bastan cizer. Ay degisince veya gorev eklenince cagrilir. */
    public void tazele() {
        String ayAdi = gosterilenAy.getMonth().getDisplayName(TextStyle.FULL, TR).toUpperCase(TR);
        ayBasligi.setText(ayAdi + " " + gosterilenAy.getYear());

        izgara.removeAll();
        LocalDate ayinIlkGunu = gosterilenAy.atDay(1);
        // Pazartesi = 1 oldugu icin 1 cikarinca kac bos hucre gerektigini buluyoruz.
        int bosHucre = ayinIlkGunu.getDayOfWeek().getValue() - 1;
        int gunSayisi = gosterilenAy.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            int gunNumarasi = i - bosHucre + 1;
            if (gunNumarasi >= 1 && gunNumarasi <= gunSayisi) {
                izgara.add(new GunHucresi(gosterilenAy.atDay(gunNumarasi)));
            } else {
                JPanel bos = new JPanel();
                bos.setBackground(Tasarim.BEYAZ);
                izgara.add(bos);
            }
        }
        izgara.revalidate();
        izgara.repaint();
    }

    /** Disaridan (ornegin uygulama acilirken) gun secmek icin. */
    public void gunuSec(LocalDate tarih) {
        seciliGun = tarih;
        gosterilenAy = YearMonth.from(tarih);
        tazele();
        if (dinleyici != null) {
            dinleyici.gunSecildi(tarih);
        }
    }

    private static JLabel tiklanabilir(String yazi, Font font, Color renk, Runnable eylem) {
        JLabel etiket = new JLabel(yazi);
        etiket.setFont(font);
        etiket.setForeground(renk);
        etiket.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        etiket.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent olay) {
                eylem.run();
            }

            @Override
            public void mouseEntered(MouseEvent olay) {
                etiket.setForeground(Tasarim.SIYAH);
            }

            @Override
            public void mouseExited(MouseEvent olay) {
                etiket.setForeground(renk);
            }
        });
        return etiket;
    }

    /**
     * Takvimdeki tek bir gun kutusu. Kendini kendisi cizer:
     * secili gun dolu daire, bugun ince halka, gorev varsa altinda nokta.
     */
    private class GunHucresi extends JPanel {

        private final LocalDate tarih;

        GunHucresi(LocalDate tarih) {
            this.tarih = tarih;
            setBackground(Tasarim.BEYAZ);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent olay) {
                    seciliGun = tarih;
                    tazele();
                    if (dinleyici != null) {
                        dinleyici.gunSecildi(tarih);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            boolean secili = tarih.equals(seciliGun);
            boolean bugun = tarih.equals(LocalDate.now());

            int genislik = getWidth();
            int yukseklik = getHeight();
            int cap = Math.min(Math.min(genislik, yukseklik) - 10, 34);
            int x = (genislik - cap) / 2;
            int y = (yukseklik - cap) / 2 - 3;

            if (secili) {
                g2.setColor(Tasarim.SIYAH);
                g2.fillOval(x, y, cap, cap);
            } else if (bugun) {
                g2.setColor(Tasarim.GRI);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawOval(x, y, cap, cap);
            }

            g2.setFont(Tasarim.GUN_NUMARASI);
            g2.setColor(secili ? Tasarim.BEYAZ : Tasarim.SIYAH);
            String yazi = String.valueOf(tarih.getDayOfMonth());
            FontMetrics olcu = g2.getFontMetrics();
            int yaziX = (genislik - olcu.stringWidth(yazi)) / 2;
            int yaziY = y + cap / 2 + olcu.getAscent() / 2 - 1;
            g2.drawString(yazi, yaziX, yaziY);

            if (depo.gorevSayisi(tarih) > 0) {
                // Nokta dairenin altinda, yani her zaman beyaz zeminde: siyah kalmali.
                g2.setColor(Tasarim.SIYAH);
                int noktaCap = 4;
                g2.fillOval((genislik - noktaCap) / 2, y + cap + 3, noktaCap, noktaCap);
            }
            g2.dispose();
        }
    }
}
