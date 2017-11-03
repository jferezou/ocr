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

        MoleculesGmsEntity moleculeEntity = new MoleculesGmsEntity();
        moleculeEntity.setNom("Coumaphos");
        moleculeEntity.setValeurTrace(0.01);
        TypeResidusEntity typeResidusEntity = new TypeResidusEntity();
        typeResidusEntity.setNom("Herbicide");
        moleculeEntity.setType(typeResidusEntity);
        Mockito.when(paramMoleculesGmsDao.findByName("coumaphos (A)")).thenReturn(moleculeEntity);

    }




    @Test
    public void testTraitementLigne() {
        String line = "coumaphos (A) 0.018 0,1";
        boolean isGms = true;
        Molecule molecule = this.residusExtractorService.traitementLigne(line, isGms);
        assertThat(molecule.getLimite()).isEqualTo("0,1");
        assertThat(molecule.getPourcentage()).isEqualTo(0.018);
        assertThat(molecule.getValue()).isEqualTo("Coumaphos");
    }

    @After
    public void reset() {

    }
}
