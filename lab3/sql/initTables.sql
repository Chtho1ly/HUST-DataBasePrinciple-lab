IF NOT EXISTS (SELECT * FROM sys.databases where name = 'SalarySystem')
	create database SalarySystem;
go
use SalarySystem;
DROP TRIGGER IF EXISTS tg_attendance_attendance_month;
DROP TRIGGER IF EXISTS tg_extra_allowance_month;
DROP VIEW IF EXISTS company_statistics;
DROP VIEW IF EXISTS department_statistics;
DROP TABLE IF EXISTS award_log;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS attendance_month_log;
DROP TABLE IF EXISTS extra;
DROP TABLE IF EXISTS allowance_month_log;
DROP TABLE IF EXISTS extra_info;
DROP TABLE IF EXISTS salary_log;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS career_info;

-- 表1 工种表
CREATE TABLE career_info(
	c_id CHAR(4) NOT NULL,
    name VARCHAR(20) NOT NULL,
	base FLOAT NOT NULL,
	authority int NOT NULL,
	CONSTRAINT pk_career_info PRIMARY KEY (c_id)
);

-- 表2 部门表
CREATE TABLE department(
	d_id CHAR(4) NOT NULL,
    name VARCHAR(20) NOT NULL,
	CONSTRAINT pk_department PRIMARY KEY (d_id)
);

-- 表3 员工表
CREATE TABLE staff  (
    s_id CHAR(4) NOT NULL,
    name VARCHAR(20) NOT NULL,
	username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
	c_id CHAR(4) NOT NULL,
	d_id CHAR(4) NOT NULL,
	telephone CHAR(11) NOT NULL,
	age INT,
    CONSTRAINT pk_staff PRIMARY KEY (s_id),
	CONSTRAINT fk_staff_cid FOREIGN KEY (c_id) REFERENCES career_info ON UPDATE CASCADE,
	CONSTRAINT fk_staff_did FOREIGN KEY (d_id) REFERENCES department ON UPDATE CASCADE
);

-- 表9 工资记录表
CREATE TABLE salary_log(
	sal_id CHAR(10) NOT NULL,
	s_id CHAR(4) NOT NULL,
	year INT NOT NULL,
	month INT NOT NULL,
	-- alm_id CHAR(10) NOT NULL,
	-- atm_id CHAR(10) NOT NULL,
	base FLOAT NOT NULL,
	allowance FLOAT NOT NULL,
	attendance FLOAT NOT NULL,
	sum FLOAT NOT NULL,
    CONSTRAINT pk_salary_log PRIMARY KEY (sal_id),
	CONSTRAINT fk_salary_log_sid FOREIGN KEY (s_id) REFERENCES staff ON DELETE CASCADE ON UPDATE CASCADE,
);

-- 表4 加班类型表
CREATE TABLE extra_info(
	ei_id INT NOT NULL,
    name VARCHAR(20) NOT NULL,
	allowance FLOAT NOT NULL,
	CONSTRAINT pk_extra_info PRIMARY KEY (ei_id)
);

-- 表5 月津贴统计表
CREATE TABLE allowance_month_log(
	am_id CHAR(10) NOT NULL,
	allowance FLOAT,
	CONSTRAINT pk_allowance_month_log PRIMARY KEY (am_id),
	--CONSTRAINT fk_allowance_statistic_salary_log_id FOREIGN KEY (am_id) REFERENCES salary_log ON DELETE CASCADE
);

-- 表6 加班记录表
CREATE TABLE extra(
	e_id CHAR(12) NOT NULL,
	s_id CHAR(4) NOT NULL,
	year INT NOT NULL,
	month INT NOT NULL,
	date INT NOT NULL,
	ei_id INT NOT NULL,
	length INT NOT NULL,
	am_id CHAR(10) NOT NULL,
    CONSTRAINT pk_extra PRIMARY KEY (e_id),
	CONSTRAINT fk_extra_sid FOREIGN KEY (s_id) REFERENCES staff ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_extra_rid FOREIGN KEY (ei_id) REFERENCES extra_info ON DELETE CASCADE ON UPDATE CASCADE,
	--CONSTRAINT fk_extra_statistic_id FOREIGN KEY (am_id) REFERENCES allowance_month_log ON DELETE CASCADE
);

