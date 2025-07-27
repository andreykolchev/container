CREATE KEYSPACE saga WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

USE saga;

CREATE TABLE saga.steps
(
    operation_id  TEXT,
    step_id       TEXT,
    updated       TIMESTAMP,
    context       TEXT,
    request_data  TEXT,
    response_data TEXT,
    PRIMARY KEY (operation_id, step_id)
);

CREATE TABLE saga.user_info_history
(
    operation_id TEXT,
    command      TEXT,
    updated      TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (operation_id, command)
);
