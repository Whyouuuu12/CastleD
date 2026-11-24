package id.ac.kampus.fortressguard.ui;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;
import id.ac.kampus.fortressguard.service.LayananLevelKustom;
import id.ac.kampus.fortressguard.service.LayananPermainan;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;

/**
 * Antarmuka Perancang Level Kustom (UC-06: Kelola Gelombang Kustom Luring,
 * UC-07: Kelola Data Skor Lokal termasuk ekspor CSV dan reset skor).
 */
public class LevelEditorController extends JPanel {
    private final LayananLevelKustom layananLevel;
    private final LayananPermainan layananPermainan;
    private final Runnable onKembaliKeMenu;
    private final Consumer<KonfigurasiLevel> onUjiMainLevel;

    private final JComboBox<String> comboKodeLevel;
    private final JSpinner spinKoin;
    private final JSpinner spinNyawa;
    private final JSpinner spinGelombang;
    private final JSpinner spinMusuhPerGel;
    private final JSpinner spinJedaSpawn;
    private final JLabel lblStatusEditor;

    private final DefaultTableModel modelTabelSkor;
    private final JLabel lblStatusSkorKosong;
    private final JScrollPane scrollTabelSkor;

    public LevelEditorController(LayananLevelKustom layananLevel,
                                 LayananPermainan layananPermainan,
                                 Runnable onKembaliKeMenu,
                                 Consumer<KonfigurasiLevel> onUjiMainLevel) {
        this.layananLevel = layananLevel;
        this.layananPermainan = layananPermainan;
        this.onKembaliKeMenu = onKembaliKeMenu;
        this.onUjiMainLevel = onUjiMainLevel;

        setLayout(new BorderLayout(16, 16));
        setBackground(TemaFortress.BATU_GELAP);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TemaFortress.BATU_GELAP);
        JPanel judulBox = new JPanel();
        judulBox.setLayout(new BoxLayout(judulBox, BoxLayout.Y_AXIS));
        judulBox.setBackground(TemaFortress.BATU_GELAP);
        judulBox.add(TemaFortress.buatLabelTerang("Editor Level Kustom & Data Skor Lokal", TemaFortress.FONT_JUDUL));
        JLabel sub = new JLabel("Atur parameter gelombang luring (waves_config.json) dan kelola papan skor lokal.");
        sub.setForeground(TemaFortress.PERKAMEN_REDUP);
        sub.setFont(TemaFortress.FONT_LABEL);
        judulBox.add(sub);

        JButton btnKembali = TemaFortress.buatTombolSekunder("Kembali ke Menu Utama");
        btnKembali.addActionListener(e -> this.onKembaliKeMenu.run());
        header.add(judulBox, BorderLayout.WEST);
        header.add(btnKembali, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Konten utama 2 kolom
        JPanel body = new JPanel(new GridLayout(1, 2, 18, 0));
        body.setBackground(TemaFortress.BATU_GELAP);

        // Kiri: Editor Konfigurasi Level (UC-06)
        JPanel panelEditor = new JPanel(new BorderLayout(0, 12));
        panelEditor.setBackground(TemaFortress.BATU_PANEL);
        panelEditor.setBorder(TemaFortress.borderPanel());

        JLabel judulEditor = TemaFortress.buatLabelTerang("Konfigurasi Gelombang Luring (UC-06)", TemaFortress.FONT_SUBJUDUL);
        panelEditor.add(judulEditor, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 12, 12));
        form.setBackground(TemaFortress.BATU_PANEL);
        form.setPreferredSize(new Dimension(460, 260));

        comboKodeLevel = new JComboBox<>(new String[]{"KUSTOM", "LVL-01", "LVL-02"});
        comboKodeLevel.setFont(TemaFortress.FONT_LABEL_BOLD);
        comboKodeLevel.addActionListener(e -> muatParameterTerpilih());

        spinKoin = new JSpinner(new SpinnerNumberModel(400, 50, 5000, 25));
        spinNyawa = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        spinGelombang = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        spinMusuhPerGel = new JSpinner(new SpinnerNumberModel(6, 1, 30, 1));
        spinJedaSpawn = new JSpinner(new SpinnerNumberModel(1.25, 0.30, 5.00, 0.10));

