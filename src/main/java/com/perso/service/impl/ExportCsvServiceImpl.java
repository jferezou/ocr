package com.perso.service.impl;

import com.perso.bdd.entity.*;
import com.perso.bdd.entity.parametrage.GenreEntity;
import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.pojo.palynologie.Palynologie;
import com.perso.pojo.palynologie.PalynologieDocument;
import com.perso.pojo.residus.Molecule;
import com.perso.pojo.residus.ResidusDocument;
import com.perso.service.ExportCsvService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ExportCsvServiceImpl implements ExportCsvService {

    private final char DEFAULT_SEPARATOR = ';';



    @Override
    @Transactional
    public String writeResult(final PalynologieDocumentEntity palynologieDocument)  throws IOException {

        final String numeroEchantillon = palynologieDocument.getNumeroEchantillon().toString();
        final Date date = palynologieDocument.getDate();
        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        final String annee = yearSdf.format(date);
        final String matrice = palynologieDocument.getMatrice().getNom();
        final String idEchantillon = palynologieDocument.getIdentifiantEchantillon();
        final String echantillon = palynologieDocument.getIdentifiant();
        final String ruche = palynologieDocument.getRuche().getNom();
        final String nomApiculteur = palynologieDocument.getContact().getNom();
        final String prenomApiculteur = palynologieDocument.getContact().getPrenom();
        final String commune = palynologieDocument.getContact().getSite();
        final Integer departement = palynologieDocument.getContact().getDepartement();
        final String region = palynologieDocument.getContact().getRegion();
        SimpleDateFormat dateSdf = new SimpleDateFormat("dd-MMMM");
        final String dateStr = dateSdf.format(date);
        SimpleDateFormat monthSdf = new SimpleDateFormat("MMMM");
        final String month = monthSdf.format(date);
        final String pdfFileName = palynologieDocument.getPdfName();
        final String page = palynologieDocument.getPdfPage();


        StringBuilder result = new StringBuilder();
        for(PalynologieEntity palynologieEntity : palynologieDocument.getPalynologieList()) {
            final String especeNom = palynologieEntity.getEspece().getNom();
            String especeNom2 = palynologieEntity.getEspece().getNom2();
            if(especeNom2 == null) {
                especeNom2 = "";
            }
            String genre = "";
            String famille = "";
            final GenreEntity genreEntity = palynologieEntity.getEspece().getGenre();
            if(genreEntity != null) {
                genre = genreEntity.getNom();
                if(genreEntity.getFamille() != null) {
                    famille = genreEntity.getFamille().getNom();
                }
            }
            result.append(writeLine(Arrays.asList(numeroEchantillon,
                    annee,
                    matrice,
                    idEchantillon,
                    echantillon,
                    ruche,
                    prenomApiculteur,
                    nomApiculteur,
                    commune,
                    departement.toString(),
                    region,
                    dateStr,
                    month,
                    famille,
                    genre,
                    especeNom,
                    especeNom2,
                    palynologieEntity.getPourcentage().toString().replace(".",","),
                    ruche,
                    pdfFileName,
                    page),'"'));
        }
        return result.toString();
    }


    @Override
    @Transactional
    public String writeResult(final ResidusDocumentEntity residusDocument)  throws IOException {

        final String certificatAnalyse = residusDocument.getCertificatAnalyse();
        final Date date = residusDocument.getDate();
        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        final String annee = yearSdf.format(date);
        final String matrice = residusDocument.getMatrice().getNom();
        final String[] splitId = residusDocument.getIdentifiant().split("-");
        String identifiantEchantillon = residusDocument.getIdentifiant();
        if(splitId.length > 2) {
            identifiantEchantillon = splitId[0]+splitId[1]+splitId[2];
        }
        final String ruche = residusDocument.getRuche().getNom();
        final String nomApiculteur = residusDocument.getContact().getNom();
        final String prenomApiculteur = residusDocument.getContact().getPrenom();
        final String site = residusDocument.getContact().getSite();
        final Integer departement = residusDocument.getContact().getDepartement();
        final String region = residusDocument.getContact().getRegion();
        SimpleDateFormat dateSdf = new SimpleDateFormat("dd-MMMM");
        final String dateStr = dateSdf.format(date);
        SimpleDateFormat monthSdf = new SimpleDateFormat("MMMM");
        final String month = monthSdf.format(date);
        final Double poids = residusDocument.getPoids();
        final String pdfFileName = residusDocument.getPdfName();

        StringBuilder result = new StringBuilder();
        for(ResidusGmsEntity residusGmsEntity : residusDocument.getResidusGmsList()) {
            result.append(writeLine(Arrays.asList(certificatAnalyse,
                    annee,
                    "",
                    matrice,
                    identifiantEchantillon,
                    ruche,
                    prenomApiculteur+ " " +nomApiculteur,
                    site,
                    departement.toString(),
                    region,
                    dateStr,
                    month,
                    "",
                    "",
                    "",
                    "",
                    poids.toString().replace(".",","),
                    "",
                    residusGmsEntity.getMoleculeGms().getNom(),
                    residusGmsEntity.getTaux().toString().replace(".",","),
                    String.valueOf(residusGmsEntity.getMoleculeGms().getValeurTrace()),
                    "",
                    residusGmsEntity.getTrace().toString(),
                    "GMS",
                    pdfFileName),'"'));
        }
        for(ResidusLmsEntity residusLmsEntity : residusDocument.getResidusLmsList()) {
            result.append(writeLine(Arrays.asList(certificatAnalyse,
                    annee,
                    "",
                    matrice,
                    identifiantEchantillon,
                    ruche,
                    prenomApiculteur+ " " +nomApiculteur,
                    site,
                    departement.toString(),
                    region,
                    dateStr,
                    month,
                    "",
                    "",
                    "",
                    "",
                    poids.toString().replace(".",","),
                    "",
                    residusLmsEntity.getMoleculeLms().getNom(),
                    residusLmsEntity.getTaux().toString().replace(".",","),
                    String.valueOf(residusLmsEntity.getMoleculeLms().getValeurTrace()),
                    "",
                    residusLmsEntity.getTrace().toString(),
                    "LMS",
                    pdfFileName),'"'));
        }
        return result.toString();
    }

    //https://tools.ietf.org/html/rfc4180
    private String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public String writeLine( List<String> values, char customQuote) throws IOException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(DEFAULT_SEPARATOR);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        return sb.toString();
    }

}
