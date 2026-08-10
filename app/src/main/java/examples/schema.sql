-- =====================================================================
-- Airline Ticket Reservation System - Database Schema
-- Target: MySQL 8.x, database name: airline_service
-- =====================================================================
-- NOTE: `users` and `user_profiles` are NOT included here - those tables
-- were created for UC 1.1 / UC 1.2 before this schema file was put
-- together, and their exact DDL was never shared, so nothing is
-- guessed here. Create the database and those two tables first, then
-- run everything below.
--
-- Usage:
--   mysql -u root -p
--   CREATE DATABASE IF NOT EXISTS airline_service;
--   USE airline_service;
--   SOURCE schema.sql;
-- =====================================================================

-- ---------------------------------------------------------------------
-- Flights
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    airline_name VARCHAR(100),
    flight_number VARCHAR(20),
    source VARCHAR(10),
    destination VARCHAR(10),
    departure_date DATE,
    departure_time TIME,
    arrival_time TIME,
    fare DOUBLE,
    travel_class VARCHAR(20),
    available_seats INT,
    stops INT,
    status VARCHAR(20),
    aircraft_type VARCHAR(50),
    duration INT
);

-- Added for UC 8 (occupancy reporting needs a fixed capacity)
ALTER TABLE flights ADD COLUMN IF NOT EXISTS total_seats INT DEFAULT 0;
UPDATE flights SET total_seats = available_seats WHERE total_seats = 0;

-- ---------------------------------------------------------------------
-- Seats
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT,
    seat_number VARCHAR(5),
    seat_type VARCHAR(20),
    category VARCHAR(20),
    status VARCHAR(20),
    extra_charge DOUBLE,
    power_outlet BOOLEAN,
    extra_legroom BOOLEAN,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

-- ---------------------------------------------------------------------
-- Airports
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS airports (
    airport_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE,
    name VARCHAR(150),
    city VARCHAR(100),
    country VARCHAR(100),
    timezone VARCHAR(50),
    terminals VARCHAR(255),
    facilities VARCHAR(500),
    contact_phone VARCHAR(15),
    contact_email VARCHAR(100),
    active BOOLEAN DEFAULT TRUE
);

-- ---------------------------------------------------------------------
-- Bookings
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    pnr VARCHAR(10) UNIQUE,
    e_ticket_number VARCHAR(30),
    flight_id INT,
    user_id INT,
    status VARCHAR(30),
    total_fare DOUBLE,
    seat_charges DOUBLE,
    created_at DATETIME,
    expiry_time DATETIME,
    check_in_status BOOLEAN,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

-- ---------------------------------------------------------------------
-- Booking Passengers
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS booking_passengers (
    passenger_booking_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    id_proof VARCHAR(50),
    meal_preference VARCHAR(20),
    special_assistance VARCHAR(100),
    frequent_flyer_number VARCHAR(30),
    seat_number VARCHAR(5),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(15),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- Added for UC 7 (partial cancellation needs to mark individual passengers)
ALTER TABLE booking_passengers ADD COLUMN IF NOT EXISTS cancelled BOOLEAN DEFAULT FALSE;

-- Added for UC 16 (document expiry validation)
ALTER TABLE booking_passengers ADD COLUMN IF NOT EXISTS id_proof_expiry_date DATE;

-- ---------------------------------------------------------------------
-- Payment Transactions
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payment_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    gateway_transaction_id VARCHAR(50),
    method VARCHAR(20),
    amount DOUBLE,
    discount_applied DOUBLE,
    status VARCHAR(30),
    created_at DATETIME,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- ---------------------------------------------------------------------
-- Notification Logs
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient VARCHAR(150),
    channel VARCHAR(20),
    type VARCHAR(40),
    message VARCHAR(500),
    success BOOLEAN,
    sent_at DATETIME
);

-- ---------------------------------------------------------------------
-- Indexes (UC 15 - search optimization)
-- ---------------------------------------------------------------------
CREATE INDEX idx_flights_route_date ON flights (source, destination, departure_date);
CREATE INDEX idx_flights_status ON flights (status);
CREATE INDEX idx_airports_code ON airports (code);
CREATE INDEX idx_bookings_pnr ON bookings (pnr);
CREATE INDEX idx_bookings_user ON bookings (user_id);
