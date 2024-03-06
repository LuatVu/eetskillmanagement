USE `skm_db`;

-- Add role ---------------------------------------------------------------------------------------
SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4';
SET @role_name = 'Administrator';
SET @role_display_name = 'Administrator';
SET @role_description = 'Administrator';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `role` (`id`, `name`, `display_name`, `description`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT @role_id, @role_name, @role_display_name, @role_description, @user_status, @user_created_date, @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `role` WHERE `name` = @role_name) LIMIT 1;

SET @role_id = 'rluser0e-d1c0-11ec-81c0-38f3ab0673e4';
SET @role_name = 'User';
SET @role_display_name = 'User';
SET @role_description = 'User';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `role` (`id`, `name`, `display_name`, `description`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT @role_id, @role_name, @role_display_name, @role_description, @user_status, @user_created_date, @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `role` WHERE `name` = @role_name) LIMIT 1;

-- Add permission_category ------------------------------------------------------------------------
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
SET @permission_category_code = 'ADMIN';
SET @permission_category_name = 'Administration';
SET @permission_category_sequence = 1;
INSERT INTO `permission_category` (`id`, `code`, `name`, `sequence`) 
SELECT * FROM (SELECT @permission_category_id, @permission_category_code, @permission_category_name, @permission_category_sequence) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission_category` WHERE `code` = @permission_category_code) LIMIT 1;

-- Add permission ---------------------------------------------------------------------------------
-- User
SET @permission_id = '13e55248-d1c5-11ec-81c0-38f3ab0673e4';
SET @permission_code = 'CREATE_USER';
SET @permission_name = 'Create user';
SET @permission_description = 'Create user';
SET @permission_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @user_created_date, @user_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = 'b01481ee-d1c8-11ec-81c0-38f3ab0673e4';
SET @permission_code = 'UPDATE_USER';
SET @permission_name = 'Update user';
SET @permission_description = 'Update user';
SET @permission_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @user_created_date, @user_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = 'deaaef2c-d1c8-11ec-81c0-38f3ab0673e4';
SET @permission_code = 'VIEW_USER';
SET @permission_name = 'View user';
SET @permission_description = 'View user';
SET @permission_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @user_created_date, @user_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = 'e8172595-d1c8-11ec-81c0-38f3ab0673e4';
SET @permission_code = 'DELETE_USER';
SET @permission_name = 'Delete user';
SET @permission_description = 'Delete user';
SET @permission_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @user_created_date, @user_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = '005be29f-d1c9-11ec-81c0-38f3ab0673e4';
SET @permission_code = 'VIEW_ALL_USER';
SET @permission_name = 'View all user';
SET @permission_description = 'View all user';
SET @permission_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @user_created_date, @user_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

-- Role
SET @permission_id = '195283d2-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'CREATE_ROLE';
SET @permission_name = 'Create role';
SET @permission_description = 'Create role';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = '21fbe010-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'UPDATE_ROLE';
SET @permission_name = 'Update role';
SET @permission_description = 'Update role';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = '27a33594-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'VIEW_ROLE';
SET @permission_name = 'View role';
SET @permission_description = 'View role';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = '2d4f8961-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'DELETE_ROLE';
SET @permission_name = 'Delete role';
SET @permission_description = 'Delete role';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = '32ff12af-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'VIEW_ALL_ROLE';
SET @permission_name = 'View all role';
SET @permission_description = 'View all role';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

SET @permission_id = 'b0d14f7a-61d5-11ed-b62d-00059a3c7a00';
SET @permission_code = 'VIEW_PERMISSION';
SET @permission_name = 'View permission';
SET @permission_description = 'View permission';
SET @permission_status = 'Active';
SET @role_created_date = NOW();
SET @role_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
SET @permission_category_id = 'admincb5-61d1-11ed-b4a3-00505695263f';
INSERT INTO `permission` (`id`, `code`, `name`, `description`, `status`, `created_date`, `created_by`, `permission_category_id`) 
SELECT * FROM (SELECT @permission_id, @permission_code, @permission_name, @permission_description, @permission_status, @role_created_date, @role_created_by, @permission_category_id) AS tmp  
WHERE NOT EXISTS (SELECT `code` FROM `permission` WHERE `code` = @permission_code) LIMIT 1;

-- Add role_permission ----------------------------------------------------------------------------
SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4';
DELETE FROM `role_permission` WHERE `role_id` = @role_id;
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) 
SELECT UUID(), @role_id, per.id FROM `permission` per;

-- Add roles for system users* --------------------------------------------------------------------
SET @user_nt_id = 'sysadmin';
SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_nt_id LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

SET @user_nt_id = 'systest';
SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_nt_id LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

-- -------------------------------------------------------------------------------------------------