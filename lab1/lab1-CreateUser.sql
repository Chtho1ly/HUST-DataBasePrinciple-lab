-- 创建用户
CREATE LOGIN user1 WITH PASSWORD='1234', DEFAULT_DATABASE=covid19mon;
CREATE LOGIN user2 WITH PASSWORD='1234', DEFAULT_DATABASE=covid19mon;
CREATE LOGIN user3 WITH PASSWORD='1234', DEFAULT_DATABASE=covid19mon;
-- 创建角色
CREATE USER user1 FROM login user1;
CREATE USER user2 FROM login user2;
CREATE USER user3 FROM login user3;