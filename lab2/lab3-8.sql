-- 8) 用一条带exists关键字的SQL语句查询前30位有出行记录的人员姓名和电话。查询结果按照人员编号排序。
--    请用一条SQL语句实现该查询：
use covid19mon;

SELECT TOP 30 person.fullname, person.telephone
FROM person
WHERE EXISTS (
    SELECT *
    FROM itinerary
    WHERE person.id = itinerary.p_id
)
ORDER BY person.id;



/*  end  of  your code  */