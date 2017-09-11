package com.perso.service;

import com.perso.utils.CSVUtils;
import com.perso.utils.CompositionObj;
import com.perso.utils.ResultatPdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UpdatedValuesServiceImpl implements UpdatedValuesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedValuesServiceImpl.class);

    private Map<Integer, ResultatPdf> valeursEnregistrees = new HashMap<>();

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

}
