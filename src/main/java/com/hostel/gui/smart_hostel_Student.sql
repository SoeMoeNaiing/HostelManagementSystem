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
-- Table structure for table `Student`
--

DROP TABLE IF EXISTS `Student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Student` (
  `student_id` varchar(20) NOT NULL,
  `student_name` varchar(100) NOT NULL,
  `gender` enum('Male','Female') NOT NULL,
  `year` varchar(20) DEFAULT NULL,
  `major` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `guardian_name` varchar(100) DEFAULT NULL,
  `guardian_phone` varchar(20) DEFAULT NULL,
  `address` text,
  `nrc` varchar(50) DEFAULT NULL,
  `room_id` int DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `Student_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `Room` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Student`
--

LOCK TABLES `Student` WRITE;
/*!40000 ALTER TABLE `Student` DISABLE KEYS */;
INSERT INTO `Student` VALUES ('UCSTGO-3512','Aung Ko Ko','Male','First Year','CS','aungkoko@gmail.com','09450013001','U Ko Ko','09250013001','Taungoo','7/TGO(N)130001',1),('UCSTGO-3513','Bona Mg','Male','First Year','CT','bonamg@gmail.com','09450013002','U Mg Mg','09250013002','Yangon','12/DAGAN(N)130002',1),('UCSTGO-3514','Chit Ko','Male','Second Year','CS','chitko@gmail.com','09450013003','U Chit Maung','09250013003','Bago','7/BGO(N)130003',2),('UCSTGO-3515','Dan Aung','Male','Second Year','CT','danaung@gmail.com','09450013004','U Aung Than','09250013004','Mandalay','9/MAMANA(N)130004',2),('UCSTGO-3516','Ei Sein','Male','Third Year','CS','eisein@gmail.com','09450013005','U Sein Mg','09250013005','Pyay','7/PYA(N)130005',3),('UCSTGO-3517','Htet Hein','Male','Third Year','CT','htethein@gmail.com','09450013006','U Hein Kyaw','09250013006','Naypyidaw','8/PABANA(N)130006',3),('UCSTGO-3518','Kaung Htet','Male','Fourth Year','CS','kaunghtet@gmail.com','09450013007','U Htet Win','09250013007','Taungoo','7/TGO(N)130007',4),('UCSTGO-3519','Lwin Moe','Male','Fourth Year','CT','lwinmoe@gmail.com','09450013008','U Moe Naing','09250013008','Yangon','12/KAMAYA(N)130008',4),('UCSTGO-3520','Min Khaing','Male','First Year','CS','minkhaing@gmail.com','09450013009','U Khaing Oo','09250013009','Bago','7/BGO(N)130009',5),('UCSTGO-3521','Naing Win','Male','First Year','CT','naingwin@gmail.com','09450013010','U Win Myint','09250013010','Mandalay','9/MAMANA(N)130010',5),('UCSTGO-3522','Phyo Thu','Male','Second Year','CS','phyothu@gmail.com','09450013011','U Thu Ya','09250013011','Pyay','7/PYA(N)130011',6),('UCSTGO-3523','Sai Htet','Male','Second Year','CT','saihtet@gmail.com','09450013012','U Sai Aung','09250013012','Taungoo','7/TGO(N)130012',6),('UCSTGO-3524','Thura Mg','Male','Third Year','CS','thuramg@gmail.com','09450013013','U Mg Kyaw','09250013013','Yangon','12/YANG(N)130013',7),('UCSTGO-3525','Wai Yan','Male','Third Year','CT','waiyan@gmail.com','09450013014','U Yan Naing','09250013014','Naypyidaw','8/PABANA(N)130014',7),('UCSTGO-3526','Yair Naing','Male','Fourth Year','CS','yairnaing@gmail.com','09450013015','U Naing Lin','09250013015','Bago','7/BGO(N)130015',8),('UCSTGO-3527','Zin Min','Male','Fourth Year','CT','zinmin@gmail.com','09450013016','U Min Swe','09250013016','Mandalay','9/MAMANA(N)130016',8),('UCSTGO-3528','Aung Thin','Male','First Year','CS','aungthin@gmail.com','09450013017','U Thin Mg','09250013017','Taungoo','7/TGO(N)130017',9),('UCSTGO-3529','Bo Bo','Male','First Year','CT','bobo@gmail.com','09450013018','U Mg Bo','09250013018','Yangon','12/YANG(N)130018',9),('UCSTGO-3530','Hein Thu','Male','Second Year','CS','heinthu@gmail.com','09450013019','U Thu Rein','09250013019','Pyay','7/PYA(N)130019',10),('UCSTGO-3531','Kyaw Swar','Male','Second Year','CT','kyawswar@gmail.com','09450013020','U Swar Win','09250013020','Bago','7/BGO(N)130020',NULL),('UCSTGO-3532','Aye Nwe','Female','First Year','CS','ayenwe@gmail.com','09450013021','Daw Nwe Nwe','09250013021','Taungoo','7/TGO(N)130021',53),('UCSTGO-3533','Ei Chaw','Female','First Year','CT','eichaw@gmail.com','09450013022','Daw Chaw Chaw','09250013022','Yangon','12/YANG(N)130022',53),('UCSTGO-3534','Hnin Yu','Female','Second Year','CS','hninyu@gmail.com','09450013023','Daw Yu Yu','09250013023','Bago','7/BGO(N)130023',54),('UCSTGO-3535','Khin Thandar','Female','Second Year','CT','khinthandar@gmail.com','09450013024','Daw Thandar','09250013024','Mandalay','9/MAMANA(N)130024',NULL),('UCSTGO-3536','Lin Lin','Female','Third Year','CS','linlin@gmail.com','09450013025','Daw Thin Thin','09250013025','Pyay','7/PYA(N)130025',55),('UCSTGO-3537','May Thu','Female','Third Year','CT','maythu@gmail.com','09450013026','Daw Thu Thu','09250013026','Naypyidaw','8/PABANA(N)130026',55),('UCSTGO-3538','Nilar Win','Female','Fourth Year','CS','nilarwin@gmail.com','09450013027','Daw Win Mg','09250013027','Taungoo','7/TGO(N)130027',56),('UCSTGO-3539','Phyu Thin','Female','Fourth Year','CT','phyuthin@gmail.com','09450013028','Daw Thin Yee','09250013028','Yangon','12/KAMAYA(N)130028',56),('UCSTGO-3540','San San','Female','First Year','CS','sansan@gmail.com','09450013029','Daw San Win','09250013029','Bago','7/BGO(N)130029',57),('UCSTGO-3541','Thida Aye','Female','First Year','CT','thidaaye@gmail.com','09450013030','Daw Aye Aye','09250013030','Mandalay','9/MAMANA(N)130030',57),('UCSTGO-3542','Wai Phyo','Female','Second Year','CS','waiphyo@gmail.com','09450013031','Daw Phyo Phyo','09250013031','Pyay','7/PYA(N)130031',58),('UCSTGO-3543','Yoon Ei','Female','Second Year','CT','yoonei@gmail.com','09450013032','Daw Ei Ei','09250013032','Taungoo','7/TGO(N)130032',58),('UCSTGO-3544','Zin Mar','Female','Third Year','CS','zinmar@gmail.com','09450013033','Daw Mar Mar','09250013033','Yangon','12/YANG(N)130033',59),('UCSTGO-3545','Cho Nwe','Female','Third Year','CT','chonwe@gmail.com','09450013034','Daw Nwe Yee','09250013034','Naypyidaw','8/PABANA(N)130034',59),('UCSTGO-3546','Eaint Thet','Female','Fourth Year','CS','eaintthet@gmail.com','09450013035','Daw Thet Mon','09250013035','Bago','7/BGO(N)130035',60),('UCSTGO-3547','Hla Hla','Female','Fourth Year','CT','hlahla@gmail.com','09450013036','Daw Hla Than','09250013036','Mandalay','9/MAMANA(N)130036',60),('UCSTGO-3548','Khaing Moe','Female','First Year','CS','khaingmoe@gmail.com','09450013037','Daw Moe Mg','09250013037','Taungoo','7/TGO(N)130037',61),('UCSTGO-3549','Mya Thandar','Female','First Year','CT','myathandar@gmail.com','09450013038','Daw Thandar Mg','09250013038','Yangon','12/YANG(N)130038',61),('UCSTGO-3550','Nwe Nwe','Female','Second Year','CS','nwenwe@gmail.com','09450013039','Daw Nwe Soe','09250013039','Pyay','7/PYA(N)130039',62),('UCSTGO-3551','Pyae Pyae','Female','Second Year','CT','pyaepyae@gmail.com','09450013040','Daw Pyae Sone','09250013040','Bago','7/BGO(N)130040',62);
/*!40000 ALTER TABLE `Student` ENABLE KEYS */;
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
