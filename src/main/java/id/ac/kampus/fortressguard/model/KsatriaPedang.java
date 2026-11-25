package id.ac.kampus.fortressguard.model;

/**
 * Unit melee ofensif dengan serangan cepat dan peluang serangan kritikal 2x (30%).
 */
public class KsatriaPedang extends UnitMelee {
    private final double peluangKritikal;
    private boolean seranganTerakhirKritikal;

    public KsatriaPedang(int x, int y) {
        super("Ksatria Pedang", x, y, 160, 30, 60, 0.75);
        this.peluangKritikal = 0.30;
        this.seranganTerakhirKritikal = false;
    }

    public double getPeluangKritikal() {
        return peluangKritikal;
    }

    public boolean isSeranganTerakhirKritikal() {
        return seranganTerakhirKritikal;
    }

    @Override
    public void bertarung(Musuh target) {
        if (isHidup() && target != null && target.isHidup()) {
            boolean kritikal = Math.random() < peluangKritikal;
            this.seranganTerakhirKritikal = kritikal;
            int totalKerusakan = kritikal ? getDayaRusakMelee() * 2 : getDayaRusakMelee();
            target.terimaKerusakan(totalKerusakan);
            resetCooldownSerang();
        }
    }
}
