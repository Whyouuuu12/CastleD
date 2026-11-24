package id.ac.kampus.fortressguard.model;

/**
 * Kontrak untuk seluruh entitas permainan yang memiliki poin darah (HP)
 * dan dapat menerima kerusakan serangan.
 */
public interface DapatDiserang {
    void terimaKerusakan(int jumlah);
    boolean isHidup();
    int getHp();
}
