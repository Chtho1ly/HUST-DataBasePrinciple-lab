-- 6) 新发现一位确诊者，已知他在2021.2.2日20:05:40到21:25:40之间在“活动中心”逗留，
--    凡在此间在同一地点逗留过的，视为接触者，请查询接触者的姓名和电话。查询结果按姓名排序.
--    请用一条SQL语句实现该查询：
use covid19mon;

SELECT person.fullname, person.telephone
FROM (person JOIN itinerary ON person.id = itinerary.p_id)
    JOIN location ON itinerary.loc_id = location.id
WHERE (itinerary.s_time BETWEEN '2021.2.2 20:05:40' AND '2021.2.2 21:25:40' OR
    itinerary.e_time BETWEEN '2021.2.2 20:05:40' AND '2021.2.2 21:25:40')
    AND location.location_name = '活动中心'
    
ORDER BY person.fullname;


/*  end  of  your code  */