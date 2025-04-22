 Create TABLE car (
 car_id SERIAL Primary KEY,
 car_brand TEXT,
 model TEXT,
 cost INTEGER
 );

 Create TABLE Owner (
  owner_id SERIAL Primary KEY,
  car_id INTEGER REFERENCES car(car_id),
  age INTEGER,
  name TEXT,
  driver_license BOOLEAN
  );
