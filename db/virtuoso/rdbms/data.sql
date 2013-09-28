fk_check_input_values(0);
-- dbdump: dumping datasource "localhost:1111", username=dba
-- tablequalifier=NULL  tableowner=NULL  tablename=is given, one or more  tabletype=NULL
-- Connected to datasource "OpenLink Virtuoso", Driver v. 06.01.3127 OpenLink Virtuoso ODBC Driver.
-- get_all_tables: tablepattern="db.intlib.%",11
-- Definitions of 26 tables were read in.
-- SELECT * FROM DB.INTLIB.DPU_INSTANCE
FOREACH HEXADECIMAL BLOB INSERT INTO DB.INTLIB.DPU_INSTANCE(id,name,use_dpu_description,description,tool_tip,configuration,dpu_id) VALUES(1,'SPARQL Extractor',0,'Extracts RDF data.','',?,1);
3C6F626A6563742D73747265616D3E0A20203C637A2E63756E692E6D66662E78
72672E696E746C69622E657874726163746F722E7264662E5244464578747261
63746F72436F6E6669673E0A202020203C53504152514C5F5F656E64706F696E
743E687474703A2F2F646270656469612E6F72672F73706172716C3C2F535041
52514C5F5F656E64706F696E743E0A202020203C486F73745F5F6E616D653E3C
2F486F73745F5F6E616D653E0A202020203C50617373776F72643E3C2F506173
73776F72643E0A202020203C47726170687355726920636C6173733D226C696E
6B65642D6C697374223E0A2020202020203C737472696E673E687474703A2F2F
646270656469612E6F72672F3C2F737472696E673E0A202020203C2F47726170
68735572693E0A202020203C53504152514C5F5F71756572793E434F4E535452
554354207B266C743B687474703A2F2F646270656469612E6F72672F7265736F
757263652F5072616775652667743B203F70203F6F7D207768657265207B266C
743B687474703A2F2F646270656469612E6F72672F7265736F757263652F5072
616775652667743B203F70203F6F207D204C494D4954203130303C2F53504152
514C5F5F71756572793E0A202020203C457874726163744661696C3E66616C73
653C2F457874726163744661696C3E0A202020203C5573655374617469737469
63616C48616E646C65723E66616C73653C2F557365537461746973746963616C
48616E646C65723E0A20203C2F637A2E63756E692E6D66662E7872672E696E74
6C69622E657874726163746F722E7264662E524446457874726163746F72436F
6E6669673E0A3C2F6F626A6563742D73747265616D3E
END
FOREACH HEXADECIMAL BLOB INSERT INTO DB.INTLIB.DPU_INSTANCE(id,name,use_dpu_description,description,tool_tip,configuration,dpu_id) VALUES(2,'RDF File Loader',0,'Loads RDF data into file.','',?,5);
3C6F626A6563742D73747265616D3E0A20203C637A2E63756E692E6D66662E78
72672E696E746C69622E6C6F616465722E66696C652E46696C654C6F61646572
436F6E6669673E0A202020203C46696C65506174683E2F746D702F6462706564
69612D726573756C742E74746C3C2F46696C65506174683E0A202020203C5244
4646696C65466F726D61743E4155544F3C2F52444646696C65466F726D61743E
0A202020203C446966664E616D653E66616C73653C2F446966664E616D653E0A
20203C2F637A2E63756E692E6D66662E7872672E696E746C69622E6C6F616465
722E66696C652E46696C654C6F61646572436F6E6669673E0A3C2F6F626A6563
742D73747265616D3E
END
-- Table DB.INTLIB.DPU_INSTANCE 2 rows output.
-- SELECT * FROM DB.INTLIB.DPU_TEMPLATE
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(1,'SPARQL Extractor',0,'Extracts RDF data.',0,'RDF_extractor','RDF_extractor-1.0.0.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(2,'RDF File Extractor',0,'Extracts RDF data from a file.',0,'File_extractor','File_extractor-1.0.0.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(2,'SILK Extractor',0,'',0,'Silk_Linker_Extractor','Silk_Linker_Extractor-1.0.0.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(3,'SPARQL Transformer',0,'SPARQL Transformer.',1,'SPARQL_transformer','SPARQL_transformer-1.0.0.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(4,'SPARQL Loader',0,'Loads RDF data.',2,'RDF_loader','RDF_loader-1.0.0.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description) VALUES(5,'RDF File Loader',0,'Loads RDF data into file.',2,'File_loader','File_loader-1.0.0.jar','',NULL,1,1,'No description in manifest.');
-- Table DB.INTLIB.DPU_TEMPLATE 5 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_CONTEXT_DPU
-- Table DB.INTLIB.EXEC_CONTEXT_DPU 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_CONTEXT_PIPELINE
-- Table DB.INTLIB.EXEC_CONTEXT_PIPELINE 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_CONTEXT_PROCCONTEXT
-- Table DB.INTLIB.EXEC_CONTEXT_PROCCONTEXT 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_DATAUNIT_INFO
-- Table DB.INTLIB.EXEC_DATAUNIT_INFO 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_PIPELINE
-- Table DB.INTLIB.EXEC_PIPELINE 0 rows output.
-- Table DB.INTLIB.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM DB.INTLIB.EXEC_RECORD
-- Table DB.INTLIB.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- Table DB.INTLIB.EXEC_RECORD 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_SCHEDULE
-- Table DB.INTLIB.EXEC_SCHEDULE 0 rows output.
-- SELECT * FROM DB.INTLIB.EXEC_SCHEDULE_AFTER
-- Table DB.INTLIB.EXEC_SCHEDULE_AFTER 0 rows output.
-- SELECT * FROM DB.INTLIB.LOGGING_EVENT
-- Table DB.INTLIB.LOGGING_EVENT 0 rows output.
-- SELECT * FROM DB.INTLIB.LOGGING_EVENT_EXCEPTION
-- Table DB.INTLIB.LOGGING_EVENT_EXCEPTION 0 rows output.
-- SELECT * FROM DB.INTLIB.LOGGING_EVENT_PROPERTY
-- Table DB.INTLIB.LOGGING_EVENT_PROPERTY 0 rows output.
-- SELECT * FROM DB.INTLIB.PPL_EDGE
INSERT INTO DB.INTLIB.PPL_EDGE(id,graph_id,node_from_id,node_to_id,data_unit_name) VALUES(2,1,1,2,'output -> input;');
-- Table DB.INTLIB.PPL_EDGE 1 rows output.
-- SELECT * FROM DB.INTLIB.PPL_GRAPH
INSERT INTO DB.INTLIB.PPL_GRAPH(id,pipeline_id) VALUES(1,1);
-- Table DB.INTLIB.PPL_GRAPH 1 rows output.
-- SELECT * FROM DB.INTLIB.PPL_MODEL
INSERT INTO DB.INTLIB.PPL_MODEL(id,name,description,user_id) VALUES(1,'DBpedia','Loads 100 triples from DBpedia.',2);
-- Table DB.INTLIB.PPL_MODEL 1 rows output.
-- SELECT * FROM DB.INTLIB.PPL_NODE
INSERT INTO DB.INTLIB.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO DB.INTLIB.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);
-- Table DB.INTLIB.PPL_NODE 2 rows output.
-- SELECT * FROM DB.INTLIB.PPL_POSITION
INSERT INTO DB.INTLIB.PPL_POSITION(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO DB.INTLIB.PPL_POSITION(id,pos_x,pos_y) VALUES(2,487,132);
-- Table DB.INTLIB.PPL_POSITION 2 rows output.
-- SELECT * FROM DB.INTLIB.SCH_EMAIL
INSERT INTO DB.INTLIB.SCH_EMAIL(id,e_user,e_domain) VALUES(1,'admin','example.com');
INSERT INTO DB.INTLIB.SCH_EMAIL(id,e_user,e_domain) VALUES(2,'user','example.com');
-- Table DB.INTLIB.SCH_EMAIL 2 rows output.
-- SELECT * FROM DB.INTLIB.SCH_SCH_NOTIFICATION
-- Table DB.INTLIB.SCH_SCH_NOTIFICATION 0 rows output.
-- SELECT * FROM DB.INTLIB.SCH_SCH_NOTIFICATION_EMAIL
-- Table DB.INTLIB.SCH_SCH_NOTIFICATION_EMAIL 0 rows output.
-- SELECT * FROM DB.INTLIB.SCH_USR_NOTIFICATION
INSERT INTO DB.INTLIB.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(1,1,1,1);
INSERT INTO DB.INTLIB.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(2,2,1,1);
-- Table DB.INTLIB.SCH_USR_NOTIFICATION 2 rows output.
-- SELECT * FROM DB.INTLIB.SCH_USR_NOTIFICATION_EMAIL
INSERT INTO DB.INTLIB.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(1,1);
INSERT INTO DB.INTLIB.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(2,2);
-- Table DB.INTLIB.SCH_USR_NOTIFICATION_EMAIL 2 rows output.
-- SELECT * FROM DB.INTLIB.USR_USER
INSERT INTO DB.INTLIB.USR_USER(id,username,email_id,u_password,full_name) VALUES(1,'admin',1,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John Admin');
INSERT INTO DB.INTLIB.USR_USER(id,username,email_id,u_password,full_name) VALUES(2,'user',2,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John User');
-- Table DB.INTLIB.USR_USER 2 rows output.
-- SELECT * FROM DB.INTLIB.USR_USER_ROLE
INSERT INTO DB.INTLIB.USR_USER_ROLE(user_id,role_id) VALUES(1,0);
INSERT INTO DB.INTLIB.USR_USER_ROLE(user_id,role_id) VALUES(1,1);
INSERT INTO DB.INTLIB.USR_USER_ROLE(user_id,role_id) VALUES(2,0);
-- Table DB.INTLIB.USR_USER_ROLE 3 rows output.


fk_check_input_values(1);
