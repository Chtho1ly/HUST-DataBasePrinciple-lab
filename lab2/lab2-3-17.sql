-- 17 查询隔离点中，房间数第二多的隔离点名称和房间数。
--    请用一条SQL语句实现该查询：
use covid19mon;
go

SELECT i1.location_name, i1.capacity
FROM isolation_location i1
WHERE EXISTS(
    SELECT *
    FROM (
        SELECT TOP 1 isolation_location.capacity
        FROM isolation_location
        WHERE isolation_location.capacity < (
            SELECT MAX(isolation_location.capacity)
            FROM isolation_location
            )
        ORDER BY isolation_location.capacity DESC
    ) i2
    WHERE i1.capacity = i2.capacity
);



/*  end  of  your code  */