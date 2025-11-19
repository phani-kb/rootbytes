-- Seed initial configuration values
delete from system_config;

insert into
    system_config (key_name, key_value, description)
values
    ('invitation.expiry.days', '7', 'Days until invitation expires'),
    ('invitation.max-per-user', '1', 'Maximum invitations per user'),
    ('security.max-login-failures', '3', 'Maximum login failures before lockout'),
    ('validation.lastname.enabled', 'true', 'Enable last name validation against valid list'),
    ('locale.default', 'en', 'Default application locale'),
    ('locale.supported', 'en', 'Supported application locales');

delete from units;

insert into
    units (name, abbreviation, unit_type, description)
values -- Volume units
    ('Cup', 'cup', 'VOLUME', 'Large volume measurement'),
    ('Deciliter', 'dl', 'VOLUME', 'Metric medium volume measurement'),
    ('Fluid Ounce', 'fl oz', 'VOLUME', 'Imperial volume measurement'),
    ('Gallon', 'gal', 'VOLUME', 'Imperial large volume measurement'),
    ('Liter', 'L', 'VOLUME', 'Metric large volume measurement'),
    ('Milliliter', 'ml', 'VOLUME', 'Metric small volume measurement'),
    ('Pint', 'pt', 'VOLUME', 'Imperial volume measurement'),
    ('Quart', 'qt', 'VOLUME', 'Imperial volume measurement'),
    ('Tablespoon', 'tbsp', 'VOLUME', 'Medium volume measurement'),
    ('Teaspoon', 'tsp', 'VOLUME', 'Small volume measurement'),
    -- Weight units
    ('Gram', 'g', 'WEIGHT', 'Metric small weight measurement'),
    ('Kilogram', 'kg', 'WEIGHT', 'Metric large weight measurement'),
    ('Milligram', 'mg', 'WEIGHT', 'Metric very small weight measurement'),
    ('Ounce', 'oz', 'WEIGHT', 'Imperial weight measurement'),
    ('Pound', 'lb', 'WEIGHT', 'Imperial weight measurement'),
    -- Count units
    ('Bunch', 'bunch', 'COUNT', 'Bundle of items'),
    ('Can', 'can', 'COUNT', 'Canned item'),
    ('Clove', 'clove', 'COUNT', 'Single clove'),
    ('Dash', 'dash', 'COUNT', 'Very small amount'),
    ('Dozen', 'doz', 'COUNT', 'Group of 12 items'),
    ('Handful', 'handful', 'COUNT', 'Approximate handful'),
    ('Head', 'head', 'COUNT', 'Whole head of produce'),
    ('Item', 'item', 'COUNT', 'Individual item count'),
    ('Package', 'pkg', 'COUNT', 'Pre-packaged item'),
    ('Pinch', 'pinch', 'COUNT', 'Very small amount'),
    ('Piece', 'pc', 'COUNT', 'Individual item count'),
    ('Slice', 'slice', 'COUNT', 'Single slice'),
    ('Sprig', 'sprig', 'COUNT', 'Small stem with leaves'),
    ('Stalk', 'stalk', 'COUNT', 'Single stalk'),
    -- Length units
    ('Centimeter', 'cm', 'LENGTH', 'Metric length measurement'),
    ('Inch', 'in', 'LENGTH', 'Imperial length measurement'),
    -- Temperature units
    ('Celsius', '°C', 'TEMPERATURE', 'Metric temperature'),
    ('Fahrenheit', '°F', 'TEMPERATURE', 'Imperial temperature'),
    -- Time units
    ('Hour', 'hr', 'TIME', 'Time measurement in hours'),
    ('Minute', 'min', 'TIME', 'Time measurement in minutes'),
    ('Second', 'sec', 'TIME', 'Time measurement in seconds');

delete from permitted_lastnames;

insert into
    permitted_lastnames (last_name, description, category, is_active)
values
    ('Kumar', 'Common Indian surname', 'INDIAN', true),
    ('Singh', 'Common Indian surname', 'INDIAN', true),
    ('Williams', 'Common English surname', 'COMMON', true),
    ('Smith', 'Common English surname', 'COMMON', true);
