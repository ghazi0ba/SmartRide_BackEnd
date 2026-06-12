-- Database Setup Script for Driver Service
-- MySQL Database Initialization

-- ===================================
-- 1. CREATE DATABASE
-- ===================================
CREATE DATABASE IF NOT EXISTS `smartride_driver` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- ===================================
-- 2. USE DATABASE
-- ===================================
USE `smartride_driver`;

-- ===================================
-- 3. CREATE TABLES
-- ===================================

-- Drivers Table
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(100) NOT NULL,
  `prenom` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `telephone` VARCHAR(20) NOT NULL,
  `statut` ENUM('DISPONIBLE', 'OCCUPÉ', 'HORS_LIGNE') NOT NULL DEFAULT 'HORS_LIGNE',
  `marque_vehicule` VARCHAR(50) NOT NULL,
  `modele_vehicule` VARCHAR(50) NOT NULL,
  `plaque_immatriculation` VARCHAR(20) NOT NULL UNIQUE,
  `user_id` BIGINT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  INDEX `idx_email` (`email`),
  INDEX `idx_plaque` (`plaque_immatriculation`),
  INDEX `idx_statut` (`statut`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================
-- 4. INSERT SAMPLE DATA
-- ===================================

INSERT INTO `drivers` 
  (`nom`, `prenom`, `email`, `telephone`, `statut`, `marque_vehicule`, `modele_vehicule`, `plaque_immatriculation`, `user_id`)
VALUES 
  ('Benali', 'Mohamed', 'm.benali@smartride.com', '+216 91 234 567', 'DISPONIBLE', 'Toyota', 'Corolla 2023', 'TN 123 ABC', 1),
  ('Amara', 'Fatima', 'f.amara@smartride.com', '+216 92 345 678', 'OCCUPÉ', 'BMW', '320i 2022', 'TN 456 XYZ', 2),
  ('Mansouri', 'Ali', 'a.mansouri@smartride.com', '+216 93 456 789', 'HORS_LIGNE', 'Mercedes', 'C-Class 2023', 'TN 789 DEF', 3),
  ('Leili', 'Nadia', 'n.leili@smartride.com', '+216 94 567 890', 'DISPONIBLE', 'Renault', 'Clio 2022', 'TN 012 GHI', 4),
  ('Darragi', 'Omar', 'o.darragi@smartride.com', '+216 95 678 901', 'DISPONIBLE', 'Peugeot', '308 2021', 'TN 345 JKL', 5);

-- ===================================
-- 5. VERIFY DATA
-- ===================================

-- Show table structure
DESCRIBE `drivers`;

-- Show all drivers
SELECT * FROM `drivers`;

-- Show drivers by status
SELECT * FROM `drivers` WHERE `statut` = 'DISPONIBLE';

-- Show driver count by status
SELECT `statut`, COUNT(*) as `count` FROM `drivers` GROUP BY `statut`;

-- ===================================
-- 6. UTILITY QUERIES
-- ===================================

-- Get total drivers
SELECT COUNT(*) as `total_drivers` FROM `drivers`;

-- Get available drivers
SELECT COUNT(*) as `available_drivers` FROM `drivers` WHERE `statut` = 'DISPONIBLE';

-- Get occupied drivers
SELECT COUNT(*) as `occupied_drivers` FROM `drivers` WHERE `statut` = 'OCCUPÉ';

-- Get offline drivers
SELECT COUNT(*) as `offline_drivers` FROM `drivers` WHERE `statut` = 'HORS_LIGNE';

-- ===================================
-- 7. CLEANUP (Optional)
-- ===================================

-- Drop table if needed
-- DROP TABLE IF EXISTS `drivers`;

-- Drop database if needed
-- DROP DATABASE IF EXISTS `smartride_driver`;

-- ===================================
-- 8. BACKUP AND RESTORE
-- ===================================

-- Export database
-- mysqldump -u root -p smartride_driver > backup_drivers.sql

-- Import database
-- mysql -u root -p smartride_driver < backup_drivers.sql
