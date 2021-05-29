/*
(1)用create function语句创建符合以下要求的函数：
   依据人员编号计算其到达所有地点的次数(即行程表中的记录数)。
   函数名为：Count_Records。函数的参数名可以自己命名:*/
use covid19mon;
DROP FUNCTION IF EXISTS Count_Records;
GO

CREATE FUNCTION Count_Records (@t_p_id INT)
RETURNS INT
AS BEGIN
    RETURN(
        SELECT COUNT(*)
        FROM itinerary
        WHERE p_id = @t_p_id
        )
END

/*
(2) 利用创建的函数，仅用一条SQL语句查询在行程表中至少有3条行程记录的人员信息，查询结果依人员编号排序。*/
GO

SELECT *
FROM person
WHERE dbo.Count_Records(person.id) >= 3
ORDER BY person.id;