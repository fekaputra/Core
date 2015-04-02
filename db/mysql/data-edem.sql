delete from `properties`;
delete from `sch_email`;
delete from `usr_user_role`;
delete from `sch_email`;
delete from `sch_usr_notification`;
delete from `sch_usr_notification_email`;
delete from `usr_user`;
delete from `runtime_properties`;
delete from `role`;
delete from `permission`;

INSERT INTO `properties` VALUES ('UV.Core.version','001.006.000'),('UV.Plugin-DevEnv.version','001.003.000');
INSERT INTO `sch_email` VALUES (NULL,'admin@example.com'),(NULL,'user@example.com');

INSERT INTO `role` VALUES (NULL, 'Administrator');
INSERT INTO `role` VALUES (NULL,'User');
INSERT INTO `role` VALUES (NULL,'MOD-R-PO');
INSERT INTO `role` VALUES (NULL,'MOD-R-TRANSA'); 

-- INSERT INTO `permission` VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies');
INSERT INTO `permission` VALUES (NULL, 'administrator', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.delete', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.save', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.edit', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.export', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.exportScheduleRules', false);
INSERT INTO `permission` VALUES (NULL, 'pipeline.import', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.importScheduleRules', false);
INSERT INTO `permission` VALUES (NULL, 'pipeline.importUserData', false);
INSERT INTO `permission` VALUES (NULL, 'pipeline.schedule', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.read', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.runDebug', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.exportDpuData', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.exportDpuJars', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.setVisibilityAtCreate', false);
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.delete', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.stop', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.run', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.downloadAllLogs', false);
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.read', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.readDpuInputOutputData', false);
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.readEvent', false);
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.readLog', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipelineExecution.sparqlDpuInputOutputData', false);
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.create', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.delete', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.edit', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.disable', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.enable', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.read', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.execute', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'scheduleRule.setPriority', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.create', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
-- INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
-- INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.save', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
-- INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
-- INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.setVisibilityAtCreate', false);
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.delete', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.edit', true);
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.export', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.copy', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.import', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.read', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'user.management', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'user.create', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'user.edit', false);
INSERT INTO `permission` VALUES (NULL, 'user.login', false);
INSERT INTO `permission` VALUES (NULL, 'user.read', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'user.delete', true);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'role.create', false);
INSERT INTO `permission` VALUES (NULL, 'role.edit', true);
INSERT INTO `permission` VALUES (NULL, 'role.read', false);
INSERT INTO `permission` VALUES (NULL, 'role.delete', true);
INSERT INTO `permission` VALUES (NULL, 'pipeline.create', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.copy', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'deleteDebugResources', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'runtimeProperties.edit', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'userNotificationSettings.editEmailGlobal', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'userNotificationSettings.editNotificationFrequency', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));
INSERT INTO `permission` VALUES (NULL, 'userNotificationSettings.createPipelineExecutionSettings', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-PO'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='MOD-R-TRANSA'), (SELECT max(id) FROM  `permission`));

-- INSERT INTO `usr_user` VALUES (nextval('seq_usr_user'),'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20);
-- INSERT INTO `usr_extuser` VALUES (currval('seq_usr_user'), 'admin');
-- INSERT INTO `usr_user` VALUES (nextval('seq_usr_user'),'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
-- INSERT INTO `usr_extuser` VALUES (currval('seq_usr_user'), 'user');


INSERT INTO `usr_user` VALUES (NULL,'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20);
INSERT INTO `usr_extuser` VALUES ((SELECT max(id) FROM  `usr_user`), 'admin');
INSERT INTO `usr_user` VALUES (NULL,'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
INSERT INTO `usr_extuser` VALUES ((SELECT max(id) FROM  `usr_user`), 'user');

INSERT INTO `sch_usr_notification` VALUES (NULL,1,1,1),(NULL,2,1,1);
INSERT INTO `sch_usr_notification_email` VALUES (1,1),(2,2);
INSERT INTO `usr_user_role` VALUES (1,1),(1,2),(2,2);
INSERT INTO `runtime_properties` (name, value) VALUES ('backend.scheduledPipelines.limit', '5');
INSERT INTO `runtime_properties` (name, value) VALUES ('run.now.pipeline.priority', '1');
