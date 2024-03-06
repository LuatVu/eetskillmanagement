USE `skm_db`;

SET @client_id = 'skm-ws';
SET @resource_ids = 'skm-ws-api';
SET @client_secret = 'Ld8ypvn#GVbTsG7e';
SET @scope = 'read,write';
SET @authorized_grant_types = 'password,authorization_code,refresh_token,implicit';
SET @web_server_redirect_uri = null;
SET @authorities = null;
SET @access_token_validity = 5400; -- 90 minutes  /*Formular : (dd hh mm ss) ~ (30 x 24 x 60 x 60)*/
SET @refresh_token_validity = 7200; -- 120 minutes
SET @additional_information = null;
SET @autoapprove = 'true';
INSERT INTO oauth_client_details(client_id, resource_ids, client_secret, scope, authorized_grant_types, 
						web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
SELECT * FROM (SELECT @client_id, @resource_ids, @client_secret, @scope, @authorized_grant_types, 
						@web_server_redirect_uri, @authorities,  @access_token_validity, @refresh_token_validity, @additional_information, @autoapprove) AS tmp  
WHERE NOT EXISTS (SELECT `client_id` FROM `oauth_client_details` WHERE `client_id` = @client_id) LIMIT 1;