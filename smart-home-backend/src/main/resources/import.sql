insert into PUBLIC.ROLE (ID, NAME) values ('9c4cf245-67c7-466e-9ff2-9878acef91cb', 'ADMIN');
insert into PUBLIC.ROLE (ID, NAME) values ('faf8829f-cb60-4365-b419-14f7b80afa4c', 'OWNER');
insert into PUBLIC.ROLE (ID, NAME) values ('ebc16e4e-64e1-4b35-946a-0885ab058453', 'TENANT');
insert into PUBLIC.ADMIN (ID, EMAIL, NAME, PASSWORD_HASH, ROLE_ID, SURNAME, VERIFIED) values ('201ab15f-3b22-4659-b839-235b9e6a728e', 'admin@gmail.com', 'Mico', '$2a$10$5tykbOhp3Uo2QrY2t3/uCOmsuwvEZl4KgEgZgN3At6JB3HQX.Z75y', '9c4cf245-67c7-466e-9ff2-9878acef91cb', 'Milic', true);