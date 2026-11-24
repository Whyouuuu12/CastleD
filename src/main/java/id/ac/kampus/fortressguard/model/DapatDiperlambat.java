package id.ac.kampus.fortressguard.model;

/**
 * Kontrak terpisah (ISP & LSP) untuk entitas bergerak yang dapat menerima
 * efek perlambatan kecepatan gerak (hanya diimplementasikan oleh Musuh).
 */
public interface DapatDiperlambat {
    void terapkanEfekLambat(double faktorLambat, int durasiDetik);
}
