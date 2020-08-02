/*
 * Copyright 2020 phyzicsz <phyzics.z@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phyzicsz.milo.core;

import com.phyzicsz.milo.MiloRenderService;
import com.phyzicsz.milo.renderer.common.MilStdAttributes;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.phyzicsz.milo.renderer.info.PNGInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class MiloRenderServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MiloRenderServiceTest.class);

    public MiloRenderServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {

    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of setDefaultSymbologyStandard method, of class MiloRenderService.
     */
    @Test
    public void testSetDefaultSymbologyStandard() throws IOException {
        logger.info("testSetDefaultSymbologyStandard");
        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "50");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        String symbolCode = "SFUPSK----*****";
        PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);

        byte[] expected = getClass().getClassLoader().getResourceAsStream(symbolCode + ".png").readAllBytes();
        byte[] actual = pi.getImageAsByteArray();

        assertThat(expected).contains(actual);

    }

    @Test
    public void testUnknownSymbology() throws IOException {
        logger.info("testUnknownSymbology");
        List<String> unknown = Arrays.asList(
                "SUPP------*****",
                "SUPPS-----*****",
                "SUPPV-----*****",
                "SUPPT-----*****",
                "SUPPL-----*****",
                "SUAP------*****",
                "SUAPM-----*****",
                "SUAPMFKB--*****",
                "SUAPMFO---*****",
                "SUAPMFQA--*****",
                "SUAPMFM---*****",
                "SUAPML----*****",
                "SUAPWMS---*****",
                "SUGPUUSO--*****");

        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "24");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        for (String symbolCode : unknown) {
            logger.info("testing symbolCode: {}", symbolCode);
            PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);
            Path testFile = Paths.get("src", "test", "resources", "2525", "unknown", symbolCode + ".png");
            byte[] expected = Files.readAllBytes(testFile);
            byte[] actual = pi.getImageAsByteArray();
            assertThat(expected).contains(actual);
        }
    }

    @Test
    public void testNeutralSymbology() throws IOException {
        logger.info("testNeutralSymbology");
        List<String> neutral = Arrays.asList(
                "SNPP------*****",
                "SNPPS-----*****",
                "SNPPV-----*****",
                "SNPPT-----*****",
                "SNPPL-----*****",
                "SNAP------*****",
                "SNAPM-----*****",
                "SNAPMFKB--*****",
                "SNAPMFO---*****",
                "SNAPMFQA--*****",
                "SNAPMFM---*****",
                "SNAPML----*****",
                "SNAPWMS---*****",
                "SNGPUUSO--*****");

        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "24");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        for (String symbolCode : neutral) {
            logger.info("testing symbolCode: {}", symbolCode);
            PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);
            Path testFile = Paths.get("src", "test", "resources", "2525", "neutral", symbolCode + ".png");
            byte[] expected = Files.readAllBytes(testFile);
            byte[] actual = pi.getImageAsByteArray();
            assertThat(expected).contains(actual);
        }
    }

    @Test
    public void testHostileSymbology() throws IOException {
        logger.info("testHostileSymbology");

        List<String> hostile = Arrays.asList(
                "SHPP------*****",
                "SHPPS-----*****",
                "SHPPV-----***** ",
                "SHPPT-----*****",
                "SHPPL-----*****",
                "SHAP------*****",
                "SHAPM-----*****",
                "SHAPMFKB--*****",
                "SHAPMFO---*****",
                "SHAPMFQA--*****",
                "SHAPMFM---*****",
                "SHAPML----*****",
                "SHAPWMS---*****",
                "SHGPUUSO--*****");

        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "24");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        for (String symbolCode : hostile) {
            logger.info("testing symbolCode: {}", symbolCode);
            PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);
            Path testFile = Paths.get("src", "test", "resources", "2525", "hostile", symbolCode + ".png");
            byte[] expected = Files.readAllBytes(testFile);
            byte[] actual = pi.getImageAsByteArray();
            assertThat(expected).contains(actual);
        }
    }

    @Test
    public void testFriendlySymbology() throws IOException {
        logger.info("testFriendlySymbology");

        List<String> friend = Arrays.asList(
                "SFPP------*****",
                "SFPPS-----*****",
                "SFPPV-----*****",
                "SFPPT-----*****",
                "SFPPL-----*****",
                "SFAP------*****",
                "SFAPM-----*****",
                "SFAPMFKB--*****",
                "SFAPMFO---*****",
                "SFAPMFQA--*****",
                "SFAPMFM---*****",
                "SFAPML----*****",
                "SFAPWMS---*****",
                "SFGPUUSO--*****");

        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "24");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        for (String symbolCode : friend) {
            logger.info("testing symbolCode: {}", symbolCode);
            PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);
            Path testFile = Paths.get("src", "test", "resources", "2525", "friend", symbolCode + ".png");
            byte[] expected = Files.readAllBytes(testFile);
            byte[] actual = pi.getImageAsByteArray();
            assertThat(expected).contains(actual);
        }
    }

}
