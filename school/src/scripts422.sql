Create TABLE Owner (
 owner_id SERIAL Primary KEY,
 age INTEGER,
 name TEXT,
 driver_license BOOLEAN
 );

 Create TABLE car (
 owner_id INTEGER REFERENCES Owner(owner_id),
 car_id SERIAL Primary KEY,
 car_brand TEXT,
 model TEXT,
 cost INTEGER
 );
