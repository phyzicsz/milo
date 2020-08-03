/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer;

import com.phyzicsz.milo.renderer.common.RendererException;
import com.phyzicsz.milo.renderer.common.SymbolUtilities;
import com.phyzicsz.milo.renderer.common.ShapeInfo;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.common.ImageInfo;
import com.phyzicsz.milo.renderer.common.TacticalGraphicLookup;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.common.SinglePointFont;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael.spinelli
 */
public class TacticalGraphicIconRenderer {

    private static final Logger logger = LoggerFactory.getLogger(TacticalGraphicIconRenderer.class);

//    private static TacticalGraphicIconRenderer _instance = null;

    private Font _TacticalGraphicFont = null;
    private RendererSettings _RendererSettings = null;
    private final Object _tgFontMutex = new Object();
    private BufferedImage _buffer = null;
    private FontRenderContext _fontRenderContext = null;

    public TacticalGraphicIconRenderer() {
        try {
            //font size of 60 produces a 40x40 pixel image.
            float fontSizeForTGIcons = 60;

            _RendererSettings = RendererSettings.getInstance();
            _TacticalGraphicFont = SinglePointFont.getInstance().getTGFont(fontSizeForTGIcons);


            if (_TacticalGraphicFont == null) {
                RendererException ex = new RendererException("TacticalGraphicIconRenderer failed to initialize - _TacticalGraphicFont didn't load.", null);
                logger.error("error loading renderer", ex);
            }

            //trying to use just 1 image all the time
            //and one FontRenderContext
            if (_buffer == null) {
                _buffer = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) _buffer.createGraphics();
                _fontRenderContext = g2d.getFontRenderContext();
            }
        } catch (Exception ex) {
            logger.error("error loading renderer", ex);
        }
    }

    public boolean CanRender(String symbolID) {
        if (TacticalGraphicLookup.getInstance().getCharCodeFromSymbol(symbolID) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Instance of the TacticalGraphicIconRenderer
     *
     * @return the instance
     */
//    public static synchronized TacticalGraphicIconRenderer getInstance() {
//        if (_instance == null) {
//            _instance = new TacticalGraphicIconRenderer();
//        }
//
//        return _instance;
//    }

    /**
     * default size to 25x25 and color to affiliation color
     *
     * @param symbolID
     * @return
     */
    public ImageInfo getIcon(String symbolID) {
        int symStd = RendererSettings.getInstance().getSymbologyStandard();
        return getIcon(symbolID, 25, null, symStd);
    }

    /**
     * defaults color to affiliation color
     *
     * @param symbolID
     * @param iconHeightWidth
     * @return
     */
    public ImageInfo getIcon(String symbolID, int iconHeightWidth) {
        int symStd = RendererSettings.getInstance().getSymbologyStandard();
        return getIcon(symbolID, iconHeightWidth, null, symStd);
    }

    public ImageInfo getIcon(String symbolID, int iconHeightWidth, Color color) {
        int symStd = RendererSettings.getInstance().getSymbologyStandard();
        return getIcon(symbolID, iconHeightWidth, color, symStd);
    }

    /**
     *
     * @param symbolID ID of the MilStdSymbol
     * @param iconHeightWidth represents the dimensions of the icon (i.e. 25 =
     * 25x25 icon)
     * @param color overrides the default affiliation line color. If null, will
     * use default.
     * @param symStd 0 for 2525B, 1 for 2525C.
     * @return
     */
    public ImageInfo getIcon(String symbolID, int iconHeightWidth, Color color, int symStd) {
        try {
            MilStdSymbol ms = new MilStdSymbol(symbolID, null, null, null);
            FontRenderContext frc = _fontRenderContext;
            String id = symbolID;
            if (SymbolUtilities.isWeather(symbolID) == false) {
                id = symbolID.substring(0, 1) + "*"
                        + symbolID.substring(2, 3) + "P"
                        + symbolID.substring(4);

                if (color != null) {
                    ms.setLineColor(color);
                }
            } else {
                Color temp = SymbolUtilities.getFillColorOfWeather(symbolID);
                if (temp == null) {
                    temp = SymbolUtilities.getLineColorOfWeather(symbolID);
                }

                if (temp != null) {
                    ms.setLineColor(temp);
                }
            }

            int charSymbolIndex = TacticalGraphicLookup.getInstance().getCharCodeFromSymbol(id, symStd);

            if (charSymbolIndex >= 0) {
                //font size of 60 produces a 40x40 pixel image.
                double ratio = iconHeightWidth / 40.0;

                char[] symbol = new char[1];
                symbol[0] = (char) charSymbolIndex;

                Point pixel = new Point(0, 0);

                //create glyph vector
                GlyphVector gvSymbol = null;

                synchronized (_tgFontMutex) {
                    if (charSymbolIndex > 0) {
                        gvSymbol = _TacticalGraphicFont.createGlyphVector(frc, symbol);
                    }
                }

                Rectangle2D bounds = gvSymbol.getPixelBounds(frc, 0, 0);

                ratio = Math.min((iconHeightWidth / bounds.getHeight()), (iconHeightWidth / bounds.getWidth()));

                //resize to pixels
                if (ratio > 0) {
                    if (gvSymbol != null) {
                        gvSymbol.setGlyphTransform(0, AffineTransform.getScaleInstance(ratio, ratio));
                    }
                }

                ShapeInfo siSymbol = null;
                if (gvSymbol != null) {
                    siSymbol = new ShapeInfo(gvSymbol, pixel);
                }
                if (siSymbol != null) {
                    siSymbol.setLineColor(ms.getLineColor());
                }
                if (siSymbol != null) {
                    siSymbol.setShapeType(ShapeInfo.SHAPE_TYPE_TG_SP_FRAME);
                }
                ArrayList<ShapeInfo> shapes = new ArrayList<ShapeInfo>();

                if (siSymbol != null) {
                    shapes.add(siSymbol);
                }
                ms.setSymbolShapes(shapes);

                ImageInfo ii = ms.toImageInfo();
                //test
                //ii.SaveImageToFile("C:\\icon.png", ImageInfo.FormatPNG);
                return ii;

            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error("error getting icon", ex);
        }
        return null;
    }

}
