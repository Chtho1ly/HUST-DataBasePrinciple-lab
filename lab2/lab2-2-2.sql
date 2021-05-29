CREATE TABLE location_record_2(
  id INT,
  p_id INT,
  loc_id INT,
  s_time DATETIME,
  e_time DATETIME,
  CONSTRAINT pk_itinerary PRIMARY KEY (id),
  CONSTRAINT fk_itinerary_pid FOREIGN KEY (p_id) REFERENCES person(id),
  CONSTRAINT fk_itinerary_lid FOREIGN KEY (loc_id) REFERENCES location(id)
)
INSERT INTO location_record_2 SELECT * FROM itinerary WHERE loc_id=2;