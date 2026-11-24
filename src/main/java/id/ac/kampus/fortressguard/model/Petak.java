package id.ac.kampus.fortressguard.model;

/**
 * Merepresentasikan satu petak grid 2D pada PetaPermainan (bagian Komposisi).
 */
public class Petak {
    private final int koordinatX;
    private final int koordinatY;
    private final boolean jalurMusuh;
    private boolean terisiUnit;

    public Petak(int koordinatX, int koordinatY, boolean jalurMusuh) {
        this.koordinatX = koordinatX;
        this.koordinatY = koordinatY;
        this.jalurMusuh = jalurMusuh;
        this.terisiUnit = false;
    }

    public int getKoordinatX() {
        return koordinatX;
    }

    public int getKoordinatY() {
        return koordinatY;
    }

    public boolean isJalurMusuh() {
        return jalurMusuh;
    }

    public boolean isTerisiUnit() {
        return terisiUnit;
    }

    public boolean dapatDibangunMenara() {
        return !jalurMusuh && !terisiUnit;
    }

    public boolean dapatDitempatkanMelee() {
        return jalurMusuh && !terisiUnit;
    }

    public void tandaiTerisi() {
        if (this.terisiUnit) {
            throw new IllegalStateException("Petak sudah terisi unit lain");
        }
        this.terisiUnit = true;
    }

    public void kosongkanPetak() {
        this.terisiUnit = false;
    }
}
