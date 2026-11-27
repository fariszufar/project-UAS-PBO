/**
 * Kelas abstrak dasar yang merepresentasikan barang/paket perlengkapan acara (FR-BRG-01).
 * Setiap barang merupakan satu paket/set yang disewa utuh dengan status TERSEDIA atau DISEWA.
 */
public abstract class Barang {

    private String id;
    private String nama;
    private double tarifHarian;
    private StatusBarang status;

    /**
     * Konstruktor objek Barang.
     *
     * @param id          ID unik barang (misal: BRG-001)
     * @param nama        nama paket perlengkapan
     * @param tarifHarian tarif sewa harian (&gt; 0)
     * @param status      status ketersediaan barang (TERSEDIA / DISEWA)
     * @throws InputTidakValidException jika tarif &lt;= 0 atau data kosong
     */
    public Barang(String id, String nama, double tarifHarian, StatusBarang status)
            throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID barang tidak boleh kosong.");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama barang tidak boleh kosong.");
        }
        if (tarifHarian <= 0) {
            throw new InputTidakValidException("Tarif harian harus lebih besar dari Rp0.");
        }

        this.id = id.trim();
        this.nama = nama.trim();
        this.tarifHarian = tarifHarian;
        this.status = (status != null) ? status : StatusBarang.TERSEDIA;
    }

    /**
     * Menghitung total biaya sewa untuk durasi hari tertentu (Polymorphism).
     * Di-override secara spesifik oleh masing-masing subclass.
     *
     * @param hari durasi lama sewa dalam hari (&gt;= 1)
     * @return total biaya sewa dalam rupiah
     */
    public abstract double hitungBiaya(int hari);

    /**
     * Menghitung denda per hari keterlambatan untuk barang ini (FR-TRX-05).
     * Denda = 50% (0,5) dari tarif harian barang, tanpa biaya pemasangan/teknisi.
     *
     * @return nominal denda per hari
     */
    public double hitungDendaPerHari() {
        return this.tarifHarian * 0.5;
    }

    /**
     * Mengembalikan nama kategori barang (Perabot / Tenda / Sound System).
     *
     * @return label kategori barang
     */
    public abstract String getKategori();

    /**
     * Mengambil nilai atribut spesifik ke-1 untuk penyimpanan berkas teks.
     *
     * @return nilai atribut 1
     */
    public abstract String getAtribut1();

    /**
     * Mengambil nilai atribut spesifik ke-2 untuk penyimpanan berkas teks.
     *
     * @return nilai atribut 2
     */
    public abstract String getAtribut2();

    /**
     * Memeriksa apakah barang saat ini siap/tersedia untuk disewa.
     *
     * @return true jika status TERSEDIA, false jika DISEWA
     */
    public boolean isTersedia() {
        return this.status == StatusBarang.TERSEDIA;
    }

    // --- Getter dan Setter dengan Validasi ---

    /**
     * Mengambil ID barang.
     * @return ID barang
     */
    public String getId() {
        return id;
    }

    /**
     * Mengatur ID barang.
     * @param id ID barang baru
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Mengambil nama barang.
     * @return nama barang
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengatur nama barang.
     * @param nama nama baru
     * @throws InputTidakValidException jika nama kosong
     */
    public void setNama(String nama) throws InputTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama barang tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    /**
     * Mengambil tarif sewa harian.
     * @return tarif harian
     */
    public double getTarifHarian() {
        return tarifHarian;
    }

    /**
     * Mengatur tarif sewa harian.
     * @param tarifHarian tarif harian baru (&gt; 0)
     * @throws InputTidakValidException jika tarif &lt;= 0
     */
    public void setTarifHarian(double tarifHarian) throws InputTidakValidException {
        if (tarifHarian <= 0) {
            throw new InputTidakValidException("Tarif harian harus lebih besar dari Rp0.");
        }
        this.tarifHarian = tarifHarian;
    }

    /**
     * Mengambil status ketersediaan barang.
     * @return status barang (TERSEDIA / DISEWA)
     */
    public StatusBarang getStatus() {
        return status;
    }

    /**
     * Mengatur status ketersediaan barang.
     * @param status status baru
     */
    public void setStatus(StatusBarang status) {
        this.status = status;
    }
}

/**
 * Representasi kategori Perabot (kursi, meja, sofa) (FR-BRG-01, 4.4).
 * Rumus biaya sewa: {@code tarifHarian * hari} (tarif harian murni).
 */
class Perabot extends Barang {

    private String jenisPerabot;
    private int jumlahUnit;