-- 表7 月考勤统计表
CREATE TABLE attendance_month_log(
	am_id CHAR(10) NOT NULL,
	offset FLOAT NOT NULL,
	CONSTRAINT pk_attendance_month_log PRIMARY KEY (am_id),
	--CONSTRAINT fk_attendance_statistic_salary_log_id FOREIGN KEY (am_id) REFERENCES salary_log ON DELETE CASCADE
);

-- 表8 考勤记录表
CREATE TABLE attendance(
	a_id CHAR(12) NOT NULL,
	s_id CHAR(4) NOT NULL,
	year INT NOT NULL,
	month INT NOT NULL,
	date INT NOT NULL,
	type INT NOT NULL,
	am_id CHAR(10) NOT NULL,
    CONSTRAINT pk_attendance PRIMARY KEY (a_id),
	CONSTRAINT fk_attendance_sid FOREIGN KEY (s_id) REFERENCES staff ON DELETE CASCADE ON UPDATE CASCADE,
	--CONSTRAINT fk_attendance_statistic_id FOREIGN KEY (am_id) REFERENCES attendance_month_log ON DELETE CASCADE ON UPDATE CASCADE
);
-- 表10 年终奖记录表
CREATE TABLE award_log(
	a_id CHAR(8) NOT NULL,
	s_id CHAR(4) NOT NULL,
	year INT NOT NULL,
	award FLOAT NOT NULL,
    CONSTRAINT pk_award_log PRIMARY KEY (a_id),
	CONSTRAINT fk_award_log_sid FOREIGN KEY (s_id) REFERENCES staff ON DELETE CASCADE ON UPDATE CASCADE
);

go
-- 视图1 部门工资统计
CREATE VIEW department_statistics
AS
	SELECT department.d_id, department.name, salary_log.year, salary_log.month, SUM(sum) AS TotalSalary
	FROM (salary_log join staff
		on salary_log.s_id = staff.s_id) join department
		on staff.d_id = department.d_id
	GROUP BY department.d_id, department.name, salary_log.year, salary_log.month
go
-- 视图2 公司工资统计
CREATE VIEW company_statistics
AS
	SELECT salary_log.year, salary_log.month, SUM(sum) AS TotalSalary
	FROM salary_log
	GROUP BY salary_log.year, salary_log.month
go
-- 触发器1 添加‘加班记录’后更新‘月津贴统计’
CREATE TRIGGER tg_extra_allowance_month
ON extra
	FOR INSERT, DELETE, UPDATE
AS DECLARE @s_id CHAR(4), @year INT, @month INT, @am_id CHAR(10), @allowance INT
	IF EXISTS (SELECT * FROM inserted)
		SELECT @s_id = s_id, @year = year, @month = month, @am_id = am_id FROM inserted
	ELSE IF EXISTS (SELECT * FROM deleted)
		SELECT @s_id = s_id, @year = year, @month = month, @am_id = am_id FROM deleted
	SET @allowance = (
		SELECT SUM(extra.length * extra_info.allowance)
		FROM extra, extra_info
		WHERE extra.s_id = @s_id AND extra.year = @year AND extra.month = @month AND extra.ei_id = extra_info.ei_id
	)
	IF EXISTS (
		SELECT *
		FROM extra, extra_info
		WHERE extra.s_id = @s_id AND extra.year = @year AND extra.month = @month AND extra.ei_id = extra_info.ei_id
	)
		UPDATE allowance_month_log SET allowance = (
			SELECT SUM(extra.length * extra_info.allowance)
			FROM extra, extra_info
			WHERE extra.s_id = @s_id AND extra.year = @year AND extra.month = @month AND extra.ei_id = extra_info.ei_id
		)
		WHERE allowance_month_log.am_id = @am_id
	ELSE
		UPDATE allowance_month_log SET allowance = 0 WHERE allowance_month_log.am_id = @am_id
;

go
-- 触发器2 添加‘考勤记录’后更新‘月考勤统计’
CREATE TRIGGER tg_attendance_attendance_month
ON attendance
	FOR INSERT, DELETE, UPDATE
