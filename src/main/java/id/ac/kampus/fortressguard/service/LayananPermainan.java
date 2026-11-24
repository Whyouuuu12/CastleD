package id.ac.kampus.fortressguard.service;

import id.ac.kampus.fortressguard.model.Menara;
import id.ac.kampus.fortressguard.model.Petak;
import id.ac.kampus.fortressguard.model.ProfilPemain;
import id.ac.kampus.fortressguard.model.SesiPermainan;
import id.ac.kampus.fortressguard.model.UnitMelee;
import id.ac.kampus.fortressguard.repository.RepositoriSkor;

import java.util.List;

/**
 * Service layer untuk koordinasi logika bisnis sesi permainan (SRP & DIP).
 * Tidak bergantung pada kelas UI maupun implementasi konkret repositori.
 */
public class LayananPermainan {
    private final RepositoriSkor repositoriSkor;

    public LayananPermainan(RepositoriSkor repositoriSkor) {
        if (repositoriSkor == null) {
            throw new IllegalArgumentException("RepositoriSkor wajib disuntikkan");
        }
        this.repositoriSkor = repositoriSkor;
    }

    public boolean bangunMenaraPadaPeta(SesiPermainan sesi, Menara menara) {
        if (sesi == null || menara == null) {
            return false;
        }
        Petak petak = sesi.getPetaPermainan().cariPetak(menara.getPosisiX(), menara.getPosisiY());
        if (!petak.dapatDibangunMenara() || sesi.getKoin() < menara.getBiaya()) {
            return false;
        }
        sesi.kurangiKoin(menara.getBiaya());
        petak.tandaiTerisi();
        sesi.simpanMenara(menara);
        return true;
    }

    public boolean tempatkanUnitMeleePadaJalur(SesiPermainan sesi, UnitMelee unit) {
        if (sesi == null || unit == null) {
            return false;
        }
        Petak petak = sesi.getPetaPermainan().cariPetak(unit.getPosisiX(), unit.getPosisiY());
        if (!petak.dapatDitempatkanMelee() || sesi.getKoin() < unit.getBiaya()) {
            return false;
        }
        sesi.kurangiKoin(unit.getBiaya());
        petak.tandaiTerisi();
        sesi.simpanUnitMelee(unit);
        return true;
    }

    public boolean upgradeMenara(SesiPermainan sesi, Menara menara, int biayaUpgrade) {
        if (sesi == null || menara == null || biayaUpgrade <= 0 || sesi.getKoin() < biayaUpgrade) {
            return false;
        }
        sesi.kurangiKoin(biayaUpgrade);
        menara.tingkatkanLevel();
        return true;
    }

    public boolean jualMenara(SesiPermainan sesi, Menara menara) {
        if (sesi == null || menara == null) {
            return false;
        }
        if (!sesi.hapusMenara(menara)) {
            return false;
        }
        int hasilJual = menara.hitungHargaJual();
        sesi.tambahKoin(hasilJual);
        Petak petak = sesi.getPetaPermainan().cariPetak(menara.getPosisiX(), menara.getPosisiY());
        petak.kosongkanPetak();
        return true;
    }

    public boolean upgradeUnitMelee(SesiPermainan sesi, UnitMelee unit, int biayaUpgrade) {
        if (sesi == null || unit == null || !unit.isHidup() || biayaUpgrade <= 0 || sesi.getKoin() < biayaUpgrade) {
            return false;
        }
        sesi.kurangiKoin(biayaUpgrade);
        unit.tingkatkanLevel();
        return true;
    }

    public void bersihkanUnitGugurDariPeta(SesiPermainan sesi) {
        if (sesi == null) {
            return;
        }
        List<UnitMelee> gugur = sesi.bersihkanUnitMeleeGugur();
        for (UnitMelee u : gugur) {
            Petak p = sesi.getPetaPermainan().cariPetak(u.getPosisiX(), u.getPosisiY());
            p.kosongkanPetak();
        }
    }

    public void akhiriSesiDanSimpanSkor(SesiPermainan sesi) {
        if (sesi == null) {
            return;
        }
        int skorAkhir = sesi.hitungSkorAkhir();
        ProfilPemain profil = sesi.getProfilPemain();
        profil.catatRekorSkorBaru(skorAkhir);
        if (!sesi.isKalah() && sesi.isSemuaGelombangSelesai()) {
            profil.bukaLevelBerikutnya();
        }
        repositoriSkor.simpanSkorTertinggi(profil.getNamaProfil(), skorAkhir);
    }

    public List<String> ambilDaftarSkorLokal() {
        return repositoriSkor.ambilDaftarSkor();
    }

    public void resetDaftarSkorLokal() {
        repositoriSkor.resetDaftarSkor();
    }

    public boolean eksporSkorKeCsv(String pathCsv) {
        return repositoriSkor.eksporCsv(pathCsv);
    }
}
