package com.perso.service.impl;

import com.perso.bdd.dao.*;
import com.perso.bdd.entity.PalynologieDocumentEntity;
import com.perso.bdd.entity.ResidusDocumentEntity;
import com.perso.bdd.entity.parametrage.*;
import com.perso.exception.BddException;
import com.perso.exception.ParsingException;
import com.perso.service.InsertService;
import com.perso.service.UpdatedValuesService;
import com.perso.utils.*;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.Molecule;
import com.perso.pojo.residus.ResidusDocument;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Getter
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, PalynologieDocument> valeursPalynologie = new TreeMap<>();
    private Map<Integer, ResidusDocument> valeursResidus = new TreeMap<>();


    @Resource
    private ParamEspeceDao paramEspeceDao;

    @Resource
    private ParamMoleculesGmsDao paramMoleculesGmsDao;

    @Resource
    private ParamMoleculesLmsDao paramMoleculesLmsDao;

    @Resource
    private InsertService insertService;

    @Resource
    private ResidusDocumentDao residusDocumentDao;
    @Resource
    private PalynologieDocumentDao palynologieDocumentDao;

    @Resource
    private ExportCsvServiceImpl exportCsvService;

    @Override
    public void parseAndSavePalynologie(final String value) throws ParsingException, BddException {

        Map<String, String> correspondance = this.parseStringToMap(value);
        List<EspeceEntity> listeEspeceEntity = this.paramEspeceDao.getAllEspeces();
        List<String> listeFleurs = new ArrayList<>();
        for(EspeceEntity fleur : listeEspeceEntity) {
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
        String pdfName = correspondance.get("pdfName");
        result.setPdfFileName(pdfName);

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
        this.insertService.insertNewPalynologie(result);


    }


    @Override
    public void parseAndSaveResidus(final String value)  throws ParsingException, BddException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        ResidusDocument result = new ResidusDocument();
        result.setGmsDataList(this.paramMoleculesGmsDao.getAllMoleculesGms());
        result.setLmsDataList(this.paramMoleculesLmsDao.getAllMoleculesLms());
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String reference = correspondance.get("reference");
        result.setReference(reference);
        String certificatAnalyses = correspondance.get("certificatAnalyses");
        result.setCertificatAnalyses(certificatAnalyses);
        String poids = correspondance.get("poids");
        result.setPoids(Double.parseDouble(poids));
        String pdfName = correspondance.get("pdfName");
        result.setPdfName(pdfName);

        List<Molecule> gmsList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("valuegms"+index)) {
            String nomcomposition = correspondance.get("valuegms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagegms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracegms"+index));
                String limite = correspondance.get("limitegms"+index);
                Molecule compo = new Molecule();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                compo.setLimite(limite);
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
                String limite = correspondance.get("limitelms"+index);
                Molecule compo = new Molecule();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                compo.setLimite(limite);
                lmsList.add(compo);
            }
            index ++;
        }

        result.setMoleculesGms(gmsList);
        result.setMoleculesLms(lmsList);

        this.valeursResidus.put(id,result);

        // on persiste en base
        this.insertService.insertNewResidus(result);
    }


    private Map<String, String> parseStringToMap(final String value) throws ParsingException {
        int accoladeOuvrante = StringUtils.countMatches(value, "{");
        if(accoladeOuvrante != 1) {
            throw new ParsingException("Attention, { est un caractère interdit, enregistrement non pris en compte !");
        }

        int accoladeFermante = StringUtils.countMatches(value, "}");
        if(accoladeFermante != 1) {
            throw new ParsingException("Attention, } est un caractère interdit, enregistrement non pris en compte !");
        }
        String[] splitValues = value.replace("{","").replace("}","").split(",\"");
        Map<String,String> correspondance = new HashMap<>();
        for(int i =0; i< splitValues.length; i++) {
            String line = splitValues[i];
            int deuxPointsCount = StringUtils.countMatches(line, "\":");
            String[] linesplited = line.split("\":");
            if(deuxPointsCount != 1) {
                throw new ParsingException("Attention, : est un caractère interdit, enregistrement non pris en compte !");
            }
            linesplited[0] = linesplited[0].replace("\"","");
            linesplited[1] = linesplited[1].replace("\"","");
            if(linesplited.length == 2) {
                correspondance.put(linesplited[0], linesplited[1]);
            }
            else {
                correspondance.put(linesplited[0], "");
            }
        }
        return correspondance;
    }


    @Override
    @Transactional
    public String getCsvPalynologie() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
            List<PalynologieDocumentEntity> palynoDocsList = this.palynologieDocumentDao.getAllPalynologieDocument();
            exportedCsv.append("sep=;");
            exportedCsv.append("\n");
            exportedCsv.append(this.exportCsvService.writeLine(Arrays.asList("n° éch.",
                    "année",
                    "type",
                    "id_échantillon",
                    "Echantillon",
                    "Balance",
                    "nom apiculteur",
                    "prénom apiculteur",
                    "commune",
                    "dept.",
                    "région",
                    "date récolte",
                    "mois",
                    "famille",
                    "genre",
                    "espèce",
                    "espèce 2",
                    "%",
                    "N° ruche",
                    "Fichier PDF",
                    "Page"),'"'));


            // on écrit les résultats dans le fichier
            for (PalynologieDocumentEntity palynologieDocument : palynoDocsList) {
                exportedCsv.append(this.exportCsvService.writeResult(palynologieDocument));
            }
        } catch (IOException e) {
            LOGGER.error("erreur",e);
        }

        return exportedCsv.toString();
    }


    @Override
    @Transactional
    public String getCsvResidus() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
            List<ResidusDocumentEntity> residusDocsList = this.residusDocumentDao.getAllResidusDocument();
            exportedCsv.append("sep=;");
            exportedCsv.append("\n");
            exportedCsv.append(this.exportCsvService.writeLine(Arrays.asList("Certificat analyse",
                    "Année",
                    "Dossier",
                    "Matrice",
                    "Id ech",
                    "Numéro de ruche",
                    "Nom apiculteur",
                    "Site","Département",
                    "Région",
                    "Date",
                    "Mois",
                    "Date début",
                    "Date fin",
                    "Nb jours récolte",
                    "Nb ruches",
                    "Poids gr",
                    "Type SA",
                    "Molécule",
                    "Résidu (mg/L)",
                    "LOQ (mg/kg)",
                    "LMR (mg/kg)",
                    "Trace",
                    "Type analyse",
                    "Fichier PDF",
                    "Valeur originale"),'"'));
            // on écrit les résultats dans le fichier
            for (ResidusDocumentEntity residusDocument : residusDocsList) {
                final String str = this.exportCsvService.writeResult(residusDocument);
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
    public void fillPalynologieMap(final List<PalynologieDocument> newValues) {
        for(PalynologieDocument palynologieDocument : newValues) {
            this.valeursPalynologie.put(palynologieDocument.getId(), palynologieDocument);
        }
    }

    @Override
    public void fillResidusMap(final List<ResidusDocument> newValues) {
        for(ResidusDocument residusDocument : newValues) {
            this.valeursResidus.put(residusDocument.getId(), residusDocument);
        }

    }


}