        form.add(TemaFortress.buatLabelTerang("Kode Level:", TemaFortress.FONT_LABEL_BOLD));
        form.add(comboKodeLevel);
        form.add(TemaFortress.buatLabelTerang("Koin Awal (min 50):", TemaFortress.FONT_LABEL_BOLD));
        form.add(spinKoin);
        form.add(TemaFortress.buatLabelTerang("Nyawa Markas Awal:", TemaFortress.FONT_LABEL_BOLD));
        form.add(spinNyawa);
        form.add(TemaFortress.buatLabelTerang("Jumlah Gelombang:", TemaFortress.FONT_LABEL_BOLD));
        form.add(spinGelombang);
        form.add(TemaFortress.buatLabelTerang("Musuh Dasar / Gelombang:", TemaFortress.FONT_LABEL_BOLD));
        form.add(spinMusuhPerGel);
        form.add(TemaFortress.buatLabelTerang("Jeda Spawn Musuh (detik):", TemaFortress.FONT_LABEL_BOLD));
        form.add(spinJedaSpawn);

        JPanel wadahForm = new JPanel(new BorderLayout());
        wadahForm.setBackground(TemaFortress.BATU_PANEL);
        wadahForm.add(form, BorderLayout.NORTH);
        panelEditor.add(wadahForm, BorderLayout.CENTER);

        JPanel footerEditor = new JPanel();
        footerEditor.setLayout(new BoxLayout(footerEditor, BoxLayout.Y_AXIS));
        footerEditor.setBackground(TemaFortress.BATU_PANEL);

        JLabel infoEvent = new JLabel("Catatan: Gelombang kelipatan 3 otomatis memicu Event Kecepatan 1.5x.");
        infoEvent.setForeground(TemaFortress.BATU_PASIR);
        infoEvent.setFont(TemaFortress.FONT_KECIL);
        infoEvent.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatusEditor = new JLabel("Siap menyimpan perubahan ke berkas lokal.");
        lblStatusEditor.setForeground(TemaFortress.PERKAMEN_REDUP);
        lblStatusEditor.setFont(TemaFortress.FONT_LABEL);
        lblStatusEditor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel barisBtnEditor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barisBtnEditor.setBackground(TemaFortress.BATU_PANEL);
        barisBtnEditor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSimpan = TemaFortress.buatTombolUtama("Simpan Konfigurasi");
        btnSimpan.addActionListener(e -> simpanKonfigurasi());

        JButton btnUjiMain = TemaFortress.buatTombolSekunder("Uji Mainkan Level Ini");
        btnUjiMain.addActionListener(e -> {
            KonfigurasiLevel k = simpanKonfigurasi();
            if (k != null) {
                this.onUjiMainLevel.accept(k);
            }
        });

        barisBtnEditor.add(btnSimpan);
        barisBtnEditor.add(btnUjiMain);

        footerEditor.add(infoEvent);
        footerEditor.add(Box.createVerticalStrut(6));
        footerEditor.add(lblStatusEditor);
        footerEditor.add(Box.createVerticalStrut(10));
        footerEditor.add(barisBtnEditor);
        panelEditor.add(footerEditor, BorderLayout.SOUTH);

        // Kanan: Pengelolaan Data Skor Lokal (UC-07)
        JPanel panelSkor = new JPanel(new BorderLayout(0, 12));
        panelSkor.setBackground(TemaFortress.BATU_PANEL);
        panelSkor.setBorder(TemaFortress.borderPanel());

        panelSkor.add(TemaFortress.buatLabelTerang("Papan Skor Lokal - Top 10 (UC-07)", TemaFortress.FONT_SUBJUDUL),
                BorderLayout.NORTH);

