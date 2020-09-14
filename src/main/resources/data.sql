--初期ユーザ
INSERT INTO user (id, name, password, role)
VALUES ('d9bbf3c8-891b-4555-8c11-548d46d39f20', 'admin', '{bcrypt}$2a$10$tRRzbA3oBdBJ8GXpGAbVs.NpQ7/gWcCeoFhPMC7OvQtKh40/RYxU6','Administrator');
INSERT INTO user (id, name, password, role)
VALUES ('20efee93-c156-485e-ab57-715f03178fb0', 'user', '{bcrypt}$2a$10$tRRzbA3oBdBJ8GXpGAbVs.NpQ7/gWcCeoFhPMC7OvQtKh40/RYxU6','GeneralUser');
