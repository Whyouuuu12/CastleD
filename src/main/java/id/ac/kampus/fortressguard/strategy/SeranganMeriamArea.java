package id.ac.kampus.fortressguard.strategy;

import id.ac.kampus.fortressguard.model.DapatDiserang;
import id.ac.kampus.fortressguard.model.Musuh;

import java.util.List;

/**
 * Strategi serangan meriam dengan ledakan percikan area (splash damage).
 */
public class SeranganMeriamArea implements StrategiSerangan {
    private final double radiusArea;

    public SeranganMeriamArea(double radiusArea) {
        this.radiusArea = Math.max(1.0, radiusArea);
    }

    public double getRadiusArea() {
        return radiusArea;
    }

    @Override
    public void lancarkanSerangan(DapatDiserang targetUtama, List<Musuh> semuaMusuh, int dayaRusak) {
        if (targetUtama != null && targetUtama.isHidup()) {
            targetUtama.terimaKerusakan(dayaRusak);
        }
        int kerusakanPercikan = Math.max(1, dayaRusak / 2);
        if (semuaMusuh == null) {
            return;
        }
        double pusatX = (targetUtama instanceof Musuh) ? ((Musuh) targetUtama).getPosisiX() : 0.0;
        double pusatY = (targetUtama instanceof Musuh) ? ((Musuh) targetUtama).getPosisiY() : 0.0;
        boolean punyaKoordinat = (targetUtama instanceof Musuh);

        for (Musuh m : semuaMusuh) {
            if (m != targetUtama && m.isHidup()) {
                if (!punyaKoordinat) {
                    m.terimaKerusakan(kerusakanPercikan);
                } else {
                    double jarak = Math.hypot(m.getPosisiX() - pusatX, m.getPosisiY() - pusatY);
                    if (jarak <= radiusArea) {
                        m.terimaKerusakan(kerusakanPercikan);
                    }
                }
            }
        }
    }

    @Override
    public String getDeskripsiStrategi() {
        return "Ledakan Area (Radius " + radiusArea + ")";
    }
}
