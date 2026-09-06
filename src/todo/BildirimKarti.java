package todo;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 * Masaustunun sag alt kosesinde beliren kucuk siyah bildirim kartu.
 * Cerceve, baslik cubugu, dugme yok: JWindow tam da bunun icin var.
 */
public class BildirimKarti {

    /** Ayni anda ekranda tek kart olsun diye acik karti burada tutuyoruz. */
    private static JWindow acikKart;

    private BildirimKarti() {
    }

    public static void goster(String baslik, List<String> satirlar) {
        kapat();

        JPanel icerik = new JPanel();
        icerik.setLayout(new BoxLayout(icerik, BoxLayout.Y_AXIS));
        icerik.setBackground(Tasarim.SIYAH);
        icerik.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel baslikEtiketi = new JLabel(baslik.toUpperCase());
        baslikEtiketi.setFont(Tasarim.KART_BASLIGI);
        baslikEtiketi.setForeground(Tasarim.GRI);
        baslikEtiketi.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        icerik.add(baslikEtiketi);
        icerik.add(Box.createVerticalStrut(10));

        for (String satir : satirlar) {
            JLabel satirEtiketi = new JLabel(satir);
            satirEtiketi.setFont(Tasarim.KART_SATIRI);
            satirEtiketi.setForeground(Tasarim.BEYAZ);
            satirEtiketi.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            icerik.add(satirEtiketi);
            icerik.add(Box.createVerticalStrut(6));
        }

        JWindow pencere = new JWindow();
        pencere.setAlwaysOnTop(true);
        pencere.setLayout(new BorderLayout());
        pencere.add(icerik, BorderLayout.CENTER);
        pencere.pack();

        Dimension boyut = pencere.getSize();
        if (boyut.width < 260) {
            pencere.setSize(260, boyut.height);
        }
        if (pencere.getWidth() > 380) {
            pencere.setSize(380, boyut.height);
        }

        // Kosleri yuvarlat. Bazi sistemlerde desteklenmiyor, o yuzden try icinde.
        try {
            pencere.setShape(new RoundRectangle2D.Double(
                    0, 0, pencere.getWidth(), pencere.getHeight(), 18, 18));
        } catch (UnsupportedOperationException hata) {
            // Kare koseli gorunur, sorun degil.
        }

        // Ekranin sag ust kosesi. getMaximumWindowBounds menu cubugunu disarida
        // biraktigi icin kart menu cubugunun altina denk gelir.
        Rectangle ekran = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int x = ekran.x + ekran.width - pencere.getWidth() - 24;
        int y = ekran.y + 24;
        pencere.setLocation(x, y);

        icerik.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icerik.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent olay) {
                kapat();
            }
        });

        pencere.setVisible(true);
        acikKart = pencere;

        // 9 saniye sonra kendiliginden kapansin.
        Timer kapatmaZamanlayicisi = new Timer(9000, olay -> kapat());
        kapatmaZamanlayicisi.setRepeats(false);
        kapatmaZamanlayicisi.start();
    }

    private static void kapat() {
        if (acikKart != null) {
            acikKart.dispose();
            acikKart = null;
        }
    }
}
