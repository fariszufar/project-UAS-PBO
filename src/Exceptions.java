/**
 * Exception yang dilempar ketika proses login gagal (username/password salah
 * atau peran pengguna tidak sesuai dengan menu yang diakses).
 */
class LoginGagalException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     * @param message pesan detail kesalahan login
     */
    public LoginGagalException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     */
    public LoginGagalException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika suatu barang tidak tersedia untuk disewa, diedit,
 * atau dihapus (misalnya karena sedang berstatus DISEWA).
 */
class BarangTidakTersediaException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     */
    public BarangTidakTersediaException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     */
    public BarangTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika pencarian barang berdasarkan ID tidak menemukan hasil.
 */
class BarangTidakDitemukanException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     */
    public BarangTidakDitemukanException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     */
    public BarangTidakDitemukanException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika input pengguna tidak valid (format salah,
 * angka negatif/di luar rentang, field kosong, atau username duplikat).
 */
class InputTidakValidException extends Exception {

    /**
     * Konstruktor dengan pesan kesalahan.
     */
    public InputTidakValidException(String message) {
        super(message);
    }

    /**
     * Konstruktor dengan pesan kesalahan dan penyebab.
     */
    public InputTidakValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
