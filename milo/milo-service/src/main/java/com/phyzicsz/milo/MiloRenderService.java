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

import com.phyzicsz.milo.renderer.common.RendererException;
import com.phyzicsz.milo.renderer.common.IPointConversion;
import com.phyzicsz.milo.renderer.common.PointConversionDummy;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.common.ImageInfo;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.plugin.ISinglePointInfo;
import com.phyzicsz.milo.renderer.IJavaRenderer;
import com.phyzicsz.milo.renderer.JavaRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import javax.print.DocFlavor.BYTE_ARRAY;
import com.phyzicsz.milo.renderer.SinglePoint2525Renderer;
import com.phyzicsz.milo.renderer.SinglePointRendererService;
import com.phyzicsz.milo.renderer.utilities.JavaRendererUtilities;
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
    
    private SinglePointRendererService sprs = null;
    private IJavaRenderer jr = null;

    public MiloRenderService() {
        jr = JavaRenderer.getInstance();
        sprs = SinglePointRendererService.getInstance();
    }

    /**
     * Not meant to be changed on the fly. Let's user choose between 2525Bch2
     * and 2525C. 2525Bch2 = 0, 2525C = 1.
     *
     * @param symStd
     */
    public void setDefaultSymbologyStandard(int symStd) {
        RendererSettings.getInstance().setSymbologyStandard(symStd);
    }


    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 50
     */
    public void setSinglePointUnitsFontSize(int size) {
        jr.setUnitSymbolSize(size);
    }

    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 60
     */
    public void setSinglePointTacticalGraphicFontSize(int size) {
        jr.setSinglePointTGSymbolSize(size);
    }

    /**
     * Will attempt to download and load a plugin given a specific url Call
     * refreshPlugins() after you've loaded all the plugins you want the service
     * to make available.
     *
     * @param url
     */
    public void loadPluginsFromUrl(String url) {
        try {
            SinglePointRendererService.getInstance().AddRenderersToPath(url);
            // SinglePointRendererService.getInstance().LoadSPRendererServices();
        } catch (Exception ex) {
            logger.error("error loading plugins,", ex);
        }
    }

    /**
     * Attemps to Load a specfic file as a plugin Call refreshPlugins() after
     * you've loaded all the plugins you want the service to make available.
     *
     * @param file
     */
    public void loadPluginsFromFile(File file) {
        try {
            SinglePointRendererService.getInstance().AddRenderersToPathByFile(file);
            // SinglePointRendererService.getInstance().LoadSPRendererServices();
        } catch (Exception ex) {
            logger.error("error loading plugins,", ex);
        }
    }

    /**
     * Scans a directory and loads any plugins located there. Call
     * refreshPlugins() after you've loaded all the plugins you want the service
     * to make available.
     *
     * @param directory
     */
    public void loadPluginsFromDirectory(File directory) {
        try {
            SinglePointRendererService.getInstance().AddRenderersToPathByDirectory(directory);
            //SinglePointRendererService.getInstance().LoadSPRendererServices();
        } catch (Exception ex) {
            logger.error("error loading plugins", ex);
        }
    }

    /**
     * After loading plugins, you need to refresh the service so that it's aware
     * of the plugins that were made available.
     */
    public void refreshPlugins() {
        SinglePointRendererService.getInstance().LoadSPRendererServices();
    }

    /**
     * Gets a list of the loaded plugins
     *
     * @return a list of the currently loaded plugins.
     */
    public ArrayList<String> getListOfLoadedPlugins() {
        return SinglePointRendererService.getInstance().getSinglePointRendererIDs();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Single Point Functions">
    /**
     * Generates an image for a milstd symbol
     *
     * @param url assumes url ends like:
     * "/SFGP-----------?T=uniquedesignation_1&H=blah&H1=etc"
     * @return
     */
    public PNGInfo getMilStdSymbolImageFromURL(String url) {
        MilStdSymbol ms = null;
        try {
            String symbolID = (url.startsWith("/") ? url.substring(url.lastIndexOf("/") + 1) : url);
            ms = JavaRendererUtilities.createMilstdSymbol(symbolID);
        } catch (Exception ex) {
            logger.error("error rendering symbol", ex);
        }

        if (ms != null) {
            return getMilStdSymbolImage(ms);
        } else {
            return null;
        }
    }

    /**
     * Generates an image for a milstd symbol
     *
     * @param symbolId
     * @param symbolInfoMap
     * @return {@link BYTE_ARRAY}
     */
    public PNGInfo getMilStdSymbolImage(String symbolId, Map<String, String> symbolInfoMap) {

        MilStdSymbol ms = JavaRendererUtilities.createMilstdSymbol(symbolId, symbolInfoMap);

        return getMilStdSymbolImage(ms);
    }

    /**
     *
     * @param ms
     * @return
     */
    private PNGInfo getMilStdSymbolImage(MilStdSymbol ms) {
        IPointConversion ipc = new PointConversionDummy();
        ImageInfo ii = null;
        PNGInfo pi = null;
        try {
            if (jr.canRender(ms)) {
                jr.Render(ms, ipc, null);
                ii = ms.toImageInfo();
            }
        } catch (RendererException ex) {
            logger.error("error rendering symbol", ex);
        }
        if (ii != null) {
            pi = new PNGInfo(ii);
        } else {
            //System.out.println("ii is null");
        }

        return pi;
    }

    /**
     * Works the same as getMilStdSymbolImageFromURL but if you specify a
     * renderer, the function will tried to get the image from the specified
     * renderer plugin.
     *
     * @param url
     * @return
     */
    public PNGInfo getSymbolImageFromURL(String url) {
        String symbolID = "";
        Map<String, String> params = null;
        try {
            symbolID = (url.startsWith("/") ? url.substring(url.lastIndexOf("/") + 1) : url);
            params = JavaRendererUtilities.createParameterMapFromURL(symbolID);

            int questionIndex = symbolID.lastIndexOf('?');
            if (questionIndex != -1) {
                symbolID = java.net.URLDecoder.decode(symbolID.substring(0, questionIndex), "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error("unsupported encoding", ex);
        }
        return getSymbolImage(symbolID, params);
    }

    /**
     * Works the same as getMilStdSymbolImage but if you specify a renderer, the
     * function will tried to get the image from the specified renderer plugin.
     *
     * @param symbolId
     * @param symbolInfoMap
     * @return {@link BYTE_ARRAY}
     */
    public PNGInfo getSymbolImage(String symbolId, Map<String, String> symbolInfoMap) {

        PNGInfo pi = null;
        ISinglePointInfo spi;
        String rendererID = "";
        try {
            if (symbolInfoMap.containsKey("renderer")) {
                rendererID = symbolInfoMap.get("renderer");
            } else if (symbolInfoMap.containsKey("RENDERER")) {
                rendererID = symbolInfoMap.get("RENDERER");
            }
            //System.out.println("Requested Renderer ID: " + rendererID);

            // check if plugin renderer was requested
            if (rendererID == null || rendererID.equals("")) {
                rendererID = SinglePoint2525Renderer.RENDERER_ID;
            }
            if (sprs.hasRenderer(rendererID) == false) {
                //if renderer id doesn't exist or is no good, set to default plugin.
                rendererID = SinglePoint2525Renderer.RENDERER_ID;
            }

            if (sprs.hasRenderer(rendererID)) {
                //System.out.println("Renderer ID: " + rendererID);
                //System.out.println("Symbol ID: " + symbolId);
                //ErrorLogger.PrintStringMap(symbolInfoMap);
                spi = sprs.render(rendererID, symbolId, symbolInfoMap);
                if (spi != null) {
                    pi = new PNGInfo(spi);
                }
            } else {
                logger.error("Lookup for 2525 renderer plugin failed");
            }

        } catch (Exception ex) {
            logger.error("error getting symbol image", ex);
        }

        return pi;
    }

    /**
     * Makes an icon for use in things like nodes on a tree view.
     *
     * @param symbolID
     * @param iconSize height & width in pixels that you want the icon to be.
     * @param showDisplayModifiers things like echelon, mobility, HQ, feint,
     * etc.
     * @return
     */
    public BufferedImage getMilStdSymbolasIcon(String symbolID, int iconSize, Boolean showDisplayModifiers) {
        return jr.RenderMilStdSymbolAsIcon(symbolID, iconSize, showDisplayModifiers);
    }

}
