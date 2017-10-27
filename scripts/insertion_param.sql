-- #######################################################################################################
-- INSERTION TABLE : param_matrice
-- #######################################################################################################

INSERT INTO param_matrice (nom, identifiant) VALUES('Pollen', 'P');
INSERT INTO param_matrice (nom, identifiant) VALUES('Miel', 'M');



-- #######################################################################################################
-- INSERTION TABLE : param_type_residus
-- #######################################################################################################

INSERT INTO param_type_residus (nom) VALUES ('');
INSERT INTO param_type_residus (nom) VALUES ('Herbicide');
INSERT INTO param_type_residus (nom) VALUES ('Fongicide');
INSERT INTO param_type_residus (nom) VALUES ('Insecticide');
INSERT INTO param_type_residus (nom) VALUES ('Acaricide');
INSERT INTO param_type_residus (nom) VALUES ('Régulateur de croissance');
INSERT INTO param_type_residus (nom) VALUES ('Répulsif insectes');




-- #######################################################################################################
-- INSERTION TABLE : param_contact
-- #######################################################################################################

INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('Julien','Delaunay','St-aubin-la-plaine',85,'Vendée','',0);
INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('David','Sowtys','Tavers',45,'Loiret','',1);
INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('Eric','Lelong','Aspiran',34,'Hérault','', 2);
INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('Pascal','Turani','Bourideys',33,'Gironde','',3);
INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('Florent','Vacher','Tavers',45,'Loiret','',4);
INSERT INTO param_contact (prenom, nom, site, departement, region, telephone, correspondance) VALUES('Jean-François','Maréchal','Tilloy-Bellay',51,'Marne','',5);




-- #######################################################################################################
-- INSERTION TABLE : param_type_residus
-- #######################################################################################################

INSERT INTO param_type (valeur) VALUES ('DOMINANT');
INSERT INTO param_type (valeur) VALUES ('ACCOMPAGNEMENT');
INSERT INTO param_type (valeur) VALUES ('ISOLE');

commit;