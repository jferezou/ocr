package com.perso.service;

import com.perso.pojo.residus.ResidusDocument;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public interface ResidusExtractorService {

    ResidusDocument extraire(final Path path);
}
