 use covid19mon;

-- 2) 查询每个隔离地及该地正在进行隔离的人数，以number为隔离人数的标题.
--    查询结果依隔离人数由多到少排序。人数相同时，依隔离地点名排序。
--    请用一条SQL语句实现该查询：
SELECT location_name, COUNT(isol_loc_id) AS number
FROM isolation_location
    JOIN isolation_record ON isolation_location.id = isolation_record.isol_loc_id
WHERE state = 1
GROUP by isolation_location.id, isolation_location.location_name
ORDER BY COUNT(isol_loc_id) DESC, location_name;

/*  end  of  your code  */