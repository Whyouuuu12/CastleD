package id.ac.kampus.fortressguard.model;

/**
 * Kelas abstrak UnitMelee yang ditempatkan di petak jalur untuk menghadang musuh.
 * Hanya mengimplementasikan DapatDiserang (memenuhi ISP & LSP).
 */
public abstract class UnitMelee implements DapatDiserang {
    private final String nama;
    private final int posisiX;
    private final int posisiY;
    private int hp;
    private int hpMaks;
    private int dayaRusakMelee;
    private final int biaya;
    private int level;
    private double sisaCooldownSerang;
    private final double jedaSerangDetik;

    public UnitMelee(String nama, int posisiX, int posisiY, int hp, int dayaRusakMelee,
                     int biaya, double jedaSerangDetik) {
        this.nama = nama;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.hp = Math.max(1, hp);
        this.hpMaks = this.hp;
        this.dayaRusakMelee = Math.max(1, dayaRusakMelee);
        this.biaya = Math.max(10, biaya);
        this.level = 1;
        this.jedaSerangDetik = Math.max(0.2, jedaSerangDetik);
        this.sisaCooldownSerang = 0.0;
    }

    public void tingkatkanLevel() {
        this.level++;
        this.dayaRusakMelee += 10;
        this.hpMaks += 40;
        this.hp = Math.min(this.hpMaks, this.hp + 40);
    }

    public void kurangiCooldown(double deltaDetik) {
        if (sisaCooldownSerang > 0) {
            sisaCooldownSerang = Math.max(0.0, sisaCooldownSerang - deltaDetik);
        }
    }

    public boolean siapBertarung() {
        return isHidup() && sisaCooldownSerang <= 0.0;
    }

    protected void resetCooldownSerang() {
        this.sisaCooldownSerang = this.jedaSerangDetik;
    }

    @Override
    public void terimaKerusakan(int jumlah) {
        if (jumlah > 0) {
            this.hp = Math.max(0, this.hp - jumlah);
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

    public String getNama() {
        return nama;
    }

    public int getPosisiX() {
        return posisiX;
    }

    public int getPosisiY() {
        return posisiY;
    }

    public int getDayaRusakMelee() {
        return dayaRusakMelee;
    }

    public int getBiaya() {
        return biaya;
    }

    public int getBiayaUpgrade() {
        return 40 + (level * 25);
    }

    public int getLevel() {
        return level;
    }

    public int getKapasitasHadangEfektif() {
        return 1;
    }

    public abstract void bertarung(Musuh target);
}
