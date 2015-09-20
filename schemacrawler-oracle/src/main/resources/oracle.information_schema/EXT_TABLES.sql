SELECT /*+ PARALLEL(AUTO) */
  NULL AS TABLE_CATALOG,
  TABLES.OWNER AS TABLE_SCHEMA,
  TABLES.TABLE_NAME,
  DBMS_METADATA.GET_DDL('TABLE', TABLES.TABLE_NAME, TABLES.OWNER) 
    AS TABLE_DEFINITION
FROM
  ALL_TABLES TABLES
  INNER JOIN ALL_USERS USERS
    ON TABLES.OWNER = USERS.USERNAME
WHERE
  USERS.ORACLE_MAINTAINED != 'Y'
  AND NOT REGEXP_LIKE(USERS.USERNAME, '^APEX_[0-9]{6}$')
  AND NOT REGEXP_LIKE(USERS.USERNAME, '^FLOWS_[0-9]{6}$')
  AND NOT REGEXP_LIKE(USERS.USERNAME, '^FLOWS_[0-9]{5}$')
  AND TABLES.TABLE_NAME NOT LIKE 'BIN$%'
ORDER BY
  TABLE_SCHEMA,
  TABLE_NAME