package id.ac.kampus.fortressguard.ui;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;
import id.ac.kampus.fortressguard.model.ProfilPemain;
import id.ac.kampus.fortressguard.service.LayananLevelKustom;
import id.ac.kampus.fortressguard.service.LayananPermainan;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Jendela utama aplikasi FortressGuard 2D (UC-01: Pilih Profil & Mode Luring).
 * Menggunakan CardLayout untuk berpindah antara Menu Utama, Arena Permainan 2D, dan Editor Level Kustom.
 */
public class MenuUtamaView extends JFrame {
    private static final String KARTU_MENU = "MENU";
    private static final String KARTU_GAME = "GAME";
    private static final String KARTU_EDITOR = "EDITOR";

    private final LayananPermainan layananPermainan;
    private final LayananLevelKustom layananLevel;

    private final CardLayout cardLayout;
    private final JPanel containerUtama;
    private final GamePanel2D gamePanel2D;
    private final LevelEditorController editorController;

    private final JTextField inputNamaProfil;
    private final JPanel wadahKartuLevel;
    private final JPanel wadahRingkasanSkor;
    private String kodeLevelTerpilih = "LVL-01";

    public MenuUtamaView(LayananPermainan layananPermainan, LayananLevelKustom layananLevel) {
        this.layananPermainan = layananPermainan;
        this.layananLevel = layananLevel;

        setTitle("FortressGuard 2D - Tower Defense Luring");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 680));
        setPreferredSize(new Dimension(1180, 720));

        cardLayout = new CardLayout();
        containerUtama = new JPanel(cardLayout);
        containerUtama.setBackground(TemaFortress.BATU_GELAP);

        inputNamaProfil = new JTextField("Komandan_FG");
        wadahKartuLevel = new JPanel();
        wadahRingkasanSkor = new JPanel();

        JPanel panelMenu = bangunPanelMenuUtama();

        gamePanel2D = new GamePanel2D(
                layananPermainan,
                this::tampilkanMenuUtama,
                this::tampilkanEditorLevel);

        editorController = new LevelEditorController(
                layananLevel,
                layananPermainan,
                this::tampilkanMenuUtama,
                this::mulaiPermainanDenganKonfigurasi);

        containerUtama.add(panelMenu, KARTU_MENU);
        containerUtama.add(gamePanel2D, KARTU_GAME);
        containerUtama.add(editorController, KARTU_EDITOR);

        setContentPane(containerUtama);
        pack();
        setLocationRelativeTo(null);

        segarkanDataMenu();
    }

    private JPanel bangunPanelMenuUtama() {
        JPanel root = new JPanel(new BorderLayout(24, 0));
        root.setBackground(TemaFortress.BATU_GELAP);
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // Kolom Kiri: Identitas Benteng + Input Profil + Aksi Utama
        JPanel kiri = new JPanel();
        kiri.setLayout(new BoxLayout(kiri, BoxLayout.Y_AXIS));
        kiri.setBackground(TemaFortress.BATU_PANEL);
        kiri.setBorder(TemaFortress.borderPanel());
        kiri.setPreferredSize(new Dimension(470, 600));

        BufferedImage benteng = TemaFortress.muatAsetBenteng();
        if (benteng != null) {
            int maxW = 384;
            int maxH = 200;
            double rasio = Math.min((double) maxW / benteng.getWidth(), (double) maxH / benteng.getHeight());
            int drawW = Math.max(1, (int) Math.round(benteng.getWidth() * rasio));
            int drawH = Math.max(1, (int) Math.round(benteng.getHeight() * rasio));

            JPanel bingkaiBenteng = new JPanel(new BorderLayout());
            bingkaiBenteng.setBackground(TemaFortress.BATU_GELAP);
            bingkaiBenteng.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TemaFortress.GARIS_BATU, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            bingkaiBenteng.setMaximumSize(new Dimension(430, 218));
            bingkaiBenteng.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel gambar = new JLabel(new ImageIcon(TemaFortress.skalaTajam(benteng, drawW, drawH)));
            gambar.setHorizontalAlignment(JLabel.CENTER);
            bingkaiBenteng.add(gambar, BorderLayout.CENTER);

            kiri.add(bingkaiBenteng);
            kiri.add(Box.createVerticalStrut(18));
        }

        JLabel judul = TemaFortress.buatLabelTerang("FORTRESSGUARD 2D", TemaFortress.FONT_JUDUL);
        judul.setForeground(TemaFortress.EMAS);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("<html><body style='width:340px'>Pertahanan Benteng 2D Desktop Luring (Java Swing &amp; OOP SOLID). "
                + "Bangun menara, kerahkan unit melee di jalur, dan tahan setiap gelombang musuh.</body></html>");
        sub.setForeground(TemaFortress.PERKAMEN_REDUP);
        sub.setFont(TemaFortress.FONT_LABEL);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        kiri.add(judul);
        kiri.add(Box.createVerticalStrut(6));
        kiri.add(sub);
        kiri.add(Box.createVerticalStrut(22));

        JLabel lblProfil = TemaFortress.buatLabelTerang("Nama Profil Lokal (UC-01):", TemaFortress.FONT_LABEL_BOLD);
        lblProfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        kiri.add(lblProfil);
        kiri.add(Box.createVerticalStrut(6));

        inputNamaProfil.setFont(TemaFortress.FONT_HUD);
        inputNamaProfil.setBackground(TemaFortress.BATU_KARTU);
        inputNamaProfil.setForeground(TemaFortress.PERKAMEN);
        inputNamaProfil.setCaretColor(TemaFortress.EMAS);
        inputNamaProfil.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaFortress.GARIS_BATU, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        inputNamaProfil.setMaximumSize(new Dimension(430, 42));
        inputNamaProfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        kiri.add(inputNamaProfil);
        kiri.add(Box.createVerticalStrut(20));

        JButton btnMulai = TemaFortress.buatTombolUtama("Mulai Pertahanan Utama");
        btnMulai.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMulai.setMaximumSize(new Dimension(430, 46));
        btnMulai.addActionListener(e -> mulaiPermainanDariMenu());

        JButton btnEditor = TemaFortress.buatTombolSekunder("Editor Level Kustom & Papan Skor Lokal");
        btnEditor.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEditor.setMaximumSize(new Dimension(430, 42));
        btnEditor.addActionListener(e -> tampilkanEditorLevel());

        kiri.add(btnMulai);
        kiri.add(Box.createVerticalStrut(10));
        kiri.add(btnEditor);
        kiri.add(Box.createVerticalGlue());

        JLabel catatan = new JLabel("<html><body style='width:340px'>100% Luring | Penyimpanan Lokal: data/waves_config.json &amp; data/local_high_scores.json</body></html>");
        catatan.setForeground(TemaFortress.BATU_PASIR);
        catatan.setFont(TemaFortress.FONT_KECIL);
        catatan.setAlignmentX(Component.LEFT_ALIGNMENT);
        kiri.add(catatan);

        // Kolom Kanan: Pilih Level + Pratinjau Skor Tertinggi Lokal
        JPanel kanan = new JPanel(new BorderLayout(0, 16));
        kanan.setBackground(TemaFortress.BATU_GELAP);

        JPanel panelLevel = new JPanel(new BorderLayout(0, 10));
        panelLevel.setBackground(TemaFortress.BATU_PANEL);
        panelLevel.setBorder(TemaFortress.borderPanel());
        panelLevel.add(TemaFortress.buatLabelTerang("Pilih Skenario Level", TemaFortress.FONT_SUBJUDUL), BorderLayout.NORTH);

        wadahKartuLevel.setLayout(new GridLayout(0, 1, 0, 8));
        wadahKartuLevel.setBackground(TemaFortress.BATU_PANEL);
        panelLevel.add(wadahKartuLevel, BorderLayout.CENTER);

        JPanel panelSkor = new JPanel(new BorderLayout(0, 10));
        panelSkor.setBackground(TemaFortress.BATU_PANEL);
        panelSkor.setBorder(TemaFortress.borderPanel());
        panelSkor.add(TemaFortress.buatLabelTerang("Rekor Skor Lokal (Top 5)", TemaFortress.FONT_SUBJUDUL),
                BorderLayout.NORTH);

        wadahRingkasanSkor.setLayout(new BoxLayout(wadahRingkasanSkor, BoxLayout.Y_AXIS));
        wadahRingkasanSkor.setBackground(TemaFortress.BATU_PANEL);
        panelSkor.add(wadahRingkasanSkor, BorderLayout.CENTER);

        kanan.add(panelLevel, BorderLayout.NORTH);
        kanan.add(panelSkor, BorderLayout.CENTER);

        root.add(kiri, BorderLayout.WEST);
        root.add(kanan, BorderLayout.CENTER);
        return root;
    }

    private void segarkanDataMenu() {
        // Muat daftar level dari LayananLevelKustom
        wadahKartuLevel.removeAll();
        ButtonGroup grup = new ButtonGroup();
        List<KonfigurasiLevel> daftarLevel = layananLevel.ambilSemuaLevel();
        for (KonfigurasiLevel k : daftarLevel) {
            String teks = String.format(
                    "<html><b>%s</b>  |  Koin Awal: %d  |  Markas: %d HP  |  %d Gelombang (%d musuh/gel)</html>",
                    k.getKodeLevel(), k.getKoinAwal(), k.getNyawaMarkasAwal(),
                    k.getJumlahGelombang(), k.getMusuhPerGelombang());
            JRadioButton radio = new JRadioButton(teks);
            radio.setBackground(TemaFortress.BATU_KARTU);
            radio.setForeground(TemaFortress.PERKAMEN);
            radio.setFont(TemaFortress.FONT_LABEL);
            radio.setOpaque(true);
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            radio.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TemaFortress.GARIS_BATU, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            radio.setSelected(k.getKodeLevel().equals(kodeLevelTerpilih));
            radio.addActionListener(e -> kodeLevelTerpilih = k.getKodeLevel());
            grup.add(radio);
            wadahKartuLevel.add(radio);
        }

        // Muat pratinjau Top 5 skor lokal (dengan empty state yang jelas)
        wadahRingkasanSkor.removeAll();
        List<String> skor = layananPermainan.ambilDaftarSkorLokal();
        if (skor.isEmpty()) {
            JLabel kosong = new JLabel("<html><body style='width:440px'>Belum ada skor tersimpan di komputer ini. Mainkan satu sesi untuk mencatat rekor.</body></html>");
            kosong.setForeground(TemaFortress.PERKAMEN_REDUP);
            kosong.setFont(TemaFortress.FONT_LABEL);
            wadahRingkasanSkor.add(kosong);
        } else {
            int batas = Math.min(5, skor.size());
            for (int i = 0; i < batas; i++) {
                String[] bagian = skor.get(i).split("\\|");
                String profil = bagian.length > 0 ? bagian[0] : "-";
                String nilai = bagian.length > 1 ? bagian[1] : "0";
                String waktu = bagian.length > 2 ? bagian[2] : "";

                JPanel baris = new JPanel(new BorderLayout());
                baris.setBackground(TemaFortress.BATU_KARTU);
                baris.setMaximumSize(new Dimension(600, 34));
                baris.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(TemaFortress.GARIS_BATU, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));

                JLabel kiriLbl = TemaFortress.buatLabelTerang("#" + (i + 1) + "  " + profil, TemaFortress.FONT_LABEL_BOLD);
                JLabel kananLbl = TemaFortress.buatLabelTerang(nilai + " poin   (" + waktu + ")", TemaFortress.FONT_LABEL);
                kananLbl.setForeground(TemaFortress.EMAS);

                baris.add(kiriLbl, BorderLayout.WEST);
                baris.add(kananLbl, BorderLayout.EAST);
                wadahRingkasanSkor.add(baris);
                wadahRingkasanSkor.add(Box.createVerticalStrut(6));
            }
        }

        wadahKartuLevel.revalidate();
        wadahKartuLevel.repaint();
        wadahRingkasanSkor.revalidate();
        wadahRingkasanSkor.repaint();
    }

    private ProfilPemain bacaProfilValid() {
        String nama = inputNamaProfil.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama profil tidak boleh kosong.",
                    "Validasi Profil",
                    JOptionPane.WARNING_MESSAGE);
            inputNamaProfil.requestFocusInWindow();
            return null;
        }
        return new ProfilPemain(nama);
    }

    private void mulaiPermainanDariMenu() {
        ProfilPemain profil = bacaProfilValid();
        if (profil == null) {
            return;
        }
        KonfigurasiLevel konfig = layananLevel.muatKonfigurasiDefault(kodeLevelTerpilih);
        mulaiPermainanDenganKonfigurasi(konfig);
    }

    private void mulaiPermainanDenganKonfigurasi(KonfigurasiLevel konfig) {
        ProfilPemain profil = bacaProfilValid();
        if (profil == null) {
            profil = new ProfilPemain("Komandan_FG");
        }
        gamePanel2D.mulaiSesiBaru(profil, konfig);
        cardLayout.show(containerUtama, KARTU_GAME);
    }

    private void tampilkanMenuUtama() {
        segarkanDataMenu();
        cardLayout.show(containerUtama, KARTU_MENU);
    }

    private void tampilkanEditorLevel() {
        editorController.segarkanData();
        cardLayout.show(containerUtama, KARTU_EDITOR);
    }
}
