CREATE TABLE Korisnik (
    id_korisnik BIGINT PRIMARY KEY,
    ime VARCHAR(255),
    prezime VARCHAR(255),
    email VARCHAR(255),
    lozinka VARCHAR(255)
);

CREATE TABLE Administrator (
    id_korisnik BIGINT PRIMARY KEY,
    datum_zaposlenja DATE,
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik)
);

CREATE TABLE Clan_sportskog_centra (
    id_korisnik BIGINT PRIMARY KEY,
    status_clanstva VARCHAR(255),
    datum_uclanjenja DATE,
    datum_zavrsetka_clanstva DATE,
    broj_izostanaka INT,
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik)
);

CREATE TABLE Zaposlenik_sportskog_centra (
    id_korisnik BIGINT PRIMARY KEY,
    datum_zaposlenja DATE,
    pozicija VARCHAR(255),
    odjel VARCHAR(255),
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik)
);

CREATE TABLE Aktivnost (
    id_aktivnost BIGINT PRIMARY KEY,
    naziv VARCHAR(255),
    opis TEXT,
    cijena_po_satu FLOAT,
    id_korisnik BIGINT,
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik)
);

CREATE TABLE Rezervacija (
    id_rezervacija BIGINT PRIMARY KEY,
    datum_vrijeme_pocetka TIMESTAMP,
    datum_vrijeme_zavrsetka TIMESTAMP,
    broj_sudionika INT,
    cijena_rezervacije FLOAT,
    id_korisnik BIGINT,
    id_aktivnost BIGINT,
    id_status BIGINT,
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik),
    FOREIGN KEY (id_aktivnost) REFERENCES Aktivnost(id_aktivnost),
    FOREIGN KEY (id_status) REFERENCES Status(id_status)
);

CREATE TABLE Obavijest (
    id_obavijest BIGINT PRIMARY KEY,
    naslov VARCHAR(255),
    tekst TEXT,
    datum_vrijeme TIMESTAMP,
    id_korisnik BIGINT,
    FOREIGN KEY (id_korisnik) REFERENCES Korisnik(id_korisnik)
);

CREATE TABLE Status (
    id_status BIGINT PRIMARY KEY,
    naziv VARCHAR(255)
);
