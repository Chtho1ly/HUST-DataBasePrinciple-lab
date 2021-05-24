-- 5) 查询地名中带有‘店’字的地点编号和名称。查询结果按地点编号排序。
--    请用一条SQL语句实现该查询：
use covid19mon;

SELECT id, location_name
FROM location
WHERE location_name LIKE '%店%'
ORDER BY id;



/*  end  of  your code  */