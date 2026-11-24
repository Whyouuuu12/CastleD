package id.ac.kampus.fortressguard.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Palet warna, tipografi, dan pemuat aset visual FortressGuard 2D.
 * Palet: Batu Obsidian (#1E1B18), Batu Pasir/Perkamen (#F4EBDC / #B99E7A),
 * Hijau Padang (#4E6B3E), Aksen Emas Heraldik (#D99B26) & Merah Peringatan (#B83B2E).
 */
public final class TemaFortress {
    public static final Color BATU_GELAP = new Color(30, 27, 24);
    public static final Color BATU_PANEL = new Color(43, 38, 33);
    public static final Color BATU_KARTU = new Color(56, 49, 42);
    public static final Color GARIS_BATU = new Color(115, 98, 79);

    public static final Color PERKAMEN = new Color(244, 235, 220);
    public static final Color PERKAMEN_REDUP = new Color(196, 182, 160);
    public static final Color BATU_PASIR = new Color(185, 158, 122);

    public static final Color RUMPUT_A = new Color(78, 107, 62);
    public static final Color RUMPUT_B = new Color(88, 118, 70);
    public static final Color JALAN_A = new Color(163, 134, 98);
    public static final Color JALAN_B = new Color(150, 122, 88);

    public static final Color EMAS = new Color(217, 155, 38);
    public static final Color EMAS_GELAP = new Color(168, 114, 22);
    public static final Color MERAH = new Color(184, 59, 46);
    public static final Color HIJAU_AKSI = new Color(68, 138, 78);
    public static final Color BIRU_ES = new Color(102, 184, 224);

    public static final Font FONT_JUDUL = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBJUDUL = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_KECIL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_HUD = new Font("Segoe UI", Font.BOLD, 14);

    private static BufferedImage cachedBenteng;

    private TemaFortress() {
    }

    public static BufferedImage muatAsetBenteng() {
        if (cachedBenteng != null) {
            return cachedBenteng;
        }
        String[] kandidat = {
                "resources/assets/base_fortress.png",
                "assets/base_fortress.png"
        };
        for (String path : kandidat) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    cachedBenteng = ImageIO.read(file);
                    return cachedBenteng;
                } catch (IOException ignored) {
                    // Coba kandidat berikutnya
                }
            }
        }
        return null;
    }

    public static JButton buatTombolUtama(String teks) {
        JButton btn = buatTombolDasar(teks, EMAS, BATU_GELAP);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }

    public static JButton buatTombolSekunder(String teks) {
        return buatTombolDasar(teks, BATU_KARTU, PERKAMEN);
    }

    public static JButton buatTombolBahaya(String teks) {
        return buatTombolDasar(teks, MERAH, PERKAMEN);
    }

    public static JButton buatTombolDasar(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(FONT_LABEL_BOLD);
        btn.setFocusPainted(true);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 18, 38));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS_BATU, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        return btn;
    }

    public static Border borderPanel() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS_BATU, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    public static JLabel buatLabelTerang(String teks, Font font) {
        JLabel lbl = new JLabel(teks);
        lbl.setForeground(PERKAMEN);
        lbl.setFont(font);
        return lbl;
    }

    public static Image skalaTajam(BufferedImage src, int targetW, int targetH) {
        BufferedImage dst = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = dst.createGraphics();
        g.setRenderingHint(
                java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(
                java.awt.RenderingHints.KEY_RENDERING,
                java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(
                java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, targetW, targetH, null);
        g.dispose();
        return dst;
    }
}
