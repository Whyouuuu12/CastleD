package id.ac.kampus.fortressguard;

import id.ac.kampus.fortressguard.model.DapatDiserang;
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
import id.ac.kampus.fortressguard.repository.RepositoriLevelMemori;
import id.ac.kampus.fortressguard.repository.RepositoriSkor;
import id.ac.kampus.fortressguard.repository.RepositoriSkorMemori;
import id.ac.kampus.fortressguard.service.LayananLevelKustom;
import id.ac.kampus.fortressguard.service.LayananPermainan;

/**
 * Kode uji Implementasi v3: minimal 2 skenario (BERHASIL dan GAGAL) untuk tiap fitur utama.
 */
public class FortressGuardV3Test {
    private static int lulus = 0;
    private static int gagal = 0;

    public static void main(String[] args) {
        System.out.println("=== PENGUJIAN IMPLEMENTASI V3 (FORTRESSGUARD) ===");

        // Fitur 1: Pembangunan Menara & Penempatan Unit Melee
        uji("F1-Berhasil: Bangun Menara di petak rumput dengan koin cukup",
                FortressGuardV3Test::ujiBangunMenaraBerhasil);
        uji("F1-Gagal: Bangun Menara di petak jalur musuh ditolak",
                FortressGuardV3Test::ujiBangunMenaraDiJalurGagal);
        uji("F1-Gagal: Tempatkan UnitMelee saat koin tidak mencukupi ditolak",
                FortressGuardV3Test::ujiTempatkanMeleeKoinKurangGagal);

        // Fitur 2: Upgrade & Jual Menara + Polimorfisme Serangan
        uji("F2-Berhasil: Upgrade & Jual Menara memperbarui level, koin, dan petak",
                FortressGuardV3Test::ujiUpgradeDanJualMenaraBerhasil);
        uji("F2-Gagal: Upgrade Menara saat koin tidak cukup ditolak",
                FortressGuardV3Test::ujiUpgradeMenaraKoinKurangGagal);
        uji("F2-Berhasil: Polimorfisme StrategiSerangan, Armor Bos, & Efek Lambat",
                FortressGuardV3Test::ujiPolimorfismeSeranganBerhasil);

        // Fitur 3: Kustomisasi Level & Penyimpanan Skor Lokal
        uji("F3-Berhasil: Perbarui Konfigurasi Level Kustom & Simpan Skor",
                FortressGuardV3Test::ujiLevelKustomDanSkorBerhasil);
        uji("F3-Gagal: Parameter Konfigurasi Level Kustom tidak valid melempar exception",
                FortressGuardV3Test::ujiLevelKustomParameterInvalidGagal);

        System.out.println("--------------------------------------------------");
        System.out.println("Ringkasan Uji: " + lulus + " LULUS, " + gagal + " GAGAL.");
        if (gagal > 0) {
            System.exit(1);
        }
    }

    private static void ujiBangunMenaraBerhasil() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananPermainan layanan = new LayananPermainan(repoSkor);
        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Armando_FG"), new KonfigurasiLevel("LVL-01", 200, 20, 3));

