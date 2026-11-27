# EventRent – Sistem Penyewaan Perlengkapan Acara Berbasis Java

**EventRent** merupakan aplikasi sistem informasi penyewaan perlengkapan acara (perabot, tenda, dan sound system) berbasis bahasa pemrograman **Java** dengan antarmuka **Command Line Interface (CLI)** yang interaktif. Aplikasi ini dirancang dan dibangun untuk memenuhi Tugas Proyek Akhir (UAS) Praktikum Pemrograman Berorientasi Objek (PBO) dengan mengimplementasikan prinsip arsitektur perangkat lunak yang bersih (*Clean Code*), pemisahan tanggung jawab (*Separation of Concerns*), dan lima pilar utama OOP secara menyeluruh.

---

##  Akun Demo (Default Login)

Berikut adalah akun demo bawaan yang siap digunakan untuk pengujian dan evaluasi sistem:

| Peran | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Penyewa | penyewa | penyewa123 |

>  **Catatan:** Pengguna baru juga dapat membuat akun mandiri secara langsung melalui submenu **Registrasi Penyewa**.

---

##  Anggota Pengembang (Kelompok)

| No | Nama Lengkap | NIM | Tanggung Jawab Utama |
|:--:|---|:---:|---|
| 1 | **Muhammad Faris Zufar** | `250810701100058` | `Akun`, `Admin`, `Penyewa`, Login & Registrasi, `Main`, `InputHelper`, Integrasi |
| 2 | **M. Diral** | `250810701100055` | `Barang`, `Perabot`, `Tenda`, `SoundSystem`, `DaftarBarang`, `KeranjangSewa`, `DataStore`, `IdGenerator`, Enum |
| 3 | **Zaw Then Theit** | `250810701100046` | `Pembayaran` (Transfer Bank, QRIS, Tunai), `Transaksi`, `Invoice`, `Cetakable`, `TabelCetak`, Laporan Pendapatan |

---

##  Fitur Utama Sistem

1. **Autentikasi & Multi-Peran (Role-Based Access):**
   - **Login Admin dan Penyewa:** Autentikasi terpisah dengan validasi kredensial aman dan batasan maksimal 3 kali gagal login per sesi demi keamanan.
   - **Registrasi Akun Baru:** Pelanggan baru dapat mendaftar mandiri langsung dari antarmuka CLI.

2. **Pengelolaan Barang (Katalog Perlengkapan):**
   - Administrator dapat melihat seluruh katalog, menambah barang baru, mengubah detail data barang, menghapus barang, serta mencari dan memfilter barang berdasarkan kategori.

3. **Keranjang Penyewaan (Cart System):**
   - Penyewa dapat memilih barang, memasukkannya ke dalam keranjang, meninjau estimasi biaya sewa, serta mencegah pemilihan barang duplikat maupun barang yang sedang disewa.

4. **Transaksi Penyewaan (Transaction Lifecycle):**
   - Siklus hidup transaksi terkontrol rapi: `MENUNGGU` (dibuat penyewa) $\rightarrow$ `DITERIMA` (disetujui admin) $\rightarrow$ `SELESAI` (barang kembali & lunas) atau `DITOLAK` (oleh admin).

5. **3 Kategori Perlengkapan Acara (Polymorphic Billing):**
   - **Perabot** (Kursi Futura, Meja Bundar, Sofa Tamu): Biaya = `Tarif Harian × Hari`
   - **Tenda** (Tenda Terop, Tenda Sarnafil): Biaya = `(Tarif Harian × Hari) + Rp150.000` *(biaya pemasangan tetap)*
   - **Sound System** (500W, 1000W, 2000W): Biaya = `(Tarif Harian × Hari) + (Rp100.000 × Hari)` *(biaya jasa teknisi per hari)*

6. **Metode Pembayaran (Interface Realization):**
   - **Transfer Bank:** Simulasi rekening resmi BCA dengan konfirmasi verifikasi transfer.
   - **QRIS:** Simulasi kode QRIS dinamis teks dengan verifikasi pembayaran e-wallet/mobile banking.
   - **Tunai (Cash):** Pembayaran langsung di kasir toko dengan perhitungan kecukupan nominal dan uang kembalian.

