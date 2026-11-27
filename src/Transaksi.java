import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Entitas Transaksi Penyewaan Perlengkapan Acara EventRent (FR-TRX-01 s/d FR-TRX-08, 4.5).
 * Menyimpan data penyewa, daftar paket barang yang disewa, durasi sewa, tanggal penting,
 * status transaksi, metode pembayaran, denda, dan nomor invoice.
 */
public class Transaksi {

    private String id;
    private Penyewa penyewa;
    private ArrayList<Barang> barang;
    private int lamaSewa;
    private LocalDate tanggalSewa;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private StatusTransaksi status;
    private Pembayaran pembayaran;
    private double denda;
    private String nomorInvoice;

    /**
     * Konstruktor untuk pembuatan transaksi baru oleh penyewa saat checkout.
     *
     * @param id         ID transaksi (misal: TRX-001)
     * @param penyewa    objek penyewa yang memesan
     * @param barang     daftar paket barang yang disewa
     * @param lamaSewa   lama sewa dalam hari (1-30 hari)
     * @param pembayaran objek metode pembayaran yang dipilih
     * @throws InputTidakValidException jika lama sewa di luar rentang 1-30 atau data tidak valid
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<Barang> barang, int lamaSewa, Pembayaran pembayaran)
            throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID transaksi tidak boleh kosong.");
        }
        if (penyewa == null) {
            throw new InputTidakValidException("Penyewa transaksi harus ditentukan.");
        }
        if (barang == null || barang.isEmpty()) {
            throw new InputTidakValidException("Daftar barang sewa tidak boleh kosong.");
        }
        if (lamaSewa < 1 || lamaSewa > 30) {
            throw new InputTidakValidException("Lama sewa harus berada dalam rentang 1 sampai 30 hari.");
        }
        if (pembayaran == null) {
            throw new InputTidakValidException("Metode pembayaran harus dipilih.");
        }

        this.id = id.trim();
        this.penyewa = penyewa;
        this.barang = new ArrayList<>(barang);
        this.lamaSewa = lamaSewa;
        this.pembayaran = pembayaran;
        this.status = StatusTransaksi.MENUNGGU;
        this.denda = 0.0;
        this.nomorInvoice = "";
    }

    /**
     * Konstruktor lengkap untuk memuat data transaksi dari file teks penyimpanan.
     *
     * @param id               ID transaksi
     * @param penyewa          objek penyewa
     * @param barang           daftar paket barang
     * @param lamaSewa         lama sewa (hari)
     * @param tanggalSewa      tanggal transaksi diterima
     * @param tanggalJatuhTempo tanggal batas sewa
     * @param tanggalKembali   tanggal aktual pengembalian
     * @param status           status transaksi
     * @param pembayaran      objek metode pembayaran
     * @param denda            nominal denda
     * @param nomorInvoice     nomor invoice tercetak
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<Barang> barang, int lamaSewa,
                     LocalDate tanggalSewa, LocalDate tanggalJatuhTempo, LocalDate tanggalKembali,
                     StatusTransaksi status, Pembayaran pembayaran, double denda, String nomorInvoice) {
        this.id = id;
        this.penyewa = penyewa;
        this.barang = (barang != null) ? barang : new ArrayList<>();
        this.lamaSewa = lamaSewa;
        this.tanggalSewa = tanggalSewa;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.tanggalKembali = tanggalKembali;
        this.status = (status != null) ? status : StatusTransaksi.MENUNGGU;
        this.pembayaran = pembayaran;
        this.denda = denda;
        this.nomorInvoice = (nomorInvoice != null) ? nomorInvoice : "";
    }

    /**
     * Menghitung total biaya sewa dari seluruh barang untuk durasi sewa yang dipesan (FR-BRG-01).
     *
     * @return total biaya sewa dalam rupiah
     */
    public double hitungTotal() {
        double total = 0.0;
        for (Barang b : barang) {
            total += b.hitungBiaya(this.lamaSewa);
        }
        return total;
    }

