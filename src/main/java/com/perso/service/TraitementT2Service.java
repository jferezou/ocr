package com.perso.service;

import com.perso.utils.ResultatPdf;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public interface TraitementT2Service {

    ResultatPdf extraire(final Path path);
}
