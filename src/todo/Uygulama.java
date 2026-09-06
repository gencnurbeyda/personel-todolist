package todo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Programin baslangic noktasi. Butun parcalari birbirine baglar.
 */
public class Uygulama {

    public static void main(String[] args) {
        // Arayuz kodu her zaman Swing'in kendi is parcaciginda calismali.
        SwingUtilities.invokeLater(Uygulama::pencereyiAc);
    }

    private static void pencereyiAc() {
        Path veriKlasoru = Paths.get("veri");
        GorevDeposu depo = new GorevDeposu(veriKlasoru.resolve("gorevler.txt"));

        TakvimPaneli takvim = new TakvimPaneli(depo);
        GunPaneli gunPaneli = new GunPaneli(depo);

        // Takvimde gune tiklaninca sag panel o gunu gostersin.
        takvim.setGunDinleyici(gunPaneli::gunuGoster);
        // Gorev eklenip silinince takvimdeki noktalar yenilensin.
        gunPaneli.setDegisiklikDinleyici(takvim::tazele);

        HatirlatmaServisi servis =
                new HatirlatmaServisi(depo, veriKlasoru.resolve("bildirim.txt"));
        takvim.setDenemeEylemi(servis::gunlukKartiniZorla);
        servis.baslat();

        takvim.setPreferredSize(new Dimension(430, 560));

        JPanel govde = new JPanel(new BorderLayout());
        govde.setBackground(Tasarim.BEYAZ);
        govde.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        govde.add(takvim, BorderLayout.WEST);
        govde.add(gunPaneli, BorderLayout.CENTER);

        JFrame cerceve = new JFrame("Todo");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setContentPane(govde);
        cerceve.getContentPane().setBackground(Tasarim.BEYAZ);
        cerceve.setSize(880, 600);
        cerceve.setMinimumSize(new Dimension(760, 520));
        cerceve.setLocationRelativeTo(null);
        cerceve.setVisible(true);

        // Uygulama acilinca bugun secili gelsin.
        takvim.gunuSec(LocalDate.now());
    }
}
