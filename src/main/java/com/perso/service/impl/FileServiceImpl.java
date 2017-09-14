package com.perso.service.impl;

import com.perso.service.FileService;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public boolean checkFileType(final File file, final MediaType fileType) throws TikaException, IOException {
        boolean isCorrect = false;
        if(file != null) {
            try (final InputStream stream = new FileInputStream(file); final InputStream bufferedInputstream = new BufferedInputStream(stream)){
                MediaType fileInfo = this.getFileInfo(file.getName(), bufferedInputstream);
                if (fileType.equals(fileInfo.getBaseType())) {
                    isCorrect = true;
                } else {
                    LOGGER.warn("Le fichier n'est pas traité car il doit être au format " + fileType + " : " + file.getPath());
                }
            }
        }
        return isCorrect;
    }

    /**
     * Méthode utilisant apache Tika pour récupérer les infos du fichier :
     * mimetype, charset ....
     *
     * @param fileName
     * @param stream
     * @return
     * @throws TikaException
     * @throws IOException
     */
    private MediaType getFileInfo(final String fileName, final InputStream stream) throws TikaException, IOException {
        TikaConfig tika = new TikaConfig();
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);
        return tika.getDetector().detect(stream, metadata);
    }
}
