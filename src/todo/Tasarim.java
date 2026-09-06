package todo;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * Uygulamanin butun renkleri ve yazi tipleri burada duruyor.
 * Tek yerde topladik ki tasarimi degistirmek istedigimizde
 * kodun her yerini tek tek aramak zorunda kalmayalim.
 */
public final class Tasarim {

    public static final Color BEYAZ = new Color(255, 255, 255);
    public static final Color SIYAH = new Color(17, 17, 17);
    public static final Color GRI = new Color(140, 140, 140);
    public static final Color ACIK_GRI = new Color(228, 228, 228);

    private static final String YAZI_TIPI = "Helvetica Neue";

    public static final Font AY_BASLIGI = harfAralikli(yeni(20), 0.18f);
    public static final Font GUN_BASLIGI = harfAralikli(yeni(13), 0.16f);
    public static final Font GUN_NUMARASI = yeni(14);
    public static final Font GUN_KISALTMASI = harfAralikli(yeni(11), 0.10f);
    public static final Font GOREV = yeni(14);
    public static final Font KUCUK = yeni(11);
    public static final Font OK = yeni(20);
    public static final Font KART_BASLIGI = harfAralikli(yeni(12), 0.16f);
    public static final Font KART_SATIRI = yeni(13);

    /** Bu sinifin nesnesi uretilmesin diye kurucu metodu gizledik. */
    private Tasarim() {
    }

    private static Font yeni(int boyut) {
        return new Font(YAZI_TIPI, Font.PLAIN, boyut);
    }

    /** Harfler arasini acar; basliklarin daha ferah gorunmesini saglar. */
    public static Font harfAralikli(Font font, float aralik) {
        Map<TextAttribute, Object> ozellikler = new HashMap<>();
        ozellikler.put(TextAttribute.TRACKING, aralik);
        return font.deriveFont(ozellikler);
    }

    /** Tamamlanan gorevlerin uzerini cizmek icin. */
    public static Font ustuCizili(Font font) {
        Map<TextAttribute, Object> ozellikler = new HashMap<>();
        ozellikler.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        return font.deriveFont(ozellikler);
    }
}
