package id.ac.kampus.fortressguard.repository;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;

import java.util.List;

/**
 * Kontrak abstraksi persistensi konfigurasi level & gelombang kustom lokal (memenuhi DIP & ISP).
 */
public interface RepositoriLevel {
    void simpanKonfigurasi(KonfigurasiLevel konfigurasi);
    KonfigurasiLevel bacaKonfigurasi(String kodeLevel);
    List<KonfigurasiLevel> ambilSemuaKonfigurasi();
}
