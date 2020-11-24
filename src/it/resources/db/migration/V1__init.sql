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
        'startsWithColumn', 'person_full_name'
        ),
        (3, 'searchCondition', 'pd_processing_consent_2',
        'containsColumn', 'person_full_name'
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
        );

--- ddm_role:
CREATE TYPE type_perm AS ENUM ('S','I','U','D');
CREATE TABLE ddm_role_permission
(
	permission_id int NOT NULL,
	object_name text NOT NULL,
	column_name text,
	operation type_perm NOT NULL,
	role_name text NOT NULL
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
