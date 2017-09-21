package com.perso.service;

import com.perso.pojo.residus.ResidusDocument;
import com.perso.utils.AggregatePdf;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Date;

@Service
public interface ResidusExtractorService {

    ResidusDocument extraire(final Path path);
    AggregatePdf extraireDate(Path path);
}