        boolean ok = layanan.bangunMenaraPadaPeta(sesi, new MenaraPemanah(2, 1));
        assertTrue(ok, "Pembangunan MenaraPemanah pada petak rumput (2,1) seharusnya berhasil");
        assertEquals(150, sesi.getKoin(), "Koin seharusnya berkurang dari 200 menjadi 150");
        assertEquals(1, sesi.getDaftarMenara().size(), "Daftar menara seharusnya berisi 1 menara");
    }

    private static void ujiBangunMenaraDiJalurGagal() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananPermainan layanan = new LayananPermainan(repoSkor);
        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Wahyu_FG"), new KonfigurasiLevel("LVL-01", 200, 20, 3));

        // Baris y = 2 adalah jalur musuh pada peta 10x5
        boolean ok = layanan.bangunMenaraPadaPeta(sesi, new MenaraMeriam(2, 2));
        assertFalse(ok, "Menara tidak boleh dibangun di atas petak jalur musuh (2,2)");
        assertEquals(200, sesi.getKoin(), "Koin tidak boleh berkurang saat pembangunan ditolak");
    }

    private static void ujiTempatkanMeleeKoinKurangGagal() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananPermainan layanan = new LayananPermainan(repoSkor);
        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Rifki_FG"), new KonfigurasiLevel("LVL-01", 50, 20, 3));

        // PenjagaPerisai berbiaya 80 koin, sementara saldo hanya 50
        boolean ok = layanan.tempatkanUnitMeleePadaJalur(sesi, new PenjagaPerisai(3, 2));
        assertFalse(ok, "Penempatan PenjagaPerisai (80 koin) seharusnya gagal saat koin hanya 50");
        assertEquals(0, sesi.getDaftarUnitMelee().size(), "Tidak boleh ada unit melee yang tersimpan");
    }

    private static void ujiUpgradeDanJualMenaraBerhasil() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananPermainan layanan = new LayananPermainan(repoSkor);
        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Armando_FG"), new KonfigurasiLevel("LVL-01", 300, 20, 3));

        Menara pemanah = new MenaraPemanah(1, 1);
        assertTrue(layanan.bangunMenaraPadaPeta(sesi, pemanah), "Bangun awal harus berhasil");

        int biayaUpgrade = pemanah.getBiayaUpgrade();
        assertTrue(layanan.upgradeMenara(sesi, pemanah, biayaUpgrade), "Upgrade menara harus berhasil");
        assertEquals(2, pemanah.getLevel(), "Level menara seharusnya naik ke 2");

        int koinSebelumJual = sesi.getKoin();
        int hargaJual = pemanah.hitungHargaJual();
        assertTrue(layanan.jualMenara(sesi, pemanah), "Jual menara harus berhasil");
        assertEquals(koinSebelumJual + hargaJual, sesi.getKoin(), "Koin harus bertambah sebesar harga jual");
        assertTrue(sesi.getPetaPermainan().cariPetak(1, 1).dapatDibangunMenara(), "Petak (1,1) harus kosong kembali");
    }

    private static void ujiUpgradeMenaraKoinKurangGagal() {
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananPermainan layanan = new LayananPermainan(repoSkor);
        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Wahyu_FG"), new KonfigurasiLevel("LVL-01", 60, 20, 3));

        Menara pemanah = new MenaraPemanah(1, 1);
        assertTrue(layanan.bangunMenaraPadaPeta(sesi, pemanah), "Bangun awal (50 koin) menyisakan 10 koin");
        boolean ok = layanan.upgradeMenara(sesi, pemanah, pemanah.getBiayaUpgrade());
        assertFalse(ok, "Upgrade seharusnya gagal karena koin tinggal 10");
        assertEquals(1, pemanah.getLevel(), "Level menara harus tetap 1");
    }

    private static void ujiPolimorfismeSeranganBerhasil() {
        Gelombang gel3 = new Gelombang(3);
        MusuhBiasa goblin = new MusuhBiasa(0.0, 2.0);
        MusuhBos bos = new MusuhBos(0.0, 2.0, 10);
        gel3.tambahMusuh(goblin);
        gel3.tambahMusuh(bos);
        gel3.picuEventKecepatan();

        Menara es = new MenaraEs(2, 1);
        DapatDiserang targetBos = bos;
        double spdSebelumEs = bos.getKecepatan();
        es.serang(targetBos, gel3.getDaftarMusuh());
        assertTrue(bos.getKecepatan() < spdSebelumEs, "Efek es harus memperlambat MusuhBos via DapatDiperlambat");
        assertEquals(295, bos.getHp(), "Armor 10 mengurangi damage 15 menjadi 5 (300 -> 295)");

        UnitMelee ksatria = new KsatriaPedang(2, 2);
        int hpGoblinSebelum = goblin.getHp();
        ksatria.bertarung(goblin);
        assertTrue(goblin.getHp() < hpGoblinSebelum, "KsatriaPedang harus mengurangi HP Goblin");
    }

    private static void ujiLevelKustomDanSkorBerhasil() {
        RepositoriLevel repoLevel = new RepositoriLevelMemori();
        RepositoriSkor repoSkor = new RepositoriSkorMemori();
        LayananLevelKustom layananLevel = new LayananLevelKustom(repoLevel);
        LayananPermainan layananPermainan = new LayananPermainan(repoSkor);

        KonfigurasiLevel kustom = layananLevel.muatKonfigurasiDefault("KUSTOM");
        layananLevel.perbaruiKonfigurasiKustom(kustom, 500, 5);
        assertEquals(500, layananLevel.muatKonfigurasiDefault("KUSTOM").getKoinAwal(), "Koin kustom harus tersimpan 500");
        assertEquals(5, layananLevel.muatKonfigurasiDefault("KUSTOM").getJumlahGelombang(), "Gelombang kustom harus 5");

        SesiPermainan sesi = new SesiPermainan(new ProfilPemain("Rifki_FG"), kustom);
        sesi.tambahKoinDanSkor(15, 200);
        layananPermainan.akhiriSesiDanSimpanSkor(sesi);
        assertEquals(1, layananPermainan.ambilDaftarSkorLokal().size(), "Skor akhir harus tersimpan di repositori");
    }

    private static void ujiLevelKustomParameterInvalidGagal() {
        RepositoriLevel repoLevel = new RepositoriLevelMemori();
        LayananLevelKustom layananLevel = new LayananLevelKustom(repoLevel);
        KonfigurasiLevel kustom = layananLevel.muatKonfigurasiDefault("KUSTOM");

        boolean melemparError = false;
        try {
            // Koin awal < 50 melanggar aturan bisnis
            layananLevel.perbaruiKonfigurasiKustom(kustom, 20, 0);
        } catch (IllegalArgumentException ex) {
            melemparError = true;
        }
        assertTrue(melemparError, "Parameter koin < 50 dan gelombang 0 harus melempar IllegalArgumentException");
    }

    private static void uji(String nama, Runnable test) {
        try {
            test.run();
            lulus++;
            System.out.println("[LULUS] " + nama);
        } catch (AssertionError | RuntimeException ex) {
            gagal++;
            System.out.println("[GAGAL] " + nama + " -> " + ex.getMessage());
        }
    }

    private static void assertTrue(boolean kondisi, String pesan) {
        if (!kondisi) throw new AssertionError(pesan);
    }

    private static void assertFalse(boolean kondisi, String pesan) {
        if (kondisi) throw new AssertionError(pesan);
    }

    private static void assertEquals(int expected, int actual, String pesan) {
        if (expected != actual) {
            throw new AssertionError(pesan + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }
}
