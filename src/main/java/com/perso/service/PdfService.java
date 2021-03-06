package com.perso.service;

import com.perso.utils.AggregatePdf;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface PdfService {
    void splitPdf(final Path path);
    boolean checkIfPdf(final File file);
    void deletePages(final Path path);
    File createPdf(final List<AggregatePdf> aggregatePdfs, String ruche);
}
