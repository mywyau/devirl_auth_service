# pistachio

This Backend service is responsible for business domain data e.g. businesses, offices and desks.

### To run the app

```
./run.sh
```

### To run the tests

```
./run_tests.sh
```

### To run only a single test suite in the integration tests:

Please remember to include the package/path for the shared resources,
the shared resources is needed to help WeaverTests locate the shared resources needed for the tests

```
./itTestOnly RegistrationControllerISpec controllers.ControllerSharedResource 
```

---

### To populate the postgres sql database using flyway

Please run the docker compose scripts

```
./setup_flyway_migrations.sh
```

### To clear down the database

```
./clear_down_flyway.sh
```

```
./clear_down_postgres.sh
```

### To clear down docker container for app and orphans
```
docker-compose down --volumes --remove-orphans
```

---

### To connect to postgres sql db

```
psql -h localhost -p 5432 -U shared_user -d shared_db
```

### Set base search path for schema

only needed if using multiple schemas in the db. At the moment we are using public so no need beforehand 
accidentally set a new schema in flyway conf

```
ALTER ROLE shared_user SET search_path TO share_schema, public;
```


### Httpie requests 

We can use httppie instead of curl

WIP TODO: add some exmaples to hit our endpoints
```
http POST http://localhost:1010/pistachio/business/offices/address/create Content-Type:application/json businessId="BUS12345" officeId="OFF12345" buildingName="Example Building" floorNumber="12" street="123 Example Street" city="Example City" country="Example Country" county="Example County" postcode="12345" latitude:=12.345678 longitude:=-98.765432
```