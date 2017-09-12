package com.perso.service;

import com.perso.utils.EstimateTime;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface PdfService {
    void splitPdf(final Path path);
    EstimateTime estimateTime();
    boolean checkIfPdf(final File file);
}
