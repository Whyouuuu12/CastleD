package id.ac.kampus.fortressguard.model;

import id.ac.kampus.fortressguard.strategy.SeranganEsLambat;

/**
 * Menara Es: serangan berefek perlambatan kecepatan gerak musuh.
 */
public class MenaraEs extends Menara {
    public MenaraEs(int x, int y) {
        super("Menara Es", x, y, 70, 15, 3.0, 1.15, new SeranganEsLambat(0.5, 3));
    }
}
