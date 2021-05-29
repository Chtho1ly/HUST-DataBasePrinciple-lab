-- 14) 依据密切接触表的内容查询每个地点的密切接触者的数量，列出内容包括：地点名称，密接者人数。
--     人数由统计获得，列名命名为close_contact_number.查询结果依密接者人数降序排列。
--  密接者人数相同时，依地点名称排序。
--    请用一条SQL语句实现该查询：
use covid19mon;
go

SELECT location.location_name, COUNT(*) as close_contact_number
FROM location JOIN
    close_contact ON location.id = close_contact.loc_id
GROUP BY location.location_name
ORDER BY COUNT(*) DESC, location.location_name;




/*  end  of  your code  */ 