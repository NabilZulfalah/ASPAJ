-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 08, 2025 at 03:08 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_asetkejuruan`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `borrowings`
--

CREATE TABLE `borrowings` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `student_id` bigint(20) UNSIGNED NOT NULL,
  `borrow_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `status` enum('pending','approved','rejected','returned','partial','partially_approved','partially_returned') NOT NULL DEFAULT 'pending',
  `returned_by` bigint(20) UNSIGNED DEFAULT NULL,
  `return_condition` text DEFAULT NULL,
  `return_photo` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `tujuan` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `class` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `borrowing_items`
--

CREATE TABLE `borrowing_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `borrowing_id` bigint(20) UNSIGNED NOT NULL,
  `commodity_id` bigint(20) UNSIGNED NOT NULL,
  `quantity` int(10) UNSIGNED NOT NULL DEFAULT 1,
  `status` enum('pending','approved','rejected','returned') DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `return_condition` varchar(255) DEFAULT NULL,
  `return_photo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `carts`
--

CREATE TABLE `carts` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `carts`
--

INSERT INTO `carts` (`id`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 31, '2025-10-08 04:52:00', '2025-10-08 04:52:00');

-- --------------------------------------------------------

--
-- Table structure for table `cart_items`
--

CREATE TABLE `cart_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `cart_id` bigint(20) UNSIGNED NOT NULL,
  `commodity_id` bigint(20) UNSIGNED NOT NULL,
  `quantity` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commodities`
--

CREATE TABLE `commodities` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `merk` varchar(255) DEFAULT NULL,
  `harga_satuan` bigint(20) DEFAULT NULL,
  `sumber` varchar(255) DEFAULT NULL,
  `tahun` year(4) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `condition` varchar(255) NOT NULL DEFAULT 'good',
  `photo` varchar(255) DEFAULT NULL,
  `lokasi` varchar(255) NOT NULL,
  `jurusan` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `commodities`
--

