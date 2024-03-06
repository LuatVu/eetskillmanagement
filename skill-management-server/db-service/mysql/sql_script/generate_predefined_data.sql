 USE `skm_db`; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Java'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '1'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = '.Net C#'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '2'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Python'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '3'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'JavaScript'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '4'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'HTML/CSS'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '5'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Angular'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '6'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'React'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '7'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Vue'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '8'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'TypeScript'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '9'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'NodeJs'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '10'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Spring Framework'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '11'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Spring Boot'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '12'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'JPA/Hibernate'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '13'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Bash/Shell/PowerShell'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '14'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'C/C++'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '15'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Scrum'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '16'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'SQL Script'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '17'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Oracle'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '18'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'SQL Server'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '19'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'MySQL'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '20'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'NoSQL'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '21'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Project Management'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '22'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'DevOps'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '23'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Continuous Integration (CI)'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '24'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Continuous Delivery (CD)'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '25'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'Eclipse RCP'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '26'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 SET @skill_id = UUID(); 
 SET @skill_name = 'OSGi'; 
 SET @skill_description = ''; 
 SET @skill_sequence = '27'; 
 INSERT INTO `skill` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @skill_id, @skill_name, @skill_description, @skill_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `skill` WHERE `name` = @skill_name) LIMIT 1; 

 USE `skm_db`; 

 SET @level_id = UUID(); 
 SET @level_name = '0'; 
 SET @level_description = ''; 
 INSERT INTO `level` (`id`, `name`, `description`) 
 SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; 

 SET @level_id = UUID(); 
 SET @level_name = '1'; 
 SET @level_description = ''; 
 INSERT INTO `level` (`id`, `name`, `description`) 
 SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; 

 SET @level_id = UUID(); 
 SET @level_name = '2'; 
 SET @level_description = ''; 
 INSERT INTO `level` (`id`, `name`, `description`) 
 SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; 

 SET @level_id = UUID(); 
 SET @level_name = '3'; 
 SET @level_description = ''; 
 INSERT INTO `level` (`id`, `name`, `description`) 
 SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; 

 SET @level_id = UUID(); 
 SET @level_name = '4'; 
 SET @level_description = ''; 
 INSERT INTO `level` (`id`, `name`, `description`) 
 SELECT * FROM (SELECT @level_id, @level_name, @level_description) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `level` WHERE `name` = @level_name) LIMIT 1; 

 USE `skm_db`; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Developer'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '1'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'QA'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '2'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Technical Lead'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '3'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Solution Architect'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '4'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Project Manager'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '5'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Business Analyst'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '6'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Scrum Master'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '7'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

 SET @project_role_id = UUID(); 
 SET @project_role_name = 'Product Owner'; 
 SET @project_role_description = ''; 
 SET @project_role_sequence = '8'; 
 INSERT INTO `project_role` (`id`, `name`, `description`, `sequence`) 
 SELECT * FROM (SELECT @project_role_id, @project_role_name, @project_role_description, @project_role_sequence) AS tmp 
 WHERE NOT EXISTS (SELECT `name` FROM `project_role` WHERE `name` = @project_role_name) LIMIT 1; 

