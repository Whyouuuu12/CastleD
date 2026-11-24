package id.ac.kampus.fortressguard.repository;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementasi RepositoriLevel di memori untuk pengujian unit terisolasi.
 */
public class RepositoriLevelMemori implements RepositoriLevel {
    private final Map<String, KonfigurasiLevel> dataTersimpan = new LinkedHashMap<>();

    public RepositoriLevelMemori() {
        dataTersimpan.put("LVL-01", new KonfigurasiLevel("LVL-01", 350, 20, 3, 5, 1.4));
        dataTersimpan.put("LVL-02", new KonfigurasiLevel("LVL-02", 420, 18, 6, 7, 1.2));
        dataTersimpan.put("KUSTOM", new KonfigurasiLevel("KUSTOM", 400, 20, 4, 6, 1.3));
    }

    @Override
    public void simpanKonfigurasi(KonfigurasiLevel konfigurasi) {
        if (konfigurasi != null) {
            dataTersimpan.put(konfigurasi.getKodeLevel(), konfigurasi);
        }
    }

    @Override
    public KonfigurasiLevel bacaKonfigurasi(String kodeLevel) {
        KonfigurasiLevel k = dataTersimpan.get(kodeLevel);
        return (k != null) ? k : new KonfigurasiLevel(kodeLevel, 350, 20, 3, 5, 1.4);
    }

    @Override
    public List<KonfigurasiLevel> ambilSemuaKonfigurasi() {
        return Collections.unmodifiableList(new ArrayList<>(dataTersimpan.values()));
    }
}
