package com.perso.service;

import java.io.File;

import com.perso.pojo.palynologie.PalynologieDocument;

public interface PalynologieExtractorService {

	PalynologieDocument extract(final File pngFile);
}
