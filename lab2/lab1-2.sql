 if not  exists (select * from sys.databases where name = 'covid19mon')
     create database covid19mon  COLLATE Chinese_PRC_CI_AS;
 go
 use covid19mon;
 go
 
-- 请在以下适当的空白位置填写SQL语句完成任务书的要求。空白位置不够的话，可以通过回车换行增加。
-- 表1 人员表(person)
DROP TABLE IF EXISTS isolation_location;
DROP TABLE IF EXISTS isolation_record;
DROP TABLE IF EXISTS close_contact;
DROP TABLE IF EXISTS diagnose_record;
DROP TABLE IF EXISTS itinerary;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS person;
CREATE TABLE person  (
    id INT,
    fullname CHAR(20) NOT NULL,
    telephone CHAR(11) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (id)
);
 
-- 表2 地点表(location)
CREATE TABLE location (
    id INT,
    location_name CHAR(20) NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);
 
-- 表3 行程表（itinerary）
CREATE TABLE itinerary  (
  id INT,
  p_id INT,
  loc_id INT,
  s_time DATETIME,
  e_time DATETIME,
  CONSTRAINT pk_itinerary PRIMARY KEY (id),
  CONSTRAINT fk_itinerary_pid FOREIGN KEY (p_id) REFERENCES person(id),
  CONSTRAINT fk_itinerary_lid FOREIGN KEY (loc_id) REFERENCES location(id)
);
 
-- 表4 诊断表（diagnose_record）
CREATE TABLE diagnose_record(
    id INT,
    p_id INT,
    diagnose_date DATETIME,
    result INT,
    CONSTRAINT pk_diagnose_record PRIMARY KEY (id),
    CONSTRAINT fk_diagnose_pid FOREIGN KEY (p_id) REFERENCES person(id)
)

-- 表5 密切接触者表（close_contact）
CREATE TABLE close_contact(
    id INT,
    p_id INT,
    contact_date DATETIME,
    loc_id INT,
    case_p_id INT,
    CONSTRAINT pk_close_contact PRIMARY KEY (id),
    CONSTRAINT fk_contact_pid FOREIGN KEY (p_id) REFERENCES person(id),
    CONSTRAINT fk_contact_lid FOREIGN KEY (loc_id) REFERENCES location(id),
    CONSTRAINT fk_contact_caseid FOREIGN KEY (case_p_id) REFERENCES person(id)
)

-- 表6 隔离地点表（isolation_location）
CREATE TABLE isolation_location(
    id INT,
    location_name CHAR(20),
    capacity INT,
    CONSTRAINT pk_isolation_loc PRIMARY KEY (id),
)

-- 表7 隔离表（isolation_record）
CREATE TABLE isolation_record(
    id INT,
    p_id INT,
    s_date DATETIME,
    e_date DATETIME,
    isol_loc_id INT,
    state INT,
    CONSTRAINT pk_isolation PRIMARY KEY (id),
    CONSTRAINT fk_isolation_pid FOREIGN KEY (p_id) REFERENCES person(id),
    CONSTRAINT fk_isolation_lid FOREIGN KEY (isol_loc_id) REFERENCES isolation_location(id),
)

-- 代码结束
/* *********************************************************** */