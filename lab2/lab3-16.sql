-- 16) 查询2021-02-02 10:00:00到14:00:00期间，行程记录最频繁的3个人的姓名及行程记录条数。
--     记录条数命名为record_number. 记录数并列的，按姓名顺序排列。
--    请用一条SQL语句实现该查询：
use covid19mon;
go

SELECT TOP 3 person.fullname, COUNT(person.id) AS record_number
FROM person 
    JOIN itinerary
    ON person.id = itinerary.p_id
WHERE itinerary.s_time between '2021-02-02 10:00:00' AND '2021-02-02 14:00:00'
    OR '2021-02-02 10:00:00' between itinerary.s_time AND itinerary.e_time
GROUP BY person.fullname, person.id
ORDER BY COUNT(person.id) DESC;




/*  end  of  your code  */