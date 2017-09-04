package com.perso;

import java.io.IOException;
import java.util.Date;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.*;
import com.perso.exception.FichierInvalideException;
import com.perso.service.ReaderFileService;
import com.perso.service.ReaderFileServiceImpl;

/**
 * Classe principale
 * @author jferezou
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.perso.*"})
@PropertySource("classpath:com/perso/config.properties")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@Import({ AppConfig.class})
public class Ocr {
	private static final Logger LOGGER = LoggerFactory.getLogger(Ocr.class);

	
	public static void main(String[] args) {
		SpringApplication.run(Ocr.class,args);
	}

	
	public void run(String[] args, AnnotationConfigApplicationContext context) {
		Date debut = new Date();
		LOGGER.info("Debut traitement");
		try {
			//chargement contexte spring
			ReaderFileService reader = context.getBean(ReaderFileServiceImpl.class);
			reader.readAndLaunch();
		}
		catch(FichierInvalideException | TikaException | IOException e) {
			LOGGER.error("Erreur lors du traitement : ", e);
		}
		Date fin = new Date();
		LOGGER.info("Fin traitement en : {} ms", fin.getTime() - debut.getTime() );
		
	}
}
