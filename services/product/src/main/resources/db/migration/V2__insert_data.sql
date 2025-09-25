-- V2__insert_data.sql
-- Seed categories & products with plenty of sample rows.
-- Assumes schema:
--   category(id PK, name, description)
--   product(id PK, name, description, available_quantity, discount [0..1], price, category_id FK)
-- Sequences:
--   category_seq, product_seq (allocationSize 50 in app)

-- =========================
-- Categories
-- =========================
INSERT INTO category (id, name, description) VALUES
                                                 (1,   'Electronics',             'Phones, laptops, wearables, and accessories'),
                                                 (51,  'Computers & Laptops',     'Notebooks, desktops, components, and peripherals'),
                                                 (101, 'Smart Home',              'Home automation and smart devices'),
                                                 (151, 'Audio',                   'Headphones, speakers, microphones'),
                                                 (201, 'Gaming',                  'Consoles, games, and accessories'),
                                                 (251, 'Cameras & Photo',         'Cameras, lenses, action cams, drones'),
                                                 (301, 'TV & Home Theater',       'Televisions, projectors, media players'),
                                                 (351, 'Appliances',              'Small and large household appliances'),
                                                 (401, 'Health & Fitness',        'Wearables, scales, massage, fitness gear'),
                                                 (451, 'Books',                   'Fiction, non-fiction, and textbooks'),
                                                 (501, 'Toys',                    'Educational and fun toys'),
                                                 (551, 'Office Supplies',         'Stationery, printers, and consumables'),
                                                 (601, 'Networking',              'Routers, switches, Wi-Fi systems'),
                                                 (651, 'Storage',                 'SSDs, HDDs, memory cards, NAS'),
                                                 (701, 'Mobile Accessories',      'Chargers, cases, cables, power banks'),
                                                 (751, 'Wearables',               'Smartwatches and fitness trackers'),
                                                 (801, 'Kitchen',                 'Cookware, coffee, and food prep'),
                                                 (851, 'Outdoors',                'Camping, lighting, and tools'),
                                                 (901, 'Pet Supplies',            'Pets’ food and accessories'),
                                                 (951, 'Automotive',              'Car electronics and accessories');

-- =========================
-- Products
-- =========================
-- Note: discounts are fractions [0..1], e.g. 0.150 = 15%
-- Prices have 2 decimals; available_quantity is stock on hand.

INSERT INTO product (id, name, description, available_quantity, discount, price, category_id) VALUES
-- Electronics (1)
(1,   'Smartphone Nova X',                 '6.7" OLED, 256GB, 5G',                               120, 0.150,  799.00, 1),
(2,   'Wireless Earbuds AirGo',            'ANC, Bluetooth 5.3, 24h battery',                    300, 0.100,  129.90, 1),
(3,   'Portable Charger 20K',              '20,000mAh PD 30W power bank',                        450, 0.050,   39.99, 1),
(4,   'USB-C Wall Charger 65W',            'GaN compact fast charger',                           500, 0.000,   29.99, 1),
(5,   'E-Ink Reader Lite',                 '6" glare-free e-ink, front light',                   180, 0.120,  119.00, 1),
(6,   'Tablet Orion 11',                   '11" IPS, 8GB/128GB, Wi-Fi 6',                        140, 0.080,  349.00, 1),

-- Computers & Laptops (51)
(51,  'Ultrabook Aero 14',                 'Intel i7, 16GB, 512GB SSD, 14" 2.8K',                 60, 0.150, 1299.00, 51),
(52,  'Mechanical Keyboard TKL',           'Hot-swap, RGB, red switches',                        220, 0.100,   89.00, 51),
(53,  'Ergo Wireless Mouse',               'Silent clicks, 2.4G + BT, 800/1600/2400 DPI',        320, 0.050,   29.50, 51),
(54,  'USB-C Hub 8-in-1',                  'HDMI 4K, PD, SD/mSD, 3xUSB',                         280, 0.000,   49.90, 51),
(55,  '27" 2K Monitor Pro',                '2560x1440, 75Hz, IPS',                               110, 0.120,  219.00, 51),
(56,  'Laptop Stand Aluminum',             'Foldable, height adjustable',                        260, 0.080,   34.90, 51),

