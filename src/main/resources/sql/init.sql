
DROP DATABASE IF EXISTS finpulse_db;

CREATE TABLE users (
    id UUID PRIMARY KEY NOT NULL,

    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    roles TEXT[] NOT NULL DEFAULT '{}',

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE expense_categories (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    category VARCHAR(40) NOT NULL,
    monthly_upper_limit INTEGER NOT NULL,
    description TEXT
);

CREATE INDEX idx_user_expense_setting_user ON user_personal_expense_setting(user_id);


CREATE TABLE personal_expenses (
    id UUID PRIMARY KEY NOT NULL,

    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES expense_categories(id),

    amount INTEGER NOT NULL,
    description TEXT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
