package id.ac.kampus.fortressguard.model;

/**
 * Musuh Bos berlapis baja yang mengurangi setiap kerusakan masuk sesuai nilai armorBaja.
 */
public class MusuhBos extends Musuh {
    private final int armorBaja;

    public MusuhBos(double x, double y, int armorBaja) {
        super(x, y, 300, 0.68, 50, 3);
        this.armorBaja = Math.max(0, armorBaja);
    }

    public MusuhBos(double x, double y, int armorBaja, int bonusHpGelombang) {
        super(x, y, 300 + Math.max(0, bonusHpGelombang), 0.68, 50, 3);
        this.armorBaja = Math.max(0, armorBaja);
    }

    public int getArmorBaja() {
        return armorBaja;
    }

    @Override
    public void terimaKerusakan(int jumlah) {
        if (jumlah > 0) {
            super.terimaKerusakan(Math.max(1, jumlah - armorBaja));
        }
    }

    @Override
    public String getNamaTipe() {
        return "Bos Berlapis Baja";
    }
}
