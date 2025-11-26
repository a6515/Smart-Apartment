-- 修复用户表唯一索引问题
-- 允许逻辑删除的用户名可以重复使用

USE smartapartment;

-- 1. 删除原来的 username 唯一索引
ALTER TABLE sys_user DROP INDEX idx_username;

-- 2. 创建包含 deleted 字段的复合唯一索引
-- 这样 deleted=0 的用户名唯一，deleted=1 的用户名可以重复
ALTER TABLE sys_user ADD UNIQUE INDEX idx_username_deleted (username, deleted);

-- 验证索引
SHOW INDEX FROM sys_user WHERE Key_name LIKE '%username%';
