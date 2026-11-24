package id.ac.kampus.fortressguard.service;

import id.ac.kampus.fortressguard.model.KonfigurasiLevel;
import id.ac.kampus.fortressguard.repository.RepositoriLevel;

import java.util.List;

/**
 * Service layer untuk pengelolaan konfigurasi gelombang & level kustom luring (UC-06).
 */
public class LayananLevelKustom {
    private final RepositoriLevel repositoriLevel;

    public LayananLevelKustom(RepositoriLevel repositoriLevel) {
        if (repositoriLevel == null) {
            throw new IllegalArgumentException("RepositoriLevel wajib disuntikkan");
        }
        this.repositoriLevel = repositoriLevel;
    }

    public KonfigurasiLevel muatKonfigurasiDefault(String kodeLevel) {
        return repositoriLevel.bacaKonfigurasi(kodeLevel);
    }

    public List<KonfigurasiLevel> ambilSemuaLevel() {
        return repositoriLevel.ambilSemuaKonfigurasi();
    }

    public void perbaruiKonfigurasiKustom(KonfigurasiLevel konfigurasi, int koinBaru, int gelombangBaru) {
        konfigurasi.sesuaikanParameterKustom(koinBaru, gelombangBaru);
        repositoriLevel.simpanKonfigurasi(konfigurasi);
    }

    public void perbaruiKonfigurasiLengkap(KonfigurasiLevel konfigurasi, int koinBaru,
                                           int nyawaMarkasBaru, int gelombangBaru,
                                           int musuhPerGelombangBaru, double jedaSpawnBaru) {
        konfigurasi.sesuaikanParameterLengkap(
                koinBaru, nyawaMarkasBaru, gelombangBaru, musuhPerGelombangBaru, jedaSpawnBaru);
        repositoriLevel.simpanKonfigurasi(konfigurasi);
    }
}
