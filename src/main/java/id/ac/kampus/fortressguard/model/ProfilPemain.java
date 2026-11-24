package id.ac.kampus.fortressguard.model;

/**
 * Menyimpan identitas profil pemain lokal, rekor skor tertinggi, dan progres level.
 */
public class ProfilPemain {
    private final String namaProfil;
    private int skorTertinggiLokal;
    private int levelTerbuka;

    public ProfilPemain(String namaProfil) {
        if (namaProfil == null || namaProfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama profil tidak boleh kosong");
        }
        this.namaProfil = namaProfil.trim();
        this.skorTertinggiLokal = 0;
        this.levelTerbuka = 1;
    }

    public String getNamaProfil() {
        return namaProfil;
    }

    public int getSkorTertinggiLokal() {
        return skorTertinggiLokal;
    }

    public int getLevelTerbuka() {
        return levelTerbuka;
    }

    public void catatRekorSkorBaru(int skorBaru) {
        if (skorBaru > this.skorTertinggiLokal) {
            this.skorTertinggiLokal = skorBaru;
        }
    }

    public void bukaLevelBerikutnya() {
        this.levelTerbuka++;
    }
}
