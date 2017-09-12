package com.perso.service;

import com.perso.utils.ResponseTraitement2;
import com.perso.utils.ResultatPdf;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public interface TraitementT2Service {

    ResponseTraitement2 extraire(final Path path);
}
