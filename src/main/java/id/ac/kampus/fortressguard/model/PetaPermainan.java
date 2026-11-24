package id.ac.kampus.fortressguard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Peta grid 2D yang memiliki hubungan Komposisi kuat terhadap daftar Petak.
 */
public class PetaPermainan {
    private final int lebar;
    private final int tinggi;
    private final List<Petak> daftarPetak;

    public PetaPermainan(int lebar, int tinggi) {
        this.lebar = Math.max(3, lebar);
        this.tinggi = Math.max(3, tinggi);
        this.daftarPetak = new ArrayList<>();
        for (int y = 0; y < this.tinggi; y++) {
            for (int x = 0; x < this.lebar; x++) {
                this.daftarPetak.add(new Petak(x, y, (y == this.tinggi / 2)));
            }
        }
    }

    public int getLebar() {
        return lebar;
    }

    public int getTinggi() {
        return tinggi;
    }

    public int getBarisJalur() {
        return tinggi / 2;
    }

    public List<Petak> getDaftarPetak() {
        return Collections.unmodifiableList(daftarPetak);
    }

    public Petak cariPetak(int x, int y) {
        for (Petak p : daftarPetak) {
            if (p.getKoordinatX() == x && p.getKoordinatY() == y) {
                return p;
            }
        }
        throw new IllegalArgumentException("Koordinat petak di luar batas peta: (" + x + ", " + y + ")");
    }
}