-- Smart Home (101)
(101, 'Smart Bulb E27 (2-Pack)',           'RGB + warm/cool white, Wi-Fi',                       500, 0.150,   24.99, 101),
(102, 'Smart Plug Mini',                   'Energy monitoring, voice assistant compatible',      450, 0.100,   14.99, 101),
(103, 'Smart Thermostat',                  'Weekly schedules, app control',                      120, 0.050,  129.00, 101),
(104, 'Indoor Security Cam 2K',            'Motion alerts, night vision',                        200, 0.000,   39.99, 101),
(105, 'Video Doorbell',                    '1080p, two-way audio, battery',                      140, 0.100,   89.99, 101),
(106, 'Robot Vacuum S10',                  'Lidar, multi-floor maps, mopping',                    90, 0.120,  329.00, 101),

-- Audio (151)
(151, 'Over-Ear Headphones Studio',        'ANC, 40h battery, BT5.3',                            180, 0.150,  179.00, 151),
(152, 'Portable Speaker Go',               'IPX7 waterproof, 15W',                               260, 0.100,   49.99, 151),
(153, 'USB Microphone Cardioid',           'Plug-and-play, monitoring',                          150, 0.050,   59.00, 151),
(154, 'Hi-Res DAC USB-C',                  'Balanced output, low noise',                         120, 0.000,   69.00, 151),
(155, 'True Wireless Sport',               'Earhooks, sweat-resistant',                          220, 0.120,   79.90, 151),
(156, 'Soundbar 2.1 Compact',              'HDMI ARC, wireless sub',                             100, 0.080,  199.00, 151),

-- Gaming (201)
(201, 'Console Gen-S 1TB',                 '4K gaming, VRR, HDR',                                 70, 0.100,  499.00, 201),
(202, 'Pro Controller Wireless',           'Hall-effect sticks, remappable',                     180, 0.050,   69.90, 201),
(203, 'Gaming Headset 7.1',                'Surround, noise-cancel mic',                         200, 0.120,   59.99, 201),
(204, 'Mechanical Keyboard 60%',           'Optical switches, PBT keycaps',                      150, 0.100,   99.00, 201),
(205, 'Mouse Pad XL RGB',                  '900x400mm, stitched edges',                          260, 0.000,   29.99, 201),
(206, 'Racing Wheel Set',                   'Force feedback, pedals',                              60, 0.150,  299.00, 201),

-- Cameras & Photo (251)
(251, 'Mirrorless Camera 24MP',            '4K30, IBIS, kit lens 16-50',                         80,  0.100,  899.00, 251),
(252, 'Action Cam 5K',                     'HyperSmooth, waterproof',                            140, 0.050,  349.00, 251),
(253, 'Drone 4K Mini',                     '249g, GPS, 31-min flight',                           90,  0.120,  459.00, 251),
(254, 'Tripod Carbon Travel',              'Lightweight, 155cm',                                 160, 0.000,  129.00, 251),
(255, 'SDXC 256GB V60',                    'UHS-II, high speed',                                 300, 0.080,   89.00, 251),
(256, 'Camera Backpack 20L',               'Modular dividers, rain cover',                       180, 0.100,   79.90, 251),

-- TV & Home Theater (301)
(301, '55" 4K QLED TV',                    '120Hz, HDR10+, Dolby Vision',                         70, 0.120,  799.00, 301),
(302, '4K Streaming Stick',                'Dolby Atmos, voice remote',                          260, 0.100,   49.99, 301),
(303, 'Projector 1080p Short-Throw',       '3000 lm, low latency',                                90, 0.080,  499.00, 301),
(304, 'AV Receiver 7.2',                   'eARC, 8K pass-through',                               60, 0.150,  699.00, 301),
(305, 'TV Wall Mount Full-Motion',         '32-75", VESA 600x400',                               220, 0.050,   39.90, 301),
(306, 'Calibration Colorimeter',           'Display calibration tool',                             50, 0.000,  149.00, 301),

