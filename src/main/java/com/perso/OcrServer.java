package com.perso;

import com.perso.config.ServerComponent;

/**
 * Classe principale
 * @author jferezou
 *
 */
public class OcrServer extends ServerComponent {
	public static void main(final String[] args) {
		run("ocr", "ocr", OcrServer.class, args);
	}
}