    /**
     * Konstruktor Perabot.
     *
     * @param id           ID unik perabot (misal: BRG-001)
     * @param nama         nama paket perabot
     * @param tarifHarian  tarif sewa per hari
     * @param status       status ketersediaan
     * @param jenisPerabot jenis perabot (misal: Kursi, Meja, Sofa)
     * @param jumlahUnit   jumlah isi unit dalam paket (&gt; 0)
     * @throws InputTidakValidException jika data input tidak valid
     */
    public Perabot(String id, String nama, double tarifHarian, StatusBarang status,
                   String jenisPerabot, int jumlahUnit) throws InputTidakValidException {
        super(id, nama, tarifHarian, status);
        if (jenisPerabot == null || jenisPerabot.trim().isEmpty()) {
            throw new InputTidakValidException("Jenis perabot tidak boleh kosong.");
        }
        if (jumlahUnit <= 0) {
            throw new InputTidakValidException("Jumlah unit perabot harus lebih besar dari 0.");
        }
        this.jenisPerabot = jenisPerabot.trim();
        this.jumlahUnit = jumlahUnit;
    }

    /**
     * {@inheritDoc}
     * Rumus Perabot: tarifHarian * hari.
     */
    @Override
    public double hitungBiaya(int hari) {
        return getTarifHarian() * hari;
    }

    /** {@inheritDoc} */
    @Override
    public String getKategori() {
        return "Perabot";
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut1() {
        return this.jenisPerabot;
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut2() {
        return String.valueOf(this.jumlahUnit);
    }

    /**
     * Mengambil jenis perabot.
     * @return jenis perabot
     */
    public String getJenisPerabot() {
        return jenisPerabot;
    }

    /**
     * Mengatur jenis perabot.
     * @param jenisPerabot jenis perabot baru
     * @throws InputTidakValidException jika jenis perabot kosong
     */
    public void setJenisPerabot(String jenisPerabot) throws InputTidakValidException {
        if (jenisPerabot == null || jenisPerabot.trim().isEmpty()) {
            throw new InputTidakValidException("Jenis perabot tidak boleh kosong.");
        }
        this.jenisPerabot = jenisPerabot.trim();
    }

    /**
     * Mengambil jumlah unit dalam paket perabot.
     * @return jumlah unit
     */
    public int getJumlahUnit() {
        return jumlahUnit;
    }

    /**
     * Mengatur jumlah unit dalam paket perabot.
     * @param jumlahUnit unit baru (&gt; 0)
     * @throws InputTidakValidException jika jumlah unit &lt;= 0
     */
    public void setJumlahUnit(int jumlahUnit) throws InputTidakValidException {
        if (jumlahUnit <= 0) {
            throw new InputTidakValidException("Jumlah unit harus lebih besar dari 0.");
        }
        this.jumlahUnit = jumlahUnit;
    }
}

/**
 * Representasi kategori Tenda / Terop (FR-BRG-01, 4.4).
 * Rumus biaya sewa: {@code (tarifHarian * hari) + 150.000} (biaya pemasangan tetap sekali per barang).
 */
class Tenda extends Barang {

    private static final double BIAYA_PEMASANGAN = 150000.0;
    private String ukuran;
    private int kapasitas;

    /**
     * Konstruktor Tenda.
     *
     * @param id          ID unik tenda (misal: BRG-005)
     * @param nama        nama paket tenda
     * @param tarifHarian tarif sewa harian
     * @param status      status ketersediaan
     * @param ukuran      dimensi ukuran tenda (misal: "4x6 m")
     * @param kapasitas   kapasitas tampung orang (&gt; 0)
     * @throws InputTidakValidException jika data input tidak valid
     */
    public Tenda(String id, String nama, double tarifHarian, StatusBarang status,
                 String ukuran, int kapasitas) throws InputTidakValidException {
        super(id, nama, tarifHarian, status);
        if (ukuran == null || ukuran.trim().isEmpty()) {
            throw new InputTidakValidException("Ukuran tenda tidak boleh kosong.");
        }
        if (kapasitas <= 0) {
            throw new InputTidakValidException("Kapasitas tenda harus lebih besar dari 0 orang.");
        }
        this.ukuran = ukuran.trim();
        this.kapasitas = kapasitas;
    }

    /**
     * {@inheritDoc}
     * Rumus Tenda: (tarifHarian * hari) + 150.000 (biaya pemasangan tetap sekali).
     */
    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() * hari) + BIAYA_PEMASANGAN;
    }

