package id.ac.kampus.fortressguard.model;

import java.util.List;

/**
 * Proyektil yang diluncurkan Menara menuju target DapatDiserang dan menerapkan StrategiSerangan saat tiba.
 */
public class Proyektil {
    private double koordinatX;
    private double koordinatY;
    private final double kecepatanLuncur;
    private final int nilaiKerusakan;
    private final DapatDiserang target;
    private final Menara sumberMenara;
    private boolean aktif;

    public Proyektil(double x, double y, double spd, int dmg, DapatDiserang target) {
        this(x, y, spd, dmg, target, null);
    }

    public Proyektil(double x, double y, double spd, int dmg, DapatDiserang target, Menara sumberMenara) {
        this.koordinatX = x;
        this.koordinatY = y;
        this.kecepatanLuncur = Math.max(1.0, spd);
        this.nilaiKerusakan = Math.max(1, dmg);
        this.target = target;
        this.sumberMenara = sumberMenara;
        this.aktif = true;
    }

    public void meluncurDanHantam() {
        if (target != null && target.isHidup()) {
            this.koordinatX += kecepatanLuncur;
            target.terimaKerusakan(nilaiKerusakan);
            this.aktif = false;
        }
    }

    /**
     * Memajukan proyektil menuju posisi target pada game loop 2D.
     * Mengembalikan true tepat pada frame proyektil menghantam target.
     */
    public boolean perbaruiPosisi(double deltaDetik, List<Musuh> semuaMusuh) {
        if (!aktif || target == null || !target.isHidup()) {
            aktif = false;
            return false;
        }
        if (!(target instanceof Musuh)) {
            target.terimaKerusakan(nilaiKerusakan);
            aktif = false;
            return true;
        }
        Musuh targetMusuh = (Musuh) target;
        double dx = targetMusuh.getPosisiX() - this.koordinatX;
        double dy = targetMusuh.getPosisiY() - this.koordinatY;
        double jarak = Math.hypot(dx, dy);
        double langkah = kecepatanLuncur * deltaDetik;

        if (jarak <= langkah || jarak < 0.22) {
            this.koordinatX = targetMusuh.getPosisiX();
            this.koordinatY = targetMusuh.getPosisiY();
            if (sumberMenara != null) {
                sumberMenara.serang(targetMusuh, semuaMusuh);
            } else {
                targetMusuh.terimaKerusakan(nilaiKerusakan);
            }
            aktif = false;
            return true;
        }

        this.koordinatX += (dx / jarak) * langkah;
        this.koordinatY += (dy / jarak) * langkah;
        return false;
    }

    public double getKoordinatX() {
        return koordinatX;
    }

    public double getKoordinatY() {
        return koordinatY;
    }

    public double getKecepatanLuncur() {
        return kecepatanLuncur;
    }

    public int getNilaiKerusakan() {
        return nilaiKerusakan;
    }

    public DapatDiserang getTarget() {
        return target;
    }

    public Menara getSumberMenara() {
        return sumberMenara;
    }

    public boolean isAktif() {
        return aktif;
    }
}
