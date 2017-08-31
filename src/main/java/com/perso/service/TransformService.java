package com.perso.service;

import java.io.File;
import java.io.IOException;

public interface TransformService {

	void extract(final File pdfFile) throws IOException;
}
