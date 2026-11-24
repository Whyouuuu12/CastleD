package id.ac.kampus.fortressguard.model;

/**
 * Menyimpan parameter konfigurasi level dan gelombang untuk mode standar maupun editor kustom luring.
 */
public class KonfigurasiLevel {
    private final String kodeLevel;
    private int koinAwal;
    private int nyawaMarkasAwal;
    private int jumlahGelombang;
    private int musuhPerGelombang;
    private double jedaSpawnDetik;

    public KonfigurasiLevel(String kodeLevel, int koinAwal, int nyawaMarkasAwal, int jumlahGelombang) {
        this(kodeLevel, koinAwal, nyawaMarkasAwal, jumlahGelombang, 5, 1.4);
    }

    public KonfigurasiLevel(String kodeLevel, int koinAwal, int nyawaMarkasAwal,
                            int jumlahGelombang, int musuhPerGelombang, double jedaSpawnDetik) {
        if (kodeLevel == null || kodeLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode level tidak boleh kosong");
        }
        if (koinAwal <= 0 || nyawaMarkasAwal <= 0 || jumlahGelombang <= 0) {
            throw new IllegalArgumentException("Parameter konfigurasi level harus positif");
        }
        if (musuhPerGelombang < 1 || jedaSpawnDetik < 0.3) {
            throw new IllegalArgumentException("Jumlah musuh minimal 1 dan jeda spawn minimal 0.3 detik");
        }
        this.kodeLevel = kodeLevel.trim();
        this.koinAwal = koinAwal;
        this.nyawaMarkasAwal = nyawaMarkasAwal;
        this.jumlahGelombang = jumlahGelombang;
        this.musuhPerGelombang = musuhPerGelombang;
        this.jedaSpawnDetik = jedaSpawnDetik;
    }

    public String getKodeLevel() {
        return kodeLevel;
    }

    public int getKoinAwal() {
        return koinAwal;
    }

    public int getNyawaMarkasAwal() {
        return nyawaMarkasAwal;
    }

    public int getJumlahGelombang() {
        return jumlahGelombang;
    }

    public int getMusuhPerGelombang() {
        return musuhPerGelombang;
    }

    public double getJedaSpawnDetik() {
        return jedaSpawnDetik;
    }

    public void sesuaikanParameterKustom(int koinAwalBaru, int jumlahGelombangBaru) {
        if (koinAwalBaru < 50 || jumlahGelombangBaru < 1) {
            throw new IllegalArgumentException("Koin awal minimal 50 dan gelombang minimal 1");
        }
        this.koinAwal = koinAwalBaru;
        this.jumlahGelombang = jumlahGelombangBaru;
    }

    public void sesuaikanParameterLengkap(int koinAwalBaru, int nyawaMarkasBaru,
                                          int jumlahGelombangBaru, int musuhPerGelombangBaru,
                                          double jedaSpawnBaru) {
        if (koinAwalBaru < 50 || nyawaMarkasBaru < 1 || jumlahGelombangBaru < 1
                || musuhPerGelombangBaru < 1 || jedaSpawnBaru < 0.3) {
            throw new IllegalArgumentException("Parameter konfigurasi kustom tidak valid");
        }
        this.koinAwal = koinAwalBaru;
        this.nyawaMarkasAwal = nyawaMarkasBaru;
        this.jumlahGelombang = jumlahGelombangBaru;
        this.musuhPerGelombang = musuhPerGelombangBaru;
        this.jedaSpawnDetik = jedaSpawnBaru;
    }
}
