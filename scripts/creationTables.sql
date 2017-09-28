-- #######################################################################################################
-- CREATION TABLE : param_matrice
-- #######################################################################################################

CREATE SEQUENCE seq_param_matrice_id;
GRANT usage on SEQUENCE seq_param_matrice_id to usrocr;


CREATE TABLE param_matrice
(
  id		            			INT8			NOT NULL default nextval('seq_param_matrice_id'),
  nom						  		VARCHAR(200)	NOT NULL,
  identifiant						VARCHAR(1)		NOT NULL,

  CONSTRAINT pk_param_matrice PRIMARY KEY (id),
  CONSTRAINT uk_param_matrice_identifiant UNIQUE (identifiant)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_matrice TO usrocr;

COMMENT ON TABLE param_matrice IS 'Cette table le paramétrage pour les matrices.';

COMMENT ON COLUMN param_matrice.nom IS 'Nom de la matrice';
COMMENT ON COLUMN param_matrice.identifiant IS 'Identifiant du nom';




-- #######################################################################################################
-- CREATION TABLE : param_contact
-- #######################################################################################################

CREATE SEQUENCE seq_param_contact_id;
GRANT usage on SEQUENCE seq_param_contact_id to usrocr;
CREATE TABLE param_contact
(
  id		            			INT8			NOT NULL default nextval('seq_param_contact_id'),
  nom						  		VARCHAR(200)	NOT NULL,
  prenom							VARCHAR(200)	NOT NULL,
  site								VARCHAR(200)	NULL,
  departement						INT4			NULL,
  region							VARCHAR(200)	NULL,
  telephone							VARCHAR(200)	NULL,
  correspondance       INT4 NOT NULL,

  CONSTRAINT pk_param_contact PRIMARY KEY (id),
  CONSTRAINT uk_param_contact_correspondancet UNIQUE (correspondance)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_contact TO usrocr;

COMMENT ON TABLE param_contact IS 'Cette table le paramétrage pour les contacts.';




-- #######################################################################################################
-- CREATION TABLE : param_molecules_gms
-- #######################################################################################################

CREATE SEQUENCE seq_param_molecules_gms_id;
GRANT usage on SEQUENCE seq_param_molecules_gms_id to usrocr;
CREATE TABLE param_molecules_gms
(
  id		            			INT8			NOT NULL default nextval('seq_param_molecules_gms_id'),
  nom						  		VARCHAR(2000)	NOT NULL,
  valeurTrace						float8			NOT NULL,

  CONSTRAINT pk_param_molecules_gms PRIMARY KEY (id),
  CONSTRAINT uk_param_molecules_gms_nom UNIQUE (nom)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_molecules_gms TO usrocr;

COMMENT ON TABLE param_molecules_gms IS 'Cette table le paramétrage pour les molécules GMS.';




-- #######################################################################################################
-- CREATION TABLE : param_molecules_lms
-- #######################################################################################################

CREATE SEQUENCE seq_param_molecules_lms_id;
GRANT usage on SEQUENCE seq_param_molecules_lms_id to usrocr;
CREATE TABLE param_molecules_lms
(
  id		            			INT8			NOT NULL default nextval('seq_param_molecules_lms_id'),
  nom						  		VARCHAR(2000)	NOT NULL,
  valeurTrace						float8			NOT NULL,

  CONSTRAINT pk_param_molecules_lms PRIMARY KEY (id),
  CONSTRAINT uk_param_molecules_lms_nom UNIQUE (nom)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_molecules_lms TO usrocr;

COMMENT ON TABLE param_molecules_lms IS 'Cette table le paramétrage pour les molécules LMS.';




-- #######################################################################################################
-- CREATION TABLE : ruches
-- #######################################################################################################

CREATE SEQUENCE seq_ruches_id;
GRANT usage on SEQUENCE seq_ruches_id to usrocr;
CREATE TABLE ruches
(
  id		            		INT8			  NOT NULL default nextval('seq_ruches_id'),
  nom						  		  VARCHAR(10)	NOT NULL,

  CONSTRAINT pk_ruches PRIMARY KEY (id),
  CONSTRAINT uk_ruches_nom UNIQUE (nom)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ruches TO usrocr;

COMMENT ON TABLE ruches IS 'Liste des ruches.';




-- #######################################################################################################
-- CREATION TABLE : param_type
-- #######################################################################################################

CREATE SEQUENCE seq_param_type_id;
GRANT usage on SEQUENCE seq_param_type_id to usrocr;
CREATE TABLE param_type
(
  id		            		INT8			  NOT NULL default nextval('seq_param_type_id'),
  valeur						  	VARCHAR(200)	  NOT NULL,

  CONSTRAINT pk_param_type PRIMARY KEY (id),
  CONSTRAINT uk_param_type_valeur UNIQUE (valeur)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_type TO usrocr;

COMMENT ON TABLE param_type IS 'Parametrage type.';






-- #######################################################################################################
-- CREATION TABLE : param_famille
-- #######################################################################################################

CREATE SEQUENCE seq_param_famille_id;
GRANT usage on SEQUENCE seq_param_famille_id to usrocr;
CREATE TABLE param_famille
(
  id		            		INT8			  NOT NULL default nextval('seq_param_famille_id'),
  nom						  	VARCHAR(200)	  NOT NULL,

  CONSTRAINT pk_param_famille PRIMARY KEY (id),
  CONSTRAINT uk_famille_nom UNIQUE (nom)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_famille TO usrocr;

COMMENT ON TABLE param_famille IS 'Paramétrage famille fleurs.';




-- #######################################################################################################
-- CREATION TABLE : param_espece
-- #######################################################################################################

CREATE SEQUENCE seq_param_espece_id;
GRANT usage on SEQUENCE seq_param_espece_id to usrocr;
CREATE TABLE param_espece
(
  id		            		INT8			  NOT NULL default nextval('seq_param_espece_id'),
  nom						  	VARCHAR(200)	  NOT NULL,
  famille_id						  	INT8	  NOT NULL,

  CONSTRAINT pk_param_espece PRIMARY KEY (id),
  CONSTRAINT uk_espece_nom UNIQUE (nom),
  CONSTRAINT fk_param_famille foreign key (famille_id) REFERENCES param_famille(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_espece TO usrocr;

COMMENT ON TABLE param_espece IS 'Paramétrage especes fleurs.';




-- #######################################################################################################
-- CREATION TABLE : param_fleurs
-- #######################################################################################################

CREATE SEQUENCE seq_param_fleurs_id;
GRANT usage on SEQUENCE seq_param_fleurs_id to usrocr;
CREATE TABLE param_fleurs
(
  id		            			INT8			NOT NULL default nextval('seq_param_fleurs_id'),
  nom						  		VARCHAR(200)	NOT NULL,
  nom2						  		VARCHAR(200)	NULL,
  espece_id						  	INT8	  NULL,

  CONSTRAINT pk_param_fleurs PRIMARY KEY (id),
  CONSTRAINT uk_fleur_nom UNIQUE (nom),
  CONSTRAINT fk_param_espece foreign key (espece_id) REFERENCES param_espece(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE param_fleurs TO usrocr;

COMMENT ON TABLE param_fleurs IS 'Cette table le paramétrage pour les fleurs.';


-- #######################################################################################################
-- CREATION TABLE : residus_document
-- #######################################################################################################

CREATE SEQUENCE seq_residus_document_id;
GRANT usage on SEQUENCE seq_residus_document_id to usrocr;
CREATE TABLE residus_document
(
  id		            			INT8			NOT NULL default nextval('seq_residus_document_id'),
  date						  		TIMESTAMP 	NOT NULL,
  identifiant						  		VARCHAR(200)	NOT NULL,
  certificat_analyse						  		VARCHAR(200)	NOT NULL,
  poids						  	float8	  NOT NULL,
  matrice_id						  	INT8	  NOT NULL,
  contact_id						  	INT8	  NOT NULL,
  ruche_id						  	INT8	  NOT NULL,
  pdf_name						  	VARCHAR(200)	  NULL,

  CONSTRAINT pk_residus_document_id PRIMARY KEY (id),
  CONSTRAINT uk_residus_document_identifiant UNIQUE (identifiant),
  CONSTRAINT fk_residus_document_matrice foreign key (matrice_id) REFERENCES param_espece(id),
  CONSTRAINT fk_residus_document_contact foreign key (contact_id) REFERENCES param_contact(id),
  CONSTRAINT fk_residus_document_ruche foreign key (ruche_id) REFERENCES ruches(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE residus_document TO usrocr;

COMMENT ON TABLE residus_document IS 'documents residus';




-- #######################################################################################################
-- CREATION TABLE : palynologie_document
-- #######################################################################################################

CREATE SEQUENCE seq_palynologie_document_id;
GRANT usage on SEQUENCE seq_palynologie_document_id to usrocr;
CREATE TABLE palynologie_document
(
  id		            			  INT8			NOT NULL default nextval('seq_palynologie_document_id'),
  date						  		    TIMESTAMP 	NOT NULL,
  identifiant						  	VARCHAR(20)	NOT NULL,
  identifiant_echantillon  	VARCHAR(50)	NOT NULL,
  numero_echantillon        INT8	  NOT NULL,
  matrice_id						  	INT8	  NOT NULL,
  contact_id						  	INT8	  NOT NULL,
  ruche_id						  	  INT8	  NOT NULL,
  pdf_name						  	  VARCHAR(200)	  NULL,

  CONSTRAINT pk_palynologie_document_id PRIMARY KEY (id),
  CONSTRAINT uk_palynologie_document_identifiant UNIQUE (identifiant_echantillon),
  CONSTRAINT fk_palynologie_document_matrice foreign key (matrice_id) REFERENCES param_espece(id),
  CONSTRAINT fk_palynologie_document_contact foreign key (contact_id) REFERENCES param_contact(id),
  CONSTRAINT fk_palynologie_document_ruche foreign key (ruche_id) REFERENCES ruches(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE palynologie_document TO usrocr;

COMMENT ON TABLE palynologie_document IS 'documents palynologie';






-- #######################################################################################################
-- CREATION TABLE : palynologie
-- #######################################################################################################

CREATE SEQUENCE seq_palynologie_id;
GRANT usage on SEQUENCE seq_palynologie_id to usrocr;
CREATE TABLE palynologie
(
  id		            		INT8			  NOT NULL default nextval('seq_palynologie_id'),
  pourcentage						float8	  NOT NULL,
  id_fleur  						INT8			  NOT NULL,
  id_type               INT8       NOT NULL,
  palynologie_document_id            INT8       NOT NULL,

  CONSTRAINT pk_palynologie PRIMARY KEY (id),
  CONSTRAINT fk_fleur foreign key (id_fleur) REFERENCES param_fleurs(id),
  CONSTRAINT fk_type foreign key (id_type) REFERENCES param_type(id),
  CONSTRAINT fk_palynologie_palynologie_document foreign key (palynologie_document_id) REFERENCES palynologie_document(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE palynologie TO usrocr;

COMMENT ON TABLE palynologie IS 'Palynologie.';




-- #######################################################################################################
-- CREATION TABLE : residus_gms
-- #######################################################################################################

CREATE SEQUENCE seq_residus_gms_id;
GRANT usage on SEQUENCE seq_residus_gms_id to usrocr;
CREATE TABLE residus_gms
(
  id		            		INT8			  NOT NULL default nextval('seq_residus_gms_id'),
  taux						  	float8	  NULL,
  id_molecule_gms  						INT8			  NOT NULL,
  trace  						BOOLEAN			  NOT NULL,
  residus_document_id INT8			  NOT NULL,

  CONSTRAINT pk_residus_gms PRIMARY KEY (id),
  CONSTRAINT fk_residus_gms foreign key (id_molecule_gms) REFERENCES param_molecules_gms(id),
  CONSTRAINT fk_residus_gms_residus_document foreign key (residus_document_id) REFERENCES residus_document(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE residus_gms TO usrocr;

COMMENT ON TABLE residus_gms IS 'Residus GMS.';




-- #######################################################################################################
-- CREATION TABLE : residus_lms
-- #######################################################################################################

CREATE SEQUENCE seq_residus_lms_id;
GRANT usage on SEQUENCE seq_residus_lms_id to usrocr;
CREATE TABLE residus_lms
(
  id		            		INT8			  NOT NULL default nextval('seq_residus_lms_id'),
  taux						  	float8	  NULL,
  id_molecule_lms  						INT8			  NOT NULL,
  trace  						BOOLEAN			  NOT NULL,
  residus_document_id INT8			  NOT NULL,

  CONSTRAINT pk_residus_lms PRIMARY KEY (id),
  CONSTRAINT fk_residus_lms foreign key (id_molecule_lms) REFERENCES param_molecules_lms(id),
  CONSTRAINT fk_residus_lms_residus_document foreign key (residus_document_id) REFERENCES residus_document(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE residus_lms TO usrocr;

COMMENT ON TABLE residus_lms IS 'Residus LMS.';
