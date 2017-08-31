package com.perso.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.perso.utils.ResultatPdf;

public interface TransformService {

	List<ResultatPdf> extract(final File pdfFile) throws IOException;
}