    /**
     * Menghitung selisih hari keterlambatan pengembalian (FR-TRX-06).
     * {@code hariTerlambat = max(0, ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalKembali))}.
     *
     * @return jumlah hari keterlambatan (0 jika tepat waktu atau belum kembali)
     */
    public int hitungHariTerlambat() {
        if (this.tanggalJatuhTempo == null || this.tanggalKembali == null) {
            return 0;
        }
        long selisih = ChronoUnit.DAYS.between(this.tanggalJatuhTempo, this.tanggalKembali);
        return (int) Math.max(0, selisih);
    }

    /**
     * Menghitung total denda keterlambatan (FR-TRX-05).
     * Denda = {@code hariTerlambat * sum(0,5 * tarifHarian tiap barang)}, tanpa biaya pemasangan/teknisi.
     *
     * @return total denda dalam rupiah
     */
    public double hitungDenda() {
        int hariTerlambat = hitungHariTerlambat();
        if (hariTerlambat <= 0) {
            return 0.0;
        }
        double dendaHarianSemuaBarang = 0.0;
        for (Barang b : barang) {
            dendaHarianSemuaBarang += b.hitungDendaPerHari();
        }
        return hariTerlambat * dendaHarianSemuaBarang;
    }

    /**
     * Mengubah status transaksi ke status baru.
     *
     * @param s status baru
     */
    public void ubahStatus(StatusTransaksi s) {
        this.status = s;
    }

    // --- Getter dan Setter ---

    /**
     * Mengambil ID transaksi.
     * @return ID transaksi
     */
    public String getId() {
        return id;
    }

    /**
     * Mengambil objek penyewa transaksi.
     * @return penyewa
     */
    public Penyewa getPenyewa() {
        return penyewa;
    }

    /**
     * Mengatur objek penyewa transaksi.
     * @param penyewa penyewa baru
     */
    public void setPenyewa(Penyewa penyewa) {
        this.penyewa = penyewa;
    }

    /**
     * Mengambil daftar barang dalam transaksi.
     * @return list barang
     */
    public ArrayList<Barang> getBarang() {
        return barang;
    }

    /**
     * Mengambil lama durasi sewa dalam hari.
     * @return lama sewa
     */
    public int getLamaSewa() {
        return lamaSewa;
    }

    /**
     * Mengatur lama durasi sewa dalam hari.
     * @param lamaSewa lama sewa baru
     */
    public void setLamaSewa(int lamaSewa) {
        this.lamaSewa = lamaSewa;
    }

    /**
     * Mengambil tanggal mulai sewa.
     * @return tanggal sewa
     */
    public LocalDate getTanggalSewa() {
        return tanggalSewa;
    }

    /**
     * Mengatur tanggal mulai sewa.
     * @param tanggalSewa tanggal sewa baru
     */
    public void setTanggalSewa(LocalDate tanggalSewa) {
        this.tanggalSewa = tanggalSewa;
    }

    /**
     * Mengambil tanggal batas pengembalian sewa.
     * @return tanggal jatuh tempo
     */
    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    /**
     * Mengatur tanggal batas pengembalian sewa.
     * @param tanggalJatuhTempo jatuh tempo baru
     */
    public void setTanggalJatuhTempo(LocalDate tanggalJatuhTempo) {
        this.tanggalJatuhTempo = tanggalJatuhTempo;
    }

    /**
     * Mengambil tanggal aktual pengembalian barang.
     * @return tanggal kembali
     */
    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    /**
     * Mengatur tanggal aktual pengembalian barang.
     * @param tanggalKembali tanggal kembali baru
     */
    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    /**
     * Mengambil status transaksi saat ini.
     * @return status transaksi
     */
    public StatusTransaksi getStatus() {
        return status;
    }

    /**
     * Mengatur status transaksi saat ini.
     * @param status status baru
     */
    public void setStatus(StatusTransaksi status) {
        this.status = status;
    }

    /**
     * Mengambil objek metode pembayaran transaksi.
     * @return metode pembayaran
     */
    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    /**
     * Mengatur objek metode pembayaran transaksi.
     * @param pembayaran pembayaran baru
     */
    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    /**
     * Mengambil nominal denda keterlambatan.
     * @return denda
     */
    public double getDenda() {
        return denda;
    }

    /**
     * Mengatur nominal denda keterlambatan.
     * @param denda denda baru
     */
    public void setDenda(double denda) {
        this.denda = denda;
    }

