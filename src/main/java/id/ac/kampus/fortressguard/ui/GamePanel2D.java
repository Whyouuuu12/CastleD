package id.ac.kampus.fortressguard.ui;

import id.ac.kampus.fortressguard.model.Gelombang;
import id.ac.kampus.fortressguard.model.KonfigurasiLevel;
import id.ac.kampus.fortressguard.model.KsatriaPedang;
import id.ac.kampus.fortressguard.model.Menara;
import id.ac.kampus.fortressguard.model.MenaraEs;
import id.ac.kampus.fortressguard.model.MenaraMeriam;
import id.ac.kampus.fortressguard.model.MenaraPemanah;
import id.ac.kampus.fortressguard.model.Musuh;
import id.ac.kampus.fortressguard.model.MusuhBiasa;
import id.ac.kampus.fortressguard.model.MusuhBos;
import id.ac.kampus.fortressguard.model.PenjagaPerisai;
import id.ac.kampus.fortressguard.model.PetaPermainan;
import id.ac.kampus.fortressguard.model.Petak;
import id.ac.kampus.fortressguard.model.ProfilPemain;
import id.ac.kampus.fortressguard.model.Proyektil;
import id.ac.kampus.fortressguard.model.SesiPermainan;
import id.ac.kampus.fortressguard.model.UnitMelee;
import id.ac.kampus.fortressguard.service.LayananPermainan;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Panel utama permainan Tower Defense 2D (UC-02, UC-03, UC-04, UC-05).
 * Merender peta grid 10x5, jalur musuh, asset benteng (base_fortress.png),
 * menara, unit melee, musuh, proyektil, serta efek tempur pada 60 FPS.
 */
public class GamePanel2D extends JPanel {
    private enum ModePenempatan {
        TIDAK_ADA,
        MENARA_PEMANAH,
        MENARA_MERIAM,
        MENARA_ES,
        KSATRIA_PEDANG,
        PENJAGA_PERISAI
    }

    private static class EfekLedakan {
        double x;
        double y;
        double radiusMaks;
        double usia;
        final double durasi = 0.35;
        final Color warna;

        EfekLedakan(double x, double y, double radiusMaks, Color warna) {
            this.x = x;
            this.y = y;
            this.radiusMaks = radiusMaks;
            this.warna = warna;
        }
    }

    private static class TeksMelayang {
        double x;
        double y;
        final String teks;
        final Color warna;
        double usia;
        final double durasi = 0.95;

        TeksMelayang(double x, double y, String teks, Color warna) {
            this.x = x;
            this.y = y;
            this.teks = teks;
            this.warna = warna;
        }
    }

    private final LayananPermainan layananPermainan;
    private final Runnable onKembaliKeMenu;
    private final Runnable onBukaPapanSkor;

    private SesiPermainan sesi;
    private ModePenempatan modePenempatan = ModePenempatan.TIDAK_ADA;
    private Menara menaraTerpilih;
    private UnitMelee unitMeleeTerpilih;

    private final List<Musuh> antreanSpawn = new ArrayList<>();
    private final List<Musuh> musuhAktif = new ArrayList<>();
    private final List<Proyektil> proyektilAktif = new ArrayList<>();
    private final List<EfekLedakan> efekLedakan = new ArrayList<>();
    private final List<TeksMelayang> teksMelayang = new ArrayList<>();

    private boolean gelombangBerjalan = false;
    private boolean jeda = false;
    private boolean sesiBerakhir = false;
    private boolean menang = false;
    private boolean skorSudahDisimpan = false;
    private int pengaliKecepatanSimulasi = 1;
    private double penghitungSpawn = 0.0;
    private double waktuAnimasi = 0.0;
    private double sisaKilatMarkas = 0.0;

    private int hoverGridX = -1;
    private int hoverGridY = -1;

    private final BufferedImage gambarBenteng;
    private final Timer gameLoopTimer;

    // Komponen HUD
    private final JLabel lblProfilLevel;
    private final JLabel lblNyawa;
    private final JLabel lblKoin;
    private final JLabel lblGelombang;
    private final JLabel lblSkor;
    private final JLabel lblEventCepat;
    private final JButton btnMulaiGelombang;
    private final JButton btnJeda;
    private final JButton btnKecepatan;

    // Komponen Sidebar Kanan
    private final JButton btnPilihPemanah;
    private final JButton btnPilihMeriam;
    private final JButton btnPilihEs;
    private final JButton btnPilihKsatria;
    private final JButton btnPilihPerisai;

    private final JLabel lblInspektorJudul;
    private final JLabel lblInspektorDetail;
    private final JButton btnUpgradeUnit;
    private final JButton btnJualMenara;
    private final JButton btnMainLagi;
    private final JButton btnLihatSkor;
    private final JLabel lblPesanStatus;

    private final ArenaKanvas arenaKanvas;

