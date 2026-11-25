package id.ac.kampus.fortressguard.model;

import id.ac.kampus.fortressguard.strategy.SeranganMeriamArea;

/**
 * Menara Meriam: serangan berat berdaya ledak area (splash damage).
 */
public class MenaraMeriam extends Menara {
    public MenaraMeriam(int x, int y) {
        super("Menara Meriam", x, y, 80, 40, 2.5, 1.55, new SeranganMeriamArea(1.8));
    }
}
