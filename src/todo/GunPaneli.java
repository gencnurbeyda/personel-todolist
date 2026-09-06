package todo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Sag taraftaki panel: secili gunun basligi, gorev listesi
 * ve en altta yeni gorev yazma alani.
 */
public class GunPaneli extends JPanel {

    private static final Locale TR = Locale.forLanguageTag("tr-TR");

    private final GorevDeposu depo;
    private LocalDate gun = LocalDate.now();
    private Runnable degisiklikDinleyici;

    private final JLabel baslik = new JLabel();
    private final JPanel liste = new JPanel();
    private final GirisAlani giris = new GirisAlani("Yeni görev yaz, Enter'a bas");

    public GunPaneli(GorevDeposu depo) {
        this.depo = depo;
        setLayout(new BorderLayout());
        setBackground(Tasarim.BEYAZ);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Tasarim.ACIK_GRI),
                BorderFactory.createEmptyBorder(30, 30, 24, 30)));

        baslik.setFont(Tasarim.GUN_BASLIGI);
        baslik.setForeground(Tasarim.SIYAH);
        baslik.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(baslik, BorderLayout.NORTH);

        liste.setLayout(new BoxLayout(liste, BoxLayout.Y_AXIS));
        liste.setBackground(Tasarim.BEYAZ);

        // Listeyi yukari yaslamak icin araya bir tasiyici panel koyuyoruz.
        JPanel tasiyici = new JPanel(new BorderLayout());
        tasiyici.setBackground(Tasarim.BEYAZ);
        tasiyici.add(liste, BorderLayout.NORTH);

        JScrollPane kaydirma = new JScrollPane(tasiyici);
        kaydirma.setBorder(null);
        kaydirma.getViewport().setBackground(Tasarim.BEYAZ);
        kaydirma.setBackground(Tasarim.BEYAZ);
        kaydirma.getVerticalScrollBar().setUnitIncrement(16);
        add(kaydirma, BorderLayout.CENTER);

        giris.setFont(Tasarim.GOREV);
        giris.setForeground(Tasarim.SIYAH);
        giris.setCaretColor(Tasarim.SIYAH);
        giris.setBackground(Tasarim.BEYAZ);
        giris.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tasarim.ACIK_GRI),
                BorderFactory.createEmptyBorder(14, 2, 2, 2)));
        giris.addActionListener(olay -> gorevEkle());
        add(giris, BorderLayout.SOUTH);

        gunuGoster(gun);
    }

    public void setDegisiklikDinleyici(Runnable degisiklikDinleyici) {
        this.degisiklikDinleyici = degisiklikDinleyici;
    }

    /** Takvimde bir gune tiklandiginda cagrilir. */
    public void gunuGoster(LocalDate tarih) {
        this.gun = tarih;
        String gunAdi = tarih.getDayOfWeek().getDisplayName(TextStyle.FULL, TR).toUpperCase(TR);
        String ayAdi = tarih.getMonth().getDisplayName(TextStyle.FULL, TR).toUpperCase(TR);
        baslik.setText(tarih.getDayOfMonth() + " " + ayAdi + "  ·  " + gunAdi);
        listeyiTazele();
    }

    private void listeyiTazele() {
        liste.removeAll();
        List<Gorev> gorevler = depo.gununGorevleri(gun);
        if (gorevler.isEmpty()) {
            JLabel bos = new JLabel("Bu gün için not yok.");
            bos.setFont(Tasarim.GOREV);
            bos.setForeground(Tasarim.GRI);
            bos.setAlignmentX(LEFT_ALIGNMENT);
            liste.add(bos);
        } else {
            for (Gorev gorev : gorevler) {
                liste.add(gorevSatiri(gorev));
            }
        }
        liste.revalidate();
        liste.repaint();
    }

    private JPanel gorevSatiri(Gorev gorev) {
        JPanel satir = new JPanel(new BorderLayout(12, 0));
        satir.setBackground(Tasarim.BEYAZ);
        satir.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        satir.setAlignmentX(LEFT_ALIGNMENT);
        satir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel isaret = new JLabel(gorev.isTamamlandi() ? "●" : "○");
        isaret.setFont(Tasarim.GOREV);
        isaret.setForeground(Tasarim.SIYAH);
        isaret.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        isaret.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent olay) {
                gorev.setTamamlandi(!gorev.isTamamlandi());
                depo.kaydet();
                listeyiTazele();
            }
        });
        satir.add(isaret, BorderLayout.WEST);

        JLabel metin = new JLabel(gorev.getMetin());
        if (gorev.isTamamlandi()) {
            metin.setFont(Tasarim.ustuCizili(Tasarim.GOREV));
            metin.setForeground(Tasarim.GRI);
        } else {
            metin.setFont(Tasarim.GOREV);
            metin.setForeground(Tasarim.SIYAH);
        }
        satir.add(metin, BorderLayout.CENTER);

        JLabel sil = new JLabel("×");
        sil.setFont(Tasarim.GOREV);
        sil.setForeground(Tasarim.BEYAZ);
        sil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sil.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent olay) {
                depo.sil(gorev);
                listeyiTazele();
                haberVer();
            }
        });
        satir.add(sil, BorderLayout.EAST);

        // Fare satirin uzerine gelince silme isareti gorunur olsun.
        MouseAdapter uzerineGelme = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent olay) {
                sil.setForeground(Tasarim.GRI);
            }

            @Override
            public void mouseExited(MouseEvent olay) {
                if (!satir.getBounds().contains(olay.getPoint())) {
                    sil.setForeground(Tasarim.BEYAZ);
                }
            }
        };
        satir.addMouseListener(uzerineGelme);
        metin.addMouseListener(uzerineGelme);
        return satir;
    }

    private void gorevEkle() {
        String metin = giris.getText().trim();
        if (metin.isEmpty()) {
            return;
        }
        depo.ekle(new Gorev(gun, metin, false));
        giris.setText("");
        listeyiTazele();
        haberVer();
    }

    /** Takvimin de kendini yenilemesi icin haber verir (gun altindaki nokta). */
    private void haberVer() {
        if (degisiklikDinleyici != null) {
            degisiklikDinleyici.run();
        }
    }

    /**
     * Ici bosken soluk bir ipucu yazisi gosteren metin kutusu.
     */
    private static class GirisAlani extends JTextField {

        private final String ipucu;

        GirisAlani(String ipucu) {
            this.ipucu = ipucu;
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent olay) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent olay) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(new Color(180, 180, 180));
            int y = (getHeight() + g2.getFontMetrics().getAscent()
                    - g2.getFontMetrics().getDescent()) / 2 + 6;
            g2.drawString(ipucu, getInsets().left, y);
            g2.dispose();
        }
    }
}
