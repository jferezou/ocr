package com.perso.service;

import com.perso.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, ResultatPdf> valeursEnregistrees = new HashMap<>();
    private Map<Integer, ResponseTraitement2> valeursEnregistreest2 = new HashMap<>();

    @Override
    public void parseAndSave(final String value) {

        String[] splitValues = value.replace("{","").replace("}","").replace("\"","").split(",");
        Map<String,String> correspondance = new HashMap<>();
        for(int i =0; i< splitValues.length; i++) {
            String line = splitValues[i];
            String[] linesplited = line.split(":");
            correspondance.put(linesplited[0], linesplited[1]);
        }

        ResultatPdf result = new ResultatPdf();
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String echantillon = correspondance.get("echantillon");
        result.setEchantillon(echantillon);

        List<CompositionObj> compoList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("nomcomposition"+index)) {
            String nomcomposition = correspondance.get("nomcomposition"+index);
            double percentage = Double.parseDouble(correspondance.get("pourcentage"+index));
            String type = correspondance.get("type"+index);
            CompositionObj compo = new CompositionObj();
            compo.setPercentage(percentage);
            compo.setType(type);
            compo.setValue(nomcomposition);
            compoList.add(compo);
            index ++;
        }
        result.setCompositions(compoList);

        this.valeursEnregistrees.put(id,result);

    }
    
    @Override
    public void parseAndSavet2(final String value) {

        String[] splitValues = value.replace("{","").replace("}","").replace("\"","").split(",");
        Map<String,String> correspondance = new HashMap<>();
        for(int i =0; i< splitValues.length; i++) {
            String line = splitValues[i];
            String[] linesplited = line.split(":");
            correspondance.put(linesplited[0], linesplited[1]);
        }

        ResponseTraitement2 result = new ResponseTraitement2();
        int id = Integer.parseInt(correspondance.get("id"));
        result.setId(id);
        String reference = correspondance.get("reference");
        result.setReference(reference);

        List<Traitement2Obj> gmsList = new ArrayList<>();
        int index=0;
        while(correspondance.containsKey("valuegms"+index)) {
            String nomcomposition = correspondance.get("valuegms"+index);
            double percentage = Double.parseDouble(correspondance.get("pourcentagegms"+index));
            Traitement2Obj compo = new Traitement2Obj();
            compo.setPourcentage(percentage);
            compo.setValue(nomcomposition);
            gmsList.add(compo);
            index ++;
        }


        List<Traitement2Obj> lmsList = new ArrayList<>();
        index=0;
        while(correspondance.containsKey("valuelms"+index)) {
            String nomcomposition = correspondance.get("valuelms"+index);
            double percentage = Double.parseDouble(correspondance.get("pourcentagelms"+index));
            Traitement2Obj compo = new Traitement2Obj();
            compo.setPourcentage(percentage);
            compo.setValue(nomcomposition);
            lmsList.add(compo);
            index ++;
        }

        result.setGmsList(gmsList);
        result.setLmsList(lmsList);

        this.valeursEnregistreest2.put(id,result);

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

}
