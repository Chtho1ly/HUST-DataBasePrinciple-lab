-- ´´½¨±í
use covid19mon;
DELETE FROM isolation_record WHERE (id>98 AND id<103);
DROP TABLE IF EXISTS isolation_location_count;
go

CREATE TABLE isolation_location_count (
    id INT,
    location_name CHAR(20) NOT NULL,
	number INT,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

go
DROP PROC IF EXISTS update_isolation_location_count;

go
CREATE PROC update_isolation_location_count AS
BEGIN
	DELETE FROM isolation_location_count WHERE (number > 0 OR number <=0);
	INSERT INTO isolation_location_count
	SELECT isolation_location.id, location_name, COUNT(isol_loc_id) AS number
	FROM isolation_location
		JOIN isolation_record ON isolation_location.id = isolation_record.isol_loc_id
	WHERE state = 1 AND isolation_location.id NOT IN(SELECT id FROM isolation_location_count)
	GROUP by isolation_location.id, isolation_location.location_name;
END

go
SELECT * FROM isolation_location_count;
