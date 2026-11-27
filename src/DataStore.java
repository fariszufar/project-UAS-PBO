import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Pengelola persistensi data EventRent ke berkas teks .txt (FR-DAT-01 s/d FR-DAT-04, 4.7a).
 * Mengelola penyimpanan akun.txt, barang.txt, dan transaksi.txt menggunakan pemisah pipe ('|').
 *
 * @author M. Diral (250810701100055)
 * @version 1.0
 */
public class DataStore {

    private static final String FOLDER_DATA = "data";
    private static final String FILE_AKUN = "data/akun.txt";
    private static final String FILE_BARANG = "data/barang.txt";
    private static final String FILE_TRANSAKSI = "data/transaksi.txt";

    private HashMap<String, Akun> akun;
    private HashMap<String, Akun> akunById;
    private DaftarBarang daftarBarang;
    private ArrayList<Transaksi> transaksi;

    /**
     * Konstruktor default DataStore.
     */
    public DataStore() {
        this.akun = new HashMap<>();
        this.akunById = new HashMap<>();
        this.daftarBarang = new DaftarBarang();
        this.transaksi = new ArrayList<>();
    }

    /**
     * Memuat seluruh data dari file teks. Jika file belum ada, sistem akan membuat data awal (seed).
     */
    public void muatData() {
        pastikanFolderData();
        muatAkun();
        muatBarang();
        muatTransaksi();
    }

    /**
     * Menyimpan seluruh data akun, barang, dan transaksi ke berkas teks.
     */
    public void simpanData() {
        pastikanFolderData();
        simpanAkun();
        simpanBarang();
        simpanTransaksi();
    }