-- Appliances (351)
(351, 'Air Fryer 5.5L',                    'Digital presets, non-stick basket',                  180, 0.120,  119.00, 351),
(352, 'Robot Mop',                          'Auto-water refill, mapping',                          70, 0.100,  279.00, 351),
(353, 'Cordless Vacuum',                   'High suction, 45-min runtime',                       140, 0.080,  229.00, 351),
(354, 'Espresso Machine',                  '15-bar pump, steam wand',                            110, 0.050,  199.00, 351),
(355, 'Electric Kettle 1.7L',              'Temp control, keep warm',                            260, 0.000,   39.99, 351),
(356, 'Blender Pro 1500W',                 'Multi-speed, 2L jar',                                130, 0.100,   99.90, 351),

-- Health & Fitness (401)
(401, 'Smart Scale Body+',                  'BMI, body fat, Wi-Fi sync',                          160, 0.120,   69.90, 401),
(402, 'Massage Gun Mini',                   '4 heads, quiet motor',                               220, 0.100,   79.00, 401),
(403, 'Foam Roller 3-in-1',                 'Trigger point & deep tissue',                        300, 0.050,   24.99, 401),
(404, 'Jump Rope Speed',                     'Adjustable, bearings',                               320, 0.000,   14.99, 401),
(405, 'Yoga Mat Pro 6mm',                    'Non-slip, eco-friendly',                             280, 0.080,   29.99, 401),
(406, 'Heart Rate Strap BT/ANT+',            'Accurate chest sensor',                              140, 0.120,   59.90, 401),

-- Books (451)
(451, 'Clean Code',                         'A Handbook of Agile Software Craftsmanship',         200, 0.150,   34.90, 451),
(452, 'Design Patterns',                    'Elements of Reusable OO Software',                   180, 0.100,   44.90, 451),
(453, 'Refactoring',                        'Improving the Design of Existing Code',              160, 0.050,   39.90, 451),
(454, 'Domain-Driven Design',               'Tackling Complexity in the Heart of Software',       140, 0.000,   49.90, 451),
(455, 'The Pragmatic Programmer',           'Your Journey to Mastery',                            190, 0.100,   37.90, 451),
(456, 'Working Effectively with Legacy Code','Michael Feathers classic',                          120, 0.120,   42.00, 451),

-- Toys (501)
(501, 'STEM Robotics Kit',                  'Build & program 12 models',                          140, 0.150,   89.00, 501),
(502, 'Magnetic Tiles 120-pc',              'Creative building set',                              200, 0.100,   59.90, 501),
(503, 'RC Car 1:16 Brushless',              'High speed, 2.4G remote',                           130, 0.050,   79.00, 501),
(504, 'Puzzle 2000-piece',                  'Premium cardboard, landscape',                       240, 0.000,   19.99, 501),
(505, 'Board Game Strategy',                '2–5 players, 60–90 min',                            180, 0.120,   44.90, 501),
(506, 'Building Bricks Classic 1500-pc',    'Compatible with major brands',                      160, 0.080,   49.90, 501),

-- Office Supplies (551)
(551, 'Inkjet Printer All-in-One',          'Print/Scan/Copy, Wi-Fi',                             90, 0.120,   99.00, 551),
(552, 'A4 Paper 80gsm (500)',               'Smooth white sheets',                                320, 0.050,    7.90, 551),
(553, 'Gel Pens (12-Pack)',                 '0.5mm, quick-dry',                                   280, 0.000,    6.99, 551),
(554, 'Office Chair Ergonomic',             'Lumbar support, mesh',                               120, 0.100,  159.00, 551),
(555, 'Desk Lamp LED',                      'Dimmable, USB charging',                             200, 0.100,   24.99, 551),
(556, 'Paper Shredder Cross-Cut',           '18-sheet, bin 25L',                                   80, 0.150,   99.90, 551),

