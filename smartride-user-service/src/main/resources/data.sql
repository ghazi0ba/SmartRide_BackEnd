-- Seed users whose emails match the Keycloak realm users (smartride-realm.json),
-- so the frontend can resolve the logged-in identity to a numeric user id.
-- DB is in-memory H2 (reset on every restart), so these inserts run once per boot.
INSERT INTO users (nom, prenom, email, password, role) VALUES
  ('One', 'Client', 'client1@smartride.tn', 'client1', 'CLIENT'),
  ('One', 'Driver', 'driver1@smartride.tn', 'driver1', 'CHAUFFEUR'),
  ('One', 'Admin',  'admin1@smartride.tn',  'admin1',  'ADMIN');
