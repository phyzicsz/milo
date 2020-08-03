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
package com.phyzicsz.milo;

import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.plugin.ISinglePointInfo;
import java.util.Map;
import javax.print.DocFlavor.BYTE_ARRAY;
import com.phyzicsz.milo.renderer.SinglePointRendererService;
import com.phyzicsz.milo.renderer.info.PNGInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main rendering service.
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class MiloRenderService {

    private static final Logger logger = LoggerFactory.getLogger(MiloRenderService.class);

    private final SinglePointRendererService renderService = new SinglePointRendererService();


    public void setDefaultSymbologyStandard(int symStd) {
        RendererSettings.getInstance().setSymbologyStandard(symStd);
    }

    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 50
     */
    public void setSinglePointUnitsFontSize(int size) {
        renderService.setSinglePointUnitsFontSize(size);
    }

    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 60
     */
    public void setSinglePointTacticalGraphicFontSize(int size) {
        renderService.setSinglePointTacticalGraphicFontSize(size);
    }

    /**
     * Works the same as getMilStdSymbolImage but if you specify a renderer, the
     * function will tried to get the image from the specified renderer plugin.
     *
     * @param symbolId
     * @param symbolInfoMap
     * @return {@link BYTE_ARRAY}
     */
    public PNGInfo getMilStdSymbolImage(String symbolId, Map<String, String> symbolInfoMap) {

        PNGInfo pi = null;
        ISinglePointInfo spi;
        try {
            spi = renderService.render(symbolId, symbolInfoMap);
            if (spi != null) {
                pi = new PNGInfo(spi);
            }
        } catch (Exception ex) {
            logger.error("error getting symbol image", ex);
        }

        return pi;
    }
}
