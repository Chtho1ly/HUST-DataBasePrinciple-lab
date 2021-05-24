-- 10) 查询人员表去过所有地点的人员姓名。查询结果依人员姓名的字典顺序排序。
--    请用一条SQL语句实现该查询：
use covid19mon;

SELECT person.fullname
FROM person
WHERE NOT EXISTS(
    SELECT *
    FROM location
    WHERE location.id NOT IN(
        SELECT itinerary.loc_id
        FROM itinerary
        WHERE person.id = itinerary.p_id
    )
)
ORDER BY person.fullname;







/*  end  of  your code  */ 