# Dokumen Spesifikasi Project: "Mau Ngapain"

## 1. Konsep Aplikasi

"Mau Ngapain" adalah aplikasi manajemen tugas harian (To-Do List) berbasis Android yang dirancang
dengan pendekatan minimalis namun sangat fungsional. Aplikasi ini berfokus pada manajemen prioritas
tugas, sehingga pengguna dapat mengidentifikasi dan mengeksekusi aktivitas berdasarkan tingkat
urgensi. Seluruh data diproses dan disimpan secara lokal (offline) menggunakan prinsip *Single
Source of Truth*.

## 2. Fitur Utama

Aplikasi ini memuat beberapa fungsionalitas inti berikut:

* **Input Tugas & Prioritas:** Penambahan tugas baru yang wajib disertai dengan penentuan tingkat
  prioritas (*High, Medium, Low*).
* **Indikator Visual:** Representasi warna yang berbeda untuk setiap tingkat prioritas guna
  mempercepat pemindaian visual.
* **Pengurutan Reaktif:** Daftar tugas secara otomatis diperbarui dan diurutkan berdasarkan
  prioritas tertinggi ke terendah secara *real-time*.
* **Manajemen Status:** Kemampuan untuk menandai tugas sebagai selesai atau mengembalikannya ke
  status aktif.
* **Interaksi Usap (Swipe-to-Delete):** Penghapusan tugas dilakukan melalui gestur usap pada daftar
  elemen antarmuka.

## 3. Teknologi dan Library

Project ini dikembangkan menggunakan tumpukan teknologi (tech stack) Android modern, meliputi:

| Komponen                  | Teknologi yang Digunakan | Fungsi Utama                                                                                     |
|:--------------------------|:-------------------------|:-------------------------------------------------------------------------------------------------|
| **Bahasa Pemrograman**    | Kotlin                   | Pengembangan logika inti dan struktur aplikasi.                                                  |
| **Antarmuka Pengguna**    | Jetpack Compose          | Pembuatan antarmuka secara deklaratif.                                                           |
| **Dependency Injection**  | Hilt (Dagger)            | Manajemen dependensi antar komponen (ViewModel, Repository, Database).                           |
| **Asynchronous & Stream** | Kotlin Coroutines & Flow | Penanganan proses asinkron di *background thread* dan aliran data reaktif dari basis data ke UI. |
| **Local Database**        | Room Database            | Penyimpanan data persisten secara lokal menggunakan abstraksi SQLite.                            |

## 4. Arsitektur Perangkat Lunak

Pengembangan "Mau Ngapain" menerapkan **Arsitektur 2-Lapisan (2-Layer Architecture)** yang
digabungkan dengan pola **MVVM (Model-View-ViewModel)**.

* **Data Layer (Lapisan Data):** Menggunakan `TaskEntity`, `TaskDao`, dan `TaskRepository` untuk
  operasi pengelolaan data yang persisten.
* **UI Layer (Lapisan Antarmuka):** Menggunakan `TaskViewModel` untuk memproses *business logic*
  sederhana dan mengekspos *State* antarmuka untuk dirender oleh Jetpack Compose.

## 5. Capaian Implementasi Kode (Code Milestones)

Untuk memastikan kualitas kode yang terstandarisasi dan mudah diuji (*testable*), pengembangan UI
Layer wajib merealisasikan konsep-konsep arsitektur modern berikut:

### A. Implementasi Unidirectional Data Flow (UDF) untuk Reactive UI

Aplikasi harus menerapkan pola **Unidirectional Data Flow (UDF)**. Dalam pola ini, state mengalir ke
bawah (dari ViewModel ke UI), dan event mengalir ke atas (dari UI ke ViewModel).

* UI (Compose) tidak boleh memodifikasi data secara langsung.
* Semua perubahan antarmuka hanya terjadi karena ada pembaruan pada nilai `StateFlow` yang
  dipancarkan oleh ViewModel.
* Interaksi pengguna (misal: klik tombol simpan) hanya bertugas mengirimkan *Event* ke ViewModel,
  yang kemudian bertanggung jawab memperbarui *State*.

### B. Pemodelan UI State (Screen State)

Seluruh elemen data yang dibutuhkan untuk merender sebuah layar dibungkus dalam satu entitas data (
*Data Class*). Ini menghilangkan masalah state yang tidak sinkron (*inconsistent state*) yang sering
terjadi jika menggunakan banyak variabel state yang terpisah.

* Menggunakan `TaskUiState` yang memuat daftar tugas, status *loading*, dan pesan error.
* Contoh model yang direalisasikan:
  ```kotlin
  data class TaskUiState(
      val tasks: List<TaskEntity> = emptyList(),
      val isLoading: Boolean = false,
      val errorMessage: String? = null
  )
  ```

### C. Pemodelan UI Event dan Side Effect

Interaksi pengguna dan efek satu kali (*one-off events*) dimodelkan secara eksplisit.

1. **UI Event:** Dibangun menggunakan `sealed class` atau `sealed interface` untuk merangkum semua
   aksi pengguna (contoh: `OnTaskAdded`, `OnTaskDeleted`). UI memanggil satu fungsi tunggal di
   ViewModel (misal: `viewModel.onEvent(event)`), sehingga fungsi logika di dalam ViewModel dapat
   terenkapsulasi dengan aman (*private*).
2. **Side Effect / UI Effect:** Menangani kejadian yang hanya boleh dieksekusi satu kali, seperti
   memunculkan *Snackbar* atau navigasi. Diimplementasikan menggunakan `SharedFlow` atau `Channel`
   di ViewModel, dan ditangkap di Compose menggunakan `LaunchedEffect` agar tidak terpicu ulang saat
   terjadi rekomposisi layar (*recomposition*).

## 6. Struktur Direktori

Struktur folder disusun berdasarkan fitur dan fungsionalitas teknis untuk memudahkan navigasi:

```text
com.masdika.maungapain/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── TaskDao.kt
│   │   └── TaskEntity.kt
│   └── repository/
│       └── TaskRepository.kt 
├── ui/
│   ├── viewmodel/
│   │   └── TaskViewModel.kt
│   ├── screen/
│   │   └── TaskListScreen.kt
│   └── components/
│       ├── TaskItem.kt
│       └── PriorityIndicator.kt
├── di/
│   └── AppModule.kt
└── MauNgapainApp.kt
```