USE `skm_db`;

-- Predefined Users
SET @user_id = 'luk1hced-617e-11ed-b62d-00059a3c7a00';
SET @user_name = 'luk1hc';
SET @user_display_name = 'Luu Kim Bao (MS/EET12)';
SET @user_email = 'bao.luukim@vn.bosch.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT @user_id, @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_name LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

-- -----
SET @user_name = 'HYC1HC';
SET @user_display_name = 'Nguyen Thi Kieu Chinh (MS/EET11)';
SET @user_email = 'Chinh.NguyenThiKieu@vn.bosch.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT UUID(), @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_name LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

-- -----
SET @user_name = 'VLL1HC';
SET @user_display_name = 'Vo Ly Luan (MS/EET11)';
SET @user_email = 'Luan.VoLy@vn.bosch.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT UUID(), @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_name LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

-- -----
SET @user_name = 'LGH1HC';
SET @user_display_name = 'Le Huu Nghia (MS/EET11 MS/EET13)';
SET @user_email = 'Nghia.LeHuu@vn.bosch.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT UUID(), @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_name LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;

-- -----
SET @user_name = 'TYA1HC';
SET @user_display_name = 'Tran Thi Phuong Uyen (MS/EET12)';
SET @user_email = 'Uyen.TranThiPhuong@vn.bosch.com';
SET @user_type = 'Person';
SET @user_status = 'Active';
SET @user_created_date = NOW();
SET @user_created_by = 'sysadmin-617e-11ed-b62d-00059a3c7a00';
INSERT INTO `user` (`id`, `name`, `display_name`, `email`, `type`, `status`, `created_date`, `created_by`) 
SELECT * FROM (SELECT UUID(), @user_name, @user_display_name, @user_email, @user_type,  @user_status, @user_created_date,  @user_created_by) AS tmp  
WHERE NOT EXISTS (SELECT `name` FROM `user` WHERE `name` = @user_name) LIMIT 1;

SET @role_id = 'rladmin3-d1c0-11ec-81c0-38f3ab0673e4'; -- Administrator
SELECT `id` FROM `user` WHERE `name` = @user_name LIMIT 1 INTO @user_id;
DELETE FROM `user_role` WHERE `user_id` = @user_id AND `role_id` = @role_id;
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) 
SELECT UUID(), @user_id, @role_id;
