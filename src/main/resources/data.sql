DROP TABLE IF EXISTS bank_account;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    pin varchar(250) NOT NULL,
    created datetime NOT NULL
);

INSERT INTO user (pin, created) VALUES
    ('3000:3d12846cfd4d87565cfd82a54292ab40:b74ef5f26ec0539eed692a8b3a0bd0ab21e436045611cdd7f60fc936952a49578f0f420d6f244bbb7d04759c84993fb8072b27b6961edc1474eb024d21c0dec4', '2021-04-28 00:00:00');

CREATE TABLE bank_account (
    account_number bigint NOT NULL,
    user_id bigint NOT NULL,
    balance decimal(12,2) NOT NULL,
    created datetime NOT NULL,
    updated datetime NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id),
    UNIQUE (account_number)
);

INSERT INTO bank_account (account_number, balance,  user_id, created, updated) VALUES
    (1234567812345678, 0, 1, '2021-04-28 00:00:00', '2021-04-28 00:00:00');
