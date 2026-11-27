/**
 * Exception yang dilempar ketika proses login gagal (username/password salah
 * atau peran pengguna tidak sesuai dengan menu yang diakses).
 *
 * @author Muhammad Faris Zufar (250810701100058)
 * @version 1.0
 */
class LoginGagalException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     *
     * @param message pesan detail kesalahan login
     */
    public LoginGagalException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     *
     * @param message pesan detail kesalahan
     * @param cause   penyebab awal terjadinya exception
     */
    public LoginGagalException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika suatu barang tidak tersedia untuk disewa, diedit,
 * atau dihapus (misalnya karena sedang berstatus DISEWA).
 *
 * @author M. Diral (250810701100055)
 * @version 1.0
 */
class BarangTidakTersediaException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     *
     * @param message pesan detail barang tidak tersedia
     */
    public BarangTidakTersediaException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     *
     * @param message pesan detail kesalahan
     * @param cause   penyebab awal
     */
    public BarangTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika pencarian barang berdasarkan ID tidak menemukan hasil.
 *
 * @author M. Diral (250810701100055)
 * @version 1.0
 */
class BarangTidakDitemukanException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     *
     * @param message pesan detail barang tidak ditemukan
     */
    public BarangTidakDitemukanException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     *
     * @param message pesan detail kesalahan
     * @param cause   penyebab awal
     */
    public BarangTidakDitemukanException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika input pengguna tidak valid (format salah,
 * angka negatif/di luar rentang, field kosong, atau username duplikat).
 *
 * @author Muhammad Faris Zufar (250810701100058)
 * @version 1.0
 */
class InputTidakValidException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     *
     * @param message pesan detail input tidak valid
     */
    public InputTidakValidException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     *
     * @param message pesan detail kesalahan
     * @param cause   penyebab awal
     */
    public InputTidakValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
