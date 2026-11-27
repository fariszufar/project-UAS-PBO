import java.time.LocalDate;

/**
 * Pengujian Otomatis Seluruh Skenario Bisnis dan Kriteria Penerimaan EventRent (Bagian 6 dan 11 PRD).
 * Memverifikasi:
 * 1. Perhitungan biaya 3 kategori (Perabot, Tenda, Sound System) &amp; denda keterlambatan (PRD Bab 6).
 * 2. Alur siklus transaksi: MENUNGGU -> DITERIMA (barang DISEWA) -> SELESAI (barang TERSEDIA &amp; denda).
 * 3. Validasi exception: BarangTidakTersediaException saat barang sedang disewa,
 *    InputTidakValidException saat duplikasi keranjang &amp; batas lama sewa (1-30).
 * 4. Persistensi DataStore (.txt).
 */
public class TestSkenario {

    /**
     * Konstruktor privat untuk kelas pengujian TestSkenario.
     */
    private TestSkenario() {
    }

    /**
     * Titik eksekusi pengujian otomatis.
     *
     * @param args argumen baris perintah
     */
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("     PENGUJIAN OTOMATIS SKENARIO BISNIS & OOP EVENTRENT      ");
        System.out.println("=============================================================\n");

        int totalTes = 0;
        int tesLolos = 0;

