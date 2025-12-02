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
    ban_count INTEGER default 0,
    banned_until TIMESTAMP,
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

create index idx_users_banned_until on users (banned_until);

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

-- Recipes table
create table if not exists recipes (
    id uuid default random_uuid () primary key,
    author_id uuid not null,
    title VARCHAR(200) not null,
    description CLOB,
    story CLOB,
    version INTEGER not null default 1,
    status VARCHAR(30) not null default 'DRAFT',
    is_current_version BOOLEAN default false,
    is_private BOOLEAN default false,
    prep_time_minutes INTEGER,
    cook_time_minutes INTEGER,
    servings INTEGER,
    difficulty VARCHAR(20),
    cuisine VARCHAR(50),
    category VARCHAR(50),
    strike_count INTEGER default 0,
    created_at TIMESTAMP not null default current_timestamp,
    updated_at TIMESTAMP not null default current_timestamp,
    submitted_at TIMESTAMP,
    published_at TIMESTAMP,
    constraint chk_recipe_status check (
        status in ('DRAFT', 'PENDING_REVIEW', 'PENDING_APPROVAL', 'PUBLISHED', 'SUSPENDED', 'REJECTED', 'PERMANENTLY_REJECTED', 'ARCHIVED')
    ),
    constraint chk_recipe_difficulty check (difficulty in ('EASY', 'MEDIUM', 'HARD')),
    constraint uq_author_title_version unique (author_id, title, version),
    foreign key (author_id) references users (id)
);

create index idx_recipes_author on recipes (author_id);

create index idx_recipes_status on recipes (status);

create index idx_recipes_published_at on recipes (published_at);

create index idx_recipes_title on recipes (title);

create index idx_recipes_is_current_version on recipes (is_current_version);

-- Recipe Dietary Info table
create table if not exists recipe_dietary_info (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null unique,
    is_vegetarian BOOLEAN default true,
    is_vegan BOOLEAN default false,
    is_dairy_free BOOLEAN default false,
    is_gluten_free BOOLEAN default false,
    has_nuts BOOLEAN default false,
    has_onion BOOLEAN default false,
    has_garlic BOOLEAN default false,
    has_eggs BOOLEAN default false,
    has_soy BOOLEAN default false,
    foreign key (recipe_id) references recipes (id) on delete cascade
);

create index idx_recipe_dietary_info_recipe on recipe_dietary_info (recipe_id);

create index idx_recipe_dietary_info_vegetarian on recipe_dietary_info (is_vegetarian);

create index idx_recipe_dietary_info_vegan on recipe_dietary_info (is_vegan);

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

-- Ingredients table
create table if not exists ingredients (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null,
    name VARCHAR(200) not null,
    quantity DECIMAL(10, 2),
    unit_id uuid,
    notes CLOB,
    order_index INTEGER not null,
    foreign key (recipe_id) references recipes (id) on delete cascade,
    foreign key (unit_id) references units (id)
);

create index idx_ingredients_recipe on ingredients (recipe_id);

create index idx_ingredients_unit on ingredients (unit_id);

-- Instructions table
create table if not exists instructions (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null,
    step_number INTEGER not null,
    description CLOB not null,
    duration_minutes INTEGER,
    constraint uq_recipe_step unique (recipe_id, step_number),
    foreign key (recipe_id) references recipes (id) on delete cascade
);

create index idx_instructions_recipe on instructions (recipe_id);

create index idx_instructions_step on instructions (recipe_id, step_number);

-- Reviews table
create table if not exists reviews (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null,
    reviewer_id uuid not null,
    status VARCHAR(20) not null default 'PENDING',
    comments CLOB,
    reviewed_at TIMESTAMP not null default current_timestamp,
    constraint chk_review_status check (status in ('PENDING', 'APPROVED', 'REJECTED')),
    constraint uq_recipe_reviewer unique (recipe_id, reviewer_id),
    foreign key (recipe_id) references recipes (id) on delete cascade,
    foreign key (reviewer_id) references users (id)
);

create index idx_reviews_recipe on reviews (recipe_id);

create index idx_reviews_reviewer on reviews (reviewer_id);

create index idx_reviews_status on reviews (status);

