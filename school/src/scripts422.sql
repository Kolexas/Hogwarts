Create TABLE Owner (
 owner_id SERIAL Primary KEY,
 age INTEGER,
 name TEXT,
 driver_license BOOLEAN
 );

 Create TABLE car (
 car_id SERIAL Primary KEY REFERENCES owner(owner_id),
 car_brand TEXT,
 model TEXT,
 cost INTEGER
 );
