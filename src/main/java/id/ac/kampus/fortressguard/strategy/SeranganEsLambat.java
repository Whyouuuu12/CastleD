package id.ac.kampus.fortressguard.strategy;

import id.ac.kampus.fortressguard.model.DapatDiperlambat;
import id.ac.kampus.fortressguard.model.DapatDiserang;
import id.ac.kampus.fortressguard.model.Musuh;

import java.util.List;

/**
 * Strategi serangan es yang melukai sekaligus memperlambat musuh melalui antarmuka DapatDiperlambat.
 */
public class SeranganEsLambat implements StrategiSerangan {
    private final double faktorLambat;
    private final int durasiDetik;

    public SeranganEsLambat(double faktorLambat, int durasiDetik) {
        this.faktorLambat = (faktorLambat > 0.0 && faktorLambat < 1.0) ? faktorLambat : 0.5;
        this.durasiDetik = Math.max(1, durasiDetik);
    }

    public double getFaktorLambat() {
        return faktorLambat;
    }

    public int getDurasiDetik() {
        return durasiDetik;
    }

    @Override
    public void lancarkanSerangan(DapatDiserang targetUtama, List<Musuh> semuaMusuh, int dayaRusak) {
        if (targetUtama != null && targetUtama.isHidup()) {
            targetUtama.terimaKerusakan(dayaRusak);
            if (targetUtama instanceof DapatDiperlambat) {
                ((DapatDiperlambat) targetUtama).terapkanEfekLambat(faktorLambat, durasiDetik);
            }
        }
    }

    @Override
    public String getDeskripsiStrategi() {
        return "Efek Es Lambat (" + (int) (faktorLambat * 100) + "% Kecepatan, " + durasiDetik + "d)";
    }
}
