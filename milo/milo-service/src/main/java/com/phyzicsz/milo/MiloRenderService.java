/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo;

import com.phyzicsz.milo.renderer.common.RendererException;
import com.phyzicsz.milo.renderer.common.SymbolDefTable;
import com.phyzicsz.milo.renderer.common.ErrorLogger;
import com.phyzicsz.milo.renderer.common.IPointConversion;
import com.phyzicsz.milo.renderer.common.SymbolUtilities;
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
import java.util.logging.Level;
import javax.print.DocFlavor.BYTE_ARRAY;
import com.phyzicsz.milo.renderer.SinglePoint2525Renderer;
import com.phyzicsz.milo.renderer.SinglePointRendererService;
import com.phyzicsz.milo.renderer.utilities.JavaRendererUtilities;
import com.phyzicsz.milo.renderer.info.PNGInfo;

/**
 *
 * @author michael.spinelli
 */
/**
 * Provides a simpler interface to all the supporting rendering classes. From
 * here the user can start the single point server or use this class to create
 * their own, specify the location of which plugins to load, get an image from a
 * url string or a collections of parameters, or generate kml or json for
 * multipoint symbology.
 */
public class MiloRenderService {

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
     * \ Set minimum level at which an item can be logged. In descending order:
     * OFF = Integer.MAX_VALUE Severe = 1000 Warning = 900 Info = 800 Config =
     * 700 Fine = 500 Finer = 400 Finest = 300 All = Integer.MIN_VALUE Use like
     * SECRenderer.setLoggingLevel(Level.INFO); or Use like
     * SECRenderer.setLoggingLevel(800);
     *
     * @param level java.util.logging.level
     */
    public void setLoggingLevel(Level level) {
        try {
            ErrorLogger.setLevel(level, true);
            ErrorLogger.LogMessage("SECRenderer", "setLoggingLevel(Level)", "Logging level set to: "
                    + ErrorLogger.getLevel().getName(), Level.CONFIG);
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "setLoggingLevel(Level)", exc, Level.INFO);
        }
    }

    /**
     * \ Set minimum level at which an item can be logged. In descending order:
     * OFF = Integer.MAX_VALUE Severe = 1000 Warning = 900 Info = 800 Config =
     * 700 Fine = 500 Finer = 400 Finest = 300 All = Integer.MIN_VALUE Use like
     * SECRenderer.setLoggingLevel(Level.INFO); or Use like
     * SECRenderer.setLoggingLevel(800);
     *
     * @param level int
     */
    public void setLoggingLevel(int level) {
        try {
            if (level > 1000) {
                ErrorLogger.setLevel(Level.OFF, true);
            } else if (level > 900) {
                ErrorLogger.setLevel(Level.SEVERE, true);
            } else if (level > 800) {
                ErrorLogger.setLevel(Level.WARNING, true);
            } else if (level > 700) {
                ErrorLogger.setLevel(Level.INFO, true);
            } else if (level > 500) {
                ErrorLogger.setLevel(Level.CONFIG, true);
            } else if (level > 400) {
                ErrorLogger.setLevel(Level.FINE, true);
            } else if (level > 300) {
                ErrorLogger.setLevel(Level.FINER, true);
            } else if (level > Integer.MIN_VALUE) {
                ErrorLogger.setLevel(Level.FINEST, true);
            } else {
                ErrorLogger.setLevel(Level.ALL, true);
            }

            ErrorLogger.LogMessage("SECRenderer", "setLoggingLevel(int)", "Logging level set to: "
                    + ErrorLogger.getLevel().getName(), Level.CONFIG);
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "setLoggingLevel(int)", exc, Level.INFO);
        }
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
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
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
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
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
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "loadDefaultPlugins", exc);
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
        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "getMilStdSymbolImageFromURL", exc);
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
            if (jr.CanRender(ms)) {
                jr.Render(ms, ipc, null);
                ii = ms.toImageInfo();
            }
        } catch (RendererException exc) {
            ErrorLogger.LogException("SECRenderer", "getMilStdSymbolImage(MilStdSymbol)", exc);
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
        } catch (UnsupportedEncodingException exc) {
            ErrorLogger.LogException("SECRenderer", "getSymbolImageFromURL", exc);
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
                String message = "Lookup for 2525 renderer plugin failed.";
                ErrorLogger.LogMessage("SECRenderer", "getSymbolImage", message, Level.WARNING);
            }

        } catch (Exception exc) {
            ErrorLogger.LogException("SECRenderer", "getSymbolImage", exc);
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

    /**
     * Google likes to resize icons. Based on patterns I've recognized, I tried
     * to compensate.
     *
     * @param width
     * @param height
     * @return
     */
    private double getIconScale(double width, double height) {
        double scale1 = 28;
        double scale2 = 30;
        double iconScale;
        if (width == height) {
            iconScale = width / scale1;
        } else if (width > height) {
            if (height <= scale2) {
                iconScale = width / 28;
            } else {
                iconScale = height / 28;
            }
        } else {
            if (width <= scale2) {
                iconScale = height / 28;
            } else {
                iconScale = width / 28;
            }
        }
        return iconScale;
    }
}
