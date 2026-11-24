package id.ac.kampus.fortressguard.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementasi RepositoriSkor berbasis berkas JSON lokal (dan dukungan ekspor CSV).
 * Menyimpan hingga 10 skor tertinggi secara luring.
 */
public class RepositoriSkorJson implements RepositoriSkor {
    private static final int MAKS_REKAMAN = 10;
    private static final Pattern POLA_ENTRI = Pattern.compile(
            "\\{\"profil\":\"([^\"]+)\",\"skor\":(\\d+)(?:,\"waktu\":\"([^\"]+)\")?\\}");

    private final String namaBerkasJson;
    private final List<EntriSkor> cacheEntri;

    private static class EntriSkor {
        private final String profil;
        private final int skor;
        private final String waktu;

        private EntriSkor(String profil, int skor, String waktu) {
            this.profil = profil;
            this.skor = skor;
            this.waktu = waktu;
        }
    }

    public RepositoriSkorJson(String namaBerkasJson) {
        this.namaBerkasJson = namaBerkasJson;
        this.cacheEntri = new ArrayList<>();
        muatDariBerkas();
    }

    public String getNamaBerkasJson() {
        return namaBerkasJson;
    }

    private void muatDariBerkas() {
        cacheEntri.clear();
        Path path = Paths.get(namaBerkasJson);
        if (!Files.exists(path)) {
            return;
        }
        try {
            String isi = Files.readString(path, StandardCharsets.UTF_8);
            Matcher m = POLA_ENTRI.matcher(isi);
            while (m.find()) {
                String profil = m.group(1);
                int skor = Integer.parseInt(m.group(2));
                String waktu = (m.group(3) != null) ? m.group(3) : "-";
                cacheEntri.add(new EntriSkor(profil, skor, waktu));
            }
            urutkanDanPangkas();
        } catch (IOException | NumberFormatException ignored) {
            // Jika berkas rusak atau belum ada, mulai dengan daftar kosong
        }
    }

    private void tulisKeBerkas() {
        Path path = Paths.get(namaBerkasJson);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < cacheEntri.size(); i++) {
                EntriSkor e = cacheEntri.get(i);
                sb.append("  {\"profil\":\"")
                        .append(bersihkanJson(e.profil))
                        .append("\",\"skor\":")
                        .append(e.skor)
                        .append(",\"waktu\":\"")
                        .append(bersihkanJson(e.waktu))
                        .append("\"}");
                if (i < cacheEntri.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]\n");
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Tetap aman secara luring bila izin berkas terbatas
        }
    }

    private void urutkanDanPangkas() {
        cacheEntri.sort(Comparator.comparingInt((EntriSkor e) -> e.skor).reversed());
        while (cacheEntri.size() > MAKS_REKAMAN) {
            cacheEntri.remove(cacheEntri.size() - 1);
        }
    }

    private String bersihkanJson(String teks) {
        return teks.replace("\\", "").replace("\"", "'").replace("\n", " ").trim();
    }

    @Override
    public void simpanSkorTertinggi(String namaProfil, int skor) {
        String bersih = (namaProfil == null || namaProfil.trim().isEmpty()) ? "Pemain" : bersihkanJson(namaProfil);
        String waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        cacheEntri.add(new EntriSkor(bersih, Math.max(0, skor), waktu));
        urutkanDanPangkas();
        tulisKeBerkas();
    }

    @Override
    public List<String> ambilDaftarSkor() {
        List<String> hasil = new ArrayList<>();
        for (EntriSkor e : cacheEntri) {
            hasil.add(e.profil + "|" + e.skor + "|" + e.waktu);
        }
        return Collections.unmodifiableList(hasil);
    }

    @Override
    public void resetDaftarSkor() {
        cacheEntri.clear();
        tulisKeBerkas();
    }

    @Override
    public boolean eksporCsv(String pathCsv) {
        Path path = Paths.get(pathCsv);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Peringkat,Profil,Skor,Waktu\n");
            for (int i = 0; i < cacheEntri.size(); i++) {
                EntriSkor e = cacheEntri.get(i);
                sb.append(i + 1).append(",")
                        .append(e.profil.replace(",", " ")).append(",")
                        .append(e.skor).append(",")
                        .append(e.waktu).append("\n");
            }
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
