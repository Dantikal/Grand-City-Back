-- Seed data matching the frontend's built-in mock data (src/entities/*/model/*.mocks.ts)
-- so the live backend shows the same catalog as the mock mode.

-- Default admin user: username "admin", password "admin123"
-- CHANGE THIS PASSWORD before going to production (see README.md).
INSERT INTO admin_users (username, password_hash) VALUES
    ('admin', '$2b$10$JiZn0jCHLrOeXMNxx1BVx.IrrVLeDBte9eELEOs0OZHO3EbUJ/I7u');

-- Agents
INSERT INTO agents (id, slug, name, role, bio, long_bio, photo, email, phone, specialties, areas, sales_count, rating, since) VALUES
('agent-1', 'mara-whitlock', 'Mara Whitlock', 'Founder · Principal',
 'Sellwood and the inner east side. Started Grand City in 2014.',
 'Mara founded Grand City after a decade at a high-volume brokerage left her wanting a slower, more honest way to work. She knows the inner east side block by block and has a reputation for pricing homes straight — even when it costs her the listing.',
 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=700&q=80',
 'mara@grandcity.studio', '(503) 555-0143',
 ARRAY['Period homes','Sellers','Pricing strategy'], ARRAY['Sellwood-Moreland','Ladd''s Addition','Alberta'],
 142, 4.9, 2014),

('agent-2', 'devon-aiyer', 'Devon Aiyer', 'Senior agent',
 'New builds and the South Waterfront. Architect''s eye.',
 'Trained as an architect before moving into property, Devon gravitates toward new construction and contemporary homes. Buyers lean on him to read a floor plan honestly and spot the things a staging photo hides.',
 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=700&q=80',
 'devon@grandcity.studio', '(503) 555-0144',
 ARRAY['New builds','Contemporary','Buyers'], ARRAY['South Waterfront','University Park','Pearl District'],
 88, 4.8, 2017),

('agent-3', 'priya-solberg', 'Priya Solberg', 'Agent · Lettings',
 'Pearl and downtown rentals, plus property management.',
 'Priya runs the lettings side of the studio — matching tenants to homes they''ll actually look after, and giving owners a manager who answers the phone. Calm, organized, and impossible to rattle.',
 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=700&q=80',
 'priya@grandcity.studio', '(503) 555-0145',
 ARRAY['Lettings','Property management','Investors'], ARRAY['Pearl District','Downtown','Richmond'],
 64, 4.9, 2019),

('agent-4', 'theo-marsh', 'Theo Marsh', 'Agent',
 'Mount Tabor and the east hills. Mid-century specialist.',
 'Theo is the studio''s mid-century devotee — he can date a house by its post-and-beam joinery and knows which east-hills streets get the best light. Patient with buyers who are waiting for exactly the right thing.',
 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=700&q=80',
 'theo@grandcity.studio', '(503) 555-0146',
 ARRAY['Mid-century','Luxury','Buyers'], ARRAY['Mount Tabor','Irvington','Laurelhurst'],
 51, 4.9, 2021);

-- Properties
INSERT INTO properties (id, slug, title, area, city, price, rent_period, category, listing_type, kind, status,
                         beds, baths, sqft, images, description, features, agent_id, lat, lng, featured, created_at) VALUES
('prop-osipenko', 'zhk-osipenko', 'Osipenko Club House', 'Pervomaisky, Osipenko St 90', 'Bishkek',
 190000, NULL, 'complex', 'luxury', 'apartment', 'new-build', 3, 2, 1023,
 ARRAY['/images/zhk-osipenko/photo-3.png','/images/zhk-osipenko/photo-4.png','/images/zhk-osipenko/photo-1.png','/images/zhk-osipenko/photo-2.png'],
 'A premium club-format low-rise of just 4 floors in Bishkek''s Pervomaisky district — gated grounds, an underground car park, and seismic resilience to 8–9 points. Built legally and fully financed by ООО «БГС». Completion February 2027.',
 ARRAY['Club format · only 4 floors','Underground parking','Gated private grounds','Seismic resilience 8–9','Total area 2,235 m²'],
 'agent-1', 42.8612, 74.631, TRUE, '2026-06-28T00:00:00Z'),

('prop-semak', 'semak-residence', 'Semak Residence', 'Levanevsky St 108', 'Bishkek',
 77000, NULL, 'complex', 'buy', 'apartment', 'new-build', 1, 1, 592,
 ARRAY['/images/semak-residence/photo-2.png','/images/semak-residence/photo-3.png','/images/semak-residence/photo-4.png','/images/semak-residence/photo-1.png'],
 'A new comfort-class landmark in Bishkek — a 17-storey monolithic tower with a ceramic-granite façade. Individual heating per apartment, underground parking for 118 cars, and 240 modern homes from $1,400/m². Completion Q4 2028.',
 ARRAY['17 storeys · 240 apartments','Individual heating','Underground parking (118)','Ceramic-granite façade','From $1,400 / m²'],
 'agent-2', 42.87, 74.576, TRUE, '2026-06-27T00:00:00Z'),

('prop-fuchika', 'zhk-fuchika', 'Fuchika Park Residence', 'Dimitrova St 2A · by the park', 'Bishkek',
 83000, NULL, 'complex', 'buy', 'apartment', 'new-build', 2, 1, 689,
 ARRAY['/images/zhk-fuchika/photo-1.png','/images/zhk-fuchika/photo-2.png','/images/zhk-fuchika/photo-3.png','/images/zhk-fuchika/photo-4.png'],
 'Comfort by the park, from established builder СК «Сталькон» (since 2001) — a 10-storey monolithic complex of 198 homes steps from Fuchika Park. Gated grounds with 24/7 security, 1,500 m² of ground-floor retail, and flexible self-finish (PSO) layouts. Completion Q4 2026.',
 ARRAY['10 storeys · 198 apartments','Beside Fuchika Park','1,500 m² ground-floor retail','Gated · 24/7 security','Self-finish (PSO) layouts'],
 'agent-4', 42.859, 74.648, TRUE, '2026-06-26T00:00:00Z');