INSERT INTO `commodities` (`id`, `code`, `name`, `merk`, `harga_satuan`, `sumber`, `tahun`, `deskripsi`, `stock`, `condition`, `photo`, `lokasi`, `jurusan`, `created_at`, `updated_at`) VALUES
(1, '10', 'Laptop', 'Asus', 8500000, 'Hibah BOS', '2023', 'Laptop untuk kegiatan belajar', 10, 'good', NULL, 'Lab Komputer', 'Rekayasa Perangkat Lunak', '2025-08-31 03:57:43', '2025-08-31 03:57:43'),
(2, '23', 'Proyektor', 'Epson', 5500000, 'Sekolah', '2022', 'Proyektor ruang kelas', 4, 'good', NULL, 'Kelas XI TITL', 'Teknik Instalasi Tenaga Listrik', '2025-08-31 03:57:43', '2025-08-31 03:57:43'),
(3, '94', 'Kamera DSLR', 'Canon', 12000000, 'Sponsor', '2021', 'Kamera dokumentasi kegiatan', 2, 'good', NULL, 'Studio Foto', 'Desain Komunikasi Visual', '2025-08-31 03:57:43', '2025-08-31 03:57:43'),
(4, '34', 'Speaker Monitor', 'Yamaha', 4500000, 'Sekolah', '2020', 'Speaker praktek audio', 6, 'maintenance', NULL, 'Lab Audio', 'Teknik Audio Video', '2025-08-31 03:57:43', '2025-08-31 03:57:43'),
(5, '89', 'PLC Kit', 'Siemens', 15000000, 'Hibah BOS', '2023', 'Modul praktek otomasi industri', 5, 'good', '/storage/commodities/psznyajSDFeGsKzlQMAZb1sg1fB4yRxwqddTHRmu.png', 'Lab Otomasi', 'teknik komputer jaringan', '2025-08-31 03:57:43', '2025-10-08 04:53:19'),
(6, '72', 'Router Mikrotik', 'Mikrotik', 2800000, 'Sekolah', '2021', 'Router jaringan untuk praktek TKJ', 7, 'good', NULL, 'Lab Jaringan', 'Teknik Komputer Jaringan', '2025-08-31 03:57:43', '2025-08-31 03:57:43');

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '2014_10_12_000000_create_users_table', 1),
(2, '2014_10_12_100000_create_password_resets_table', 1),
(3, '2019_08_19_000000_create_failed_jobs_table', 1),
(4, '2019_12_14_000001_create_personal_access_tokens_table', 1),
(5, '2025_08_01_052732_create_admins_table', 1),
(6, '2025_08_01_052739_create_officers_table', 1),
(7, '2025_08_01_052746_create_commodities_table', 1),
(8, '2025_08_01_052753_create_program_studies_table', 1),
(9, '2025_08_01_052759_create_school_classes_table', 1),
(10, '2025_08_01_052806_create_students_table', 1),
(11, '2025_08_01_052813_create_borrowings_table', 1),
(12, '2025_08_04_050004_add_role_to_users_table', 1),
(13, '2025_08_07_014354_update_borrowings_table_add_status_and_return_fields', 1),
(14, '2025_08_11_071132_add_quantity_to_borrowings_table', 1),
(15, '2025_08_13_132424_add_name_and_class_to_borrowings_table', 1),
(16, '2025_08_13_133026_drop_program_studies_table', 1),
(17, '2025_08_13_144937_drop_officers_id_from_borrowings_table', 1),
(18, '2025_08_15_210000_add_condition_to_commodities_table', 1),
(19, '2025_08_15_210000_add_user_id_to_students_table', 1),
(20, '2025_08_26_101248_create_notifications_table', 1),
(21, '2025_08_29_014532_add_return_fields_to_borrowings_table', 1),
(22, '2025_08_31_052236_add_new_fields_to_commodities_table', 1),
(23, '2025_08_31_060335_add_tujuan_to_borrowings_table', 1),
(24, '2025_08_31_074918_add_nis_and_approval_fields_to_users_table', 1),
(25, '2025_08_31_083856_alter_commodities_table_change_code_to_string', 1),
(26, '2025_09_03_052737_add_profile_picture_to_users_table', 2),
(27, '2024_06_01_000000_add_jurusan_to_users_table', 3),
(28, '2025_09_04_000000_add_must_change_password_to_users_table', 3),
(29, '2025_09_08_024039_restructure_borrowings_for_multiple_items', 3),
(30, '2025_09_08_031332_make_school_class_id_nullable_in_students_table', 3),
(31, '2025_09_08_031411_modify_school_class_id_in_students_table_v2', 3),
(32, '2025_09_08_044519_add_last_seen_notifications_to_users_table', 3),
(33, '2025_09_21_023523_create_carts_table', 3),
(34, '2025_09_21_023544_create_cart_items_table', 3),
(35, '2025_09_21_030811_add_photo_to_commodities_table', 3),
(36, '2025_09_26_014210_add_status_to_borrowing_items_table', 3),
(37, '2025_09_26_034547_add_returned_to_borrowing_items_status_enum', 3),
(38, '2025_09_26_041408_add_return_fields_to_borrowing_items_table', 3),
(39, '2025_09_27_083349_add_new_statuses_to_borrowings_table', 3);

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` char(36) NOT NULL,
  `type` varchar(255) NOT NULL,
  `notifiable_type` varchar(255) NOT NULL,
  `notifiable_id` bigint(20) UNSIGNED NOT NULL,
  `data` text NOT NULL,
  `read_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `officers`
--

