package com.perso.service;

import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface GenerateImageService {

    File generatePng(final Path path);
    boolean checkIfPng(final File file);
}