-- Networking (601)
(601, 'Wi-Fi 6 Router AX3000',              'Dual-band, OFDMA',                                   180, 0.120,   89.00, 601),
(602, 'Mesh Wi-Fi System (3-Pack)',         'Seamless roaming, app setup',                        110, 0.100,  199.00, 601),
(603, 'Gigabit Switch 8-Port',              'Fanless, metal case',                                260, 0.050,   24.99, 601),
(604, 'PoE Switch 16-Port',                 '802.3af/at, rack-mount',                              70, 0.080,  229.00, 601),
(605, 'USB-C Ethernet Adapter 2.5G',        'Type-C to RJ45 2.5GbE',                              180, 0.000,   29.90, 601),
(606, 'Outdoor CPE 5GHz',                   'Long-range bridge',                                   60, 0.120,  149.00, 601),

-- Storage (651)
(651, 'NVMe SSD 1TB Gen4',                  'PCIe 4.0, 7000MB/s',                                 160, 0.150,   99.00, 651),
(652, 'Portable SSD 2TB',                   'USB-C 10Gbps',                                       120, 0.100,  149.00, 651),
(653, 'HDD 8TB 3.5"',                       'NAS/RAID rated',                                      90, 0.050,  169.00, 651),
(654, 'MicroSD 512GB',                      'A2, U3, V30',                                        220, 0.000,   39.90, 651),
(655, 'NAS 2-Bay Enclosure',                'Quad-core, 2GB RAM',                                  70, 0.120,  279.00, 651),
(656, 'USB Flash Drive 128GB',              'Metal body, keyring loop',                           300, 0.080,   12.99, 651),

-- Mobile Accessories (701)
(701, 'MagSafe Wireless Charger',           '15W fast charge',                                    200, 0.120,   29.99, 701),
(702, 'Aramid Fiber Case',                  'Slim & protective',                                  180, 0.100,   24.99, 701),
(703, 'Tempered Glass (2-Pack)',            '9H hardness, oleophobic',                            320, 0.050,    9.99, 701),
(704, 'Car Charger 45W PD',                 'Dual USB-C PD',                                      240, 0.080,   19.99, 701),
(705, 'Phone Grip & Stand',                 'Repositionable',                                     300, 0.000,    8.99, 701),
(706, 'Cable USB-C to C 2m',                '100W e-marker',                                      340, 0.120,   12.49, 701),

-- Wearables (751)
(751, 'Smartwatch Fit S',                   'SpO2, GPS, 7-day battery',                           140, 0.150,  149.00, 751),
(752, 'Fitness Tracker Lite',               'Sleep tracking, notifications',                      220, 0.100,   39.99, 751),
(753, 'Sport Band (3-Pack)',                'Breathable silicone',                                280, 0.050,   14.99, 751),
(754, 'Watch Charger Dock',                 'Magnetic, USB-C',                                    260, 0.080,   12.99, 751),
(755, 'Chest Strap Sensor PRO',             'BLE/ANT+, dual channel',                             120, 0.100,   69.00, 751),
(756, 'Smart Ring (Gen2)',                  'Temp & HRV trends, sleep',                            80, 0.120,  299.00, 751),

-- Kitchen (801)
(801, 'Barista Grinder Burr',               'Precision 40-step grind',                            100, 0.120,  169.00, 801),
(802, 'Gooseneck Kettle 1L',                'Pour-over, temp control',                            160, 0.100,   69.90, 801),
(803, 'Non-stick Pan 28cm',                 'PFOA-free, induction',                               220, 0.050,   29.99, 801),
(804, 'Chef Knife 8"',                      'German steel, full tang',                            180, 0.080,   39.99, 801),
(805, 'Sous Vide Stick',                    '1100W, precise control',                             100, 0.120,   99.00, 801),
(806, 'Air-tight Containers (10)',          'BPA-free, stackable',                                260, 0.000,   24.99, 801),

-- Outdoors (851)
(851, 'LED Camping Lantern',                'Rechargeable, 1000 lm',                              200, 0.120,   29.99, 851),
(852, 'Portable Power Station 600W',        'LiFePO4, 512Wh',                                      70, 0.100,  399.00, 851),
(853, 'Hiking Backpack 35L',                'Ventilated back, rain cover',                        140, 0.050,   79.00, 851),
(854, 'Water Filter Straw',                 '0.1μm hollow fiber',                                 220, 0.080,   19.99, 851),
(855, 'Headlamp 700 lm',                    'USB-C, lightweight',                                 240, 0.100,   24.99, 851),
(856, 'Titanium Spork (2-Pack)',            'Ultralight, durable',                                300, 0.000,   12.99, 851),

