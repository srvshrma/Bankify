INSERT INTO customers (id, full_name, email, joined_on) VALUES (1, 'Aarav Mehta', 'aarav@bank.com', '2024-01-15');
INSERT INTO customers (id, full_name, email, joined_on) VALUES (2, 'Isha Kapoor', 'isha@bank.com', '2024-03-11');

INSERT INTO accounts (id, account_number, account_type, balance, created_at, customer_id)
VALUES (1, 'BNK240101000001', 'SAVINGS', 8500.00, '2024-01-16T10:15:00', 1);
INSERT INTO accounts (id, account_number, account_type, balance, created_at, customer_id)
VALUES (2, 'BNK240201000002', 'CURRENT', 1200.00, '2024-03-12T09:45:00', 2);
INSERT INTO accounts (id, account_number, account_type, balance, created_at, customer_id)
VALUES (3, 'BNK240201000003', 'FIXED_DEPOSIT', 15000.00, '2024-03-13T16:20:00', 2);

INSERT INTO bank_transactions (id, transaction_type, amount, description, transaction_time, account_id)
VALUES (1, 'DEPOSIT', 8500.00, 'Initial deposit', '2024-01-16T10:15:00', 1);
INSERT INTO bank_transactions (id, transaction_type, amount, description, transaction_time, account_id)
VALUES (2, 'DEPOSIT', 1200.00, 'Initial deposit', '2024-03-12T09:45:00', 2);
INSERT INTO bank_transactions (id, transaction_type, amount, description, transaction_time, account_id)
VALUES (3, 'DEPOSIT', 15000.00, 'Initial deposit', '2024-03-13T16:20:00', 3);

ALTER TABLE customers ALTER COLUMN id RESTART WITH 3;
ALTER TABLE accounts ALTER COLUMN id RESTART WITH 4;
ALTER TABLE bank_transactions ALTER COLUMN id RESTART WITH 4;
