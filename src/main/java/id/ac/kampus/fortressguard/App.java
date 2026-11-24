package id.ac.kampus.fortressguard;

import id.ac.kampus.fortressguard.model.Gelombang;
import id.ac.kampus.fortressguard.model.KonfigurasiLevel;
import id.ac.kampus.fortressguard.model.KsatriaPedang;
import id.ac.kampus.fortressguard.model.Menara;
import id.ac.kampus.fortressguard.model.MenaraEs;
import id.ac.kampus.fortressguard.model.MenaraMeriam;
import id.ac.kampus.fortressguard.model.MenaraPemanah;
import id.ac.kampus.fortressguard.model.MusuhBiasa;
import id.ac.kampus.fortressguard.model.MusuhBos;
import id.ac.kampus.fortressguard.model.PenjagaPerisai;
import id.ac.kampus.fortressguard.model.ProfilPemain;
import id.ac.kampus.fortressguard.model.SesiPermainan;
import id.ac.kampus.fortressguard.model.UnitMelee;
import id.ac.kampus.fortressguard.repository.RepositoriLevel;
import id.ac.kampus.fortressguard.repository.RepositoriLevelJson;
import id.ac.kampus.fortressguard.repository.RepositoriLevelMemori;
import id.ac.kampus.fortressguard.repository.RepositoriSkor;
import id.ac.kampus.fortressguard.repository.RepositoriSkorJson;
import id.ac.kampus.fortressguard.repository.RepositoriSkorMemori;
import id.ac.kampus.fortressguard.service.LayananLevelKustom;
import id.ac.kampus.fortressguard.service.LayananPermainan;
import id.ac.kampus.fortressguard.ui.MenuUtamaView;

import javax.swing.SwingUtilities;

/**
 * Titik masuk sekaligus Perakit Aplikasi (Composition Root / Dependency Injection) FortressGuard 2D.
 */
public class App {
    public static void main(String[] args) {
        if (args.length > 0 && "--cli-verify".equalsIgnoreCase(args[0])) {
            jalankanUjiKonsolV2();
            return;
        }

        RepositoriSkor repoSkor = new RepositoriSkorJson("data/local_high_scores.json");
        RepositoriLevel repoLevel = new RepositoriLevelJson("data/waves_config.json");
        LayananPermainan layananPermainan = new LayananPermainan(repoSkor);
        LayananLevelKustom layananLevel = new LayananLevelKustom(repoLevel);

        SwingUtilities.invokeLater(() -> {
            MenuUtamaView jendela = new MenuUtamaView(layananPermainan, layananLevel);
            jendela.setVisible(true);
        });
    }

    /**
     * Uji cepat konsol untuk memverifikasi seluruh logika domain Rancangan v2 (SOLID) tanpa GUI.
     */
    private static void jalankanUjiKonsolV2() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        RepositoriLevel repoLevel = new RepositoriLevelMemori();
        LayananPermainan layananPermainan = new LayananPermainan(repoSkor);
        LayananLevelKustom layananLevel = new LayananLevelKustom(repoLevel);

        ProfilPemain pemain = new ProfilPemain("Armando_FG");
        KonfigurasiLevel level1 = layananLevel.muatKonfigurasiDefault("LVL-01");
        SesiPermainan sesi = new SesiPermainan(pemain, level1);

        Menara pemanah = new MenaraPemanah(2, 1);
        Menara meriam = new MenaraMeriam(3, 1);
        Menara es = new MenaraEs(4, 1);
        UnitMelee ksatria = new KsatriaPedang(2, 2);
        UnitMelee penjaga = new PenjagaPerisai(3, 2);

        boolean ok1 = layananPermainan.bangunMenaraPadaPeta(sesi, pemanah);
        boolean ok2 = layananPermainan.bangunMenaraPadaPeta(sesi, meriam);
        boolean ok3 = layananPermainan.bangunMenaraPadaPeta(sesi, es);
        boolean ok4 = layananPermainan.tempatkanUnitMeleePadaJalur(sesi, ksatria);
        boolean ok5 = layananPermainan.tempatkanUnitMeleePadaJalur(sesi, penjaga);

        Gelombang gel3 = sesi.getDaftarGelombang().get(2);
        MusuhBiasa goblin = new MusuhBiasa(0.0, 2.0);
        MusuhBos bosBaja = new MusuhBos(0.0, 2.0, 10);
        gel3.tambahMusuh(goblin);
        gel3.tambahMusuh(bosBaja);
        gel3.picuEventKecepatan();

        pemanah.serang(goblin, gel3.getDaftarMusuh());
        meriam.serang(bosBaja, gel3.getDaftarMusuh());
        es.serang(bosBaja, gel3.getDaftarMusuh());
        ksatria.bertarung(goblin);
        penjaga.bertarung(bosBaja);

        sesi.tambahKoinDanSkor(goblin.getHadiahKoin(), 150);
        boolean jualOk = layananPermainan.jualMenara(sesi, pemanah);
        layananPermainan.akhiriSesiDanSimpanSkor(sesi);

        System.out.println("=== UJI KOMPILASI & EKSEKUSI RANCANGAN V2 (FORTRESSGUARD 2D) ===");
        System.out.println("Penempatan valid  : " + (ok1 && ok2 && ok3 && ok4 && ok5));
        System.out.println("Jual menara valid : " + jualOk);
        System.out.println("Profil Pemain     : " + pemain.getNamaProfil());
        System.out.println("Sisa Koin Sesi    : " + sesi.getKoin());
        System.out.println("HP Goblin (Biasa) : " + goblin.getHp() + " | Kecepatan Event: " + goblin.getKecepatan());
        System.out.println("HP Bos Baja       : " + bosBaja.getHp() + " | Kecepatan Efek Es: " + bosBaja.getKecepatan());
        System.out.println("Rekaman Skor      : " + repoSkor.ambilDaftarSkor());
    }
}
