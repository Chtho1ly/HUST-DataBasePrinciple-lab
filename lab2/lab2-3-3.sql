 use covid19mon;

-- 2) 查询行程表中人员编号大于30的同一个人的接续行程信息。输出内容包括：
-- 人员编号,姓名,重合时间,起始地点id,起始地点,结束地点id,结束地点。
-- 查询结果依人员编号排序，如同一人员有多个接续行程，再按重合时间排序。
-- 请用一条SQL语句实现该查询：
SELECT person.id, person.fullname, person.telephone, itinerary1.e_time as reclosing_time,
    itinerary1.loc_id as loc1, location1.location_name as address1,
    itinerary2.loc_id as loc2, location2.location_name as address2
FROM ((itinerary itinerary1 JOIN location location1 ON itinerary1.loc_id = location1.id)
    JOIN (itinerary itinerary2 JOIN location location2 ON itinerary2.loc_id = location2.id)
    ON itinerary1.p_id = itinerary2.p_id)
    JOIN person ON person.id = itinerary1.p_id
WHERE person.id > 30
    AND itinerary1.e_time = itinerary2.s_time
ORDER BY person.id, itinerary1.e_time;



/*  end  of  your code  */