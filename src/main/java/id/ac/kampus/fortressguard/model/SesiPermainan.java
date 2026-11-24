package id.ac.kampus.fortressguard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Entitas inti status sesi permainan (SRP: fokus menyimpan state & invarian domain).
 * Komposisi (◆) terhadap PetaPermainan & Gelombang; Agregasi (◇) terhadap Menara & UnitMelee.
 */
public class SesiPermainan {
    private final ProfilPemain profilPemain;
    private final KonfigurasiLevel konfigurasiLevel;
    private final PetaPermainan petaPermainan;
    private final List<Gelombang> daftarGelombang;
    private final List<Menara> daftarMenara;
    private final List<UnitMelee> daftarUnitMelee;
    private int koin;
    private int nyawaMarkas;
    private final int nyawaMarkasMaks;
    private int skor;
    private int musuhDikalahkan;
    private int indeksGelombangAktif;

    public SesiPermainan(ProfilPemain profilPemain, KonfigurasiLevel konfigurasi) {
        if (profilPemain == null || konfigurasi == null) {
            throw new IllegalArgumentException("Profil pemain dan konfigurasi level wajib diisi");
        }
        this.profilPemain = profilPemain;
        this.konfigurasiLevel = konfigurasi;
        this.koin = konfigurasi.getKoinAwal();
        this.nyawaMarkas = konfigurasi.getNyawaMarkasAwal();
        this.nyawaMarkasMaks = konfigurasi.getNyawaMarkasAwal();
        this.skor = 0;
        this.musuhDikalahkan = 0;
        this.indeksGelombangAktif = 0;
        this.petaPermainan = new PetaPermainan(10, 5);
        this.daftarGelombang = new ArrayList<>();
        for (int i = 1; i <= konfigurasi.getJumlahGelombang(); i++) {
            this.daftarGelombang.add(new Gelombang(i));
        }
        this.daftarMenara = new ArrayList<>();
        this.daftarUnitMelee = new ArrayList<>();
    }

    public void kurangiKoin(int jumlah) {
        if (jumlah <= 0 || this.koin < jumlah) {
            throw new IllegalStateException("Koin tidak mencukupi");
        }
        this.koin -= jumlah;
    }

    public void tambahKoin(int jumlah) {
        if (jumlah > 0) {
            this.koin += jumlah;
        }
    }

    public void tambahKoinDanSkor(int tambahanKoin, int tambahanSkor) {
        if (tambahanKoin > 0) {
            this.koin += tambahanKoin;
        }
        if (tambahanSkor > 0) {
            this.skor += tambahanSkor;
        }
    }

    public void catatMusuhDikalahkan() {
        this.musuhDikalahkan++;
    }

    public void kurangiNyawaMarkas(int dayaSerang) {
        if (dayaSerang > 0) {
            this.nyawaMarkas = Math.max(0, this.nyawaMarkas - dayaSerang);
        }
    }

    public void simpanMenara(Menara m) {
        if (m != null) {
            this.daftarMenara.add(m);
        }
    }

    public boolean hapusMenara(Menara m) {
        return this.daftarMenara.remove(m);
    }

    public void simpanUnitMelee(UnitMelee u) {
        if (u != null) {
            this.daftarUnitMelee.add(u);
        }
    }

    public List<UnitMelee> bersihkanUnitMeleeGugur() {
        List<UnitMelee> gugur = new ArrayList<>();
        Iterator<UnitMelee> it = daftarUnitMelee.iterator();
        while (it.hasNext()) {
            UnitMelee u = it.next();
            if (!u.isHidup()) {
                gugur.add(u);
                it.remove();
            }
        }
        return gugur;
    }

    public void majuKeGelombangBerikutnya() {
        if (indeksGelombangAktif < daftarGelombang.size()) {
            indeksGelombangAktif++;
        }
    }

    public boolean isKalah() {
        return nyawaMarkas <= 0;
    }

    public boolean isSemuaGelombangSelesai() {
        return indeksGelombangAktif >= daftarGelombang.size();
    }

    public int hitungSkorAkhir() {
        return skor + (nyawaMarkas * 25) + (koin / 2);
    }

    public ProfilPemain getProfilPemain() {
        return profilPemain;
    }

    public KonfigurasiLevel getKonfigurasiLevel() {
        return konfigurasiLevel;
    }

    public PetaPermainan getPetaPermainan() {
        return petaPermainan;
    }

    public List<Gelombang> getDaftarGelombang() {
        return Collections.unmodifiableList(daftarGelombang);
    }

    public List<Menara> getDaftarMenara() {
        return Collections.unmodifiableList(daftarMenara);
    }

    public List<UnitMelee> getDaftarUnitMelee() {
        return Collections.unmodifiableList(daftarUnitMelee);
    }

    public int getKoin() {
        return koin;
    }

    public int getNyawaMarkas() {
        return nyawaMarkas;
    }

    public int getNyawaMarkasMaks() {
        return nyawaMarkasMaks;
    }

    public int getSkor() {
        return skor;
    }

    public int getMusuhDikalahkan() {
        return musuhDikalahkan;
    }

    public int getIndeksGelombangAktif() {
        return indeksGelombangAktif;
    }
}
