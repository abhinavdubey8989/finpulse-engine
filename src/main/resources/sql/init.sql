
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    expense_settings JSONB NOT NULL DEFAULT '[]'::jsonb,
    roles TEXT[] NOT NULL DEFAULT '{}'

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE group_expenses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    year INTEGER NOT NULL,
    month VARCHAR(20) NOT NULL,
    user_name VARCHAR(100) NOT NULL,

    rent DOUBLE PRECISION DEFAULT 0,
    subscription DOUBLE PRECISION DEFAULT 0,
    grocery_hygiene DOUBLE PRECISION DEFAULT 0,
    commute DOUBLE PRECISION DEFAULT 0,
    eat_out DOUBLE PRECISION DEFAULT 0,
    shopping DOUBLE PRECISION DEFAULT 0,
    fitness DOUBLE PRECISION DEFAULT 0,
    travel DOUBLE PRECISION DEFAULT 0,
    family DOUBLE PRECISION DEFAULT 0,
    misc DOUBLE PRECISION DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE group_expenses_details (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    year JSONB NOT NULL,
    month JSONB NOT NULL,
    user_name JSONB NOT NULL,

    rent JSONB DEFAULT '{}'::jsonb,
    subscription JSONB DEFAULT '{}'::jsonb,
    grocery_hygiene JSONB DEFAULT '{}'::jsonb,
    commute JSONB DEFAULT '{}'::jsonb,
    eat_out JSONB DEFAULT '{}'::jsonb,
    shopping JSONB DEFAULT '{}'::jsonb,
    fitness JSONB DEFAULT '{}'::jsonb,
    travel JSONB DEFAULT '{}'::jsonb,
    family JSONB DEFAULT '{}'::jsonb,
    misc JSONB DEFAULT '{}'::jsonb,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE personal_expenses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    year INTEGER NOT NULL,
    month VARCHAR(20) NOT NULL,
    user_name VARCHAR(100) NOT NULL,

    invest DOUBLE PRECISION DEFAULT 0,
    dad_invest DOUBLE PRECISION DEFAULT 0,

    rent DOUBLE PRECISION DEFAULT 0,
    subscription DOUBLE PRECISION DEFAULT 0,
    grocery_hygiene DOUBLE PRECISION DEFAULT 0,
    commute DOUBLE PRECISION DEFAULT 0,
    eat_out DOUBLE PRECISION DEFAULT 0,
    shopping DOUBLE PRECISION DEFAULT 0, -- non-cosmetic always
    shopping_cosmetic DOUBLE PRECISION DEFAULT 0,
    fitness DOUBLE PRECISION DEFAULT 0,
    travel DOUBLE PRECISION DEFAULT 0,
    family DOUBLE PRECISION DEFAULT 0,
    house_hold DOUBLE PRECISION DEFAULT 0,
    misc DOUBLE PRECISION DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE personal_expenses_details (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    year JSONB NOT NULL,
    month JSONB NOT NULL,
    user_name JSONB NOT NULL,

    invest JSONB DEFAULT '{}'::jsonb,
    dad_invest JSONB DEFAULT '{}'::jsonb,

    rent JSONB DEFAULT '{}'::jsonb,
    subscription JSONB DEFAULT '{}'::jsonb,
    grocery_hygiene JSONB DEFAULT '{}'::jsonb,
    commute JSONB DEFAULT '{}'::jsonb,
    eat_out JSONB DEFAULT '{}'::jsonb,
    shopping JSONB DEFAULT '{}'::jsonb,
    shopping_cosmetic JSONB DEFAULT '{}'::jsonb,
    fitness JSONB DEFAULT '{}'::jsonb,
    travel JSONB DEFAULT '{}'::jsonb,
    family JSONB DEFAULT '{}'::jsonb,
    house_hold JSONB DEFAULT '{}'::jsonb,
    misc JSONB DEFAULT '{}'::jsonb,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
