-- Seed initial configuration values
delete from permitted_lastnames;

insert into
    permitted_lastnames (last_name, description, category, is_active)
values
    ('Kumar', 'Common Indian surname', 'INDIAN', true),
    ('Singh', 'Common Indian surname', 'INDIAN', true),
    ('Williams', 'Common English surname', 'COMMON', true),
    ('Smith', 'Common English surname', 'COMMON', true);
