CREATE TABLE categories
(
    id        bigserial PRIMARY KEY,
    "name"    text,
    parent_id bigint
);
