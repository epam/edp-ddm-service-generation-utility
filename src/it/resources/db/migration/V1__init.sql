--- extensions:
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--- ddm_liquibase_metadata:
CREATE TABLE ddm_liquibase_metadata
(
	metadata_id int NOT NULL,
	change_type text NOT NULL,
	change_name text NOT NULL,
	attribute_name text NOT NULL,
	attribute_value text NOT NULL
);

insert into ddm_liquibase_metadata (metadata_id, change_type, change_name,
        attribute_name, attribute_value)
values (1, 'searchCondition', 'pd_processing_consent_2',
        'equalColumn', 'person_full_name'
        ),
        (2, 'searchCondition', 'pd_processing_consent_2',
        'startsWithColumn', 'person_full_name2'
        ),
        (3, 'searchCondition', 'pd_processing_consent_2',
        'containsColumn', 'person_full_name3'
        ),
        (4, 'searchCondition', 'pd_processing_consent_2',
        'equalColumn', 'person_pass_number'
        ),
        (5, 'searchCondition', 'pd_processing_consent_2',
        'limit', '1'
        ),
        (6, 'anotherType', 'anotherName',
        'equalColumn', 'person_pass_number'
        ),
        (7, 'anotherType', 'withoutLimit',
        'equalColumn', 'person_pass_number'
        ),
        (8, 'searchCondition', 'pd_processing_consent_2',
        'startsWithArrayColumn', 'person_full_name4'
        ),
        (9, 'searchCondition', 'pd_processing_consent_2',
        'logicOperator', '{"operations":[{"tableName":"pd_processing_consent","logicOperators":[{"type": "or","columns": ["person_pass_number"],"logicOperators":[{"type":"and","columns":["person_full_name3","person_full_name4"],"logicOperators":[]}]}]}]}');

--changeset platform:table-ddm_rls_metadata
CREATE TABLE ddm_rls_metadata
(
    rls_id          INTEGER NOT NULL,
    name            TEXT                                     NOT NULL,
    type            TEXT                                     NOT NULL,
    jwt_attribute   TEXT                                     NOT NULL,
    check_column    TEXT                                     NOT NULL,
    check_table     TEXT                                     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()   NOT NULL,
    CONSTRAINT pk_ddm_rls_metadata PRIMARY KEY (rls_id)
);

insert into ddm_rls_metadata (rls_id, name, type, jwt_attribute, check_table, check_column)
values (1, 'name1', 'read', 'katottg', 'table1', 'col1'),
       (2, 'name2', 'read', 'katottg', 'table2', 'col2'),
       (3, 'name3', 'write', 'katottg', 'table3', 'col3');

--- ddm_role:
CREATE TYPE type_perm AS ENUM ('S','I','U','D');
CREATE TYPE type_object AS ENUM ('table','search_condition');
CREATE TABLE ddm_role_permission
(
	permission_id int NOT NULL,
	object_name text NOT NULL,
	column_name text,
	operation type_perm NOT NULL,
	role_name text NOT NULL,
	object_type TYPE_OBJECT NOT NULL DEFAULT 'table'
);

insert into ddm_role_permission (permission_id, object_name, column_name,
        operation, role_name)
values (1, 'pd_processing_consent', 'person_full_name',
        'S', 'citizen'
        ),
        (2, 'pd_processing_consent', 'person_pass_number',
        'S', 'citizen'
        ),
        (3, 'pd_processing_consent', null,
        'S', 'officer'
        ),
        (4, 'pd_processing_consent', 'person_pass_number',
        'U', 'officer'
        ),
        (5, 'another_table', 'another_column',
        'S', 'citizen'
        );

insert into ddm_role_permission (permission_id, object_name, column_name,
        operation, role_name, object_type)
values (6, 'pb_table_read_role', null,
        'S', 'officer', 'table'
        ),
        (7, 'pb_table_read_role', null,
        'S', 'op-regression', 'search_condition'
        ),
        (8, 'pb_type_read', null,
        'S', 'officer', 'table'
        ),
        (9, 'pb_type_read', null,
        'S', 'op-regression', 'search_condition'
        ),
        (10, 'pb_tables_read_roles_1', null,
        'S', 'officer', 'table'
        ),
        (11, 'pb_tables_read_roles_2', null,
        'S', 'op-regression', 'table'
        ),
        (12, 'pb_tables_read_roles_1', null,
        'S', 'head-officer', 'search_condition'
        ),
        (13, 'pb_column_read_roles', 'first_column',
        'S', 'officer', 'table'
        ),
        (14, 'pb_column_read_roles', null,
        'S', 'op-regression', 'table'
        );