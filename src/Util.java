import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Utilitas generator nomor ID otomatis berformat standar untuk seluruh entitas sistem.
 * Format ID:
 * - Akun: AKN-001, AKN-002, dst.
 * - Barang: BRG-001, BRG-002, dst.
 * - Transaksi: TRX-001, TRX-002, dst.
 * - Invoice: INV-001, INV-002, dst.
 */
class IdGenerator {

    private static int counterAkun = 0;
    private static int counterBarang = 0;
    private static int counterTransaksi = 0;
    private static int counterInvoice = 0;

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private IdGenerator() {
    }

    /**
     * Meng-generate ID akun baru berikutnya.
     *
     * @return ID akun baru
     */
    public static synchronized String generateIdAkun() {
        counterAkun++;
        return String.format("AKN-%03d", counterAkun);
    }

    /**
     * Meng-generate ID barang baru berikutnya.
     *
     * @return ID barang baru
     */
    public static synchronized String generateIdBarang() {
        counterBarang++;
        return String.format("BRG-%03d", counterBarang);
    }

    /**
     * Meng-generate ID transaksi baru berikutnya.
     *
     * @return ID transaksi baru
     */
    public static synchronized String generateIdTransaksi() {
        counterTransaksi++;
        return String.format("TRX-%03d", counterTransaksi);
    }

    /**
     * Meng-generate nomor invoice baru berikutnya.
     *
     * @return nomor invoice baru
     */
    public static synchronized String generateNomorInvoice() {
        counterInvoice++;
        return String.format("INV-%03d", counterInvoice);
    }

    /**
     * Memperbarui counter ID akun berdasarkan ID yang dibaca dari file penyimpanan.
     */
    public static synchronized void updateCounterAkun(String id) {
        int no = ekstrakNomor(id, "AKN-");
        if (no > counterAkun) {
            counterAkun = no;
        }
    }

    /**
     * Memperbarui counter ID barang berdasarkan ID yang dibaca dari file penyimpanan.
     *
     * @param id string ID barang (misal: BRG-010)
     */
    public static synchronized void updateCounterBarang(String id) {
        int no = ekstrakNomor(id, "BRG-");
        if (no > counterBarang) {
            counterBarang = no;
        }
    }

    /**
     * Memperbarui counter ID transaksi berdasarkan ID yang dibaca dari file penyimpanan.
     *
     * @param id string ID transaksi (misal: TRX-005)
     */
    public static synchronized void updateCounterTransaksi(String id) {
        int no = ekstrakNomor(id, "TRX-");
        if (no > counterTransaksi) {
            counterTransaksi = no;
        }
    }

    /**
     * Memperbarui counter nomor invoice berdasarkan nomor invoice yang dibaca dari file.
     *
     * @param nomor string nomor invoice (misal: INV-005)
     */
    public static synchronized void updateCounterInvoice(String nomor) {
        int no = ekstrakNomor(nomor, "INV-");
        if (no > counterInvoice) {
            counterInvoice = no;
        }
    }

