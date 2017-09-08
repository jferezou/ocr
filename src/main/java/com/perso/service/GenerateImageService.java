package com.perso.service;

import java.io.File;
import java.nio.file.Path;

public interface GenerateImageService {

    File generatePng(final Path path);
}
