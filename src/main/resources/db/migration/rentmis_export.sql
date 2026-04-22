mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: rentmis
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.22.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
mysqldump: Error: 'Access denied; you need (at least one of) the PROCESS privilege(s) for this operation' when trying to dump tablespaces

--
-- Current Database: `rentmis`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `rentmis` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `rentmis`;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `user_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_message` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_audit_user` (`user_id`),
  KEY `idx_audit_action` (`action`),
  KEY `idx_audit_entity` (`entity_type`,`entity_id`),
  KEY `idx_audit_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (1,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-06 20:25:52'),(2,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 20:27:12'),(3,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 20:27:23'),(4,NULL,NULL,'AUTH_LOGIN','User',2,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 20:27:37'),(5,NULL,NULL,'PROPERTY_CREATED','Property',1,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-06 20:27:37'),(6,NULL,NULL,'UNIT_CREATED','Unit',1,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-06 20:27:38'),(7,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 20:27:45'),(8,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 20:34:35'),(9,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 20:35:32'),(10,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 21:03:22'),(11,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 21:04:37'),(12,NULL,NULL,'AUTH_LOGIN','User',2,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 21:12:13'),(13,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 21:22:17'),(14,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 21:22:23'),(15,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 21:25:05'),(16,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 21:27:50'),(17,NULL,NULL,'AUTH_LOGIN_FAILED','User',3,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-06 21:29:15'),(18,NULL,NULL,'AUTH_LOGIN_FAILED','User',3,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-06 21:29:50'),(19,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 21:46:13'),(20,NULL,NULL,'AUTH_LOGIN','User',2,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 22:33:57'),(21,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 22:57:30'),(22,NULL,NULL,'AUTH_LOGIN','User',2,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-06 22:58:17'),(23,NULL,NULL,'UNIT_UPDATED','Unit',1,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-06 23:21:13'),(24,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 23:42:05'),(25,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 23:42:16'),(26,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-06 23:51:40'),(27,NULL,NULL,'CONTRACT_CREATED','Contract',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 00:26:02'),(28,NULL,NULL,'CONTRACT_SIGNED_LANDLORD','Contract',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 00:26:51'),(29,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 00:55:42'),(30,NULL,NULL,'CONTRACT_SIGNED_TENANT','Contract',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 00:56:10'),(31,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-07 01:02:17'),(32,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 01:27:42'),(33,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-07 01:38:08'),(34,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-07 01:38:08'),(35,NULL,NULL,'PAYMENT_INITIATED','Payment',1,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-07 01:43:12'),(36,NULL,NULL,'PAYMENT_INITIATED','Payment',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-07 01:51:22'),(37,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'127.0.0.1',NULL,'SUCCESS',NULL,'2026-04-07 02:00:12'),(38,NULL,NULL,'AUTH_LOGIN','User',2,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 03:28:14'),(39,NULL,NULL,'AUTH_LOGIN_FAILED','User',3,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:55:54'),(40,NULL,NULL,'AUTH_LOGIN_FAILED','User',3,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:06'),(41,NULL,NULL,'AUTH_LOGIN_FAILED','User',3,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:20'),(42,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:32'),(43,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:32'),(44,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:33'),(45,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:33'),(46,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:34'),(47,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-07 03:58:34'),(48,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-07 04:00:06'),(49,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-07 04:00:30'),(50,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-07 04:00:56'),(51,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'102.22.184.255',NULL,'FAILURE',NULL,'2026-04-07 04:01:13'),(52,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 04:02:01'),(53,NULL,NULL,'AUTH_LOGIN','User',3,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 04:12:09'),(54,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',1,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-07 04:43:46'),(55,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',1,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-07 04:43:59'),(56,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',1,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-07 04:44:06'),(57,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-07 04:44:49'),(58,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',1,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-07 04:45:19'),(59,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',1,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-07 04:50:32'),(60,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 09:14:45'),(61,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 09:52:03'),(62,NULL,NULL,'PROPERTY_CREATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-08 09:54:34'),(63,NULL,NULL,'UNIT_CREATED','Unit',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-08 10:09:06'),(64,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 10:50:16'),(65,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 10:52:53'),(66,NULL,NULL,'CONTRACT_CREATED','Contract',3,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:03:49'),(67,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',3,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-08 11:06:53'),(68,NULL,NULL,'CONTRACT_RESIGNED_LANDLORD','Contract',3,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:08:11'),(69,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:09:49'),(70,NULL,NULL,'CONTRACT_RESIGNED_TENANT','Contract',3,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:10:44'),(71,NULL,NULL,'PAYMENT_INITIATED','Payment',3,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-08 11:12:13'),(72,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:23:11'),(73,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'41.186.132.102',NULL,'SUCCESS',NULL,'2026-04-08 11:43:59'),(74,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-09 04:12:43'),(75,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 08:46:45'),(76,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 16:43:20'),(77,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 16:43:40'),(78,NULL,NULL,'UNIT_UPDATED','Unit',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 16:45:09'),(79,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 16:49:15'),(80,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 17:01:50'),(81,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 17:40:47'),(82,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-10 17:41:00'),(83,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 19:11:30'),(84,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 19:23:45'),(85,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 19:27:09'),(86,NULL,NULL,'AUTH_LOGIN_FAILED','User',6,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-10 19:28:09'),(87,NULL,NULL,'AUTH_LOGIN_FAILED','User',6,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-10 19:28:15'),(88,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-10 19:29:25'),(89,NULL,NULL,'AUTH_LOGIN_FAILED','User',1,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-10 19:31:22'),(90,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 19:53:30'),(91,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 20:46:41'),(92,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 20:47:46'),(93,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 20:48:38'),(94,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 21:04:15'),(95,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-10 21:07:50'),(96,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-11 09:18:25'),(97,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-11 10:14:05'),(98,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-11 10:39:57'),(99,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-11 13:38:35'),(100,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-11 13:39:59'),(101,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 19:48:49'),(102,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 21:12:52'),(103,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 21:39:21'),(104,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:00:48'),(105,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:01:33'),(106,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:02:10'),(107,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:04:06'),(108,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:05:18'),(109,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:20:22'),(110,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:22:11'),(111,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-12 22:22:29'),(112,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.140',NULL,'SUCCESS',NULL,'2026-04-13 09:19:25'),(113,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.140',NULL,'SUCCESS',NULL,'2026-04-13 10:04:36'),(114,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'129.222.149.140',NULL,'SUCCESS',NULL,'2026-04-13 10:28:05'),(115,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.140',NULL,'SUCCESS',NULL,'2026-04-13 10:31:57'),(116,NULL,NULL,'UNIT_CREATED','Unit',3,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-13 10:33:05'),(117,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'129.222.149.140',NULL,'SUCCESS',NULL,'2026-04-13 10:34:13'),(118,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 09:36:57'),(119,NULL,NULL,'MANUAL_PAYMENT_INITIATED','Payment',4,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-14 09:37:40'),(120,NULL,NULL,'AUTH_LOGIN_FAILED','User',4,NULL,NULL,'129.222.149.8',NULL,'FAILURE',NULL,'2026-04-14 09:40:03'),(121,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 09:40:16'),(122,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 10:00:25'),(123,NULL,NULL,'MANUAL_PAYMENT_INITIATED','Payment',5,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-14 10:00:59'),(124,NULL,NULL,'RECEIPT_UPLOADED','Payment',5,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-14 10:01:25'),(125,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 10:03:06'),(126,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 10:24:38'),(127,NULL,NULL,'MANUAL_PAYMENT_INITIATED','Payment',6,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-14 10:25:47'),(128,NULL,NULL,'RECEIPT_UPLOADED','Payment',6,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-14 10:25:59'),(129,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 10:27:04'),(130,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:37:54'),(131,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:02'),(132,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:03'),(133,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:03'),(134,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:03'),(135,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:03'),(136,NULL,NULL,'AUTH_LOGIN_FAILED','User',2,NULL,NULL,'127.0.0.1',NULL,'FAILURE',NULL,'2026-04-14 10:38:03'),(137,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 11:39:56'),(138,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.8',NULL,'SUCCESS',NULL,'2026-04-14 11:53:41'),(139,NULL,NULL,'CASH_PAYMENT_RECORDED','Payment',7,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-15 08:16:20'),(140,NULL,NULL,'AUTH_LOGIN_FAILED','User',5,NULL,NULL,'41.186.139.130',NULL,'FAILURE',NULL,'2026-04-15 08:28:03'),(141,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'41.186.139.130',NULL,'SUCCESS',NULL,'2026-04-15 08:28:17'),(142,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'102.22.184.255',NULL,'SUCCESS',NULL,'2026-04-16 00:00:04'),(143,NULL,NULL,'UNIT_CREATED','Unit',4,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 00:37:59'),(144,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'41.186.139.116',NULL,'SUCCESS',NULL,'2026-04-16 00:44:03'),(145,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 07:02:30'),(146,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 07:51:42'),(147,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 08:29:21'),(148,NULL,NULL,'PROPERTY_CREATED','Property',3,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 08:36:30'),(149,NULL,NULL,'UNIT_CREATED','Unit',5,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 08:43:29'),(150,NULL,NULL,'CONTRACT_CREATED','Contract',6,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 08:54:38'),(151,NULL,NULL,'CONTRACT_TAMPER_DETECTED','Contract',6,NULL,NULL,'public-verify',NULL,'SUCCESS',NULL,'2026-04-16 08:59:59'),(152,NULL,NULL,'CONTRACT_RESIGNED_LANDLORD','Contract',6,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 09:01:13'),(153,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 09:02:59'),(154,NULL,NULL,'CONTRACT_RESIGNED_TENANT','Contract',6,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 09:05:23'),(155,NULL,NULL,'MULTI_PAYMENT_INITIATED','Payment',8,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 09:08:53'),(156,NULL,NULL,'MANUAL_PAYMENT_INITIATED','Payment',10,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 09:12:04'),(157,NULL,NULL,'RECEIPT_UPLOADED','Payment',10,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 09:12:17'),(158,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 09:13:32'),(159,NULL,NULL,'MANUAL_PAYMENT_CONFIRMED','Payment',10,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-16 09:15:22'),(160,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.152',NULL,'SUCCESS',NULL,'2026-04-16 12:34:11'),(161,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.36',NULL,'SUCCESS',NULL,'2026-04-20 11:29:03'),(162,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-20 11:30:23'),(163,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-20 11:30:45'),(164,NULL,NULL,'PROPERTY_UPDATED','Property',2,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-20 11:38:30'),(165,NULL,NULL,'UNIT_CREATED','Unit',6,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-20 12:21:21'),(166,NULL,NULL,'UNIT_CREATED','Unit',7,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-20 12:22:28'),(167,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 09:38:54'),(168,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 09:51:42'),(169,NULL,NULL,'PROPERTY_CREATED','Property',4,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 10:09:31'),(170,NULL,NULL,'PROPERTY_UPDATED','Property',4,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 10:13:57'),(171,NULL,NULL,'UNIT_CREATED','Unit',8,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 10:21:43'),(172,NULL,NULL,'CASH_PAYMENT_RECORDED','Payment',11,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 10:51:54'),(173,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 10:57:18'),(174,NULL,NULL,'MULTI_PAYMENT_INITIATED','Payment',12,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 11:03:34'),(175,NULL,NULL,'MANUAL_PAYMENT_INITIATED','Payment',13,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 11:06:07'),(176,NULL,NULL,'RECEIPT_UPLOADED','Payment',13,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 11:06:08'),(177,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 11:07:14'),(178,NULL,NULL,'MANUAL_PAYMENT_CONFIRMED','Payment',13,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 11:08:30'),(179,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 11:09:16'),(180,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 11:10:18'),(181,NULL,NULL,'AUTH_LOGIN_FAILED','User',5,NULL,NULL,'129.222.149.219',NULL,'FAILURE',NULL,'2026-04-21 11:11:42'),(182,NULL,NULL,'AUTH_LOGIN_FAILED','User',5,NULL,NULL,'129.222.149.219',NULL,'FAILURE',NULL,'2026-04-21 11:11:56'),(183,NULL,NULL,'AUTH_LOGIN','User',5,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 11:12:08'),(184,NULL,NULL,'AUTH_LOGIN','User',1,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 11:15:02'),(185,NULL,NULL,'AUTH_LOGIN','User',6,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 12:06:55'),(186,NULL,NULL,'AUTH_LOGIN','User',4,NULL,NULL,'129.222.149.219',NULL,'SUCCESS',NULL,'2026-04-21 13:24:19'),(187,NULL,NULL,'CASH_PAYMENT_RECORDED','Payment',14,NULL,NULL,NULL,NULL,'SUCCESS',NULL,'2026-04-21 23:04:23');
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commissions`
--

DROP TABLE IF EXISTS `commissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `admin_notes` text COLLATE utf8mb4_unicode_ci,
  `amount` decimal(12,2) DEFAULT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `currency` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `property_id` bigint DEFAULT NULL,
  `property_ref` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('PENDING','APPROVED','PAID') COLLATE utf8mb4_unicode_ci NOT NULL,
  `unit_ref` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `agent_id` bigint NOT NULL,
  `linkage_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c7yyqmab8kbrljmak8v1xbthl` (`linkage_id`),
  KEY `idx_commission_agent` (`agent_id`),
  KEY `idx_commission_status` (`status`),
  CONSTRAINT `FKfgfpu9euyhk47iuj1gnqvuqdi` FOREIGN KEY (`linkage_id`) REFERENCES `property_linkages` (`id`),
  CONSTRAINT `FKqyeskyal1ojww9ntypq7d4ltr` FOREIGN KEY (`agent_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `commissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contracts`
--

DROP TABLE IF EXISTS `contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contracts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` bigint NOT NULL,
  `landlord_id` bigint NOT NULL,
  `unit_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `monthly_rent` decimal(12,2) NOT NULL,
  `deposit_amount` decimal(12,2) DEFAULT NULL,
  `status` enum('DRAFT','PENDING_SIGNATURE','ACTIVE','EXPIRED','TERMINATED','RENEWED','PENDING_RESIGN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT',
  `terms_conditions` longtext COLLATE utf8mb4_unicode_ci,
  `special_clauses` text COLLATE utf8mb4_unicode_ci,
  `landlord_signed_at` datetime DEFAULT NULL,
  `tenant_signed_at` datetime DEFAULT NULL,
  `landlord_signature_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_signature_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contract_hash` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `blockchain_tx_hash` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `blockchain_network` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `blockchain_timestamp` datetime DEFAULT NULL,
  `blockchain_block_number` bigint DEFAULT NULL,
  `terminated_at` datetime DEFAULT NULL,
  `termination_reason` text COLLATE utf8mb4_unicode_ci,
  `pdf_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `landlord_signature` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_signature` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tamper_detected_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_number` (`contract_number`),
  KEY `idx_contracts_tenant` (`tenant_id`),
  KEY `idx_contracts_landlord` (`landlord_id`),
  KEY `idx_contracts_unit` (`unit_id`),
  KEY `idx_contracts_status` (`status`),
  KEY `idx_contracts_hash` (`contract_hash`),
  CONSTRAINT `fk_contracts_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_contracts_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_contracts_unit` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contracts`
--

LOCK TABLES `contracts` WRITE;
/*!40000 ALTER TABLE `contracts` DISABLE KEYS */;
INSERT INTO `contracts` VALUES (1,'CON-202604-01001',3,2,1,'2026-04-01','2027-04-01',10.00,10.00,'ACTIVE','Standard terms: Just pay on the 1st of each month','','2026-04-07 00:26:51','2026-04-07 00:56:10','102.22.184.255','102.22.184.255','b433b9720db9eced3b85cc4239e95fd28515d50e5afbf67d53edd25aa7ac6249','0x31efe0d85f2e06b19c234102764a8ebd','RentMIS-CryptoRef-v1','2026-04-07 00:56:10',NULL,NULL,NULL,NULL,'2026-04-07 00:26:02','2026-04-07 06:53:11','landlord@test.rw','tenant@test.rw','15856ea2a0ff3ee660193326649a68223636b41668c0bf2e036c561d598801b8','0c3cc96ce24e3e64a07b5c169439e01afcdcfcb80b257bbdc720617f3eecdcd3',NULL),(3,'CON-202604-01002',5,4,2,'2026-04-08','2027-04-08',10.00,10.00,'ACTIVE','Payment must be made on 1st of each month','No pets allowed','2026-04-08 11:08:11','2026-04-08 11:10:43','41.186.132.102','41.186.132.102','ecd0744e4ab625aeaf0a27ba90035d4dbd453a0d2f759e2344f61a468dee6f86','0x7596bf6119b7c629a0063e5a1d8c7004','RentMIS-CryptoRef-v1','2026-04-08 11:10:43',NULL,NULL,NULL,NULL,'2026-04-08 11:03:49','2026-04-08 11:10:44','damien@test.rw','pierre@test.rw','41559b4bf2e905f1b029f2ae405618e6905e3ec26e6484d221f33d3d212c9c77','37a524d5a5e74d1d4087a96aeca365d624c806885e7a5765cab6f55d4d080f3b','2026-04-08 11:06:53'),(6,'CON-202604-01003',5,4,5,'2026-04-30','2026-07-31',750000.00,1000.00,'ACTIVE','Rent must be deposited on every 1st of each month','','2026-04-16 09:01:13','2026-04-16 09:05:23','129.222.149.152','129.222.149.152','82304e433aae4b7ad5a8bb29f97c4a476b35b6ed4a63c0fb04dd68278f154e2f','0xde77977f354be6b494e3d17c21ac5579','RentMIS-CryptoRef-v1','2026-04-16 09:05:23',NULL,NULL,NULL,NULL,'2026-04-16 08:54:38','2026-04-16 09:05:23','damien@test.rw','pierre@test.rw','cc09ee5a4201c2a959997b50f1f993c532bc5b9365638331dc4f8beb5acc04ec','8121e744b5a0a7425bb769ecd5a0d39ab076248e4d51500a08add240618a5ef8','2026-04-16 08:59:59');
/*!40000 ALTER TABLE `contracts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `invoice_number` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ebm_invoice_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issued_at` datetime NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `tax_amount` decimal(12,2) DEFAULT '0.00',
  `total_amount` decimal(12,2) NOT NULL,
  `currency` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'RWF',
  `qr_code` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pdf_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ebm_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ebm_response` text COLLATE utf8mb4_unicode_ci,
  `ebm_submitted_at` datetime DEFAULT NULL,
  `ebm_retry_count` int DEFAULT '0',
  `ebm_verification_code` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verification_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_number` (`invoice_number`),
  UNIQUE KEY `uk_payment_id` (`payment_id`),
  KEY `idx_invoices_ebm` (`ebm_invoice_number`),
  KEY `idx_invoices_payment` (`payment_id`),
  KEY `idx_invoices_number` (`invoice_number`),
  CONSTRAINT `fk_invoices_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (1,2,'INV-202604-001001','EBM-53','2026-04-07 02:00:14',8.47,1.53,10.00,'RWF','6528981',NULL,'SUCCESS','{\"success\":true,\"message\":null,\"data\":{\"response\":{\"resultCd\":\"000\",\"resultMsg\":\"It is succeeded\",\"resultDt\":\"20260407040021\",\"data\":{\"rcptNo\":53,\"intrlData\":\"4KN3LOUSNF5MGCBNJISNH2QS2A\",\"rcptSign\":\"SZRX4PBYQKXZ7NXV\",\"totRcptNo\":62,\"vsdcRcptPbctDate\":\"20260407040021\",\"sdcId\":\"SDC010000033\",\"mrcNo\":\"WIS00000033\"}},\"request\":{\"tin\":\"999555888\",\"bhfId\":\"01\",\"invcNo\":242,\"orgInvcNo\":0,\"custTin\":null,\"prcOrdCd\":\"000000\",\"custNm\":\"Marie Uwase\",\"salesTyCd\":\"N\",\"rcptTyCd\":\"S\",\"pmtTyCd\":\"05\",\"salesSttsCd\":\"02\",\"cfmDt\":\"20260407020020\",\"salesDt\":\"20260407\",\"stockRlsDt\":null,\"cnclReqDt\":null,\"cnclDt\":null,\"rfdDt\":null,\"rfdRsnCd\":null,\"totItemCnt\":1,\"taxblAmtA\":10.0,\"taxblAmtB\":0.0,\"taxblAmtC\":0.0,\"taxblAmtD\":0.0,\"taxRtA\":0.0,\"taxRtB\":18.0,\"taxRtC\":0.0,\"taxRtD\":0.0,\"taxAmtA\":0.0,\"taxAmtB\":0.0,\"taxAmtC\":0.0,\"taxAmtD\":0.0,\"totTaxblAmt\":10.0,\"totTaxAmt\":0.0,\"totAmt\":10.0,\"prchrAcptcYn\":\"Y\",\"remark\":null,\"regrId\":\"Admin\",\"regrNm\":\"Admin\",\"modrId\":\"Admin\",\"modrNm\":\"Admin\",\"receipt\":{\"custTin\":null,\"custMblNo\":\"+250788654321\",\"rptNo\":242,\"trdeNm\":\"\",\"adrs\":\"Umujyi wa Kigali,Nyarugenge\",\"topMsg\":\"Munzi \\nUmujyi wa Kigali_Nyarugenge_Gitega\\nTEL :0786783148\\nEMAIL :munzi.urnaud@gmail.com\\nTIN :999555888\\n\",\"btmMsg\":\"THANK YOU\",\"prchrAcptcYn\":\"Y\"},\"itemList\":[{\"itemSeq\":1,\"itemCd\":\"RW1BJBG0000001\",\"itemClsCd\":\"5041230400\",\"itemNm\":\"Dodo\",\"bcd\":null,\"pkgUnitCd\":\"BJ\",\"pkg\":1.0,\"qtyUnitCd\":\"BG\",\"qty\":1.0,\"itemExprDt\":null,\"prc\":10.0,\"splyAmt\":10.0,\"totDcAmt\":0.0,\"taxblAmt\":10.0,\"taxTyCd\":\"A\",\"taxAmt\":0.0,\"totAmt\":10.0,\"dcRt\":0,\"dcAmt\":0.0,\"isrccCd\":null,\"isrccNm\":null,\"isrcRt\":null,\"isrcAmt\":null}]},\"default_mrc\":\"WIS00000029\",\"customerId\":\"3633d7f1-e6e4-45a6-afd4-cb3fe9d9b6a0\",\"companyId\":null,\"deskId\":null,\"ebmVerificationCode\":\"6528981\"},\"correlationId\":null,\"responseId\":null,\"responseTime\":null}','2026-04-07 02:00:23',0,'6528981','https://qa.inkomane.rw/ebm/6528981','2026-04-07 02:00:14','2026-04-07 05:44:32','tenant@test.rw','tenant@test.rw'),(2,3,'INV-202604-001004','EBM-57','2026-04-08 11:13:42',8.47,1.53,10.00,'RWF','6837227',NULL,'SUCCESS','{\"success\":true,\"message\":null,\"data\":{\"response\":{\"resultCd\":\"000\",\"resultMsg\":\"It is succeeded\",\"resultDt\":\"20260412224132\",\"data\":{\"rcptNo\":57,\"intrlData\":\"TG63FU5Q7ADPPHGKTKTY5A6QAA\",\"rcptSign\":\"5POHLHTOOU5AN3TB\",\"totRcptNo\":66,\"vsdcRcptPbctDate\":\"20260412224132\",\"sdcId\":\"SDC010000033\",\"mrcNo\":\"WIS00000033\"}},\"request\":{\"tin\":\"999555888\",\"bhfId\":\"01\",\"invcNo\":246,\"orgInvcNo\":0,\"custTin\":null,\"prcOrdCd\":\"000000\",\"custNm\":\"Jean Pierre MUTUYIMANA\",\"salesTyCd\":\"N\",\"rcptTyCd\":\"S\",\"pmtTyCd\":\"05\",\"salesSttsCd\":\"02\",\"cfmDt\":\"20260412204132\",\"salesDt\":\"20260412\",\"stockRlsDt\":null,\"cnclReqDt\":null,\"cnclDt\":null,\"rfdDt\":null,\"rfdRsnCd\":null,\"totItemCnt\":1,\"taxblAmtA\":10.0,\"taxblAmtB\":0.0,\"taxblAmtC\":0.0,\"taxblAmtD\":0.0,\"taxRtA\":0.0,\"taxRtB\":18.0,\"taxRtC\":0.0,\"taxRtD\":0.0,\"taxAmtA\":0.0,\"taxAmtB\":0.0,\"taxAmtC\":0.0,\"taxAmtD\":0.0,\"totTaxblAmt\":10.0,\"totTaxAmt\":0.0,\"totAmt\":10.0,\"prchrAcptcYn\":\"Y\",\"remark\":null,\"regrId\":\"Admin\",\"regrNm\":\"Admin\",\"modrId\":\"Admin\",\"modrNm\":\"Admin\",\"receipt\":{\"custTin\":null,\"custMblNo\":\"+250788009988\",\"rptNo\":246,\"trdeNm\":\"\",\"adrs\":\"Umujyi wa Kigali,Nyarugenge\",\"topMsg\":\"Munzi \\nUmujyi wa Kigali_Nyarugenge_Gitega\\nTEL :0786783148\\nEMAIL :munzi.urnaud@gmail.com\\nTIN :999555888\\n\",\"btmMsg\":\"THANK YOU\",\"prchrAcptcYn\":\"Y\"},\"itemList\":[{\"itemSeq\":1,\"itemCd\":\"RW1BJBG0000001\",\"itemClsCd\":\"5041230400\",\"itemNm\":\"Dodo\",\"bcd\":null,\"pkgUnitCd\":\"BJ\",\"pkg\":1.0,\"qtyUnitCd\":\"BG\",\"qty\":1.0,\"itemExprDt\":null,\"prc\":10.0,\"splyAmt\":10.0,\"totDcAmt\":0.0,\"taxblAmt\":10.0,\"taxTyCd\":\"A\",\"taxAmt\":0.0,\"totAmt\":10.0,\"dcRt\":0,\"dcAmt\":0.0,\"isrccCd\":null,\"isrccNm\":null,\"isrcRt\":null,\"isrcAmt\":null}]},\"default_mrc\":\"WIS00000029\",\"customerId\":\"b1c6e35a-187b-4bd1-8a1d-986907903122\",\"companyId\":null,\"deskId\":null,\"ebmVerificationCode\":\"6837227\"},\"correlationId\":null,\"responseId\":null,\"responseTime\":null}','2026-04-12 20:41:34',1,'6837227','https://qa.inkomane.rw/ebm/6837227','2026-04-08 11:13:42','2026-04-12 20:41:34','pierre@test.rw','damien@test.rw'),(3,8,'INV-202604-001007','EBM-58','2026-04-16 09:17:50',16.95,3.05,20.00,'RWF','7849946',NULL,'SUCCESS','{\"success\":true,\"message\":null,\"data\":{\"response\":{\"resultCd\":\"000\",\"resultMsg\":\"It is succeeded\",\"resultDt\":\"20260416111811\",\"data\":{\"rcptNo\":58,\"intrlData\":\"343P7W47OCCJMGBR4EFCCBW4HY\",\"rcptSign\":\"5YQQTP355K4ILPME\",\"totRcptNo\":67,\"vsdcRcptPbctDate\":\"20260416111811\",\"sdcId\":\"SDC010000033\",\"mrcNo\":\"WIS00000033\"}},\"request\":{\"tin\":\"999555888\",\"bhfId\":\"01\",\"invcNo\":247,\"orgInvcNo\":0,\"custTin\":null,\"prcOrdCd\":\"000000\",\"custNm\":\"Jean Pierre MUTUYIMANA\",\"salesTyCd\":\"N\",\"rcptTyCd\":\"S\",\"pmtTyCd\":\"05\",\"salesSttsCd\":\"02\",\"cfmDt\":\"20260416091810\",\"salesDt\":\"20260416\",\"stockRlsDt\":null,\"cnclReqDt\":null,\"cnclDt\":null,\"rfdDt\":null,\"rfdRsnCd\":null,\"totItemCnt\":1,\"taxblAmtA\":20.0,\"taxblAmtB\":0.0,\"taxblAmtC\":0.0,\"taxblAmtD\":0.0,\"taxRtA\":0.0,\"taxRtB\":18.0,\"taxRtC\":0.0,\"taxRtD\":0.0,\"taxAmtA\":0.0,\"taxAmtB\":0.0,\"taxAmtC\":0.0,\"taxAmtD\":0.0,\"totTaxblAmt\":20.0,\"totTaxAmt\":0.0,\"totAmt\":20.0,\"prchrAcptcYn\":\"Y\",\"remark\":null,\"regrId\":\"Admin\",\"regrNm\":\"Admin\",\"modrId\":\"Admin\",\"modrNm\":\"Admin\",\"receipt\":{\"custTin\":null,\"custMblNo\":\"+250788009988\",\"rptNo\":247,\"trdeNm\":\"\",\"adrs\":\"Umujyi wa Kigali,Nyarugenge\",\"topMsg\":\"Munzi \\nUmujyi wa Kigali_Nyarugenge_Gitega\\nTEL :0786783148\\nEMAIL :munzi.urnaud@gmail.com\\nTIN :999555888\\n\",\"btmMsg\":\"THANK YOU\",\"prchrAcptcYn\":\"Y\"},\"itemList\":[{\"itemSeq\":1,\"itemCd\":\"RW1BJBG0000001\",\"itemClsCd\":\"5041230400\",\"itemNm\":\"Dodo\",\"bcd\":null,\"pkgUnitCd\":\"BJ\",\"pkg\":1.0,\"qtyUnitCd\":\"BG\",\"qty\":1.0,\"itemExprDt\":null,\"prc\":20.0,\"splyAmt\":20.0,\"totDcAmt\":0.0,\"taxblAmt\":20.0,\"taxTyCd\":\"A\",\"taxAmt\":0.0,\"totAmt\":20.0,\"dcRt\":0,\"dcAmt\":0.0,\"isrccCd\":null,\"isrccNm\":null,\"isrcRt\":null,\"isrcAmt\":null}]},\"default_mrc\":\"WIS00000029\",\"customerId\":\"b1c6e35a-187b-4bd1-8a1d-986907903122\",\"companyId\":null,\"deskId\":null,\"ebmVerificationCode\":\"7849946\"},\"correlationId\":null,\"responseId\":null,\"responseTime\":null}','2026-04-16 09:18:13',2,'7849946','https://qa.inkomane.rw/ebm/7849946','2026-04-16 09:17:50','2026-04-16 09:18:13','damien@test.rw','damien@test.rw'),(5,7,'INV-202604-001002','EBM-59','2026-04-21 10:42:24',8.47,1.53,10.00,'RWF','1252737',NULL,'SUCCESS','{\"success\":true,\"message\":null,\"data\":{\"response\":{\"resultCd\":\"000\",\"resultMsg\":\"It is succeeded\",\"resultDt\":\"20260421124248\",\"data\":{\"rcptNo\":59,\"intrlData\":\"V2DZA37SIMKXDJU3TQSINO7ZMU\",\"rcptSign\":\"7P7KC5PCBJXGIV3O\",\"totRcptNo\":68,\"vsdcRcptPbctDate\":\"20260421124248\",\"sdcId\":\"SDC010000033\",\"mrcNo\":\"WIS00000033\"}},\"request\":{\"tin\":\"999555888\",\"bhfId\":\"01\",\"invcNo\":248,\"orgInvcNo\":0,\"custTin\":null,\"prcOrdCd\":\"000000\",\"custNm\":\"Jean Pierre MUTUYIMANA\",\"salesTyCd\":\"N\",\"rcptTyCd\":\"S\",\"pmtTyCd\":\"01\",\"salesSttsCd\":\"02\",\"cfmDt\":\"20260421104247\",\"salesDt\":\"20260421\",\"stockRlsDt\":null,\"cnclReqDt\":null,\"cnclDt\":null,\"rfdDt\":null,\"rfdRsnCd\":null,\"totItemCnt\":1,\"taxblAmtA\":10.0,\"taxblAmtB\":0.0,\"taxblAmtC\":0.0,\"taxblAmtD\":0.0,\"taxRtA\":0.0,\"taxRtB\":18.0,\"taxRtC\":0.0,\"taxRtD\":0.0,\"taxAmtA\":0.0,\"taxAmtB\":0.0,\"taxAmtC\":0.0,\"taxAmtD\":0.0,\"totTaxblAmt\":10.0,\"totTaxAmt\":0.0,\"totAmt\":10.0,\"prchrAcptcYn\":\"Y\",\"remark\":null,\"regrId\":\"Admin\",\"regrNm\":\"Admin\",\"modrId\":\"Admin\",\"modrNm\":\"Admin\",\"receipt\":{\"custTin\":null,\"custMblNo\":\"+250788009988\",\"rptNo\":248,\"trdeNm\":\"\",\"adrs\":\"Umujyi wa Kigali,Nyarugenge\",\"topMsg\":\"Munzi \\nUmujyi wa Kigali_Nyarugenge_Gitega\\nTEL :0786783148\\nEMAIL :munzi.urnaud@gmail.com\\nTIN :999555888\\n\",\"btmMsg\":\"THANK YOU\",\"prchrAcptcYn\":\"Y\"},\"itemList\":[{\"itemSeq\":1,\"itemCd\":\"RW1BJBG0000001\",\"itemClsCd\":\"5041230400\",\"itemNm\":\"Dodo\",\"bcd\":null,\"pkgUnitCd\":\"BJ\",\"pkg\":1.0,\"qtyUnitCd\":\"BG\",\"qty\":1.0,\"itemExprDt\":null,\"prc\":10.0,\"splyAmt\":10.0,\"totDcAmt\":0.0,\"taxblAmt\":10.0,\"taxTyCd\":\"A\",\"taxAmt\":0.0,\"totAmt\":10.0,\"dcRt\":0,\"dcAmt\":0.0,\"isrccCd\":null,\"isrccNm\":null,\"isrcRt\":null,\"isrcAmt\":null}]},\"default_mrc\":\"WIS00000029\",\"customerId\":\"b1c6e35a-187b-4bd1-8a1d-986907903122\",\"companyId\":null,\"deskId\":null,\"ebmVerificationCode\":\"1252737\"},\"correlationId\":null,\"responseId\":null,\"responseTime\":null}','2026-04-21 10:42:50',3,'1252737','https://qa.inkomane.rw/ebm/1252737','2026-04-21 10:42:24','2026-04-21 10:42:50','damien@test.rw','damien@test.rw'),(6,13,'INV-202604-001006','EBM-60','2026-04-21 11:10:33',8.47,1.53,10.00,'RWF','3031261',NULL,'SUCCESS','{\"success\":true,\"message\":null,\"data\":{\"response\":{\"resultCd\":\"000\",\"resultMsg\":\"It is succeeded\",\"resultDt\":\"20260421131042\",\"data\":{\"rcptNo\":60,\"intrlData\":\"OENMLOIYOB2UK5YAA6X4XOEPLY\",\"rcptSign\":\"M6DOOXSG66LW23D4\",\"totRcptNo\":69,\"vsdcRcptPbctDate\":\"20260421131042\",\"sdcId\":\"SDC010000033\",\"mrcNo\":\"WIS00000033\"}},\"request\":{\"tin\":\"999555888\",\"bhfId\":\"01\",\"invcNo\":249,\"orgInvcNo\":0,\"custTin\":null,\"prcOrdCd\":\"000000\",\"custNm\":\"Jean Pierre MUTUYIMANA\",\"salesTyCd\":\"N\",\"rcptTyCd\":\"S\",\"pmtTyCd\":\"06\",\"salesSttsCd\":\"02\",\"cfmDt\":\"20260421111041\",\"salesDt\":\"20260421\",\"stockRlsDt\":null,\"cnclReqDt\":null,\"cnclDt\":null,\"rfdDt\":null,\"rfdRsnCd\":null,\"totItemCnt\":1,\"taxblAmtA\":10.0,\"taxblAmtB\":0.0,\"taxblAmtC\":0.0,\"taxblAmtD\":0.0,\"taxRtA\":0.0,\"taxRtB\":18.0,\"taxRtC\":0.0,\"taxRtD\":0.0,\"taxAmtA\":0.0,\"taxAmtB\":0.0,\"taxAmtC\":0.0,\"taxAmtD\":0.0,\"totTaxblAmt\":10.0,\"totTaxAmt\":0.0,\"totAmt\":10.0,\"prchrAcptcYn\":\"Y\",\"remark\":null,\"regrId\":\"Admin\",\"regrNm\":\"Admin\",\"modrId\":\"Admin\",\"modrNm\":\"Admin\",\"receipt\":{\"custTin\":null,\"custMblNo\":\"+250788009988\",\"rptNo\":249,\"trdeNm\":\"\",\"adrs\":\"Umujyi wa Kigali,Nyarugenge\",\"topMsg\":\"Munzi \\nUmujyi wa Kigali_Nyarugenge_Gitega\\nTEL :0786783148\\nEMAIL :munzi.urnaud@gmail.com\\nTIN :999555888\\n\",\"btmMsg\":\"THANK YOU\",\"prchrAcptcYn\":\"Y\"},\"itemList\":[{\"itemSeq\":1,\"itemCd\":\"RW1BJBG0000001\",\"itemClsCd\":\"5041230400\",\"itemNm\":\"Dodo\",\"bcd\":null,\"pkgUnitCd\":\"BJ\",\"pkg\":1.0,\"qtyUnitCd\":\"BG\",\"qty\":1.0,\"itemExprDt\":null,\"prc\":10.0,\"splyAmt\":10.0,\"totDcAmt\":0.0,\"taxblAmt\":10.0,\"taxTyCd\":\"A\",\"taxAmt\":0.0,\"totAmt\":10.0,\"dcRt\":0,\"dcAmt\":0.0,\"isrccCd\":null,\"isrccNm\":null,\"isrcRt\":null,\"isrcAmt\":null}]},\"default_mrc\":\"WIS00000029\",\"customerId\":\"b1c6e35a-187b-4bd1-8a1d-986907903122\",\"companyId\":null,\"deskId\":null,\"ebmVerificationCode\":\"3031261\"},\"correlationId\":null,\"responseId\":null,\"responseTime\":null}','2026-04-21 11:10:43',2,'3031261','https://qa.inkomane.rw/ebm/3031261','2026-04-21 11:10:33','2026-04-21 11:10:43','damien@test.rw','damien@test.rw');
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reference_number` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` bigint NOT NULL,
  `unit_id` bigint NOT NULL,
  `contract_id` bigint DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `penalty_amount` decimal(12,2) DEFAULT '0.00',
  `total_amount` decimal(12,2) NOT NULL,
  `currency` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'RWF',
  `status` enum('PENDING','PROCESSING','COMPLETED','FAILED','REFUNDED','CANCELLED','PENDING_CONFIRMATION') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_period_month` int DEFAULT NULL,
  `payment_period_year` int DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `glspay_transaction_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `glspay_checkout_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `glspay_webhook_data` text COLLATE utf8mb4_unicode_ci,
  `glspay_signature_verified` tinyint(1) DEFAULT '0',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `confirmed_at` datetime(6) DEFAULT NULL,
  `receipt_file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `receipt_original_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `confirmed_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reference_number` (`reference_number`),
  UNIQUE KEY `uk_idempotency_key` (`idempotency_key`),
  KEY `idx_payments_tenant` (`tenant_id`),
  KEY `idx_payments_unit` (`unit_id`),
  KEY `idx_payments_status` (`status`),
  KEY `idx_payments_reference` (`reference_number`),
  KEY `idx_payments_period` (`payment_period_month`,`payment_period_year`),
  KEY `idx_payments_glspay_tx` (`glspay_transaction_id`),
  KEY `fk_payments_contract` (`contract_id`),
  KEY `FKbh1kwjvw3n5ui0ybbkuub7vlx` (`confirmed_by`),
  CONSTRAINT `fk_payments_contract` FOREIGN KEY (`contract_id`) REFERENCES `contracts` (`id`),
  CONSTRAINT `fk_payments_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_payments_unit` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`),
  CONSTRAINT `FKbh1kwjvw3n5ui0ybbkuub7vlx` FOREIGN KEY (`confirmed_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (2,'PAY-20260407035121-1001',3,1,1,10.00,0.00,10.00,'RWF','COMPLETED','GLSPAY',4,2026,NULL,'2026-04-07 02:00:14','efb7b6c1-8434-41a1-9984-02502acfd331','http://86.48.7.218:3030/checkout/efb7b6c1-8434-41a1-9984-02502acfd331',NULL,0,NULL,'pay-1-4-2026','2026-04-07 01:51:21','2026-04-07 02:00:23','tenant@test.rw','tenant@test.rw',NULL,NULL,NULL,NULL,NULL),(3,'PAY-20260408131213-1003',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','GLSPAY',4,2026,NULL,'2026-04-08 11:13:42','6fe629d7-ba75-49fd-9c7f-c0cd47c3f739','http://86.48.7.218:3030/checkout/6fe629d7-ba75-49fd-9c7f-c0cd47c3f739',NULL,0,NULL,'pay-2-4-2026','2026-04-08 11:12:13','2026-04-08 11:13:43','pierre@test.rw','pierre@test.rw',NULL,NULL,NULL,NULL,NULL),(6,'PAY-20260414122547-1001',5,2,3,10.00,0.00,10.00,'RWF','PENDING_CONFIRMATION','BANK_TRANSFER',5,2026,NULL,NULL,NULL,NULL,NULL,0,NULL,'manual-2-1776162346473-2026-5','2026-04-14 10:25:47','2026-04-14 12:34:17','pierre@test.rw','pierre@test.rw',NULL,NULL,'receipt-PAY-20260414122547-1001-1776162359306.jpg','bank_slip.jpg',NULL),(7,'PAY-20260415101620-1001',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','CASH',6,2026,NULL,'2026-04-15 08:16:20',NULL,NULL,NULL,0,NULL,'dd78489e-31e5-4cc7-a4c8-55c26eff703e','2026-04-15 08:16:20','2026-04-15 08:16:20','damien@test.rw','damien@test.rw',NULL,'2026-04-15 08:16:20.331219',NULL,NULL,4),(8,'PAY-20260416110853-1004',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','GLSPAY',7,2026,NULL,'2026-04-16 09:10:05','d979877b-8cbb-423e-abcd-4803827a42be','http://86.48.7.218:3030/checkout/d979877b-8cbb-423e-abcd-4803827a42be',NULL,0,NULL,'bulk-2-1776330532067-2026-7','2026-04-16 09:08:53','2026-04-20 15:12:25','pierre@test.rw','pierre@test.rw',NULL,NULL,NULL,NULL,NULL),(9,'PAY-20260416110853-1005',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','GLSPAY',8,2026,NULL,'2026-04-16 09:10:05','d979877b-8cbb-423e-abcd-4803827a42be','http://86.48.7.218:3030/checkout/d979877b-8cbb-423e-abcd-4803827a42be',NULL,0,NULL,'bulk-2-1776330532067-2026-8','2026-04-16 09:08:53','2026-04-20 15:12:25','pierre@test.rw','pierre@test.rw',NULL,NULL,NULL,NULL,NULL),(10,'PAY-20260416111203-1006',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','BANK_TRANSFER',9,2026,NULL,'2026-04-16 09:15:22',NULL,NULL,NULL,0,NULL,'manual-2-1776330721900-2026-9','2026-04-16 09:12:04','2026-04-16 09:15:22','pierre@test.rw','damien@test.rw',NULL,'2026-04-16 09:15:22.126126','receipt-PAY-20260416111203-1006-1776330736849.jpg','bank_slip.jpg',4),(11,'PAY-20260421125154-1003',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','CASH',10,2026,NULL,'2026-04-21 10:51:54',NULL,NULL,NULL,0,NULL,'da70d3a5-18c5-48e7-a807-0952e7a779dd','2026-04-21 10:51:54','2026-04-21 10:51:54','damien@test.rw','damien@test.rw',NULL,'2026-04-21 10:51:54.023253',NULL,NULL,4),(12,'PAY-20260421130333-1004',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','GLSPAY',11,2026,NULL,'2026-04-21 11:04:14','9a70a915-e02a-4fe9-b79b-cd48983c3f47','http://86.48.7.218:3030/checkout/9a70a915-e02a-4fe9-b79b-cd48983c3f47',NULL,0,NULL,'bulk-2-1776769412796-2026-11','2026-04-21 11:03:33','2026-04-21 11:04:14','pierre@test.rw','pierre@test.rw',NULL,NULL,NULL,NULL,NULL),(13,'PAY-20260421130606-1005',5,2,3,10.00,0.00,10.00,'RWF','COMPLETED','BANK_TRANSFER',12,2026,NULL,'2026-04-21 11:08:30',NULL,NULL,NULL,0,NULL,'manual-2-1776769565980-2026-12','2026-04-21 11:06:07','2026-04-21 11:08:30','pierre@test.rw','damien@test.rw',NULL,'2026-04-21 11:08:29.994230','receipt-PAY-20260421130606-1005-1776769567808.png','auth_login_pic.png',4),(14,'PAY-20260422010422-1001',5,5,6,750000.00,0.00,750000.00,'RWF','COMPLETED','CASH',4,2026,NULL,'2026-04-21 23:04:23',NULL,NULL,NULL,0,NULL,'d62a112c-613a-4cd5-9c2a-b2844a850e1e','2026-04-21 23:04:23','2026-04-21 23:04:23','damien@test.rw','damien@test.rw',NULL,'2026-04-21 23:04:22.984696',NULL,NULL,4);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `properties`
--

DROP TABLE IF EXISTS `properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `properties` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sector` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cell` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `property_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_units` int DEFAULT '0',
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `landlord_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` enum('FULL_HOUSE','APARTMENT_BUILDING','COMPLEX') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `has_wings` bit(1) DEFAULT NULL,
  `house_area_sqm` decimal(8,2) DEFAULT NULL,
  `house_rent_amount` decimal(12,2) DEFAULT NULL,
  `land_use` enum('RESIDENTIAL','COMMERCIAL','MIXED') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `num_bathrooms` int DEFAULT NULL,
  `num_bedrooms` int DEFAULT NULL,
  `parking_spaces` int DEFAULT NULL,
  `province` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `upi` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `village` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_area_sqm` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_properties_landlord` (`landlord_id`),
  KEY `idx_properties_status` (`is_active`),
  CONSTRAINT `fk_properties_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `properties`
--

LOCK TABLES `properties` WRITE;
/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
INSERT INTO `properties` VALUES (1,'Kigali Heights Apartments','KG 123 St, Kacyiru','Kigali','Gasabo','Kacyiru',NULL,'Modern apartment complex','APARTMENT',1,NULL,NULL,NULL,1,2,'2026-04-06 20:27:37','2026-04-06 20:27:38','landlord@test.rw','landlord@test.rw',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'Damien House Plaza','KK500St',NULL,'Kicukiro','Kicukiro',NULL,NULL,'COMMERCIAL',5,NULL,NULL,NULL,1,4,'2026-04-08 09:54:34','2026-04-20 12:22:28','damien@test.rw','damien@test.rw','COMPLEX',_binary '\0',NULL,NULL,'MIXED',NULL,NULL,NULL,'Kigali City',NULL,NULL,30.00),(3,'Ben House','KK501St',NULL,'Kicukiro','Kicukiro',NULL,NULL,NULL,1,NULL,NULL,NULL,1,4,'2026-04-16 08:36:30','2026-04-16 08:43:29','damien@test.rw','damien@test.rw','COMPLEX',_binary '\0',NULL,NULL,'MIXED',NULL,NULL,NULL,'Kigali City',NULL,NULL,NULL),(4,'Makuza Peace Plaza','KG 574St',NULL,'Gasabo','Kacyiru',NULL,NULL,NULL,1,NULL,NULL,NULL,1,4,'2026-04-21 10:09:31','2026-04-21 10:21:43','damien@test.rw','damien@test.rw','COMPLEX',_binary '\0',NULL,NULL,'MIXED',NULL,NULL,NULL,'Kigali City',NULL,NULL,20.00);
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_linkages`
--

DROP TABLE IF EXISTS `property_linkages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `property_linkages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `reviewed_notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','ACCEPTED','REJECTED','CONTRACT_SIGNED','EXPIRED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_lead_email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_lead_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_lead_phone` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `agent_id` bigint NOT NULL,
  `property_id` bigint NOT NULL,
  `unit_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_linkage_agent` (`agent_id`),
  KEY `idx_linkage_property` (`property_id`),
  KEY `idx_linkage_unit` (`unit_id`),
  KEY `idx_linkage_status` (`status`),
  CONSTRAINT `FK6y3lsrxebhbafo6dryoxd7ol6` FOREIGN KEY (`agent_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKlb7c9affq7dlys6rvob0eroe8` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`),
  CONSTRAINT `FKt2ekbw7uiow67px38780fqrdq` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_linkages`
--

LOCK TABLES `property_linkages` WRITE;
/*!40000 ALTER TABLE `property_linkages` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_linkages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_reports`
--

DROP TABLE IF EXISTS `tenant_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` enum('PENDING','VERIFIED','DISMISSED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `reported_by_id` bigint NOT NULL,
  `reviewed_by_id` bigint DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_reports_tenant` (`tenant_id`),
  KEY `idx_tenant_reports_status` (`status`),
  KEY `FK1gwkduf372w6e4o9h5elch4c7` (`reported_by_id`),
  KEY `FKfps79rbvr1rn3ci7qjl73m24q` (`reviewed_by_id`),
  CONSTRAINT `FK1gwkduf372w6e4o9h5elch4c7` FOREIGN KEY (`reported_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfps79rbvr1rn3ci7qjl73m24q` FOREIGN KEY (`reviewed_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjrgdnsnispdi3if6rq2oa08uu` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_reports`
--

LOCK TABLES `tenant_reports` WRITE;
/*!40000 ALTER TABLE `tenant_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `tenant_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `units`
--

DROP TABLE IF EXISTS `units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `units` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `unit_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `floor_number` int DEFAULT NULL,
  `unit_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rent_amount` decimal(12,2) NOT NULL,
  `deposit_amount` decimal(12,2) DEFAULT NULL,
  `area_sqm` decimal(8,2) DEFAULT NULL,
  `num_bedrooms` int DEFAULT NULL,
  `num_bathrooms` int DEFAULT NULL,
  `status` enum('AVAILABLE','OCCUPIED','MAINTENANCE','RESERVED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AVAILABLE',
  `amenities` text COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) DEFAULT '1',
  `property_id` bigint NOT NULL,
  `current_tenant_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_purpose` enum('RESIDENTIAL','SHOP','OFFICE','RESTAURANT') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wing_id` bigint DEFAULT NULL,
  `price_per_sqm` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_unit_property_number` (`property_id`,`unit_number`),
  KEY `idx_units_property` (`property_id`),
  KEY `idx_units_status` (`status`),
  KEY `idx_units_tenant` (`current_tenant_id`),
  KEY `FKfqclkjufo6lln2nsq6p7jlx5t` (`wing_id`),
  CONSTRAINT `fk_units_property` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`),
  CONSTRAINT `fk_units_tenant` FOREIGN KEY (`current_tenant_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfqclkjufo6lln2nsq6p7jlx5t` FOREIGN KEY (`wing_id`) REFERENCES `wings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `units`
--

LOCK TABLES `units` WRITE;
/*!40000 ALTER TABLE `units` DISABLE KEYS */;
INSERT INTO `units` VALUES (1,'A-101',1,'2BR',10.00,10.00,NULL,2,1,'OCCUPIED','',1,1,3,'2026-04-06 20:27:38','2026-04-07 00:56:10','landlord@test.rw','tenant@test.rw',NULL,NULL,NULL),(2,'C-100',1,'2BR',10.00,10.00,NULL,2,2,'OCCUPIED',NULL,1,2,5,'2026-04-08 10:09:06','2026-04-10 16:45:09','damien@test.rw','damien@test.rw','RESIDENTIAL',NULL,NULL),(3,'C-101',2,'2BR',10.00,10.00,NULL,2,1,'AVAILABLE',NULL,1,2,NULL,'2026-04-13 10:33:05','2026-04-13 10:33:05','damien@test.rw','damien@test.rw','RESIDENTIAL',NULL,NULL),(4,'A-100',0,NULL,10.00,10.00,10.00,NULL,NULL,'AVAILABLE',NULL,1,2,NULL,'2026-04-16 00:37:58','2026-04-16 00:37:58','damien@test.rw','damien@test.rw','SHOP',NULL,1.00),(5,'A-100',0,NULL,750000.00,1000.00,150.00,NULL,NULL,'OCCUPIED',NULL,1,3,5,'2026-04-16 08:43:29','2026-04-16 09:05:23','damien@test.rw','pierre@test.rw','SHOP',NULL,5000.00),(6,'A-101',0,NULL,10.00,10.00,10.00,NULL,NULL,'AVAILABLE',NULL,1,2,NULL,'2026-04-20 12:21:20','2026-04-20 12:21:20','damien@test.rw','damien@test.rw','SHOP',NULL,1.00),(7,'A-102',0,NULL,10.00,15.00,10.00,NULL,NULL,'AVAILABLE',NULL,1,2,NULL,'2026-04-20 12:22:28','2026-04-20 12:22:28','damien@test.rw','damien@test.rw','SHOP',NULL,1.00),(8,'A-100',0,NULL,10.00,10.00,10.00,NULL,NULL,'AVAILABLE',NULL,1,4,NULL,'2026-04-21 10:21:43','2026-04-21 10:21:43','damien@test.rw','damien@test.rw','SHOP',NULL,1.00);
/*!40000 ALTER TABLE `units` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `national_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ADMIN','LANDLORD','TENANT','AGENT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `is_verified` tinyint(1) DEFAULT '0',
  `profile_image` longtext COLLATE utf8mb4_unicode_ci,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refresh_token` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refresh_token_expiry` datetime DEFAULT NULL,
  `password_reset_token` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_reset_expiry` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `failed_login_attempts` int DEFAULT '0',
  `locked_until` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` enum('ENGLISH','KINYARWANDA','FRENCH') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_users_email` (`email`),
  KEY `idx_users_phone` (`phone`),
  KEY `idx_users_role` (`role`),
  KEY `idx_users_nid` (`national_id`),
  KEY `idx_users_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'System','Administrator','admin@rentmis.rw','$2a$10$hM3vHpiivBvZiCAHKKgRkuhk56X40EaCzi1MtDbJ/u2/lyByrzWrm','+250788274945',NULL,'ADMIN',1,1,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-21 11:15:02',0,NULL,'2026-04-06 22:23:58','2026-04-21 11:16:01',NULL,'admin@rentmis.rw',NULL),(2,'Jean','Mugisha','landlord@test.rw','$2a$10$hM3vHpiivBvZiCAHKKgRkuhk56X40EaCzi1MtDbJ/u2/lyByrzWrm','+250788123456','1199880123456789','LANDLORD',1,0,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-07 03:28:14',0,NULL,'2026-04-06 20:27:23','2026-04-07 03:59:35','anonymousUser','landlord@test.rw',NULL),(3,'Marie','Uwase','tenant@test.rw','$2a$10$hM3vHpiivBvZiCAHKKgRkuhk56X40EaCzi1MtDbJ/u2/lyByrzWrm','+250788654321','1199780987654321','TENANT',1,0,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-07 04:12:09',0,NULL,'2026-04-06 20:27:24','2026-04-11 09:26:53','anonymousUser','admin@rentmis.rw',NULL),(4,'Damien','NYANGEZI','damien@test.rw','$2a$12$iJRxLPdTS2LT40svJya1K.iJzUotNrx58CNFbxvkMGrP3O62OtB22','+250788223344','1198680010908048','LANDLORD',1,0,NULL,NULL,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYW1pZW5AdGVzdC5ydyIsImlhdCI6MTc3Njc3Nzg1OSwiZXhwIjoxNzc3MzgyNjU5fQ.ciFH5Z8RfWGYpP_LjClfQHi76Euz03lICRYpjNygW1F1sA0qdyQ5KCipILPnHnWxgVR81I4VwO-UhDrQP5kA9Q','2026-04-28 13:24:19',NULL,NULL,'2026-04-21 13:24:19',0,NULL,'2026-04-08 09:51:41','2026-04-21 13:24:19','anonymousUser','anonymousUser',NULL),(5,'Jean Pierre','MUTUYIMANA','pierre@test.rw','$2a$12$lc5cXAx2EOn7ikjZpEPVmuPoKFjJZei/hbSTHelpbZx51DUIo9J5O','+250788009988','1199080136402107','TENANT',1,0,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-21 11:12:08',0,NULL,'2026-04-08 10:49:57','2026-04-21 11:14:49','anonymousUser','pierre@test.rw','FRENCH'),(6,'Henriette Marie','IRADUKUNDA','henriette@test.rw','$2a$12$8lUn9ED74ZX9Az1VJIMeeOxPJU42jNVtqWmuhzGkJ9PySJ8LP2PSq','+250785143731','1199270088633089','AGENT',1,1,'/9j/4AAQSkZJRgABAAGkQtMOAAD//gAfTEVBRCBUZWNobm9sb2dpZXMgSW5jLiBWMS4wMQD/2wCEAAMCAgMCAgMDAwMEBAMEBQkGBQUFBQsICAYJDQwODg0MDQ0PERYSDxAUEA0NExoTFBYXGBgYDhIbHRoYHBYYGBcBBAQEBQUFCwYGCxcPDQ8XFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXF//EAaIAAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKCwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoLEAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+foRAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/AABEIAggBkAMBIQACEQEDEQH/2gAMAwEAAhEDEQA/AP1SooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigBskqQRs8jqiKMlmOABXB3H7QHwys/Ec+gS+P/AA2mtwY82zOqQ+bHnBG5d2R1HX1FBLlGO7Oqg8VaLdS2MUOsWEkt8he0RLlCbhQMkoAfmAHORWpQNST2YUUDCigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAq6nqtloljLe6hdw2lpEMvNNIERR7k8V8XfGn/AIKjeEvAV9dWHgzw3deKpoBt+0+ebWDd7ZQswH0Gf1oVrmNWryLTV9j551T/AIKY/GO5tRLEfDVk5BKpHYs3XoDvY9PbFeSfET9tL4ufFbw/NofiLxm8enzcuunRR2rEZ6FkUEj2JrGpUcH7upFJVKy5aiseOQ+NNSs4vs51m/mtfSa4Zg315qjbazpttczXKpH9rcYZlUbj+NVDEPlsUsqpprVluy8YPZTWrRTuklspWGTcQ0QPXae34VuL8VNefywPFWsrHF91RfOF/LNJ4h83N2M3k1GLclJ6npXg39tr4v8AgG2lGk+O7yWIRCKGG+VLlIwD1AkU8+9ew/Cb/gqF8SPDMyweNLfTPEtm8pYyBBazoDjCgxrtwD6oTz1rWlUVWfvuwVaVXCx5KSckj6Z+G3/BTP4d+J7qS38WWN34WPnrFBPKTcwS5z1ZFyv4rjnrX1P4X8ceHvGtuZ9A1uw1KJRlja3CybfqAeKc3CM+WLuXhqtSpTUqkeV9jbopHQFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAE4HtXyf8AHT/go/8ADD4VyXej+G7xPFni6CZ4HsLJ2SGBlyGMk5XZgEYwu4/TrQjOpNQXmfm/8c/2jfHPx51v+0vFer7tOjnMtjpkChLeyB4wo6k4H3mJJrw7XfFrW90RCQHbiuec0p8sXoRh6PP+8qboyjrsty+JJDhe+elRf2qkkZ2twO/rWVRSjsepZbmLf66ZH2Kx29ODVWHUWspOGy3vVQiuoOfMywdUlku0MzbfpU8t9IH2oSAT61E4JaR2JnPl0iSR6hNGQrMdtajauYIVaM5btiqjFWui5VVZO5s6X4huZVhNw4Kht2Gr0P4dfErV/hz4507xX4V1J7DX7RvkmADKyEYKFTwQRwQazk4x96O5lXUJw1eqPu74e/8ABT/ULVdPj8baFZSW4YLd3ViHWTaTywQ5GfbI/CvvLwX440L4h+HrTW/DuqW2oabcoGSWCUOASAdrY6MM8g8it6FZz0kcNOctpbm5RXSahRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABUV3dwWFrNc3M0cFtAhkllkYKkagZLEngADnNAH5nftp/8FA7bxpZXHgn4Z6jf2mno5F/rUWYvta4I8uIhs+WQxySASQO3X8/rjWINPgSCzjCRLnAzms5OUWZQhGtPXocxf+JZZHbLYC9DmuLu9RnluXZZMvu6+lZRhG93udMFLZGtHexGAgfMwX86pPqEj2xAUqgpy95mibWj3M9VPOM1bsrWWNhI0Z9iaJNJFJKOjH36SPMvqvJqWR2MIlGSB2qOiSJ5b6li11OKSLy2U7u1QrcvlsNyppJOKaBw0szVtLxSF3PlgPXpW1Z68YICIh8w71zzi7ELVm1o3imQZjlk3Z4INemeE/iV4n8ExWz+FvEuqaK0L70NndvGCe+QDg/jU8yikxOhF6tH6k/su/t8eEPj3NH4f1RG0LxbDApdLpkS3vXA+fyG3E5GCdp5weM4OPqavTpzU43RjGV9OqCirKCigAooAKKACigAooAKKACigAooAKKACigAooAKKACigAooAyvFfirSPA3hzUdf16/hsNG06EzXVzMcJEg6k/4DkngV+Q37Xf7bHiX4865eWPhvWr3RvhrAxjtbe2doJtUUhMtcjPKllJVeMA85OaznLlViJXfuo+SNb1YC1+Q/IvOBXJXetKsQZc5PH0rDmk5K5rCn7PTqc/d3Zkdl3cE8VHY6VcXDNsQmtuZRV2dEIOUrx6G9a6G8NuysCH9TTP7OmiVYyhK9hiuZ1Iscqb5rLcuweE7uZw8UeTirbaTdgCJkIYVg8RDnsbRoSe+5NYeEZLmRnmDBQPSpl8GSrFIIhuQ9M9qyqYyEXZGsMNJaSKB8Iy2attjy/c1nXHh+cHhWz/FgVtDExlqiK1Pk0ZVfSprf5hu4p9qLgHBDAduK1c4zWhnGMOxft3kt5Nzbt3bNdZpOvu0aRy8hazqRi4aEvlbsjpNP8Rxx3KFUAK9GBwU9xX3n+yz/AMFBtR8NappXhPx/c/bfDLiO3t9YlYmayOQoMp/jj9SfmHuOKvDNw0kcWKoSpNVI/M/Si3uIruCOaCRZIZFDI6HKsDyCD3FSV6Ak01dBRQMKKACigAooAKKACigAooAKKACigAooAKKACigAooAKx/F3jDRfAPh2913xBqUGnaRZRmSe4nbCooGfqTxwBkntRsB+Q37an7a2rfH7ULjQdI82z+HME/8AosI4bUijHE0mQCAeCEPA4zyMj4/8Q6uy2sUdvgLGMAegrjnPmenQ0pU38bOUutWkeIgDjvUFhpsmsSBIsgd6IrkV2buk5K6Om03wA8zhtgIXrntXaaH4SWBNoiA+orgxuJtojvw1BpWNG48JLIAGUACpIvCtsQGdBhRxxXmwxtvd1udX1Y09O0WOPkKAB0FLN4ajacMUxu71Eq/K9TWlQurdTUt/DcaxBSo/KrUWgwxxFViH5VwVcTzSsjrjhuWOpUk8NpMr/ugB34qvH4FT7+wbPTFEMb7N2dzOeC5t7GfefD6K5PEeBn0rOk+FzPJheD2FenHHxSWjOKthLq0bIydT+Gd4bgbcBVHUVnS+DrnT0O5WJzwQK7qeOhOFkjjeGlF7mWILm3uvlB2dDkV02laov2UwS/KprqlLlijGtFctmfcX7Ev7cMfwds7TwR4/1J5PBQwmnaiwaR9NPACNjJMROf8Ad/3en6jxSpNGskbBo2GVZTkEdiK76VVTjoeXH3W4dh1FalhRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFFAEN9fW2l2U93eXEVvaW8ZkmmlcIkagZLMTwAB3Nfj3+2J+0jqPxp+J2pSwXrr4N0djBo9urssdwBndOyE43tnrgEKFHrnGvLliZ1KbmuVOx8ga1rLXhYqGLZOFzwK56DRr/AFW5C7GCZ9O1calZtnoQpuMTtdE+Fb3sW5l2p7iuz0X4bw6bCCqKrDviuDE4y2iW56VDD6c1zYGkQ2fyoBuzycVchsokGTjP0rxK9dtnpUociI/s6+b04FS/YQyHKj8qJRvZj5LyJ7WzVDgL0FWltQ5Ge1KUr2NIx5WTpbhSAegqdIgDnt2rmnC00bX5Rwgy42j8KmK87GG2iKUb3M9WWoreMxgEA81KLONW4UcVnCTdzCUbgLG3lcqY+a2tL8L6bdAJNAjnHcV3Qm7JGM4WRieLPg3a3cLvaQoG6jaMYrw7xh8PNS8Ltvkgfys8HHFenhsRze6zGrBSVzF06/8AIVY5wfLHXPav0E/YG/bK8Sf8LBsPhz431dL3w5fWjDSb66kSP+z2iQsIixxuVlG0ZOQQoHBr2qE+WR8/iabbU1pY/S+iu8zCigAooAKKACigAooAKKACigAooAKKACigAooAKKAPzW/4KG/tNHxF4jg8DeF9eZdC0lnGsGzufkvJ+MRvtPzLHg8HjdnPKjHwK09x4n17yI1YqxwoFclSfvGmHpc8+a+x3egfBeV3DzwbUbuRXcW/w6sNOijjWBNq+1eNia7btY9mnSS6lifTILaPyoUVQO9Y7DYrLj5a8pu7bO+jqUZLXd1HFMSNQdoHHSsbcysdiWlix9jXBbHFTJahhx07VM7vQbldDltRGAAPmNWlszF0FVy+6JPlQySIKB2IqVIzwMcU5PlikJy5lYmKrABgEt/Kn5Vj8y/NWEoX1DmvoKXMaDjBpyXOAARmhQvK5lOPUvIFcAqfm9Kv2l68ACq2GrZR5XYza0Ol0u7BwZHz7GtLUvDdj4tsXtbsDYwwCBW97HHUjynzn8RfgheaE8skDeZASSm0dBXnelzLYyG0uWI2ng/3TXr0KloHPiYKMLo/UP8A4J4ftcah8RvP+GPjW9a58T2FubnSrxzua6tFwpSRicmReue6n1U5+4q9vD1OemmzxIsKK2GFFABRQAUUAFFABRQAUUAFFABRQAUUAFFABXzP+3j+0jdfAH4W21roE4i8X+IpvsthL3tIhjzZ+QQdoKqBxy4PO0ilJ2QaH5EWto3iC/jsLFFkmZtrlf4m719K/Cz4JWngbThdagglvphl8j7g9K8XEV3BWPRwtOMTp9TWFE2QxgKBxx0rk9QlULyQK8mc23dnfS1djlNQvAJCBjHtWRJ8y+1c0lrY9GnGxG4LYUVKlkUTdUQbjKxu4pD1tHlUbQcVbitJM4C4UVbUG7tkpcqsPFo45AO4Gr/2Py1XB+bHNCajsTIqz2S8E5zmpBbgYJzwKl3kioOy0I5FKkGoJCByWP4Vko2DlYwuxxk9PWmhzkkcVcG4jd7F6wmATHORV2OZY3Gepok7SOeo2bNrOAFHatvTtUaHaFOVBoUm9Dnldqxs30SazZmFwNpFfOPxc+GH9k3TX1oh2M3zAdFrsw9Rxkc/Kn7rMj4Z+Mb74Y+N9H8W6JM0GuaXMskbgna4HVGx1VhkEdwTX7ffBb4mW3xh+F3h3xhbQ+QNUtQ8sGc+TKCVkQHuAwYA9xg19NhKnMrHi14clRpbHa0V2GQUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAZfirxNp3grwzquv6tP5Gl6XayXV1JjOyNFLMQO5wOnevwp+PPxd8QftF/FjVfFmqO5aaQ22lwAYFrZhiYohjrjcST3ZmPesqsuVAo80lE91/Z5+Cq+BLCXVNYjjk1C5w0XGfJX/E16drd+gVlBr57EyUp6HrxgopRRwOrXnlh8HiuJ1O+yxz0rhqaWO2hBdDCc+axXHFJHHufbjisXud8W7WLK2Jyp296tJakowA56VmnrqU5q1iWKBodq7avLafLgcGqcIOVi2k2OjtSvBWnLZSPIcjir5FHVEOyVglsy4AVfu1GbNpHx028U4uwRSSZC2nksQTVeXTfLBOOnSpcGaqbtqUzAUBZh+FQmGQklV4odoqxNSXYRA8T+laEAOAamagYSatoXorgg/L0rVsbj5eny07KK0MeVnT6beEoDiqPiezj1e0mhmA2spGKunKOxytcsj5h8Q2H9haw8QPyI/I9q+1v+CdX7R8Pg3xo3w417UPL0bxGwfRPMA2xXgGGTPbzF2gZONyKByxr38FNQSODGQhFabn6YUV7J5oUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAfAn/BUr44W+m6Jofwusbu4jvr101PUvJkwjQKXWOJwDzucb8Ef8s0NfJfwC+GieJtQXWryErYwnMAI4YjrXFjZKNLTc6MJC9Tmex9L310bS3KDgKMLxXEatqQiJxyT+lfPvmvoejFOUtDh9SvDISAfxrn75eM9656t7HdSXKylCfnyox706OFhNk8isYqV9TpTfMaUEbSMFVa1IbJ/lUjFJ03sinG2pdGnKrbsZFTpZhcYQ570RhJSuyuZ9C+lkvlgMuPShrJVBK9RWiUuokpdSBdNOdz8UPaKz7UXgDmlq9i2uxG2mhnyF5xUFxp+FIK81o27ESbM2TScYwMAdaqXFgCpVeKTgnrIXMmig1h5K9eRTUDxDaRWMuXmFo3oWYXVF461ftZ8Ac1SXMtBSTTN3TrsIQN1W7wsy/Kee1FP2dzlqR5XeR5D8TPBDrm7Rd0shy2O1cZ4G16Dwn4z8O6xqF1Jb22j6lb3DPGpLKElVuMd+K9ahUi2o9Dz8UoSifvtbzpc28U0ZzHIoZT7EZFSV9GtjyQopgFFABRQAUUAFFABRQAUUAFFABRQAVm+JvEVh4P8ADeq69qs3kaXpdpLd3cu0t5cUal3bA5OFBOBQB+HnibxPf/tD/G/XfETwyLJ4j1NpIIp33NaQhiEjz/soFHbpX1poejW/hjSLaxt1CpAgXpjtXj4+eiSPQoJRjdGZrt865O7AA7VwWrX/AJrbR0rzJNcvunbTaTMC9BjXk1g3MjeZtB4rmcZSZ3QlBktvb7lAA71pQWBLjC/nSkrbGtuxr2umKg9CprUgsTJgKKalFK5al0LtvpciKARy36VP9laLACjI61lNtbFWUUTBd8WCvPbFHkB0AIwaFd7FNWJGt1EYUrUK2MbEcHitIxfQSa3Hy2qqPlH1qrNbCXAAxVJIxbdjPubMR7sfpWVLbZYkduoqnF2JW+hQngRs5JAFU44UZSecdBmspU1ItK/wkckfkkACnwtt+lY6wdjS2l2aljMAwzn5a37WZXXc3SqUdNDlqQbepm+LUK6c7BA24YGe1fOutWqwXE0VxErbm4Brswj1szirwj7N2R+pn/BN342a58U/hPq2i+Iri4vdR8L3SW6X07AtNDIpZFJ7lMEZPbb6V9c19TRd6aZ4NmtGFFagFFABRQAUUAFFABRQAUUAFFABRQAV8g/8FM/iVZ6D8CX8BK5/tjxk4ihAyPLggeOSVsj32Lg9dzelJ7CbsfFv7Ovg+C1vZdVmVPOjG1Bj7le8alN5nA/MV8/jG5TfketTtyKJw/iO9w5RTjAriZ3JkJNcU3ZWOqkuhk6lPuOAaoxpubBxj0rNM6oqxrWkKqMgVsWVr8u41S2NYmpFbbQrd60bWPIG3hqmNrXNPI1LdCvDHmnSWxlk4G0Vm2rjvqAtTGRjGBT/ALMTyBg0noxuV2I9v5adOaquMLjkYrSLsxqOhAZyEOevQZqjNMyr6Gqck9jJqzsZ8t0S2M1nXEhZtqggVSi+V6iS1KUyM3A6VD5bDBwoA6CsUzRwb2ZBLuByeKhHyMA2RWXLd3KT5Y2NCyc59q2oHJUKKcXdXOaSZNe6dNqWnyxKMNt+XPrXh3xB8OyaW2y5bFwDk4rsw7UZI5qkeWLufQX/AATC+JmqeE/j5deCljaXRvFVlJLIWb/VTWyFkccd1LqRkdQewr9Xq+mw7/do+fqaTYUVuQFFABRQAUUAFFABRQAUUAFFABRQAV+TP7fvj64+IH7S+qaWQq6d4Tt4rO0ZCf3jMoklJz3Dtt47IKzqu0SoO0kQ/BoKdLjK8YyW969Av7sRqyg189VfvnrWtY4XW7gPL1ya5q4mWKNgRg9q5Zas66cOpgXU7Mfu/r0ptsVd++RSa5DrW1ze09TJgAciuhs02/L0HpUudlcq1rI1olTy8EZParkA8rpUKXN5FQjqaNtD5iFjTjLlcBcVF7I0lTsxVRkxUnlkr6U4r3bhy3YTQHABqhcKUJA+lJVPesWkUpYflGBzWbdW0sjY6AVo1yGT0ZRltWRTmqDxGNSW/A1KleRdiDYAmV5qFgTwRSfcnk63Kl3F/EOgqFYmIBxx61kqluhUVdFi2fEuAOlbdk3I4p046GMkdFZoXB2tg44rzH4weH7u8t0jtwpnY5LH0rpTtocc/iOE+GXxQuv2ffif4X8aWqrNc6TeKksLdJIZPkmHUc+Wz498V+8AIIBB47V9JgJXpHh4rSqworuOYKKACigAooAKKACigAooAKKACigDN8SeJNL8HaBqGt63fQWOk6fC091czttSFFGSSa/FCDxTf/FLxhq2ua4qtqmsX0s8xRcL8zlhgdgAQPwrnxLtA3oR5pHufgDRE0HRZDt+dzwPTFLqt5uLKTg14Ele7O+HxHIalc7iw3cetc1eTSSNjd0rkep6NKOlin5YUn5jnvUlqEzhetXKdzojc6fT7YvsAGMV0kFkyqoXG6ufns3E2toa1taBFVSvzetXYdOKnPOKTehUGy9FbM3CqQBUxsgqgL1rOKSLlsEcBVWBWpIULjaRwtW1Z2CMUhZUDZUg8VRmsWfjB/Ks2lFl9dCsbB4zhs4qncWeQcA0KTIlAzZrCQA5HFZdxZGQ7WGFpLTUlxSSKTwrChCjpVQx/Kc1pKdo3JaKzxhgQarrHuO3JrBFK6ViREMRGK0LOY+YAB0rRRXNuZVInUaQpY85rC+IejXM9u0sRPyoTwK6qW9jgrPW58u+K7SKaO5eXeFhDSEAc8dq/cD9lL4pH40fs6+A/GDWbWk1/pwSaFjnbJEzQuR7Fo2I74Ir6XA/BY8bF/Ger0V3HIFFABRQAUUAFFABRQAUUAFFABRQB4h+21qtpo/7K/xCnvYfNt3sUgKYByZJUjHB92B/Cvy9+F2miWSzvQNvmY2KewrjxfwWN8PbnsfQlxH/AGdpozgSY6Vw2o3G8sW456140nyrQ9KkrM5i/cAtjoKypGCAnFcPPqehTXUovJvkI6AVoaTDuOdoxnpRJ3Oqnc7HSYuhPGK6exReCq5Ncz0k0aqDtqdHaWqCNSVBbrV6KFGcLjiqaS2NIrleheFuuAqjio5bBUX5SaIwSQ7IbHZ5BFSR2DW/OMiqexnbUtxaWJFDcA+lOXTFj7fjWM10NF7uxUu9LUqRtx9Kzm0rCkDAxTjorDbbRjX1iIslTk1zV7CzTFdp4rFt3sZqLuZD2jFj8tRSWhAOQRVXSWgJcruVJLUBcDOarGFoD0GTQn0ByfNYPLwPQ1YsF2P1FVKKb0M5HY6GgbYuRUvjuGRfDFyYsCRFOD7+9dVGLjHU82urOx8ieKJpJDdRSRoJnUrgDjNfqt/wTI8QS6n+yxpelzqFl0XULq1GP7rSGUf+jD+VfQ4GyZ5GLTVj6vor1DiCigAooAKKACigAooAKKACigAooA+Sf+CmfiGHTf2frXSWvkgn1bV4I1gLYa4VAzkAdwCEJ/Cvjr4LeGTLpdpcSo2YMYHauDGStGx00FDfqei6/cGVxngL2rh9XYFyR07V48rWPSpnM32R8xUiqDwmTBB4HWuCooRldHpUouSCLTzLJjacV0Oi6W1udxHHpXPKq4ndCCijsNO07zGT5CBXTabpYDDPAFRFyesi22joodLCJwtWU04qRgYwKtya3IjcneJbePOPm71J5KyQKFrSE/dHIsQ2iBFBAwKjuSsYIxwOgqk/d1FFEmnkSAA1Zu4gm3H0rO6W5olYquvyZI6VkXSBiQvFZufYexlSWg38j2qrLpCZ2hctS0vcUrJ6FC48M7WBBzn07VRu/D6r8qnoPzrKXurQFFGJeaG1su5QT7VjXVqY26USktOUORN6FNYt2c9qms1CtnGB2zWtOfUxmrbnU6G5EiKBxmum8T2KTeGbkmMsdowAa7qbckkzysRvY+PfGNjHba3L5rhiTkIvBFfeH/BILUbi8+HvxNglnZ4rfxEBFGxz5YMQ/wAP0r28Cnzq55WMvZH3/RXsHAFFABRQAUUAFFABRQAUUAFFABRQB+e3/BWqzeVPhNMZD5S3d4ix/wC2fIIP6V558E0ll8KKzoEJYqfwrzsbaK1OvDwVuY0NbO+Z1HQGuNv4dzEV49TRWR6VGxjXEW35ahtrXfx715tSaS5Xuevh42N/SdOjeVQwAAroWtIbeLqoVeTXJNSbR1tpFmw12zd1RJk3oOma14PEMClVMgA9RVqTW5N09zat/EAlCrGQR2rQgvjIwZm6dqUqiehpGJNLMWwSOBTFuRuUYwK0i+hLi7mjHcL5QJPNZuoXqbchunQVo+ZKzGkkSaXfLGu7PFW59UjLDc2PSspPTUSuZOp+JrawQl3Gwdcda5C4+IdnHMwVvk9axgpthOcYrUx5/ifAg3xxlgOn0qjdfG2OFF8uyPB+Yk84rqhQk9DllOT1WxC/xl81Q32MhewB5NRt8VIbyTbGArnohNaTwktkgVWS66Elr46+1T/Zri0I44degpL+PeAw+71zXFWhyS0OilNSehl7Ajcfypyj5/mH5UQ0QVY8u5v6QGSRcDge9dxeD7T4bnVRlthr0sPe1zx8RZS1Pj3xRFFDrc6tlpWc9e3NfcH/AASHljbwf8VEitBAE16Pc4bPmNsbJ9u3517mBcuddjy8Y1bQ/QGivYPOCigAooAKKACigAooAKKACigAooA/Pj/grJBdXcvwlS3R/LhuL2R2A4B/0fH8jXF/B22lbwmZZkCqT8nvXlY++h6GGt7Nl3VLZY5nJAye1chqEIQs5H4CvLr2irnZQ3OY1adLaMydh0rkb7xtDYEiNS8o6KK82VNzloe5BqELso2fxI1Vr0RiDZz6dKl1nW9XvZlEt06xNzhTit/YKLTktDCda7sV1u54JAyzscjB+atK08SzwRiNJ2Zc+tVOFJLYuEzudC8XyRRIgPHvXoWga19ph3M5yK82pyx6HVTnZ2Ohtr8yfh61MbhMgk/lSjUSNXEnkumMAVSAKx7i5Vckkn60NzfUmLV7DbbUCkmD90VjeIPEjQuUXKgc1lKb2GrI838S+LpH3hW9s1wtz4hmX+9hj3ropyjFah7LmQkerMJ42lZlgU4IHXFXpVuNcmH2S32wjjLDGa63UtG8ehzypO3KjotP8DX8kAkLKrH7ozWiPhdZ21s15dXZWQH5Qp5JqIYxN21MvZSj8RjySajozlI4jJCT1Irc0vxdb39uIbgeVcDjae9RW5ZxvEcHyTLBZWYkcVKsQO3HFcUOaOjOmpeSNfT49uOeldtp8Zn0aZI2+YKa9OhUSR4+KjufHnxCuktdfnWLDybzu9jnpX21/wAEepjL4I+KpZ13/wDCRIdoPIBj/wA/lX0GAa5lY8fFrlikfoRRXrnAFFABRQAUUAFFABRQAUUAFFABRQB8L/8ABUdtmlfDQBHJa+uhuU8AYi4/z6VwvwbxP8PYG2/LGWUH6V5+MaW524ZNxdh2pQ72bjnNcdrKbCw24UeteJXmuWx6OGjqeS+PdZ34s7Q5mbg47VB4W8JQG33XCF7jruPavNhU5bnrODtudJL4YjVVMcY831xWVqXhG5CtIzgnHyqKzni+Xe5UMPdXOafR7mGTBHy96vQaEUG4VE8Voaqlyl+G3nhxtOMV2mi6y8cUMfIKnmsa1VctzZQ6HZabqzzPjdgV0FrcKABn8a5OduWhsTSzHoDxiqU/Xr0rsc/dsiWkc/qWoNZ9GIArjtd1l7iUYPGMGoir6lcuhyl/G9wpVQOveoYNJTgyAHH6VLqqzDZFuz8PJeXAYr8g7V2+l6alrGqqgAHTirhKSp8tyXE3Fm8lAucBaZc3ccsa5G4r2PQVChK25F09CNfIeLL7fYVy+paHFeXTSoojPZgKacou1yORNmJLLd2N4Ull/dKflPtXVaTeRzRo2c+lXNaqxfLym/bALjaRiu18LMnkSq3UqePwr0cNaSseTi1ZnxL8QXEvibUEjBjdZm3Z9c19/wD/AASN0jTLH4c/EG6s3Z7251lBdnfkZVCQAO33jX0OX/EeLjmrJH3vRXsHmhRQAUUAFFABRQAUUAFFABRQAUUAfE//AAU7WJ/C3w9SQHC6rK/HoEXP8683+Cc0E/w9XylKxq7BRXl4+N2dmGlbQv6nEI42fFeU+O9V+xJ5SN88gPTtXlVbONj1ML8Z5xYWI85pdoZs5Oe1dBBdi2fbkdOwryJTs7H0MFzI0rK8M3saviAy8t17Vm9yr8pRu9HjmJO3HvWJLphtZHRGJrNLll6kOrrsU3kFqfmJBq9YXatIpLCs665UbRfMddpOoxIq47dK6Wy1Ay4HTFc/LZ3LNeKRpY8DqOahkiKjnqa2VT3dhRlY5rVMMxWQZAridUg5Zv4Qc1hJPmTKa90ylnVjtUe1aVva70VcV1UKHmTVl7p0Gl2aW69ua1Y3COFA59a6KlHlVzkUuYSfryaoTW4GWDfrSiuVXHGWpmzXn2dshunaqLa0ectWNaXkXDVkZ8m9XMn4VZ09BbyBY+FFcsJuLsdMlZHWWMn7nPpXbeDGTziXbClTXr0Hpc8fGHyD8Z7WPQPEGvSov72Nnk+vev0a/wCCV3ww07wb+zJa+K7Wa6e/8a3k2o3aTMNkRSV4UCADgFU3c5OW9MV9Plt5K585jJdD7Gor1jgCigAooAKKACigAooAKKACigAooA+TP+CkkK23wSsdVayEyWmopG82Mm3WTv7ZKgfiK8C+ANzDdfC22niYfMWIP4mvLxsbSuehhX7h0OvXP2bT5ZGHCqTXgWszNqd6Z9uBnv6V42Jtax62Ej7xl3TJCOFAx6VBDdea4XivFcnzWSPehFRidFYPgrxxW5bsSCADiulU1LUwlU94nQbUO4ce9ZcqRlySRmtlDl2MuZMyb+zjkyABgHtXPS2rWchZWIXPSuerDlR00mauj6uBIiFtvPOa9D0aZXdNp4ricWnc3nojttOtd4GFpNSsxCCcD2rVycNjNM4rWpM5Ixx0rgNTvC0jq3bsKwUbu5rHRFS1cRMGCfLVi68SW1iu5nC4r0qVNaWMMQ0yi/jq4lT/AEOD/gT9KntNa1zU2+SaOJMclTXoex904OaNty7JbeIZASt+m3HOW5/lWFqUuvWOd98HGP71cs6EubVFx9na/MYUni7ULMfv4946ZFJZ+Nba9fbkpITja3Fc9ajroddGodNZXxO3bjbW/ZSb8Eda4qkEtTpcuZHTaZKwAB+7XYeFpsXAVe4xXbhpcrPKxcFY+W/2kGOj+Nr2CRHka4uEjUIuThhxxX7XfC7wFY/Cz4ceGfCGmqBZaJp8NnGQMbtihS31JBJ9zX1mWvmg2fM420bI6eivTOAKKACigAooAKKACigAooAKKACigD5z/wCCh2kz6v8Ase/EGO2V2lgjtbjCDnbHdRM35KGP4V8f/stRfZPgnpRLby+9iCeVyx4NcGNdlY7cK9HY6vxVcFrN4exHNeR6hZeTv2kDPSvncUz3MLFaMwJLFhE7yEYWuWuvEEFlOVhIZh2FcUYWTPUU0/dRPZ+JNSugCqiGNuFLCte78Sf2PZh9R1u3t1Ay2HOfyHNdVOnGS0Oac4LqN0vxXp2pSBYfEMcisM/M5X+YFaXmkbmhu4plHZWyat05R6HM6ttirHrGJtjAjHXNTX5S6hBQAmsK1Nt7HoYafNG5zNzK1ncLjgg969J8D6tHdpGWbkDnHauSaTR2yd4HrukXqeQoB4o1iVJIcAnpRKC5TGMtTzHxFO0AZicL29q4QM1zebQOM1j7PW0ToukhdVD2duVi3bvSvO/FWsWPhkhtXuN97IheGzU/M4H6V6mFotS0R51afKtNx3hbwX8WvibBPceG/DF1aadFH967i8pWz/dLY3fhXAR6FBq+sT6P4h8XTaW8BJmnEhMYI6gDI5r1qNSn7Wyd2jzqmHl7LmtqYHiHRPB9vfC30jxnqd3KvDTSfKnHoazfDknjTXdSmsPDs99eJAM7VO75R3NdU5Rd3WVkjio0akvhRt6l4p8beDTDb+ILCaJZPuiaPaSKu6R8Q9M1R1WeIwTDjcorz6mFhKPtKLuepCpOlJRaseveFNUW9gQRy7k7V6ZpMQaAEccV4NalOMrzVj0qdVtamrZu0T4zxXaeGZNtzE3qcUqF+c5cUvduc78OtJl1T/go74B0+SBnsZIWvfM25VTDbTOP/HkX8xX6udK+zy2HLSv3Pk8Y71LFL+3dN+3fYf7QtftuceR5y+Zn/dzmrteicaa6BRQMKKACigAooAKKACigAooAKKAKetaRa6/o1/pd9Ak9je27288TjKyI6lWUj0IJFfkh+xdd3U3w31xboSBU1EiINngeWnArixqXJdnfgFeT9D0fxJcYkZRxn1rhL8IZfm6DrXzdfk+Z7WHvFaHl/jXxEdWuW0+0nFtp0f8Ax8THjgda8s13xrHHdDTPC9pJf3PT5EMjNjuAOTWlLDc8Vc19ryM5DxrF8QdN0uK+1C31CwsZmwEdGQKfxHFdx+zv8R/hX4TttWn+Knh7Vtc1CUKtp9nO9VA3Z3AyoD1HXPTt39KjRoKlaktTmq0arblY6X/hYnw4+JPiyDR9E8D6lYQ6pcR2dkkRUOXdgqjCsecn3/GvsDxX/wAEw9Hh8L2954c8W6uuuFFfZfSoYFfHT5UBAB+tedjJV6UlswpxcXH26smeEeL/AIPeMPgprlhpnjK40+5F5EZYp7OYsqqDg7tyqc1ckt7ezsJJBIphCZUipqKXs7noXjTa9lszzme6N7dFyeh4rsfB9x9kk+TO3+KvMmknZHfBtqx6vo+sMyIqghQO5rQ1fWQ9sF6GmnFx90x5ZXsjzjxBfCYlWJ4rmrG9S01Fd2Nue9FL4zaV1CxreKvFWj+HdOF9eP5cDELkIWOfoKufBL4j/s7+C7jWtc8X6Rc63rEjq1pcT2Bn+z+oQMQFOe+M+9ezS91HA4ylqtz3LwZ/wUh+FsELWV1pHiGNFbZG6WsbKV9x5mRXyB+0DpHgf4h+ONR1vwOWsrS+be9vJblNh4ycc4zyeveuOlCVKd3oaKjWjJua0Z47dfBgWsZna/Crnk+WeBX0t8F/H/hj4H+A5LXwdoEuueKrjm6u2BjEq9QD34yRgD8a7sTVdWnyMVKjyu9M8++Iek+JPjXrv9t+J7dbPauEt44ygQemDk9u5rndN+EGn2U3EG8g9xXNRxTpRUE7JGlanGUU3ueiaBokFlGsSQ+Xt6V32jv5Ue1ulc+KqxqPcmkpdTURQJRtPFdX4cJR1OMAHrXFSa57Cr/Bqamn+NfAvwJ+LOkfFjxcmqXF/Z2s1lp9rYgMXZ0K5KkgdGYZJwMjjOK8y+Lv7YvxQ/aXt7/SpoV8KeDmnXGl2jlpbmMAEeZcDBbnsoVegIJGa+pw2Mpwoct9T5upgqtSpzJaHA+HvCWnXK/ZGtJAuQxHnMCSOc5zmv1T/ZG8Tat4k+CWkLreofb9R092s2uCBudUxs3HuQpAz1OMnkmngcdiK9bln8JGLwVGhC8FqezUV7J5oUUAFFABRQAUUAFFABRQAUUAFfEXxA8Fad8PvEeuaRpVrHb2q3DzIiIFAD/NjA9M4/CuHH39lodWDny1DxHxLcCN2+b7teY+ItalG8Rk+lfLVZRU1c+koRThoeH+K/DGs+Jr1khuWjhJ5A6Go/h/4X134S+NLbXrRUuJYxhldfl2nGf5V6MMXShDl6mTg3O0j6G8U/tDeFPHPgy48P694VuTNdp5byLGjqh/vAnkfhXzK3wffU9be30u1uJIDkx5Qn5feuWlXnTba2Z3+yrKOp6n8IfBPiT4W+JrTX7Hw9Y3t/YOJLVbwjZHICCHwSDwR2IPvXq3j7xl8a/ifNDeTfEPUPD0iOGNrpd5JDEAOwEbD9Sa0dRTalU1Ry1Wk1z9Dzr4i+GfHPjTUPtOu+ML7VGC7Fe6lZzGvoMnAH0rH/tG40HRo9HebzTGNu6tZ1aLjZGkp03ZQI9Mi2jLd67LQmSNVPTb2rwa0/e909aEGlodvpl4zhe2elWL64ynJORUKXLESXvHGaw2/LAk/hXK6iXK7gelaUaiTLqQstRNO1OyvLuC31O3SeCM/dcZWvQrG38OSR4tdPtU3D5sRKB/KvWpuc1eJ5ddVIP3NECWOl6bzBplntx/DAo/pQ9/puARpcIbox2AE/pWjju5GLxGKlGzkVZ7rT7mMRnT4iAfQVLba1aaW6iz0qKM4xlEA/kK5+eT0NaLrR0TEu7641ItuiCg9hVaLSwi7QuB34rmqLtudEVJ/EWk08RgMcFqsW0W2XHpXK5NbmseVbGnG27ao7Gum0PcrR56elOlyqVzlrv3dSr8ZtBi1nQtNklOEgmBPGcgjpXjGra1FoY228Pyg4BxXXOXLsc9CMpRcYnnviTWvEOu3Ytbe9ksoHB2+TwT+I5r9U/+CZejTaR+yJ4Ze4maWW8u7ufexJJHnMgzn/cr18oq05T5Y7mGdUFSw65lqfVFFfRHywUUAFFABRQAUUAFFABRQAV8SftV/tN+NNI8ZXvh/wAH3x0nTtKPl3FwiKZp5cHOCQcKM4wOSRnPYY16nJC5rQpOpNRR4j4D/wCCgvxX+G+uRXHjJ7bxR4VTIuIFhSK8QYOGSRQBwSDhgcgYyvUe1eOPi14f+Nmn6f4z8PR3MNle2gDpcIFdXUkEHHHGMcV5zxKq0nFnZPDOlUTPB/EsPmtIVPXpXCX+ljJDc5r5zEWUj38L8FjH+wLZOWC8Cs/UJjMPucD2pcyaR0ezV7kNlcfYpRIIAT7itnTtcksrgzIxRjx8tEOaL97Y6Jc2xrxeIbq54ZiFJ5zUz+I7exX5mJYV30+Vq1jgqw5mcj4k8dzSsY7Xq3GK5O2spLiRpZz+8JzWdeUYRtY6aOHSWqNrSrE3Gccbe1dRp1l5bKpOK8GpNXserBpROp08pGFAP3fSprqVXbg8elN7IhJdDF1a0EikoBtPWuSurTMpXaQBVQV3oaqN1qZ8ulKz/uxg96SO7udMfywWMI6V10as4vlucU6DkrG/p3iWMgLu+YdjWtBercncVG3vXqtux59SjKOxZghik5VQq1ox2qRoMDNc0vdKpJrcfHHhhjFTFN/yqK4aknFnUou5ItoVUYFRpAFc1yptuxbj2JIR8+MnAro9GmHmIAOnWumn0Rx4hM3PF8MV14dUSqWVXBA9DXg/ivS0ur2FAgyzYAHelinLRplZfTanqcz4q0RvDuvWwZNpiQbk/wB7pX69fsteCR8PP2fvBOhiPy2isBM6+jSkyt+rmvYyCD9o5dkcfErfLBHqlFfWHyQUUAFFABRQAUUAFFABRQAV+W/7eevan8J/j7d6VDpH9oaZ4hsl1VJhuDRZdkdM4xkMmfo61yY1fujtwC5q6R872ms6b4wtJvs7dVw8bdVPpX1L8MtMj0n4RaTbooCqGwuOmWJr59Jt2Tse7XiouzRha7EAG45/lXG3tsx7c1wYr3Gb4ZambNZ7htK1AuhCU8LxXD7T3kj0lHlJB4XRYxnn29KE0GKEncAAB1Nd1OalpYwq3tco3nk2Mbb5APSuN1PUWuZTFAp54zW/tFFE0YXdg0/QmQlnB3Hua1YdKO35V715lWvzs9KEeVWNbTtPW0znAJqzCwSTrwKwTUXqjZtRRdgucyfu2wFqO51Hy5QWz74NVyijHQdFqaScZGPemXFql4dy4FTGm4u9yGmmZFzpssUxIHHoKG00XURDIQe1bwlbXsNNWMK+8PvaN5keQ1Nhe+04AsTt61tHFapGUqalqbuna1MyY24FbcOrNgKO9bSqK5m6Ops2QMignitq1swMFe1efVl79hqk72JZLcxnpxVRbUh29O1TGNmFrB5flvjFauj/ACOCCOveumlozir6JnR+Jp/K8JySAA7WFeY6THaza7ZXNyB5ETlmz9OP1xWteGiRGClo2c7ZeF5/ip8R00JZzHN4n1BLSwcgnygG6nHQBefwr9lNNsY9M0+2s4v9VbxLGv0UAD+VfR5PR5VKR5We1uZU4drliivbPnQooAKKACigAooAKKACigAr4u/4KVeGIv8AhG/BviZUBuIryTTc45PmrvUZ+sR/OsMQr0mb4V2rRPzd8N+HXTWLyKJ/9IhvlDhewzX3NZlY/BFmkY2kDgY6V8rD4rH2WZRtTRxOrZYsPSudmtzIxxxXFilqc+ChZXuRjTuOaabcwDOOR0rz1CzPT3VitcOyAnHP1rB1C9lI2biPSu2n7qF7PmZztzp815P87EitCz8NpFysf41z167k+Sx0U6ajqay6Uipg9actqttEQQKzhpGw+f3rGRdTmIlR2qik7N8obAqkrI1tqWY5mU4B+6KgnmySckntXL7TkZsoWsU/OkiOd1X9N1gR/Kxrek+pnUhyO50FldRX8YVcZzV6LRS7cHFaQhq2cvNYWTQNxwRu59Kp3fhpC20jBHtULe5op2GQ+H1ij2Ac9uKsxaA8ZU44pSqOUrFPVGrYWnlgg5AWty0tyqhg3Hpinycz5rmTZbaJXjOeorLnURNzkVcJcraM79CE7gauWAKTKc8VrBcsjnqxOm8Wsp8COG4YyKM15Xq1i1rp2UcqWXg1rWfvIywbs5I+i/8AgnV8KLTxHqus/Ei78me30yZtN0uCRMvb3IAM8vI4yjooIPRnyBxn74r7HLaXJQXmfLZpV9pipW6BRXeeeFFABRQAUUAFFABRQAUUAFeQftX/AAsl+LHwS1vTrKN5NZ05f7S0yJBky3EKsVjxxncCy/Vge1TNe60XSlyzTPyN8Dyix8Ypc3KKi6kBJIOyv2FfYcDMPCtvgAEDGDXyTVps+2zGq6tOMrbnFakcbuee9YRlO/GfwrzsT8ZlhIaFkDbGG7elVJ5OcY5rkWjPQhpoUpIGmJUA++KpPovmSEsDwfSolJuR0QVmW4NDjVMbNrVoxaMqRKu3mr9nzWY5NFbVbNbG33AYNcdqV4XO1CPfmqjSUZXMufoYpMlw5Ayalt7Ta2GU/wCFFWfLGxtCOt0XBpLhNyNxS/2S8ab2Xgdq8h1HOTR6EUrFSfSpcE4Of5ViywS2zncOldVGTVkRUgktCbTtVmtbtMHCA17T4PtI9dsFKyDf9a9JbWR5NZOOp0H/AAjUluMAD3qCfw2ByBk+pqvZ6AmtGmV28PheAMsKcNIeJgfL/SsFTs7G3NfQVNGc5yox6VcgsfJTG3mnGKWhc1dWIru2dFyorIukJO9hjHFJwSjoZpWRVB59hV6yVGKlu1KDcGrHLWVomp46uGi8HwD5FXz1PJ7V518TNUaTwTdNZbRNHBuRge68kfkK73TjJJXOXD+6pM/TD9l/wpb+D/2f/AdnFai2uJ9Ht7u8XAybiaNZJST3O9j+AA7V6jX21CHJTUex8ZUd5thRWpAUUAFFABRQAUUAFFABRQAUUAflt+2v8Ho/hj8aFureN49E8SXDalby44SbcPNiB9mYNjsHArsdHuGu/AVrM33mcgfrXzGIpShWk7H1kK3tMDBLocPq8nlsSc8mslmKkHHevHxCUtWdeEeiuWfNLKoAIAp6w5+8K5Wmd0FaRYt7bc2FFaUWlGcBShH4UcnU15lE1LLwzFId8meBgCtRNARYg20bR3IrohBNGEqjijyz4m6hHZ3P2ZDyvXFecpbSzsztlQfWrqQjCNyKcnJmppGkyvJtVCc129l4HMsQL/Kx7V5tVe1dkehBxpvRnb+Hvg/9siibO9vQdq6EfBq3sxm4ZcH+Eiinl6irxTLeIjFbmFrfw90y2aRYWDHp16VwXiD4bzrEXhg3jHAArT6vy7ExxLejOP1DwPLaQ7pIihPQY5q74H1i48M6sIZTiJiBg08NWnGpyz0HiacZR0PerG6ivYEdTxjvVhoUYY2jFevKMdzxqbaKq2Ckll4+tH9nPKxUYxXLKN9GdkJXYh0p4eTjFQyWYjOSRWHs4rRHRdWKd3bgRFiQBXMakAAQp6Gos1ojG73MWSXymAHSr+nMzOp7elaU4q92Y1mmtTN+P95NZeFNCggVmae5AIX0Arj9Ps5tZ8ZfDTw7KD9n1rxDZ6bdKBk+VNIqsfwGa68PDnqpHG5KFCUux+y8EEdrDHDEgSKNQqKowFA4AFPr7lK2h8UFFABRQAUUAFFABRQAUUAFFABRQB8y/wDBQPwTba58DW8TuubnwndpeqAmS8bERuvsPmVv+AV81/De+GsfCbS7sAqLgtIFPYEmvEzKM1UT6HuZbUj7CUHuYGtxbpCF6CsOX5ZFUdq+frRS3PZwpZhZcg5rTtV3gGuZprVHpJWNexs1SQd/SujtbdIowWI6URT6kSt0NC3KADcvHak1S/S3tHCnnHFdUadrWMKlrHz1qW/WPEM8kwyI3K4z1rWsdHjmuF3KAgrnrSbXLI1w/Lp2OknbSfD9p5zbfMUZwDXOz/Eoaiyra/ulU8Z6ms4QhH4TfkqSdraHqPw8+KD6ZCz3KeZxjIPSn+J/iy97HJ5a7VXnr1rZYhRi4p6gsPKUttDy+58dmO7cmY7mPTNdboPxMit7XbdyxRn1dhxXPCrKxrVoRjH3VqF/4m0zWjlZoZO3ysDXn/iSCCK9VocYB4NZT1kmgU21Zno3gm8Kaeis+W75rtobpGUKMbq9eDvDzPK1UrIcybVyOlPiiO3cpxis5NWs9zpghfLyOTms+8UqRiuZpp6G2xiX8vGK5vUDgHtg1kpPm1DoY7BW5IrR0qLfOgJwueeKuLbl5HJV2NDx0lpNfaNb3BXhchW+taH7FngqXxb+2Dql3cWUt14a8OaS17bTNCWhgvS6xIpbGA+0zMvf5CR0Nenl0VLEKJ5OLq8uHkj9NKK+yPmAooAKKACigAooAKKACigAooAKKAMzxP4b0/xh4c1PQtVt1n0zUbZ7a4iYcMjgg/zr8vv2e49btPCviTRPEEbWupaVevB9jkwHg2jaykezBhXnZjH92menlj99ryNDWIirsAOK5y4GOg4zXzWIs1dHu4aetkOtduQCBgdK17Z/LYEV51qieux6ybtodBYMJir9BW5G4Xb7V0wXczmnEJrzy1yp6dqw9avJJbWVlOMDmumBhLbU8aNybXVZyz9XJwa5z4i/FabwbpbSWkHnTN8qjsD71yunz1FB9Tpoq8bx6Hzzqf7RviSWRhc+VsY/dCEY/WtnwN8ck1XUIbK7gaOSQ4WQHj8a9mrk6jScqT1R58c5hGv7OV0fQGj+Jbi0tQizfL14Oc0+78VtHDPNIwVEXJJNfJVo1Izstz6ai4yhzI8B8e/ETXNUmkh0SF1QHBlUfN+HpXLeHvhl8WPHt1tsbbV54ictIXbYg9TX1uAo4OhSvVScvvPCzKGY1qlsO7RPdfhh8KNb+G5mutd1jznK4MPzfIfxNd3pjTeJNajhiH7pT8zegryMb7OdfmpKyOjCRqQpfvXdnt+nWtrbWcax43KADV5H2uGXoOmK3typI5FdzujUhm3IDmnq2XwMgVjLXY6YLUkMqISvcVnXdwA2B+dYO6VjVuzMO8IBbHQVy96cOe3PFYt9xqxnK48wg8VveHoPNukGB1rppqL2OLE+7BmL8VZ1ttYguOc2cO/A9ucV9lfsDeDbfRPg7c+I0YNP4m1CS7J24KIhMaoT3wVc/wDA69jKqaWJTPBx91hr92fS9FfUHghRQAUUAFFABRQAUUAFFABRQAUUAFfDnx2+Bmo+FP2mdX+Iv9oNPpHiqyjt0tVi2i0kjSJXBOTnPlK2cD77Dtk82LS9i2+h0YabhU06nmHiS1NvO4A4zXJ3aKCVx09a+UxDUo+6fSYZqJVhQbjlse1admdzdenrXDtoz1qctDo9MlVUVcce1ayyhQDmtabVhO5TvrhUiLKeRWBcaksgKMevWtVUjEmyeh5z4x0J5na4tB8y9q4iweey1FZry080KehXNRNKXvo2pxklZHotz4J8E/EnTVXWNHiilUZ3IoQj8RXjfi/4ReF9Hu3j0S3KFT8pLZNbfWqzj7snYx9jQjL343Zb0TTr+0s4olRm2jGcZ4rZm8P3erWjxSoY0P3sjGRXnVad6ikd1KryU2jqfBWpaD4MEcL6bG7A8lowf516bP8AFeCTTjFpVvHbSEYyBgflXQ5yjszBym3dPQ841jR7vX7tbi6uTgnJUdK1tJ0w6YgW1+Unq1YKrFOzN2vdsdfo93NBHskJbPeugtbvnaCMVsqjnLQ5qkFHY0rW5VT1q2k7SqcfLinNOKJg9SnPeLnaH+tV57pVjzXLz3VjdNGRe3irCWB/CuYu7je5JP0qEmtWaNJIriAu6kE4zXX+GLUtcIR1X0ruouLjZI8vFPSxx/xFuY9W8WvoFtDJc6xqWLe1tYF3SuWGBgV+mPwR+HR+E3wo8M+EnuFuJtLs1jmmUECSQ5ZyM9txNe7lNNuo5dj57Mai9lGn53O4or6A8cKKACigAooAKKACigAooAKKACigAryD9qq6/sv4Q3mqEDyrG5hkkOPuqzbOPxcVlWV6bRdN2mj5B8V2+9IZVBwyA5+orgLxCJGOa+WrUrH0mHlYyxxITkitKwcGQKGrzJuzPZpyvHQ3La7ERCg1qJdqyZzwKqGrDrcxNX1Py1Kr0rl7i8IYndgdq05kuhcbJ3IpL9Xhxnn0NZUqxyAsEBNYybuXyv4ijqF9JFBsi+Ud8ViwaeZ5QzHn3qeRxe47qKuz0HwppVrDt3puFbOs6dbTxARAKa7KdL3eZnmyqPmOC1bw7Gj5P3qp2djIhwGxzXNXlpodtCquXY3rPzolCscitS1u9nJ6CuanG50+1TNS1vi+MDArZtJ8YI+9W91Ezmro0oJzgAmrS3hQbQ2FxVc+hnBdChcXagblOeaqzXbmMr0FYtaXNUYl3etkAMAKzixEnqPWsoy1LekbFuBsEAdu1dn4bt7icLDZlft1wRFBnpvbhSfxIruoQ5pWR5GJfLFvsfVP7OP7Hug/A/XNR8W6jdHWfGupEvJdSL+6sd+S6QA8gHcQWPJAA4BIP0L0r7TCUFRpKJ8jXqurNyCiukxCigAooAKKACigAooAKKACigAooAKp6xo1h4g0y407U7OC8sLhds0E8YdHHoQeKGugHxd8cvAkXgTxNLplvv8A7OZBNa7uSFOeM+xBFeF39mfMZhx618vi6Xs6j1PfwU7wRg3C7Hx0psMjRHKHBryqi5We9RlZGnFd4YEn5qtjUSq7Q2OPWsFc6HHQzr2UFck1g3UhD8UKV2Qo3ZUlZmUKvynFRANEhzRbWxafKjKv32kLkUzT5UFwAzfKO1bwpamUpcp6P4clRocKyjj1q1qF1DbRE5HHfNdS3uck3bU4zVtYh80tuGPrVOHVIZDgEA/WpnBS0NaaujUtL1QMDmtEyRbBsHFYulyouOhJbXSocE4ArZs71EAHb1zWLiaJ30NRLtTFhTg1Wl1HahVm2gVile5T0RSbVFf5FP41De6o0CBVOfU1XPpsVDV2MWW+3yHNRpM8kgC9KXKpSRc9EbGn7N4BJJr2T4D6M2tfFzwnaKMwLI9xLxxiNdw/UCvRoa1EjxcdL92z9AF6CivtI7I+RCimAUUAFFABRQAUUAFFABRQAUUAFFABRQB4H+1j4eNzoek6vGvzW8jQyH/ZYZH6g18d6tCUL+1eFmSXtD2cvl+7scjeLsfAquJvJ6jIrwa1NKVz6Gg9BJJdykg4IpIbsrxWcXd2OmbsrEjEt97pVWURq52kZ9KiStKyM4sozFEwW61l6jflBhOldKgkkZuTcrGVFpt9rL4ijbHqa0rbwNfs20Tqr+maI1JSukjVOOzdizJ4e17Twot0eXHHyHNH9jeLNQKwi0kVO7NxitISfLdjlCnp7xq2nwuHl79S1NQ/UrGc1Nc+A9MjiAt7l1kHcnrV07t3aIm1F2jqQL4XuIUYx3CtgcDNQtDd2cfz8gVLV9zOStsVjqBU5OKu2us7SBkisaq00EnZGzZ6yWjO4jPYVUu75m7/AIVzKPK7m6StcrJfFTx2pz3pcFTQ4pvXQ2ppEDzZJQfnVq0ZUUdS1JU1fcis10N3SELOOO9fVX7GOgzXHirxFqssQ+z2ltHDA2P4nOWx+C/rXrZfHmrxieHmUuWmz636UV9cfMBRQAUUAFFABRQAUUAFFABRQAUUAFFABRQByHxb8PHxN8O9bslTdMIDLEMZO5Pm49zgj8a/PrW4y3zBSA3Y9q8rMaUZWkz0cDNq8TjLyIq7ZOMVllyCQw47V85XXY+mw8upVnYY4PIqNZTkGueMUtTepbcmnvwiYJxis2S+zyBzVwV2LoZ090hyzy4Ue9VV1e1V1DKX5wK6XFS0Q7Jq6OhttcIC+VHt4xgVJPfOnzeYwb2NXGlGnrHc55RTV5FjTvEF1ayg+cx9Bmun/ty4ngBaUrxzg1fJGW4KEeXQxJr4MTlsnNVpdQVAO31oba0CFoysZd7qrojmN3Uj0NclqPjbULCdmdxJD/dIrGfNszopqL+Ixp/iLDIcuoQ+1JZePFurkRBuKFBdAnTSOpsdfaR1bJ21p3OoGSLcrfWubEQmmnYUWktCKzvSoODmronJIOePSsai5kkdMErEsLEt2461etnLMMCtaVO8DmqOzOn0ULCC8hwv8q+/f2VfCsnhn4R2EtwuLnUpHujnrsJwn/joB/GvayiF6vN2R4Wau1Nep7BRX0h8+FFABRQAUUAFFABRQAUUAFFABRQAUUAFFAAQCCCOK+BfjV4S/wCEP8d6vpQXEIk822GP+WTcr+WcfhXDjo3pnZg5cs7dzxrU02bwTXPXD+X06+lfN1YpI+iw8pWsjPmmO08c1US4aNsjpXOox2R2OStqVLy9wfvc1yviXxh/ZUBx9761UIWE5XVkccvjD+0m2mXb325rcsdYigRHZwSOnNdCjy6oq7grGza+L4IT15PXmrs3ipLmParfN2rbkXLdmPvSdmWNK1uISqXcbhxjNdZD4ghgsznn8aybd9NjV02tInPah4qtFJHmYkHasmbxV8o25JzT1uZOnLmsZd34wnbciwnb61g6lqUk8ZLQMc+1Ek2bJSRw/iSx1GCyluUiKRr3PasXwEdR1K/dhvaOP7z44FdNGlH2T5lqTOvbdntegT7tqs3SuhV8rgNxXHXXcKTUixAgVA3Rs8VeWY7QAvNeZf3tDs0saFopCgdzWnZQtvGK6lJRjZHLN+8d34O8NTeKvEOieHreJ2fVrtLZmXrEp+8/0Vck/Sv070fTINF0my0+1XbbWkKQxL6KoAH6CveyVPlbZ85m8/fUS3RXuHjhRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAV87/tgeDTdeHtO8UW0G6WwkFvdMByIXPBPsG4/4FWGIjek0a0Hy1Ez4z1eMM5KgbD0NcxfxKrYB6V8xNLVM+lotxMi7PyYHWsi8OxQVODXNGLO5W5byMHWL5YISMnmuH1PQbzxOCsHVckVvT5bk88YK7Oa8N+E/O1iW2vJpIZE4G4Y5r0rTPhbEsDM2oszH7oA6V3Wppc0kTConK8jRX4VphEjmkd/4jir9j8K7uDJUSuoXj5a5ataK9DrpU1JlWH4YazbO1yY5vL3fe29KsyfD3xDFbeaFn2t0GKzhKL1PTpYRvdGho/wV1zVD5j2szyDqNtdPpvwH1a7Z1e3ERjHBNXL3vhJqQoQu5Ity/CqLRbKU3bxs6jgL61zGr2FrbwL+7UKvHSsKs5JpJ6nFKdKf8M8+1rw/deJpBpsKfuZWG444FdOvhDTfBPhyPS7SFPOzulkxyxPvXXSm1G0jzsRy7I5yL9xJhBgd62LOMgByRXNVdnqa4dWWpopLnA6Ed6vW7bmwOtcnJG90dkn7uhpw5jwcdK3NMQM4IODWvImjkk09j6p/YY8Gtrmv6140kKtZaeX02CN05Fx8rO4PbCED/gdfZwGK+nyymoUE+58pmE3LEPyCivROIKKACigAooAKKACigAooAKKACigAooAKKACqWt6NaeIdIvNMvohJZ3cTRSoe6kYNJq6sCdj81PiH4PvPAfifVPDt+V+12MnDL0eMjKMPqpBrgblAoO6vmcRFQlys+koVOaHMjKuIlYGue1BQSe201yre530pdzmtSiWdsdfSqlr5umXSTRE4X+GrptKVwqWsa01rZ+IZRKUWO6HccVtaNaNYOFbnb0rolLoYQTTsdbpFw0M24DrXqfg7VY7C2ZJoUcMc8jNc8FFvVHc+dJOLsdW/jKya1a3OnIEP+yKqt4osI3gRLZCc9CtbOMeXRG1OddfbOgvviRBYWCx2enRrKeuVx/KuQu/E93eGWSLEO/kgdqcJQ7bEzpV/ilK6PNPFWrOjSKZPNl9ua4mezluDmU/IfWuWcoynoi6ceSFwju7fR4mWJVMpHWsKaWS4dpJGLE9jSpz7nL7CUtWY19EqSYA59quWsWFDK34VFa7iOPuFpLhYjwAe30q/YvhuSaiFtzdvQ2YXZsKo+X1rWsLS91jVNM8P6Pay3esavcLawRRDLKX4Dn0UdSewBPaqpS5nY5p+7Bs/Vf4Z+B7b4beA9E8N2rLIun2yxyShdvnSdXkIycFmJPXvXTV9lSgoU1FdEfGVJOc3J9QorQgKKACigAooAKKACigAooAKKACigAooAKKACigD53/a8+DreLPDB8V6Pal9c0uPE6Rrlrm3zk/UpyR7Fvavg27lEiB1Py968TMYcs79z3MsmpU3HsZ00gC4HFYV+MKwGMV5Uj2I6Kxz00Khs5qe0tlcAMvNOOvyNGk4loaakDbk+VvarEGoy2TKJBvWodS6IjT5nZHTaTrFncoCjYcdQa7jSLnIDBwUHvUSThrc76SVrHSRX0KjdJjOKrLeQfaNylevFT7a5004KKuXH1SJF+cr04JrH1HWbeBDmdFGPWkpNGsrHC6x4h02Ekp88vrXJ32vfanJzhB2FS5cupzKlzGermdsn1p78LhcCmlb3jGS1KUlrF95s5pibYnCDpVy0Rk2r2sXFjGOmKuWkW4DLAc1nF3RPtOXobtjCWZVU9eK6jwF+1J4W/Zj+KdhqOp6dcapP5JgvEtNvmW0T4JYZIBYYB25GfUV14T3aqfYyrU3iIunHS5+pfgPx1onxL8IaX4m8O3qXmjalCJbeZe46EEdmBBBB5BBB5rer66LUkmj4upTlSm4S3WgUUyAooAKKACigAooAKKACigAooAKKACigAooAKKAEZQylWAKkYINfnb+1l8DG+EfiNta023x4Q1Ob9wF6WkzAsYsehwxX2GO3PFjqfNTv2OzAT5ayTPnm4lIbAPHY1nXJz93t1r56a1PqLcpmMMy/d49au2yFRwuSe9Z83KrFcl0i2Y1K4PWsy9RkPH4VnF2djaMbaFESvb5Kgg1PFruoQLiKd1A9DWU1zRsddOVlYJvG2vWwyt0/HSq/wDwm+v/AC7pCG9aXKnFFRXLK5GfGWv3BCyXL4+tRz3t9PzNPIR1wTRKkkr3K57ajoTPKADk1oQ6ZLLjOcUp2jG45z5l2NJNPMSrtHSkuI9i7mAGKzjHS5zyKONwII4qvt3HKj7vFdcFymUnaxZhcxrirVmWLDHFaOCWolG2pH4l8bWngPSZLqeRftcikW8RPLn/AOtXzVdanc6zqM+oXche6mfc7N1zXRQhyq56eVU71ec+4f8Agm/+0LJ8NPEk3h3Vb64fw1qcuDDvJjtJjtAlAJwAckNjrwe1frLG4kjVx0YZFfQ4SV6Vux8xxbhFRxqqx2mr/MdRXSfKhRQAUUAFFABRQAUUAFFABRQAUUAFFABRQAUUAFc38RfDGieL/BWraV4ihWTSZoG83OAUwOGU9mHUH1qZW5Xccb3Vj8mfGmhP4b1O6sA3mJbyMiyHqwBOD+Vc5DMhG09cV8zUjyyaR9rCNSMFKasyu8JRsd81PExVh2rmm2loUidSu8/NyKeY0lPzLWNS6jc0poryWUcnCjAPWqYtGi3KqE+vFYe0bVjph724+DTJblwFi/SrKeGZzcfNENvv0oioj91WEuPDjwkfIBTY9Cld8FAaVSlEaavqacXh9YVBK/N7VaW0EI+lR7NP3WROpfRDXk/dEAc1nyJ5g+bHHStOTVWMndRKchjiVl43e1V0OATwFrRq3zISbWoxj0NTrMsETTt0RScH2FbJcsdBqTifMvjDxTd+LfEVxeXTHaj7Y07IoPSoYZxjqK9Nw5YJLoe3l0+VanffCPxN/wAI94rtWL7YpnCv/Sv2s/ZZ+KqfEHwBaWU0ge/02IRO+7PmKPun8uPwrqwU3GaiupxcWYZVcCqq3i/8z2qivXPzQKKACigAooAKKACigAooAKKACigAooAKKACigCK6vILGEy3E0cMS9Wdgo/WvAPiz8Uo/ERu7LTph/ZNkMtKrcXLY5/AdK5sTVUIWvqexk2X1MViV7rcVufld8dfi3eaF49mih2yWhOZFbqOT/StEX8bRRTRHKOAy4PavEcXa6Pt82UI1VBbouw3azjIPzCrMR3kYHArkqJqWh5SXKTLbjcXANThSuKym21Y0hLoWorXzBwRVq208r8gjJz3rkUWjS9malpphiPCGtSCyaY4K8VcaSktCJSSYT6WpcBgKcmixQpyRTcJx3RDnfQgurMQjjt+tY12ojfAGBRK3UIMzpP3ZORwapzyJHEcKSaIyVrotyb2MieTzT0AqpIXXOPu1ppK3MUnpZjI5yoySABWbrGsJBpd8xPyLGf5V0U48z0Imo2PnFD5jzN/ebNKCYsDoK9PyPTppxgmjf0MF498ZAkU9fSv0L/YR+J76Hd6fc3Ew2qDayx/3t2PmrOlVcKiR6eKpKvgJw7xf5H6bwSCaCOQEEMoII96fX0CPxxqzsFFAgooAKKACigAooAKKACigAooAKKACigClrGs2Xh+wlvdQuY7e2jGWdzj8B6n2rxjxJ+0V5is2jwCC0Qn99OoLyY7Be2a569eNKPmerlWV1sdVShG6W+tjyHxF481bX5JL2+uJSx/1cRkJUe+KwfGWsJoHgmZ2GXmUg9sZFeJVq1ZX5j9QwWVUMFTj7KNm99ex+Xnxo1Qal4vv5eykrW74E8WMdCtBK+7au3r0xWiT9joeHnMv9pv1PQbDUo5gpRxg1tWlzvPyVwS5mcS95JmvDIGjwDRuO75uAKzS1HFWZoWNwmQPSum0uZZHzgYxWUkrhK/Q045lQkKBThL5XQfLTh7uxleT3IxdeaeBj0pGbH1rbmTjqSlIq3Vxt4Nc/f3IVyeMDoK55xi2aRUjImnduR0Haqks26IjPPpilCCjubJJKxlXMxi5xWdcXOBuJ4qlZ6CnotDHvdTaZticKOOtc/431ZtO8J3zAjey7R+NdlBqMlE5ZzfMjwtdegg4ZiD9KeNct5iNsn6V7Dw8t7Fxzih8F9To/DWoqzhEbJY4r6r/AGWNV+x6tc2e/lgCi5/iriqQUZeZ9ll86dbCXP1s+Cnis+JvBNkssm68s41hnz1yOh/Ku/r3KL5qaZ+R5lSVHGVILo2FFaHCFFABRQAUUAFFABRQAUUAFFABRQAVkeK/FFn4P0S41K9b5Ix8iA/NI3ZRSb5VcqEHOSiup8n+KfHOq/EHXZHvWBt4+iJ/q4h2C/1NY11Zvezq9wQlpFz1x0rwK85zqavQ/W8jwVPB4eL5UpNdPNIgtb+HVtYjgit/9HTq564rzr9ovXUt9GurdXMccUW7cOuRmsKt1G/c92NOSrKEuiPzT8X3bXd1cXEh+aQsaoeCNXWFngLHDcgV30o3otnwueTUcby/10PSrDVpbEqVOV9K7fQfEKygA4U+5rhlFNXictKT2Opj1DZGNrAj61Ol7vAUmuSUknodVh6XoR8KRgVu6brYWPk4wKyi00Uo3WhqxeI4gBtJz3FW7bXYtuZX6dKtLl3M1TdrkZ15Hl4+UCpW1uMrx19qJNJXE6ckjLvNWX5lyOnrXP3F7uXk1zuonojZRUbFeO7LH0FQXt6sJAQ81pGSWjIbVzCv9YRDtbgDvXO3WrNOWCnavat401vYU00rMpvc+SMEVzHjUve6NOg9MgVtFJTUjimtbnil1bhicjkVnkNEcdDX01N6WPmsXT5Z8yNfwzq8un6vbMZD5e7BBPHNfVXwD1hNN8b2bHlmI3EdBXBjKa5k0j7bhDFynCVGbvbb8D9LfhX48ufDaLcae6mN0HmxsMhx/jX1B4Z8U6f4rsBc2EwcLgSJjBjb0NVgq1/cZ5/FmW+zq/WILTZ/oa9FeifGBRQAUUAFFABRQAUUAFFABRQAUUAZviLX7Twzo9zqF5IqRQIWwTgsewHua+SviJ8QtS8WXSTXUwYscW8CfciHsP61x4uryRsfS8MYFYjGJytZdzJtCmm2aqB7sPU1k6nqMlzmLOF9K8Wd5Ssj9Vw1GPNe2iNjw1YrZW01w+PuV85ftH6kJ9F1Hc+F55HWitsojhPmrTkfAnjFvK+Vc/cauS8OXDW98jL1zjmvTw0f9mZ+bcRyf9qpL+tj1XTLz7RECTlvQVuWU7QPuzXlt68qNoR0Op0rXN6qrHaR61vRaspAGRurkqwcXqdUdVykiXWSSTU66l0Abp0xXJOXNbl0OhQs7FiHUJI23Z6VoW+pFhnrT53FamtSMdkh9zqO8YUkEDvVEa3LCpCsf8KiNTlTTMVTb0K0+sPIc7qYt8rDJP50k1c3dL3SKbU9qkKcAdqybzWRHlt3bpW0Yt6nI4WRg3GoG6kJxx6VUaRgRxj0FdME3ZIw5ZdxZd0i/NWZqyL9gk3enFacsrbnLVXQ8d1KEQXUiEdDxWbJErdhX0VJ+6meRiYJtoreUYpVK9q+kfgXcR3Wt6BcAkAHEg9SBioxicoXXQ9bhOao42SfX/NH6OfD9zDYxAHqowPauz07xDqHhS/aWyvLiAXIAbynwDj1rxqcpRfMnsfd4uhTrydOcU011Jof2nvGPhPWboahHZ61ZqcR2aqIZlHb5x3x6g13/hr9sbwffLDH4ht7vQLmRsBZh50Y/wCBJ0/EV7WFxarS5LHxWN4JxUKMsTSlHlWqWtz2Xw34r0fxfYC90TUra+tem+CQMB9fT8a1a7T4dqzsFFAgooAKKACigAooAKKAOa8ZfEbw94Bt/M1nUY4HKlkhHzSP9FHP9K8E8aftQa9riXFr4OsEsImUpHfXKiSRGIOGCZ2jHXBzWFet7OOh7eTZNPMqvJzcqXW3r/keWy+J9b/sjzfFGty6nqKMzyzs3ynk4AXgDjHAFV9InF463koyzj93/sjtXgym51Ln6xl+XQweGUVa662sat0waPdngdKy4E+0T7f1pqXMztpe7FvsdLqVwmm6EcDkrivk345zRyaawkO4yPgD1rOb9+xGCjpKT7nxj8U9Maz1Qqfl/dkgV55pYKSo2cfNXs4Z/uWj814jpOOb7/1ZHpmjtuhVk64rqNPbzUAbqK8qrDW44z5dDThjbdlBwKsrqEkICntXJNG0L3LMOqFSQWqZNVEZ5PSuepCx6kV1NG21ZTyT16CtGLUlRMrw1c8k2apWHf2oioSx+btWZdaoiZCn60Uoe9cS90oLq4B46CpZtT3ICPlxWiXUirsVJtS3NhTVGeRR8xbr2rVPQ4ebUhi+f7tWBAwQALW0NUYVLrQekCxfM1YutvlGx0ppdDFSvOx5T4liEd6cc5FYJGOK+gw7/do8/GL94QyrxXtH7OF4ZPEVtbM+FjYMv4mrr/wzTJZcmPj/AF1P0x8DyGGygByGZR+VdXeSqI8OCSnzLzXiSV02fpsovmi0eO/HG0FkumeKIJpIZ7dvJYKeGB9a5G68R217ZJLHdIr7cnd8xB9KUG4JtH1OXKMsMrq9tCp4R8TzeGtSldtVuIPP5XyCyc+vFe/+Cf2gNYsYxDH4hu5btB+789mZce+6uijipRsePneUYXGUuRU4p97I9G0v9q/WfDUsa63ZR6rbykANCFheP+YNdvp37Znw6udWXTLufULG7wCTLZs0Yz/tLmvUo4tTdmrH5Vm/CFbA0fbQmpLtZ3/U9q0vVbPWrCG9sLmK5tJl3RyxMGVh9atV2Hx7TTswooEFFABRQBi+J/GGleD9Plu9SuQixrny0G6RvYKOa+d/Gv7Umpaw1zB4dtTpmmxjD3V2uLgn1C52qPzP0rCtWUI6bnuZLk08wrqErxj3seBa54khvZJdQvtXlkuZG5kYl2etzw7qEE+jxXEasC4OGcYJ/CvEq1W3zH69hsF9VoqjHp1sYt6W1rUxB5ebaFsu2f4h2rotPYQnCqNo7U7e6enW/hqJNJcuS2OlS6UmJCW6npWcfddzkklGm7FrxWfK0xV7Ed6+WfjBB597aRNErKpJznpS+KpYMH/DR8sfHkR/2naJGu0rbnJ9eTXkOk8KB716tDSk0fnfEqtnX9dkeheHj5gVeg9K7m304EArXnVF7xytWkXLdJLVirLxV1YEmXbiuWrC7N4tIifRztLr26CqctpPEMlTWcdXZm8anKiLdJFhj8vFTw6lLGOGPFVOCaNoVeZCS6lK/VzkVGZXdsseKxjBbkqpZgZkXg8YokvvbAqoQsmzGc7iZlusbV4HGamj0/acv2qorldiLcpbitRsAUY9q0I7QIoJ6iqabOWrLUq30sccZz1xxXLauzvEVGKG+YcU+U808RqUuNp6iucfOemK97DfAjgxZC2Oma9n/ZisGbxS9wozsCgH05rWt/DZGVxvjYeR+lfgoH+y4M/e2iupukaELuyVYeleHNcqaP1ZWtFeRyOt6TD4j0HUtFuJFy6HDHnYex/CvGtA8Lafoz+XLH588ZILse+fSpu42Vj6HL68qXNBR3OilurWPyzJCmVPHyg4rsrW3tNY0fZLErHg9NpFP2v2bG+LUopSXckm0kPbgLMWAHyoRXO3VlqFyjxnbEqnCsvORQ5M5YuNS6ehWttN8SaDIlxZeOtY0+NCCEtZ5I9v0w+P0r6Z+F37T2pWXh+DT9WhbVJbQBDeTTbZphjgtxgn3rrwWMlF8jWh8jn/AAvSzF+1UuVrsj6qor3T8XCmTTR20TSyyLHGoyzMcAfjQNJt2RwHib46eGPDwkjt7h9Su0B/c2g3c+7Hj+deUa9+0Nr2t2zm0ij0q2OQVVt06j/eIwPwFcdbFRgrRZ9Nk3DtXFVbYiMox9D51+IXxSup7srDdtJIHy80jlnf6nvXl+t+Lbm9nHn3DyKeMHgV5km6r5mfsmUZRTwtBU4t6Gv4E0p/Et4ZpIz/AGfAfmz/ABH0Fei388iBbe3IMzDAHoKwnfY7q9vacvRFjToVtbcRKSXzliepNa8MYVMr1rSbtZdDgrPUjZwPlyBWho7CS5VAelTJ6e6YVV+7YzxzMTboijKqMmvnLxv5dxrm+VCwQYx6VEL3DDwX1dHy1+0ja/Z9bsHVSsMlq20kdwxzXielqowoPevVoXVE/OuILPNry/rRHeaDF5Lo+a9I0SZSoyRn0rgxD93QwaTTOngsop02sv41HPojxn9zgiuXn90iGjIls5YDh8in7ARyuawnJnTCzVkNfS7edRvXFVpNCi6IfpSU5OBLi4vQj/4R4CPqd1NOhsq4xx2qFJppFOF1ch/sDGCzDPpTRpkcQIA3D0rfnfLZkRXRk0cP8KJtFXo7FQgJPNQ2xzdtSURJGdoAH0qCeUKpHpW3tOVaHPCPPK8jEvpizcjGK57UZMK239amNmjom7KxwHiBAJckgkelc5OMnNe5hvgR5uJty2KrxivoP9luwkE01xyIvMA+pFbVtYWFk9NfXY/11R+g/g1nWyikyMHA2967J79pEEbqMDpXjVJO9j9QdJWicjKpt9dlQqNs3SvLfGDnQfFd1AYwizDzVPbFNSSin2PpMukvbK73RgS6u88g8oDGeleleF7wmzQyEZ28k1jduTbPQxqUouw/VNcNuR5eflOMetRx68HUFhtPoKhXdziWH91Fe/vy8ZUrlcZ96yNL1SS0viQ7AAHGe1XTVtxzglGzP1QqhrOv6f4dtTcajdxW8XYyMBn6etfUtqKuz+YYQlOSjFXZ4b4z/aqtNPMsGj2Z89CfLNypIl9MbTx+NeD674+1jxnetfavb3ouWPAa4JjQegXpj8K8yvjVH4Wff5HwsnFVMZBxfTUpS6nd6hOnmvHHEg2/u1wSPc1yfxE8ZyadaG1tSUQD53zy1eZJ87Uj9IpYeEeWEdkeFXHiZpro+Wd6N3NXNEsLzxTq1vp9uuGkOWb+4nc10R9yNj6GlFU6d30PoSCC18LaPHFEuyCFAOO59fxosEEga7lXbK/T2FRK19Dx7tpz7lq2TdIGHygVYa+aNiq9Khu8bMzlHmdh0WHXcx5PStzQ4BZxSTt9BWcXKL0OTEu0HHuZeszfaA5HI714L4xQLrt0w+7x+FaU5O5tRXLFI+c/2o9O22Hh25HMTJMoPocivnXTUAK7R3r0qDfsnc/OOJIL+1L+X+R3ugB9qhhXcaeuQhXgD0rgrOF7XOaEI8tjs7C7IUEjpWvbXAJ3AVxJIUYWuXF8i4iO5BVX+zkB+UgA1SUJbhCM4vRFa70x0BKjP0qoYWtx86nIrNrl0iac76jogZBnsKhm3DIzzU8r3NE7IqJFIr/NkinPaBhxxiqa1Mm438xrFYcHIyKctwrAk8CiTjEHCUo6lGeYhiVJqrdTtHHkt1pJLdjhGyszGkn80sdx4rK1FxFCcdK2hFKQTir26HE65sCfIOe+a5x1Ga9nD/CefiYWdiuyjOBX1N+zXphh0nTljViZnLO3Yc1deTjDQ2yWmpYtf11Ptrw9G1tbQjoQAOa6tpUwPYV5M9T9OnCyjYwvE0BjS3vLfLSxNgjPY15d+0Bo1xq3hi21m0JDabIPtDA4zGev5HH61Ft0d+Flbkk+mh5noVyt5HDLHIVRwMGvU9LnNlp6KuWwPXrRLR3PdUOfVlLUrybzBu6E8YqZbpFtyf4hxUKUov3TedOMUnEoSa2z/I2fQEViG5uhdMEPHv2qm0tGck4Rk7M+9PHP7UC3F3JpnhhGjZX+W+KrIsg9h2+teReJPEN5cmS+1zUJZ5ZB80ZYtv8A14r1cTiv5WfjvD/DcYxVTFw1e2pwTeKPMn8q3hCRZ+UNyRT2umGC7lieetebKKa1P0iGH5afLYr6jrcVjb4LYYivG/HmttcyMoHHf5qdNcu5tSi4xTfQ4JJeTt9cDHPNfRXwu8FN4X0cXF3j7fcqGckcxr2Wt+eFrM7a2I/ccvVmzNLJrl+uCRZW564++fpV5ned8KdqrxQoxa0OdxUbLsXrfdGgz+VSAg9BisJp3905Zb6CxtvmVUHINdagWKyRe5HNF4qDOHGfZRzGrN5YbZXh/iZGl167BOVGMDFTSdnZnTBpRVzxX9ovT0vfA0UpBZrWcbP9kMDmvli0iEDqCMDrXp0pLlaR8JxJStjozt0O00P93tbkiu20pC5UDoO1cNaCT1PKp7nV2I2AEEZ+tayShEHrXHJraJpdFuCQDg8CplbYwweKxcuU0SLYB2bj0FVrhkbhlxiodSWyCVOLVyuMbdoUYqpcR7se1aKdl7xLjoM8tipYjiqUwYL/AHR2qHXipaMn2S3sVRHuxvPFNeARnk/QU+aMo3ZstjOuLkREqnJHaqM2+Qbm6HtVwlHS+xDSGLEPL5XArI1EoqFR0NbqSvoYScjiPELKWwvSudZABwOlevh/gOeuubcrxoGuUUdzivuX9nzw+9po9nG67Y1VSvHtTxTaiehw3SjLEO/9bH1BpPlxBUySOxxWrLjI2HivKdz9Dmmmipqlq1xpc6oMyKpKjNcvpsUWt6JPZXK7oLiNo3B7g8Ua30OqhJeya7M8I0HTT4e1270i7OGtpiiDHVR0NelQsptwqn5RVRXMvePoaMZKkmzJ1i+jhAQngVUg1COSE5bAxxSVJ302OqVKXJcopKvn7s96vCMBfMxxSnFP1POqtJ6H0Sbu20q0ENtAsaDsK4vxJqvmZwo/E0nzuWp8thaPKtNjiItTaO/ZeMk9u1bxvMWxZjgDvWs07XR71KF4nC+J9cMLEb1x65ryTxL4kEsruwGegrSnTctB8ia5Udt+z54IufFGpNr93EBpto+IEYZE0g7/AIV71r17Ks8dnbqolk/1mP4V9aiUHGfKYy5ZVlHsAYQRrFHxgYqWOFmA2kqf51pJSXwkvRXZaCmHgtTVJzy1ZTa6GJraNYhphIR07Vo3uoxwy+SOSB0Fc+ux59W9Srbsc7f3mS6vC5OOCB0rxnU7hr3UL0xDBU4HrW0Kbumdfsmopo8++IOlnVvA+qWMoAaQAqT2YHIr5FvtMksb3y3XayjGK7KUtT5niPCucI1Y9DpPDaNjLL19a73TrLKLtJU+1ZYmLuj5KmnHc24bXCAc/LUwlkgIHJFcMuXobpLct219uwGGBV+K8RvlBHHvWM4XSNoWaLX2ny04fjFNMyuvzAk1hopam0YJxKvnrESC2Kia5VDuNKo3dIlwV7IQXiq3tVC6vI5ZduenTFZ6OWgnSlYoy3ZDYQEYqCSRnO5j9M1tFWVjKSskiqsJLkqoIqZrbaMMuOO9XokkjNprRlG4cRDaBk9q5zV9xGcBQO1dVCN9zNuKdjhtVl8yUr2U1kygnOOBXsUlZIwqp2sangXQZdd8W6dbIpI80M2PQHmv0O+G+nfYrSFVjCqABgD0qMUpSVontcMYaXtXP+uh6tZbRIoPWrErSRTYRsA9q85Pl3PvJJc1mSm7KJhu4xXI2R+xavPEpIUvuFVZ3TRthoaSPPviroJsfFlvq8I/4+kCtjpuH/1qbb3JjtlzJhQOeaEtD6PL3z4dX6GLr9/G0AKkZHp1rGj1QLGrHp6GhKfRnTVckrXHWl/mbKc4NdPaTy30aoUKhR1xUJSucKoubdj1TWPERtnCrIGZlxz2rjdSuppWLs5+lbQtF6ni4ekkyho6m4v95Gav+JtWGn2/l8Zx07USkpbHctNjwnxRrc9zcP8AvAilvyqj4C8IXfxK8Sw6Xao/2JGDXlxjiNM84Pr6VtF8q0Kk1GLZ9ewWdh4L8PwWWmRCK1to9kMYPJPv6kmq2mWrGNrq4z9rl5IPVfb6UNa83Y4Kd1Tc3uy1DCep9a1bW2Ea7m5rOUrGVaelivf3A37FTI9RVixsgYlZsZ7L6VjFcurIk+SmjUtHIJihOG7n0riPiD8WfD3wlg83VJDPcynCRRjc5P8ASlNdUcijeTjtfqeYWP7XGj3uqLDHo9yUnYKhJGQT2qzpej3gu9S1i9xFDMS6xZ4jGc80U297noLDShD2nNdHnPxH1lZtIvTAf3Z+4ynrXjOq+Hj4o0jzoI0Oo2y84H3k/wAa66cXaxzVsE6+FcdNUYWgwmNtjdVOCPevQtHt2AQFflrOqm5H5rVw06U3BvY6MWaGMbTgiozYkHJXpXHU+LQzppp2ZH9hUMc8U1rUD5lUgisZJ2OmnuAjIO0k496ZIZudjdOlZO25om+hVllmPDKMiqrXMzAjbwtQovqK7KzXkhBUkCnJzgqpZvar5Uncq9yzbWE8rlmQgVNPozD5iePSndGUouL1KhKWrFVXOOlVbqd5MZ4PpV0o9WRZmbdq0I3cmuT1e8ModRwRXZSXYhxuchqC7H96yp1IcHNerRehy10+U9m/Zp8NfbdZu9Vk4S2Xy19yev8AKvtfwZD92RW2xjoD1rmrt87PreGYe7/Xkd1bRmSVSp9xU886h+eo4rkk0fVyV5WILmUlQVB96xtTh2Sw3C8EHBpqWljooe67GR4/0033hn7Q4Ym2k3gL6dK8tk1VbW2ZWU9OM0P4rH0GUNOm4LucjrerPEmUG7notZtheT3jbW4GePWlGEm7pnZXpu9zsPD9nvcqEKuPWvQdJtGtkBlA3VbXLI55R5Y6dSW6Y3UAmzjaM9ay7q/JQCP72MZq1seU1Zl7Rka2gBXlm5Jrh/iH4jWG4ZYn3fL83NRe8rCjseTaFomrfEjxImlaVE0kkjZZj92Je7Mewr7D8GeDdI+GHh5rPTvliyGnmfl5nxjJ/wAK3fwnNiZ3Sgupftrc39w93dcAf6lewFWo4TK+7GAvAqY6IxlK2nRF2NFLAKOe9FzNsUKtZ1Ohz2blYz5njhwxbJHXmrEF1JcrhEIU9Wz2qOvKbSj7vMzzL47fGaL4ceG5LHSLyJtbufkQK4LQjux9MV8aavrupeJr9rvVLyW6u2+9JK2TWM3bQzjGz9Tsvgjocep/ECx85A8VurS7SMjIHH619BeKdTuJvD9/aRKd8iMmB3FVh3qdsYOKSZ4xeaPLbeCAjlt+TlT1HNcvoMkumXiujYGcHjtXZGLvzI7sNT5tA8aeFms5T4h09P3JA+1RgcD3FbXhK5h1CBGDcY6elKorn53xRgXh8U530f8AmzrHsP3Ssi4pBF8o3LgjvXJezPmISuxZoE2jdHULW4KAAbah6otOzK8tiZFwBxUX9lGNlAfA+lcnLyxNYy5mMk0jBz/CKY2nIuBEv3qlauxotXYiTQmWQgxjFattocaKpEQz60+Wy5Sd9C1LYLAACOK57WMrnaCB0HNaUo8yMk9TnpM7iGBzSfZ+juOM1unYzbM3WZ2jG1cba4rUUChjnmqppqVyHocrfMWJVgBjpWWQ0kyIoyzHAAr2aSsjGvFxsj7M+AnguDTPC9pG8bLK43yMRj5j1Fe96RafZPLRVIUVxSfvn6Nw9SUaF/66HX2r9l7Dih3ZuMcg1k43Z6dveIJpWSI5/SqFw0r2E2wZ2qSB70uSzepvCKsV0I1XRHgZ9rPFtIPPNfOmuXrw3VxazHLRuV2jjGDTS1ue1lPu1ZR7GBcf6ay+WcEHHWtbSdMeWRUMRJB6g4xVKN/I9GT56ljvdM0tbQdSrYGSa2r3VEtYly+B9a1cl8I6kbtLsRXkvl6Oij7zdcVj2Mh89Vbke1K/c8ipHldhnifxgmjWrRW7FZFFeceGPCGufGHxK9pat5VnAQbq6fpEDnjHcnHAqYXg7o5pLkhzvofVfgT4a6H8MNFlg0mJjK/zTTS4MkxHTJ9PYUqRz3pM+pIQFJ8uFOR+NZp++2eVTqSnUc3oWl1FIIsrFIQB/dph1hpI0EMJZj/CRihyVjoVG+rZJHf3q4VYFyevtTLhHkk3sxCr19qrkSW41GEJXjqYfiHxZ4b8K2hvb7UYYo4+q7ssT7Dqa8Q+JX7Uct7ZTad4WheBHBVrqQYbH+yO1ZznaNkiuVyV59D52u7m4vZnmuZZJpnOWeRiWY+5NOgizgdOK5pJipJynqfTP7OngIad4avdevI8XF+PLtMjlUGcn8T/ACro/FFxFotlLNM3zA4GPU100YcuhrWk2+Xscle2X9p2cZZRtCk4PFcBfaEFY4AzngiuunaN4ndgpcs+Uu6XI9lH5UwEkTjayHoRXMalpU/grUhfWhd9InblQM+UfSqaurnBxNl/1nCuV9v8meiaFqcWo2ke3BOK0GszIPlHSvOrU+Vn4/KLhNx7CNbhhtYciojYrK2PujtioUbDU9B6aaI34BOBUjaTHIAxGD6UpVHF7EwlZgdKQqM9KR9HC4K/pWfNrexspagml/NytTGykjxgYA7UuXmM+bSxR1KJlixt/GuQ1AHeVI5Hf0reG1ilaK3Ma4UbgO/rSKqxghjkCi+tgT5Vc5vxFIi7m3YAHSvPtW1IAHHbpXTSi5NIal7pzc8xcljXefBLwDP4y8TJKI91vaEM2em7tXoyfLAxow9riIxPsvwtp975wiUgpbcsG4Br0LRrx7x2MilQvHTpXBo0z9Uy/DqnQST2R09kdillHy1BdsxPy+tYJWZpFLnKM1wUOCOKsW+NpUDIYYrSceV2RvJWiYuiFkmnhkTASYhc+lfOXxt0ufw78Q7oFfLiu1E0Jz1B6/qK2Urcp6OEqezr2XVHJ6HLJLeomSV9K9O0C2xG0jHhe1OrFWvc9mlDW50Et0Y7fc2Aw7e1cL4n8SyTfJEWAU9Kz5bux2QgkjuvtP2i2uYVD4jfCnuBVMRy2URZc+Yeh70TvLQ8NxctzkNb0q4upXfO7d69q7T4P+N7bwBotzZT2pa4ebzGdRwwxW1P4SK+G9pSdNHaar8fIPLzDphfjjnFZ9h8Z7i8fP2AIp6AnpWSg7ao4aGWRjG0riX3xhu7DLPao8f91OtULj9oIKii10f94R95m6UlQ7Gyy2DejdjmPEPx88QSW+y0jhtZs/eC5J/OvOvE/wASvFeu27WtxrU/lMPnWIhAfyroWEi47mssDSpxvF6nnszSSN+8kdz6sc1F5eDis5UVFXTPO1bsxWgCrnmliUA4rlnG25rGKjI+kvgj4yudY8GtpikCbS2xn1jOdv8AUVu+NvDz67pEtsZdshw6sOvHNa3lo0XKG8znbGb7Pp58/kD5QW/KsmTTElcgZwTwK2jBXua0Ze/zIqy6UF3KE6dKt2Nmk9nLaXERMMgwQRwavmaPTn+9oSizhr5bjwBqmxg7WMr4jfsPau30XxLBdxqCy59qmpBSjc/GM8wUsNiZKzszYMkUoO1lJo2x4GCBXFY8FSlHoOjILbVOcVLtONvFT7O7Fza3FIULz0pVYINvT+lSoPU3u+UkWWNV5FV7i4EfzcCiK5dDJJsyNSukaItvBx6GuH1S8wW5qvZscIrm1OUvdVNvu5+UVmXHigQw/KVJNEYJG7inE4/XNYmuQRu4zXKzyGQ5Y8CvRoLS4SaUbFKXmRQOhr6gsJpfhR4A0iWxhSC5ljUkSD5nc8k/StMQ7QOnJKMauLfN0N3wj+06NN3wa7pamNwB9otuGX6qf8a+gvCHijTfFHhqC9026SaGQZDKfun0PofavMs3sj9Gw1SEXyJnYaPM/wBiCnBbvmnSuy5OBitVBR1NGlzszrwnZhVqDTLllk2lsHtWvKmdainTY27WW21ZXAyrj8M15D+1Jozz6XomtAZaCYwNjsGBI/UfrWE1aL8iqdueDPOPCGnCNVlwvI6nrXcx3dpbRFU3g9TmtYwc4ao+soKajojnfEHiQRN5QkbkcYPSubS9a7jIVGLA/eamrrQKujPYdMcfa7stjJfJUdqrXrmV3CngfpWllezPPSa3MR1807C2KzriRIN0SruOOfUVSUlsdCgt0RadbpK+4ucL1WtO0VoN0h4jzwPSk5vZmNXlTsVb5zO74PymsGS3EchbzMDsMVtS7EULqVuhVv4xKoBH3RkVg3VipMhDYOOAK2ScVoVWhGKOZkjKk+1R4GQK5ZWitTw3H3rEhjJXA6VDt2PgiuOpzNXNXHlZ1vw28Vy+DfFdnerlrdj5dxHnAZD6/Tr+FfUOtMtuH2YbcuR+NVBy5C5J8tuh5rfwG6u7ez37dzbsVr3ulJbR/MwUgcEV0RajExovlVluYkfzT+UOW9a0LdMgqPmUdK0hGMo3Z60JuK1G6ho9nremTWN9FvikOB6ofUV4F4jg1j4Za19jn3SWTnNvOBw6/wCI71lOVtD5HinLnWp+1im7G7pHjVp4xiQo3qTXQxeLJEVVaQMfSuLms9T83lQto0XovE5XGHC+1a1p4lSVgC4BraLi1ocrpSXQsyaqhX72T7VVfWQDkZ+Wsk5c2hSUrWIbjXTKg2NgCqk+tkx4L59apqF9RpSsYl5reFKlht9q5fU9VMrfJxTnNrRlRgnucxflp2KknFZtxaRxR4zWDm1KyNXFJHP6hCI1JBrnpzjivTw7ujGsuWJ6b+z58MLX4heLHn1QN/Y+mqJZVwQJmzwme3cn6e9dL8W/Gi+MPEbfZFaPTLH91aoevGMk/Uj8gKjFTfwnvcPYZcsqvU4WYGWL5hXr37MfjeDwn4uWw1C58qwvWAXcfkD5GCa5qUuV2PpYRbrxl1PtA3lvHdgwDKOueDxV6VBNEuB+Aqn1PRkpRs2Y90kkQYKp/Gs6O1cyBjwQe1VZ2ujupSiokuuwSfZraZXKtC+5vcVjeO9E/wCEs8CahbmMNIqeZGCM/MORU2cvUKbSUZdmeEWVzFpOlKbgqJRn5R1NYt94ilu2OB5ceMYHWt4znFWPtqFRRpr0OfuJjJJlicZp1vLKrjyyQfTNT1OOcuZs9ugg+zCaQHG5vmHrVCeQvI237vpWj5bXMGo20KRZDudRgVjzESv8gwRxT9+2htGyWgWsZt5iQ2fUVc+1bkZScpUyUd3uc9aKuVickkcDtTXUhOEBPqaum2tRUouxSMfm7lbg9jisDV4VtVZV4LdK2jNt6hV1VmcrPCEPSoFi56VlVj0Z5Moe9oSBCq+wqCeLqR1rmnBqBU1oRwy+U3NfV3hrV08SeF9FuIgTNPGFYHsVGD/KopP3WjNz/dNGddWaJ4qaIDAVTg1U1Vz9o2zE+Sg4xWsFeGpOHSbuylfRRWdmlxHnLHirllHcw2wuFAKtj8K0hKzt0PRnpG5cWTfgAYbrWd4t8K2fjHQnsLuFd3WKTHzRN6ipqRV9TDEQVejynziNAuPDmsy6Ze4E8TYypyCO1dFFpEzEMr9BXDVjZn5PmFB0K7jLYl8qe3X1Ye9KupPGV4wR71mqigcMo31RoJ4ikjixjkdagPiYhSRxWkZq9zJRXQo3HiJ5cBSQKz5dbuN3lL0PvROyYctiGQTzsuG4qx9iIQA9amc+Yl2iQS2WFOVGBXPamSpKg/IBUpagr31ON1GUSFsdjWXa2kmpX9vaW4BmnkWNATjJJwK9eirRMq7TskfXq6NF8DfhKNMs5C2o3K5umKjmVgM4PoMYH0rxWaOC8lQJCEAX5gD94+tclWcXK0z9EyXAqlhVG2r/AOCU57RF3BRgVWCGBgw4x0xXLZx3O6rSjGV49D7l+Dvi638X+AbCSNma9t0EUgYYww4616DYXrxfLL1rscoXSR0TipRZFc+bITjhazLhbhGXy2oV1G3U1o8i0Zbd3uNLnhOPMZMVFo08tzZ+QcbcFapNKQuVKL8nc+PPF88lh4q1Oxlck21y6AdgAeKx2vyejfpWcJ3Z9HDE/u0osakjs4yeKusmXAXjitIJvc1p3kj/2Q==',NULL,NULL,NULL,NULL,NULL,'2026-04-21 12:06:55',0,NULL,'2026-04-10 19:10:34','2026-04-21 12:11:28','anonymousUser','henriette@test.rw',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wings`
--

DROP TABLE IF EXISTS `wings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `property_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_wings_property` (`property_id`),
  CONSTRAINT `FK3xybcbee1hn3gqm9vom6i1vgh` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wings`
--

LOCK TABLES `wings` WRITE;
/*!40000 ALTER TABLE `wings` DISABLE KEYS */;
/*!40000 ALTER TABLE `wings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'rentmis'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 15:36:29