    private static int ekstrakNomor(String teks, String prefix) {
        if (teks == null || !teks.toUpperCase().startsWith(prefix)) {
            return 0;
        }
        try {
            return Integer.parseInt(teks.substring(prefix.length()).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

/**
 * Utilitas pembacaan input dari CLI yang aman, tangguh, dan anti-crash (NFR-01, FR-UI-03).
 * Menggunakan pendekatan {@code Scanner.nextLine()} yang selalu di-parse secara aman.
 */
class InputHelper {

    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Locale LOCALE_ID = Locale.forLanguageTag("id-ID");

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private InputHelper() {
    }

    /**
     * Mengatur instance Scanner baru (berguna untuk mock testing otomatis).
     */
    public static void setScanner(Scanner s) {
        if (s != null) {
            scanner = s;
        }
    }

    /**
     * Membaca string input dari pengguna.
     */
    public static String bacaString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Membaca string yang tidak boleh kosong.
     */
    public static String bacaStringWajib(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[ERROR] Input tidak boleh kosong! Silakan isi kembali.");
        }
    }

    /**
     * Membaca string tanpa karakter pembatas pipe ('|') dan tidak kosong.
     */
    public static String bacaStringAman(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[ERROR] Input tidak boleh kosong!");
                continue;
            }
            if (input.contains("|")) {
                System.out.println("[ERROR] Input tidak boleh mengandung karakter pipe ('|')!");
                continue;
            }
            return input;
        }
    }

    /**
     * Membaca angka integer dalam rentang nilai tertentu secara aman.
     */
    public static int bacaInteger(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("[ERROR] Masukan harus berada dalam rentang %d sampai %d!%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Masukan harus berupa angka bilangan bulat yang valid!");
            }
        }
    }

    /**
     * Membaca angka double dalam rentang nilai tertentu.
     */
    public static double bacaDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("[ERROR] Nominal harus berada di antara %.0f sampai %.0f!%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Masukan harus berupa angka nominal yang valid!");
            }
        }
    }

    /**
     * Membaca konfirmasi Ya atau Tidak (y/n).
     * prompt pesan konfirmasi
     *  true jika Ya ('y'), false jika Tidak ('n')
     */
    public static boolean bacaKonfirmasi(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("ya")) {
                return true;
            } else if (input.equals("n") || input.equals("tidak")) {
                return false;
            }
            System.out.println("[ERROR] Harap masukkan 'y' untuk ya atau 'n' untuk tidak.");
        }
    }

    /**
     * Membaca tanggal berformat yyyy-MM-dd. Jika kosong, mengembalikan nilai default (hari ini).
     *
     * prompt         pesan tampilan
     * defaultTanggal tanggal default jika input kosong
     * objek LocalDate
     */
    public static LocalDate bacaTanggal(String prompt, LocalDate defaultTanggal) {
        while (true) {
            String defaultStr = (defaultTanggal != null) ? " [" + defaultTanggal.toString() + "]" : "";
            System.out.print(prompt + defaultStr + ": ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && defaultTanggal != null) {
                return defaultTanggal;
            }
            try {
                return LocalDate.parse(input, FORMAT_TANGGAL);
            } catch (DateTimeParseException e) {
                System.out.println("[ERROR] Format tanggal salah! Gunakan format yyyy-MM-dd (contoh: 2026-11-28).");
            }
        }
    }

    /**
     * Menunggu penekanan tombol ENTER untuk melanjutkan.
     */
    public static void tungguEnter() {
        System.out.print("\nTekan [ENTER] untuk melanjutkan...");
        scanner.nextLine();
    }

    /**
     * Memformat angka nominal menjadi representasi Rupiah standar (contoh: Rp2.025.000).
     */
    public static String formatRupiah(double nominal) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_ID);
        return "Rp" + nf.format(Math.round(nominal));
    }

    /**
     * Mencetak garis pembatas mendatar.
     */
    public static void cetakGaris(char karakter, int panjang) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < panjang; i++) {
            sb.append(karakter);
        }
        System.out.println(sb.toString());
    }

    /**
     * Mencetak header terpusat.
     */
    public static void cetakHeader(String judul) {
        System.out.println();
        cetakGaris('=', 75);
        int spasi = Math.max(0, (75 - judul.length()) / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spasi; i++) {
            sb.append(" ");
        }
        sb.append(judul);
        System.out.println(sb.toString());
        cetakGaris('=', 75);
    }
}

/**
 * Utilitas pencetakan tabel berbasis terminal/CLI dengan garis rapi dan presisi (FR-UI-01).
 */
class TabelCetak {

    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas.
     */
    private TabelCetak() {
    }

    /**
     * Mencetak tabel daftar barang.
     */
    public static void cetakTabelBarang(List<String[]> dataBarang) {
        int lebar = 80;
        InputHelper.cetakGaris('=', lebar);
        System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                "ID", "Nama Perlengkapan", "Kategori", "Tarif/Hari", "Status");
        InputHelper.cetakGaris('-', lebar);

        if (dataBarang == null || dataBarang.isEmpty()) {
            System.out.println("  (Tidak ada data barang yang tersedia)");
        } else {
            for (String[] row : dataBarang) {
                System.out.printf("  %-8s | %-28s | %-14s | %-13s | %s\n",
                        row[0],
                        potongTeks(row[1], 28),
                        row[2],
                        row[3],
                        row[4]);
            }
        }
        InputHelper.cetakGaris('=', lebar);
    }

    /**
     * Memotong string jika panjangnya melebihi batas kolom.
     */
    public static String potongTeks(String teks, int maxPanjang) {
        if (teks == null) return "";
        if (teks.length() <= maxPanjang) {
            return teks;
        }
        if (maxPanjang <= 3) {
            return teks.substring(0, maxPanjang);
        }
        return teks.substring(0, maxPanjang - 3) + "...";
    }
}
