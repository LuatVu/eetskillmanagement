-- Return group id in Group Role table where input role id
select group_id from group_role where role_id = 'rol11111-a7bd-11e9-8f0a-00059a3c7a00';
-- Return Group name from Group table where input Group id
select * from `group` where id in (select group_id from group_role where role_id = 'rol11111-a7bd-11e9-8f0a-00059a3c7a00');
-- Return user id from user_group where input groupId
select * from `user` where id in (
select user_id from user_group where group_id in (select group_id from `group` where id in (select group_id from group_role where role_id = 'rol11111-a7bd-11e9-8f0a-00059a3c7a00'))
);

-- Check role of user -> input user id


SELECT * FROM liquibasedemo.permission a, role_permission b where b.role_id in (select a.id from role a, group_role b where b.group_id in 
(select a.id from `group` a, user_group b where b.user_id = 'sysadmin-9e9d-11ec-8036-00059a3c7a01')) and a.id = b.permission_id;