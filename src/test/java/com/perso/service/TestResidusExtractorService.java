package com.perso.service;

import com.perso.bdd.dao.ParamMoleculesGmsDao;
import com.perso.bdd.dao.impl.MoleculesGmsDaoImpl;
import com.perso.bdd.dao.impl.MoleculesLmsDaoImpl;
import com.perso.bdd.entity.parametrage.MoleculeEntity;
import com.perso.bdd.entity.parametrage.MoleculesGmsEntity;
import com.perso.bdd.entity.parametrage.TypeResidusEntity;
import com.perso.exception.BddException;
import com.perso.pojo.residus.Molecule;
import com.perso.service.impl.ResidusExtractorServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestResidusExtractorService {

    @Mock
    private MoleculesGmsDaoImpl paramMoleculesGmsDao;

    @Mock
    private MoleculesLmsDaoImpl paramMoleculesLmsDao;

    @InjectMocks
    ResidusExtractorServiceImpl residusExtractorService;

    @Before
    public void init() throws BddException {

        MoleculesGmsEntity moleculeEntityCoumaphos = new MoleculesGmsEntity();
        moleculeEntityCoumaphos.setNom("Coumaphos");
        moleculeEntityCoumaphos.setValeurTrace(0.01);
        TypeResidusEntity typeResidusEntity = new TypeResidusEntity();
        typeResidusEntity.setNom("Herbicide");
        moleculeEntityCoumaphos.setType(typeResidusEntity);
        Mockito.when(paramMoleculesGmsDao.findByName("coumaphos (A)")).thenReturn(moleculeEntityCoumaphos);

        MoleculesGmsEntity moleculeEntityEtho = new MoleculesGmsEntity();
        moleculeEntityEtho.setNom("ethofumesate (sum of ethofumesate and the metabolite 2,3-dihydro-3,3-dimethyl-2-oxobenzofuran-5-yl methane sulphonate expressed as ethofumesate)");
        moleculeEntityEtho.setValeurTrace(0.03);
        TypeResidusEntity typeResidusEntityF = new TypeResidusEntity();
        typeResidusEntityF.setNom("Fongicide");
        moleculeEntityEtho.setType(typeResidusEntityF);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("ethofumesate (sum of ")).thenReturn(moleculeEntityEtho);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("ethofumesate (sum of ethofumesate and the metabolite")).thenReturn(moleculeEntityEtho);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("ethofumesate and the metabolite 2,3-dihydro-3,3-dimethyl-2-oxo")).thenReturn(moleculeEntityEtho);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("2,3-dihydro-3,3-dimethyl-2-oxo-benzofuran-5-yl")).thenReturn(moleculeEntityEtho);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("benzofuran-5-yl methane sulphonate expressed as ")).thenReturn(moleculeEntityEtho);
        Mockito.when(paramMoleculesGmsDao.findByNameContaining("methane sulphonate expressed as ethofumesate")).thenReturn(moleculeEntityEtho);

        Mockito.when(paramMoleculesGmsDao.findByName("2,3-dihydro-3,3-dimethyl-2-oxo-(A)")).thenThrow(BddException.class);
    }




    @Test
    public void testTraitementLigne() {
        String line = "coumaphos (A) 0.018 0,1";
        boolean isGms = true;
        Molecule molecule = this.residusExtractorService.traitementLigne(line, isGms, "");
        assertThat(molecule.getLimite()).isEqualTo("0,1");
        assertThat(molecule.getPourcentage()).isEqualTo(0.018);
        assertThat(molecule.getValue()).isEqualTo("Coumaphos");
    }
    @Test
    public void testTraitementLigneSansPourcentgae() {
        String line = "coumaphos (A) 0,1";
        boolean isGms = true;
        Molecule molecule = this.residusExtractorService.traitementLigne(line, isGms,"");
        assertThat(molecule.getLimite()).isEqualTo("0,1");
        assertThat(molecule.getPourcentage()).isEqualTo(0.01);
        assertThat(molecule.getValue()).isEqualTo("Coumaphos");
    }



    @Test
    public void testTraitementLigneMulti() {
        String[] line = this.residusExtractorService.nettoyerResultat("ethofumesate (sum of \n" +
                "ethofumesate and the metabolite \n" +
                "2,3-dihydro-3,3-dimethyl-2-oxo-\n" +
                "A 0,017 0,05\n" +
                "benzofuran-5-yl methane \n" +
                "sulphonate expressed as \n" +
                "ethofumesate)").split("\n");
        boolean isGms = true;
        Molecule molecule = this.residusExtractorService.traitementLigne(line[0], isGms,simulerTraitement(""));
        assertThat(molecule.getValue()).isNull();
        assertThat(molecule.getPourcentage()).isNull();

        Molecule molecule2 = this.residusExtractorService.traitementLigne(line[1], isGms,simulerTraitement(line[0]));
        assertThat(molecule2.getLimite()).isEqualTo("");
        assertThat(molecule2.getPourcentage()).isEqualTo(0.03);
        assertThat(molecule2.getValue()).isEqualTo("ethofumesate (sum of ethofumesate and the metabolite 2,3-dihydro-3,3-dimethyl-2-oxobenzofuran-5-yl methane sulphonate expressed as ethofumesate)");

        Molecule molecule3 = this.residusExtractorService.traitementLigne(line[2], isGms,simulerTraitement(line[1]));
        assertThat(molecule3.getLimite()).isEqualTo("0,05");
        assertThat(molecule3.getPourcentage()).isEqualTo(0.017);
        assertThat(molecule3.getValue()).isEqualTo("ethofumesate (sum of ethofumesate and the metabolite 2,3-dihydro-3,3-dimethyl-2-oxobenzofuran-5-yl methane sulphonate expressed as ethofumesate)");

    }


    private String simulerTraitement(String line) {
        String value = StringUtils.trim(line);
        value = StringUtils.removeEnd(value,"-");
        return value;
    }

    @After
    public void reset() {

    }
}
