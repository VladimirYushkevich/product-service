CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE product_status_enum_type AS ENUM('DELETED', 'ACTIVE', 'BLOCKED');

CREATE TABLE products
(
    id                uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    "name"            text,
    relation_path     text,
    online_status     product_status_enum_type,
    description_long  text,
    description_short text
);
