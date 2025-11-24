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
    invitee_email VARCHAR(255) not null,
    is_active BOOLEAN default true,
    expires_at TIMESTAMP not null,
    used_at TIMESTAMP,
    created_at TIMESTAMP not null default current_timestamp,
    foreign key (inviter_id) references users (id),
    foreign key (invitee_id) references users (id)
);

create index idx_invitation_codes_code on invitation_codes (code);

create index idx_invitation_codes_inviter on invitation_codes (inviter_id);

create index idx_invitation_codes_invitee on invitation_codes (invitee_id);

create index idx_invitation_codes_invitee_email on invitation_codes (invitee_email);

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

-- LastName Aliases table
create table if not exists lastname_aliases (
    id uuid default random_uuid () primary key,
    permitted_lastname_id uuid not null,
    alias VARCHAR(100) not null,
    created_at TIMESTAMP not null default current_timestamp,
    updated_at TIMESTAMP not null default current_timestamp,
    foreign key (permitted_lastname_id) references permitted_lastnames (id) on delete cascade
);

create index idx_lastname_aliases_permitted_lastname on lastname_aliases (permitted_lastname_id);

create index idx_lastname_aliases_alias on lastname_aliases (alias);

create unique index idx_lastname_aliases_unique on lastname_aliases (permitted_lastname_id, alias);

-- Units table
create table if not exists units (
    id uuid default random_uuid () primary key,
    name VARCHAR(50) not null unique,
    abbreviation VARCHAR(10) not null unique,
    unit_type VARCHAR(20) not null,
    description VARCHAR(200),
    is_active BOOLEAN not null default true,
    created_at TIMESTAMP not null default current_timestamp,
    updated_at TIMESTAMP not null default current_timestamp,
    constraint chk_unit_type check (unit_type in ('VOLUME', 'WEIGHT', 'COUNT', 'LENGTH', 'TEMPERATURE', 'TIME'))
);

create index idx_units_type on units (unit_type);

create index idx_units_is_active on units (is_active);

create index idx_units_name on units (name);

-- Notifications table
create table if not exists notifications (
    id uuid default random_uuid () primary key,
    user_id uuid not null,
    type VARCHAR(50) not null,
    title VARCHAR(200) not null,
    message CLOB not null,
    data CLOB,
    status VARCHAR(20) not null default 'UNREAD',
    created_at TIMESTAMP not null default current_timestamp,
    read_at TIMESTAMP,
    constraint chk_notification_status check (status in ('UNREAD', 'READ', 'ARCHIVED')),
    foreign key (user_id) references users (id) on delete cascade
);

create index idx_notifications_user on notifications (user_id);

create index idx_notifications_status on notifications (status);

create index idx_notifications_created_at on notifications (created_at);

-- Notification Preferences table
create table notification_preferences (
    user_id uuid primary key,
    email_enabled BOOLEAN not null default false,
    sms_enabled BOOLEAN not null default false,
    frequency VARCHAR(20) not null default 'INSTANT',
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    subscribed_events CLOB,
    updated_at TIMESTAMP not null default current_timestamp,
    constraint chk_frequency check (frequency in ('INSTANT', 'DAILY_DIGEST', 'WEEKLY_DIGEST')),
    foreign key (user_id) references users (id) on delete cascade
);

create index idx_notification_preferences_user on notification_preferences (user_id);

-- Audit Logs table
create table if not exists audit_logs (
    id uuid default random_uuid () primary key,
    user_id uuid not null,
    action VARCHAR(100) not null,
    entity_type VARCHAR(50) not null,
    entity_id uuid,
    changes CLOB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP not null default current_timestamp,
    foreign key (user_id) references users (id)
);

create index idx_audit_logs_user on audit_logs (user_id);

create index idx_audit_logs_entity on audit_logs (entity_type, entity_id);

create index idx_audit_logs_created_at on audit_logs (created_at);

-- System Configuration table
create table system_config (
    key_name VARCHAR(100) primary key not null,
    key_value VARCHAR(255) not null,
    description CLOB,
    updated_by uuid,
    updated_at TIMESTAMP not null default current_timestamp,
    foreign key (updated_by) references users (id)
);

create index idx_system_name on system_config (key_name);
