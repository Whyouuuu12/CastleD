package id.ac.kampus.fortressguard.model;

/**
 * Unit melee defensif berdarah tebal dengan armor perisai dan kemampuan menghadang hingga 3 musuh.
 */
public class PenjagaPerisai extends UnitMelee {
    private final int armorPerisai;
    private final int kapasitasHadang;

    public PenjagaPerisai(int x, int y) {
        super("Penjaga Perisai", x, y, 300, 15, 80, 1.0);
        this.armorPerisai = 8;
        this.kapasitasHadang = 3;
    }

    public int getArmorPerisai() {
        return armorPerisai;
    }

    public int getKapasitasHadang() {
        return kapasitasHadang;
    }

    @Override
    public int getKapasitasHadangEfektif() {
        return kapasitasHadang;
    }

    @Override
    public void terimaKerusakan(int jumlah) {
        if (jumlah > 0) {
            super.terimaKerusakan(Math.max(1, jumlah - armorPerisai));
        }
    }

    @Override
    public void bertarung(Musuh target) {
        if (isHidup() && target != null && target.isHidup()) {
            target.terimaKerusakan(getDayaRusakMelee());
            resetCooldownSerang();
        }
    }
}
