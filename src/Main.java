/**
 * Titik Masuk Utama (Main Entry Point) Aplikasi Rental Perlengkapan Acara EventRent (FR-UI-03, 3, 7).
 * Mengatur alur Menu Utama:
 * 1. Admin (Login langsung)
 * 2. Penyewa (Submenu: Login &amp; Registrasi)
 * 0. Keluar
 */
public class Main {

    private static DataStore dataStore;
    private static Akun akunAktif;

    /**
     * Konstruktor privat untuk kelas Main.
     */
    private Main() {
    }

    /**
     * Titik eksekusi program utama.
     */
    public static void main(String[] args) {
        dataStore = new DataStore();
        dataStore.muatData();

        // Shutdown hook untuk menjamin penyimpanan data saat aplikasi dihentikan
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (dataStore != null) {
                    dataStore.simpanData();
                }
            } catch (Exception ignored) {}
        }));

        menuUtama();
    }

    /**
     * Menampilkan menu utama EventRent (Bagian 3 PRD).
     */
    public static void menuUtama() {
        boolean berjalan = true;
        while (berjalan) {
            System.out.println();
            System.out.println("=========== EVENTRENT ===========");
            System.out.println("  1. Admin");
            System.out.println("  2. Penyewa");
            System.out.println("  0. Keluar");
            System.out.println("=================================");

            int pilihan = InputHelper.bacaInteger("Pilih menu: ", 0, 2);
            switch (pilihan) {
                case 1:
                    loginAdmin();
                    break;
                case 2:
                    menuPenyewa();
                    break;
                case 0:
                    berjalan = false;
                    dataStore.simpanData();
                    System.out.println();
                    InputHelper.cetakGaris('=', 50);
                    System.out.println("  Terima kasih telah menggunakan EventRent!");
                    System.out.println("  Semoga acara Anda berjalan sukses dan meriah!");
                    InputHelper.cetakGaris('=', 50);
                    System.out.println();
                    break;
            }
        }
    }

    /**
     * Memproses login Administrator dengan batas maksimal 3 kali gagal (FR-AKN-01, 3.1, 3.5).
     */
    public static void loginAdmin() {
        int percobaan = 0;
        final int MAX_PERCOBAAN = 3;

        while (percobaan < MAX_PERCOBAAN) {
            InputHelper.cetakHeader("LOGIN ADMIN (Sisa percobaan: " + (MAX_PERCOBAAN - percobaan) + ")");
            String user = InputHelper.bacaString("Username: ");
            String pass = InputHelper.bacaString("Password: ");

            try {
                Akun a = dataStore.cariAkun(user);
                if (a == null || !a.login(user, pass)) {
                    throw new LoginGagalException("Username atau password salah!");
                }
                if (!(a instanceof Admin)) {
                    throw new LoginGagalException("Akun '" + user + "' bukan merupakan akun Administrator!");
                }

                akunAktif = a;
                System.out.println("\n[SUKSES] Selamat datang kembali, " + a.getNama() + "!");
                InputHelper.tungguEnter();

                // Buka menu admin
                akunAktif.tampilkanMenu();
                akunAktif = null;
                return; // Kembali ke menu utama setelah logout
            } catch (LoginGagalException e) {
                percobaan++;
                System.out.println("\n[GAGAL LOGIN] " + e.getMessage());
                if (percobaan >= MAX_PERCOBAAN) {
                    System.out.println("\n[PERINGATAN] Anda telah gagal login sebanyak 3 kali.");
                    System.out.println("Sistem akan menyimpan data dan menghentikan program demi keamanan.");
                    dataStore.simpanData();
                    System.exit(0);
                } else {
                    System.out.println("Sisa percobaan login: " + (MAX_PERCOBAAN - percobaan));
                    InputHelper.tungguEnter();
                }
            }
        }
    }

    /**
     * Menampilkan submenu akses Penyewa (Login, Registrasi, Kembali) (Bagian 3.2 PRD).
     */
    public static void menuPenyewa() {
        boolean aktif = true;
        while (aktif) {
            InputHelper.cetakHeader("AKSES PENYEWA");
            System.out.println("  1. Login");
            System.out.println("  2. Registrasi Akun Baru");
            System.out.println("  0. Kembali ke Menu Utama");
            InputHelper.cetakGaris('-', 50);

            int pil = InputHelper.bacaInteger("Pilih aksi: ", 0, 2);
            switch (pil) {
                case 1:
                    loginPenyewa();
                    break;
                case 2:
                    registrasiPenyewa();
                    break;
                case 0:
                    aktif = false;
                    break;
            }
        }
    }

    private static void loginPenyewa() {
        int percobaan = 0;
        final int MAX_PERCOBAAN = 3;

        while (percobaan < MAX_PERCOBAAN) {
            InputHelper.cetakHeader("LOGIN PENYEWA (Sisa percobaan: " + (MAX_PERCOBAAN - percobaan) + ")");
            String user = InputHelper.bacaString("Username: ");
            String pass = InputHelper.bacaString("Password: ");

            try {
                Akun a = dataStore.cariAkun(user);
                if (a == null || !a.login(user, pass)) {
                    throw new LoginGagalException("Username atau password salah!");
                }
                if (!(a instanceof Penyewa)) {
                    throw new LoginGagalException("Akun '" + user + "' bukan merupakan akun Penyewa!");
                }

                akunAktif = a;
                System.out.println("\n[SUKSES] Selamat datang, " + a.getNama() + "!");
                InputHelper.tungguEnter();

                // Buka menu penyewa
                akunAktif.tampilkanMenu();
                akunAktif = null;
                return; // Kembali ke submenu penyewa setelah logout
            } catch (LoginGagalException e) {
                percobaan++;
                System.out.println("\n[GAGAL LOGIN] " + e.getMessage());
                if (percobaan >= MAX_PERCOBAAN) {
                    System.out.println("\n[PERINGATAN] Anda telah gagal login sebanyak 3 kali.");
                    System.out.println("Sistem akan menyimpan data dan menghentikan program demi keamanan.");
                    dataStore.simpanData();
                    System.exit(0);
                } else {
                    System.out.println("Sisa percobaan login: " + (MAX_PERCOBAAN - percobaan));
                    InputHelper.tungguEnter();
                }
            }
        }
    }

    private static void registrasiPenyewa() {
        InputHelper.cetakHeader("REGISTRASI PENYEWA BARU (FR-AKN-04)");
        try {
            String user = InputHelper.bacaStringAman("Username baru (tanpa spasi/pipe): ");
            if (user.contains(" ")) {
                throw new InputTidakValidException("Username tidak boleh mengandung spasi.");
            }
            if (dataStore.cariAkun(user) != null) {
                throw new InputTidakValidException("Username '" + user + "' sudah digunakan. Silakan gunakan username lain.");
            }

            String pass = InputHelper.bacaStringAman("Password (minimal 4 karakter): ");
            if (pass.length() < 4) {
                throw new InputTidakValidException("Password minimal harus 4 karakter.");
            }

            String nama = InputHelper.bacaStringAman("Nama lengkap: ");
            String idBaru = IdGenerator.generateIdAkun();

            Penyewa pnyBaru = new Penyewa(idBaru, user, pass, nama, dataStore);
            dataStore.tambahAkun(pnyBaru);

            System.out.println("\n[SUKSES] Akun penyewa berhasil didaftarkan!");
            System.out.println("  ID Akun  : " + idBaru);
            System.out.println("  Username : " + user);
            System.out.println("  Nama     : " + nama);
            System.out.println("Silakan pilih menu Login untuk masuk ke akun Anda.");
        } catch (InputTidakValidException e) {
            System.out.println("\n[GAGAL REGISTRASI] " + e.getMessage());
        }
        InputHelper.tungguEnter();
    }
}
