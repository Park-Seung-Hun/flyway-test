CREATE SEQUENCE IF NOT EXISTS member_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE member
(
    id       BIGINT NOT NULL,
    name     VARCHAR(255),
    nickname VARCHAR(255),
    CONSTRAINT pk_member PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT uc_member_name UNIQUE (name);