-- Pet Supplies (901)
(901, 'Smart Pet Feeder',                   'App schedule, portion control',                      120, 0.120,  129.00, 901),
(902, 'Cat Fountain 2.5L',                  'Triple-filter, quiet pump',                          180, 0.100,   29.99, 901),
(903, 'Dog Harness No-Pull',                'Reflective, padded',                                 220, 0.050,   24.99, 901),
(904, 'Pet Camera Treat Toss',              '1080p, two-way audio',                               100, 0.080,  139.00, 901),
(905, 'Chew Toys (5-Pack)',                 'Durable rubber',                                     260, 0.000,   19.99, 901),
(906, 'Litter Deodorizer',                  'Activated charcoal, long-lasting',                   280, 0.100,    9.99, 901),

-- Automotive (951)
(951, 'Dash Cam 2K',                        'Wide-angle, parking mode',                           140, 0.120,   79.00, 951),
(952, 'OBD2 Scanner BT',                    'Live data, fault codes',                             200, 0.100,   44.90, 951),
(953, 'Car Vacuum Handheld',                'Cordless, strong suction',                           220, 0.050,   39.99, 951),
(954, 'Magnetic Phone Mount',               '360° rotation, vent clip',                           320, 0.080,   12.99, 951),
(955, 'Tire Inflator Digital',              'Auto shut-off, 150 PSI',                             180, 0.100,   49.99, 951),
(956, 'Jump Starter 2000A',                 'Portable, power bank',                                 90, 0.150,  109.00, 951);

