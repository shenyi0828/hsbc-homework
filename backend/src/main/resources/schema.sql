-- Drop table if exists
DROP TABLE IF EXISTS transactions;

-- Create transactions table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL COMMENT 'Unique transaction identifier',
    amount INT NOT NULL COMMENT 'Transaction amount in cents',
    transaction_type TINYINT NOT NULL CHECK (transaction_type IN (1, 2)) COMMENT 'Transaction type: 1=EXPENSE, 2=INCOME',
    account_number VARCHAR(32) NOT NULL COMMENT 'Account number',
    counterparty_account VARCHAR(32) COMMENT 'Counterparty account',
    description VARCHAR(500) COMMENT 'Transaction description',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE UNIQUE INDEX idx_transactions_transaction_id ON transactions(transaction_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);