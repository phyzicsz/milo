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
import ArmyC2.C2SD.Utilities.MilStdAttributes;
import ArmyC2.C2SD.Utilities.MilStdSymbol;
import ArmyC2.C2SD.Utilities.RendererSettings;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.phyzicsz.milo.renderer.info.PNGInfo;

/**
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class MiloRenderServiceTest {

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
        System.out.println("setDefaultSymbologyStandard");

        MiloRenderService service = new MiloRenderService();
        service.setDefaultSymbologyStandard(RendererSettings.Symbology_2525C);
        service.setSinglePointUnitsFontSize(24);

        Map<String, String> params = new HashMap<>();

        params.put(MilStdAttributes.PixelSize, "50");
        params.put(MilStdAttributes.KeepUnitRatio, "true");//default is true

        String symbolCode = "SFUPSK----*****";
        PNGInfo pi = service.getMilStdSymbolImage(symbolCode, params);

        byte[] expected = getClass().getClassLoader().getResourceAsStream(symbolCode+".png").readAllBytes();
        byte[] actual = pi.getImageAsByteArray();
        
        assertThat(expected).contains(actual);

    }

}
