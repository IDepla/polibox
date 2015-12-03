CREATE DATABASE  IF NOT EXISTS `polibox` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `polibox`;
-- MySQL dump 10.13  Distrib 5.5.46, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: polibox
-- ------------------------------------------------------
-- Server version	5.5.46-0ubuntu0.12.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` int(11) NOT NULL,
  `device_name` varchar(45) DEFAULT NULL,
  `file_list_digest` varchar(45) DEFAULT NULL,
  `last_ping` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `last_complete_sync` timestamp NULL DEFAULT NULL,
  `device_time_sync` timestamp NULL DEFAULT NULL,
  `device_deletable` bit(1) DEFAULT b'1' COMMENT 'viene usato per cancellare il device, io vado a mappare il web come dummy device ed è incancellabile.',
  `random_salt` varchar(45) DEFAULT NULL,
  `device_max_space` int(10) unsigned DEFAULT NULL,
  `auto_authentication_key` varchar(61) DEFAULT NULL,
  `last_auto_authentication_timestamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Device_User_idx` (`owner`),
  CONSTRAINT `fk_Device_User` FOREIGN KEY (`owner`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (29,11,'Browser',NULL,'2015-12-02 16:51:00',NULL,'2015-12-02 16:51:00',NULL,'\0','[B@2a0b6a06',0,'$2a$10$/jJw4hJfVGneaC4LkplnJ.UAJicBOZPYf71suBUv80MjD9dBa4TBO',NULL),(30,11,'polibox_client',NULL,'2015-12-02 16:52:26',NULL,NULL,NULL,'','[B@3a0e801c',0,'$2a$10$iEjaQK4KCF39q7yAgjlimOxALC2E84Jl1occBHQF8khP41j5alLOS',NULL);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `device_login`
--

DROP TABLE IF EXISTS `device_login`;
/*!50001 DROP VIEW IF EXISTS `device_login`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `device_login` (
  `user` tinyint NOT NULL,
  `device` tinyint NOT NULL,
  `password` tinyint NOT NULL,
  `last_use` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `device_to_sync_resource`
--

DROP TABLE IF EXISTS `device_to_sync_resource`;
/*!50001 DROP VIEW IF EXISTS `device_to_sync_resource`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `device_to_sync_resource` (
  `id` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `email_notification`
--

DROP TABLE IF EXISTS `email_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email_notification` (
  `user_id` int(11) NOT NULL,
  `notification_code` int(11) NOT NULL,
  `enabled` bit(1) DEFAULT b'0',
  PRIMARY KEY (`user_id`,`notification_code`),
  KEY `fk_user_has_notification_code_notification_code1_idx` (`notification_code`),
  KEY `fk_user_has_notification_code_user1_idx` (`user_id`),
  CONSTRAINT `fk_user_has_notification_code_notification_code1` FOREIGN KEY (`notification_code`) REFERENCES `notification_code` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_has_notification_code_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email_notification`
--

