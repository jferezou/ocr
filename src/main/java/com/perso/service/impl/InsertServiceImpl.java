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
    private ParamRuchierDao paramRuchierDao;
    @Resource
    private ParamTypeDao paramTypeDao;
    @Resource
    private RucheDao rucheDao;
    @Resource
    private ParamFleursDao paramFleursDao;
    @Resource
    private PalynologieDocumentDao palynologieDocumentDao;
    @Resource
    private ResidusDocumentDao residusDocumentDao;

    @Override
    public void insertNewPalynologie(PalynologieDocument result) throws BddException {
        // enregistrer BDD
        String[] splitAppelationDemandeur = result.getAppelationDemandeur().split(" ");
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
                String ruchier = ident.substring(1,2);
                String ruche = ident.substring(2,6);

                Date date = spd.parse(dateS);

                palynodoc = new PalynologieDocumentEntity();
                palynodoc.setDate(date);
                palynodoc.setIdentifiant(matrice+ruchier);
                palynodoc.setIdentifiantEchantillon(ident);
                String echantillongString = result.getEchantillon().replace("\\n", "").replace(" ", "");
                palynodoc.setNumeroEchantillon(Long.parseLong(echantillongString));
                palynodoc.setPdfName(result.getPdfFileName());

                MatriceEntity matriceEntity = this.paramMatriceDao.findByIdentifiant(matrice);
                palynodoc.setMatrice(matriceEntity);
                RuchierEntity ruchierEntity = this.paramRuchierDao.findByCorrespondance(Integer.parseInt(ruchier));
                palynodoc.setRuchier(ruchierEntity);
                RuchesEntity ruchesEntity = getRuchesEntity(ruche);
                palynodoc.setRuche(ruchesEntity);
                palynodoc.setPalynologieList(new ArrayList<>());

                for(Palynologie palyno : result.getCompositions()) {
                    FleursEntity fleursEntity = this.paramFleursDao.findByName(palyno.getValue());
                    if(fleursEntity == null) {
                        LOGGER.warn("La fleurs n'existe pas, on la créé");
                        fleursEntity = new FleursEntity();
                        fleursEntity.setNom(palyno.getValue());
                        this.paramFleursDao.createFleur(fleursEntity);
                    }

                    TypeEntity type = this.paramTypeDao.findByName(palyno.getType().toUpperCase());

                    PalynologieEntity palynologieEntity = new PalynologieEntity();
                    palynologieEntity.setFleur(fleursEntity);
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
    }


    @Override
    public void insertNewResidus(ResidusDocument result) throws BddException{
        // enregistrer BDD
        String identifiant = result.getReference();
        String[] splitreference = identifiant.split("-");
        if(splitreference.length == 5) {
            try {
                String matrice = splitreference[0];
                String ruchier = splitreference[1];
                String ruche = splitreference[2];
                String dateS = splitreference[3];
                SimpleDateFormat spd = new SimpleDateFormat("dd/MM/yy");
                Date date = spd.parse(dateS);

                ResidusDocumentEntity residusDoc = this.residusDocumentDao.findByIdentifiant(identifiant);
                if(residusDoc != null) {
                    this.residusDocumentDao.deleteResidusDocument(residusDoc);
                }


                residusDoc = new ResidusDocumentEntity();
                residusDoc.setDate(date);
                residusDoc.setIdentifiant(identifiant);
                residusDoc.setCertificatAnalyse(residusDoc.getCertificatAnalyse());
                residusDoc.setPdfName(result.getPdfName());

                MatriceEntity matriceEntity = this.paramMatriceDao.findByIdentifiant(matrice);
                residusDoc.setMatrice(matriceEntity);
                RuchierEntity ruchierEntity = this.paramRuchierDao.findByCorrespondance(Integer.parseInt(ruchier));
                residusDoc.setRuchier(ruchierEntity);
                RuchesEntity ruchesEntity = getRuchesEntity(ruche);
                residusDoc.setRuche(ruchesEntity);
                residusDoc.setResidusGmsList(new ArrayList<>());

                for(Molecule molecule : result.getMoleculesGms()) {
                    MoleculesGmsEntity moleculeEntity = this.paramMoleculesGmsDao.findByName(molecule.getValue());
                    if(moleculeEntity == null) {
                        LOGGER.warn("La molecule n'existe pas, on la créé");
                        throw new BddException("La molécule GMS "+molecule.getValue()+" n'existe pas dans le référentiel");
                    }


                    ResidusGmsEntity residusGmsEntity = new ResidusGmsEntity();
                    residusGmsEntity.setMoleculeGms(moleculeEntity);
                    residusGmsEntity.setTaux(molecule.getPourcentage());
                    residusGmsEntity.setTrace(molecule.isTrace());
                    residusDoc.getResidusGmsList().add(residusGmsEntity);
                }


                for(Molecule molecule : result.getMoleculesGms()) {
                    MoleculesLmsEntity moleculeEntity = this.paramMoleculesLmsDao.findByName(molecule.getValue());
                    if(moleculeEntity == null) {
                        LOGGER.warn("La molecule n'existe pas, on la créé");
                        throw new BddException("La molécule LMS "+molecule.getValue()+" n'existe pas dans le référentiel");
                    }


                    ResidusLmsEntity residusLmsEntity = new ResidusLmsEntity();
                    residusLmsEntity.setMoleculeLms(moleculeEntity);
                    residusLmsEntity.setTaux(molecule.getPourcentage());
                    residusLmsEntity.setTrace(molecule.isTrace());
                    residusDoc.getResidusLmsList().add(residusLmsEntity);
                }
                this.residusDocumentDao.createResidusDocument(residusDoc);
            } catch (ParseException e) {
                LOGGER.error("Erreur de parsin de date",e);
            }
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
