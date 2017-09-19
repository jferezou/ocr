package com.perso.service;

import java.io.File;
import java.nio.file.Path;

public interface PdfService {
    void splitPdf(final Path path);
    boolean checkIfPdf(final File file);
}
