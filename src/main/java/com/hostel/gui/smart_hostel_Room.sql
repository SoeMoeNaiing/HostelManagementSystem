-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: smart_hostel
-- ------------------------------------------------------
-- Server version	8.4.10

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
-- Table structure for table `Room`
--

DROP TABLE IF EXISTS `Room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Room` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `room_number` varchar(10) NOT NULL,
  `floor_number` int NOT NULL,
  `capacity` int NOT NULL DEFAULT '2',
  `hostel_id` int NOT NULL,
  PRIMARY KEY (`room_id`),
  KEY `hostel_id` (`hostel_id`),
  CONSTRAINT `Room_ibfk_1` FOREIGN KEY (`hostel_id`) REFERENCES `Hostel` (`hostel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Room`
--

LOCK TABLES `Room` WRITE;
/*!40000 ALTER TABLE `Room` DISABLE KEYS */;
INSERT INTO `Room` VALUES (1,'G1',1,2,1),(2,'G2',1,2,1),(3,'G3',1,2,1),(4,'G4',1,2,1),(5,'G5',1,2,1),(6,'G6',1,2,1),(7,'G7',1,2,1),(8,'G8',1,2,1),(9,'G9',1,2,1),(10,'G10',1,2,1),(11,'G11',1,2,1),(12,'G12',1,2,1),(13,'G13',1,2,1),(14,'G14',1,2,1),(15,'G15',1,2,1),(16,'G16',1,2,1),(17,'G17',1,2,1),(18,'G18',1,2,1),(19,'G19',1,2,1),(20,'G20',1,2,1),(21,'G21',1,2,1),(22,'G22',1,2,1),(23,'G23',1,2,1),(24,'G24',1,2,1),(25,'G25',1,2,1),(26,'G26',1,2,1),(27,'F1',2,2,1),(28,'F2',2,2,1),(29,'F3',2,2,1),(30,'F4',2,2,1),(31,'F5',2,2,1),(32,'F6',2,2,1),(33,'F7',2,2,1),(34,'F8',2,2,1),(35,'F9',2,2,1),(36,'F10',2,2,1),(37,'F11',2,2,1),(38,'F12',2,2,1),(39,'F13',2,2,1),(40,'F14',2,2,1),(41,'F15',2,2,1),(42,'F16',2,2,1),(43,'F17',2,2,1),(44,'F18',2,2,1),(45,'F19',2,2,1),(46,'F20',2,2,1),(47,'F21',2,2,1),(48,'F22',2,2,1),(49,'F23',2,2,1),(50,'F24',2,2,1),(51,'F25',2,2,1),(52,'F26',2,2,1),(53,'G1',1,2,2),(54,'G2',1,2,2),(55,'G3',1,2,2),(56,'G4',1,2,2),(57,'G5',1,2,2),(58,'G6',1,2,2),(59,'G7',1,2,2),(60,'G8',1,2,2),(61,'G9',1,2,2),(62,'G10',1,2,2),(63,'G11',1,2,2),(64,'G12',1,2,2),(65,'G13',1,2,2),(66,'G14',1,2,2),(67,'G15',1,2,2),(68,'G16',1,2,2),(69,'G17',1,2,2),(70,'G18',1,2,2),(71,'G19',1,2,2),(72,'G20',1,2,2),(73,'G21',1,2,2),(74,'G22',1,2,2),(75,'G23',1,2,2),(76,'G24',1,2,2),(77,'G25',1,2,2),(78,'G26',1,2,2),(79,'F1',2,2,2),(80,'F2',2,2,2),(81,'F3',2,2,2),(82,'F4',2,2,2),(83,'F5',2,2,2),(84,'F6',2,2,2),(85,'F7',2,2,2),(86,'F8',2,2,2),(87,'F9',2,2,2),(88,'F10',2,2,2),(89,'F11',2,2,2),(90,'F12',2,2,2),(91,'F13',2,2,2),(92,'F14',2,2,2),(93,'F15',2,2,2),(94,'F16',2,2,2),(95,'F17',2,2,2),(96,'F18',2,2,2),(97,'F19',2,2,2),(98,'F20',2,2,2),(99,'F21',2,2,2),(100,'F22',2,2,2),(101,'F23',2,2,2),(102,'F24',2,2,2),(103,'F25',2,2,2),(104,'F26',2,2,2);
/*!40000 ALTER TABLE `Room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-14 15:14:18
