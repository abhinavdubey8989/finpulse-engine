
DROP DATABASE IF EXISTS finpulse_db;


CREATE TYPE user_roles AS ENUM (
    'USER',
    'ADMIN'
 );


CREATE TYPE group_roles AS ENUM (
     'G_USER',
     'G_ADMIN'
 );

CREATE TYPE differentiator_type AS ENUM (
    'PERSONAL',
    'GROUP'
 );


 CREATE TYPE split_type AS ENUM (
     'PERCENT',
     'EXACT'
  );


CREATE TABLE users (
    id UUID PRIMARY KEY NOT NULL,

    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    roles TEXT[] NOT NULL DEFAULT '{}',
--    roles user_roles[] NOT NULL DEFAULT ARRAY['USER']::user_roles[],

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE expense_groups (
    id UUID PRIMARY KEY NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    name VARCHAR(40) NOT NULL,
    description VARCHAR(40) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE expense_categories (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    category VARCHAR(40) NOT NULL,
    monthly_upper_limit INTEGER NOT NULL,
    description TEXT,
    type differentiator_type NOT NULL,
    group_id UUID REFERENCES expense_groups(id),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE expense_tags (
    id UUID PRIMARY KEY NOT NULL,
    category_id UUID NOT NULL REFERENCES expense_categories(id),
    name VARCHAR(40) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE personal_expenses (
    id UUID PRIMARY KEY NOT NULL,

    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES expense_categories(id),
    tag_id UUID REFERENCES expense_tags(id),

    amount INTEGER NOT NULL,
    description TEXT,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE group_membership (
    id UUID PRIMARY KEY NOT NULL,
    group_id UUID NOT NULL REFERENCES expense_groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    group_roles group_roles[] NOT NULL DEFAULT ARRAY['G_USER']::group_roles[],

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);


CREATE TABLE group_expenses (
    id UUID PRIMARY KEY NOT NULL,

    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    paid_by UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES expense_categories(id),
    tag_id UUID REFERENCES expense_tags(id),
    group_id UUID REFERENCES expense_groups(id),

    amount INTEGER NOT NULL,
    description TEXT,

    split_type split_type NOT NULL,
    splits JSONB NOT NULL,
    due_amounts JSONB NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
