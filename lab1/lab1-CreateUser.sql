-- 创建用户
CREATE LOGIN u1 WITH PASSWORD='1234', DEFAULT_DATABASE=SalarySystem;
CREATE LOGIN u2 WITH PASSWORD='1234', DEFAULT_DATABASE=SalarySystem;
CREATE LOGIN u3 WITH PASSWORD='1234', DEFAULT_DATABASE=SalarySystem;
-- 创建角色
CREATE USER u11 FROM login u1;
CREATE USER u12 FROM login u1;
CREATE USER u13 FROM login u1;