    /**
     * Mengambil nomor invoice transaksi.
     * @return nomor invoice
     */
    public String getNomorInvoice() {
        return nomorInvoice;
    }

    /**
     * Mengatur nomor invoice transaksi.
     * @param nomorInvoice nomor invoice baru
     */
    public void setNomorInvoice(String nomorInvoice) {
        this.nomorInvoice = nomorInvoice;
    }
}

/**
 * Keranjang belanja sewa perlengkapan acara in-memory milik Penyewa (FR-PNY-04, FR-PNY-05).
 * Menampung daftar barang yang akan disewa oleh penyewa dalam satu sesi.
 */
class KeranjangSewa {

    private ArrayList<Barang> daftarBarang;

    /**
     * Konstruktor default KeranjangSewa.
     */
    public KeranjangSewa() {
        this.daftarBarang = new ArrayList<>();
    }

    /**
     * Menambahkan paket barang ke dalam keranjang belanja sewa (FR-PNY-04).
     * Validasi:
     * 1. Barang tidak boleh null.
     * 2. Barang yang sedang DISEWA ditolak (melempar BarangTidakTersediaException).
     * 3. Barang yang sudah ada dalam keranjang tidak boleh ditambahkan dua kali (duplikat ditolak).
     *
     * @param b objek Barang yang akan disewa
     * @throws BarangTidakTersediaException jika status barang sedang DISEWA
     * @throws InputTidakValidException      jika barang duplikat sudah ada di keranjang
     */
    public void tambahBarang(Barang b) throws BarangTidakTersediaException, InputTidakValidException {
        if (b == null) {
            throw new InputTidakValidException("Barang tidak valid.");
        }
        if (b.getStatus() == StatusBarang.DISEWA) {
            throw new BarangTidakTersediaException(
                    "Barang '" + b.getNama() + "' (" + b.getId() + ") saat ini sedang DISEWA oleh pelanggan lain!");
        }

        // Cek duplikasi di keranjang
        for (Barang item : daftarBarang) {
            if (item.getId().equalsIgnoreCase(b.getId())) {
                throw new InputTidakValidException(
                        "Barang '" + b.getNama() + "' sudah ada di dalam keranjang Anda.");
            }
        }

        daftarBarang.add(b);
    }

    /**
     * Menghapus barang dari keranjang berdasarkan ID barang (FR-PNY-05).
     *
     * @param id ID barang yang ingin dihapus
     * @throws BarangTidakDitemukanException jika barang tidak ada dalam keranjang
     */
    public void hapusBarang(String id) throws BarangTidakDitemukanException {
        if (id == null || id.trim().isEmpty()) {
            throw new BarangTidakDitemukanException("ID barang tidak boleh kosong.");
        }
        for (int i = 0; i < daftarBarang.size(); i++) {
            if (daftarBarang.get(i).getId().equalsIgnoreCase(id.trim())) {
                daftarBarang.remove(i);
                return;
            }
        }
        throw new BarangTidakDitemukanException("Barang dengan ID '" + id + "' tidak ditemukan dalam keranjang.");
    }

    /**
     * Mengosongkan seluruh isi keranjang sewa.
     */
    public void kosongkan() {
        daftarBarang.clear();
    }

    /**
     * Memeriksa apakah keranjang dalam keadaan kosong.
     *
     * @return true jika kosong, false jika ada isi barang
     */
    public boolean isKosong() {
        return daftarBarang.isEmpty();
    }

    /**
     * Mengambil salinan daftar barang yang ada di keranjang.
     *
     * @return list barang
     */
    public ArrayList<Barang> getBarang() {
        return new ArrayList<>(daftarBarang);
    }

    /**
     * Menghitung total estimasi biaya sewa seluruh barang di keranjang untuk durasi hari tertentu.
     *
     * @param hari durasi lama sewa
     * @return total estimasi biaya dalam rupiah
     */
    public double hitungTotal(int hari) {
        double total = 0.0;
        for (Barang b : daftarBarang) {
            total += b.hitungBiaya(hari);
        }
        return total;
    }

    /**
     * Mengembalikan jumlah item paket barang yang ada dalam keranjang.
     *
     * @return jumlah item
     */
    public int jumlahBarang() {
        return daftarBarang.size();
    }
}


