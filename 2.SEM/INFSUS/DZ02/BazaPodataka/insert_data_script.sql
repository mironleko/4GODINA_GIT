INSERT INTO Korisnik (id_korisnik, ime, prezime, email, lozinka) VALUES 
(1, 'Ivana', 'Horvat', 'ivana.horvat@email.com', 'lozinka123'),
(2, 'Marko', 'Marić', 'marko.maric@email.com', 'lozinka321'),
(3, 'Ana', 'Kovačević', 'ana.kovacevic@email.com', 'lozinkaaa');

INSERT INTO Administrator (id_korisnik, datum_zaposlenja) VALUES 
(1, '2022-01-10');

INSERT INTO Clan_sportskog_centra (id_korisnik, status_clanstva, datum_uclanjenja, datum_zavrsetka_clanstva, broj_izostanaka) VALUES 
(2, 'aktivan', '2022-01-15', '2023-01-15', 0);

INSERT INTO Zaposlenik_sportskog_centra (id_korisnik, datum_zaposlenja, pozicija, odjel) VALUES 
(3, '2022-05-01', 'Voditelj recepcije', 'Administracija');

INSERT INTO Aktivnost (id_aktivnost, naziv, opis, cijena_po_satu, id_korisnik) VALUES 
(1, 'Nogomet', 'Nogometna utakmica na otvorenom', 120.0, 2);

INSERT INTO Status (id_status, naziv) VALUES 
(1, 'Potvrđena'),
(2, 'Otkazana'),
(3, 'Završena');

INSERT INTO Rezervacija (id_rezervacija, datum_vrijeme_pocetka, datum_vrijeme_zavrsetka, broj_sudionika, cijena_rezervacije, id_korisnik, id_aktivnost, id_status) VALUES 
(1, '2024-04-15 16:00:00', '2024-04-15 17:30:00', 22, 180.0, 2, 1, 1);

INSERT INTO Obavijest (id_obavijest, naslov, tekst, datum_vrijeme, id_korisnik) VALUES 
(1, 'Zatvaranje centra za praznike', 'Sportski centar bit će zatvoren za vrijeme državnih praznika.', '2022-12-23 12:00:00', 3)