    public GamePanel2D(LayananPermainan layananPermainan, Runnable onKembaliKeMenu, Runnable onBukaPapanSkor) {
        this.layananPermainan = layananPermainan;
        this.onKembaliKeMenu = onKembaliKeMenu;
        this.onBukaPapanSkor = onBukaPapanSkor;
        this.gambarBenteng = TemaFortress.muatAsetBenteng();

        setLayout(new BorderLayout(0, 0));
        setBackground(TemaFortress.BATU_GELAP);

        // 1. TOP HUD BAR
        JPanel hudBar = new JPanel(new BorderLayout(12, 0));
        hudBar.setBackground(TemaFortress.BATU_PANEL);
        hudBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, TemaFortress.GARIS_BATU),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        JPanel hudKiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 2));
        hudKiri.setBackground(TemaFortress.BATU_PANEL);

        lblProfilLevel = TemaFortress.buatLabelTerang("Profil: -", TemaFortress.FONT_HUD);
        lblNyawa = TemaFortress.buatLabelTerang("Markas: 20/20", TemaFortress.FONT_HUD);
        lblNyawa.setForeground(new Color(235, 118, 102));
        lblKoin = TemaFortress.buatLabelTerang("Koin: 350", TemaFortress.FONT_HUD);
        lblKoin.setForeground(TemaFortress.EMAS);
        lblGelombang = TemaFortress.buatLabelTerang("Gelombang: 0/3", TemaFortress.FONT_HUD);
        lblSkor = TemaFortress.buatLabelTerang("Skor: 0", TemaFortress.FONT_HUD);

        lblEventCepat = new JLabel("EVENT CEPAT 1.5x");
        lblEventCepat.setOpaque(true);
        lblEventCepat.setBackground(TemaFortress.MERAH);
        lblEventCepat.setForeground(TemaFortress.PERKAMEN);
        lblEventCepat.setFont(TemaFortress.FONT_KECIL);
        lblEventCepat.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        lblEventCepat.setVisible(false);

        hudKiri.add(lblProfilLevel);
        hudKiri.add(lblNyawa);
        hudKiri.add(lblKoin);
        hudKiri.add(lblGelombang);
        hudKiri.add(lblSkor);
        hudKiri.add(lblEventCepat);

        JPanel hudKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hudKanan.setBackground(TemaFortress.BATU_PANEL);

        btnMulaiGelombang = TemaFortress.buatTombolUtama("Mulai Gelombang");
        btnMulaiGelombang.addActionListener(e -> mulaiGelombangBerikutnya());

        btnJeda = TemaFortress.buatTombolSekunder("Jeda");
        btnJeda.addActionListener(e -> toggleJeda());

        btnKecepatan = TemaFortress.buatTombolSekunder("1x");
        btnKecepatan.setToolTipText("Ubah kecepatan simulasi 1x / 2x");
        btnKecepatan.addActionListener(e -> toggleKecepatan());

        JButton btnMenu = TemaFortress.buatTombolSekunder("Menu");
        btnMenu.addActionListener(e -> {
            hentikanLoop();
            this.onKembaliKeMenu.run();
        });

        hudKanan.add(btnMulaiGelombang);
        hudKanan.add(btnJeda);
        hudKanan.add(btnKecepatan);
        hudKanan.add(btnMenu);

        hudBar.add(hudKiri, BorderLayout.WEST);
        hudBar.add(hudKanan, BorderLayout.EAST);
        add(hudBar, BorderLayout.NORTH);

        // 2. CENTER ARENA KANVAS
        arenaKanvas = new ArenaKanvas();
        add(arenaKanvas, BorderLayout.CENTER);

        // 3. RIGHT SIDEBAR KOMANDO
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(TemaFortress.BATU_PANEL);
        sidebar.setPreferredSize(new Dimension(280, 600));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, TemaFortress.GARIS_BATU),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel judulBangun = TemaFortress.buatLabelTerang("Bangun & Kerahkan (UC-03)", TemaFortress.FONT_SUBJUDUL);
        judulBangun.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(judulBangun);
        sidebar.add(Box.createVerticalStrut(8));

        btnPilihPemanah = buatTombolUnit("[1] Menara Pemanah", "50 Koin | Lahan | Cepat", ModePenempatan.MENARA_PEMANAH);
        btnPilihMeriam = buatTombolUnit("[2] Menara Meriam", "80 Koin | Lahan | Ledakan Area", ModePenempatan.MENARA_MERIAM);
        btnPilihEs = buatTombolUnit("[3] Menara Es", "70 Koin | Lahan | Lambat 50%", ModePenempatan.MENARA_ES);
        btnPilihKsatria = buatTombolUnit("[4] Ksatria Pedang", "60 Koin | Jalur | Kritikal 2x", ModePenempatan.KSATRIA_PEDANG);
        btnPilihPerisai = buatTombolUnit("[5] Penjaga Perisai", "80 Koin | Jalur | Hadang 3 Musuh", ModePenempatan.PENJAGA_PERISAI);

        sidebar.add(btnPilihPemanah);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnPilihMeriam);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnPilihEs);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnPilihKsatria);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnPilihPerisai);
        sidebar.add(Box.createVerticalStrut(14));

        // Panel Inspektor (UC-04)
        JPanel panelInspektor = new JPanel();
        panelInspektor.setLayout(new BoxLayout(panelInspektor, BoxLayout.Y_AXIS));
        panelInspektor.setBackground(TemaFortress.BATU_KARTU);
        panelInspektor.setBorder(TemaFortress.borderPanel());
        panelInspektor.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelInspektor.setMaximumSize(new Dimension(260, 220));

        lblInspektorJudul = TemaFortress.buatLabelTerang("Inspektor Unit (UC-04)", TemaFortress.FONT_LABEL_BOLD);
        lblInspektorJudul.setForeground(TemaFortress.EMAS);
        lblInspektorJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblInspektorDetail = new JLabel("<html><body style='width:185px'>Klik menara atau unit melee di peta untuk melihat statistik, upgrade, atau jual.</body></html>");
        lblInspektorDetail.setForeground(TemaFortress.PERKAMEN_REDUP);
        lblInspektorDetail.setFont(TemaFortress.FONT_KECIL);
        lblInspektorDetail.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnUpgradeUnit = TemaFortress.buatTombolUtama("Upgrade");
        btnUpgradeUnit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUpgradeUnit.setMaximumSize(new Dimension(236, 36));
        btnUpgradeUnit.setVisible(false);
        btnUpgradeUnit.addActionListener(e -> prosesUpgradeTerpilih());

        btnJualMenara = TemaFortress.buatTombolBahaya("Jual Menara");
        btnJualMenara.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnJualMenara.setMaximumSize(new Dimension(236, 36));
        btnJualMenara.setVisible(false);
        btnJualMenara.addActionListener(e -> prosesJualMenaraTerpilih());

        panelInspektor.add(lblInspektorJudul);
        panelInspektor.add(Box.createVerticalStrut(6));
        panelInspektor.add(lblInspektorDetail);
        panelInspektor.add(Box.createVerticalStrut(10));
        panelInspektor.add(btnUpgradeUnit);
        panelInspektor.add(Box.createVerticalStrut(6));
        panelInspektor.add(btnJualMenara);

        sidebar.add(panelInspektor);
        sidebar.add(Box.createVerticalStrut(12));

        // Tombol akhir sesi (muncul hanya saat Victory / Game Over)
        btnMainLagi = TemaFortress.buatTombolUtama("Main Lagi");
        btnMainLagi.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMainLagi.setMaximumSize(new Dimension(256, 38));
        btnMainLagi.setVisible(false);
        btnMainLagi.addActionListener(e -> {
            if (sesi != null) {
                mulaiSesiBaru(sesi.getProfilPemain(), sesi.getKonfigurasiLevel());
            }
        });

        btnLihatSkor = TemaFortress.buatTombolSekunder("Lihat Papan Skor Lokal");
        btnLihatSkor.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLihatSkor.setMaximumSize(new Dimension(256, 38));
        btnLihatSkor.setVisible(false);
        btnLihatSkor.addActionListener(e -> {
            hentikanLoop();
            this.onBukaPapanSkor.run();
        });

        sidebar.add(btnMainLagi);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnLihatSkor);
        sidebar.add(Box.createVerticalGlue());

        JLabel shortcutHint = new JLabel("<html><body style='width:200px'>Pintasan: [1-5] Pilih Unit | [Spasi] Mulai/Jeda | [Esc] Batal</body></html>");
        shortcutHint.setForeground(TemaFortress.PERKAMEN_REDUP);
        shortcutHint.setFont(TemaFortress.FONT_KECIL);
        shortcutHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(shortcutHint);

        add(sidebar, BorderLayout.EAST);

        // 4. BOTTOM STATUS BAR
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(TemaFortress.BATU_GELAP);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, TemaFortress.GARIS_BATU),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        lblPesanStatus = TemaFortress.buatLabelTerang(
                "Pilih menara di panel kanan lalu klik petak rumput untuk membangun pertahanan.",
                TemaFortress.FONT_LABEL);
        statusBar.add(lblPesanStatus, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);

        pasangPintasanKeyboard();

        // Game Loop 60 FPS (16 ms)
        gameLoopTimer = new Timer(16, e -> {
            if (!jeda && sesi != null) {
                double dt = 0.016 * pengaliKecepatanSimulasi;
                perbaruiLogika(dt);
            }
            arenaKanvas.repaint();
        });
    }

    private JButton buatTombolUnit(String judul, String subInfo, ModePenempatan mode) {
        JButton btn = new JButton("<html><b>" + judul + "</b><br><span style='font-size:10px;color:#C4B6A0'>"
                + subInfo + "</span></html>");
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setBackground(TemaFortress.BATU_KARTU);
        btn.setForeground(TemaFortress.PERKAMEN);
        btn.setFont(TemaFortress.FONT_LABEL);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(256, 48));
        btn.setPreferredSize(new Dimension(256, 48));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaFortress.GARIS_BATU, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        btn.addActionListener(e -> pilihModePenempatan(mode));
        return btn;
    }

    private void pasangPintasanKeyboard() {
        bindKey("1", () -> pilihModePenempatan(ModePenempatan.MENARA_PEMANAH));
        bindKey("2", () -> pilihModePenempatan(ModePenempatan.MENARA_MERIAM));
        bindKey("3", () -> pilihModePenempatan(ModePenempatan.MENARA_ES));
        bindKey("4", () -> pilihModePenempatan(ModePenempatan.KSATRIA_PEDANG));
        bindKey("5", () -> pilihModePenempatan(ModePenempatan.PENJAGA_PERISAI));
        bindKey("ESCAPE", () -> {
            modePenempatan = ModePenempatan.TIDAK_ADA;
            menaraTerpilih = null;
            unitMeleeTerpilih = null;
            perbaruiTampilanTombolUnit();
            perbaruiInspektor();
            tulisStatus("Pilihan dibatalkan.");
        });
        bindKey("SPACE", () -> {
            if (sesiBerakhir) {
                return;
            }
            if (!gelombangBerjalan) {
                mulaiGelombangBerikutnya();
            } else {
                toggleJeda();
            }
        });
    }

    private void bindKey(String keyStroke, Runnable aksi) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), "aksi_" + keyStroke);
        getActionMap().put("aksi_" + keyStroke, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aksi.run();
            }
        });
    }

    public void mulaiSesiBaru(ProfilPemain profil, KonfigurasiLevel konfigurasi) {
        this.sesi = new SesiPermainan(profil, konfigurasi);
        this.modePenempatan = ModePenempatan.TIDAK_ADA;
        this.menaraTerpilih = null;
        this.unitMeleeTerpilih = null;
        this.antreanSpawn.clear();
        this.musuhAktif.clear();
        this.proyektilAktif.clear();
        this.efekLedakan.clear();
        this.teksMelayang.clear();
        this.gelombangBerjalan = false;
        this.jeda = false;
        this.sesiBerakhir = false;
        this.menang = false;
        this.skorSudahDisimpan = false;
        this.pengaliKecepatanSimulasi = 1;
        this.penghitungSpawn = 0.0;
        this.sisaKilatMarkas = 0.0;

        btnJeda.setText("Jeda");
        btnKecepatan.setText("1x");
        btnMulaiGelombang.setEnabled(true);
        btnMainLagi.setVisible(false);
        btnLihatSkor.setVisible(false);
        lblEventCepat.setVisible(false);

        perbaruiHud();
        perbaruiTampilanTombolUnit();
        perbaruiInspektor();
        tulisStatus("Sesi dimulai (" + konfigurasi.getKodeLevel()
                + "). Bangun menara di rumput atau unit melee di jalur, lalu klik Mulai Gelombang.");

        if (!gameLoopTimer.isRunning()) {
            gameLoopTimer.start();
        }
        requestFocusInWindow();
    }

    public void hentikanLoop() {
        if (gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
    }

    private void pilihModePenempatan(ModePenempatan mode) {
        if (sesiBerakhir) {
            return;
        }
        this.modePenempatan = (this.modePenempatan == mode) ? ModePenempatan.TIDAK_ADA : mode;
        this.menaraTerpilih = null;
        this.unitMeleeTerpilih = null;
        perbaruiTampilanTombolUnit();
        perbaruiInspektor();
        if (this.modePenempatan == ModePenempatan.TIDAK_ADA) {
            tulisStatus("Mode penempatan dibatalkan.");
        } else {
            boolean jalur = (mode == ModePenempatan.KSATRIA_PEDANG || mode == ModePenempatan.PENJAGA_PERISAI);
            tulisStatus(jalur
                    ? "Klik petak JALUR (tengah) untuk menempatkan unit melee."
                    : "Klik petak RUMPUT (hijau) untuk membangun menara pertahanan.");
        }
    }

    private void perbaruiTampilanTombolUnit() {
        aturHighlightTombol(btnPilihPemanah, modePenempatan == ModePenempatan.MENARA_PEMANAH);
        aturHighlightTombol(btnPilihMeriam, modePenempatan == ModePenempatan.MENARA_MERIAM);
        aturHighlightTombol(btnPilihEs, modePenempatan == ModePenempatan.MENARA_ES);
        aturHighlightTombol(btnPilihKsatria, modePenempatan == ModePenempatan.KSATRIA_PEDANG);
        aturHighlightTombol(btnPilihPerisai, modePenempatan == ModePenempatan.PENJAGA_PERISAI);
    }

    private void aturHighlightTombol(JButton btn, boolean aktif) {
        btn.setBackground(aktif ? new Color(84, 68, 42) : TemaFortress.BATU_KARTU);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktif ? TemaFortress.EMAS : TemaFortress.GARIS_BATU, aktif ? 2 : 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    }

    private void mulaiGelombangBerikutnya() {
        if (sesi == null || sesiBerakhir || gelombangBerjalan || sesi.isSemuaGelombangSelesai()) {
            return;
        }
        int idx = sesi.getIndeksGelombangAktif();
        Gelombang gel = sesi.getDaftarGelombang().get(idx);
        KonfigurasiLevel konfig = sesi.getKonfigurasiLevel();
        int barisJalur = sesi.getPetaPermainan().getBarisJalur();

        int jumlahDasar = konfig.getMusuhPerGelombang() + idx;
        int bonusHp = idx * 20;
        for (int i = 0; i < jumlahDasar; i++) {
            gel.tambahMusuh(new MusuhBiasa(-0.4, barisJalur, bonusHp));
        }
        if (gel.getNomorGelombang() >= 2 || idx == sesi.getDaftarGelombang().size() - 1) {
            int jumlahBos = (idx == sesi.getDaftarGelombang().size() - 1) ? 2 : 1;
            for (int b = 0; b < jumlahBos; b++) {
                gel.tambahMusuh(new MusuhBos(-0.4, barisJalur, 8 + idx * 2, bonusHp * 2));
            }
        }

        gel.picuEventKecepatan();
        antreanSpawn.clear();
        antreanSpawn.addAll(gel.getDaftarMusuh());
        gelombangBerjalan = true;
        penghitungSpawn = 0.0;
        btnMulaiGelombang.setEnabled(false);
        lblEventCepat.setVisible(gel.isEventCepatAktif());

        if (gel.isEventCepatAktif()) {
            tulisStatus("Gelombang " + gel.getNomorGelombang()
                    + " dimulai! EVENT CEPAT 1.5x AKTIF: Musuh bergerak jauh lebih cepat!");
        } else {
            tulisStatus("Gelombang " + gel.getNomorGelombang() + " dimulai (" + antreanSpawn.size() + " musuh).");
        }
        perbaruiHud();
    }

    private void toggleJeda() {
        if (sesiBerakhir) {
            return;
        }
        jeda = !jeda;
        btnJeda.setText(jeda ? "Lanjut" : "Jeda");
        tulisStatus(jeda ? "Permainan dijeda." : "Permainan dilanjutkan.");
    }

    private void toggleKecepatan() {
        pengaliKecepatanSimulasi = (pengaliKecepatanSimulasi == 1) ? 2 : 1;
        btnKecepatan.setText(pengaliKecepatanSimulasi + "x");
    }

    private void perbaruiLogika(double dt) {
        waktuAnimasi += dt;
        if (sisaKilatMarkas > 0) {
            sisaKilatMarkas = Math.max(0.0, sisaKilatMarkas - dt);
        }

        perbaruiEfekVisual(dt);

        if (sesiBerakhir) {
            return;
        }

        // 1. Spawn musuh bertahap
        if (gelombangBerjalan && !antreanSpawn.isEmpty()) {
            penghitungSpawn -= dt;
            if (penghitungSpawn <= 0.0) {
                musuhAktif.add(antreanSpawn.remove(0));
                penghitungSpawn = sesi.getKonfigurasiLevel().getJedaSpawnDetik();
            }
        }

        // 2. Hadang & Pertarungan Unit Melee
        for (Musuh m : musuhAktif) {
            m.aturStatusDihadang(false);
            m.perbaruiStatusEfek(dt);
        }

        for (UnitMelee u : sesi.getDaftarUnitMelee()) {
            u.kurangiCooldown(dt);
            if (!u.isHidup()) {
                continue;
            }
            int jumlahTerhadang = 0;
            Musuh targetBertarung = null;
            for (Musuh m : musuhAktif) {
                if (!m.isHidup() || m.isSudahMencapaiMarkas()) {
                    continue;
                }
                double jarak = Math.abs(m.getPosisiX() - u.getPosisiX());
                if (jarak <= 0.48 && jumlahTerhadang < u.getKapasitasHadangEfektif()) {
                    m.aturStatusDihadang(true);
                    jumlahTerhadang++;
                    if (targetBertarung == null) {
                        targetBertarung = m;
                    }
                    if (m.siapSerangBalik()) {
                        m.serangBalik(u);
                    }
                }
            }
            if (targetBertarung != null && u.siapBertarung()) {
                int hpSebelum = targetBertarung.getHp();
                u.bertarung(targetBertarung);
                int dmg = hpSebelum - targetBertarung.getHp();
                if (u instanceof KsatriaPedang && ((KsatriaPedang) u).isSeranganTerakhirKritikal()) {
                    teksMelayang.add(new TeksMelayang(targetBertarung.getPosisiX(), targetBertarung.getPosisiY() - 0.35,
                            "KRIT -" + dmg, TemaFortress.EMAS));
                }
            }
        }
        layananPermainan.bersihkanUnitGugurDariPeta(sesi);
        if (unitMeleeTerpilih != null && !unitMeleeTerpilih.isHidup()) {
            unitMeleeTerpilih = null;
            perbaruiInspektor();
        }

        // 3. Pergerakan Musuh & Deteksi Tembus Markas
        PetaPermainan peta = sesi.getPetaPermainan();
        for (Musuh m : musuhAktif) {
            if (!m.isHidup() || m.isSudahMencapaiMarkas()) {
                continue;
            }
            m.bergerakMaju(dt);
            if (m.getPosisiX() >= peta.getLebar() - 0.15) {
                m.tandaiMencapaiMarkas();
                sesi.kurangiNyawaMarkas(m.getDayaSerangMarkas());
                sisaKilatMarkas = 0.35;
                teksMelayang.add(new TeksMelayang(peta.getLebar() - 0.2, m.getPosisiY() - 0.4,
                        "MARKAS -" + m.getDayaSerangMarkas(), TemaFortress.MERAH));
            }
        }

        // 4. Menara Deteksi Musuh & Luncurkan Proyektil
        for (Menara menara : sesi.getDaftarMenara()) {
            menara.kurangiCooldown(dt);
            if (!menara.siapMenembak()) {
                continue;
            }
            Musuh target = cariMusuhTerdepanDalamJangkauan(menara);
            if (target != null) {
                double spdProyektil = (menara instanceof MenaraMeriam) ? 6.5 : 10.5;
                proyektilAktif.add(new Proyektil(
                        menara.getPosisiX(), menara.getPosisiY(),
                        spdProyektil, menara.getDayaRusak(), target, menara));
                menara.resetCooldown();
            }
        }

        // 5. Pembaruan Proyektil
        Iterator<Proyektil> itProy = proyektilAktif.iterator();
        while (itProy.hasNext()) {
            Proyektil p = itProy.next();
            boolean kena = p.perbaruiPosisi(dt, musuhAktif);
            if (kena) {
                if (p.getSumberMenara() instanceof MenaraMeriam) {
                    efekLedakan.add(new EfekLedakan(p.getKoordinatX(), p.getKoordinatY(), 1.8, new Color(235, 130, 48)));
                } else if (p.getSumberMenara() instanceof MenaraEs) {
                    efekLedakan.add(new EfekLedakan(p.getKoordinatX(), p.getKoordinatY(), 0.7, TemaFortress.BIRU_ES));
                }
            }
            if (!p.isAktif()) {
                itProy.remove();
            }
        }

        // 6. Kumpulkan Hadiah Koin & Bersihkan Musuh Mati/Lolos
        Iterator<Musuh> itMusuh = musuhAktif.iterator();
        while (itMusuh.hasNext()) {
            Musuh m = itMusuh.next();
            if (!m.isHidup()) {
                if (!m.isHadiahSudahDiberikan()) {
                    m.tandaiHadiahDiberikan();
                    int koinHadiah = m.getHadiahKoin();
                    int skorHadiah = koinHadiah * 5;
                    sesi.tambahKoinDanSkor(koinHadiah, skorHadiah);
                    sesi.catatMusuhDikalahkan();
                    teksMelayang.add(new TeksMelayang(m.getPosisiX(), m.getPosisiY() - 0.3,
                            "+" + koinHadiah + " Koin", TemaFortress.EMAS));
                }
                itMusuh.remove();
            } else if (m.isSudahMencapaiMarkas()) {
                itMusuh.remove();
            }
        }

        // 7. Cek Kalah / Selesai Gelombang / Menang
        if (sesi.isKalah()) {
            akhiriSesi(false);
            return;
        }

        if (gelombangBerjalan && antreanSpawn.isEmpty() && musuhAktif.isEmpty()) {
            gelombangBerjalan = false;
            lblEventCepat.setVisible(false);
            sesi.majuKeGelombangBerikutnya();
            if (sesi.isSemuaGelombangSelesai()) {
                akhiriSesi(true);
                return;
            }
            btnMulaiGelombang.setEnabled(true);
            tulisStatus("Gelombang berhasil ditahan! Perkuat pertahanan lalu klik Mulai Gelombang berikutnya.");
        }

        perbaruiHud();
    }

    private void perbaruiEfekVisual(double dt) {
        Iterator<EfekLedakan> itLedak = efekLedakan.iterator();
        while (itLedak.hasNext()) {
            EfekLedakan e = itLedak.next();
            e.usia += dt;
            if (e.usia >= e.durasi) {
                itLedak.remove();
            }
        }
        Iterator<TeksMelayang> itTeks = teksMelayang.iterator();
        while (itTeks.hasNext()) {
            TeksMelayang t = itTeks.next();
            t.usia += dt;
            t.y -= dt * 0.65;
            if (t.usia >= t.durasi) {
                itTeks.remove();
            }
        }
    }

    private Musuh cariMusuhTerdepanDalamJangkauan(Menara menara) {
        Musuh terbaik = null;
        double xTerjauh = -Double.MAX_VALUE;
        for (Musuh m : musuhAktif) {
            if (!m.isHidup() || m.isSudahMencapaiMarkas()) {
                continue;
            }
            double jarak = Math.hypot(m.getPosisiX() - menara.getPosisiX(), m.getPosisiY() - menara.getPosisiY());
            if (jarak <= menara.getJangkauan() && m.getPosisiX() > xTerjauh) {
                xTerjauh = m.getPosisiX();
                terbaik = m;
            }
        }
        return terbaik;
    }

    private void akhiriSesi(boolean statusMenang) {
        this.sesiBerakhir = true;
        this.menang = statusMenang;
        this.gelombangBerjalan = false;
        btnMulaiGelombang.setEnabled(false);
        btnMainLagi.setVisible(true);
        btnLihatSkor.setVisible(true);

        if (!skorSudahDisimpan) {
            layananPermainan.akhiriSesiDanSimpanSkor(sesi);
            skorSudahDisimpan = true;
        }
        perbaruiHud();
        tulisStatus(statusMenang
                ? "KEMENANGAN! Benteng berhasil dipertahankan. Skor akhir " + sesi.hitungSkorAkhir() + " tersimpan di lokal."
                : "MARKAS RUNTUH! Skor akhir " + sesi.hitungSkorAkhir() + " tersimpan di papan peringkat lokal.");
    }

    private void klikPetak(int gx, int gy) {
        if (sesi == null || sesiBerakhir) {
            return;
        }
        PetaPermainan peta = sesi.getPetaPermainan();
        if (gx < 0 || gx >= peta.getLebar() || gy < 0 || gy >= peta.getTinggi()) {
            return;
        }

        if (modePenempatan != ModePenempatan.TIDAK_ADA) {
            boolean berhasil = false;
            switch (modePenempatan) {
                case MENARA_PEMANAH:
                    berhasil = layananPermainan.bangunMenaraPadaPeta(sesi, new MenaraPemanah(gx, gy));
                    break;
                case MENARA_MERIAM:
                    berhasil = layananPermainan.bangunMenaraPadaPeta(sesi, new MenaraMeriam(gx, gy));
                    break;
                case MENARA_ES:
                    berhasil = layananPermainan.bangunMenaraPadaPeta(sesi, new MenaraEs(gx, gy));
                    break;
                case KSATRIA_PEDANG:
                    berhasil = layananPermainan.tempatkanUnitMeleePadaJalur(sesi, new KsatriaPedang(gx, gy));
                    break;
                case PENJAGA_PERISAI:
                    berhasil = layananPermainan.tempatkanUnitMeleePadaJalur(sesi, new PenjagaPerisai(gx, gy));
                    break;
                default:
                    break;
            }
            if (berhasil) {
                tulisStatus("Unit berhasil ditempatkan di petak (" + gx + ", " + gy + ").");
                perbaruiHud();
            } else {
                Petak p = peta.cariPetak(gx, gy);
                if (p.isTerisiUnit()) {
                    tulisStatus("Petak (" + gx + ", " + gy + ") sudah terisi unit.");
                } else {
                    tulisStatus("Tidak bisa menempatkan unit di petak ini (periksa jenis petak atau koin).");
                }
            }
            return;
        }

        // Mode seleksi unit untuk Upgrade / Jual (UC-04)
        menaraTerpilih = null;
        unitMeleeTerpilih = null;
        for (Menara m : sesi.getDaftarMenara()) {
            if (m.getPosisiX() == gx && m.getPosisiY() == gy) {
                menaraTerpilih = m;
                break;
            }
        }
        if (menaraTerpilih == null) {
            for (UnitMelee u : sesi.getDaftarUnitMelee()) {
                if (u.getPosisiX() == gx && u.getPosisiY() == gy) {
                    unitMeleeTerpilih = u;
                    break;
                }
            }
        }
        perbaruiInspektor();
    }

    private void prosesUpgradeTerpilih() {
        if (sesi == null || sesiBerakhir) {
            return;
        }
        if (menaraTerpilih != null) {
            int biaya = menaraTerpilih.getBiayaUpgrade();
            if (layananPermainan.upgradeMenara(sesi, menaraTerpilih, biaya)) {
                tulisStatus(menaraTerpilih.getNama() + " naik ke Level " + menaraTerpilih.getLevel() + "!");
                perbaruiHud();
                perbaruiInspektor();
            } else {
                tulisStatus("Koin tidak cukup untuk upgrade (butuh " + biaya + " Koin).");
            }
        } else if (unitMeleeTerpilih != null) {
            int biaya = unitMeleeTerpilih.getBiayaUpgrade();
            if (layananPermainan.upgradeUnitMelee(sesi, unitMeleeTerpilih, biaya)) {
                tulisStatus(unitMeleeTerpilih.getNama() + " naik ke Level " + unitMeleeTerpilih.getLevel() + "!");
                perbaruiHud();
                perbaruiInspektor();
            } else {
                tulisStatus("Koin tidak cukup untuk upgrade (butuh " + biaya + " Koin).");
            }
        }
    }

    private void prosesJualMenaraTerpilih() {
        if (sesi == null || sesiBerakhir || menaraTerpilih == null) {
            return;
        }
        String nama = menaraTerpilih.getNama();
        int hargaJual = menaraTerpilih.hitungHargaJual();
        if (layananPermainan.jualMenara(sesi, menaraTerpilih)) {
            menaraTerpilih = null;
            tulisStatus(nama + " dijual (+" + hargaJual + " Koin).");
            perbaruiHud();
            perbaruiInspektor();
        }
    }

    private void perbaruiInspektor() {
        if (menaraTerpilih != null) {
            lblInspektorJudul.setText(menaraTerpilih.getNama() + " (Lv." + menaraTerpilih.getLevel() + ")");
            lblInspektorDetail.setText(String.format(
                    "<html>Daya Rusak: <b>%d</b><br>Jangkauan: <b>%.1f petak</b><br>Strategi: %s</html>",
                    menaraTerpilih.getDayaRusak(),
                    menaraTerpilih.getJangkauan(),
                    menaraTerpilih.getStrategiSerangan().getDeskripsiStrategi()));
            btnUpgradeUnit.setText("Upgrade (" + menaraTerpilih.getBiayaUpgrade() + " Koin)");
            btnUpgradeUnit.setVisible(true);
            btnJualMenara.setText("Jual Menara (+" + menaraTerpilih.hitungHargaJual() + " Koin)");
            btnJualMenara.setVisible(true);
        } else if (unitMeleeTerpilih != null) {
            lblInspektorJudul.setText(unitMeleeTerpilih.getNama() + " (Lv." + unitMeleeTerpilih.getLevel() + ")");
            String ekstra = (unitMeleeTerpilih instanceof PenjagaPerisai)
                    ? "Armor: " + ((PenjagaPerisai) unitMeleeTerpilih).getArmorPerisai()
                    + " | Hadang: " + ((PenjagaPerisai) unitMeleeTerpilih).getKapasitasHadang()
                    : "Peluang Kritikal: 30% (2x)";
            lblInspektorDetail.setText(String.format(
                    "<html>HP: <b>%d / %d</b><br>Daya Melee: <b>%d</b><br>%s</html>",
                    unitMeleeTerpilih.getHp(),
                    unitMeleeTerpilih.getHpMaks(),
                    unitMeleeTerpilih.getDayaRusakMelee(),
                    ekstra));
            btnUpgradeUnit.setText("Upgrade (" + unitMeleeTerpilih.getBiayaUpgrade() + " Koin)");
            btnUpgradeUnit.setVisible(true);
            btnJualMenara.setVisible(false);
        } else {
            lblInspektorJudul.setText("Inspektor Unit (UC-04)");
            lblInspektorDetail.setText("<html><body style='width:185px'>Klik menara atau unit melee di peta untuk melihat statistik, upgrade, atau jual.</body></html>");
            btnUpgradeUnit.setVisible(false);
            btnJualMenara.setVisible(false);
        }
    }

    private void perbaruiHud() {
        if (sesi == null) {
            return;
        }
        lblProfilLevel.setText(sesi.getProfilPemain().getNamaProfil()
                + " [" + sesi.getKonfigurasiLevel().getKodeLevel() + "]");
        lblNyawa.setText("Markas: " + sesi.getNyawaMarkas() + "/" + sesi.getNyawaMarkasMaks());
        lblKoin.setText("Koin: " + sesi.getKoin());
        int nomorGelTampil = Math.min(sesi.getDaftarGelombang().size(),
                sesi.getIndeksGelombangAktif() + (gelombangBerjalan ? 1 : 0));
        lblGelombang.setText("Gelombang: " + nomorGelTampil + "/" + sesi.getDaftarGelombang().size());
        lblSkor.setText("Skor: " + sesi.getSkor());
    }

    private void tulisStatus(String pesan) {
        lblPesanStatus.setText(pesan);
    }

    /**
     * Kanvas 2D khusus penggambaran peta, benteng pixel-art, menara, unit, musuh, dan proyektil.
     */
    private class ArenaKanvas extends JPanel {
        private int ukuranPetak = 64;
        private int offsetX = 24;
        private int offsetY = 48;

        ArenaKanvas() {
            setBackground(new Color(48, 63, 39));
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    perbaruiHover(e.getX(), e.getY());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverGridX = -1;
                    hoverGridY = -1;
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        modePenempatan = ModePenempatan.TIDAK_ADA;
                        perbaruiTampilanTombolUnit();
                        return;
                    }
                    perbaruiHover(e.getX(), e.getY());
                    if (hoverGridX >= 0 && hoverGridY >= 0) {
                        klikPetak(hoverGridX, hoverGridY);
                    }
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        private void hitungTataLetak() {
            if (sesi == null) {
                return;
            }
            PetaPermainan peta = sesi.getPetaPermainan();
            int lebarMarkas = 224;
            int lebarTersedia = Math.max(460, getWidth() - lebarMarkas - 44);
            int tinggiTersedia = Math.max(300, getHeight() - 48);
            ukuranPetak = Math.max(46, Math.min(lebarTersedia / peta.getLebar(), tinggiTersedia / peta.getTinggi()));
            int totalLebar = peta.getLebar() * ukuranPetak + lebarMarkas;
            int totalTinggi = peta.getTinggi() * ukuranPetak;
            offsetX = Math.max(18, (getWidth() - totalLebar) / 2);
            offsetY = Math.max(24, (getHeight() - totalTinggi) / 2);
        }

        private void perbaruiHover(int mx, int my) {
            if (sesi == null) {
                return;
            }
            hitungTataLetak();
            int gx = (mx - offsetX) / ukuranPetak;
            int gy = (my - offsetY) / ukuranPetak;
            PetaPermainan peta = sesi.getPetaPermainan();
            if (mx >= offsetX && my >= offsetY && gx >= 0 && gx < peta.getLebar() && gy >= 0 && gy < peta.getTinggi()) {
                hoverGridX = gx;
                hoverGridY = gy;
            } else {
                hoverGridX = -1;
                hoverGridY = -1;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sesi == null) {
                return;
            }
            hitungTataLetak();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            PetaPermainan peta = sesi.getPetaPermainan();
            gambarLatarDanGrid(g2, peta);
            gambarMarkasBenteng(g2, peta);
            gambarHoverDanJangkauan(g2, peta);
            gambarMenara(g2);
            gambarUnitMelee(g2);
            gambarMusuh(g2);
            gambarProyektil(g2);
            gambarEfekLedakan(g2);
            gambarTeksMelayang(g2);

            if (sesiBerakhir) {
                gambarOverlayAkhir(g2);
            }
            g2.dispose();
        }

        private void gambarLatarDanGrid(Graphics2D g2, PetaPermainan peta) {
            int gridW = peta.getLebar() * ukuranPetak;
            int gridH = peta.getTinggi() * ukuranPetak;

            // Bingkai batu luar dengan efek ketebalan 2.5D di bawah
            g2.setColor(new Color(18, 16, 14, 150));
            g2.fillRoundRect(offsetX - 6, offsetY + 2, gridW + 234, gridH + 18, 12, 12);
            g2.setColor(TemaFortress.BATU_PANEL);
            g2.fillRoundRect(offsetX - 8, offsetY - 8, gridW + 234, gridH + 16, 10, 10);
            g2.setColor(TemaFortress.GARIS_BATU);
            g2.drawRoundRect(offsetX - 8, offsetY - 8, gridW + 234, gridH + 16, 10, 10);

            for (Petak p : peta.getDaftarPetak()) {
                int px = offsetX + p.getKoordinatX() * ukuranPetak;
                int py = offsetY + p.getKoordinatY() * ukuranPetak;
                boolean genap = ((p.getKoordinatX() + p.getKoordinatY()) % 2 == 0);

                if (p.isJalurMusuh()) {
                    g2.setColor(genap ? TemaFortress.JALAN_A : TemaFortress.JALAN_B);
                    g2.fillRect(px, py, ukuranPetak, ukuranPetak);

                    // Tepian batu jalan
                    g2.setColor(new Color(122, 98, 70));
                    g2.fillRect(px, py, ukuranPetak, 4);
                    g2.fillRect(px, py + ukuranPetak - 4, ukuranPetak, 4);

                    // Penunjuk arah halus ke benteng
                    g2.setColor(new Color(224, 200, 165, 95));
                    int cx = px + ukuranPetak / 2;
                    int cy = py + ukuranPetak / 2;
                    g2.drawLine(cx - 8, cy - 5, cx + 2, cy);
                    g2.drawLine(cx - 8, cy + 5, cx + 2, cy);
                } else {
                    g2.setColor(genap ? TemaFortress.RUMPUT_A : TemaFortress.RUMPUT_B);
                    g2.fillRect(px, py, ukuranPetak, ukuranPetak);

                    // Rumpun rumput kecil
                    g2.setColor(new Color(62, 88, 49));
                    g2.fillRect(px + 10, py + 12, 3, 6);
                    g2.fillRect(px + 14, py + 14, 3, 4);
                    g2.fillRect(px + ukuranPetak - 16, py + ukuranPetak - 16, 3, 5);
                }

                g2.setColor(new Color(30, 27, 24, 55));
                g2.drawRect(px, py, ukuranPetak, ukuranPetak);
            }

            // Gerbang Spawn Musuh di sisi kiri jalur
            int spawnY = offsetY + peta.getBarisJalur() * ukuranPetak;
            g2.setColor(new Color(110, 42, 34));
            g2.fillRoundRect(offsetX - 6, spawnY + 8, 14, ukuranPetak - 16, 4, 4);
            g2.setColor(TemaFortress.PERKAMEN);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString("SPAWN", offsetX + 4, spawnY - 5);
        }

        private void gambarMarkasBenteng(Graphics2D g2, PetaPermainan peta) {
            int baseAreaX = offsetX + peta.getLebar() * ukuranPetak;
            int baseAreaY = offsetY;
            int baseAreaW = 220;
            int baseAreaH = peta.getTinggi() * ukuranPetak;

            // Latar teras rumput & pelataran batu 2.5D untuk benteng
            g2.setColor(new Color(69, 94, 55));
            g2.fillRect(baseAreaX, baseAreaY, baseAreaW, baseAreaH);

            // Teras batu segi delapan bernuansa 2.5D di bawah benteng
            int terasPadX = 10;
            int terasPadY = 18;
            g2.setColor(new Color(52, 46, 39));
            g2.fillRoundRect(baseAreaX + terasPadX, baseAreaY + terasPadY + 6,
                    baseAreaW - terasPadX * 2, baseAreaH - terasPadY * 2, 16, 16);
            g2.setColor(new Color(110, 96, 78));
            g2.fillRoundRect(baseAreaX + terasPadX, baseAreaY + terasPadY,
                    baseAreaW - terasPadX * 2, baseAreaH - terasPadY * 2, 16, 16);
            g2.setColor(new Color(142, 124, 100));
            g2.drawRoundRect(baseAreaX + terasPadX, baseAreaY + terasPadY,
                    baseAreaW - terasPadX * 2, baseAreaH - terasPadY * 2, 16, 16);

            // Hitung ukuran proporsional benteng 2.5D (1042x824)
            int maxW = baseAreaW - 30;
            int maxH = Math.min(baseAreaH - 72, 168);
            int castleW = maxW;
            int castleH = maxH;
            if (gambarBenteng != null) {
                double rasio = Math.min((double) maxW / gambarBenteng.getWidth(),
                        (double) maxH / gambarBenteng.getHeight());
                castleW = Math.max(1, (int) Math.round(gambarBenteng.getWidth() * rasio));
                castleH = Math.max(1, (int) Math.round(gambarBenteng.getHeight() * rasio));
            }
            int castleX = baseAreaX + (baseAreaW - castleW) / 2;
            int castleY = baseAreaY + (baseAreaH - castleH) / 2 + 10;

            // Sambungan jalan tanah menyatu mulus ke gerbang depan-kiri benteng (tidak tembus ke belakang)
            int jalurY = offsetY + peta.getBarisJalur() * ukuranPetak;
            int gateX = castleX + (int) Math.round(castleW * 0.28);
            int gateY = castleY + (int) Math.round(castleH * 0.78);
            int[] roadXs = {baseAreaX, baseAreaX + 14, gateX + 6, gateX - 12, baseAreaX + 10, baseAreaX};
            int[] roadYs = {jalurY, jalurY, gateY - 16, gateY + 12, jalurY + ukuranPetak, jalurY + ukuranPetak};
            g2.setColor(TemaFortress.JALAN_A);
            g2.fillPolygon(roadXs, roadYs, roadXs.length);
            g2.setColor(new Color(122, 98, 70));
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(baseAreaX, jalurY + 1, baseAreaX + 14, jalurY + 1);
            g2.drawLine(baseAreaX + 14, jalurY + 1, gateX + 2, gateY - 14);
            g2.drawLine(baseAreaX, jalurY + ukuranPetak - 2, baseAreaX + 10, jalurY + ukuranPetak - 2);
            g2.drawLine(baseAreaX + 10, jalurY + ukuranPetak - 2, gateX - 12, gateY + 11);
            g2.setStroke(new BasicStroke(1f));

            // Bayangan kontak isometrik (menempel tepat di dasar dinding, condong ke kanan-bawah)
            int leftBaseX = castleX + (int) Math.round(castleW * 0.08);
            int leftBaseY = castleY + (int) Math.round(castleH * 0.67);
            int frontBaseX = castleX + (int) Math.round(castleW * 0.50);
            int frontBaseY = castleY + (int) Math.round(castleH * 0.96);
            int rightBaseX = castleX + (int) Math.round(castleW * 0.94);
            int rightBaseY = castleY + (int) Math.round(castleH * 0.67);
            int backBaseX = castleX + (int) Math.round(castleW * 0.52);
            int backBaseY = castleY + (int) Math.round(castleH * 0.42);

            int[] shadowXs = {leftBaseX + 4, frontBaseX + 8, rightBaseX + 12, backBaseX + 12};
            int[] shadowYs = {leftBaseY + 3, frontBaseY + 4, rightBaseY + 4, backBaseY + 4};
            g2.setColor(new Color(22, 19, 16, 115));
            g2.fillPolygon(shadowXs, shadowYs, 4);

            if (gambarBenteng != null) {
                Object hintLama = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(gambarBenteng, castleX, castleY, castleW, castleH, null);
                if (hintLama != null) {
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hintLama);
                }
            } else {
                g2.setColor(TemaFortress.BATU_PASIR);
                g2.fillRect(castleX, castleY, castleW, castleH);
            }

            // Kilat merah saat markas diserang
            if (sisaKilatMarkas > 0) {
                g2.setColor(new Color(184, 59, 46, 105));
                g2.fillRoundRect(castleX, castleY, castleW, castleH, 12, 12);
            }

            // Label & Bar Nyawa Markas di bagian atas teras
            int barW = 164;
            int barH = 12;
            int barX = baseAreaX + (baseAreaW - barW) / 2;
            int barY = baseAreaY + 42;

            g2.setColor(TemaFortress.PERKAMEN);
            g2.setFont(TemaFortress.FONT_KECIL);
            g2.drawString("MARKAS UTAMA (" + sesi.getNyawaMarkas() + "/" + sesi.getNyawaMarkasMaks() + ")",
                    barX + 16, barY - 6);

            g2.setColor(TemaFortress.BATU_GELAP);
            g2.fillRoundRect(barX, barY, barW, barH, 5, 5);
            double rasioHp = (double) sesi.getNyawaMarkas() / Math.max(1, sesi.getNyawaMarkasMaks());
            g2.setColor(rasioHp > 0.45 ? TemaFortress.HIJAU_AKSI : TemaFortress.MERAH);
            g2.fillRoundRect(barX + 1, barY + 1, (int) ((barW - 2) * rasioHp), barH - 2, 4, 4);
            g2.setColor(TemaFortress.PERKAMEN);
            g2.drawRoundRect(barX, barY, barW, barH, 5, 5);
        }

        private void gambarHoverDanJangkauan(Graphics2D g2, PetaPermainan peta) {
            // Jangkauan menara terpilih
            if (menaraTerpilih != null) {
                gambarLingkaranJangkauan(g2, menaraTerpilih.getPosisiX(), menaraTerpilih.getPosisiY(),
                        menaraTerpilih.getJangkauan(), new Color(217, 155, 38, 170));
            }

            if (hoverGridX < 0 || hoverGridY < 0 || sesiBerakhir) {
                return;
            }
            int px = offsetX + hoverGridX * ukuranPetak;
            int py = offsetY + hoverGridY * ukuranPetak;
            Petak petak = peta.cariPetak(hoverGridX, hoverGridY);

            if (modePenempatan != ModePenempatan.TIDAK_ADA) {
                boolean butuhJalur = (modePenempatan == ModePenempatan.KSATRIA_PEDANG
                        || modePenempatan == ModePenempatan.PENJAGA_PERISAI);
                int biaya = biayaMode(modePenempatan);
                boolean valid = (butuhJalur ? petak.dapatDitempatkanMelee() : petak.dapatDibangunMenara())
                        && sesi.getKoin() >= biaya;

                g2.setColor(valid ? new Color(68, 180, 92, 105) : new Color(184, 59, 46, 115));
                g2.fillRect(px + 1, py + 1, ukuranPetak - 2, ukuranPetak - 2);
                g2.setColor(valid ? new Color(140, 235, 155) : TemaFortress.MERAH);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(px + 1, py + 1, ukuranPetak - 2, ukuranPetak - 2);

                if (!butuhJalur) {
                    gambarLingkaranJangkauan(g2, hoverGridX, hoverGridY, jangkauanMode(modePenempatan),
                            valid ? new Color(244, 235, 220, 140) : new Color(184, 59, 46, 130));
                }
            } else {
                g2.setColor(new Color(244, 235, 220, 75));
                g2.drawRect(px + 1, py + 1, ukuranPetak - 2, ukuranPetak - 2);
            }
        }

        private void gambarLingkaranJangkauan(Graphics2D g2, int gx, int gy, double radiusPetak, Color warna) {
            int cx = offsetX + gx * ukuranPetak + ukuranPetak / 2;
            int cy = offsetY + gy * ukuranPetak + ukuranPetak / 2;
            int r = (int) (radiusPetak * ukuranPetak);
            Stroke lama = g2.getStroke();
            g2.setColor(new Color(warna.getRed(), warna.getGreen(), warna.getBlue(), 34));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(warna);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6f, 5f}, 0));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setStroke(lama);
        }

        private void gambarMenara(Graphics2D g2) {
            for (Menara m : sesi.getDaftarMenara()) {
                int px = offsetX + m.getPosisiX() * ukuranPetak;
                int py = offsetY + m.getPosisiY() * ukuranPetak;
                int pad = 8;
                int w = ukuranPetak - pad * 2;
                int h = ukuranPetak - pad * 2;

                // Bayangan 2.5D & Alas batu menara
                g2.setColor(new Color(20, 18, 15, 110));
                g2.fillOval(px + pad + 2, py + pad + h - 8, w - 2, 12);
                g2.setColor(new Color(52, 46, 40));
                g2.fillRoundRect(px + pad, py + pad + 8, w, h - 4, 8, 8);
                g2.setColor(new Color(74, 66, 58));
                g2.fillRoundRect(px + pad, py + pad + 4, w, h - 4, 8, 8);
                g2.setColor(TemaFortress.BATU_GELAP);
                g2.drawRoundRect(px + pad, py + pad + 4, w, h - 4, 8, 8);

                if (m instanceof MenaraPemanah) {
                    // Menara kayu + atap emas + ikon busur
                    g2.setColor(new Color(145, 96, 52));
                    g2.fillRect(px + pad + 6, py + pad + 6, w - 12, h - 12);
                    g2.setColor(TemaFortress.EMAS);
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawArc(px + ukuranPetak / 2 - 8, py + ukuranPetak / 2 - 10, 16, 20, -60, 120);
                    g2.drawLine(px + ukuranPetak / 2 - 6, py + ukuranPetak / 2, px + ukuranPetak / 2 + 10, py + ukuranPetak / 2);
                } else if (m instanceof MenaraMeriam) {
                    // Menara besi gelap + laras meriam
                    g2.setColor(new Color(52, 50, 50));
                    g2.fillOval(px + pad + 5, py + pad + 6, w - 10, h - 10);
                    g2.setColor(new Color(220, 125, 45));
                    g2.fillRect(px + ukuranPetak / 2 - 2, py + ukuranPetak / 2 - 5, 16, 10);
                    g2.setColor(TemaFortress.BATU_PASIR);
                    g2.drawOval(px + pad + 5, py + pad + 6, w - 10, h - 10);
                } else if (m instanceof MenaraEs) {
                    // Obelisk kristal es
                    int cx = px + ukuranPetak / 2;
                    int cy = py + ukuranPetak / 2;
                    int[] xs = {cx, cx + 12, cx, cx - 12};
                    int[] ys = {cy - 16, cy, cy + 14, cy};
                    g2.setColor(TemaFortress.BIRU_ES);
                    g2.fillPolygon(xs, ys, 4);
                    g2.setColor(TemaFortress.PERKAMEN);
                    g2.drawPolygon(xs, ys, 4);
                }

                // Badge Level
                g2.setColor(TemaFortress.BATU_GELAP);
                g2.fillRoundRect(px + ukuranPetak - 22, py + 4, 18, 14, 4, 4);
                g2.setColor(TemaFortress.EMAS);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("L" + m.getLevel(), px + ukuranPetak - 19, py + 15);

                if (m == menaraTerpilih) {
                    g2.setColor(TemaFortress.EMAS);
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawRect(px + 2, py + 2, ukuranPetak - 4, ukuranPetak - 4);
                }
            }
        }

        private void gambarUnitMelee(Graphics2D g2) {
            for (UnitMelee u : sesi.getDaftarUnitMelee()) {
                if (!u.isHidup()) {
                    continue;
                }
                int px = offsetX + u.getPosisiX() * ukuranPetak;
                int py = offsetY + u.getPosisiY() * ukuranPetak;
                int cx = px + ukuranPetak / 2;
                int cy = py + ukuranPetak / 2;

                // Bayangan 2.5D di bawah unit melee
                g2.setColor(new Color(20, 18, 15, 110));
                g2.fillOval(cx - 13, cy + 9, 26, 9);

                if (u instanceof KsatriaPedang) {
                    // Ksatria baju zirah baja + pedang
                    g2.setColor(new Color(178, 190, 202));
                    g2.fillOval(cx - 12, cy - 12, 24, 24);
                    g2.setColor(TemaFortress.BATU_GELAP);
                    g2.drawOval(cx - 12, cy - 12, 24, 24);
                    g2.setColor(TemaFortress.EMAS);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(cx + 4, cy + 6, cx + 15, cy - 12);
                } else {
                    // Penjaga Perisai dengan perisai besar berlapis emas
                    g2.setColor(new Color(72, 108, 142));
                    g2.fillRoundRect(cx - 14, cy - 14, 28, 28, 8, 8);
                    g2.setColor(TemaFortress.EMAS);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(cx - 14, cy - 14, 28, 28, 8, 8);
                    g2.drawLine(cx, cy - 9, cx, cy + 9);
                    g2.drawLine(cx - 8, cy, cx + 8, cy);
                }

                // HP Bar unit melee
                gambarHpBarKecil(g2, px + 8, py + 5, ukuranPetak - 16, u.getHp(), u.getHpMaks(), new Color(82, 184, 112));

                if (u == unitMeleeTerpilih) {
                    g2.setColor(TemaFortress.EMAS);
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawRect(px + 2, py + 2, ukuranPetak - 4, ukuranPetak - 4);
                }
            }
        }

        private void gambarMusuh(Graphics2D g2) {
            for (Musuh m : musuhAktif) {
                if (!m.isHidup()) {
                    continue;
                }
                int cx = offsetX + (int) ((m.getPosisiX() + 0.5) * ukuranPetak);
                int bob = (int) (Math.sin(waktuAnimasi * 10.0 + m.getPosisiX() * 3.0) * 2.5);
                int cy = offsetY + (int) ((m.getPosisiY() + 0.5) * ukuranPetak) + bob;

                boolean bos = (m instanceof MusuhBos);
                int r = bos ? 18 : 12;

                // Bayangan 2.5D di bawah musuh
                g2.setColor(new Color(20, 18, 15, 115));
                g2.fillOval(cx - r, offsetY + (int) ((m.getPosisiY() + 0.5) * ukuranPetak) + r - 4, r * 2, 9);

                // Efek visual Event Cepat (jejak amber)
                if (m.isEventCepatAktif()) {
                    g2.setColor(new Color(235, 165, 45, 150));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(cx - r - 10, cy - 4, cx - r - 2, cy - 4);
                    g2.drawLine(cx - r - 14, cy + 3, cx - r - 2, cy + 3);
                }

                // Badan musuh
                if (bos) {
                    g2.setColor(new Color(92, 42, 42));
                    g2.fillRoundRect(cx - r, cy - r, r * 2, r * 2, 8, 8);
                    g2.setColor(new Color(180, 174, 166));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawRoundRect(cx - r, cy - r, r * 2, r * 2, 8, 8);
                    // Tanduk bos
                    g2.drawLine(cx - 10, cy - r, cx - 15, cy - r - 6);
                    g2.drawLine(cx + 10, cy - r, cx + 15, cy - r - 6);
                } else {
                    g2.setColor(new Color(156, 56, 48));
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setColor(TemaFortress.BATU_GELAP);
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                }

                // Aura es saat terkena MenaraEs
                if (m.isSedangMelambat()) {
                    g2.setColor(new Color(102, 184, 224, 180));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(cx - r - 3, cy - r - 3, (r + 3) * 2, (r + 3) * 2);
                }

                gambarHpBarKecil(g2, cx - 18, cy - r - 10, 36, m.getHp(), m.getHpMaks(), TemaFortress.MERAH);
            }
        }

        private void gambarHpBarKecil(Graphics2D g2, int x, int y, int w, int hp, int hpMaks, Color warnaIsi) {
            g2.setColor(TemaFortress.BATU_GELAP);
            g2.fillRect(x, y, w, 5);
            double rasio = Math.max(0.0, Math.min(1.0, (double) hp / Math.max(1, hpMaks)));
            g2.setColor(warnaIsi);
            g2.fillRect(x + 1, y + 1, (int) ((w - 2) * rasio), 3);
        }

        private void gambarProyektil(Graphics2D g2) {
            for (Proyektil p : proyektilAktif) {
                int px = offsetX + (int) ((p.getKoordinatX() + 0.5) * ukuranPetak);
                int py = offsetY + (int) ((p.getKoordinatY() + 0.5) * ukuranPetak);
                if (p.getSumberMenara() instanceof MenaraMeriam) {
                    g2.setColor(TemaFortress.BATU_GELAP);
                    g2.fillOval(px - 6, py - 6, 12, 12);
                    g2.setColor(TemaFortress.EMAS);
                    g2.drawOval(px - 6, py - 6, 12, 12);
                } else if (p.getSumberMenara() instanceof MenaraEs) {
                    g2.setColor(TemaFortress.BIRU_ES);
                    g2.fillOval(px - 5, py - 5, 10, 10);
                    g2.setColor(TemaFortress.PERKAMEN);
                    g2.drawOval(px - 5, py - 5, 10, 10);
                } else {
                    g2.setColor(TemaFortress.PERKAMEN);
                    g2.fillOval(px - 4, py - 4, 8, 8);
                }
            }
        }

        private void gambarEfekLedakan(Graphics2D g2) {
            for (EfekLedakan e : efekLedakan) {
                double progres = e.usia / e.durasi;
                int r = (int) (e.radiusMaks * ukuranPetak * progres);
                int cx = offsetX + (int) ((e.x + 0.5) * ukuranPetak);
                int cy = offsetY + (int) ((e.y + 0.5) * ukuranPetak);
                int alpha = Math.max(0, (int) ((1.0 - progres) * 180));
                g2.setColor(new Color(e.warna.getRed(), e.warna.getGreen(), e.warna.getBlue(), alpha));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
        }

        private void gambarTeksMelayang(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            for (TeksMelayang t : teksMelayang) {
                int x = offsetX + (int) ((t.x + 0.5) * ukuranPetak) - 18;
                int y = offsetY + (int) ((t.y + 0.5) * ukuranPetak);
                g2.setColor(TemaFortress.BATU_GELAP);
                g2.drawString(t.teks, x + 1, y + 1);
                g2.setColor(t.warna);
                g2.drawString(t.teks, x, y);
            }
        }

        private void gambarOverlayAkhir(Graphics2D g2) {
            g2.setColor(new Color(20, 18, 16, 195));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int boxW = 420;
            int boxH = 220;
            int bx = (getWidth() - boxW) / 2;
            int by = (getHeight() - boxH) / 2;

            g2.setColor(TemaFortress.BATU_PANEL);
            g2.fillRoundRect(bx, by, boxW, boxH, 12, 12);
            g2.setColor(menang ? TemaFortress.EMAS : TemaFortress.MERAH);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRoundRect(bx, by, boxW, boxH, 12, 12);

            g2.setFont(TemaFortress.FONT_JUDUL);
            g2.setColor(menang ? TemaFortress.EMAS : TemaFortress.MERAH);
            g2.drawString(menang ? "BENTENG BERTAHAN!" : "MARKAS RUNTUH", bx + 32, by + 50);

            g2.setFont(TemaFortress.FONT_LABEL);
            g2.setColor(TemaFortress.PERKAMEN);
            g2.drawString("Profil: " + sesi.getProfilPemain().getNamaProfil(), bx + 32, by + 88);
            g2.drawString("Musuh Dikalahkan: " + sesi.getMusuhDikalahkan(), bx + 32, by + 112);
            g2.drawString("Sisa Nyawa Markas: " + sesi.getNyawaMarkas() + " (Bonus +" + (sesi.getNyawaMarkas() * 25) + ")",
                    bx + 32, by + 136);

            g2.setFont(TemaFortress.FONT_SUBJUDUL);
            g2.setColor(TemaFortress.EMAS);
            g2.drawString("Skor Akhir Tersimpan: " + sesi.hitungSkorAkhir(), bx + 32, by + 174);

            g2.setFont(TemaFortress.FONT_KECIL);
            g2.setColor(TemaFortress.PERKAMEN_REDUP);
            g2.drawString("Gunakan tombol Main Lagi atau Lihat Papan Skor Lokal di panel kanan.", bx + 32, by + 200);
        }

        private int biayaMode(ModePenempatan mode) {
            switch (mode) {
                case MENARA_PEMANAH:
                    return 50;
                case MENARA_MERIAM:
                    return 80;
                case MENARA_ES:
                    return 70;
                case KSATRIA_PEDANG:
                    return 60;
                case PENJAGA_PERISAI:
                    return 80;
                default:
                    return 0;
            }
        }

        private double jangkauanMode(ModePenempatan mode) {
            switch (mode) {
                case MENARA_PEMANAH:
                    return 3.5;
                case MENARA_MERIAM:
                    return 2.5;
                case MENARA_ES:
                    return 3.0;
                default:
                    return 1.0;
            }
        }
    }
}
