package id.ac.kampus.fortressguard.model;

import id.ac.kampus.fortressguard.strategy.StrategiSerangan;

import java.util.List;

/**
 * Kelas abstrak Menara pertahanan yang mendelegasikan efek serangan ke StrategiSerangan (OCP).
 * Seluruh atribut 100% private.
 */
public abstract class Menara {
    private final String nama;
    private final int posisiX;
    private final int posisiY;
    private final int biaya;
    private int totalInvestasi;
    private int dayaRusak;
    private double jangkauan;
    private int level;
    private final double jedaTembakDetik;
    private double sisaCooldown;
    private final StrategiSerangan strategiSerangan;

    public Menara(String nama, int posisiX, int posisiY, int biaya, int dayaRusak,
                  double jangkauan, double jedaTembakDetik, StrategiSerangan strategiSerangan) {
        if (strategiSerangan == null) {
            throw new IllegalArgumentException("Strategi serangan wajib diisi");
        }
        this.nama = nama;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.biaya = Math.max(10, biaya);
        this.totalInvestasi = this.biaya;
        this.dayaRusak = Math.max(1, dayaRusak);
        this.jangkauan = Math.max(1.0, jangkauan);
        this.level = 1;
        this.jedaTembakDetik = Math.max(0.2, jedaTembakDetik);
        this.sisaCooldown = 0.0;
        this.strategiSerangan = strategiSerangan;
    }

    public void serang(DapatDiserang targetUtama, List<Musuh> semuaMusuh) {
        this.strategiSerangan.lancarkanSerangan(targetUtama, semuaMusuh, this.dayaRusak);
    }

    public void tingkatkanLevel() {
        int biayaUpgrade = getBiayaUpgrade();
        this.totalInvestasi += biayaUpgrade;
        this.level++;
        this.dayaRusak += 12;
        this.jangkauan += 0.5;
    }

    public void kurangiCooldown(double deltaDetik) {
        if (sisaCooldown > 0) {
            sisaCooldown = Math.max(0.0, sisaCooldown - deltaDetik);
        }
    }

    public boolean siapMenembak() {
        return sisaCooldown <= 0.0;
    }

    public void resetCooldown() {
        this.sisaCooldown = this.jedaTembakDetik;
    }

    public int getBiayaUpgrade() {
        return 35 + (level * 25);
    }

    public int hitungHargaJual() {
        return (int) Math.round(totalInvestasi * 0.65);
    }

    public String getNama() {
        return nama;
    }

    public int getPosisiX() {
        return posisiX;
    }

    public int getPosisiY() {
        return posisiY;
    }

    public int getBiaya() {
        return biaya;
    }

    public int getDayaRusak() {
        return dayaRusak;
    }

    public double getJangkauan() {
        return jangkauan;
    }

    public int getLevel() {
        return level;
    }

    public double getJedaTembakDetik() {
        return jedaTembakDetik;
    }

    public StrategiSerangan getStrategiSerangan() {
        return strategiSerangan;
    }
}
