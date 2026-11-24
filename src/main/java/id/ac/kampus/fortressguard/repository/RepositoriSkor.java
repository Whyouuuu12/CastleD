package id.ac.kampus.fortressguard.repository;

import java.util.List;

/**
 * Kontrak abstraksi persistensi papan skor lokal (memenuhi DIP & ISP).
 */
public interface RepositoriSkor {
    void simpanSkorTertinggi(String namaProfil, int skor);
    List<String> ambilDaftarSkor();
    void resetDaftarSkor();
    boolean eksporCsv(String pathCsv);
}
