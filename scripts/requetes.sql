

select residus_doc.date, residus_doc.identifiant, residus_doc.certificat_analyse, residus_doc.poids, matrice.nom, ruche.nom, contact.nom, contact.prenom, contact.site, contact.departement, contact.region, contact.telephone
from residus_document residus_doc
  inner join param_matrice matrice on residus_doc.matrice_id = matrice.id
  inner join param_contact contact on residus_doc.contact_id = contact.id
  inner join ruches ruche on residus_doc.ruche_id = ruche.id
  inner join residus_gms gms on gms.residus_document_id=residus_doc.id
  inner join param_molecules_gms paramgms on gms.id_molecule_gms=paramgms.id
  inner join residus_lms lms on lms.residus_document_id=residus_doc.id
  inner join param_molecules_lms paramlms on lms.id_molecule_lms=paramlms.id;


select *
from residus_document residus_doc
  inner join param_matrice matrice on residus_doc.matrice_id = matrice.id
  inner join param_contact contact on residus_doc.contact_id = contact.id
  inner join ruches ruche on residus_doc.ruche_id = ruche.id
  inner join residus_gms gms on gms.residus_document_id=residus_doc.id
  inner join param_molecules_gms paramgms on gms.id_molecule_gms=paramgms.id
  inner join residus_lms lms on lms.residus_document_id=residus_doc.id
  inner join param_molecules_lms paramlms on lms.id_molecule_lms=paramlms.id;

select *
from residus_document residus_doc
  inner join param_matrice matrice on residus_doc.matrice_id = matrice.id
  inner join param_contact contact on residus_doc.contact_id = contact.id
  inner join ruches ruche on residus_doc.ruche_id = ruche.id
  inner join residus_gms gms on gms.residus_document_id=residus_doc.id
  inner join param_molecules_gms paramgms on gms.id_molecule_gms=paramgms.id;


select *
from palynologie_document palyno_doc
  inner join param_matrice matrice on palyno_doc.matrice_id = matrice.id
  inner join param_contact contact on palyno_doc.contact_id = contact.id
  inner join ruches ruche on palyno_doc.ruche_id = ruche.id
  inner join palynologie palyno on palyno.palynologie_document_id=palyno_doc.id
  inner join param_fleurs fleurs on palyno.id_fleur=fleurs.id
  left outer join param_espece espece on fleurs.espece_id=espece.id
  left outer join param_famille famille on espece.famille_id=famille.id;