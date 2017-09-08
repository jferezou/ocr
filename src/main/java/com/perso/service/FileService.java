package com.perso.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;

import java.io.File;
import java.io.IOException;

public interface FileService {
    boolean checkFileType(final File file, final MediaType fileType) throws TikaException, IOException;
}
