# cashew

## connect to db

```
psql -h localhost -p 5450 -U cashew_user -d bookingdb
```

## db username

```
cashew_user
```

## password

```
cashew
```

## inserting some data

```
INSERT INTO desks (id, location, status)
VALUES
('desk1', 'Zone A', 'available'),
('desk2', 'Zone B', 'unavailable'),
('desk3', 'Zone A', 'available'),
('desk4', 'Zone C', 'available
```

## http requests

http GET http://localhost:8080/desks

http POST http://localhost:8080/desk/book userId="user1" deskId="desk1" startTime="2024-10-06T10:00:00" endTime="
2024-10-06T14:00:00"

## inserting some data

```
INSERT INTO desks (id, location, status)
VALUES
('desk1', 'Zone A', 'available'),
('desk2', 'Zone B', 'unavailable'),
('desk3', 'Zone A', 'available'),
('desk4', 'Zone C', 'available
```

or call the POST endpoints correctly

docker run -p 5450:5432 --name postgres-container -e POSTGRES_PASSWORD=cashew --restart unless-stopped -d postgres

## creating tables in sql

```
CREATE TABLE desks (
id VARCHAR(255) PRIMARY KEY,    -- Unique identifier for each desk
location VARCHAR(255) NOT NULL, -- Location of the desk
status VARCHAR(50) NOT NULL     -- Status of the desk (e.g., "available", "unavailable")
);
```

```
CREATE TABLE bookings (
id VARCHAR(255) PRIMARY KEY,   
user_id VARCHAR(255) NOT NULL,
desk_id VARCHAR(255) NOT NULL,    
start_time TIMESTAMP NOT NULL,    
end_time TIMESTAMP NOT NULL,    
FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL  -- Desk foreign key
);
```

## sql db initalise script - creates tables and data

```
psql -h localhost -p 5450 -U your_username -d bookingdb -f init.sql
```

## or using docker

```
docker exec -i postgres-container psql -U cashew_user -d bookingdb < init.sql
```

## using flyway db migrations

```
flyway -url=jdbc:postgresql://localhost:5450/cashew_db -user=cashew_user -password=cashew_password migrate
```

```
psql -h localhost -p 5450 -U postgres

PGPASSWORD=cashew psql -h localhost -p 5450 -U postgres
```

ALTER USER cashew_man WITH PASSWORD 'cashew';

```
CREATE USER cashew_man WITH PASSWORD 'cashew';
```

GRANT ALL PRIVILEGES ON DATABASE postgres TO cashew_man;

GRANT CONNECT ON DATABASE postgres-container TO cashew_man;
GRANT USAGE ON SCHEMA public TO new_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO new_user;

sudo chmod -R 755 /Users/michaelyau/self_projects/cashew/cashew/migrations

## Access db

psql -h localhost -p 5450 -U cashew_user -d cashew_db
cashew_password

## Docker commands

## Sets up docker compose environment

```
docker-compose up --build
```

## Clean up docker compose environment

```
docker-compose down
```

### GET Request - bookings

```
http GET http://localhost:8080/api/bookings/booking_1
```

### POST Request - bookings

```
http POST http://localhost:8080/api/bookings booking_id="booking_7" booking_name="Meeting with Mary" userId:=5 workspaceId:=1 booking_date="2024-10-21" start_time="2024-10-21T09:00:00" end_time="2024-10-21T12:00:00" status="Confirmed" created_at="2024-10-15T17:04:38"
```

```
http POST http://localhost:8080/register userId="user_id_1" username="newuser" password="SecurePass123!" first_name="John" last_name="Doe" street="123_Main_St" city="Anytown" country="Country" county="County" postcode="12345" contact_number="1234567890" email="newuser@example.com" role="Wanderer"
```

## Make sure when creating users values are unique and do not violate any rules

```
http POST http://localhost:8080/cashew/register username="mikey5922" password="secure_password" first_name="mike" last_name="smith" contact_number="+07402205071" role="user" email="mikey5922@example.com" created_at="2024-10-20T10:00:00"
```


### IT Test DB
```
psql -h localhost -p 5450 -U cashew_user -d cashew_test_db
```

### Create user request
```
http POST http://localhost:8080/cashew/register userId="user_id_123" username="newuser" password="SecurePass123@" email="newuser@example.com" role="Wanderer" created_at="2024-01-01T12:00:00"
```

### Login request
```
http POST http://localhost:8080/cashew/login username="mikey5922" password="X10gk7rm@"
```

http POST http://localhost:8080/cashew/register user_id="mikey5922_new" username="mikey5922" password="SecurePass123@" email="m.yw.yau@gmail.com" role="Wanderer" created_at="2024-01-01T12:00:00"

http POST http://localhost:8080/cashew/register user_id="mikey5922_new" username="mikey5922" password="SecurePass123@" email="m.yw.yau@gmail.com" role="Wanderer" created_at="2024-01-01T12:00:00"
