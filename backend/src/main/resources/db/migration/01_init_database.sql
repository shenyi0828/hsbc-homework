-- Create database if not exists
CREATE DATABASE IF NOT EXISTS hsbc_transaction CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hsbc_transaction;

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key',
    transaction_id VARCHAR(100) NOT NULL UNIQUE COMMENT 'Unique transaction identifier',
    account_number VARCHAR(50) NOT NULL COMMENT 'Account number',
    counterparty_account VARCHAR(50) COMMENT 'Counterparty account number',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD' COMMENT 'Currency code',
    amount INT NOT NULL COMMENT 'Amount in cents',
    transaction_type TINYINT NOT NULL COMMENT 'Transaction type: 1=DEPOSIT, 2=WITHDRAWAL, 3=TRANSFER',
    transaction_status TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 1=PENDING, 2=COMPLETED, 3=FAILED, 4=CANCELLED',
    description TEXT COMMENT 'Transaction description',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
    
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_account_number (account_number),
    INDEX idx_counterparty_account (counterparty_account),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_status (transaction_status),
    INDEX idx_created_at (created_at),
    INDEX idx_currency (currency)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Transaction records table';

-- Insert sample data
INSERT INTO transactions (transaction_id, account_number, counterparty_account, currency, amount, transaction_type, transaction_status, description) VALUES
('TXN001', 'ACC001', NULL, 'USD', 100000, 1, 2, 'Initial deposit'),
('TXN002', 'ACC001', NULL, 'USD', 25000, 2, 2, 'ATM withdrawal'),
('TXN003', 'ACC002', NULL, 'USD', 50000, 1, 2, 'Salary deposit'),
('TXN004', 'ACC002', 'ACC003', 'USD', 15000, 3, 2, 'Transfer to ACC003'),
('TXN005', 'ACC003', 'ACC002', 'USD', 15000, 1, 2, 'Transfer from ACC002'),
('TXN006', 'ACC001', NULL, 'USD', 5000, 2, 1, 'Pending withdrawal'),
('TXN007', 'ACC003', NULL, 'USD', 2000, 2, 3, 'Failed withdrawal'),
('TXN008', 'ACC002', NULL, 'EUR', 30000, 1, 1, 'Pending deposit');

-- Create user for application (if not exists)
CREATE USER IF NOT EXISTS 'hsbc_user'@'%' IDENTIFIED BY 'hsbc_pass';
GRANT ALL PRIVILEGES ON hsbc_transaction.* TO 'hsbc_user'@'%';
FLUSH PRIVILEGES;