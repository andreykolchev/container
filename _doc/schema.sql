CREATE TABLE public.step_entity
(
    operation_id  VARCHAR(255),
    step_id       VARCHAR(255),
    date          TIMESTAMP,
    context       TEXT,
    request_data  TEXT,
    response_data TEXT,
    PRIMARY KEY (operation_id, step_id)
);