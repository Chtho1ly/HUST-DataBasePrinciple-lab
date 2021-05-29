use covid19mon;

-- 1) 查询累计人流量大于30的地点名称和累计人流量，累积人流量请用visitors作标题名称。
--    查询结果按照人流量从高到低排序，人流量相同时，依地点名称的字典顺序排序。（注意：同一人多次逛同一地点，去几次算几次）
--    请用一条SQL语句实现该查询：
SELECT location_name, COUNT(loc_id) as visitors
FROM location
    JOIN itinerary ON location.id = itinerary.loc_id
GROUP BY loc_id, location.location_name
HAVING COUNT(loc_id) > 30
ORDER BY COUNT(loc_id) DESC, location_name;


/*  end  of  your code  */