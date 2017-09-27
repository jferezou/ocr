package com.perso.service.impl;

import com.perso.bdd.dao.*;
import com.perso.bdd.entity.PalynologieDocumentEntity;
import com.perso.bdd.entity.PalynologieEntity;
import com.perso.bdd.entity.RuchesEntity;
import com.perso.bdd.entity.parametrage.FleursEntity;
import com.perso.bdd.entity.parametrage.MatriceEntity;
import com.perso.bdd.entity.parametrage.RuchierEntity;
import com.perso.bdd.entity.parametrage.TypeEntity;
import com.perso.exception.ParsingException;
import com.perso.service.UpdatedValuesService;
import com.perso.utils.*;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.Molecule;
import com.perso.pojo.residus.ResidusDocument;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Getter
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, PalynologieDocument> valeursPalynologie = new HashMap<>();
    private Map<Integer, ResidusDocument> valeursResidus = new HashMap<>();

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

    @Override
    public void parseAndSavePalynologie(final String value) throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);
        List<FleursEntity> listeFleursEntity = this.paramFleursDao.getAllFleurs();
        List<String> listeFleurs = new ArrayList<>();
        for(FleursEntity fleur : listeFleursEntity) {
            listeFleurs.add(fleur.getNom());
        }
        PalynologieDocument result = new PalynologieDocument();
        result.setFleurs(listeFleurs);
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String echantillon = correspondance.get("echantillon");
        result.setEchantillon(echantillon);
        String appelationDemandeur = correspondance.get("appelationDemandeur");
        result.setAppelationDemandeur(appelationDemandeur);

        List<Palynologie> compoList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("nomcomposition"+index)) {
            String nomcomposition = correspondance.get("nomcomposition"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentage" + index));
                String type = correspondance.get("type" + index);
                boolean isValid = Boolean.parseBoolean(correspondance.get("valid"+index));
                Palynologie compo = new Palynologie();
                compo.setPercentage(percentage);
                compo.setType(type);
                compo.setValue(nomcomposition);
                compo.setValid(isValid);
                compoList.add(compo);
            }
            index ++;
        }
        result.setCompositions(compoList);

        this.valeursPalynologie.put(id,result);
        insertNewPalynologie(result);


    }

    private void insertNewPalynologie(PalynologieDocument result) {
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
                RuchesEntity ruchesEntity = this.rucheDao.findRucheByName(ruche);
                if(ruchesEntity == null) {
                    LOGGER.warn("La ruche n'existe pas, on la créé");
                    ruchesEntity = new RuchesEntity();
                    ruchesEntity.setNom(ruche);
                    this.rucheDao.createRuche(ruchesEntity);
                }
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
    public void parseAndSaveResidus(final String value)  throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        ResidusDocument result = new ResidusDocument();
        result.setGmsDataList(this.paramMoleculesGmsDao.getAllMoleculesGms());
        result.setLmsDataList(this.paramMoleculesLmsDao.getAllMoleculesLms());
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String reference = correspondance.get("reference");
        result.setReference(reference);

        List<Molecule> gmsList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("valuegms"+index)) {
            String nomcomposition = correspondance.get("valuegms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagegms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracegms"+index));
                Molecule compo = new Molecule();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                gmsList.add(compo);
            }
            index ++;
        }


        List<Molecule> lmsList = new ArrayList<>();
        index=0;
        while(correspondance.containsKey("valuelms"+index)) {
            String nomcomposition = correspondance.get("valuelms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagelms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracelms"+index));
                Molecule compo = new Molecule();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                lmsList.add(compo);
            }
            index ++;
        }

        result.setMoleculesGms(gmsList);
        result.setMoleculesLms(lmsList);

        this.valeursResidus.put(id,result);

        // on persiste en base

    }

    private Map<String, String> parseStringToMap(String value) throws ParsingException {
        int accoladeOuvrante = StringUtils.countMatches(value, "{");
        if(accoladeOuvrante != 1) {
            throw new ParsingException("Attention, { est un caractère interdit, enregistrement non pris en compte !");
        }

        int accoladeFermante = StringUtils.countMatches(value, "}");
        if(accoladeFermante != 1) {
            throw new ParsingException("Attention, } est un caractère interdit, enregistrement non pris en compte !");
        }
        String[] splitValues = value.replace("{","").replace("}","").replace("\"","").split(",");
        Map<String,String> correspondance = new HashMap<>();
        for(int i =0; i< splitValues.length; i++) {
            String line = splitValues[i];
            int deuxPointsCount = StringUtils.countMatches(line, ":");
            String[] linesplited = line.split(":");
            if(deuxPointsCount != 1) {
                throw new ParsingException("Attention, : est un caractère interdit, enregistrement non pris en compte !");
            }
            if(linesplited.length == 2) {
                correspondance.put(linesplited[0], linesplited[1].replace("ceciestundeuxpoints",":"));
            }
            else {
                correspondance.put(linesplited[0], "");
            }
        }
        return correspondance;
    }


    @Override
    public String getCsvPalynologie() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
        exportedCsv.append("sep=;");
        exportedCsv.append("\n");
        exportedCsv.append(CSVUtils.writeLine(Arrays.asList("Echantillon", "Composition","Pourcentage", "Type"),'"'));

        // on écrit les résultats dans le fichier
        for (PalynologieDocument palynologieDocument : this.valeursPalynologie.values()) {
            exportedCsv.append(CSVUtils.writeResult(palynologieDocument));
        }
    } catch (IOException e) {
        LOGGER.error("erreur",e);
    }

        return exportedCsv.toString();
    }


    @Override
    public String getCsvResidus() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
            exportedCsv.append("sep=;");
            exportedCsv.append("\n");
            exportedCsv.append(CSVUtils.writeLine(Arrays.asList("Reference", "Composition","Pourcentage", "Type"),'"'));

            // on écrit les résultats dans le fichier
            for (ResidusDocument residusDocument : this.valeursResidus.values()) {
                final String str = CSVUtils.writeResult(residusDocument);
                exportedCsv.append(str);
            }
        } catch (IOException e) {
            LOGGER.error("erreur",e);
        }

        return exportedCsv.toString();
    }

    @Override
    public void cleanPalynologieMap() {
        this.valeursPalynologie.clear();
    }

    @Override
    public void cleanResidusMap() {
        this.valeursResidus.clear();
    }

    @Override
    public void fillPalynologieMap(List<PalynologieDocument> newValues) {
        for(PalynologieDocument palynologieDocument : newValues) {
            this.valeursPalynologie.put(palynologieDocument.getId(), palynologieDocument);
        }
    }

    @Override
    public void fillResidusMap(List<ResidusDocument> newValues) {
        for(ResidusDocument residusDocument : newValues) {
            this.valeursResidus.put(residusDocument.getId(), residusDocument);
        }

    }


}
