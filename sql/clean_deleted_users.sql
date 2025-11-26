-- 临时方案：清理已逻辑删除的用户
-- 注意：这会永久删除数据，请谨慎使用！

USE smartapartment;

-- 查看已删除的用户
SELECT id, username, real_name, deleted, create_time 
FROM sys_user 
WHERE deleted = 1;

-- 如果需要，可以物理删除特定用户
-- DELETE FROM sys_user WHERE username = 'gjx' AND deleted = 1;

-- 或者物理删除所有已逻辑删除的用户
-- DELETE FROM sys_user WHERE deleted = 1;
