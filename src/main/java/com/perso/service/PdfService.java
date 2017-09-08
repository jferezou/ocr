package com.perso.service;

import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface PdfService {
    void splitPdf(final Path path);
}
