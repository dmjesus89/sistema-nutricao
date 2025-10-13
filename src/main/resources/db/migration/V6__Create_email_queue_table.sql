-- V6__Create_email_queue_table.sql
-- Migration for email queue table to handle asynchronous email delivery

-- Create Email Queue Table
CREATE TABLE email_queue (
    id BIGSERIAL PRIMARY KEY,
    email_type VARCHAR(30) NOT NULL,
    recipient_email VARCHAR(100) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    token VARCHAR(500),
    additional_data TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 5,
    last_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    next_retry_at TIMESTAMP,
    sent_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for email_queue
CREATE INDEX idx_email_queue_status ON email_queue (status);
CREATE INDEX idx_email_queue_email_type ON email_queue (email_type);
CREATE INDEX idx_email_queue_recipient_email ON email_queue (recipient_email);
CREATE INDEX idx_email_queue_next_retry ON email_queue (next_retry_at);
CREATE INDEX idx_email_queue_created_at ON email_queue (created_at);
CREATE INDEX idx_email_queue_status_retry ON email_queue (status, next_retry_at)
    WHERE status = 'PENDING';

-- Add check constraints for enum values
ALTER TABLE email_queue
    ADD CONSTRAINT chk_email_queue_email_type
    CHECK (email_type IN ('CONFIRMATION', 'WELCOME', 'PASSWORD_RESET', 'MEAL_REMINDER', 'MEAL_CONSUMPTION', 'WEEKLY_SUMMARY'));

ALTER TABLE email_queue
    ADD CONSTRAINT chk_email_queue_status
    CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED'));

ALTER TABLE email_queue
    ADD CONSTRAINT chk_email_queue_retry_count
    CHECK (retry_count >= 0);

ALTER TABLE email_queue
    ADD CONSTRAINT chk_email_queue_max_retries
    CHECK (max_retries >= 0);

-- Add table comments
COMMENT ON TABLE email_queue IS 'Queue for asynchronous email delivery with retry mechanism';
COMMENT ON COLUMN email_queue.email_type IS 'Type of email: CONFIRMATION, WELCOME, PASSWORD_RESET, MEAL_REMINDER, MEAL_CONSUMPTION, WEEKLY_SUMMARY';
COMMENT ON COLUMN email_queue.status IS 'Current status: PENDING, PROCESSING, SENT, FAILED';
COMMENT ON COLUMN email_queue.retry_count IS 'Number of retry attempts made';
COMMENT ON COLUMN email_queue.next_retry_at IS 'Timestamp when the next retry should be attempted (exponential backoff)';
