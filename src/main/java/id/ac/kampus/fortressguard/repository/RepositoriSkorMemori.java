package id.ac.kampus.fortressguard.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementasi RepositoriSkor di memori untuk pengujian unit terisolasi tanpa sistem berkas.
 */
public class RepositoriSkorMemori implements RepositoriSkor {
    private final List<String> rekamanSkor = new ArrayList<>();

    @Override
    public void simpanSkorTertinggi(String namaProfil, int skor) {
        rekamanSkor.add(namaProfil + ":" + skor);
    }

    @Override
    public List<String> ambilDaftarSkor() {
        return Collections.unmodifiableList(rekamanSkor);
    }

    @Override
    public void resetDaftarSkor() {
        rekamanSkor.clear();
    }

    @Override
    public boolean eksporCsv(String pathCsv) {
        return true;
    }
}
