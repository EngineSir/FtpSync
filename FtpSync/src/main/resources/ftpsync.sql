/*
Navicat MySQL Data Transfer

Source Server         : Engine
Source Server Version : 50640
Source Host           : localhost:3306
Source Database       : ftpsync

Target Server Type    : MYSQL
Target Server Version : 50640
File Encoding         : 65001

Date: 2019-10-10 10:27:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ftplinkinfo
-- ----------------------------
DROP TABLE IF EXISTS `ftplinkinfo`;
CREATE TABLE `ftplinkinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `remoteIp` varchar(30) DEFAULT NULL,
  `nativeIp` varchar(30) DEFAULT NULL,
  `time` varchar(40) DEFAULT NULL,
  `creater` varchar(20) DEFAULT NULL,
  `nativePort` varchar(6) DEFAULT NULL,
  `remotePort` varchar(6) DEFAULT NULL,
  `nativeUsername` varchar(20) DEFAULT NULL,
  `remoteUsername` varchar(20) DEFAULT NULL,
  `nativePassword` varchar(40) DEFAULT NULL,
  `remotePassword` varchar(40) DEFAULT NULL,
  `syncTime` varchar(10) DEFAULT NULL,
  `task` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ftplinkinfo
-- ----------------------------
INSERT INTO `ftplinkinfo` VALUES ('25', '192.168.119.1', '192.168.119.1', '2019-10-10 10:27:10', '吕冠森', '21', '22', 'Engine', 'Engine', 'lvguanshao1314', 'lvguanshao1314', '100', '本地测试任务1');
INSERT INTO `ftplinkinfo` VALUES ('31', '192.168.119.1', '192.168.119.1', '2019-10-08 18:25:47', '吕冠森', '21', '22', 'Engine', 'Engine', 'lvguanshao1314', 'lvguanshao1314', '600', '本地测试任务');

-- ----------------------------
-- Table structure for syncdate
-- ----------------------------
DROP TABLE IF EXISTS `syncdate`;
CREATE TABLE `syncdate` (
  `id` int(11) NOT NULL DEFAULT '0',
  `path` varchar(100) NOT NULL DEFAULT '',
  `time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of syncdate
-- ----------------------------
