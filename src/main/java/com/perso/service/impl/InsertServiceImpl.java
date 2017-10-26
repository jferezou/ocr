package com.perso.service.impl;

import com.perso.bdd.dao.*;
import com.perso.bdd.entity.*;
import com.perso.bdd.entity.parametrage.*;
import com.perso.exception.BddException;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.Molecule;
import com.perso.pojo.residus.ResidusDocument;
import com.perso.service.InsertService;
import com.perso.utils.StringUtilsOcr;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class InsertServiceImpl implements InsertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InsertServiceImpl.class);

    @Resource
    private ParamMoleculesGmsDao paramMoleculesGmsDao;

    @Resource
    private ParamMoleculesLmsDao paramMoleculesLmsDao;

    @Resource
    private ParamMatriceDao paramMatriceDao;
    @Resource
    private ParamContactDao paramContactDao;
    @Resource
    private ParamTypeDao paramTypeDao;
    @Resource
    private RucheDao rucheDao;
    @Resource
    private ParamEspeceDao paramEspeceDao;
    @Resource
    private PalynologieDocumentDao palynologieDocumentDao;
    @Resource
    private ResidusDocumentDao residusDocumentDao;

    @Override
    public void insertNewPalynologie(PalynologieDocument result) throws BddException {
        // enregistrer BDD
        String appelationDemandeur = result.getAppelationDemandeur();
        appelationDemandeur = StringUtils.stripStart(appelationDemandeur, " ");
        String[] splitAppelationDemandeur = appelationDemandeur.split(" ");
        if(splitAppelationDemandeur.length == 2) {
            try {
                String ident = splitAppelationDemandeur[0];

                PalynologieDocumentEntity palynodoc = this.palynologieDocumentDao.findByEchantillonId(ident);
                if(palynodoc != null) {
                    this.palynologieDocumentDao.deletePalynologieDocument(palynodoc);
                }

                String dateS = splitAppelationDemandeur[1];
                SimpleDateFormat spd = new SimpleDateFormat("dd/MM/yy");

                String matrice = ident.substring(0,1);
                String contact = ident.substring(1,2);
                String ruche = ident.substring(2,6);

                Date date = spd.parse(dateS);

                palynodoc = new PalynologieDocumentEntity();
                palynodoc.setDate(date);
                palynodoc.setIdentifiant(matrice+contact);
                palynodoc.setIdentifiantEchantillon(ident);
                String echantillongString = result.getEchantillon().replace("\\n", "").replace(" ", "").replace("/\n", "");
                try {
                    palynodoc.setNumeroEchantillon(Long.parseLong(echantillongString));
                } catch (NumberFormatException e) {
                    throw new BddException("Erreur sur l'échantillon : "+echantillongString);
                }

                String pdfTempName = result.getPdfFileName();
                pdfTempName = StringUtils.removeEnd(pdfTempName,".pdf");
                int lastUnderscoreIndex = StringUtilsOcr.getLastUnderscore(pdfTempName);
                palynodoc.setPdfName(pdfTempName.substring(0,lastUnderscoreIndex)+".pdf");
                palynodoc.setPdfPage(pdfTempName.substring(lastUnderscoreIndex+1,pdfTempName.length()));

                MatriceEntity matriceEntity = this.paramMatriceDao.findByIdentifiant(matrice);
                palynodoc.setMatrice(matriceEntity);
                ContactEntity contactEntity = this.paramContactDao.findByCorrespondance(Integer.parseInt(contact));
                palynodoc.setContact(contactEntity);
                RuchesEntity ruchesEntity = getRuchesEntity(ruche);
                palynodoc.setRuche(ruchesEntity);
                palynodoc.setPalynologieList(new ArrayList<>());

                for(Palynologie palyno : result.getCompositions()) {
                    // on recherche sans accents
                    EspeceEntity especeEntity = this.paramEspeceDao.findByName(StringUtils.stripAccents(palyno.getValue()));
                    if(especeEntity == null) {
                        // on recherche avec accents
                        especeEntity = this.paramEspeceDao.findByName(palyno.getValue());
                        if(especeEntity == null) {
                            LOGGER.warn("L'espèce n'existe pas, on la créé");
                            especeEntity = new EspeceEntity();
                            especeEntity.setNom(palyno.getValue());
                            this.paramEspeceDao.createEspece(especeEntity);
                        }
                    }

                    TypeEntity type = this.paramTypeDao.findByName(palyno.getType().toUpperCase());

                    PalynologieEntity palynologieEntity = new PalynologieEntity();
                    palynologieEntity.setEspece(especeEntity);
                    palynologieEntity.setPalynologieDocument(palynodoc);
                    palynologieEntity.setPourcentage(palyno.getPercentage());
                    palynologieEntity.setType(type);
                    palynodoc.getPalynologieList().add(palynologieEntity);
                }
                this.palynologieDocumentDao.createPalynologieDocument(palynodoc);
            } catch (ParseException e) {
                LOGGER.error("Erreur de parsin de date",e);
            }
        }
        else {
            throw new BddException("L'appelation demandeur n'est pas correcte : '"+appelationDemandeur+"'");
        }

    }


    @Override
    public void insertNewResidus(ResidusDocument result) throws BddException{
        // enregistrer BDD
        String identifiant = result.getReference();
        String[] splitreference = identifiant.split("-");
        if(splitreference.length == 5) {
            try {
                String matrice = splitreference[0];
                String contact = splitreference[1];
                String ruche = splitreference[2];
                String dateS = splitreference[3];
                SimpleDateFormat spd = new SimpleDateFormat("dd/MM/yy");
                Date date = spd.parse(dateS);

                ResidusDocumentEntity residusDoc = this.residusDocumentDao.findByIdentifiant(identifiant);
                if(residusDoc != null) {
                    this.residusDocumentDao.deleteResidusDocument(residusDoc);
                }


                residusDoc = new ResidusDocumentEntity();
                residusDoc.setResidusGmsList(new ArrayList<>());
                residusDoc.setResidusLmsList(new ArrayList<>());
                residusDoc.setDate(date);
                residusDoc.setIdentifiant(identifiant);
                residusDoc.setCertificatAnalyse(result.getCertificatAnalyses());
                residusDoc.setPdfName(result.getPdfName());
                residusDoc.setPoids(result.getPoids());

                MatriceEntity matriceEntity = this.paramMatriceDao.findByIdentifiant(matrice);
                residusDoc.setMatrice(matriceEntity);
                ContactEntity contactEntity = this.paramContactDao.findByCorrespondance(Integer.parseInt(contact));
                residusDoc.setContact(contactEntity);
                RuchesEntity ruchesEntity = getRuchesEntity(ruche);
                residusDoc.setRuche(ruchesEntity);
                residusDoc.setResidusGmsList(new ArrayList<>());

                for(Molecule molecule : result.getMoleculesGms()) {
                    MoleculesGmsEntity moleculeEntity = this.paramMoleculesGmsDao.findByName(molecule.getValue());

                    ResidusGmsEntity residusGmsEntity = new ResidusGmsEntity();
                    residusGmsEntity.setMoleculeGms(moleculeEntity);
                    residusGmsEntity.setTaux(molecule.getPourcentage());
                    residusGmsEntity.setTrace(molecule.isTrace());
                    residusGmsEntity.setLimite(molecule.getLimite());
                    residusGmsEntity.setResidusDocument(residusDoc);
                    residusDoc.getResidusGmsList().add(residusGmsEntity);
                }


                for(Molecule molecule : result.getMoleculesLms()) {
                    MoleculesLmsEntity moleculeEntity = this.paramMoleculesLmsDao.findByName(molecule.getValue());

                    ResidusLmsEntity residusLmsEntity = new ResidusLmsEntity();
                    residusLmsEntity.setMoleculeLms(moleculeEntity);
                    residusLmsEntity.setTaux(molecule.getPourcentage());
                    residusLmsEntity.setTrace(molecule.isTrace());
                    residusLmsEntity.setLimite(molecule.getLimite());
                    residusLmsEntity.setResidusDocument(residusDoc);
                    residusDoc.getResidusLmsList().add(residusLmsEntity);
                }
                this.residusDocumentDao.createResidusDocument(residusDoc);
            } catch (ParseException e) {
                LOGGER.error("Erreur de parsing de date",e);
            }
        }
        else {
            throw new BddException("L'identifiant n'est pas correct : '"+identifiant+"'");
        }

    }





    private RuchesEntity getRuchesEntity(String ruche) {
        RuchesEntity ruchesEntity = this.rucheDao.findRucheByName(ruche);
        if(ruchesEntity == null) {
            LOGGER.warn("La ruche n'existe pas, on la créé");
            ruchesEntity = new RuchesEntity();
            ruchesEntity.setNom(ruche);
            this.rucheDao.createRuche(ruchesEntity);
        }
        return ruchesEntity;
    }
}