LOCK TABLES `email_notification` WRITE;
/*!40000 ALTER TABLE `email_notification` DISABLE KEYS */;
INSERT INTO `email_notification` VALUES (11,103,''),(11,104,''),(11,105,''),(11,106,''),(11,107,''),(11,108,''),(11,109,''),(11,110,''),(11,111,''),(11,112,''),(11,113,''),(11,120,''),(11,130,''),(11,150,''),(11,203,''),(11,204,''),(11,303,''),(11,304,''),(11,305,''),(11,313,''),(11,314,''),(11,315,''),(11,403,''),(11,404,''),(11,500,''),(11,501,''),(11,502,''),(11,503,''),(11,504,''),(11,505,'');
/*!40000 ALTER TABLE `email_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text,
  `creation_time` timestamp NULL DEFAULT NULL,
  `notification_code` int(11) NOT NULL,
  `sent` bit(1) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Notification_notification_code1_idx` (`notification_code`),
  KEY `fk_notification_user1_idx` (`user_id`),
  CONSTRAINT `fk_Notification_notification_code1` FOREIGN KEY (`notification_code`) REFERENCES `notification_code` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2010 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (2006,'account creato con email:prova@prova.com','2015-12-02 16:51:00',500,'',11),(2007,'device Browser collegato','2015-12-02 16:52:08',303,'',11),(2008,'device polibox_client creato','2015-12-02 16:52:25',313,'',11),(2009,'device polibox_client collegato','2015-12-02 16:58:15',303,'',11);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_code`
--

DROP TABLE IF EXISTS `notification_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_code` (
  `description` varchar(45) DEFAULT NULL,
  `code` int(11) NOT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_code`
--

LOCK TABLES `notification_code` WRITE;
/*!40000 ALTER TABLE `notification_code` DISABLE KEYS */;
INSERT INTO `notification_code` VALUES ('risorsa modificata',103),('file cancellata',104),('file creata',105),('risorsa ripristinata da history',106),('risorsa ripristinata da cestino',107),('conflitto di modifica ',108),('file sincronizzati',109),('directory creata',110),('directory cancellata',111),('directory rinominata',112),('file rinominato',113),('risorsa spostata in cestino',120),('condivisione modificata',130),('condivisione cancellata',150),('richiesta condivisione',203),('condivisione accettata',204),('device collegato',303),('device cancellato',304),('dispositivo scollegato',305),('device creato',313),('opzioni di notifica device modificate',314),('device rinominato',315),('spazio in esaurimento',403),('spazio nel device insufficiente',404),('account creato',500),('email modificata',501),('password modificata',502),('richiesta recupero account',503),('dettagli personali modificati',504),('opzioni notifica email modificate',505);
/*!40000 ALTER TABLE `notification_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_device`
--

DROP TABLE IF EXISTS `notification_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_device` (
  `device_id` int(11) NOT NULL,
  `notification_code` int(11) NOT NULL,
  `enabled` bit(1) DEFAULT b'1',
  PRIMARY KEY (`device_id`,`notification_code`),
  KEY `fk_device_has_notification_code_notification_code1_idx` (`notification_code`),
  KEY `fk_device_has_notification_code_device1_idx` (`device_id`),
  CONSTRAINT `fk_device_has_notification_code_device1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_device_has_notification_code_notification_code1` FOREIGN KEY (`notification_code`) REFERENCES `notification_code` (`code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_device`
--

LOCK TABLES `notification_device` WRITE;
/*!40000 ALTER TABLE `notification_device` DISABLE KEYS */;
INSERT INTO `notification_device` VALUES (29,103,''),(29,104,''),(29,105,''),(29,106,''),(29,107,''),(29,108,''),(29,109,''),(29,110,''),(29,111,''),(29,112,''),(29,113,''),(29,120,''),(29,130,''),(29,150,''),(29,203,''),(29,204,''),(29,303,''),(29,304,''),(29,305,''),(29,313,''),(29,314,''),(29,315,''),(29,403,''),(29,404,''),(29,500,''),(29,501,''),(29,502,''),(29,503,''),(29,504,''),(29,505,''),(30,103,''),(30,104,''),(30,105,''),(30,106,''),(30,107,''),(30,108,''),(30,109,''),(30,110,''),(30,111,''),(30,112,''),(30,113,''),(30,120,''),(30,130,''),(30,150,''),(30,203,''),(30,204,''),(30,303,''),(30,304,''),(30,305,''),(30,313,''),(30,314,''),(30,315,''),(30,403,''),(30,404,''),(30,500,''),(30,501,''),(30,502,''),(30,503,''),(30,504,''),(30,505,'');
/*!40000 ALTER TABLE `notification_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  `deleted` bit(1) DEFAULT b'0',
  `writing_lock` bit(1) DEFAULT NULL,
  `is_directory` bit(1) DEFAULT b'0',
  `last_modify` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=478 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_owners`
--

DROP TABLE IF EXISTS `resource_owners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_owners` (
  `resource_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `from` timestamp NULL DEFAULT NULL COMMENT '	',
  PRIMARY KEY (`resource_id`,`user_id`),
  KEY `fk_Resource_has_User_User2_idx` (`user_id`),
  KEY `fk_Resource_has_User_Resource2_idx` (`resource_id`),
  CONSTRAINT `fk_Resource_has_User_Resource2` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_Resource_has_User_User2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_owners`
--

LOCK TABLES `resource_owners` WRITE;
/*!40000 ALTER TABLE `resource_owners` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_owners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_version`
--

DROP TABLE IF EXISTS `resource_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_version` (
  `resource_id` int(11) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `digest` varchar(129) DEFAULT NULL,
  `deleted` bit(1) DEFAULT b'0',
  `chunk_number` int(10) unsigned DEFAULT NULL,
  `size` int(10) unsigned DEFAULT NULL,
  `mime` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `creation_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`resource_id`,`version`),
  KEY `fk_resource_version_user1_idx` (`user_id`),
  CONSTRAINT `fk_resource_version_resource1` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_version_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_version`
--

LOCK TABLES `resource_version` WRITE;
/*!40000 ALTER TABLE `resource_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_version_chunk`
--

DROP TABLE IF EXISTS `resource_version_chunk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_version_chunk` (
  `resource_id` int(11) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `data` mediumblob,
  `digest` varchar(129) DEFAULT NULL,
  `chunk_number` int(11) unsigned NOT NULL,
  `real_size` int(11) DEFAULT NULL,
  PRIMARY KEY (`resource_id`,`version`,`chunk_number`),
  KEY `fk_resource_version_has_resource_chunk_resource_version1_idx` (`resource_id`,`version`),
  CONSTRAINT `fk_resource_version_has_resource_chunk_resource_version1` FOREIGN KEY (`resource_id`, `version`) REFERENCES `resource_version` (`resource_id`, `version`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_version_chunk`
--

LOCK TABLES `resource_version_chunk` WRITE;
/*!40000 ALTER TABLE `resource_version_chunk` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_version_chunk` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_version_in_device`
--

DROP TABLE IF EXISTS `resource_version_in_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_version_in_device` (
  `resource_id` int(11) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  `device_id` int(11) NOT NULL,
  `last_sync` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`resource_id`,`device_id`),
  KEY `fk_resource_version_has_device_device1_idx` (`device_id`),
  KEY `fk_resource_version_has_device_resource_version1_idx` (`resource_id`,`version`),
  CONSTRAINT `fk_resource_version_has_device_device1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_version_has_device_resource_version1` FOREIGN KEY (`resource_id`, `version`) REFERENCES `resource_version` (`resource_id`, `version`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_version_in_device`
--

LOCK TABLES `resource_version_in_device` WRITE;
/*!40000 ALTER TABLE `resource_version_in_device` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_version_in_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `resource_view`
--

DROP TABLE IF EXISTS `resource_view`;
/*!50001 DROP VIEW IF EXISTS `resource_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `resource_view` (
  `resource_id` tinyint NOT NULL,
  `name` tinyint NOT NULL,
  `digest` tinyint NOT NULL,
  `size` tinyint NOT NULL,
  `mime` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `sharing`
--

DROP TABLE IF EXISTS `sharing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharing` (
  `to_user_id` int(11) NOT NULL,
  `from_user_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  `from_request_time` timestamp NULL DEFAULT NULL COMMENT '			',
  `permission` int(11) NOT NULL,
  `request_accepted` bit(1) DEFAULT b'0',
  `to_accepted_time` timestamp NULL DEFAULT NULL,
  `to_show_request` bit(1) DEFAULT b'1',
  PRIMARY KEY (`to_user_id`,`from_user_id`,`resource_id`),
  KEY `fk_sharing_sharing_mode1_idx` (`permission`),
  KEY `fk_sharing_user1_idx` (`to_user_id`),
  KEY `fk_sharing_resource_owners1_idx` (`resource_id`,`from_user_id`),
  CONSTRAINT `fk_sharing_resource_owners1` FOREIGN KEY (`resource_id`, `from_user_id`) REFERENCES `resource_owners` (`resource_id`, `user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_sharing_sharing_mode1` FOREIGN KEY (`permission`) REFERENCES `sharing_mode` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_sharing_user1` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharing`
--

LOCK TABLES `sharing` WRITE;
/*!40000 ALTER TABLE `sharing` DISABLE KEYS */;
/*!40000 ALTER TABLE `sharing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharing_mode`
--

DROP TABLE IF EXISTS `sharing_mode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharing_mode` (
  `id` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `default` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharing_mode`
--

LOCK TABLES `sharing_mode` WRITE;
/*!40000 ALTER TABLE `sharing_mode` DISABLE KEYS */;
INSERT INTO `sharing_mode` VALUES (1,'read only','\0'),(2,'read - modify - delete','');
/*!40000 ALTER TABLE `sharing_mode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(61) NOT NULL,
  `last_login` timestamp NULL DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `surname` varchar(45) DEFAULT NULL,
  `company` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `position` varchar(45) DEFAULT NULL,
  `sign_in_time` timestamp NULL DEFAULT NULL,
  `last_action_time` timestamp NULL DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `enabled` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (11,'prova@prova.com','$2a$10$4c7zJ/Cprf9c2JfxAd.59.IVQ9mW6ak/aUUzgTi7jGVrj2.WKwvmK','2015-12-02 16:52:07','i','d','','','','2015-12-02 16:51:00','2015-12-02 16:51:00','\0','');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `device_login`
--

/*!50001 DROP TABLE IF EXISTS `device_login`*/;
/*!50001 DROP VIEW IF EXISTS `device_login`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `device_login` AS select `u`.`id` AS `user`,`d`.`id` AS `device`,`d`.`auto_authentication_key` AS `password`,`d`.`last_ping` AS `last_use` from (`user` `u` join `device` `d`) where (`u`.`id` = `d`.`owner`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `device_to_sync_resource`
--

/*!50001 DROP TABLE IF EXISTS `device_to_sync_resource`*/;
/*!50001 DROP VIEW IF EXISTS `device_to_sync_resource`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `device_to_sync_resource` AS select `device`.`id` AS `id` from `device` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `resource_view`
--

/*!50001 DROP TABLE IF EXISTS `resource_view`*/;
/*!50001 DROP VIEW IF EXISTS `resource_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `resource_view` AS select `resource_version`.`resource_id` AS `resource_id`,`resource`.`name` AS `name`,`resource_version`.`digest` AS `digest`,`resource_version`.`size` AS `size`,`resource_version`.`mime` AS `mime` from (`resource_version` join `resource`) where (`resource_version`.`resource_id` = `resource`.`id`) group by `resource_version`.`resource_id`,`resource_version`.`version` having max(`resource_version`.`version`) order by `resource`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-12-02 18:36:46
