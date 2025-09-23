-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: payment_mgmt
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'123456','USER','anas'),(2,'654321','ADMIN','Usama'),(3,'$2a$10$MfGOa6XqLyOMK2nrh51Hw.7tn8TipMz8mDz0ivb5DlAJSXlJ4IZum','USER','Bilal'),(4,'$2a$10$ErDs7TtPqlbafh/YXvqkEO1f5.aO9yW4W.A72KhGVs8TJyB2Gir2O','USER','Babar'),(5,'$2a$10$5Vl4XUdFkQrJlt0Z5zbpBO4jm0COJhP4qYi8foikqYVUZuwv7Vjm6','ADMIN','Ali'),(6,'$2a$10$EFcNKtQF6N/tuJe/XHWrj.R4N8DR5UodhJUPZB8PAspR1s5.DakoO','USER','All'),(7,'$2a$10$cVjHRiGcU9AR2gV81/Ht2eO3vFtUH2xqdXzVMQ94OjbM2b4hg3SfO','USER','james'),(8,'$2a$10$mx1OYq5s09KHulH1msPHMOtCXNYjOwDuoI/FEZcInhRxzPKfdwU76','ADMIN','james123'),(9,'$2a$10$wA5Wj.RTzdoVKHtEUZ4cAeqQIu5bm678CwT17KF2Rypynhhh6FF86','ADMIN','james1234'),(10,'$2a$10$IU3MYAzeZzrEzo4mGithGOGrGtksZcpPmTzS.AXIvpacpZZHzDj0y','ADMIN','james7'),(11,'$2a$10$xOsteHHPMTOreOvMdr6UD.dLcn82E17eviRipxnaa3I/8vfsCrjMq','ADMIN','james7v'),(12,'$2a$10$0LT5VhLua4arjUQwfnK30uRBrliBW9gzwz0fwQlIOw./BQL8TiZYy','ADMIN','james7bnv'),(13,'$2a$10$cFHJkB3GGzFnr.abkCDqPu8QkbdK5/1CngFiuL2FuemnoKnZLmbr.','ADMIN','jingalala'),(14,'$2a$10$xSIc.wTdz0gOJRXfQltWb.iA9LNoU5ssR368omOsu7pOfZiltggI.','ADMIN','jingalala1'),(15,'$2a$10$CAC/tllqWlZ248PizH/zReTSypUhmwrozzUSuIYnyhlJZSzITSI8u','ADMIN','jingalala12'),(16,'$2a$10$0DyPNNmIwDrd7OwujM8T/OiYR..A1Se/VMW4w64PCesYuyE/oOumy','ADMIN','jingalala122'),(17,'$2a$10$vh1RIy.nQRuH2d0.fx8/cOEkQtKO1uuF.Sp0WzP0gLHb2aiTHcYTC','ADMIN','jingalala1022'),(18,'$2a$10$wQhiqszCXvhcH6F.JROKD.D5gOeopUhNVlpfxiQWNH9/EY5.I5w.O','ADMIN','jingalala10220'),(19,'$2a$10$SypFm93P6VFJJvXrZOHu6.EzXCh45Vqx4HpC.hSnCP.Bg1sr2VctC','ADMIN','jingalala102264540'),(20,'$2a$10$wz3j08x0THnxUJCRNTkH3uNQIVlRoV10sYPVRqCMhds/yNedk5eru','ADMIN','jingalala1022640540'),(21,'$2a$10$L7pMfoPCl2kPSAk0VHZFiugjk.lm/1hbg1YDwEV2caVx4Np4gtSYy','ADMIN','jinagalala1022640540'),(22,'$2a$10$vSXMrZ6LSUnfsfSMrEm/5.DaLF0QlWis4BVYu5rvP8mYUuhxY3yky','USER','jinagalala1022640540123'),(23,'$2a$10$bsFK8oDb06NtpWaaAAhX1ufw8/FASuVNVbrVbQJpbqpBmRMQCCpJi','USER','AAAAAAAAA'),(24,'$2a$10$toiW.bKx7B.bEMxD/v3bi.ZMK85TaJC7MEJNLKox0w9Y5/IE083ge','USER','b'),(25,'$2a$10$OZTjW0mv2DmwTuu3zC2FIejWCZEcEWGK/sv8MTHxZl87qDH0B8rCS','USER','ab'),(26,'$2a$10$E91kO5ChzrWGNqZIbqLhmutYN5oFlcwPHvW.MMuqxsHZ8ieuOzlO2','USER','abc');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-06 17:49:53
