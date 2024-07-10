
INSERT INTO interest_rate (id, interest_rate, created_date, updated_date)
VALUES (RANDOM_UUID(), 6.5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO config_setting (id, key_name, "value", description) VALUES (RANDOM_UUID(), 'repayment.terms', '12,24,36', 'Repayment periods in months');
INSERT INTO config_setting (id, key_name, "value", description) VALUES (RANDOM_UUID(), 'token.expiry.time', '3600000', 'JWT token expiry time in milliseconds');
