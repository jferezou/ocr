package com.algo;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.perso.service.TransformService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=TestConfig.class)
public class TransformServiceTest {
	@Autowired
	private TransformService transformService;
	
	
	@Test
	public void testVersJavanais() {
//		assertThat(this.transformService.convertToJavanais("c'est")).isEqualTo("c'est");
//		assertThat(this.transformService.convertToJavanais("bonjour")).isEqualTo("bavonjavour");
//		assertThat(this.transformService.convertToJavanais("chante")).isEqualTo("chavantave");
//		assertThat(this.transformService.convertToJavanais("moyen")).isEqualTo("mavoyen");
//		assertThat(this.transformService.convertToJavanais("exemple")).isEqualTo("avexavemplave");
//		assertThat(this.transformService.convertToJavanais("au")).isEqualTo("avau");
//		assertThat(this.transformService.convertToJavanais("éu")).isEqualTo("avéu");
//		assertThat(this.transformService.convertToJavanais("èu")).isEqualTo("avèu");
//		assertThat(this.transformService.convertToJavanais("êu")).isEqualTo("avêu");
//		assertThat(this.transformService.convertToJavanais("àu")).isEqualTo("avàu");
//		assertThat(this.transformService.convertToJavanais("ôu")).isEqualTo("avôu");
//		assertThat(this.transformService.convertToJavanais("ùu")).isEqualTo("avùu");
//		assertThat(this.transformService.convertToJavanais("çu")).isEqualTo("çavu");
	}

	
	@Test
	public void testFromJavanais() {
//		assertThat(this.transformService.convertFromJavanais("bavonjavour")).isEqualTo("bonjour");
//		assertThat(this.transformService.convertFromJavanais("chavantave")).isEqualTo("chante");
//		assertThat(this.transformService.convertFromJavanais("mavoyen")).isEqualTo("moyen");
//		assertThat(this.transformService.convertFromJavanais("avexavemplave")).isEqualTo("exemple");
//		assertThat(this.transformService.convertFromJavanais("avau")).isEqualTo("au");
//		assertThat(this.transformService.convertFromJavanais("avoir")).isEqualTo("oir");
//		assertThat(this.transformService.convertFromJavanais("aava")).isEqualTo("aava");
//		assertThat(this.transformService.convertFromJavanais("eavi")).isEqualTo("eavi");
//		assertThat(this.transformService.convertFromJavanais(" avi")).isEqualTo(" i");
//		assertThat(this.transformService.convertFromJavanais(" avh")).isEqualTo(" avh");
//		assertThat(this.transformService.convertFromJavanais("c'avest")).isEqualTo("c'avest");
//		assertThat(this.transformService.convertFromJavanais(" avé")).isEqualTo(" é");
//		assertThat(this.transformService.convertFromJavanais(" avè")).isEqualTo(" è");
//		assertThat(this.transformService.convertFromJavanais(" avê")).isEqualTo(" ê");
//		assertThat(this.transformService.convertFromJavanais(" avà")).isEqualTo(" à");
//		assertThat(this.transformService.convertFromJavanais(" avô")).isEqualTo(" ô");
//		assertThat(this.transformService.convertFromJavanais(" avù")).isEqualTo(" ù");
//		assertThat(this.transformService.convertFromJavanais(" avà")).isEqualTo(" à");
//		assertThat(this.transformService.convertFromJavanais(" avç")).isEqualTo(" avç");
	}

}
