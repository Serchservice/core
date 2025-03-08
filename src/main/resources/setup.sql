CREATE EXTENSION postgis;

-- Create schemas if they don't already exist
create schema if not exists account;
create schema if not exists admin;
create schema if not exists company;
create schema if not exists conversation;
create schema if not exists identity;
create schema if not exists platform;
create schema if not exists sharing;
create schema if not exists trip;
create schema if not exists admin_action;
create schema if not exists nearby;

-- Step 1: Check if the schema exists, and if not, exit with a message
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.schemata
            WHERE schema_name = 'nearby'
        ) THEN
            RAISE NOTICE 'Schema "nearby" does not exist. Exiting without creating anything.';
        ELSE
            RAISE NOTICE 'Schema "nearby" exists. Proceeding to check the table.';
        END IF;
    END;
$$;

-- Step 2: Check if the table exists, and if not, exit with a message
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.tables
            WHERE table_schema = 'nearby'
              AND table_name = 'go_activities'
        ) THEN
            RAISE NOTICE 'Table "nearby.go_activities" does not exist. Exiting without creating anything.';
        ELSE
            RAISE NOTICE 'Table "nearby.go_activities" exists. Proceeding to check indexes.';
        END IF;
    END;
$$;

-- Step 3: Check for the first index (date, start_time) and create it if it does not exist
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_indexes
            WHERE schemaname = 'nearby'
              AND tablename = 'go_activities'
              AND indexname = 'idx_go_activity_timestamp_start_time'
        ) THEN
            CREATE INDEX idx_go_activity_timestamp_start_time
                ON nearby.go_activities (date, start_time);
            RAISE NOTICE 'Index "idx_go_activity_timestamp_start_time" created successfully.';
        ELSE
            RAISE NOTICE 'Index "idx_go_activity_timestamp_start_time" already exists.';
        END IF;
    END;
$$;

-- Step 4: Check for the second index (date, end_time) and create it if it does not exist
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_indexes
            WHERE schemaname = 'nearby'
              AND tablename = 'go_activities'
              AND indexname = 'idx_go_activity_timestamp_end_time'
        ) THEN
            CREATE INDEX idx_go_activity_timestamp_end_time
                ON nearby.go_activities (date, end_time);
            RAISE NOTICE 'Index "idx_go_activity_timestamp_end_time" created successfully.';
        ELSE
            RAISE NOTICE 'Index "idx_go_activity_timestamp_end_time" already exists.';
        END IF;
    END;
$$;

-- Insert GoInterest Categories with timestamps
INSERT INTO nearby.go_interest_categories (id, name, created_at, updated_at) VALUES
(1, 'Sports & Physical Activities', NOW(), NOW()),
(2, 'Entertainment & Fun', NOW(), NOW()),
(3, 'Food & Drink', NOW(), NOW()),
(4, 'Travel & Outdoor Adventures', NOW(), NOW()),
(5, 'Learning & Creativity', NOW(), NOW()),
(6, 'Pet & Animal Lovers', NOW(), NOW()),
(7, 'DIY & Social Causes', NOW(), NOW()),
(8, 'Special Interests', NOW(), NOW());

-- Insert Interests for "Sports & Physical Activities"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(101, 'Basketball', '🏀', 0, 1, 'playing', NOW(), NOW()),
(102, 'Football/Soccer', '⚽', 0, 1, 'playing', NOW(), NOW()),
(103, 'American Football', '🏈', 0, 1, 'playing', NOW(), NOW()),
(104, 'Volleyball', '🏐', 0, 1, 'playing', NOW(), NOW()),
(105, 'Hockey', '🏒', 0, 1, 'playing', NOW(), NOW()),
(106, 'Tennis', '🎾', 0, 1, 'playing', NOW(), NOW()),
(107, 'Table Tennis', '🏓', 0, 1, 'playing', NOW(), NOW()),
(108, 'Badminton', '🏸', 0, 1, 'playing', NOW(), NOW()),
(109, 'Golf', '⛳', 0, 1, 'playing', NOW(), NOW()),
(110, 'Swimming', '🏊', 0, 1, '', NOW(), NOW()),
(111, 'Cycling', '🚴', 0, 1, '', NOW(), NOW()),
(112, 'Running & Jogging', '🏃', 0, 1, '', NOW(), NOW()),
(113, 'Martial Arts', '🥋', 0, 1, '', NOW(), NOW()),
(114, 'Gym & Fitness', '🏋️', 0, 1, '', NOW(), NOW()),
(115, 'Hiking & Trekking', '🥾', 0, 1, '', NOW(), NOW()),
(116, 'Rock Climbing', '🧗', 0, 1, '', NOW(), NOW()),
(117, 'Surfing', '🏄', 0, 1, '', NOW(), NOW()),
(118, 'Skiing & Snowboarding', '⛷️', 0, 1, '', NOW(), NOW()),
(119, 'Water Sports', '🚣', 0, 1, '', NOW(), NOW());

