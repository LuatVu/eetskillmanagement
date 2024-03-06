USE `skm_db`;

-- setting
SET @setting_key = 'skm_ws.version';
SET @setting_value = '1.0.0';
INSERT INTO `setting` (`key`, `value`) 
SELECT * FROM (SELECT @setting_key, @setting_value) AS tmp  
WHERE NOT EXISTS (SELECT `key` FROM `setting` WHERE `key` = @setting_key) LIMIT 1;

-- Add system users* ---
SET @user_id = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @user_name = 'sysadmin';
SET @user_display_name = 'System Administrator';
SET @user_email = 'sysadmin@skm.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT @user_id, @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @user_id = 'systeste-617e-11ed-b62d-00059a3c7a00';
SET @user_name = 'systest';
SET @user_display_name = 'System Test';
SET @user_email = 'systest@skm.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT @user_id, @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;


