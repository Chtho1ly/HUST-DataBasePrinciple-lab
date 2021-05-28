-- test

go
SELECT * FROM isolation_location_count;
go
update_isolation_location_count;
go
SELECT * FROM isolation_location_count;

go
INSERT INTO isolation_record VALUES (99,29,'2021-02-04 11:00:00.0000000','2021-02-18 11:00:00.0000000',4,1);
INSERT INTO isolation_record VALUES (100,29,'2021-02-04 11:00:00.0000000','2021-02-18 11:00:00.0000000',4,1);
INSERT INTO isolation_record VALUES (101,29,'2021-02-04 11:00:00.0000000','2021-02-18 11:00:00.0000000',4,1);
INSERT INTO isolation_record VALUES (102,29,'2021-02-04 11:00:00.0000000','2021-02-18 11:00:00.0000000',4,1);

-- ´æ´¢¹ý³Ì
go
SELECT * FROM isolation_location_count;
go
update_isolation_location_count;
go
SELECT * FROM isolation_location_count;