-- Insert Interests for "Entertainment & Fun"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(201, 'Movie Night', '🎬', 0, 2, '', NOW(), NOW()),
(202, 'Gaming', '🎮', 0, 2, '', NOW(), NOW()),
(203, 'Board Games', '🎲', 0, 2, 'playing', NOW(), NOW()),
(204, 'Karaoke', '🎤', 0, 2, '', NOW(), NOW()),
(205, 'Live Music & Concerts', '🎸', 0, 2, '', NOW(), NOW());

-- Insert Interests for "Food & Drink"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(301, 'Foodie Meetups', '🍕', 0, 3, '', NOW(), NOW()),
(302, 'Cooking & Baking', '👨‍🍳', 0, 3, '', NOW(), NOW()),
(303, 'Coffee Meetups', '☕', 0, 3, '', NOW(), NOW()),
(304, 'Wine & Beer Tasting', '🍷', 0, 3, '', NOW(), NOW());

-- Insert Interests for "Travel & Outdoor Adventures"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(401, 'Beach Days', '🏖️', 0, 4, '', NOW(), NOW()),
(402, 'Road Trips', '🚗', 0, 4, '', NOW(), NOW()),
(403, 'Camping', '🏕️', 0, 4, '', NOW(), NOW()),
(404, 'Motorcycle & Car Clubs', '🏍️', 0, 4, '', NOW(), NOW()),
(405, 'Theme Parks', '🎢', 0, 4, '', NOW(), NOW());

-- Insert Interests for "Learning & Creativity"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(501, 'Art & Painting', '🖌️', 0, 5, '', NOW(), NOW()),
(502, 'Photography', '📷', 0, 5, '', NOW(), NOW()),
(503, 'Theater & Improv', '🎭', 0, 5, '', NOW(), NOW()),
(504, 'Writing & Poetry', '✍️', 0, 5, '', NOW(), NOW()),
(505, 'Coding & Tech', '💻', 0, 5, '', NOW(), NOW());

-- Insert Interests for "Pet & Animal Lovers"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(601, 'Dog Walking & Playdates', '🐕', 0, 6, '', NOW(), NOW()),
(602, 'Birdwatching', '🦜', 0, 6, '', NOW(), NOW()),
(603, 'Aquarium Enthusiasts', '🐠', 0, 6, '', NOW(), NOW());

-- Insert Interests for "DIY & Social Causes"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(701, 'DIY & Woodworking', '🛠️', 0, 7, '', NOW(), NOW()),
(702, 'Gardening', '🌿', 0, 7, '', NOW(), NOW()),
(703, 'Volunteering', '🤝', 0, 7, '', NOW(), NOW());

-- Insert Interests for "Special Interests"
INSERT INTO nearby.go_interests (id, name, emoji, popularity, category_id, verb, created_at, updated_at) VALUES
(801, 'Stargazing & Astronomy', '🔭', 0, 8, '', NOW(), NOW()),
(802, 'Escape Rooms', '🕵️', 0, 8, '', NOW(), NOW()),
(803, 'History & Mythology', '🏛️', 0, 8, '', NOW(), NOW()),
(804, 'Flea Markets & Thrift Shopping', '🛍️', 0, 8, '', NOW(), NOW());

CREATE OR REPLACE FUNCTION notify_interest_growth()
    RETURNS TRIGGER AS $$
DECLARE
    interest_json TEXT;
BEGIN
    interest_json := json_build_object(
        'interest_id', NEW.interest_id,
        'user_interest_id', NEW.id
    )::TEXT;  -- Explicitly cast JSON to TEXT

    PERFORM pg_notify('interest_growth', interest_json);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER interest_insert_trigger
    AFTER INSERT ON nearby.go_user_interests
    FOR EACH ROW
EXECUTE FUNCTION notify_interest_growth();