CREATE TABLE `officers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `school_classes`
--

CREATE TABLE `school_classes` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `level` enum('X','XI','XII') NOT NULL,
  `program_study` varchar(255) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT 30,
  `description` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `school_classes`
--

INSERT INTO `school_classes` (`id`, `name`, `level`, `program_study`, `capacity`, `description`, `created_at`, `updated_at`) VALUES
(1, 'X RPL 1', 'X', 'Rekayasa Perangkat Lunak', 36, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16'),
(2, 'XI TITL 1', 'XI', 'Teknik Instalasi Tenaga Listrik', 34, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16'),
(3, 'X DKV 2', 'X', 'Desain Komunikasi Visual', 35, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16'),
(4, 'XII TAV 1', 'XII', 'Teknik Audio Video', 33, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16'),
(5, 'XI TOI 2', 'XI', 'Teknik Otomasi Industri', 32, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16'),
(6, 'XII TKJ 3', 'XII', 'Teknik Komputer Jaringan', 37, NULL, '2025-08-31 03:58:16', '2025-08-31 03:58:16');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `school_class_id` bigint(20) UNSIGNED DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `nis` varchar(255) DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `password_changed_at` timestamp NULL DEFAULT NULL,
  `must_change_password` tinyint(1) NOT NULL DEFAULT 1,
  `last_seen_notifications` timestamp NULL DEFAULT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `role` varchar(255) NOT NULL DEFAULT 'admin',
  `approval_status` enum('pending','approved','rejected') NOT NULL DEFAULT 'pending',
  `jurusan` varchar(255) DEFAULT NULL COMMENT 'Department for officers'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `profile_picture`, `nis`, `email_verified_at`, `password`, `password_changed_at`, `must_change_password`, `last_seen_notifications`, `remember_token`, `created_at`, `updated_at`, `role`, `approval_status`, `jurusan`) VALUES
(1, 'Admin Utama', 'admin1@example.com', 'admin1.jpg', NULL, '2025-10-08 07:08:24', '1234', NULL, 1, NULL, NULL, '2025-10-08 07:08:24', '2025-10-08 07:08:24', 'admin', 'approved', NULL),
(2, 'Admin Jurusan', 'admin2@example.com', 'admin2.jpg', NULL, '2025-10-08 07:08:24', '5678', NULL, 1, NULL, NULL, '2025-10-08 07:08:24', '2025-10-08 07:08:24', 'admin', 'approved', NULL),
(3, 'Budi Santoso', 'budi@student.com', 'budi.jpg', '1234567890', '2025-10-08 07:08:24', '1111', NULL, 1, NULL, NULL, '2025-10-08 07:08:24', '2025-10-08 07:08:24', 'student', 'approved', NULL),
(4, 'Siti Aminah', 'siti@student.com', 'siti.jpg', '1234567891', '2025-10-08 07:08:24', '2222', NULL, 1, NULL, NULL, '2025-10-08 07:08:24', '2025-10-08 07:08:24', 'student', 'pending', NULL),
(5, 'Andi Wijaya', 'andi@student.com', 'andi.jpg', '1234567892', '2025-10-08 07:08:24', '3333', NULL, 1, NULL, NULL, '2025-10-08 07:08:24', '2025-10-08 07:08:24', 'student', 'approved', NULL),
(31, 'Admin Aset', 'admin@aset.com', NULL, NULL, NULL, '$2y$10$/7P.u./aDxvOVPXAZa3Kne2E7aci4l0J8v15lZzyD2.SZCLNnNete', NULL, 1, NULL, NULL, '2025-10-08 04:50:17', '2025-10-08 04:50:17', 'admin', 'pending', NULL),
(32, 'Admin Kedua', 'ilham@aset.com', NULL, NULL, NULL, '$2y$10$.k/1AyZy7DjM72ejsaKSKePB86nvV8/RhhfPGe6hErVEp4R8XpzCC', NULL, 1, NULL, NULL, '2025-10-08 04:50:17', '2025-10-08 04:50:17', 'admin', 'pending', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `admins_email_unique` (`email`);

--
-- Indexes for table `borrowings`
--
ALTER TABLE `borrowings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `borrowings_student_id_foreign` (`student_id`),
  ADD KEY `borrowings_returned_by_foreign` (`returned_by`);

--
-- Indexes for table `borrowing_items`
--
ALTER TABLE `borrowing_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `borrowing_items_borrowing_id_foreign` (`borrowing_id`),
  ADD KEY `borrowing_items_commodity_id_foreign` (`commodity_id`);

--
-- Indexes for table `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `carts_user_id_foreign` (`user_id`);

--
-- Indexes for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cart_items_cart_id_foreign` (`cart_id`),
  ADD KEY `cart_items_commodity_id_foreign` (`commodity_id`);

--
-- Indexes for table `commodities`
--
ALTER TABLE `commodities`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `notifications_notifiable_type_notifiable_id_index` (`notifiable_type`,`notifiable_id`);

--
-- Indexes for table `officers`
--
ALTER TABLE `officers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `officers_email_unique` (`email`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`);

--
-- Indexes for table `school_classes`
--
ALTER TABLE `school_classes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`),
  ADD KEY `students_school_class_id_foreign` (`school_class_id`),
  ADD KEY `students_user_id_foreign` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`),
  ADD UNIQUE KEY `users_nis_unique` (`nis`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `borrowings`
--
ALTER TABLE `borrowings`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `borrowing_items`
--
ALTER TABLE `borrowing_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `carts`
--
ALTER TABLE `carts`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `commodities`
--
ALTER TABLE `commodities`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `officers`
--
ALTER TABLE `officers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `school_classes`
--
ALTER TABLE `school_classes`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=130;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `borrowings`
--
ALTER TABLE `borrowings`
  ADD CONSTRAINT `borrowings_returned_by_foreign` FOREIGN KEY (`returned_by`) REFERENCES `officers` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `borrowings_student_id_foreign` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `borrowing_items`
--
ALTER TABLE `borrowing_items`
  ADD CONSTRAINT `borrowing_items_borrowing_id_foreign` FOREIGN KEY (`borrowing_id`) REFERENCES `borrowings` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `borrowing_items_commodity_id_foreign` FOREIGN KEY (`commodity_id`) REFERENCES `commodities` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `carts_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_cart_id_foreign` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_items_commodity_id_foreign` FOREIGN KEY (`commodity_id`) REFERENCES `commodities` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_school_class_id_foreign` FOREIGN KEY (`school_class_id`) REFERENCES `school_classes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `students_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
