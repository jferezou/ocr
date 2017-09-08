package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.perso.utils.ResultatPdf;

public interface TransformService {

	ResultatPdf extract(final File pngFile) throws IOException;
}
