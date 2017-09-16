package com.perso.service.impl;

import com.perso.exception.ParsingException;
import com.perso.service.UpdatedValuesService;
import com.perso.utils.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Getter
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, ResultatPdf> valeursEnregistrees = new HashMap<>();
    private Map<Integer, ResponseTraitement2> valeursEnregistreest2 = new HashMap<>();

    @Override
    public void parseAndSave(final String value) throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        ResultatPdf result = new ResultatPdf();
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String echantillon = correspondance.get("echantillon");
        result.setEchantillon(echantillon);

        List<CompositionObj> compoList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("nomcomposition"+index)) {
            String nomcomposition = correspondance.get("nomcomposition"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentage" + index));
                String type = correspondance.get("type" + index);
                boolean isValid = Boolean.parseBoolean(correspondance.get("valid"+index));
                CompositionObj compo = new CompositionObj();
                compo.setPercentage(percentage);
                compo.setType(type);
                compo.setValue(nomcomposition);
                compo.setValid(isValid);
                compoList.add(compo);
            }
            index ++;
        }
        result.setCompositions(compoList);

        this.valeursEnregistrees.put(id,result);

    }
    
    @Override
    public void parseAndSavet2(final String value)  throws ParsingException {

        Map<String, String> correspondance = this.parseStringToMap(value);

        ResponseTraitement2 result = new ResponseTraitement2();
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String reference = correspondance.get("reference");
        result.setReference(reference);

        List<Traitement2Obj> gmsList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("valuegms"+index)) {
            String nomcomposition = correspondance.get("valuegms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagegms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracegms"+index));
                Traitement2Obj compo = new Traitement2Obj();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                gmsList.add(compo);
            }
            index ++;
        }


        List<Traitement2Obj> lmsList = new ArrayList<>();
        index=0;
        while(correspondance.containsKey("valuelms"+index)) {
            String nomcomposition = correspondance.get("valuelms"+index);
            if(!nomcomposition.isEmpty()) {
                double percentage = Double.parseDouble(correspondance.get("pourcentagelms" + index));
                boolean isTrace = Boolean.parseBoolean(correspondance.get("tracelms"+index));
                Traitement2Obj compo = new Traitement2Obj();
                compo.setPourcentage(percentage);
                compo.setValue(nomcomposition);
                compo.setTrace(isTrace);
                lmsList.add(compo);
            }
            index ++;
        }

        result.setGmsList(gmsList);
        result.setLmsList(lmsList);

        this.valeursEnregistreest2.put(id,result);

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
    public String getCsv() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
        exportedCsv.append("sep=;");
        exportedCsv.append("\n");
        exportedCsv.append(CSVUtils.writeLine(Arrays.asList("Echantillon", "Composition","Pourcentage", "Type"),'"'));

        // on écrit les résultats dans le fichier
        for (ResultatPdf resultatPdf : this.valeursEnregistrees.values()) {
            exportedCsv.append(CSVUtils.writeResult(resultatPdf));
        }
    } catch (IOException e) {
        LOGGER.error("erreur",e);
    }

        return exportedCsv.toString();
    }


    @Override
    public String getCsvt2() {
        StringBuilder exportedCsv = new StringBuilder();
        try {
            exportedCsv.append("sep=;");
            exportedCsv.append("\n");
            exportedCsv.append(CSVUtils.writeLine(Arrays.asList("Reference", "Composition","Pourcentage", "Type"),'"'));

            // on écrit les résultats dans le fichier
            for (ResponseTraitement2 responseTraitement2 : this.valeursEnregistreest2.values()) {
                final String str = CSVUtils.writeResult(responseTraitement2);
                exportedCsv.append(str);
            }
        } catch (IOException e) {
            LOGGER.error("erreur",e);
        }

        return exportedCsv.toString();
    }

    @Override
    public void cleanT1Map() {
        this.valeursEnregistrees.clear();
    }

    @Override
    public void cleanT2Map() {
        this.valeursEnregistreest2.clear();
    }

    @Override
    public void fillT1Map(List<ResultatPdf> newValues) {
        for(ResultatPdf resultatPdf : newValues) {
            this.valeursEnregistrees.put(resultatPdf.getId(), resultatPdf);
        }
    }

    @Override
    public void fillT2Map(List<ResponseTraitement2> newValues) {
        for(ResponseTraitement2 responseTraitement2 : newValues) {
            this.valeursEnregistreest2.put(responseTraitement2.getId(), responseTraitement2);
        }

    }


}