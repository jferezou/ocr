package com.perso.service.impl;

import com.perso.config.ListeFleursConfig;
import com.perso.exception.ParsingException;
import com.perso.pojo.residus.GmsElementList;
import com.perso.pojo.residus.LmsElementList;
import com.perso.service.UpdatedValuesService;
import com.perso.utils.*;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.Residu;
import com.perso.pojo.residus.ResidusDocument;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Getter
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, PalynologieDocument> valeursPalynologie = new HashMap<>();
    private Map<Integer, ResidusDocument> valeursResidus = new HashMap<>();
    @Resource
    private ListeFleursConfig listeFleurs;

    @Override
    public void parseAndSavePalynologie(final String value) throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        PalynologieDocument result = new PalynologieDocument();
        result.setFleurs(this.listeFleurs.getFleurs());
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String echantillon = correspondance.get("echantillon");
        result.setEchantillon(echantillon);

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

    }
    
    @Override
    public void parseAndSaveResidus(final String value)  throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        ResidusDocument result = new ResidusDocument();
        result.setGmsDataList(new GmsElementList().toList());
        result.setLmsDataList(new LmsElementList().toList());
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String reference = correspondance.get("reference");
        result.setReference(reference);

        List<Residu> gmsList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("valuegms"+index)) {
            String nomcomposition = correspondance.get("valuegms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagegms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracegms"+index));
                Residu compo = new Residu();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                gmsList.add(compo);
            }
            index ++;
        }


        List<Residu> lmsList = new ArrayList<>();
        index=0;
        while(correspondance.containsKey("valuelms"+index)) {
            String nomcomposition = correspondance.get("valuelms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagelms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracelms"+index));
                Residu compo = new Residu();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                lmsList.add(compo);
            }
            index ++;
        }

        result.setGmsList(gmsList);
        result.setLmsList(lmsList);

        this.valeursResidus.put(id,result);

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
                correspondance.put(linesplited[0], linesplited[1]);
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
