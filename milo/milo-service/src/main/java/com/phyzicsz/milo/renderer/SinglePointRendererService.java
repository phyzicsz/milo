/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer;

import com.phyzicsz.milo.renderer.plugin.ISinglePointInfo;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael.spinelli
 */
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class SinglePointRendererService {

    private static final Logger logger = LoggerFactory.getLogger(SinglePointRendererService.class);

    private SinglePoint2525Renderer renderer = new SinglePoint2525Renderer();


    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 50
     */
    public void setSinglePointUnitsFontSize(int size) {
        renderer.setSinglePointUnitsFontSize(size);
    }

    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 60
     */
    public void setSinglePointTacticalGraphicFontSize(int size) {
        renderer.setSinglePointTacticalGraphicFontSize(size);
    }

    public ISinglePointInfo render(String symbolID, Map<String, String> params) {
        ISinglePointInfo returnVal = null;
        try {
            try {
                returnVal = renderer.render(symbolID, params);
            } catch (Exception ex) {
                String message = "failed to produce an image for symboldID \""
                        + symbolID + "\"";
                logger.error("render error: {}", message, ex);
            }
        } catch (Exception ex) {
            logger.error("render error", ex);
        } finally {
            return returnVal;
        }

    }

}
