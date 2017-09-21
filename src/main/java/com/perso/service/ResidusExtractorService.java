package com.perso.service;

import com.perso.pojo.residus.ResidusDocument;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Date;

@Service
public interface ResidusExtractorService {

    ResidusDocument extraire(final Path path);
    Date extraireDate(Path path);
}