AS DECLARE @s_id CHAR(4), @year INT, @month INT, @am_id CHAR(10), @count_1 INT, @count_2 INT, @count_3 INT, @cursor CURSOR
	IF EXISTS (SELECT * FROM inserted)
		SET @cursor = CURSOR FOR SELECT s_id, year, month, am_id FROM inserted
	ELSE IF EXISTS (SELECT * FROM deleted)
		SET @cursor = CURSOR FOR SELECT s_id, year, month, am_id FROM deleted
	OPEN @cursor
	FETCH NEXT FROM @cursor INTO @s_id, @year, @month, @am_id
	WHILE @@FETCH_STATUS = 0
	BEGIN
		DROP TABLE IF EXISTS attendance_count
		CREATE TABLE attendance_count
		(
			type INT NOT NULL,
			type_count INT NOT NULL,
			CONSTRAINT pk_type_count PRIMARY KEY (type)
		)
		INSERT INTO attendance_count
			SELECT type, COUNT(a_id) AS type_count
			FROM attendance
			WHERE attendance.s_id = @s_id AND attendance.year = @year AND attendance.month = @month
			GROUP BY type
		IF EXISTS (SELECT * FROM attendance_count WHERE type = 1)
			SELECT @count_1 = type_count FROM attendance_count WHERE type = 1
		ELSE
			SET @count_1 = 0
		IF EXISTS (SELECT * FROM attendance_count WHERE type = 2)
			SELECT @count_2 = type_count FROM attendance_count WHERE type = 2
		ELSE
			SET @count_2 = 0
		IF EXISTS (SELECT * FROM attendance_count WHERE type = 3)
			SELECT @count_3 = type_count FROM attendance_count WHERE type = 3
		ELSE
			SET @count_3 = 0
		if(@count_1 + @count_2 + @count_3 = 0)
			UPDATE attendance_month_log 
				SET offset = 1
				WHERE attendance_month_log.am_id = @am_id
		ELSE
			UPDATE attendance_month_log 
				SET offset = (@count_1*1.1 - @count_2 - @count_3*7) / (@count_1 + @count_2 + @count_3)
				WHERE attendance_month_log.am_id = @am_id
		DROP TABLE IF EXISTS attendance_count
		FETCH NEXT FROM @cursor INTO @s_id, @year, @month, @am_id
	END
	CLOSE @cursor;
    DEALLOCATE @cursor;
;

go
-- 触发器3 添加‘工资记录’后添加‘月考勤统计’和'月津贴记录'
CREATE TRIGGER tg_salary_month_insert
ON salary_log
	FOR INSERT
AS DECLARE @sal_id CHAR(10)
	SELECT @sal_id = sal_id FROM inserted
	INSERT INTO attendance_month_log SELECT sal_id, 1 FROM inserted
	INSERT INTO allowance_month_log SELECT sal_id, 0 FROM inserted
;

go
-- 触发器4 删除‘工资记录’后删除‘月考勤统计’和'月津贴记录'
CREATE TRIGGER tg_salary_month_delete
ON salary_log
	FOR DELETE
AS DECLARE @sal_id CHAR(10)
	SELECT @sal_id = sal_id FROM deleted
	DELETE FROM attendance_month_log WHERE (am_id in (SELECT sal_id FROM deleted))
	DELETE FROM allowance_month_log WHERE (am_id in (SELECT sal_id FROM deleted))
;

go
-- 触发器5 添加‘工资记录’后添加‘年终奖记录'
CREATE TRIGGER tg_salary_award_insert
ON salary_log
	FOR INSERT
AS
INSERT INTO award_log
	SELECT CAST(inserted.year as CHAR(4)) + inserted.s_id, inserted.s_id, inserted.year, 0
	FROM inserted
	WHERE NOT EXISTS (SELECT * FROM award_log WHERE inserted.s_id = award_log.s_id AND inserted.year = award_log.year)
;

go
-- 触发器6 修改‘月津贴记录’后更新‘工资记录'
CREATE TRIGGER tg_allowance_month_salary_update
ON allowance_month_log
	FOR UPDATE
AS
	UPDATE salary_log
	SET allowance = (
		SELECT allowance FROM inserted WHERE sal_id = am_id
	)
	WHERE sal_id in (SELECT am_id FROM inserted)
	UPDATE salary_log
	SET sum = base * attendance + allowance
	WHERE sal_id in (SELECT am_id FROM inserted)
;

go
-- 触发器7 修改‘月考勤记录’后更新‘工资记录'
CREATE TRIGGER tg_attendance_month_salary_update
ON attendance_month_log
	FOR UPDATE
