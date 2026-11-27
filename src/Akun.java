import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Kelas abstrak dasar pengguna sistem EventRent (FR-AKN-01, 7).
 * Menyediakan enkapsulasi atribut kredensial serta verifikasi login.
 *
 * @author Muhammad Faris Zufar (250810701100058)
 * @version 1.0
 */
public abstract class Akun {

    private String id;
    private String username;
    private String password;
    private String nama;

    /**
     * Konstruktor dasar untuk Akun.
     *
     * @param id       ID akun (misal: AKN-001)
     * @param username username login (unik, tanpa spasi)
     * @param password kata sandi (minimal 4 karakter)
     * @param nama     nama lengkap pengguna
     * @throws InputTidakValidException jika validasi atribut gagal
     */
    public Akun(String id, String username, String password, String nama)
            throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID akun tidak boleh kosong.");
        }
        setUsername(username);
        setPassword(password);
        setNama(nama);
        this.id = id.trim();
    }

    /**
     * Memverifikasi kecocokan username (case-insensitive) dan password (case-sensitive).
     *
     * @param inputUser username masukan
     * @param inputPass password masukan
     * @return true jika kredensial cocok
     */
    public boolean login(String inputUser, String inputPass) {
        if (inputUser == null || inputPass == null) {
            return false;
        }
        return this.username.equalsIgnoreCase(inputUser.trim()) && this.password.equals(inputPass);
    }

    /**
     * Menampilkan menu interaktif utama setelah login sesuai peran pengguna.
     */
    public abstract void tampilkanMenu();

    /**
     * Mengembalikan nama peran akun ("ADMIN" atau "PENYEWA").
     *
     * @return label peran
     */
    public abstract String getPeran();

    // --- Getter dan Setter dengan Validasi ---

    /**
     * Mengambil ID akun.
     * @return ID akun
     */
    public String getId() {
        return id;
    }

    /**
     * Mengatur ID akun.
     * @param id ID baru
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Mengambil username akun.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Mengatur username akun.
     * @param username username baru
     * @throws InputTidakValidException jika username tidak valid
     */
    public void setUsername(String username) throws InputTidakValidException {
        if (username == null || username.trim().isEmpty()) {
            throw new InputTidakValidException("Username tidak boleh kosong.");
        }
        if (username.contains(" ") || username.contains("|")) {
            throw new InputTidakValidException("Username tidak boleh mengandung spasi atau karakter '|'.");
        }
        this.username = username.trim();
    }

    /**
     * Mengambil password akun.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mengatur password akun.
     * @param password password baru
     * @throws InputTidakValidException jika password &lt; 4 karakter
     */
    public void setPassword(String password) throws InputTidakValidException {
        if (password == null || password.length() < 4) {
            throw new InputTidakValidException("Password minimal harus 4 karakter.");
        }
        this.password = password;
    }

    /**
     * Mengambil nama lengkap pemilik akun.
     * @return nama lengkap
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengatur nama lengkap pemilik akun.
     * @param nama nama baru
     * @throws InputTidakValidException jika nama tidak valid
     */
    public void setNama(String nama) throws InputTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama tidak boleh kosong.");
        }
        if (nama.contains("|")) {
            throw new InputTidakValidException("Nama tidak boleh mengandung karakter '|'.");
        }
        this.nama = nama.trim();
    }
}

/**
 * Entitas Administrator EventRent (FR-ADM-01 s/d FR-ADM-10, 4.2).
 * Bertanggung jawab mengelola katalog barang, memproses persetujuan transaksi,
 * mengonfirmasi pengembalian beserta denda, serta melihat laporan pendapatan toko.
 *
 * @author Muhammad Faris Zufar (250810701100058)
 * @version 1.0
 */
class Admin extends Akun {

    private DataStore dataStore;

    /**
     * Konstruktor Admin.
     *
     * @param id        ID akun admin (misal: AKN-001)
     * @param username  username admin
     * @param password  kata sandi admin
     * @param nama      nama lengkap admin
     * @param dataStore referensi pengelola penyimpanan data
     * @throws InputTidakValidException jika validasi kredensial gagal
     */
    public Admin(String id, String username, String password, String nama, DataStore dataStore)
            throws InputTidakValidException {
        super(id, username, password, nama);
        this.dataStore = dataStore;
    }

    /** {@inheritDoc} */
    @Override
    public String getPeran() {
        return "ADMIN";
    }

    /**
     * {@inheritDoc}
     * Menampilkan menu interaktif Administrator (Bagian 5.1 PRD).
     */
    @Override
    public void tampilkanMenu() {
        boolean aktif = true;
        while (aktif) {
            InputHelper.cetakHeader("PANEL ADMINISTRATOR - " + getNama());
            System.out.println("  1. Kelola Barang (Tambah / Edit / Hapus / Lihat)");
            System.out.println("  2. Cari Barang / Filter Kategori");
            System.out.println("  3. Transaksi Menunggu -> Terima / Tolak");
            System.out.println("  4. Transaksi Disewa -> Konfirmasi Pengembalian");
            System.out.println("  5. Lihat Semua Transaksi");
            System.out.println("  6. Laporan Pendapatan Toko");
            System.out.println("  0. Logout (Kembali ke Menu Utama)");
            InputHelper.cetakGaris('-', 75);

            int pilihan = InputHelper.bacaInteger("Pilih menu: ", 0, 6);
            switch (pilihan) {
                case 1:
                    kelolaBarang();
                    break;
                case 2:
                    menuCariFilterBarang();
                    break;
                case 3:
                    menuTransaksiMenunggu();
                    break;
                case 4:
                    menuKonfirmasiPengembalian();
                    break;
                case 5:
                    menuLihatSemuaTransaksi();
                    break;
                case 6:
                    menuLaporanPendapatan();
                    break;
                case 0:
                    aktif = false;
                    System.out.println("\n[INFO] Logout berhasil. Kembali ke Menu Utama EventRent.");
                    break;
            }
        }
    }

    // ==========================================
    // 1. KELOLA BARANG
    // ==========================================

