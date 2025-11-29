/*
 Navicat Premium Data Transfer

 Source Server         : 我的阿里数据库
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : 47.107.139.162:3306
 Source Schema         : smart_apartment

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 29/11/2025 09:58:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告内容',
  `publisher_id` bigint NOT NULL COMMENT '发布者ID',
  `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布者姓名',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型：ALL-全体学生, BUILDING-指定楼栋',
  `target_building_id` bigint NULL DEFAULT NULL COMMENT '目标楼栋ID',
  `target_building_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标楼栋名称',
  `announcement_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型：NOTICE-通知, WARNING-警告, INFO-信息',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-草稿, 1-已发布, 2-已撤回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '1', '测试公告66666666', 1, '系统管理员', 'ALL', NULL, NULL, 'NOTICE', '2025-11-29 08:51:15', 1, '2025-11-29 08:51:14', '2025-11-29 08:51:14', 0);
INSERT INTO `announcement` VALUES (2, '冬季供暖', '11111', 1, '系统管理员', 'ALL', NULL, NULL, 'NOTICE', '2025-11-29 08:54:34', 1, '2025-11-29 08:54:33', '2025-11-29 08:54:33', 0);
INSERT INTO `announcement` VALUES (3, '中秋发月饼', '11111', 1, '系统管理员', 'ALL', NULL, NULL, 'NOTICE', '2025-11-29 09:30:34', 1, '2025-11-29 09:30:32', '2025-11-29 09:30:32', 0);
INSERT INTO `announcement` VALUES (4, '春游', '1', 1, '系统管理员', 'ALL', NULL, NULL, 'NOTICE', '2025-11-29 09:43:19', 1, '2025-11-29 09:43:18', '2025-11-29 09:43:18', 0);

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `bed_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '床位号',
  `bed_status` tinyint NULL DEFAULT 1 COMMENT '床位状态(1:空闲 2:已占用 3:维修中)',
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生姓名',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '入住时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_room_bed`(`room_id` ASC, `bed_number` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_bed_status`(`bed_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 211 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '床位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, 1, 'A', 2, 6, '2', '2025-11-26 16:27:35', 0, '2025-11-25 12:01:46', '2025-11-26 16:27:35');
INSERT INTO `bed` VALUES (2, 2, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 14:43:42');
INSERT INTO `bed` VALUES (3, 3, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 15:28:12');
INSERT INTO `bed` VALUES (4, 4, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (5, 5, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (6, 6, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (7, 7, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (8, 8, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (9, 9, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (10, 10, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (11, 18, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (12, 19, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (13, 20, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (14, 21, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (15, 22, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (16, 23, 'A', 2, 5, '1', '2025-11-25 15:23:27', 0, '2025-11-25 12:01:46', '2025-11-25 15:23:27');
INSERT INTO `bed` VALUES (17, 28, 'A', 1, NULL, '', NULL, 0, '2025-11-25 12:01:46', '2025-11-25 15:24:31');
INSERT INTO `bed` VALUES (18, 29, 'A', 1, NULL, '', '2025-11-25 14:53:31', 0, '2025-11-25 12:01:46', '2025-11-25 15:10:04');
INSERT INTO `bed` VALUES (19, 30, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (20, 31, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-26 15:00:11');
INSERT INTO `bed` VALUES (32, 1, 'B', 1, NULL, '', '2025-11-25 12:24:48', 0, '2025-11-25 12:01:46', '2025-11-25 15:10:14');
INSERT INTO `bed` VALUES (33, 2, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (34, 3, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (35, 4, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (36, 5, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (37, 6, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (38, 7, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (39, 8, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (40, 9, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (41, 10, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (42, 18, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (43, 19, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (44, 20, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (45, 21, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (46, 22, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (47, 23, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (48, 28, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (49, 29, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (50, 30, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (51, 31, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 15:37:50');
INSERT INTO `bed` VALUES (63, 1, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (64, 2, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (65, 3, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (66, 4, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (67, 5, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (68, 6, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (69, 7, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (70, 8, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (71, 9, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (72, 10, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (73, 18, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (74, 19, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (75, 20, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (76, 21, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (77, 22, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (78, 23, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (79, 28, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 14:53:30');
INSERT INTO `bed` VALUES (80, 29, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (81, 30, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (82, 31, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (94, 1, 'D', 2, 5, '1', '2025-11-26 15:00:11', 0, '2025-11-25 12:01:46', '2025-11-26 15:00:11');
INSERT INTO `bed` VALUES (95, 2, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (96, 3, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (97, 4, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (98, 5, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (99, 6, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (100, 7, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (101, 8, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (102, 9, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (103, 10, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (104, 18, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (105, 19, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (106, 20, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (107, 21, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (108, 22, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (109, 23, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (110, 28, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (111, 29, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (112, 30, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (113, 31, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (125, 11, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (126, 12, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (127, 13, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (128, 14, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (129, 15, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (130, 16, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (131, 17, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (132, 24, 'A', 1, 6, '2', '2025-11-26 16:07:37', 0, '2025-11-25 12:01:46', '2025-11-26 16:22:50');
INSERT INTO `bed` VALUES (133, 25, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (134, 26, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (135, 27, 'A', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (140, 11, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (141, 12, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (142, 13, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (143, 14, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (144, 15, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (145, 16, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (146, 17, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (147, 24, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (148, 25, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (149, 26, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (150, 27, 'B', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (155, 11, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (156, 12, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (157, 13, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (158, 14, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (159, 15, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (160, 16, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (161, 17, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (162, 24, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (163, 25, 'C', 1, NULL, '', '2025-11-25 15:24:56', 0, '2025-11-25 12:01:46', '2025-11-25 15:25:00');
INSERT INTO `bed` VALUES (164, 26, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (165, 27, 'C', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (170, 11, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (171, 12, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (172, 13, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (173, 14, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (174, 15, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (175, 16, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (176, 17, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (177, 24, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (178, 25, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (179, 26, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (180, 27, 'D', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (185, 11, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (186, 12, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (187, 13, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (188, 14, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (189, 15, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (190, 16, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (191, 17, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (192, 24, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (193, 25, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (194, 26, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (195, 27, 'E', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (200, 11, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (201, 12, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (202, 13, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (203, 14, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (204, 15, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (205, 16, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (206, 17, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (207, 24, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (208, 25, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (209, 26, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');
INSERT INTO `bed` VALUES (210, 27, 'F', 1, NULL, NULL, NULL, 0, '2025-11-25 12:01:46', '2025-11-25 12:01:46');

-- ----------------------------
-- Table structure for building
-- ----------------------------
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼宇ID',
  `building_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼宇名称',
  `building_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼宇编号',
  `building_type` tinyint NULL DEFAULT 1 COMMENT '楼宇类型(1:男生宿舍 2:女生宿舍)',
  `floors` int NULL DEFAULT 0 COMMENT '楼层数',
  `total_rooms` int NULL DEFAULT 0 COMMENT '总房间数',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼宇地址',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '宿管ID',
  `manager_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宿管姓名',
  `manager_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宿管电话',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '楼宇描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:停用 1:正常)',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_building_code`(`building_code` ASC) USING BTREE,
  INDEX `idx_manager_id`(`manager_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '楼宇表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of building
-- ----------------------------
INSERT INTO `building` VALUES (1, '梨园', 'LY-1-E', 1, 6, 120, '东校区梨园区1栋东', NULL, '张三', '13800138001', '梨园1东，男生宿舍', 1, 0, '2025-11-25 10:35:29', '2025-11-27 14:40:18');
INSERT INTO `building` VALUES (12, '梅园', 'MY-1', 1, 8, 200, '北校区梅园区1栋', NULL, '郑十', '13800138008', '梅园1栋，混合宿舍', 1, 0, '2025-11-25 10:35:29', '2025-11-27 14:38:40');
INSERT INTO `building` VALUES (15, '菊园实验楼mysql测试', 'JY-S', 1, 4, 40, '创新港菊园区', NULL, '卫十三', '13800138011', '菊园实验楼，博士生宿舍', 1, 0, '2025-11-25 10:35:29', '2025-11-27 16:21:05');

-- ----------------------------
-- Table structure for check_in_application
-- ----------------------------
DROP TABLE IF EXISTS `check_in_application`;
CREATE TABLE `check_in_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别(1:男 2:女)',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院系',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `preferred_building_id` bigint NULL DEFAULT NULL COMMENT '意向楼宇ID',
  `preferred_room_type` int NULL DEFAULT NULL COMMENT '期望房间类型：1-4人间，2-6人间',
  `application_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请原因',
  `application_status` tinyint NULL DEFAULT 1 COMMENT '申请状态(1:待审核 2:已通过 3:已拒绝 4:已入住)',
  `assigned_room_id` bigint NULL DEFAULT NULL COMMENT '分配的房间ID',
  `assigned_bed_id` bigint NULL DEFAULT NULL COMMENT '分配的床位ID',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approve_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批备注',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '实际入住时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`application_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入住申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of check_in_application
-- ----------------------------
INSERT INTO `check_in_application` VALUES (12, 5, '1', '1', '1', 1, '1', '1', '1', 1, 1, '1', 4, 1, 94, 1, '系统管理员', '2025-11-25 15:27:38', '1', '2025-11-25 15:27:40', 0, '2025-11-25 15:27:25', '2025-11-26 15:00:11');
INSERT INTO `check_in_application` VALUES (13, 6, '2', '2', '2', 1, '2', '2', '2', 1, 1, '1', 3, NULL, NULL, 1, '系统管理员', '2025-11-25 15:36:11', '1', NULL, 1, '2025-11-25 15:35:53', '2025-11-26 16:06:57');
INSERT INTO `check_in_application` VALUES (14, 6, '2', '2', '2', 1, '2', '2', '2', 15, 1, '1', 5, 1, 1, 1, '系统管理员', '2025-11-25 15:36:37', '1', '2025-11-25 15:36:39', 0, '2025-11-25 15:36:23', '2025-11-26 14:58:43');
INSERT INTO `check_in_application` VALUES (15, 6, '2', '2', '2', 1, '2', '2', '2', 12, 2, '1', 5, 24, 132, 1, '系统管理员', '2025-11-26 16:07:37', '1', '2025-11-26 16:07:46', 0, '2025-11-26 16:07:18', '2025-11-26 16:22:50');
INSERT INTO `check_in_application` VALUES (16, 6, '2', '2', '2', 1, '2', '2', '2', 1, 1, '1', 4, 1, 1, 1, '系统管理员', '2025-11-26 16:27:35', '1', '2025-11-26 16:27:46', 0, '2025-11-26 16:27:24', '2025-11-26 16:27:45');

-- ----------------------------
-- Table structure for checkout_application
-- ----------------------------
DROP TABLE IF EXISTS `checkout_application`;
CREATE TABLE `checkout_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `check_in_application_id` bigint NOT NULL COMMENT '关联的入住申请ID',
  `building_id` bigint NOT NULL COMMENT '楼宇ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `bed_id` bigint NOT NULL COMMENT '床位ID',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退宿原因',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细说明',
  `checkout_date` date NOT NULL COMMENT '预计退宿日期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待审核 2-已同意 3-已驳回 4-已撤销',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退宿申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of checkout_application
-- ----------------------------
INSERT INTO `checkout_application` VALUES (6, 6, 14, 15, 1, 1, '毕业离校', '1', '2025-11-25', 2, 1, 'admin', '1', '2025-11-26 14:58:43', 0, '2025-11-26 14:58:34', '2025-11-26 14:58:43');
INSERT INTO `checkout_application` VALUES (7, 6, 15, 12, 24, 132, '其他原因', '1', '2025-11-25', 2, 1, 'admin', '1', '2025-11-26 16:22:50', 0, '2025-11-26 16:20:33', '2025-11-26 16:22:50');

-- ----------------------------
-- Table structure for fee_record
-- ----------------------------
DROP TABLE IF EXISTS `fee_record`;
CREATE TABLE `fee_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '费用ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `building_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼宇名称',
  `room_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间号',
  `fee_type` tinyint NOT NULL COMMENT '费用类型(1:住宿费 2:水费 3:电费 4:其他)',
  `fee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '费用名称',
  `fee_amount` decimal(10, 2) NOT NULL COMMENT '费用金额',
  `billing_period` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账单周期(如:2024年1月)',
  `usage_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '使用量(水电度数)',
  `fee_status` tinyint NULL DEFAULT 1 COMMENT '缴费状态(1:未缴费 2:已缴费)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '缴费时间',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '缴费方式(1:现金 2:支付宝 3:微信 4:银行卡)',
  `due_date` date NULL DEFAULT NULL COMMENT '到期日期',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_fee_status`(`fee_status` ASC) USING BTREE,
  INDEX `idx_billing_period`(`billing_period` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '费用记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fee_record
-- ----------------------------

-- ----------------------------
-- Table structure for hygiene_inspection
-- ----------------------------
DROP TABLE IF EXISTS `hygiene_inspection`;
CREATE TABLE `hygiene_inspection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '检查ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `building_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼宇名称',
  `room_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间号',
  `inspector_id` bigint NULL DEFAULT NULL COMMENT '检查人ID',
  `inspector_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查人姓名',
  `inspection_date` date NOT NULL COMMENT '检查日期',
  `cleanliness_score` int NULL DEFAULT 0 COMMENT '清洁度评分(0-100)',
  `order_score` int NULL DEFAULT 0 COMMENT '整洁度评分(0-100)',
  `total_score` int NULL DEFAULT 0 COMMENT '总分(0-100)',
  `inspection_result` tinyint NULL DEFAULT 2 COMMENT '检查结果(1:优秀 2:良好 3:及格 4:不及格)',
  `inspection_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '检查图片(JSON数组)',
  `inspection_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '检查评语',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_inspection_date`(`inspection_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '卫生检查表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hygiene_inspection
-- ----------------------------

-- ----------------------------
-- Table structure for repair_application
-- ----------------------------
DROP TABLE IF EXISTS `repair_application`;
CREATE TABLE `repair_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报修ID',
  `student_id` bigint NOT NULL COMMENT '报修学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `building_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '楼宇名称',
  `room_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间号',
  `repair_type` tinyint NULL DEFAULT 1 COMMENT '报修类型(1:水电 2:家具 3:网络 4:其他)',
  `repair_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报修标题',
  `repair_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报修内容',
  `repair_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报修图片(JSON数组)',
  `repair_status` tinyint NULL DEFAULT 1 COMMENT '报修状态(1:待接单 2:已接单 3:维修中 4:已完成 5:已取消)',
  `urgency_level` tinyint NULL DEFAULT 2 COMMENT '紧急程度(1:紧急 2:一般 3:不急)',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '维修人员ID',
  `handler_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员姓名',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '接单时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `repair_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '维修备注',
  `satisfaction_rating` tinyint NULL DEFAULT NULL COMMENT '满意度评分(1-5)',
  `satisfaction_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '满意度评价',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_status`(`repair_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报修申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of repair_application
-- ----------------------------
INSERT INTO `repair_application` VALUES (1, 5, '1', '1', 31, NULL, NULL, 1, '水龙头漏水', '报了', NULL, 4, 2, 1, '系统管理员', '2025-11-26 13:38:54', '2025-11-26 13:39:47', '维修已完成', 5, '服务好评', 0, '2025-11-26 13:32:28', '2025-11-26 13:40:04');
INSERT INTO `repair_application` VALUES (2, 5, '1', '1', 31, NULL, NULL, 3, '网络403', '网络失效了', NULL, 4, 2, 1, '系统管理员', '2025-11-26 13:46:54', '2025-11-26 13:46:56', '维修已完成', 1, '差评', 0, '2025-11-26 13:39:30', '2025-11-26 13:47:13');
INSERT INTO `repair_application` VALUES (3, 5, '1', '1', 1, NULL, NULL, 2, '桌子坏了', '11', NULL, 4, 2, 1, '系统管理员', '2025-11-26 15:58:30', '2025-11-26 15:59:20', '维修已完成', 5, '1', 0, '2025-11-26 15:57:51', '2025-11-26 15:59:49');
INSERT INTO `repair_application` VALUES (4, 5, '1', '1', 1, NULL, NULL, 1, '电线坏了', '1', NULL, 4, 2, 1, '系统管理员', '2025-11-26 16:00:32', '2025-11-26 16:05:33', '维修已完成', NULL, NULL, 0, '2025-11-26 15:59:44', '2025-11-26 16:05:32');
INSERT INTO `repair_application` VALUES (5, 5, '1', '1', 1, NULL, NULL, 4, '丢东西了', '1', NULL, 4, 2, 1, '系统管理员', '2025-11-26 16:05:58', '2025-11-26 16:06:26', '维修已完成', NULL, NULL, 0, '2025-11-26 16:05:48', '2025-11-26 16:06:26');
INSERT INTO `repair_application` VALUES (6, 6, '2', '2', 1, NULL, NULL, 1, '新人报修', '1', NULL, 4, 2, 1, '系统管理员', '2025-11-26 16:28:28', '2025-11-26 16:28:35', '维修已完成', 5, '非常好\n', 0, '2025-11-26 16:28:17', '2025-11-26 16:28:45');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `building_id` bigint NOT NULL COMMENT '楼宇ID',
  `room_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `floor` int NOT NULL COMMENT '楼层',
  `room_type` tinyint NULL DEFAULT 1 COMMENT '房间类型(1:4人间 2:6人间 3:8人间)',
  `total_beds` int NULL DEFAULT 4 COMMENT '床位总数',
  `available_beds` int NULL DEFAULT 4 COMMENT '可用床位数',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '房间面积(平方米)',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '住宿费(元/学期)',
  `facilities` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设施配置(JSON)',
  `room_status` tinyint NULL DEFAULT 1 COMMENT '房间状态(1:空闲 2:部分入住 3:已满 4:维修中)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '房间描述',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_building_room`(`building_id` ASC, `room_number` ASC) USING BTREE,
  INDEX `idx_building_id`(`building_id` ASC) USING BTREE,
  INDEX `idx_room_status`(`room_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, '101', 1, 4, 4, 2, NULL, 0.00, NULL, 2, NULL, 0, '2025-11-25 12:01:46', '2025-11-29 08:04:47');
INSERT INTO `room` VALUES (2, 1, '102', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 14:26:16');
INSERT INTO `room` VALUES (3, 1, '103', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:26');
INSERT INTO `room` VALUES (4, 1, '104', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:26');
INSERT INTO `room` VALUES (5, 1, '201', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:27');
INSERT INTO `room` VALUES (6, 1, '202', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:27');
INSERT INTO `room` VALUES (7, 1, '203', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:30');
INSERT INTO `room` VALUES (8, 1, '204', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:29');
INSERT INTO `room` VALUES (9, 1, '301', 3, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:30');
INSERT INTO `room` VALUES (10, 1, '302', 3, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:31');
INSERT INTO `room` VALUES (11, 1, '401', 4, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:34');
INSERT INTO `room` VALUES (12, 1, '402', 4, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:39');
INSERT INTO `room` VALUES (13, 1, '403', 4, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:41');
INSERT INTO `room` VALUES (14, 1, '501', 5, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:41');
INSERT INTO `room` VALUES (15, 1, '502', 5, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:42');
INSERT INTO `room` VALUES (16, 1, '601', 6, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:43');
INSERT INTO `room` VALUES (17, 1, '602', 6, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:45');
INSERT INTO `room` VALUES (18, 12, '101', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:54');
INSERT INTO `room` VALUES (19, 12, '102', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:55');
INSERT INTO `room` VALUES (20, 12, '103', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:56');
INSERT INTO `room` VALUES (21, 12, '201', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:57');
INSERT INTO `room` VALUES (22, 12, '202', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:58');
INSERT INTO `room` VALUES (23, 12, '203', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:32:59');
INSERT INTO `room` VALUES (24, 12, '301', 3, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:08');
INSERT INTO `room` VALUES (25, 12, '302', 3, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:09');
INSERT INTO `room` VALUES (26, 12, '401', 4, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:13');
INSERT INTO `room` VALUES (27, 12, '402', 4, 6, 6, 6, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:12');
INSERT INTO `room` VALUES (28, 15, '101', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:15');
INSERT INTO `room` VALUES (29, 15, '102', 1, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:17');
INSERT INTO `room` VALUES (30, 15, '201', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:18');
INSERT INTO `room` VALUES (31, 15, '202', 2, 4, 4, 4, NULL, 0.00, NULL, 1, NULL, 0, '2025-11-25 12:01:46', '2025-11-27 10:33:20');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典类型',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典值',
  `dict_sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP',
  `duration` bigint NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID(0为顶级菜单)',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint NULL DEFAULT 1 COMMENT '菜单类型(1:目录 2:菜单 3:按钮)',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROLE_ADMIN', '拥有系统所有权限', 1, 0, 0, '2025-11-24 15:20:10', '2025-11-24 15:20:10');
INSERT INTO `sys_role` VALUES (2, '宿管员', 'ROLE_MANAGER', '管理公寓日常事务', 1, 0, 0, '2025-11-24 15:20:10', '2025-11-24 15:20:10');
INSERT INTO `sys_role` VALUES (3, '学生', 'ROLE_STUDENT', '学生用户', 1, 0, 0, '2025-11-24 15:20:10', '2025-11-24 15:20:10');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别(0:未知 1:男 2:女)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `user_type` tinyint NULL DEFAULT 3 COMMENT '用户类型(1:管理员 2:宿管 3:学生)',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院系',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username_deleted`(`username` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_student_number`(`student_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$yBpHPhzFYdGUwn8rQ2UTCeHvfuN31pIzzqkuubjofqIPXcbF4PN0y', '系统管理员', '13800138000', NULL, 0, NULL, 1, 1, NULL, NULL, NULL, NULL, 0, '2025-11-24 15:20:10', '2025-11-24 17:03:24');
INSERT INTO `sys_user` VALUES (5, '1', '$2a$10$UVe9csd1MPqz1MEC5UJD2.lWntnZ87VQ/TGXr9ZxgImTGW40znHl.', '1', '13458774108', '17452166458@qq.com', 1, NULL, 1, 3, '1', '1', '1', '1', 0, '2025-11-25 13:57:14', '2025-11-26 16:55:18');
INSERT INTO `sys_user` VALUES (6, '2', '$2a$10$5YuWQuKkQQ5f3yDEHinfROeMycS8nCbjjus4r12GQxJPOOYE/XzPG', '2', '2', '2', 1, NULL, 1, 3, '2', '2', '2', '2', 0, '2025-11-25 15:35:37', '2025-11-26 15:52:53');
INSERT INTO `sys_user` VALUES (7, '小白', '$2a$10$WqnZwr49OSmWx2mpdTZwVOv0dXAhgUoe6m1NTHzvpLgzJo4ReC4/u', '小白', '13545665410', '4512365218@qq.com', 1, NULL, 1, 3, '202202546', '知网', '计算机', '计算机333', 0, '2025-11-28 15:21:58', '2025-11-28 15:21:58');
INSERT INTO `sys_user` VALUES (8, '小黑', '$2a$10$XN6HUENjtP3bA6kMrQkm7uqEg.BKpeCf5G4v.K697WD9LG/AkkM6.', '小黑', '15679613540', '1452987056@gmail.com', 1, NULL, 1, 3, '202205456', '机械', '机械111', '机械101', 0, '2025-11-28 15:22:51', '2025-11-28 15:22:51');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-11-24 15:20:10');

-- ----------------------------
-- Table structure for system_message
-- ----------------------------
DROP TABLE IF EXISTS `system_message`;
CREATE TABLE `system_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息类型: NOTIFICATION, ANNOUNCEMENT, ROOM_UPDATE',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送者ID',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送者名称',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型: USER, BUILDING, ROLE, ALL',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID，如用户ID、楼栋ID',
  `entity_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相关实体类型: ROOM, BUILDING, REPAIR',
  `entity_id` bigint NULL DEFAULT NULL COMMENT '相关实体ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'info' COMMENT '消息状态: success, warning, error, info',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外数据（JSON格式）',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '消息过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_message
-- ----------------------------
INSERT INTO `system_message` VALUES (1, 'ANNOUNCEMENT', '中秋发月饼', '11111', 1, '系统管理员', 'ROLE', NULL, 'ANNOUNCEMENT', 3, 'success', 'STUDENT', NULL, '2025-11-29 09:30:32', '2025-11-29 09:30:32', 0);
INSERT INTO `system_message` VALUES (2, 'ANNOUNCEMENT', '春游', '1', 1, '系统管理员', 'ROLE', NULL, 'ANNOUNCEMENT', 4, 'success', 'STUDENT', NULL, '2025-11-29 09:43:18', '2025-11-29 09:43:18', 0);

-- ----------------------------
-- Table structure for transfer_application
-- ----------------------------
DROP TABLE IF EXISTS `transfer_application`;
CREATE TABLE `transfer_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `check_in_application_id` bigint NOT NULL COMMENT '关联的入住申请ID',
  `current_building_id` bigint NOT NULL COMMENT '当前楼宇ID',
  `current_room_id` bigint NOT NULL COMMENT '当前房间ID',
  `current_bed_id` bigint NOT NULL COMMENT '当前床位ID',
  `target_building_id` bigint NOT NULL COMMENT '目标楼宇ID',
  `target_room_id` bigint NOT NULL COMMENT '目标房间ID',
  `target_room_type` tinyint NOT NULL COMMENT '目标房间类型：1-4人间 2-6人间',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '换宿原因',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细说明',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待审核 2-已同意 3-已驳回 4-已撤销 5-已完成',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `assigned_bed_id` bigint NULL DEFAULT NULL COMMENT '管理员分配的床位ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '换宿申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of transfer_application
-- ----------------------------
INSERT INTO `transfer_application` VALUES (6, 5, 11, 1, 1, 1, 15, 28, 1, '室友关系不和', '1223', 5, 1, 'admin', '1', '2025-11-25 15:18:48', 17, 0, '2025-11-25 15:18:33', '2025-11-25 15:18:48');
INSERT INTO `transfer_application` VALUES (7, 5, 11, 15, 28, 17, 12, 23, 1, '室友关系不和', '1', 5, 1, 'admin', '1', '2025-11-25 15:23:27', 16, 0, '2025-11-25 15:23:18', '2025-11-25 15:23:27');
INSERT INTO `transfer_application` VALUES (8, 5, 12, 1, 3, 3, 15, 31, 1, '房间设施问题', '1', 5, 1, 'admin', '1', '2025-11-25 15:28:13', 20, 0, '2025-11-25 15:28:04', '2025-11-25 15:28:12');
INSERT INTO `transfer_application` VALUES (9, 6, 14, 15, 31, 51, 1, 1, 1, '室友关系不和', '1', 3, 1, 'admin', '1', '2025-11-25 15:37:28', NULL, 0, '2025-11-25 15:37:15', '2025-11-25 15:37:27');
INSERT INTO `transfer_application` VALUES (10, 6, 14, 15, 31, 51, 1, 1, 1, '房间设施问题', '1', 5, 1, 'admin', '1', '2025-11-25 15:37:51', 1, 0, '2025-11-25 15:37:42', '2025-11-25 15:37:50');
INSERT INTO `transfer_application` VALUES (11, 5, 12, 15, 31, 20, 1, 1, 1, '室友关系不和', '1', 5, 1, 'admin', '1', '2025-11-26 15:00:11', 94, 0, '2025-11-26 14:59:58', '2025-11-26 15:00:11');

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '读取状态: 0-未读, 1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '读取时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_read_status`(`read_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (1, 5, 1, 0, NULL, 0, '2025-11-29 09:30:32', '2025-11-29 09:30:32');
INSERT INTO `user_message` VALUES (2, 6, 1, 0, NULL, 0, '2025-11-29 09:30:32', '2025-11-29 09:30:32');
INSERT INTO `user_message` VALUES (3, 7, 1, 0, NULL, 0, '2025-11-29 09:30:32', '2025-11-29 09:30:32');
INSERT INTO `user_message` VALUES (4, 8, 1, 0, NULL, 0, '2025-11-29 09:30:32', '2025-11-29 09:30:32');
INSERT INTO `user_message` VALUES (5, 5, 2, 0, NULL, 0, '2025-11-29 09:43:18', '2025-11-29 09:43:18');
INSERT INTO `user_message` VALUES (6, 6, 2, 0, NULL, 0, '2025-11-29 09:43:18', '2025-11-29 09:43:18');
INSERT INTO `user_message` VALUES (7, 7, 2, 0, NULL, 0, '2025-11-29 09:43:18', '2025-11-29 09:43:18');
INSERT INTO `user_message` VALUES (8, 8, 2, 0, NULL, 0, '2025-11-29 09:43:18', '2025-11-29 09:43:18');

SET FOREIGN_KEY_CHECKS = 1;
