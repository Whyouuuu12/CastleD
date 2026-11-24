package id.ac.kampus.fortressguard.model;

/**
 * Musuh standar berjumlah banyak dengan kecepatan menengah.
 */
public class MusuhBiasa extends Musuh {
    public MusuhBiasa(double x, double y) {
        super(x, y, 100, 1.15, 15, 1);
    }

    public MusuhBiasa(double x, double y, int bonusHpGelombang) {
        super(x, y, 100 + Math.max(0, bonusHpGelombang), 1.15, 15, 1);
    }

    @Override
    public String getNamaTipe() {
        return "Goblin Penyerang";
    }
}
