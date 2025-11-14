-- Users table
create table if not exists users (
    id uuid default random_uuid () primary key,
    email VARCHAR(255) unique not null,
    phone VARCHAR(20) unique,
    last_name VARCHAR(100) not null,
    public_name VARCHAR(100),
    unique_name VARCHAR(6) unique,
    password_hash VARCHAR(255),
    user_role VARCHAR(20) not null default 'USER',
    status VARCHAR(20) not null default 'PENDING',
    email_verified BOOLEAN default false,
    phone_verified BOOLEAN default false,
    created_at TIMESTAMP not null default current_timestamp,
    updated_at TIMESTAMP not null default current_timestamp,
    last_login_at TIMESTAMP,
    constraint chk_role check (user_role in ('ADMIN', 'MODERATOR', 'REVIEWER', 'AUTHOR', 'USER', 'BANNED')),
    constraint chk_status check (status in ('PENDING', 'ACTIVE', 'INACTIVE', 'BANNED'))
);

create index idx_users_email on users (email);

create index idx_users_role on users (user_role);

create index idx_users_status on users (status);

create index idx_users_public_name on users (public_name);

-- Invitation Codes table
create table if not exists invitation_codes (
    id uuid default random_uuid () primary key,
    code VARCHAR(50) unique not null,
    inviter_id uuid not null,
    invitee_id uuid,
    is_active BOOLEAN default true,
    expires_at TIMESTAMP not null,
    used_at TIMESTAMP,
    created_at TIMESTAMP not null default current_timestamp,
    foreign key (inviter_id) references users (id),
    foreign key (invitee_id) references users (id)
);

create index idx_invitation_codes_code on invitation_codes (code);

create index idx_invitation_codes_inviter on invitation_codes (inviter_id);

-- OTP Codes table
create table otp_codes (
    id uuid default random_uuid () primary key,
    user_id uuid not null,
    code VARCHAR(6) not null,
    purpose VARCHAR(50) not null,
    attempts INT not null default 0,
    max_attempts INT not null default 3,
    resend_count INTEGER not null default 0,
    max_resends INTEGER not null default 3,
    expires_at TIMESTAMP not null,
    verified_at TIMESTAMP,
    created_at TIMESTAMP not null default current_timestamp,
    constraint chk_otp_purpose check (purpose in ('REGISTRATION', 'LOGIN', 'PASSWORD_RESET', 'PHONE_CHANGE', 'EMAIL_CHANGE')),
    foreign key (user_id) references users (id) on delete cascade
);

create index idx_otp_user_id on otp_codes (user_id);

create index idx_otp_code on otp_codes (code);

create index idx_otp_expires_at on otp_codes (expires_at);

-- Permitted Last Names table
create table permitted_lastnames (
    id uuid default random_uuid () primary key,
    last_name VARCHAR(100) unique not null,
    description VARCHAR(500),
    category VARCHAR(50),
    is_active BOOLEAN not null default true,
    created_at TIMESTAMP not null default current_timestamp,
    updated_at TIMESTAMP not null default current_timestamp,
    created_by uuid,
    updated_by uuid
);

create index idx_permitted_lastnames_name on permitted_lastnames (last_name);

create index idx_permitted_lastnames_active on permitted_lastnames (is_active);

create index idx_permitted_lastnames_category on permitted_lastnames (category);
