package com.perso.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class StringUtilsOcrTest {

    @Test
    public void testGetfirstdigitIndex() {
        String input = "azerty";
        assertThat(StringUtilsOcr.getfirstdigitIndex(input)).isEqualTo(-1);
        input = "1azerty";
        assertThat(StringUtilsOcr.getfirstdigitIndex(input)).isEqualTo(0);
        input = "azerty5";
        assertThat(StringUtilsOcr.getfirstdigitIndex(input)).isEqualTo(6);
        input = "azerty pmhhnez 5.3";
        assertThat(StringUtilsOcr.getfirstdigitIndex(input)).isEqualTo(15);
    }

    @Test
    public void testGetfirstspace() {
        String input = "azerty";
        assertThat(StringUtilsOcr.getFirstSpace(input)).isEqualTo(-1);
        input = "1azerty";
        assertThat(StringUtilsOcr.getFirstSpace(input)).isEqualTo(-1);
        input = " azerty5";
        assertThat(StringUtilsOcr.getFirstSpace(input)).isEqualTo(0);
        input = "azerty pmhhnez 5.3";
        assertThat(StringUtilsOcr.getFirstSpace(input)).isEqualTo(6);
        input = "az erty pmhhnez 5.3";
        assertThat(StringUtilsOcr.getFirstSpace(input)).isEqualTo(2);
    }


    @Test
    public void testTrimEtSupprimeTiretalaFin() {
        String input = " azerty-";
        assertThat(StringUtilsOcr.trimEtSupprimeTiretalaFin(input)).isEqualTo("azerty");
        input = " azerty grr";
        assertThat(StringUtilsOcr.trimEtSupprimeTiretalaFin(input)).isEqualTo("azerty grr");
        input = "aze p jjeiei  \n olohz é(rty-";
        assertThat(StringUtilsOcr.trimEtSupprimeTiretalaFin(input)).isEqualTo("aze p jjeiei  \n olohz é(rty");
        input = "azerty";
        assertThat(StringUtilsOcr.trimEtSupprimeTiretalaFin(input)).isEqualTo("azerty");
    }


}
