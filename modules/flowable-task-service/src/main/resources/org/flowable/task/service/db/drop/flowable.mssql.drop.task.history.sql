if exists (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_TASK_SCOPE') drop index ACT_HI_TASKINST.ACT_IDX_HI_TASK_SCOPE;
if exists (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_TASK_SUB_SCOPE') drop index ACT_HI_TASKINST.ACT_IDX_HI_TASK_SUB_SCOPE;
if exists (SELECT name FROM sysindexes WHERE name = 'ACT_IDX_HI_TASK_SCOPE_DEF') drop index ACT_HI_TASKINST.ACT_IDX_HI_TASK_SCOPE_DEF;

if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'ACT_HI_TASKINST') drop table ACT_HI_TASKINST;
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = 'ACT_HI_TSK_LOG') drop table ACT_HI_TSK_LOG;