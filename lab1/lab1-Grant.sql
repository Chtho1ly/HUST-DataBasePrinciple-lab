USE covid19mon;

-- 分配权限
GRANT SELECT ON person TO user1;
GRANT SELECT, INSERT ON person TO user2;
GRANT SELECT, DELETE ON person TO user3;