AS
	UPDATE salary_log
	SET attendance = (
		SELECT offset FROM inserted WHERE sal_id = am_id
	)
	WHERE sal_id in (SELECT am_id FROM inserted)
	UPDATE salary_log
	SET sum = base * attendance + allowance
	WHERE sal_id in (SELECT am_id FROM inserted)
;

go
-- 触发器8 修改‘工资记录'后更新'年终奖记录'
CREATE TRIGGER tg_salary_award_update
ON salary_log
	FOR UPDATE, INSERT, DELETE
AS
	UPDATE award_log
	SET award = (
		SELECT sum(sum)/12 FROM salary_log WHERE award_log.year = salary_log.year AND award_log.s_id = salary_log.s_id
	)
	WHERE award_log.year in (SELECT year FROM inserted) AND award_log.s_id in (SELECT s_id FROM inserted)
;

go
-- 初始化工种表
INSERT INTO career_info VALUES('0001', '经理', 10000, 2);
INSERT INTO career_info VALUES('0002', '设计师', 8000, 1);
INSERT INTO career_info VALUES('0003', '顾问', 8000, 1);
INSERT INTO career_info VALUES('0004', '技工', 6000, 1);
INSERT INTO career_info VALUES('0005', '业务员', 6000, 1);

-- 初始化部门表
INSERT INTO department VALUES('0001', '管理部');
INSERT INTO department VALUES('0002', '设计部');
INSERT INTO department VALUES('0003', '生产部');
INSERT INTO department VALUES('0004', '业务部');

-- 初始化员工表
INSERT INTO staff VALUES('0000', 'Mr.Zero', '1', '1', '0001', '0001', '14725836900', 17);
INSERT INTO staff VALUES('0011', 'Designer Alpha', '2', '2', '0002', '0002', '14725836900', 17);
INSERT INTO staff VALUES('0012', 'Consultant Beta', 'b', '1111', '0002', '0003', '14725836900', 17);

-- 初始化加班类型表
INSERT INTO extra_info VALUES(1, '工作日加班', 50);
INSERT INTO extra_info VALUES(2, '节假日加班', 100);

-- 初始化工资表
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021050000', '0000', 2021, 5, 10000, 0, 1, 10000);
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021050011', '0011', 2021, 5, 8000, 0, 1, 8000);
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021050012', '0012', 2021, 5, 8000, 0, 1, 8000);
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021060000', '0000', 2021, 6, 10000, 0, 1, 10000);
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021060011', '0011', 2021, 6, 8000, 0, 1, 8000);
INSERT INTO salary_log (sal_id, s_id, year, month, base, allowance, attendance, sum) VALUES('2021060012', '0012', 2021, 6, 8000, 0, 1, 8000);

-- 初始化考勤表
INSERT INTO attendance VALUES('202105010000', '0000', 2021, 5, 1, 1, '2021050000');
INSERT INTO attendance VALUES('202105020000', '0000', 2021, 5, 2, 1, '2021050000');
INSERT INTO attendance VALUES('202105030000', '0000', 2021, 5, 3, 2, '2021050000');
INSERT INTO attendance VALUES('202105010011', '0011', 2021, 5, 1, 1, '2021050011');
INSERT INTO attendance VALUES('202105020011', '0011', 2021, 5, 2, 1, '2021050011');
INSERT INTO attendance VALUES('202105030011', '0011', 2021, 5, 3, 2, '2021050011');
INSERT INTO attendance VALUES('202105010012', '0012', 2021, 5, 1, 1, '2021050012');
INSERT INTO attendance VALUES('202105020012', '0012', 2021, 5, 2, 1, '2021050012');
INSERT INTO attendance VALUES('202105030012', '0012', 2021, 5, 3, 3, '2021050012');

-- 初始化加班表
INSERT INTO extra VALUES('202105010000', '0000', 2021, 5, 1, 1, 5, '2021050000');
INSERT INTO extra VALUES('202105010011', '0011', 2021, 5, 1, 2, 10, '2021050011');
INSERT INTO extra VALUES('202105010012', '0012', 2021, 5, 1, 2, 20, '2021050012');
INSERT INTO extra VALUES('202105020000', '0000', 2021, 5, 1, 1, 5, '2021050000');
INSERT INTO extra VALUES('202105020011', '0011', 2021, 5, 1, 2, 10, '2021050011');
INSERT INTO extra VALUES('202105020012', '0012', 2021, 5, 1, 2, 20, '2021050012');
