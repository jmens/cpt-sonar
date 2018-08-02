SELECT
  t1.id id,
  t1.day day,
  t1.pendingCount pendingCount,
  t1.acceptedCount,
  t1.rejectedCount,
  t2.state state
FROM
  (
    SELECT
      t1.id `id`,
      t1.day `day`,
      COALESCE (t2.pendingCount, (SELECT COUNT(1) from captain)) `pendingCount`,
      COALESCE (t2.acceptedCount, 0) `acceptedCount`,
      COALESCE (t2.rejectedCount, 0) `rejectedCount`
    FROM
      (
        SELECT `id`, `day` FROM schedule s
      ) t1
      LEFT JOIN
      (
        SELECT
          sc.schedule_id,
          COUNT(sc.state='PENDING') + (select COUNT(1) from captain) - COALESCE(COUNT(sc.state = 'ACCEPTED'), 0) - COALESCE(COUNT(sc.state = 'REJECTED'), 0) pendingCount,
          COALESCE(sum(sc.state = 'ACCEPTED'), 0) acceptedCount,
          COALESCE(sum(sc.state = 'REJECTED'), 0) rejectedCount
        FROM schedule_to_captain sc
        GROUP BY sc.schedule_id
      ) t2
        ON t1.id=t2.schedule_id



  ) t1 LEFT JOIN
  (
  SELECT s.id, COALESCE(sct.state, 'PENDING') state
  FROM schedule s
  LEFT JOIN (SELECT * FROM schedule_to_captain WHERE captain_id=?) sct
    ON sct.schedule_id = s.id
  ORDER BY s.day
  ) t2
ON t1.id = t2.id

