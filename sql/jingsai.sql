-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jingsai
-- ------------------------------------------------------
-- Server version	8.0.31

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

--
-- Table structure for table `award`
--

DROP TABLE IF EXISTS `award`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `award` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `competition_info_id` int NOT NULL COMMENT '外键，关联竞赛信息表',
  `competition_level` int NOT NULL COMMENT '获奖等级',
  `advisor` varchar(255) NOT NULL COMMENT '指导老师',
  `award_year` int NOT NULL COMMENT '获奖年份',
  `award_date` datetime NOT NULL COMMENT '获奖日期',
  `first_place_student_id` int DEFAULT NULL COMMENT '外键，关联学生信息表，第一成员',
  `applicant` bigint NOT NULL COMMENT '提交者ID，标识由谁提交',
  `entry_date` varchar(80) DEFAULT NULL COMMENT '填表日期，默认为当前日期',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-审核中，1-审核通过，2-审核驳回',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `competition_info_id` (`competition_info_id`),
  KEY `competition_level` (`competition_level`),
  CONSTRAINT `award_ibfk_1` FOREIGN KEY (`competition_info_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `award_ibfk_2` FOREIGN KEY (`competition_level`) REFERENCES `competition_level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生竞赛获奖表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `award`
--

LOCK TABLES `award` WRITE;
/*!40000 ALTER TABLE `award` DISABLE KEYS */;
INSERT INTO `award` VALUES (1,2,6,'a',2024,'2024-12-19 00:00:00',1,1,'2024-12-19',1,'2024-12-19 16:03:29','2025-01-01 21:11:17'),(3,1,5,'aaa',2024,'2024-12-19 00:00:00',2,1,'2024-12-19',2,'2024-12-19 21:13:37','2024-12-31 10:00:52'),(8,1,5,'1',2025,'2025-01-02 00:00:00',12,1,'2025-01-02',0,'2025-01-02 15:26:16','2025-01-02 15:26:16');
/*!40000 ALTER TABLE `award` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `competition`
--

DROP TABLE IF EXISTS `competition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `competition` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '竞赛编号',
  `name` varchar(255) NOT NULL COMMENT '竞赛名称',
  `description` text NOT NULL COMMENT '竞赛描述',
  `url` varchar(256) NOT NULL COMMENT '官网链接',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='竞赛信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT INTO `competition` VALUES (1,'蓝桥杯','蓝桥杯大赛是面向全国高校学生的编程与创新大赛','http://www.lanqiao.org','2024-12-18 00:15:48','2024-12-18 00:15:48'),(2,'ACPC','ACPC（Asia Regional Contest of ACM ICPC）是亚洲地区的 ACM 国际大学生程序设计竞赛','https://acmcontest.org','2024-12-18 00:15:48','2024-12-18 00:15:48'),(3,'数学建模竞赛','全国大学生数学建模竞赛，是中国规模最大、水平最高的数学建模赛事','http://www.mcm.edu.cn','2024-12-18 00:15:48','2024-12-18 00:15:48'),(4,'全国大学生英语竞赛','全国大学生英语竞赛（National English Contest for College Students, NECCS）','http://www.neccs.cn','2024-12-18 00:15:48','2024-12-18 00:15:48'),(6,'lan','1','http://www.lanqiao.org','2025-01-01 12:10:33','2025-01-01 12:10:33');
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `competition_level`
--

DROP TABLE IF EXISTS `competition_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `competition_level` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `competition_id` int NOT NULL COMMENT '赛事编号',
  `level` varchar(24) NOT NULL COMMENT '竞赛等级',
  `ranking` varchar(50) NOT NULL COMMENT '获奖名次',
  `credit` varchar(10) NOT NULL COMMENT '认定学分',
  `achievement` int NOT NULL DEFAULT '0' COMMENT '折算成绩',
  PRIMARY KEY (`id`),
  KEY `competition_id` (`competition_id`),
  CONSTRAINT `competition_level_ibfk_1` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='竞赛级别表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competition_level`
--

LOCK TABLES `competition_level` WRITE;
/*!40000 ALTER TABLE `competition_level` DISABLE KEYS */;
INSERT INTO `competition_level` VALUES (1,1,'省级赛','一等奖','3',90),(2,1,'省级赛','二等奖','2',80),(3,1,'省级赛','三等奖','1',70),(4,1,'全国赛','一等奖','5',100),(5,1,'全国赛','二等奖','4',85),(6,2,'区域赛','冠军','4',100),(7,2,'区域赛','亚军','3',90),(8,2,'区域赛','季军','2',80),(9,2,'世界赛','金奖','6',110),(10,2,'世界赛','银奖','5',95),(11,3,'全国赛','一等奖','6',100),(12,3,'全国赛','二等奖','5',90),(13,3,'全国赛','三等奖','4',80),(14,4,'全国赛','一等奖','5',95),(15,4,'全国赛','二等奖','4',85),(16,4,'全国赛','三等奖','3',75);
/*!40000 ALTER TABLE `competition_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) NOT NULL COMMENT '专业名称',
  `award_count` int DEFAULT '0' COMMENT '获奖人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` VALUES (1,'计算机科学与技术',20),(2,'电子工程',17),(3,'机械工程',10),(4,'土木工程',12),(5,'软件工程',18),(6,'经济学1',8),(7,'法学',5),(8,'环境科学',6),(9,'生物医学工程',4),(10,'医学',9);
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `student_id` varchar(512) NOT NULL COMMENT '学生学号',
  `name` varchar(150) NOT NULL COMMENT '学生名字',
  `grade` varchar(10) NOT NULL COMMENT '年级',
  `profession` int NOT NULL COMMENT '专业',
  `gender` tinyint DEFAULT NULL COMMENT '性别',
  `email` varchar(254) NOT NULL COMMENT '邮箱',
  `phone` varchar(128) DEFAULT NULL COMMENT '电话',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_student_id_uindex` (`student_id`),
  UNIQUE KEY `student_user_id_uindex` (`user_id`),
  KEY `profession` (`profession`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`profession`) REFERENCES `major` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,1,'2021001','张三','大二',6,1,'zhangsan@example.com','13812345678'),(2,2,'2021002','李四','大一',2,0,'lisi@example.com','13812345679'),(3,NULL,'2021003','王五','大二',3,1,'wangwu@example.com','13812345680'),(4,NULL,'2021004','赵六','大二',4,0,'zhaoliu@example.com','13812345681'),(5,NULL,'2021005','钱七','大三',5,1,'qianqi@example.com','13812345682'),(6,NULL,'2021006','孙八','大三',6,0,'sunba@example.com','13812345683'),(7,NULL,'2021007','周九','大四',7,1,'zhoujiu@example.com','13812345684'),(8,NULL,'2021008','吴十','大四',8,0,'wushi@example.com','13812345685'),(9,NULL,'2021009','郑十一','大一',9,1,'zhengshiyi@example.com','13812345686'),(10,NULL,'2021010','冯十二','大二',10,0,'fengshier@example.com','13812345687'),(12,NULL,'2021011','qy','大三',5,1,'qy@qq.com','13812345688'),(14,NULL,'2021012','张三1','大一',1,NULL,'zhangsan1@example.com','13800138000'),(15,NULL,'2021013','张三2','大二',1,NULL,'zhangsan2@example.com','13800138001');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_award`
--

DROP TABLE IF EXISTS `student_award`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_award` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` bigint NOT NULL COMMENT '外键，关联学生信息表',
  `award_id` int NOT NULL COMMENT '外键，关联学生获奖表',
  `ranking_in_team` int NOT NULL COMMENT '团队中的排名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_id` (`student_id`,`award_id`),
  KEY `award_id` (`award_id`),
  CONSTRAINT `student_award_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `student_award_ibfk_2` FOREIGN KEY (`award_id`) REFERENCES `award` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生获奖团队表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_award`
--

LOCK TABLES `student_award` WRITE;
/*!40000 ALTER TABLE `student_award` DISABLE KEYS */;
INSERT INTO `student_award` VALUES (1,1,1,2,'2025-01-01 20:49:37','2025-01-01 20:49:37'),(3,2,1,1,'2025-01-02 08:34:07','2025-01-02 08:54:15'),(5,2,3,1,'2025-01-02 09:38:42','2025-01-02 09:38:42');
/*!40000 ALTER TABLE `student_award` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(256) DEFAULT NULL COMMENT '用户昵称',
  `userAccount` varchar(256) DEFAULT NULL COMMENT '账号',
  `avatarUrl` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  `password` varchar(512) DEFAULT NULL COMMENT '密码',
  `status` int NOT NULL DEFAULT '0' COMMENT '用户状态',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `role` varchar(256) NOT NULL DEFAULT 'user' COMMENT 'user-普通用户 admin-管理员',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userAccount` (`userAccount`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admi2','admin',NULL,'$2a$10$rLwqUaVHzdZ2tCL0eX9Vhu59WPkHewuBtOrwOJEQ2IURXgSxl3vUu',0,'2024-12-19 11:15:53','2025-01-03 13:58:39',0,'admin'),(2,'测试','1111',NULL,'$2a$10$Kq69dPYUqS41TSbzcH5KEeZ4W6iB/3K3OUaMg6OKfo/KnRBChjwz.',0,'2024-12-31 21:56:39','2025-01-07 16:07:41',0,'user'),(5,'ce','11',NULL,'$2a$10$Kq69dPYUqS41TSbzcH5KEeZ4W6iB/3K3OUaMg6OKfo/KnRBChjwz.',0,'2025-01-02 14:18:11','2025-01-02 14:59:14',1,'user');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-08 13:47:52
