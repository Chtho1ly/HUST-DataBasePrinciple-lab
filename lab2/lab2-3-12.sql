-- 12) 从视图isolation_location_status中查询各隔离点的剩余空房间的数目。
--    需要列出的数据项：隔离点名称，剩余房间数。其中剩余房间数为计算得出，请给该列命名为available_rooms
--    查询结果依隔离点编号排序。
--    请用一条SQL语句实现该查询：
use covid19mon;
go

SELECT location_name, (capacity-occupied) AS available_rooms
FROM isolation_location_status
WHERE capacity>occupied
ORDER BY id;



/*  end  of  your code  */ 