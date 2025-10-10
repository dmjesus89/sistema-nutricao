-- Add email notification tracking fields to users table
-- This allows us to track which welcome emails have been sent

ALTER TABLE users ADD COLUMN IF NOT EXISTS welcome_email_sent BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS welcome_email_sent_at TIMESTAMP;

-- Create index for querying users who haven't received welcome email
CREATE INDEX IF NOT EXISTS idx_users_welcome_email_pending
    ON users (welcome_email_sent, enabled, created_at)
    WHERE welcome_email_sent = false AND enabled = true;

COMMENT ON COLUMN users.welcome_email_sent IS 'Indicates whether the welcome email has been sent after account confirmation';
COMMENT ON COLUMN users.welcome_email_sent_at IS 'Timestamp when the welcome email was sent';
