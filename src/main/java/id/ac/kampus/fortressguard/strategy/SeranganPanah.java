package id.ac.kampus.fortressguard.strategy;

import id.ac.kampus.fortressguard.model.DapatDiserang;
import id.ac.kampus.fortressguard.model.Musuh;

import java.util.List;

/**
 * Strategi serangan panah tunggal cepat dengan bonus tusukan.
 */
public class SeranganPanah implements StrategiSerangan {
    private final int bonusTusukan;

    public SeranganPanah(int bonusTusukan) {
        this.bonusTusukan = Math.max(0, bonusTusukan);
    }

    public int getBonusTusukan() {
        return bonusTusukan;
    }

    @Override
    public void lancarkanSerangan(DapatDiserang targetUtama, List<Musuh> semuaMusuh, int dayaRusak) {
        if (targetUtama != null && targetUtama.isHidup()) {
            targetUtama.terimaKerusakan(dayaRusak + bonusTusukan);
        }
    }

    @Override
    public String getDeskripsiStrategi() {
        return "Panah Tunggal (+Bonus " + bonusTusukan + ")";
    }
}