    /** {@inheritDoc} */
    @Override
    public String getKategori() {
        return "Tenda";
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut1() {
        return this.ukuran;
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut2() {
        return String.valueOf(this.kapasitas);
    }

    /**
     * Mengambil biaya pemasangan tetap tenda.
     * @return biaya pemasangan (Rp150.000)
     */
    public static double getBiayaPemasangan() {
        return BIAYA_PEMASANGAN;
    }

    /**
     * Mengambil ukuran dimensi tenda.
     * @return ukuran tenda
     */
    public String getUkuran() {
        return ukuran;
    }

    /**
     * Mengatur ukuran dimensi tenda.
     * @param ukuran ukuran baru
     * @throws InputTidakValidException jika ukuran kosong
     */
    public void setUkuran(String ukuran) throws InputTidakValidException {
        if (ukuran == null || ukuran.trim().isEmpty()) {
            throw new InputTidakValidException("Ukuran tenda tidak boleh kosong.");
        }
        this.ukuran = ukuran.trim();
    }

    /**
     * Mengambil kapasitas orang tenda.
     * @return kapasitas orang
     */
    public int getKapasitas() {
        return kapasitas;
    }

    /**
     * Mengatur kapasitas orang tenda.
     * @param kapasitas kapasitas baru (&gt; 0)
     * @throws InputTidakValidException jika kapasitas &lt;= 0
     */
    public void setKapasitas(int kapasitas) throws InputTidakValidException {
        if (kapasitas <= 0) {
            throw new InputTidakValidException("Kapasitas tenda harus lebih besar dari 0 orang.");
        }
        this.kapasitas = kapasitas;
    }
}

/**
 * Representasi kategori Sound System (FR-BRG-01, 4.4).
 * Rumus biaya sewa: {@code (tarifHarian * hari) + (100.000 * hari)} (biaya teknisi per hari).
 */
class SoundSystem extends Barang {

    private static final double BIAYA_TEKNISI_PER_HARI = 100000.0;
    private int dayaWatt;
    private int jumlahMikrofon;

    /**
     * Konstruktor SoundSystem.
     *
     * @param id             ID unik sound system (misal: BRG-008)
     * @param nama           nama paket sound system
     * @param tarifHarian    tarif sewa harian
     * @param status         status ketersediaan
     * @param dayaWatt       daya keluaran dalam watt (&gt; 0)
     * @param jumlahMikrofon jumlah mikrofon dalam paket (&gt;= 0)
     * @throws InputTidakValidException jika data input tidak valid
     */
    public SoundSystem(String id, String nama, double tarifHarian, StatusBarang status,
                       int dayaWatt, int jumlahMikrofon) throws InputTidakValidException {
        super(id, nama, tarifHarian, status);
        if (dayaWatt <= 0) {
            throw new InputTidakValidException("Daya sound system harus lebih besar dari 0 Watt.");
        }
        if (jumlahMikrofon < 0) {
            throw new InputTidakValidException("Jumlah mikrofon tidak boleh negatif.");
        }
        this.dayaWatt = dayaWatt;
        this.jumlahMikrofon = jumlahMikrofon;
    }

    /**
     * {@inheritDoc}
     * Rumus Sound System: (tarifHarian * hari) + (100.000 * hari) (biaya teknisi per hari).
     */
    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() * hari) + (BIAYA_TEKNISI_PER_HARI * hari);
    }

    /** {@inheritDoc} */
    @Override
    public String getKategori() {
        return "Sound System";
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut1() {
        return String.valueOf(this.dayaWatt);
    }

    /** {@inheritDoc} */
    @Override
    public String getAtribut2() {
        return String.valueOf(this.jumlahMikrofon);
    }

    /**
     * Mengambil biaya jasa teknisi sound system per hari.
     * @return biaya teknisi per hari (Rp100.000)
     */
    public static double getBiayaTeknisiPerHari() {
        return BIAYA_TEKNISI_PER_HARI;
    }

    /**
     * Mengambil daya sound system dalam Watt.
     * @return daya watt
     */
    public int getDayaWatt() {
        return dayaWatt;
    }

    /**
     * Mengatur daya sound system dalam Watt.
     * @param dayaWatt daya baru (&gt; 0)
     * @throws InputTidakValidException jika daya &lt;= 0
     */
    public void setDayaWatt(int dayaWatt) throws InputTidakValidException {
        if (dayaWatt <= 0) {
            throw new InputTidakValidException("Daya sound system harus lebih besar dari 0 Watt.");
        }
        this.dayaWatt = dayaWatt;
    }

    /**
     * Mengambil jumlah mikrofon dalam paket.
     * @return jumlah mic
     */
    public int getJumlahMikrofon() {
        return jumlahMikrofon;
    }

    /**
     * Mengatur jumlah mikrofon dalam paket.
     * @param jumlahMikrofon mic baru (&gt;= 0)
     * @throws InputTidakValidException jika mic &lt; 0
     */
    public void setJumlahMikrofon(int jumlahMikrofon) throws InputTidakValidException {
        if (jumlahMikrofon < 0) {
            throw new InputTidakValidException("Jumlah mikrofon tidak boleh negatif.");
        }
        this.jumlahMikrofon = jumlahMikrofon;
    }
}

/**
 * Enumerasi status ketersediaan barang/paket perlengkapan acara.
 */
enum StatusBarang {
    /** Barang siap dan tersedia untuk disewa */
    TERSEDIA,
    /** Barang sedang disewa dalam transaksi aktif */
    DISEWA
}
