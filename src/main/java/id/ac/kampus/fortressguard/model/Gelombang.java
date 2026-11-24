package id.ac.kampus.fortressguard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mengelola daftar musuh untuk satu nomor gelombang serta Event Gelombang Cepat (kelipatan 3).
 */
public class Gelombang {
    private final int nomorGelombang;
    private final boolean eventCepatAktif;
    private final double pengaliKecepatanEvent;
    private final List<Musuh> daftarMusuh;

    public Gelombang(int nomorGelombang) {
        this.nomorGelombang = Math.max(1, nomorGelombang);
        this.eventCepatAktif = (this.nomorGelombang % 3 == 0);
        this.pengaliKecepatanEvent = 1.5;
        this.daftarMusuh = new ArrayList<>();
    }

    public void tambahMusuh(Musuh m) {
        if (m != null) {
            this.daftarMusuh.add(m);
        }
    }

    public void picuEventKecepatan() {
        if (this.eventCepatAktif) {
            for (Musuh m : daftarMusuh) {
                m.terapkanLonjakanKecepatan(pengaliKecepatanEvent);
            }
        }
    }

    public int getNomorGelombang() {
        return nomorGelombang;
    }

    public boolean isEventCepatAktif() {
        return eventCepatAktif;
    }

    public double getPengaliKecepatanEvent() {
        return pengaliKecepatanEvent;
    }

    public List<Musuh> getDaftarMusuh() {
        return Collections.unmodifiableList(daftarMusuh);
    }
}
