package id.ac.kampus.fortressguard.repository;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementasi RepositoriLevel berbasis berkas JSON lokal (data/waves_config.json).
 */
public class RepositoriLevelJson implements RepositoriLevel {
    private static final Pattern POLA_LEVEL = Pattern.compile(
            "\\{\"kodeLevel\":\"([^\"]+)\",\"koinAwal\":(\\d+),\"nyawaMarkasAwal\":(\\d+),"
                    + "\"jumlahGelombang\":(\\d+),\"musuhPerGelombang\":(\\d+),\"jedaSpawnDetik\":([0-9]+(?:\\.[0-9]+)?)\\}");

    private final String namaBerkasLevel;
    private final Map<String, KonfigurasiLevel> cacheLevel;

    public RepositoriLevelJson(String namaBerkasLevel) {
        this.namaBerkasLevel = namaBerkasLevel;
        this.cacheLevel = new LinkedHashMap<>();
        inisialisasiDefault();
        muatDariBerkas();
    }

    public String getNamaBerkasLevel() {
        return namaBerkasLevel;
    }

    private void inisialisasiDefault() {
        cacheLevel.put("LVL-01", new KonfigurasiLevel("LVL-01", 350, 20, 3, 5, 1.4));
        cacheLevel.put("LVL-02", new KonfigurasiLevel("LVL-02", 420, 18, 6, 7, 1.15));
        cacheLevel.put("KUSTOM", new KonfigurasiLevel("KUSTOM", 400, 20, 4, 6, 1.25));
    }

    private void muatDariBerkas() {
        Path path = Paths.get(namaBerkasLevel);
        if (!Files.exists(path)) {
            tulisKeBerkas();
            return;
        }
        try {
            String isi = Files.readString(path, StandardCharsets.UTF_8);
            Matcher m = POLA_LEVEL.matcher(isi);
            while (m.find()) {
                String kode = m.group(1);
                int koin = Integer.parseInt(m.group(2));
                int nyawa = Integer.parseInt(m.group(3));
                int gelombang = Integer.parseInt(m.group(4));
                int musuhPerGel = Integer.parseInt(m.group(5));
                double jeda = Double.parseDouble(m.group(6));
                cacheLevel.put(kode, new KonfigurasiLevel(kode, koin, nyawa, gelombang, musuhPerGel, jeda));
            }
        } catch (IOException | IllegalArgumentException ignored) {
            // Pertahankan default jika berkas tidak terbaca
        }
    }

    private void tulisKeBerkas() {
        Path path = Paths.get(namaBerkasLevel);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            List<KonfigurasiLevel> daftar = new ArrayList<>(cacheLevel.values());
            for (int i = 0; i < daftar.size(); i++) {
                KonfigurasiLevel k = daftar.get(i);
                sb.append(String.format(Locale.US,
                        "  {\"kodeLevel\":\"%s\",\"koinAwal\":%d,\"nyawaMarkasAwal\":%d,"
                                + "\"jumlahGelombang\":%d,\"musuhPerGelombang\":%d,\"jedaSpawnDetik\":%.2f}",
                        k.getKodeLevel(), k.getKoinAwal(), k.getNyawaMarkasAwal(),
                        k.getJumlahGelombang(), k.getMusuhPerGelombang(), k.getJedaSpawnDetik()));
                if (i < daftar.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]\n");
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Aman jika gagal menulis
        }
    }

    @Override
    public void simpanKonfigurasi(KonfigurasiLevel konfigurasi) {
        if (konfigurasi != null) {
            cacheLevel.put(konfigurasi.getKodeLevel(), konfigurasi);
            tulisKeBerkas();
        }
    }

    @Override
    public KonfigurasiLevel bacaKonfigurasi(String kodeLevel) {
        KonfigurasiLevel k = cacheLevel.get(kodeLevel);
        if (k != null) {
            return k;
        }
        return new KonfigurasiLevel(kodeLevel, 350, 20, 3, 5, 1.4);
    }

    @Override
    public List<KonfigurasiLevel> ambilSemuaKonfigurasi() {
        return Collections.unmodifiableList(new ArrayList<>(cacheLevel.values()));
    }
}