-- (More products to reach ~120 total)
INSERT INTO product (id, name, description, available_quantity, discount, price, category_id) VALUES
                                                                                                  (7,   'BT Speaker Mini Plus',               'Pocket size, 10W, stereo pair',                      310, 0.050,   29.99, 151),
                                                                                                  (8,   'Noise-Isolating IEMs',               'Detachable cable, MMCX',                             150, 0.100,   59.00, 151),
                                                                                                  (9,   'USB-C PD Power Strip',               '3 AC + 2 USB-C + 1 USB-A',                           180, 0.080,   39.99, 1),
                                                                                                  (10,  'BT Tracker (4-Pack)',                'Find keys/wallet via app',                           260, 0.120,   49.99, 1),
                                                                                                  (57,  'Laptop 15" Student',                 'i5, 8GB, 256GB SSD',                                  90, 0.100,  699.00, 51),
                                                                                                  (58,  'USB Webcam 1080p',                   'Auto light, dual mics',                              200, 0.050,   39.99, 51),
                                                                                                  (107, 'Smart Blind Motor',                   'Retrofit, schedules',                                160, 0.080,   59.90, 101),
                                                                                                  (108, 'Leak Sensor (3-Pack)',               'Water alerts, app',                                  220, 0.120,   39.90, 101),
                                                                                                  (157, 'Studio Mic Arm',                     'Spring suspension, desk clamp',                      180, 0.080,   29.99, 151),
                                                                                                  (158, 'BT Receiver for Car',                'AUX, hands-free calls',                              240, 0.050,   17.99, 151),
                                                                                                  (207, 'Gaming Chair XL',                     'Lumbar & neck pillows',                              110, 0.100,  189.00, 201),
                                                                                                  (208, 'NVMe Heatsink',                      'Keeps SSD cool',                                     260, 0.000,   12.99, 201),
                                                                                                  (257, 'Lens 50mm F1.8',                     'Fast prime, portrait',                               120, 0.120,  229.00, 251),
                                                                                                  (258, 'Gimbal 3-Axis',                      'Smart tracking, foldable',                           140, 0.100,   99.00, 251),
                                                                                                  (307, 'HDMI 2.1 Cable 2m',                  '48Gbps, 8K/60, 4K/120',                              400, 0.120,   14.99, 301),
                                                                                                  (308, 'TV Backlight LED',                   'Bias lighting, USB',                                 300, 0.100,   16.99, 301),
                                                                                                  (357, 'Air Purifier H13',                   'HEPA filter, 40m²',                                  130, 0.080,  129.00, 351),
                                                                                                  (358, 'Humidifier 4L',                      'Cool mist, auto-off',                                220, 0.050,   49.99, 351),
                                                                                                  (407, 'Kettlebell 16kg',                    'Cast iron, powder coat',                             100, 0.120,   59.00, 401),
                                                                                                  (408, 'Resistance Bands (5)',               'Latex, stackable',                                   280, 0.100,   19.99, 401),
                                                                                                  (457, 'Groovy Algorithms',                  'Fun data structures guide',                          160, 0.120,   29.90, 451),
                                                                                                  (458, 'Clean Architecture',                 'A Craftsman’s Guide to Software Structure',          140, 0.100,   44.90, 451),
                                                                                                  (507, 'Drone Coding Kit',                    'Learn to code flight paths',                         120, 0.100,   89.00, 501),
                                                                                                  (508, 'Marble Run 300-pc',                  'STEM toy, creative builds',                          180, 0.050,   39.90, 501),
                                                                                                  (557, 'Label Maker BT',                      'Thermal, smartphone app',                            140, 0.120,   39.90, 551),
                                                                                                  (558, 'Monitor Arm Single',                  'Gas spring, 27"',                                    180, 0.080,   49.90, 551),
                                                                                                  (607, 'SFP+ Module 10G',                    'Multimode 850nm',                                    160, 0.120,   29.90, 601),
                                                                                                  (608, 'Ethernet Cable Cat7 10m',            'Shielded, flat',                                     240, 0.100,   12.90, 601),
                                                                                                  (657, 'SATA SSD 1TB',                       '2.5", 560MB/s',                                      180, 0.150,   69.00, 651),
                                                                                                  (658, 'M.2 Enclosure USB-C',                '10Gbps tool-less',                                   200, 0.080,   24.90, 651),
                                                                                                  (707, 'Ring Holder',                         'Metal, low-profile',                                 320, 0.100,    7.99, 701),
                                                                                                  (708, 'Car Vent Mount Mag',                  'Strong magnets, compact',                            260, 0.050,   11.99, 701),
                                                                                                  (757, 'Screen Protector Watch',              '2-Pack TPU',                                         340, 0.120,    8.99, 751),
                                                                                                  (758, 'Nylon Sport Loop',                    'Breathable strap',                                   300, 0.080,   12.99, 751),
                                                                                                  (807, 'Spice Grinder',                        'Electric, stainless steel',                          140, 0.100,   29.90, 801),
                                                                                                  (808, 'Vacuum Sealer',                        'Food preservation system',                           100, 0.120,   79.00, 801),
                                                                                                  (857, 'Camping Stove',                        'Isobutane, ultralight',                              160, 0.100,   24.99, 851),
                                                                                                  (858, 'Folding Saw',                           'Pocket, aggressive teeth',                           180, 0.050,   19.99, 851),
                                                                                                  (907, 'Cat Scratcher',                         'Cardboard lounge',                                  220, 0.100,   16.99, 901),
                                                                                                  (908, 'Dog Treat Pouch',                       'Waist clip, waterproof',                            260, 0.120,   14.99, 901),
                                                                                                  (957, 'USB-C Car Charger 67W',                 'Dual-C, PPS',                                       220, 0.100,   24.99, 951),
                                                                                                  (958, 'Blind Spot Mirrors (2)',                'HD glass, adjustable',                              260, 0.050,    9.99, 951);

-- =========================
-- Align sequences to max IDs (so nextval is safely ahead)
-- =========================
-- Bump to MAX(id) + 50 to play nicely with allocationSize=50 patterns (even if not used directly)
SELECT setval('category_seq', COALESCE((SELECT MAX(id) FROM category), 0) + 50, true);
SELECT setval('product_seq',  COALESCE((SELECT MAX(id) FROM product),  0) + 50, true);
