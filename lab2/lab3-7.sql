-- 7) 查询正在使用的隔离点名,查询结果按隔离点的编号排序。
--    请用一条SQL语句实现该查询：


use covid19mon;

SELECT location_name
FROM isolation_location
    JOIN isolation_record ON isolation_location.id = isolation_record.isol_loc_id
WHERE state = 1 
GROUP by isolation_location.location_name, isolation_location.id
HAVING COUNT(isol_loc_id) > 0
ORDER BY isolation_location.id;

/*  end  of  your code  */