    /**
     * Submenu pengelolaan barang: lihat, tambah, edit, dan hapus (FR-ADM-01 s/d FR-ADM-03).
     */
    public void kelolaBarang() {
        boolean sub = true;
        while (sub) {
            InputHelper.cetakHeader("KELOLA BARANG PERLENGKAPAN ACARA");
            System.out.println("  1. Lihat Seluruh Barang");
            System.out.println("  2. Tambah Barang Baru");
            System.out.println("  3. Edit Data Barang");
            System.out.println("  4. Hapus Barang");
            System.out.println("  0. Kembali ke Menu Admin");
            InputHelper.cetakGaris('-', 75);

            int pil = InputHelper.bacaInteger("Pilih aksi: ", 0, 4);
            switch (pil) {
                case 1:
                    tampilkanTabelBarang(dataStore.getDaftarBarang().getSemuaBarang(), "DAFTAR SELURUH BARANG EVENTRENT");
                    InputHelper.tungguEnter();
                    break;
                case 2:
                    aksiTambahBarang();
                    break;
                case 3:
                    aksiEditBarang();
                    break;
                case 4:
                    aksiHapusBarang();
                    break;
                case 0:
                    sub = false;
                    break;
            }
        }
    }

    private void aksiTambahBarang() {
        InputHelper.cetakHeader("TAMBAH BARANG PERLENGKAPAN BARU");
        System.out.println("  Pilih Kategori Barang:");
        System.out.println("  1. Perabot (Kursi, Meja, Sofa)");
        System.out.println("  2. Tenda (Terop, Sarnafil)");
        System.out.println("  3. Sound System");
        System.out.println("  0. Batal");
        InputHelper.cetakGaris('-', 75);

        int kat = InputHelper.bacaInteger("Pilihan kategori: ", 0, 3);
        if (kat == 0) return;

        try {
            String nama = InputHelper.bacaStringAman("Nama paket barang: ");
            double tarif = InputHelper.bacaDouble("Tarif sewa per hari (Rp): ", 1, 100_000_000);
            String idBaru = IdGenerator.generateIdBarang();

            Barang baru = null;
            if (kat == 1) {
                String jenis = InputHelper.bacaStringAman("Jenis perabot (misal: Kursi, Meja, Sofa): ");
                int unit = InputHelper.bacaInteger("Jumlah isi unit per paket: ", 1, 10_000);
                baru = new Perabot(idBaru, nama, tarif, StatusBarang.TERSEDIA, jenis, unit);
            } else if (kat == 2) {
                String ukuran = InputHelper.bacaStringAman("Ukuran tenda (misal: 4x6 m, 6x12 m): ");
                int kap = InputHelper.bacaInteger("Kapasitas orang: ", 1, 10_000);
                baru = new Tenda(idBaru, nama, tarif, StatusBarang.TERSEDIA, ukuran, kap);
            } else if (kat == 3) {
                int daya = InputHelper.bacaInteger("Daya sound system (Watt): ", 1, 100_000);
                int mic = InputHelper.bacaInteger("Jumlah mikrofon: ", 0, 100);
                baru = new SoundSystem(idBaru, nama, tarif, StatusBarang.TERSEDIA, daya, mic);
            }

            if (baru != null) {
                dataStore.getDaftarBarang().tambahBarang(baru);
                dataStore.simpanBarang();
                System.out.println("\n[SUKSES] Barang berhasil ditambahkan dengan ID: " + baru.getId());
            }
        } catch (InputTidakValidException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    private void aksiEditBarang() {
        tampilkanTabelBarang(dataStore.getDaftarBarang().getSemuaBarang(), "PILIH BARANG UNTUK DIEDIT");
        String id = InputHelper.bacaString("Masukkan ID Barang yang ingin diedit (atau 0 untuk batal): ");
        if ("0".equals(id)) return;

        try {
            Barang b = dataStore.getDaftarBarang().cariById(id);
            if (b.getStatus() == StatusBarang.DISEWA) {
                throw new BarangTidakTersediaException(
                        "Barang '" + b.getNama() + "' (" + b.getId() + ") sedang DISEWA sehingga tidak boleh diedit!");
            }

            InputHelper.cetakHeader("EDIT DATA BARANG: " + b.getId() + " - " + b.getNama());
            System.out.println("Tekan ENTER tanpa mengisi jika tidak ingin mengubah nilai atribut.");

            String namaBaru = InputHelper.bacaString("Nama barang baru [" + b.getNama() + "]: ");
            if (!namaBaru.isEmpty()) {
                if (namaBaru.contains("|")) {
                    throw new InputTidakValidException("Nama tidak boleh mengandung karakter pipe ('|').");
                }
                b.setNama(namaBaru);
            }

            String tarifStr = InputHelper.bacaString("Tarif per hari baru [" + (long) b.getTarifHarian() + "]: ");
            if (!tarifStr.isEmpty()) {
                double tarifBaru = Double.parseDouble(tarifStr.replace(',', '.'));
                b.setTarifHarian(tarifBaru);
            }

            if (b instanceof Perabot) {
                Perabot p = (Perabot) b;
                String jenisBaru = InputHelper.bacaString("Jenis perabot baru [" + p.getJenisPerabot() + "]: ");
                if (!jenisBaru.isEmpty()) p.setJenisPerabot(jenisBaru);
                String unitStr = InputHelper.bacaString("Jumlah isi unit baru [" + p.getJumlahUnit() + "]: ");
                if (!unitStr.isEmpty()) p.setJumlahUnit(Integer.parseInt(unitStr));
            } else if (b instanceof Tenda) {
                Tenda t = (Tenda) b;
                String ukuranBaru = InputHelper.bacaString("Ukuran tenda baru [" + t.getUkuran() + "]: ");
                if (!ukuranBaru.isEmpty()) t.setUkuran(ukuranBaru);
                String kapStr = InputHelper.bacaString("Kapasitas orang baru [" + t.getKapasitas() + "]: ");
                if (!kapStr.isEmpty()) t.setKapasitas(Integer.parseInt(kapStr));
            } else if (b instanceof SoundSystem) {
                SoundSystem s = (SoundSystem) b;
                String dayaStr = InputHelper.bacaString("Daya Watt baru [" + s.getDayaWatt() + "]: ");
                if (!dayaStr.isEmpty()) s.setDayaWatt(Integer.parseInt(dayaStr));
                String micStr = InputHelper.bacaString("Jumlah mic baru [" + s.getJumlahMikrofon() + "]: ");
                if (!micStr.isEmpty()) s.setJumlahMikrofon(Integer.parseInt(micStr));
            }

            dataStore.simpanBarang();
            System.out.println("\n[SUKSES] Data barang " + b.getId() + " berhasil diperbarui!");
        } catch (BarangTidakDitemukanException | BarangTidakTersediaException | InputTidakValidException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Format angka yang dimasukkan tidak valid.");
        }
        InputHelper.tungguEnter();
    }

    private void aksiHapusBarang() {
        tampilkanTabelBarang(dataStore.getDaftarBarang().getSemuaBarang(), "PILIH BARANG UNTUK DIHAPUS");
        String id = InputHelper.bacaString("Masukkan ID Barang yang ingin dihapus (atau 0 untuk batal): ");
        if ("0".equals(id)) return;

        try {
            Barang b = dataStore.getDaftarBarang().cariById(id);
            boolean konfirm = InputHelper.bacaKonfirmasi("Apakah Anda yakin ingin menghapus '" + b.getNama() + "' (" + b.getId() + ")?");
            if (konfirm) {
                dataStore.getDaftarBarang().hapusBarang(id);
                dataStore.simpanBarang();
                System.out.println("\n[SUKSES] Barang " + id + " berhasil dihapus dari sistem.");
            } else {
                System.out.println("\n[INFO] Penghapusan barang dibatalkan.");
            }
        } catch (BarangTidakDitemukanException | BarangTidakTersediaException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 2. CARI & FILTER BARANG
    // ==========================================

    private void menuCariFilterBarang() {
        InputHelper.cetakHeader("PENCARIAN DAN FILTER BARANG");
        System.out.println("  1. Cari Barang Berdasarkan Nama");
        System.out.println("  2. Filter Kategori: Perabot");
        System.out.println("  3. Filter Kategori: Tenda");
        System.out.println("  4. Filter Kategori: Sound System");
        System.out.println("  0. Kembali");
        InputHelper.cetakGaris('-', 75);

        int pil = InputHelper.bacaInteger("Pilih menu: ", 0, 4);
        switch (pil) {
            case 1:
                String q = InputHelper.bacaStringWajib("Masukkan kata kunci nama barang: ");
                ArrayList<Barang> hasilCari = dataStore.getDaftarBarang().cariByNama(q);
                tampilkanTabelBarang(hasilCari, "HASIL PENCARIAN NAMA: '" + q + "'");
                InputHelper.tungguEnter();
                break;
            case 2:
                tampilkanTabelBarang(dataStore.getDaftarBarang().filterByKategori("Perabot"), "FILTER KATEGORI: PERABOT");
                InputHelper.tungguEnter();
                break;
            case 3:
                tampilkanTabelBarang(dataStore.getDaftarBarang().filterByKategori("Tenda"), "FILTER KATEGORI: TENDA");
                InputHelper.tungguEnter();
                break;
            case 4:
                tampilkanTabelBarang(dataStore.getDaftarBarang().filterByKategori("Sound System"), "FILTER KATEGORI: SOUND SYSTEM");
                InputHelper.tungguEnter();
                break;
            case 0:
                break;
        }
    }

    // ==========================================
    // 3. TRANSAKSI MENUNGGU (TERIMA / TOLAK)
    // ==========================================

    private void menuTransaksiMenunggu() {
        ArrayList<Transaksi> list = dataStore.getTransaksiMenunggu();
        InputHelper.cetakHeader("TRANSAKSI MENUNGGU PERSETUJUAN ADMIN (FR-ADM-05)");

        if (list.isEmpty()) {
            System.out.println("  Tidak ada transaksi yang menunggu persetujuan.");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        System.out.printf("  %-8s | %-10s | %-18s | %-6s | %-15s | %s\n",
                "ID TRX", "ID Pny", "Nama Penyewa", "Lama", "Total Biaya", "Metode");
        InputHelper.cetakGaris('-', 75);

        for (Transaksi t : list) {
            System.out.printf("  %-8s | %-10s | %-18s | %-6s | %-15s | %s\n",
                    t.getId(),
                    (t.getPenyewa() != null ? t.getPenyewa().getId() : "-"),
                    (t.getPenyewa() != null ? t.getPenyewa().getNama() : "-"),
                    t.getLamaSewa() + " hari",
                    InputHelper.formatRupiah(t.hitungTotal()),
                    (t.getPembayaran() != null ? t.getPembayaran().getNamaMetode() : "-"));
        }
        InputHelper.cetakGaris('-', 75);

        String idTrx = InputHelper.bacaString("Ketik ID Transaksi yang ingin diproses (atau 0 untuk batal): ");
        if ("0".equals(idTrx)) return;

        try {
            Transaksi target = dataStore.cariTransaksi(idTrx);
            if (target.getStatus() != StatusTransaksi.MENUNGGU) {
                System.out.println("\n[WARN] Transaksi ini tidak berstatus MENUNGGU.");
                InputHelper.tungguEnter();
                return;
            }

            System.out.println("\n  Tindakan Transaksi " + target.getId() + ":");
            System.out.println("  1. TERIMA Transaksi (Barang menjadi DISEWA & Cetak Invoice Sewa)");
            System.out.println("  2. TOLAK Transaksi (Barang tetap TERSEDIA)");
            System.out.println("  0. Batal");
            InputHelper.cetakGaris('-', 75);

            int aksi = InputHelper.bacaInteger("Pilih tindakan: ", 0, 2);
            if (aksi == 1) {
                terimaTransaksi(target);
                dataStore.simpanBarang();
                dataStore.simpanTransaksi();
            } else if (aksi == 2) {
                tolakTransaksi(target);
                dataStore.simpanTransaksi();
            }
        } catch (InputTidakValidException | BarangTidakTersediaException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    /**
     * Menerima transaksi sewa (FR-ADM-06, FR-TRX-04).
     * Memeriksa ulang ketersediaan seluruh barang, mengubah status barang menjadi DISEWA,
     * status transaksi menjadi DITERIMA, menetapkan tanggal sewa dan jatuh tempo,
     * serta mencetak invoice sewa.
     *
     * @param t transaksi yang akan diterima
     * @throws BarangTidakTersediaException jika ada barang yang sudah berstatus DISEWA
     * @throws InputTidakValidException     jika status transaksi bukan MENUNGGU
     */
    public void terimaTransaksi(Transaksi t) throws BarangTidakTersediaException, InputTidakValidException {
        if (t == null) {
            throw new InputTidakValidException("Transaksi tidak valid.");
        }
        if (t.getStatus() != StatusTransaksi.MENUNGGU) {
            throw new InputTidakValidException("Hanya transaksi berstatus MENUNGGU yang dapat diterima.");
        }

        // Cek ulang ketersediaan barang (FR-TRX-04)
        for (Barang b : t.getBarang()) {
            if (b.getStatus() == StatusBarang.DISEWA) {
                throw new BarangTidakTersediaException(
                        "Gagal menerima transaksi! Barang '" + b.getNama() + "' (" + b.getId()
                                + ") saat ini sedang disewa oleh transaksi lain.");
            }
        }

        // Ubah status barang menjadi DISEWA
        for (Barang b : t.getBarang()) {
            b.setStatus(StatusBarang.DISEWA);
        }

        LocalDate sekarang = LocalDate.now();
        t.setTanggalSewa(sekarang);
        t.setTanggalJatuhTempo(sekarang.plusDays(t.getLamaSewa()));
        t.ubahStatus(StatusTransaksi.DITERIMA);

        String noInv = IdGenerator.generateNomorInvoice();
        t.setNomorInvoice(noInv);

        System.out.println("\n[SUKSES] Transaksi " + t.getId() + " berhasil DITERIMA!");
        new Invoice(noInv, t).cetak();
    }

    /**
     * Menolak transaksi sewa yang berstatus MENUNGGU (FR-ADM-07).
     *
     * @param t transaksi yang ditolak
     * @throws InputTidakValidException jika status transaksi bukan MENUNGGU
     */
    public void tolakTransaksi(Transaksi t) throws InputTidakValidException {
        if (t == null) {
            throw new InputTidakValidException("Transaksi tidak valid.");
        }
        if (t.getStatus() != StatusTransaksi.MENUNGGU) {
            throw new InputTidakValidException("Hanya transaksi berstatus MENUNGGU yang dapat ditolak.");
        }
        t.ubahStatus(StatusTransaksi.DITOLAK);
        System.out.println("\n[SUKSES] Transaksi " + t.getId() + " telah DITOLAK. Status barang tetap TERSEDIA.");
    }

    // ==========================================
    // 4. KONFIRMASI PENGEMBALIAN
    // ==========================================

    private void menuKonfirmasiPengembalian() {
        ArrayList<Transaksi> list = dataStore.getTransaksiDisewa();
        InputHelper.cetakHeader("KONFIRMASI PENGEMBALIAN BARANG SEWA (FR-ADM-08)");

        if (list.isEmpty()) {
            System.out.println("  Tidak ada transaksi yang sedang aktif disewa.");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        System.out.printf("  %-8s | %-10s | %-18s | %-12s | %-12s | %s\n",
                "ID TRX", "ID Pny", "Nama Penyewa", "Tgl Sewa", "Jatuh Tempo", "Lama");
        InputHelper.cetakGaris('-', 75);

        for (Transaksi t : list) {
            System.out.printf("  %-8s | %-10s | %-18s | %-12s | %-12s | %d hari\n",
                    t.getId(),
                    (t.getPenyewa() != null ? t.getPenyewa().getId() : "-"),
                    (t.getPenyewa() != null ? t.getPenyewa().getNama() : "-"),
                    (t.getTanggalSewa() != null ? t.getTanggalSewa().toString() : "-"),
                    (t.getTanggalJatuhTempo() != null ? t.getTanggalJatuhTempo().toString() : "-"),
                    t.getLamaSewa());
        }
        InputHelper.cetakGaris('-', 75);

        String idTrx = InputHelper.bacaString("Ketik ID Transaksi yang barangnya dikembalikan (atau 0 untuk batal): ");
        if ("0".equals(idTrx)) return;

        try {
            Transaksi target = dataStore.cariTransaksi(idTrx);
            if (target.getStatus() != StatusTransaksi.DITERIMA) {
                System.out.println("\n[WARN] Hanya transaksi berstatus DITERIMA (sedang disewa) yang dapat dikonfirmasi.");
                InputHelper.tungguEnter();
                return;
            }

            LocalDate tglKembali = InputHelper.bacaTanggal("Tanggal pengembalian (default hari ini)", LocalDate.now());
            konfirmasiKembali(target, tglKembali);

            dataStore.simpanBarang();
            dataStore.simpanTransaksi();
        } catch (InputTidakValidException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    /**
     * Mengonfirmasi pengembalian barang sewaan (FR-ADM-08, FR-TRX-05 s/d FR-TRX-07).
     * Menghitung denda jika terlambat, memproses pembayaran denda (jika &gt; 0),
     * mengembalikan status barang menjadi TERSEDIA, status transaksi menjadi SELESAI,
     * dan mencetak invoice final.
     *
     * @param t          transaksi yang dikembalikan
     * @param tglKembali tanggal aktual pengembalian
     * @throws InputTidakValidException jika tanggal kembali sebelum tanggal sewa atau status bukan DITERIMA
     */
    public void konfirmasiKembali(Transaksi t, LocalDate tglKembali) throws InputTidakValidException {
        if (t == null) {
            throw new InputTidakValidException("Transaksi tidak valid.");
        }
        if (t.getStatus() != StatusTransaksi.DITERIMA) {
            throw new InputTidakValidException("Hanya transaksi berstatus DITERIMA yang dapat dikembalikan.");
        }
        if (tglKembali == null) {
            tglKembali = LocalDate.now();
        }
        if (t.getTanggalSewa() != null && tglKembali.isBefore(t.getTanggalSewa())) {
            throw new InputTidakValidException(
                    "Tanggal kembali (" + tglKembali + ") tidak boleh lebih awal dari tanggal sewa (" + t.getTanggalSewa() + ")!");
        }

        t.setTanggalKembali(tglKembali);
        double denda = t.hitungDenda();
        t.setDenda(denda);

        if (denda > 0) {
            System.out.println("\n[PERINGATAN] Pengembalian terlambat " + t.hitungHariTerlambat() + " hari!");
            System.out.println("Total denda keterlambatan: " + InputHelper.formatRupiah(denda));
            System.out.println("Memproses penagihan denda via metode pembayaran: " + t.getPembayaran().getNamaMetode());
            t.getPembayaran().prosesPembayaran(denda);
        } else {
            System.out.println("\n[INFO] Pengembalian barang tepat waktu, tidak dikenakan denda.");
        }

        // Kembalikan status barang menjadi TERSEDIA
        for (Barang b : t.getBarang()) {
            b.setStatus(StatusBarang.TERSEDIA);
        }

        t.ubahStatus(StatusTransaksi.SELESAI);
        System.out.println("\n[SUKSES] Pengembalian transaksi " + t.getId() + " selesai dikonfirmasi!");
        new Invoice(t.getNomorInvoice(), t).cetak();
    }

    // ==========================================
    // 5. LIHAT SEMUA TRANSAKSI
    // ==========================================

    private void menuLihatSemuaTransaksi() {
        ArrayList<Transaksi> list = dataStore.getSemuaTransaksi();
        InputHelper.cetakHeader("LAPORAN SELURUH TRANSAKSI EVENTRENT (FR-ADM-09)");

        if (list.isEmpty()) {
            System.out.println("  (Belum ada transaksi yang tercatat)");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        System.out.printf("  %-8s | %-10s | %-18s | %-9s | %-15s | %-13s | %s\n",
                "ID TRX", "ID Pny", "Nama Penyewa", "Status", "Biaya Sewa", "Denda", "Metode");
        InputHelper.cetakGaris('-', 75);

        for (Transaksi t : list) {
            System.out.printf("  %-8s | %-10s | %-18s | %-9s | %-15s | %-13s | %s\n",
                    t.getId(),
                    (t.getPenyewa() != null ? t.getPenyewa().getId() : "-"),
                    (t.getPenyewa() != null ? t.getPenyewa().getNama() : "-"),
                    t.getStatus().name(),
                    InputHelper.formatRupiah(t.hitungTotal()),
                    InputHelper.formatRupiah(t.getDenda()),
                    (t.getPembayaran() != null ? t.getPembayaran().getNamaMetode() : "-"));
        }
        InputHelper.cetakGaris('-', 75);

        String idTrx = InputHelper.bacaString("Ketik ID Transaksi untuk cetak ulang Invoice (atau tekan ENTER untuk kembali): ");
        if (!idTrx.isEmpty()) {
            try {
                Transaksi t = dataStore.cariTransaksi(idTrx);
                new Invoice(t.getNomorInvoice(), t).cetak();
            } catch (InputTidakValidException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            }
        }
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 6. LAPORAN PENDAPATAN (FR-LAP-01)
    // ==========================================

    private void menuLaporanPendapatan() {
        ArrayList<Transaksi> list = dataStore.getSemuaTransaksi();
        InputHelper.cetakHeader("LAPORAN PENDAPATAN TOKO EVENTRENT (FR-LAP-01)");

        double totalSewa = 0.0;
        double totalDenda = 0.0;
        int jumlahSelesai = 0;

        HashMap<String, Double> pendapatanPerKategori = new HashMap<>();
        pendapatanPerKategori.put("Perabot", 0.0);
        pendapatanPerKategori.put("Tenda", 0.0);
        pendapatanPerKategori.put("Sound System", 0.0);

        HashMap<String, Double> pendapatanPerMetode = new HashMap<>();
        pendapatanPerMetode.put("Transfer Bank", 0.0);
        pendapatanPerMetode.put("QRIS", 0.0);
        pendapatanPerMetode.put("Tunai", 0.0);

        for (Transaksi t : list) {
            if (t.getStatus() == StatusTransaksi.SELESAI) {
                jumlahSelesai++;
                double sewaTrx = t.hitungTotal();
                double dendaTrx = t.getDenda();
                totalSewa += sewaTrx;
                totalDenda += dendaTrx;

                // Rincian per kategori barang
                for (Barang b : t.getBarang()) {
                    String kat = b.getKategori();
                    double biayaItem = b.hitungBiaya(t.getLamaSewa());
                    pendapatanPerKategori.put(kat, pendapatanPerKategori.getOrDefault(kat, 0.0) + biayaItem);
                }

                // Rincian per metode pembayaran
                if (t.getPembayaran() != null) {
                    String metode = t.getPembayaran().getNamaMetode();
                    double totalTrx = sewaTrx + dendaTrx;
                    pendapatanPerMetode.put(metode, pendapatanPerMetode.getOrDefault(metode, 0.0) + totalTrx);
                }
            }
        }

        System.out.printf("  Jumlah Transaksi Selesai : %d transaksi\n", jumlahSelesai);
        System.out.printf("  Total Pendapatan Sewa    : %s\n", InputHelper.formatRupiah(totalSewa));
        System.out.printf("  Total Pendapatan Denda   : %s\n", InputHelper.formatRupiah(totalDenda));
        System.out.printf("  TOTAL KESELURUHAN OMZET  : %s\n", InputHelper.formatRupiah(totalSewa + totalDenda));
        InputHelper.cetakGaris('-', 75);

        System.out.println("  RINCIAN PENDAPATAN PER KATEGORI BARANG:");
        for (String kat : pendapatanPerKategori.keySet()) {
            System.out.printf("    - %-15s : %s\n", kat, InputHelper.formatRupiah(pendapatanPerKategori.get(kat)));
        }
        InputHelper.cetakGaris('-', 75);

        System.out.println("  RINCIAN PENDAPATAN PER METODE PEMBAYARAN:");
        for (String met : pendapatanPerMetode.keySet()) {
            System.out.printf("    - %-15s : %s\n", met, InputHelper.formatRupiah(pendapatanPerMetode.get(met)));
        }
        InputHelper.cetakGaris('=', 75);
        InputHelper.tungguEnter();
    }

    private void tampilkanTabelBarang(List<Barang> daftar, String judul) {
        InputHelper.cetakHeader(judul);
        System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                "ID", "Nama Perlengkapan", "Kategori", "Tarif/Hari", "Status");
        InputHelper.cetakGaris('-', 75);

        if (daftar == null || daftar.isEmpty()) {
            System.out.println("  (Tidak ada barang pada daftar ini)");
        } else {
            for (Barang b : daftar) {
                System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                        b.getId(),
                        potong(b.getNama(), 28),
                        b.getKategori(),
                        InputHelper.formatRupiah(b.getTarifHarian()),
                        b.getStatus().name());
            }
        }
        InputHelper.cetakGaris('-', 75);
    }

    private String potong(String teks, int max) {
        if (teks == null) return "";
        if (teks.length() <= max) return teks;
        return teks.substring(0, max - 3) + "...";
    }
}

/**
 * Entitas Penyewa / Pelanggan EventRent (FR-PNY-01 s/d FR-PNY-08, 4.3).
 * Bertanggung jawab melihat katalog, mencari/memfilter barang, mengelola keranjang sewa,
 * melakukan checkout dengan lama sewa (1-30 hari) dan metode pembayaran, serta meninjau riwayat transaksi.
 *
 * @author Muhammad Faris Zufar (250810701100058)
 * @version 1.0
 */
class Penyewa extends Akun {

    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayat;
    private DataStore dataStore;

    /**
     * Konstruktor Penyewa.
     *
     * @param id        ID akun penyewa (misal: AKN-002)
     * @param username  username penyewa
     * @param password  kata sandi penyewa
     * @param nama      nama lengkap penyewa
     * @param dataStore referensi pengelola penyimpanan data
     * @throws InputTidakValidException jika validasi kredensial gagal
     */
    public Penyewa(String id, String username, String password, String nama, DataStore dataStore)
            throws InputTidakValidException {
        super(id, username, password, nama);
        this.keranjang = new KeranjangSewa();
        this.riwayat = new ArrayList<>();
        this.dataStore = dataStore;
    }

    /** {@inheritDoc} */
    @Override
    public String getPeran() {
        return "PENYEWA";
    }

    /**
     * {@inheritDoc}
     * Menampilkan menu interaktif Penyewa (Bagian 5.2 PRD).
     */
    @Override
    public void tampilkanMenu() {
        boolean aktif = true;
        while (aktif) {
            InputHelper.cetakHeader("PANEL PENYEWA - " + getNama() + " (" + getId() + ")");
            System.out.println("  1. Lihat Daftar Barang");
            System.out.println("  2. Cari Barang (Berdasarkan Nama)");
            System.out.println("  3. Filter Barang Per Kategori");
            System.out.println("  4. Tambah Barang ke Daftar Sewa (Keranjang)");
            System.out.println("  5. Lihat / Hapus Isi Daftar Sewa (" + keranjang.jumlahBarang() + " barang)");
            System.out.println("  6. Checkout");
            System.out.println("  7. Riwayat Sewa (Transaksi Diterima & Selesai)");
            System.out.println("  8. Status Transaksi Saya (Semua Transaksi)");
            System.out.println("  0. Logout (Kembali ke Menu Utama)");
            InputHelper.cetakGaris('-', 75);

            int pilihan = InputHelper.bacaInteger("Pilih menu: ", 0, 8);
            switch (pilihan) {
                case 1:
                    lihatBarang();
                    InputHelper.tungguEnter();
                    break;
                case 2:
                    menuCariBarang();
                    break;
                case 3:
                    menuFilterKategori();
                    break;
                case 4:
                    menuTambahKeranjang();
                    break;
                case 5:
                    menuKelolaKeranjang();
                    break;
                case 6:
                    menuCheckout();
                    break;
                case 7:
                    lihatRiwayat();
                    break;
                case 8:
                    lihatStatusTransaksi();
                    break;
                case 0:
                    aktif = false;
                    System.out.println("\n[INFO] Logout berhasil. Sampai jumpa kembali, " + getNama() + "!");
                    break;
            }
        }
    }

    // ==========================================
    // 1. LIHAT BARANG (FR-PNY-01)
    // ==========================================

    /**
     * Menampilkan seluruh daftar barang beserta status ketersediaannya (FR-PNY-01).
     */
    public void lihatBarang() {
        tampilkanTabel(dataStore.getDaftarBarang().getSemuaBarang(), "KATALOG PERLENGKAPAN ACARA EVENTRENT");
    }

    // ==========================================
    // 2. CARI BARANG (FR-PNY-02)
    // ==========================================

    private void menuCariBarang() {
        InputHelper.cetakHeader("PENCARIAN PERLENGKAPAN ACARA");
        String kata = InputHelper.bacaStringWajib("Masukkan kata kunci nama perlengkapan: ");
        ArrayList<Barang> hasil = dataStore.getDaftarBarang().cariByNama(kata);
        tampilkanTabel(hasil, "HASIL PENCARIAN DENGAN KATA: '" + kata + "'");
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 3. FILTER KATEGORI (FR-PNY-03)
    // ==========================================

    private void menuFilterKategori() {
        InputHelper.cetakHeader("FILTER PERLENGKAPAN BERDASARKAN KATEGORI");
        System.out.println("  1. Perabot (Kursi Futura, Kursi Chiavari, Meja Bundar, Sofa)");
        System.out.println("  2. Tenda (Tenda Terop, Tenda Sarnafil)");
        System.out.println("  3. Sound System (500W, 1000W, 2000W)");
        System.out.println("  0. Batal");
        InputHelper.cetakGaris('-', 75);

        int pil = InputHelper.bacaInteger("Pilih kategori: ", 0, 3);
        if (pil == 0) return;

        String kat = (pil == 1) ? "Perabot" : (pil == 2) ? "Tenda" : "Sound System";
        ArrayList<Barang> hasil = dataStore.getDaftarBarang().filterByKategori(kat);
        tampilkanTabel(hasil, "KATALOG KATEGORI: " + kat.toUpperCase());
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 4. TAMBAH KE KERANJANG (FR-PNY-04)
    // ==========================================

    private void menuTambahKeranjang() {
        tampilkanTabel(dataStore.getDaftarBarang().getBarangTersedia(), "DAFTAR BARANG YANG TERSEDIA UNTUK DISEWA");
        String id = InputHelper.bacaString("Masukkan ID Barang yang ingin disewa (atau 0 untuk batal): ");
        if ("0".equals(id)) return;

        try {
            Barang b = dataStore.getDaftarBarang().cariById(id);
            keranjang.tambahBarang(b);
            System.out.println("\n[SUKSES] '" + b.getNama() + "' (" + b.getId() + ") berhasil ditambahkan ke daftar sewa Anda!");
        } catch (BarangTidakDitemukanException | BarangTidakTersediaException | InputTidakValidException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 5. KELOLA KERANJANG (FR-PNY-05)
    // ==========================================

    private void menuKelolaKeranjang() {
        if (keranjang.isKosong()) {
            InputHelper.cetakHeader("DAFTAR SEWA ANDA (KERANJANG)");
            System.out.println("  Daftar sewa Anda saat ini masih kosong.");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        boolean aktif = true;
        while (aktif && !keranjang.isKosong()) {
            tampilkanTabel(keranjang.getBarang(), "ISI DAFTAR SEWA (KERANJANG) ANDA");
            System.out.println("  Tindakan:");
            System.out.println("  1. Hapus Salah Satu Barang dari Daftar Sewa");
            System.out.println("  2. Kosongkan Seluruh Daftar Sewa");
            System.out.println("  0. Kembali ke Menu Penyewa");
            InputHelper.cetakGaris('-', 75);

            int pil = InputHelper.bacaInteger("Pilih tindakan: ", 0, 2);
            switch (pil) {
                case 1:
                    String idHapus = InputHelper.bacaString("Masukkan ID Barang yang ingin dihapus dari daftar sewa: ");
                    try {
                        keranjang.hapusBarang(idHapus);
                        System.out.println("\n[SUKSES] Barang " + idHapus + " berhasil dihapus dari daftar sewa.");
                    } catch (BarangTidakDitemukanException e) {
                        System.out.println("\n[ERROR] " + e.getMessage());
                    }
                    break;
                case 2:
                    boolean yakin = InputHelper.bacaKonfirmasi("Kosongkan seluruh daftar sewa Anda?");
                    if (yakin) {
                        keranjang.kosongkan();
                        System.out.println("\n[INFO] Daftar sewa telah dikosongkan.");
                    }
                    break;
                case 0:
                    aktif = false;
                    break;
            }
        }
    }

    // ==========================================
    // 6. CHECKOUT (FR-PNY-06, FR-TRX-01, FR-TRX-03)
    // ==========================================

    private void menuCheckout() {
        if (keranjang.isKosong()) {
            System.out.println("\n[WARN] Daftar sewa Anda masih kosong! Tambahkan barang terlebih dahulu.");
            InputHelper.tungguEnter();
            return;
        }

        InputHelper.cetakHeader("RINGKASAN CHECKOUT SEWA ACARA");
        for (Barang b : keranjang.getBarang()) {
            System.out.printf("  - [%s] %-30s (%s) - Tarif: %s/hari\n",
                    b.getId(), b.getNama(), b.getKategori(), InputHelper.formatRupiah(b.getTarifHarian()));
        }
        InputHelper.cetakGaris('-', 75);

        try {
            int lamaSewa = InputHelper.bacaInteger("Masukkan lama durasi sewa (1 - 30 hari): ", 1, 30);
            double totalBiaya = keranjang.hitungTotal(lamaSewa);

            System.out.println("\n  Total Biaya Sewa untuk " + lamaSewa + " hari: "
                    + InputHelper.formatRupiah(totalBiaya));

            System.out.println("\n  Pilih Metode Pembayaran (FR-BYR-02):");
            System.out.println("  1. Transfer Bank (BCA 1234567890 a.n. EventRent)");
            System.out.println("  2. QRIS (Pindai QR Code)");
            System.out.println("  3. Tunai (Bayar langsung di kasir toko)");
            System.out.println("  0. Batal Checkout");
            InputHelper.cetakGaris('-', 75);

            int pilMetode = InputHelper.bacaInteger("Pilihan metode: ", 0, 3);
            if (pilMetode == 0) {
                System.out.println("\n[INFO] Checkout dibatalkan.");
                InputHelper.tungguEnter();
                return;
            }

            Pembayaran pemb;
            if (pilMetode == 1) {
                pemb = new TransferBank();
            } else if (pilMetode == 2) {
                pemb = new QRIS();
            } else {
                pemb = new Tunai();
            }

            Transaksi t = buatTransaksi(lamaSewa, pemb);
            System.out.println("\n[SUKSES] Transaksi " + t.getId() + " berhasil dibuat dengan status MENUNGGU!");
            System.out.println("Silakan tunggu persetujuan dan verifikasi dari Administrator.");
        } catch (InputTidakValidException | BarangTidakTersediaException e) {
            System.out.println("\n[ERROR CHECKOUT] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }

    /**
     * Membuat transaksi baru dari keranjang belanja sewa (FR-PNY-06, FR-TRX-01, FR-TRX-03).
     *
     * @param lamaSewa lama durasi sewa dalam hari (1-30 hari)
     * @param p        objek metode pembayaran yang dipilih
     * @return objek Transaksi baru berstatus MENUNGGU
     * @throws InputTidakValidException     jika lama sewa di luar rentang atau keranjang kosong
     * @throws BarangTidakTersediaException jika ada barang di keranjang yang berstatus DISEWA
     */
    public Transaksi buatTransaksi(int lamaSewa, Pembayaran p)
            throws InputTidakValidException, BarangTidakTersediaException {
        if (lamaSewa < 1 || lamaSewa > 30) {
            throw new InputTidakValidException("Lama sewa harus berada dalam rentang 1 sampai 30 hari.");
        }
        if (keranjang.isKosong()) {
            throw new InputTidakValidException("Daftar sewa (keranjang) Anda masih kosong.");
        }
        if (p == null) {
            throw new InputTidakValidException("Metode pembayaran harus ditentukan.");
        }

        // Cek ketersediaan saat checkout (FR-TRX-03)
        for (Barang b : keranjang.getBarang()) {
            if (b.getStatus() == StatusBarang.DISEWA) {
                throw new BarangTidakTersediaException(
                        "Barang '" + b.getNama() + "' (" + b.getId() + ") saat ini sedang DISEWA oleh transaksi lain!");
            }
        }

        double total = keranjang.hitungTotal(lamaSewa);
        System.out.println("\nMemproses pembayaran biaya sewa sebesar " + InputHelper.formatRupiah(total) + "...");
        p.prosesPembayaran(total);

        String idTrx = IdGenerator.generateIdTransaksi();
        Transaksi t = new Transaksi(idTrx, this, keranjang.getBarang(), lamaSewa, p);

        riwayat.add(t);
        if (dataStore != null) {
            dataStore.tambahTransaksi(t);
        }
        keranjang.kosongkan();

        return t;
    }

    // ==========================================
    // 7. RIWAYAT SEWA (FR-PNY-07)
    // ==========================================

    /**
     * Menampilkan riwayat transaksi sewa yang telah diterima atau selesai (FR-PNY-07).
     */
    public void lihatRiwayat() {
        InputHelper.cetakHeader("RIWAYAT SEWA ACARA - " + getNama());
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : riwayat) {
            if (t.getStatus() == StatusTransaksi.DITERIMA || t.getStatus() == StatusTransaksi.SELESAI) {
                list.add(t);
            }
        }

        if (list.isEmpty()) {
            System.out.println("  (Belum ada riwayat transaksi yang diterima/selesai)");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        cetakTabelTransaksiPenyewa(list);

        String idTrx = InputHelper.bacaString("Ketik ID Transaksi untuk cetak Invoice (atau tekan ENTER untuk kembali): ");
        if (!idTrx.isEmpty()) {
            boolean temu = false;
            for (Transaksi t : list) {
                if (t.getId().equalsIgnoreCase(idTrx.trim())) {
                    new Invoice(t.getNomorInvoice(), t).cetak();
                    temu = true;
                    break;
                }
            }
            if (!temu) {
                System.out.println("\n[ERROR] Transaksi dengan ID '" + idTrx + "' tidak ditemukan di riwayat Anda.");
            }
        }
        InputHelper.tungguEnter();
    }

    // ==========================================
    // 8. STATUS TRANSAKSI SAYA (FR-PNY-08)
    // ==========================================

    /**
     * Menampilkan seluruh status transaksi penyewa (termasuk MENUNGGU dan DITOLAK) (FR-PNY-08).
     */
    public void lihatStatusTransaksi() {
        InputHelper.cetakHeader("STATUS SELURUH TRANSAKSI SAYA - " + getNama());
        if (riwayat.isEmpty()) {
            System.out.println("  (Anda belum memiliki riwayat transaksi)");
            InputHelper.cetakGaris('-', 75);
            InputHelper.tungguEnter();
            return;
        }

        cetakTabelTransaksiPenyewa(riwayat);
        InputHelper.tungguEnter();
    }

    private void cetakTabelTransaksiPenyewa(List<Transaksi> list) {
        System.out.printf("  %-8s | %-10s | %-12s | %-12s | %-15s | %-12s | %s\n",
                "ID TRX", "Status", "Tgl Sewa", "Jatuh Tempo", "Total Biaya", "Denda", "Metode");
        InputHelper.cetakGaris('-', 75);

        for (Transaksi t : list) {
            System.out.printf("  %-8s | %-10s | %-12s | %-12s | %-15s | %-12s | %s\n",
                    t.getId(),
                    t.getStatus().name(),
                    (t.getTanggalSewa() != null ? t.getTanggalSewa().toString() : "-"),
                    (t.getTanggalJatuhTempo() != null ? t.getTanggalJatuhTempo().toString() : "-"),
                    InputHelper.formatRupiah(t.hitungTotal()),
                    InputHelper.formatRupiah(t.getDenda()),
                    (t.getPembayaran() != null ? t.getPembayaran().getNamaMetode() : "-"));
        }
        InputHelper.cetakGaris('-', 75);
    }

    private void tampilkanTabel(List<Barang> daftar, String judul) {
        InputHelper.cetakHeader(judul);
        System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                "ID", "Nama Perlengkapan", "Kategori", "Tarif/Hari", "Status");
        InputHelper.cetakGaris('-', 75);

        if (daftar == null || daftar.isEmpty()) {
            System.out.println("  (Tidak ada perlengkapan yang sesuai)");
        } else {
            for (Barang b : daftar) {
                System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                        b.getId(),
                        potong(b.getNama(), 28),
                        b.getKategori(),
                        InputHelper.formatRupiah(b.getTarifHarian()),
                        b.getStatus().name());
            }
        }
        InputHelper.cetakGaris('-', 75);
    }

    private String potong(String teks, int max) {
        if (teks == null) return "";
        if (teks.length() <= max) return teks;
        return teks.substring(0, max - 3) + "...";
    }

    /**
     * Menambahkan transaksi ke daftar riwayat (misalnya saat data dimuat dari berkas).
     *
     * @param t transaksi yang ditambahkan
     */
    public void tambahRiwayat(Transaksi t) {
        if (t != null && !riwayat.contains(t)) {
            riwayat.add(t);
        }
    }

    /**
     * Mengambil objek keranjang sewa milik penyewa.
     * @return objek KeranjangSewa
     */
    public KeranjangSewa getKeranjang() {
        return keranjang;
    }

    /**
     * Mengambil daftar riwayat transaksi milik penyewa.
     * @return list transaksi
     */
    public ArrayList<Transaksi> getRiwayat() {
        return new ArrayList<>(riwayat);
    }
}