    private void pastikanFolderData() {
        File folder = new File(FOLDER_DATA);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ==========================================
    // 1. MANAJEMEN AKUN
    // ==========================================

    private void muatAkun() {
        akun.clear();
        akunById.clear();
        File f = new File(FILE_AKUN);
        if (!f.exists()) {
            buatDataAwalAkun();
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: id|peran|username|password|nama
                String[] p = line.split("\\|", -1);
                if (p.length >= 5) {
                    String id = p[0].trim();
                    String peran = p[1].trim();
                    String username = p[2].trim();
                    String password = p[3].trim();
                    String nama = p[4].trim();

                    try {
                        Akun a;
                        if ("ADMIN".equalsIgnoreCase(peran)) {
                            a = new Admin(id, username, password, nama, this);
                        } else {
                            a = new Penyewa(id, username, password, nama, this);
                        }
                        akun.put(username.toLowerCase(), a);
                        akunById.put(id.toUpperCase(), a);
                        IdGenerator.updateCounterAkun(id);
                    } catch (InputTidakValidException e) {
                        System.err.println("[WARN] Gagal membaca akun: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal membaca berkas akun: " + e.getMessage());
        }

        if (akun.isEmpty()) {
            buatDataAwalAkun();
        }
    }

    private void buatDataAwalAkun() {
        try {
            Admin adm = new Admin("AKN-001", "admin", "admin123", "Administrator EventRent", this);
            Penyewa pny = new Penyewa("AKN-002", "penyewa", "penyewa123", "Penyewa EventRent", this);

            akun.put(adm.getUsername().toLowerCase(), adm);
            akunById.put(adm.getId().toUpperCase(), adm);
            IdGenerator.updateCounterAkun(adm.getId());

            akun.put(pny.getUsername().toLowerCase(), pny);
            akunById.put(pny.getId().toUpperCase(), pny);
            IdGenerator.updateCounterAkun(pny.getId());

            simpanAkun();
        } catch (InputTidakValidException e) {
            System.err.println("[ERROR] Gagal membuat akun seed: " + e.getMessage());
        }
    }

    /**
     * Menyimpan data seluruh akun ke akun.txt.
     */
    public void simpanAkun() {
        File f = new File(FILE_AKUN);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("# id|peran|username|password|nama\n");
            for (Akun a : akunById.values()) {
                bw.write(String.format("%s|%s|%s|%s|%s\n",
                        a.getId(), a.getPeran(), a.getUsername(), a.getPassword(), a.getNama()));
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal menulis berkas akun: " + e.getMessage());
        }
    }

    /**
     * Mencari akun berdasarkan username (case-insensitive).
     * @param username username yang dicari
     * @return objek Akun atau null jika tidak ditemukan
     */
    public Akun cariAkun(String username) {
        if (username == null) return null;
        return akun.get(username.trim().toLowerCase());
    }

    /**
     * Mencari akun berdasarkan ID akun (misal: AKN-001).
     * @param id ID akun
     * @return objek Akun atau null jika tidak ditemukan
     */
    public Akun cariAkunById(String id) {
        if (id == null) return null;
        return akunById.get(id.trim().toUpperCase());
    }

    /**
     * Menambahkan akun baru ke sistem dan menyimpannya ke berkas akun.txt.
     * @param a objek akun baru
     * @throws InputTidakValidException jika username sudah terdaftar
     */
    public void tambahAkun(Akun a) throws InputTidakValidException {
        if (a == null) {
            throw new InputTidakValidException("Akun tidak valid.");
        }
        String key = a.getUsername().toLowerCase();
        if (akun.containsKey(key)) {
            throw new InputTidakValidException("Username '" + a.getUsername() + "' sudah digunakan. Pilih username lain.");
        }
        akun.put(key, a);
        akunById.put(a.getId().toUpperCase(), a);
        IdGenerator.updateCounterAkun(a.getId());
        simpanAkun();
    }

    // ==========================================
    // 2. MANAJEMEN BARANG
    // ==========================================

    private void muatBarang() {
        daftarBarang.kosongkan();
        File f = new File(FILE_BARANG);
        if (!f.exists()) {
            buatDataAwalBarang();
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: id|kategori|nama|tarifHarian|status|atribut1|atribut2
                String[] p = line.split("\\|", -1);
                if (p.length >= 7) {
                    String id = p[0].trim();
                    String kategori = p[1].trim().toUpperCase();
                    String nama = p[2].trim();
                    double tarif = Double.parseDouble(p[3].trim());
                    StatusBarang status = StatusBarang.valueOf(p[4].trim().toUpperCase());
                    String at1 = p[5].trim();
                    String at2 = p[6].trim();

                    Barang b = null;
                    if ("PERABOT".equalsIgnoreCase(kategori)) {
                        int jmlUnit = Integer.parseInt(at2);
                        b = new Perabot(id, nama, tarif, status, at1, jmlUnit);
                    } else if ("TENDA".equalsIgnoreCase(kategori)) {
                        int kap = Integer.parseInt(at2);
                        b = new Tenda(id, nama, tarif, status, at1, kap);
                    } else if ("SOUNDSYSTEM".equalsIgnoreCase(kategori) || "SOUND SYSTEM".equalsIgnoreCase(kategori)) {
                        int daya = Integer.parseInt(at1);
                        int mic = Integer.parseInt(at2);
                        b = new SoundSystem(id, nama, tarif, status, daya, mic);
                    }

                    if (b != null) {
                        daftarBarang.tambahBarang(b);
                        IdGenerator.updateCounterBarang(id);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat berkas barang: " + e.getMessage());
        }

        if (daftarBarang.jumlahBarang() == 0) {
            buatDataAwalBarang();
        }
    }

    private void buatDataAwalBarang() {
        try {
            daftarBarang.tambahBarang(new Perabot("BRG-001", "Kursi Futura Set 50 Pcs", 75000, StatusBarang.TERSEDIA, "Kursi", 50));
            daftarBarang.tambahBarang(new Perabot("BRG-002", "Kursi Chiavari Set 20 Pcs", 100000, StatusBarang.TERSEDIA, "Kursi", 20));
            daftarBarang.tambahBarang(new Perabot("BRG-003", "Meja Bundar Set 10 Pcs", 60000, StatusBarang.TERSEDIA, "Meja", 10));
            daftarBarang.tambahBarang(new Perabot("BRG-004", "Sofa Tamu Set", 120000, StatusBarang.TERSEDIA, "Sofa", 1));

            daftarBarang.tambahBarang(new Tenda("BRG-005", "Tenda Terop 4x6 m", 200000, StatusBarang.TERSEDIA, "4x6 m", 20));
            daftarBarang.tambahBarang(new Tenda("BRG-006", "Tenda Terop 6x12 m", 400000, StatusBarang.TERSEDIA, "6x12 m", 60));
            daftarBarang.tambahBarang(new Tenda("BRG-007", "Tenda Sarnafil 10x15 m", 800000, StatusBarang.TERSEDIA, "10x15 m", 150));

            daftarBarang.tambahBarang(new SoundSystem("BRG-008", "Sound System Kecil 500 Watt", 250000, StatusBarang.TERSEDIA, 500, 2));
            daftarBarang.tambahBarang(new SoundSystem("BRG-009", "Sound System Sedang 1000 Watt", 450000, StatusBarang.TERSEDIA, 1000, 4));
            daftarBarang.tambahBarang(new SoundSystem("BRG-010", "Sound System Panggung 2000 Watt", 750000, StatusBarang.TERSEDIA, 2000, 6));

            for (Barang b : daftarBarang.getSemuaBarang()) {
                IdGenerator.updateCounterBarang(b.getId());
            }
            simpanBarang();
        } catch (InputTidakValidException e) {
            System.err.println("[ERROR] Gagal membuat data seed barang: " + e.getMessage());
        }
    }

    /**
     * Menyimpan data barang ke barang.txt.
     */
    public void simpanBarang() {
        File f = new File(FILE_BARANG);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("# id|kategori|nama|tarifHarian|status|atribut1|atribut2\n");
            for (Barang b : daftarBarang.getSemuaBarang()) {
                String katFile = b.getKategori().replace(" ", "").toUpperCase();
                bw.write(String.format("%s|%s|%s|%.0f|%s|%s|%s\n",
                        b.getId(),
                        katFile,
                        b.getNama(),
                        b.getTarifHarian(),
                        b.getStatus().name(),
                        b.getAtribut1(),
                        b.getAtribut2()));
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal menulis berkas barang: " + e.getMessage());
        }
    }

    /**
     * Mengambil instance pengelola koleksi barang.
     * @return objek DaftarBarang
     */
    public DaftarBarang getDaftarBarang() {
        return daftarBarang;
    }

    // ==========================================
    // 3. MANAJEMEN TRANSAKSI
    // ==========================================

    private void muatTransaksi() {
        transaksi.clear();
        File f = new File(FILE_TRANSAKSI);
        if (!f.exists()) {
            simpanTransaksi();
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: id|idPenyewa|idBarang1;idBarang2|lamaSewa|tglSewa|tglJatuhTempo|tglKembali|status|metode|denda|nomorInvoice
                String[] p = line.split("\\|", -1);
                if (p.length >= 11) {
                    String idTrx = p[0].trim();
                    String idPenyewa = p[1].trim();
                    String barangIdsStr = p[2].trim();
                    int lamaSewa = Integer.parseInt(p[3].trim());
                    LocalDate tglSewa = !p[4].trim().isEmpty() ? LocalDate.parse(p[4].trim()) : null;
                    LocalDate tglJatuhTempo = !p[5].trim().isEmpty() ? LocalDate.parse(p[5].trim()) : null;
                    LocalDate tglKembali = !p[6].trim().isEmpty() ? LocalDate.parse(p[6].trim()) : null;
                    StatusTransaksi status = StatusTransaksi.valueOf(p[7].trim().toUpperCase());
                    String namaMetode = p[8].trim();
                    double denda = Double.parseDouble(p[9].trim());
                    String noInvoice = p[10].trim();

                    Akun a = cariAkunById(idPenyewa);
                    Penyewa pny = (a instanceof Penyewa) ? (Penyewa) a : null;

                    ArrayList<Barang> barangTrx = new ArrayList<>();
                    if (!barangIdsStr.isEmpty()) {
                        String[] bIds = barangIdsStr.split(";");
                        for (String bId : bIds) {
                            try {
                                Barang b = daftarBarang.cariById(bId.trim());
                                barangTrx.add(b);
                            } catch (BarangTidakDitemukanException ignored) {}
                        }
                    }

                    Pembayaran pemb;
                    if (namaMetode.toLowerCase().contains("tunai")) {
                        pemb = new Tunai();
                    } else if (namaMetode.toLowerCase().contains("qris")) {
                        pemb = new QRIS();
                    } else {
                        pemb = new TransferBank();
                    }

                    Transaksi t = new Transaksi(idTrx, pny, barangTrx, lamaSewa,
                            tglSewa, tglJatuhTempo, tglKembali, status, pemb, denda, noInvoice);

                    transaksi.add(t);
                    if (pny != null) {
                        pny.tambahRiwayat(t);
                    }

                    IdGenerator.updateCounterTransaksi(idTrx);
                    if (!noInvoice.isEmpty()) {
                        IdGenerator.updateCounterInvoice(noInvoice);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat berkas transaksi: " + e.getMessage());
        }
    }

    /**
     * Menyimpan seluruh data transaksi ke transaksi.txt.
     */
    public void simpanTransaksi() {
        File f = new File(FILE_TRANSAKSI);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("# id|idPenyewa|idBarang1;idBarang2|lamaSewa|tglSewa|tglJatuhTempo|tglKembali|status|metode|denda|nomorInvoice\n");
            for (Transaksi t : transaksi) {
                StringBuilder bSb = new StringBuilder();
                for (int i = 0; i < t.getBarang().size(); i++) {
                    bSb.append(t.getBarang().get(i).getId());
                    if (i < t.getBarang().size() - 1) {
                        bSb.append(";");
                    }
                }

                String idPny = (t.getPenyewa() != null) ? t.getPenyewa().getId() : "";
                String tglSewa = (t.getTanggalSewa() != null) ? t.getTanggalSewa().toString() : "";
                String tglJatuh = (t.getTanggalJatuhTempo() != null) ? t.getTanggalJatuhTempo().toString() : "";
                String tglKembali = (t.getTanggalKembali() != null) ? t.getTanggalKembali().toString() : "";
                String metode = (t.getPembayaran() != null) ? t.getPembayaran().getNamaMetode() : "";

                bw.write(String.format("%s|%s|%s|%d|%s|%s|%s|%s|%s|%.0f|%s\n",
                        t.getId(),
                        idPny,
                        bSb.toString(),
                        t.getLamaSewa(),
                        tglSewa,
                        tglJatuh,
                        tglKembali,
                        t.getStatus().name(),
                        metode,
                        t.getDenda(),
                        t.getNomorInvoice()));
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal menulis berkas transaksi: " + e.getMessage());
        }
    }

    /**
     * Menambahkan transaksi baru dan menyimpannya ke transaksi.txt.
     * @param t transaksi baru
     */
    public void tambahTransaksi(Transaksi t) {
        if (t != null && !transaksi.contains(t)) {
            transaksi.add(t);
            IdGenerator.updateCounterTransaksi(t.getId());
            simpanTransaksi();
        }
    }

    /**
     * Mengambil salinan seluruh transaksi yang tercatat.
     * @return list seluruh transaksi
     */
    public ArrayList<Transaksi> getSemuaTransaksi() {
        return new ArrayList<>(transaksi);
    }

    /**
     * Mengambil transaksi yang sedang berstatus MENUNGGU.
     * @return list transaksi berstatus MENUNGGU
     */
    public ArrayList<Transaksi> getTransaksiMenunggu() {
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : transaksi) {
            if (t.getStatus() == StatusTransaksi.MENUNGGU) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Mengambil transaksi yang sedang aktif disewa (status DITERIMA).
     * @return list transaksi berstatus DITERIMA
     */
    public ArrayList<Transaksi> getTransaksiDisewa() {
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : transaksi) {
            if (t.getStatus() == StatusTransaksi.DITERIMA) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Mencari transaksi berdasarkan ID transaksi.
     * @param id ID transaksi
     * @return objek Transaksi yang dicari
     * @throws InputTidakValidException jika tidak ditemukan
     */
    public Transaksi cariTransaksi(String id) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID transaksi tidak boleh kosong.");
        }
        for (Transaksi t : transaksi) {
            if (t.getId().equalsIgnoreCase(id.trim())) {
                return t;
            }
        }
        throw new InputTidakValidException("Transaksi dengan ID '" + id + "' tidak ditemukan.");
    }
}

/**
 * Pengelola koleksi katalog barang/paket perlengkapan acara EventRent (FR-ADM-01 s/d FR-ADM-04).
 *
 * @author M. Diral (250810701100055)
 * @version 1.0
 */
class DaftarBarang {

    private ArrayList<Barang> barangList;

    /**
     * Konstruktor default DaftarBarang.
     */
    public DaftarBarang() {
        this.barangList = new ArrayList<>();
    }

    /**
     * Menambahkan barang baru ke dalam daftar.
     *
     * @param b objek barang baru
     * @throws InputTidakValidException jika ID sudah ada atau barang null
     */
    public void tambahBarang(Barang b) throws InputTidakValidException {
        if (b == null) {
            throw new InputTidakValidException("Objek barang tidak boleh null.");
        }
        for (Barang ada : barangList) {
            if (ada.getId().equalsIgnoreCase(b.getId())) {
                throw new InputTidakValidException("Barang dengan ID '" + b.getId() + "' sudah terdaftar.");
            }
        }
        barangList.add(b);
    }

    /**
     * Menghapus barang dari katalog (FR-ADM-03).
     * Validasi: Barang yang sedang berstatus DISEWA dilarang dihapus.
     *
     * @param id ID barang yang akan dihapus
     * @throws BarangTidakDitemukanException jika ID tidak ditemukan
     * @throws BarangTidakTersediaException  jika barang sedang disewa
     */
    public void hapusBarang(String id) throws BarangTidakDitemukanException, BarangTidakTersediaException {
        Barang b = cariById(id);
        if (b.getStatus() == StatusBarang.DISEWA) {
            throw new BarangTidakTersediaException(
                    "Barang '" + b.getNama() + "' (" + b.getId() + ") tidak dapat dihapus karena sedang DISEWA!");
        }
        barangList.remove(b);
    }

    /**
     * Mencari barang berdasarkan ID persis (case-insensitive) (FR-ADM-04, 7).
     *
     * @param id ID barang (misal: BRG-001)
     * @return objek Barang yang ditemukan
     * @throws BarangTidakDitemukanException jika tidak ada barang dengan ID tersebut
     */
    public Barang cariById(String id) throws BarangTidakDitemukanException {
        if (id == null || id.trim().isEmpty()) {
            throw new BarangTidakDitemukanException("ID barang tidak boleh kosong.");
        }
        for (Barang b : barangList) {
            if (b.getId().equalsIgnoreCase(id.trim())) {
                return b;
            }
        }
        throw new BarangTidakDitemukanException("Barang dengan ID '" + id + "' tidak ditemukan.");
    }

    /**
     * Mencari barang berdasarkan potongan nama (contains, case-insensitive) (FR-PNY-02).
     *
     * @param kata kata kunci nama barang
     * @return daftar barang yang namanya cocok
     */
    public ArrayList<Barang> cariByNama(String kata) {
        ArrayList<Barang> hasil = new ArrayList<>();
        if (kata == null || kata.trim().isEmpty()) {
            return hasil;
        }
        String q = kata.trim().toLowerCase();
        for (Barang b : barangList) {
            if (b.getNama().toLowerCase().contains(q)) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Memfilter barang berdasarkan kategori (Perabot / Tenda / Sound System) (FR-PNY-03).
     *
     * @param kategori nama kategori ("Perabot", "Tenda", "Sound System")
     * @return daftar barang yang berada dalam kategori yang dipilih
     */
    public ArrayList<Barang> filterByKategori(String kategori) {
        ArrayList<Barang> hasil = new ArrayList<>();
        if (kategori == null) return hasil;
        for (Barang b : barangList) {
            if (b.getKategori().equalsIgnoreCase(kategori.trim())) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Mengambil daftar seluruh barang yang ada di katalog.
     *
     * @return list seluruh barang
     */
    public ArrayList<Barang> getSemuaBarang() {
        return new ArrayList<>(barangList);
    }

    /**
     * Mengambil daftar barang yang siap/tersedia untuk disewa (status TERSEDIA).
     *
     * @return list barang berstatus TERSEDIA
     */
    public ArrayList<Barang> getBarangTersedia() {
        ArrayList<Barang> hasil = new ArrayList<>();
        for (Barang b : barangList) {
            if (b.getStatus() == StatusBarang.TERSEDIA) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Mengosongkan daftar barang di memori.
     */
    public void kosongkan() {
        barangList.clear();
    }

    /**
     * Mengembalikan jumlah item barang yang ada.
     *
     * @return jumlah barang
     */
    public int jumlahBarang() {
        return barangList.size();
    }
}
