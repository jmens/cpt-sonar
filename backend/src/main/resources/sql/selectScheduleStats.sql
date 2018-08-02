SELECT
  s.id, s.day,
  (COUNT(sc.state='PENDING') + (select COUNT(1) from captain) - COUNT(sc.state = 'ACCEPTED') - COUNT(sc.state = 'REJECTED')) pendingCount,
  COALESCE(sum(sc.state = 'ACCEPTED'), 0) acceptedCount,
  COALESCE(sum(sc.state = 'REJECTED'), 0) rejectedCount
FROM schedule s
  LEFT JOIN schedule_to_captain sc
    ON s.id=sc.schedule_id
GROUP BY s.id, s.day, sc.state
ORDER BY day;

SELECT
  t1.id,
  COALESCE (t2.pendingCount, 0),
  COALESCE (t2.acceptedCount, 0),
  COALESCE (t2.rejectedCount, 0)
FROM
(
  SELECT id FROM schedule s
) t1
LEFT JOIN
(
  SELECT
    sc.schedule_id,
    (COUNT(sc.state='PENDING') + (select COUNT(1) from captain) - COUNT(sc.state = 'ACCEPTED') - COUNT(sc.state = 'REJECTED')) pendingCount,
    COALESCE(sum(sc.state = 'ACCEPTED'), 0) acceptedCount,
    COALESCE(sum(sc.state = 'REJECTED'), 0) rejectedCount
  FROM schedule_to_captain sc
  GROUP BY sc.schedule_id
) t2
ON t1.id=t2.schedule_id;
