-- 13) 筛查发现，靳宛儿为无症状感染者。现需查询其接触者姓名名单和电话，以便通知并安排隔离。查询结题按姓名排序。
--    凡行程表中，在同一地点逗留时间与靳宛儿有交集的，均视为接触者。
--    请用一条SQL语句实现该查询：
use covid19mon;
go

SELECT p1.fullname, p1.telephone
FROM person p1, person p2
WHERE EXISTS (
    SELECT *
    FROM itinerary i1, itinerary i2
    WHERE (
        p1.id = i1.p_id AND
        p2.id = i2.p_id AND
        p1.fullname <> '靳宛儿' AND
        p2.fullname = '靳宛儿' AND
        i1.loc_id = i2.loc_id AND
        (
            i1.s_time BETWEEN i2.s_time AND i2.e_time OR
            i2.s_time BETWEEN i1.s_time AND i1.e_time
        )
    )
)
ORDER BY p1.fullname;




/*  end  of  your code  */ 