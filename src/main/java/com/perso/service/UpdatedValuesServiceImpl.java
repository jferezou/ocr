package com.perso.service;

import com.perso.utils.CompositionObj;
import com.perso.utils.ResultatPdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
