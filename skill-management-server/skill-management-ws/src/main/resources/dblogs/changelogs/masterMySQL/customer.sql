insert into customer (id,name) select uuid(), temp.customer_gb from (select distinct customer_gb from project where customer_gb <> '') as temp;
