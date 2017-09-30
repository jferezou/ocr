package com.perso.bdd.entity.parametrage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(exclude={"valeurTrace"})
public abstract class MoleculeEntity {


    protected String nom;

    protected Double valeurTrace;



}
