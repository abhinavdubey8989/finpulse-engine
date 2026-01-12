
DROP DATABASE IF EXISTS finpulse_db

CREATE TABLE users (
    -- id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    roles TEXT[] NOT NULL DEFAULT '{}',

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE user_expense_settings (
    user_id UUID NOT NULL REFERENCES users(id),
    category VARCHAR(40) NOT NULL,
    monthly_upper_limit INTEGER NOT NULL DEFAULT 2000,
    PRIMARY KEY (user_id, category)
);

CREATE INDEX idx_user_expense_setting_user ON user_personal_expense_setting(user_id);


CREATE TABLE personal_expenses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    year INTEGER NOT NULL,
    month VARCHAR(20) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),

    category VARCHAR(50) NOT NULL,
    amount INTEGER NOT NULL,
    description TEXT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