        modelTabelSkor = new DefaultTableModel(new String[]{"#", "Profil", "Skor", "Waktu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelSkor = new JTable(modelTabelSkor);
        tabelSkor.setBackground(TemaFortress.BATU_KARTU);
        tabelSkor.setForeground(TemaFortress.PERKAMEN);
        tabelSkor.setGridColor(TemaFortress.GARIS_BATU);
        tabelSkor.setRowHeight(28);
        tabelSkor.setFont(TemaFortress.FONT_LABEL);
        tabelSkor.getTableHeader().setBackground(TemaFortress.BATU_GELAP);
        tabelSkor.getTableHeader().setForeground(TemaFortress.EMAS);
        tabelSkor.getTableHeader().setFont(TemaFortress.FONT_LABEL_BOLD);

        scrollTabelSkor = new JScrollPane(tabelSkor);
        scrollTabelSkor.getViewport().setBackground(TemaFortress.BATU_KARTU);
        scrollTabelSkor.setBorder(BorderFactory.createLineBorder(TemaFortress.GARIS_BATU));

        lblStatusSkorKosong = new JLabel("Belum ada rekaman skor lokal. Selesaikan satu sesi permainan terlebih dahulu.");
        lblStatusSkorKosong.setForeground(TemaFortress.PERKAMEN_REDUP);
        lblStatusSkorKosong.setFont(TemaFortress.FONT_LABEL);
        lblStatusSkorKosong.setHorizontalAlignment(JLabel.CENTER);
        lblStatusSkorKosong.setPreferredSize(new Dimension(200, 120));

        JPanel wadahTabel = new JPanel(new BorderLayout());
        wadahTabel.setBackground(TemaFortress.BATU_PANEL);
        wadahTabel.add(scrollTabelSkor, BorderLayout.CENTER);
        wadahTabel.add(lblStatusSkorKosong, BorderLayout.SOUTH);
        panelSkor.add(wadahTabel, BorderLayout.CENTER);

        JPanel barisBtnSkor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barisBtnSkor.setBackground(TemaFortress.BATU_PANEL);

        JButton btnEksporCsv = TemaFortress.buatTombolSekunder("Ekspor ke CSV");
        btnEksporCsv.addActionListener(e -> {
            boolean sukses = layananPermainan.eksporSkorKeCsv("data/local_high_scores.csv");
            JOptionPane.showMessageDialog(this,
                    sukses ? "Skor lokal berhasil diekspor ke data/local_high_scores.csv"
                            : "Gagal mengekspor skor ke CSV.",
                    "Ekspor CSV",
                    sukses ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        JButton btnResetSkor = TemaFortress.buatTombolBahaya("Reset Skor Lokal");
        btnResetSkor.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Hapus seluruh riwayat skor lokal dari local_high_scores.json?",
                    "Konfirmasi Reset Skor",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (konfirmasi == JOptionPane.YES_OPTION) {
                layananPermainan.resetDaftarSkorLokal();
                segarkanData();
            }
        });

        barisBtnSkor.add(btnEksporCsv);
        barisBtnSkor.add(btnResetSkor);
        panelSkor.add(barisBtnSkor, BorderLayout.SOUTH);

        body.add(panelEditor);
        body.add(panelSkor);
        add(body, BorderLayout.CENTER);

        segarkanData();
    }

    public void segarkanData() {
        muatParameterTerpilih();
        muatTabelSkor();
    }

    private void muatParameterTerpilih() {
        String kode = (String) comboKodeLevel.getSelectedItem();
        if (kode == null) {
            return;
        }
        KonfigurasiLevel k = layananLevel.muatKonfigurasiDefault(kode);
        spinKoin.setValue(k.getKoinAwal());
        spinNyawa.setValue(k.getNyawaMarkasAwal());
        spinGelombang.setValue(k.getJumlahGelombang());
        spinMusuhPerGel.setValue(k.getMusuhPerGelombang());
        spinJedaSpawn.setValue(k.getJedaSpawnDetik());
    }

    private KonfigurasiLevel simpanKonfigurasi() {
        String kode = (String) comboKodeLevel.getSelectedItem();
        if (kode == null) {
            return null;
        }
        try {
            int koin = ((Number) spinKoin.getValue()).intValue();
            int nyawa = ((Number) spinNyawa.getValue()).intValue();
            int gelombang = ((Number) spinGelombang.getValue()).intValue();
            int musuhPerGel = ((Number) spinMusuhPerGel.getValue()).intValue();
            double jeda = ((Number) spinJedaSpawn.getValue()).doubleValue();

            KonfigurasiLevel konfig = layananLevel.muatKonfigurasiDefault(kode);
            layananLevel.perbaruiKonfigurasiLengkap(konfig, koin, nyawa, gelombang, musuhPerGel, jeda);
            lblStatusEditor.setForeground(TemaFortress.EMAS);
            lblStatusEditor.setText("Tersimpan! Level " + kode + " diperbarui ke data/waves_config.json.");
            return konfig;
        } catch (IllegalArgumentException ex) {
            lblStatusEditor.setForeground(TemaFortress.MERAH);
            lblStatusEditor.setText("Gagal: " + ex.getMessage());
            return null;
        }
    }

    private void muatTabelSkor() {
        modelTabelSkor.setRowCount(0);
        List<String> daftar = layananPermainan.ambilDaftarSkorLokal();
        boolean kosong = daftar.isEmpty();
        lblStatusSkorKosong.setVisible(kosong);
        scrollTabelSkor.setVisible(!kosong);

        int peringkat = 1;
        for (String entri : daftar) {
            String[] bagian = entri.split("\\|");
            String profil = bagian.length > 0 ? bagian[0] : "-";
            String skor = bagian.length > 1 ? bagian[1] : "0";
            String waktu = bagian.length > 2 ? bagian[2] : "-";
            modelTabelSkor.addRow(new Object[]{peringkat++, profil, skor, waktu});
        }
        revalidate();
        repaint();
    }
}
