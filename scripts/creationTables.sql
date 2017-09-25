
CREATE SEQUENCE seq_param_matrice_id;
GRANT usage on SEQUENCE seq_param_matrice_id to usrocr;


CREATE TABLE param_matrice
(
  id		            			INT8			NOT NULL default nextval('seq_param_matrice_id'),
  nom						  		VARCHAR(200)	NOT NULL,
  identifiant						VARCHAR(1)		NOT NULL,

  CONSTRAINT pk_param_matrice PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_matrice TO usrocr;

COMMENT ON TABLE param_matrice IS 'Cette table le paramétrage pour les matrices.';

COMMENT ON COLUMN param_matrice.nom IS 'Nom de la matrice';
COMMENT ON COLUMN param_matrice.identifiant IS 'Identifiant du nom';





CREATE SEQUENCE seq_param_ruchier_id;
GRANT usage on SEQUENCE seq_param_ruchier_id to usrocr;
CREATE TABLE param_ruchier
(
  id		            			INT8			NOT NULL default nextval('seq_param_ruchier_id'),
  nom						  		VARCHAR(200)	NOT NULL,
  prenom							VARCHAR(200)	NOT NULL,
  site								VARCHAR(200)	NULL,
  departement						INT4			NULL,
  region							VARCHAR(200)	NULL,
  telephone							VARCHAR(200)	NULL,
  correspondance       INT4 NOT NULL,

  CONSTRAINT pk_param_ruchier PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_ruchier TO usrocr;

COMMENT ON TABLE param_ruchier IS 'Cette table le paramétrage pour les ruchiers.';



CREATE SEQUENCE seq_param_fleurs_id;
GRANT usage on SEQUENCE seq_param_fleurs_id to usrocr;
CREATE TABLE param_fleurs
(
  id		            			INT8			NOT NULL default nextval('seq_param_fleurs_id'),
  nom						  		VARCHAR(200)	NOT NULL,

  CONSTRAINT pk_param_fleurs PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_fleurs TO usrocr;

COMMENT ON TABLE param_fleurs IS 'Cette table le paramétrage pour les fleurs.';


CREATE SEQUENCE seq_param_molecules_gms_id;
GRANT usage on SEQUENCE seq_param_molecules_gms_id to usrocr;
CREATE TABLE param_molecules_gms
(
  id		            			INT8			NOT NULL default nextval('seq_param_molecules_gms_id'),
  nom						  		VARCHAR(2000)	NOT NULL,
  valeurTrace						float8			NOT NULL,

  CONSTRAINT pk_param_molecules_gms PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_molecules_gms TO usrocr;

COMMENT ON TABLE param_molecules_gms IS 'Cette table le paramétrage pour les molécules GMS.';


CREATE SEQUENCE seq_param_molecules_lms_id;
GRANT usage on SEQUENCE seq_param_molecules_lms_id to usrocr;
CREATE TABLE param_molecules_lms
(
  id		            			INT8			NOT NULL default nextval('seq_param_molecules_lms_id'),
  nom						  		VARCHAR(2000)	NOT NULL,
  valeurTrace						float8			NOT NULL,

  CONSTRAINT pk_param_molecules_lms PRIMARY KEY (id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_molecules_lms TO usrocr;

COMMENT ON TABLE param_molecules_lms IS 'Cette table le paramétrage pour les molécules LMS.';