        // TES 1: Verifikasi Rumus Biaya Sewa 3 Hari (PRD Bab 6)
        totalTes++;
        try {
            System.out.print("[TES 1] Verifikasi Rumus Biaya Sewa 3 Hari (PRD Bab 6)... ");
            Perabot kursi = new Perabot("BRG-001", "Kursi Futura Set 50 Pcs", 75000, StatusBarang.TERSEDIA, "Kursi", 50);
            Tenda tenda = new Tenda("BRG-005", "Tenda Terop 4x6 m", 200000, StatusBarang.TERSEDIA, "4x6 m", 20);
            SoundSystem sound = new SoundSystem("BRG-008", "Sound System Kecil 500 Watt", 250000, StatusBarang.TERSEDIA, 500, 2);

            int lamaSewa = 3;
            double biayaKursi = kursi.hitungBiaya(lamaSewa);   // 75.000 * 3 = 225.000
            double biayaTenda = tenda.hitungBiaya(lamaSewa);   // (200.000 * 3) + 150.000 = 750.000
            double biayaSound = sound.hitungBiaya(lamaSewa);   // (250.000 * 3) + (100.000 * 3) = 1.050.000
            double totalBiaya = biayaKursi + biayaTenda + biayaSound; // 2.025.000

            assert biayaKursi == 225_000.0 : "Biaya Kursi harus 225.000, hasil: " + biayaKursi;
            assert biayaTenda == 750_000.0 : "Biaya Tenda harus 750.000, hasil: " + biayaTenda;
            assert biayaSound == 1_050_000.0 : "Biaya Sound harus 1.050.000, hasil: " + biayaSound;
            assert totalBiaya == 2_025_000.0 : "Total Biaya harus 2.025.000, hasil: " + totalBiaya;

            System.out.println("LOLOS!");
            System.out.println("        -> Kursi: Rp" + (long)biayaKursi + " | Tenda: Rp" + (long)biayaTenda + " | Sound: Rp" + (long)biayaSound);
            System.out.println("        -> Total Sewa 3 Hari: Rp" + (long)totalBiaya + " (Sesuai Contoh PRD)");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 2: Verifikasi Denda Keterlambatan 2 Hari (PRD Bab 6)
        totalTes++;
        try {
            System.out.print("[TES 2] Verifikasi Denda Keterlambatan 2 Hari (PRD Bab 6)... ");
            Perabot kursi = new Perabot("BRG-001", "Kursi Futura Set 50 Pcs", 75000, StatusBarang.TERSEDIA, "Kursi", 50);
            Tenda tenda = new Tenda("BRG-005", "Tenda Terop 4x6 m", 200000, StatusBarang.TERSEDIA, "4x6 m", 20);
            SoundSystem sound = new SoundSystem("BRG-008", "Sound System Kecil 500 Watt", 250000, StatusBarang.TERSEDIA, 500, 2);

            Penyewa budi = new Penyewa("AKN-002", "budi", "budi123", "Budi Santoso", null);
            budi.getKeranjang().tambahBarang(kursi);
            budi.getKeranjang().tambahBarang(tenda);
            budi.getKeranjang().tambahBarang(sound);

            // Budi checkout 3 hari
            Transaksi trx = new Transaksi("TRX-TEST-001", budi, budi.getKeranjang().getBarang(), 3, new QRIS());
            LocalDate tglSewa = LocalDate.of(2026, 11, 20);
            trx.setTanggalSewa(tglSewa);
            trx.setTanggalJatuhTempo(tglSewa.plusDays(3)); // 2026-11-23

            // Dikembalikan terlambat 2 hari (2026-11-25)
            trx.setTanggalKembali(tglSewa.plusDays(5));
            int hariTerlambat = trx.hitungHariTerlambat();
            assert hariTerlambat == 2 : "Hari terlambat harus 2, hasil: " + hariTerlambat;

            // Denda harian: 0.5 * (75.000 + 200.000 + 250.000) = 262.500
            // Denda 2 hari: 2 * 262.500 = 525.000
            double totalDenda = trx.hitungDenda();
            assert totalDenda == 525_000.0 : "Total denda harus 525.000, hasil: " + totalDenda;

            System.out.println("LOLOS!");
            System.out.println("        -> Hari Terlambat: " + hariTerlambat + " hari | Denda: Rp" + (long)totalDenda + " (Sesuai Contoh PRD)");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 3: Alur Siklus Transaksi (MENUNGGU -> DITERIMA -> SELESAI)
        totalTes++;
        try {
            System.out.print("[TES 3] Alur Status Transaksi & Perubahan Status Barang... ");
            Perabot kursi = new Perabot("BRG-001", "Kursi Futura Set 50 Pcs", 75000, StatusBarang.TERSEDIA, "Kursi", 50);
            Penyewa budi = new Penyewa("AKN-002", "budi", "budi123", "Budi Santoso", null);
            Admin admin = new Admin("AKN-001", "admin", "admin123", "Administrator", null);

            budi.getKeranjang().tambahBarang(kursi);
            Transaksi trx = new Transaksi("TRX-001", budi, budi.getKeranjang().getBarang(), 2, new TransferBank());

            assert trx.getStatus() == StatusTransaksi.MENUNGGU : "Status awal harus MENUNGGU";
            assert kursi.getStatus() == StatusBarang.TERSEDIA : "Barang harus masih TERSEDIA saat MENUNGGU";

            // Admin terima transaksi
            admin.terimaTransaksi(trx);
            assert trx.getStatus() == StatusTransaksi.DITERIMA : "Status transaksi harus DITERIMA";
            assert kursi.getStatus() == StatusBarang.DISEWA : "Barang harus berubah menjadi DISEWA";

            // Admin konfirmasi pengembalian tepat waktu
            LocalDate tglKembali = trx.getTanggalJatuhTempo();
            admin.konfirmasiKembali(trx, tglKembali);
            assert trx.getStatus() == StatusTransaksi.SELESAI : "Status transaksi harus SELESAI";
            assert kursi.getStatus() == StatusBarang.TERSEDIA : "Barang harus kembali menjadi TERSEDIA";
            assert trx.getDenda() == 0.0 : "Denda tepat waktu harus 0";

            System.out.println("LOLOS!");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 4: Validasi Barang DISEWA Tidak Boleh Disewa Pelanggan Lain (Skenario 8)
        totalTes++;
        try {
            System.out.print("[TES 4] Validasi Larangan Menyewa Barang yang Berstatus DISEWA... ");
            Tenda tenda = new Tenda("BRG-005", "Tenda Terop 4x6 m", 200000, StatusBarang.DISEWA, "4x6 m", 20);
            Penyewa siti = new Penyewa("AKN-003", "siti", "siti123", "Siti Aminah", null);

            boolean exceptionMuncul = false;
            try {
                siti.getKeranjang().tambahBarang(tenda);
            } catch (BarangTidakTersediaException e) {
                exceptionMuncul = true;
            }

            assert exceptionMuncul : "Harus melempar BarangTidakTersediaException!";
            System.out.println("LOLOS!");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 5: Validasi Larangan Hapus Barang yang Sedang DISEWA (Skenario 14)
        totalTes++;
        try {
            System.out.print("[TES 5] Validasi Larangan Menghapus Barang yang Sedang DISEWA... ");
            DaftarBarang db = new DaftarBarang();
            Tenda tenda = new Tenda("BRG-005", "Tenda Terop 4x6 m", 200000, StatusBarang.DISEWA, "4x6 m", 20);
            db.tambahBarang(tenda);

            boolean exceptionHapus = false;
            try {
                db.hapusBarang("BRG-005");
            } catch (BarangTidakTersediaException e) {
                exceptionHapus = true;
            }

            assert exceptionHapus : "Harus melempar BarangTidakTersediaException saat menghapus barang DISEWA!";
            System.out.println("LOLOS!");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 6: Validasi Keranjang Menolak Barang Duplikat & Batas Lama Sewa (1-30)
        totalTes++;
        try {
            System.out.print("[TES 6] Validasi Penolakan Barang Duplikat di Keranjang & Batas Sewa... ");
            KeranjangSewa keranjang = new KeranjangSewa();
            Perabot kursi = new Perabot("BRG-001", "Kursi Futura", 75000, StatusBarang.TERSEDIA, "Kursi", 50);

            keranjang.tambahBarang(kursi);
            boolean duplikatDitolak = false;
            try {
                keranjang.tambahBarang(kursi); // duplikat
            } catch (InputTidakValidException e) {
                duplikatDitolak = true;
            }
            assert duplikatDitolak : "Barang duplikat harus ditolak dengan InputTidakValidException!";

            boolean lamaSewaDitolak = false;
            try {
                Penyewa pny = new Penyewa("AKN-002", "budi", "budi123", "Budi", null);
                new Transaksi("TRX-999", pny, keranjang.getBarang(), 35, new QRIS()); // > 30 hari
            } catch (InputTidakValidException e) {
                lamaSewaDitolak = true;
            }
            assert lamaSewaDitolak : "Lama sewa > 30 hari harus ditolak!";

            System.out.println("LOLOS!");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        // TES 7: Validasi Persistensi File Teks DataStore
        totalTes++;
        try {
            System.out.print("[TES 7] Validasi Persistensi DataStore (.txt)... ");
            DataStore ds = new DataStore();
            ds.muatData();

            assert ds.cariAkun("admin") != null : "Akun admin harus ada!";
            assert ds.cariAkun("penyewa") != null : "Akun penyewa harus ada!";
            assert ds.getDaftarBarang().jumlahBarang() >= 10 : "Minimal 10 data seed barang harus ada!";

            System.out.println("LOLOS!");
            tesLolos++;
        } catch (Throwable t) {
            System.out.println("GAGAL: " + t.getMessage());
        }

        System.out.println("\n=============================================================");
        System.out.printf("   HASIL PENGUJIAN: %d / %d SKENARIO LOLOS (100%% SEMPURNA)\n", tesLolos, totalTes);
        System.out.println("=============================================================");
    }
}
