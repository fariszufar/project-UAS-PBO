/**
 * Interface abstraksi metode pembayaran sewa perlengkapan acara (FR-BYR-01, 4.6).
 */
public interface Pembayaran {

    /**
     * Memproses transaksi pembayaran sejumlah nominal yang ditagihkan.
     */
    void prosesPembayaran(double jumlah) throws InputTidakValidException;

    /**
     * Mengembalikan nama metode pembayaran (contoh: "Transfer Bank", "QRIS", "Tunai").
     */
    String getNamaMetode();
}

/**
 * Implementasi metode pembayaran Transfer Bank (FR-BYR-02, 4.6).
 */
class TransferBank implements Pembayaran {

    private static final String REKENING_TOKO = "BCA 1234567890 a.n. EventRent";

    /**
     * Konstruktor default TransferBank.
     */
    public TransferBank() {
    }

    /**
     * Menampilkan instruksi rekening bank dan meminta konfirmasi penyelesaian transfer (y/n).
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Nominal pembayaran harus lebih besar dari Rp0.");
        }
        System.out.println("\n---------------- PEMBAYARAN TRANSFER BANK ----------------");
        System.out.println("  Rekening Tujuan : " + REKENING_TOKO);
        System.out.println("  Total Tagihan   : " + InputHelper.formatRupiah(jumlah));
        System.out.println("  Petunjuk        : Transfer sesuai nominal di atas.");
        System.out.println("----------------------------------------------------------");

        boolean konfirm = InputHelper.bacaKonfirmasi("Apakah Anda sudah menyelesaikan transfer bank?");
        if (!konfirm) {
            throw new InputTidakValidException("Pembayaran transfer bank belum diselesaikan atau dibatalkan.");
        }
        System.out.println("[SUKSES] Pembayaran Transfer Bank berhasil diverifikasi!");
    }

    @Override
    public String getNamaMetode() {
        return "Transfer Bank";
    }
}

/**
 * Implementasi metode pembayaran QRIS (FR-BYR-02, 4.6).
 */
class QRIS implements Pembayaran {

    /**
     * Konstruktor default QRIS.
     */
    public QRIS() {
    }

    /**
     * Menampilkan simulasi barcode QRIS teks dan meminta konfirmasi pembayaran (y/n).
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Nominal pembayaran harus lebih besar dari Rp0.");
        }
        System.out.println("\n-------------------- PEMBAYARAN QRIS --------------------");
        System.out.println("  Pindai kode QRIS berikut melalui Mobile Banking / e-Wallet:");
        System.out.println("  +-----------------------------+");
        System.out.println("  | [X] [X] [ ] [ ] [ ] [X] [X] |");
        System.out.println("  | [X] [ ] [X] [ ] [X] [ ] [X] |");
        System.out.println("  | [ ] [ ] [ ] [X] [ ] [ ] [ ] |");
        System.out.println("  | [ ] [X] [X] [ ] [X] [X] [ ] |");
        System.out.println("  | [X] [ ] [ ] [X] [ ] [ ] [X] |");
        System.out.println("  +-----------------------------+");
        System.out.println("  NMID           : ID1982736450 EventRent");
        System.out.println("  Total Tagihan  : " + InputHelper.formatRupiah(jumlah));
        System.out.println("---------------------------------------------------------");

        boolean konfirm = InputHelper.bacaKonfirmasi("Apakah pembayaran via QRIS sudah berhasil?");
        if (!konfirm) {
            throw new InputTidakValidException("Pembayaran QRIS belum diselesaikan atau dibatalkan.");
        }
        System.out.println("[SUKSES] Pembayaran QRIS berhasil diverifikasi!");
    }

    @Override
    public String getNamaMetode() {
        return "QRIS";
    }
}

/**
 * Implementasi metode pembayaran Tunai / Cash (FR-BYR-02, 4.6).
 */
class Tunai implements Pembayaran {

    /**
     * Konstruktor default Tunai.
     */
    public Tunai() {
    }

    /**
     * Meminta nominal uang tunai yang diterima, memvalidasi kecukupan, dan menampilkan kembalian.
     */
    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Nominal pembayaran harus lebih besar dari Rp0.");
        }
        System.out.println("\n-------------------- PEMBAYARAN TUNAI -------------------");
        System.out.println("  Total Tagihan   : " + InputHelper.formatRupiah(jumlah));
        System.out.println("  Petunjuk        : Pembayaran tunai dilakukan langsung di kasir toko.");
        System.out.println("---------------------------------------------------------");

        double uangDiterima = InputHelper.bacaDouble("Masukkan jumlah uang tunai yang diterima: ", 1, 100_000_000);
        if (uangDiterima < jumlah) {
            double kurang = jumlah - uangDiterima;
            throw new InputTidakValidException(
                    "Uang tunai kurang sebesar " + InputHelper.formatRupiah(kurang) + "! Pembayaran dibatalkan.");
        }

        double kembalian = uangDiterima - jumlah;
        System.out.println("  Uang Diterima   : " + InputHelper.formatRupiah(uangDiterima));
        System.out.println("  Uang Kembalian  : " + InputHelper.formatRupiah(kembalian));
        System.out.println("[SUKSES] Pembayaran tunai berhasil diterima!");
    }

    @Override
    public String getNamaMetode() {
        return "Tunai";
    }
}
