-- 4) 查询充珉瑶和贾涵山的行程情况。查询结果包括：姓名、电话、到过什么地方（地名），何时到达，何时离开 。
--  列名原样列出，不必用别名。查询结果依人员编号降序排序，同一人员行程依行程开始时间顺序排列.

--    请用一条SQL语句实现该查询：
use covid19mon;
SELECT person.fullname, person.telephone, location.location_name, convert(char(19),itinerary.s_time,20) as s_time, convert(char(19),itinerary.e_time,20) as e_time
FROM person LEFT JOIN (itinerary JOIN location ON location.id = itinerary.loc_id)
    ON person.id = itinerary.p_id
WHERE person.fullname = '充珉瑶' OR person.fullname = '贾涵山'
ORDER BY person.id DESC, itinerary.s_time;


/*  end  of  your code  */