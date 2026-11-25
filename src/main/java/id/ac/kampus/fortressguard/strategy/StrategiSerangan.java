package id.ac.kampus.fortressguard.strategy;

import id.ac.kampus.fortressguard.model.DapatDiserang;
import id.ac.kampus.fortressguard.model.Musuh;

import java.util.List;

/**
 * Antarmuka Strategy Pattern (OCP) untuk perilaku serangan setiap menara.
 */
public interface StrategiSerangan {
    void lancarkanSerangan(DapatDiserang targetUtama, List<Musuh> semuaMusuh, int dayaRusak);
    String getDeskripsiStrategi();
}
