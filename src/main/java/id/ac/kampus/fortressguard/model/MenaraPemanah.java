package id.ac.kampus.fortressguard.model;

import id.ac.kampus.fortressguard.strategy.SeranganPanah;

/**
 * Menara Pemanah: serangan tunggal cepat dengan jangkauan luas.
 */
public class MenaraPemanah extends Menara {
    public MenaraPemanah(int x, int y) {
        super("Menara Pemanah", x, y, 50, 25, 3.5, 0.75, new SeranganPanah(5));
    }
}