-- Approvals table
create table if not exists approvals (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null,
    approver_id uuid not null,
    status VARCHAR(20) not null default 'PENDING',
    comments CLOB,
    approved_at TIMESTAMP not null default current_timestamp,
    constraint chk_approval_status check (status in ('PENDING', 'APPROVED', 'REJECTED')),
    constraint uq_recipe_approver unique (recipe_id, approver_id),
    foreign key (recipe_id) references recipes (id) on delete cascade,
    foreign key (approver_id) references users (id)
);

create index idx_approvals_recipe on approvals (recipe_id);

create index idx_approvals_approver on approvals (approver_id);

create index idx_approvals_status on approvals (status);

-- Flags table
create table if not exists flags (
    id uuid default random_uuid () primary key,
    recipe_id uuid not null,
    user_id uuid not null,
    reason VARCHAR(50) not null,
    description CLOB,
    status VARCHAR(20) not null default 'PENDING',
    created_at TIMESTAMP not null default current_timestamp,
    resolved_at TIMESTAMP,
    resolved_by uuid,
    constraint chk_flag_reason check (
        reason in (
            'INAPPROPRIATE',
            'NON_VEGETARIAN',
            'SPAM',
            'COPYRIGHT',
            'MISLEADING',
            'DUPLICATE',
            'LOW_QUALITY',
            'HEALTH_SAFETY',
            'OTHER'
        )
    ),
    constraint chk_flag_status check (status in ('PENDING', 'UNDER_REVIEW', 'RESOLVED', 'DISMISSED')),
    foreign key (recipe_id) references recipes (id) on delete cascade,
    foreign key (user_id) references users (id),
    foreign key (resolved_by) references users (id)
);

create index idx_flags_recipe on flags (recipe_id);

create index idx_flags_user on flags (user_id);

create index idx_flags_status on flags (status);

create index idx_flags_created_at on flags (created_at);

-- Notifications table
create table if not exists notifications (
    id uuid default random_uuid () primary key,
    user_id uuid not null,
    type VARCHAR(50) not null,
    title VARCHAR(200) not null,
    message CLOB not null,
    data CLOB,
    status VARCHAR(20) not null default 'UNREAD',
    priority VARCHAR(20) not null default 'MEDIUM',
    action_url VARCHAR(500),
    created_at TIMESTAMP not null default current_timestamp,
    read_at TIMESTAMP,
    archived_at TIMESTAMP,
    expires_at TIMESTAMP,
    constraint chk_notification_status check (status in ('UNREAD', 'READ', 'ARCHIVED', 'DELETED')),
    constraint chk_notification_priority check (priority in ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')),
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

-- Notification metadata table
create table if not exists notification_metadata (
    id uuid default random_uuid () primary key,
    notification_id uuid not null,
    entity_type VARCHAR(50) not null,
    entity_id uuid,
    action_url VARCHAR(500),
    foreign key (notification_id) references notifications (id) on delete cascade
);

create index idx_notification_metadata_notification on notification_metadata (notification_id);

create index idx_notification_metadata_entity on notification_metadata (entity_type, entity_id);

-- Notification Queue table
create table if not exists notification_queue (
    id uuid default random_uuid () primary key,
    user_id uuid not null,
    notification_type VARCHAR(50) not null,
    title VARCHAR(200) not null,
    message text not null,
    data text,
    action_url VARCHAR(500),
    priority VARCHAR(20) not null default 'MEDIUM',
    channel VARCHAR(20) not null,
    scheduled_for TIMESTAMP not null default current_timestamp,
    status VARCHAR(20) not null default 'PENDING',
    attempts INT not null default 0,
    max_attempts INT not null default 3,
    last_attempt_at TIMESTAMP,
    error_message text,
    created_at TIMESTAMP not null default current_timestamp,
    processed_at TIMESTAMP,
    constraint chk_queue_status check (status in ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'CANCELLED')),
    constraint chk_queue_channel check (channel in ('EMAIL', 'SMS', 'IN_APP')),
    constraint chk_queue_priority check (priority in ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')),
    foreign key (user_id) references users (id) on delete cascade
);

create index idx_queue_user on notification_queue (user_id);

create index idx_queue_status on notification_queue (status);

create index idx_queue_scheduled on notification_queue (scheduled_for);

create index idx_queue_status_scheduled on notification_queue (status, scheduled_for);

create index idx_queue_channel on notification_queue (channel);

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