7. **Invoice & Pencetakan Struk:**
   - Pencetakan struk/invoice resmi ASCII (`Invoice implements Cetakable`) saat pesanan disetujui admin dan saat pengembalian barang selesai diproses.

8. **Riwayat Penyewaan & Status Pemesanan:**
   - Penyewa dapat memantau status pesanan sewa secara langsung serta melihat arsip transaksi yang telah selesai.

9. **Pengembalian Barang & Perhitungan Denda:**
   - Admin memproses konfirmasi barang kembali. Jika melewati batas jatuh tempo, sistem otomatis menghitung denda keterlambatan: `Hari Terlambat × (50% × Total Tarif Harian Barang)`.

---

##  Teknologi & Konsep yang Digunakan

- **Bahasa Pemrograman:** Java (Standard Edition, kompatibel Java 17 / 21 LTS).
- **Paradigma PBO (Object-Oriented Programming):**
  - **Abstraction:** Penerapan `abstract class Akun` dan `abstract class Barang` sebagai kerangka dasar entitas.
  - **Encapsulation:** Seluruh atribut berstatus `private` dengan validasi ketat pada *getter* dan *setter*.
  - **Inheritance:** Pewarisan kelas turunan (`Admin` dan `Penyewa` mewarisi `Akun`; `Perabot`, `Tenda`, dan `SoundSystem` mewarisi `Barang`).
  - **Polymorphism:** Metode `hitungBiaya(int hari)` dan `tampilkanMenu()` yang berjalan dinamis sesuai tipe objek instansiasi.
  - **Interface:** Abstraksi kontrak perilaku menggunakan `interface Pembayaran` dan `interface Cetakable`.
  - **Enumeration:** Manajemen status berbasis tipe aman dengan `enum StatusBarang` (`TERSEDIA`, `DISEWA`) dan `enum StatusTransaksi` (`MENUNGGU`, `DITERIMA`, `SELESAI`, `DITOLAK`).
  - **Custom Exceptions:** Penanganan galat terstruktur (`LoginGagalException`, `BarangTidakTersediaException`, `BarangTidakDitemukanException`, `InputTidakValidException`).
- **Persistensi Data:** Berkas teks murni (`.txt`) menggunakan pembatas pipe (`|`) pada `data/akun.txt`, `data/barang.txt`, dan `data/transaksi.txt`.
- **Pengujian Otomatis:** Skenario pengujian mandiri tanpa framework eksternal (`TestSkenario.java`).

---

##  Cara Menjalankan & Mengompilasi

> **Catatan Penting:** Kompilasi harus memakai `src\*.java` (bukan `-sourcepath`) karena beberapa kelas non-public berada dalam satu file yang sama (package-private).

### 1. Kompilasi Program:
Gunakan script otomatis di Command Prompt / Terminal:
```cmd
compile.bat
```
Atau gunakan perintah manual `javac`:
```cmd
javac -encoding UTF-8 -d out src\*.java
```

### 2. Menjalankan Aplikasi Utama (CLI):
```cmd
run.bat
```
Atau melalui perintah `java`:
```cmd
java -cp out Main
```

### 3. Menjalankan Pengujian Otomatis Skenario Bisnis:
```cmd
test.bat
```
Atau melalui perintah `java`:
```cmd
java -cp out TestSkenario
```

### 4. Menghasilkan Dokumentasi Javadoc:
```cmd
javadoc -package -d docs\javadoc src\*.java
```

---

##  Struktur Direktori Proyek

```
Uas PBO/
├── src/
│   ├── Main.java
│   ├── Akun.java
│   ├── Barang.java
│   ├── Transaksi.java
│   ├── Pembayaran.java
│   ├── DataStore.java
│   ├── Exceptions.java
│   ├── Util.java
│   └── TestSkenario.java
├── data/
│   ├── akun.txt
│   ├── barang.txt
│   └── transaksi.txt
├── docs/
│   ├── PRD_EventRent.md
│   ├── UAS_Praktikum_PBO_B.pdf
│   ├── class-diagram.png
│   ├── class-diagram.md
│   └── javadoc/
├── out/
├── compile.bat
├── run.bat
├── test.bat
├── README.md
└── .gitignore
```
