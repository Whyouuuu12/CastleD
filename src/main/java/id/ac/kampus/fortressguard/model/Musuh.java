package id.ac.kampus.fortressguard.model;

/**
 * Kelas abstrak Musuh yang mengimplementasikan DapatDiserang dan DapatDiperlambat.
 * Seluruh atribut 100% private.
 */
public abstract class Musuh implements DapatDiserang, DapatDiperlambat {
    private double posisiX;
    private double posisiY;
    private int hp;
    private final int hpMaks;
    private double kecepatan;
    private final double kecepatanDasar;
    private double pengaliEventAktif;
    private double faktorLambatAktif;
    private double sisaDurasiLambat;
    private final int hadiahKoin;
    private final int dayaSerangMarkas;
    private final int dayaSerangMelee;
    private double sisaCooldownSerang;
    private boolean sedangDihadang;
    private boolean sudahMencapaiMarkas;
    private boolean hadiahSudahDiberikan;

    public Musuh(double posisiX, double posisiY, int hp, double kecepatan,
                 int hadiahKoin, int dayaSerangMarkas) {
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.hp = Math.max(1, hp);
        this.hpMaks = this.hp;
        this.kecepatanDasar = Math.max(0.1, kecepatan);
        this.pengaliEventAktif = 1.0;
        this.faktorLambatAktif = 1.0;
        this.sisaDurasiLambat = 0.0;
        this.kecepatan = this.kecepatanDasar;
        this.hadiahKoin = Math.max(1, hadiahKoin);
        this.dayaSerangMarkas = Math.max(1, dayaSerangMarkas);
        this.dayaSerangMelee = Math.max(8, dayaSerangMarkas * 10);
        this.sisaCooldownSerang = 0.0;
        this.sedangDihadang = false;
        this.sudahMencapaiMarkas = false;
        this.hadiahSudahDiberikan = false;
    }

    public void bergerakMaju() {
        if (isHidup() && !sedangDihadang) {
            this.posisiX += this.kecepatan;
        }
    }

    public void bergerakMaju(double deltaDetik) {
        if (isHidup() && !sedangDihadang && deltaDetik > 0) {
            this.posisiX += this.kecepatan * deltaDetik;
        }
    }

    public void perbaruiStatusEfek(double deltaDetik) {
        if (sisaDurasiLambat > 0) {
            sisaDurasiLambat = Math.max(0.0, sisaDurasiLambat - deltaDetik);
            if (sisaDurasiLambat == 0.0) {
                faktorLambatAktif = 1.0;
                hitungUlangKecepatan();
            }
        }
        if (sisaCooldownSerang > 0) {
            sisaCooldownSerang = Math.max(0.0, sisaCooldownSerang - deltaDetik);
        }
    }

    private void hitungUlangKecepatan() {
        this.kecepatan = this.kecepatanDasar * this.pengaliEventAktif * this.faktorLambatAktif;
    }

    public void terapkanLonjakanKecepatan(double pengali) {
        if (pengali > 1.0) {
            this.pengaliEventAktif = pengali;
            hitungUlangKecepatan();
        }
    }

    @Override
    public void terapkanEfekLambat(double faktorLambat, int durasiDetik) {
        if (faktorLambat > 0.0 && faktorLambat < 1.0 && durasiDetik > 0) {
            this.faktorLambatAktif = Math.min(this.faktorLambatAktif, faktorLambat);
            this.sisaDurasiLambat = Math.max(this.sisaDurasiLambat, durasiDetik);
            hitungUlangKecepatan();
        }
    }

    @Override
    public void terimaKerusakan(int jumlah) {
        if (jumlah > 0) {
            this.hp = Math.max(0, this.hp - jumlah);
        }
    }

    public boolean siapSerangBalik() {
        return isHidup() && sisaCooldownSerang <= 0.0;
    }

    public void serangBalik(DapatDiserang targetUnit) {
        if (siapSerangBalik() && targetUnit != null && targetUnit.isHidup()) {
            targetUnit.terimaKerusakan(this.dayaSerangMelee);
            this.sisaCooldownSerang = 1.1;
        }
    }

    @Override
    public boolean isHidup() {
        return this.hp > 0;
    }

    @Override
    public int getHp() {
        return this.hp;
    }

    public int getHpMaks() {
        return this.hpMaks;
    }

    public double getPosisiX() {
        return posisiX;
    }

    public double getPosisiY() {
        return posisiY;
    }

    public double getKecepatan() {
        return kecepatan;
    }

    public boolean isSedangMelambat() {
        return sisaDurasiLambat > 0.0;
    }

    public boolean isEventCepatAktif() {
        return pengaliEventAktif > 1.0;
    }

    public int getHadiahKoin() {
        return hadiahKoin;
    }

    public int getDayaSerangMarkas() {
        return dayaSerangMarkas;
    }

    public boolean isSedangDihadang() {
        return sedangDihadang;
    }

    public void aturStatusDihadang(boolean dihadang) {
        this.sedangDihadang = dihadang;
    }

    public boolean isSudahMencapaiMarkas() {
        return sudahMencapaiMarkas;
    }

    public void tandaiMencapaiMarkas() {
        this.sudahMencapaiMarkas = true;
    }

    public boolean isHadiahSudahDiberikan() {
        return hadiahSudahDiberikan;
    }

    public void tandaiHadiahDiberikan() {
        this.hadiahSudahDiberikan = true;
    }

    public abstract String getNamaTipe();
}
