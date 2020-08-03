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
package com.phyzicsz.milo.renderer;

import com.phyzicsz.milo.renderer.common.UnitDef;
import com.phyzicsz.milo.renderer.common.RendererException;
import com.phyzicsz.milo.renderer.common.SymbolDefTable;
import com.phyzicsz.milo.renderer.common.PointConversionDummy;
import com.phyzicsz.milo.renderer.common.ModifiersTG;
import com.phyzicsz.milo.renderer.common.SymbolDraw;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.common.ImageInfo;
import com.phyzicsz.milo.renderer.common.UnitFontLookupInfo;
import com.phyzicsz.milo.renderer.common.UnitDefTable;
import com.phyzicsz.milo.renderer.common.IPointConversion;
import com.phyzicsz.milo.renderer.common.SymbolUtilities;
import com.phyzicsz.milo.renderer.common.SinglePointLookup;
import com.phyzicsz.milo.renderer.common.MilStdAttributes;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.common.UnitFontLookup;
import com.phyzicsz.milo.renderer.common.SymbolDef;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rendering service.
 *
 * @author phyzicsz <phyzics.z@gmail.com>
 */
public class JavaRenderer implements IJavaRenderer {

    private static final Logger logger = LoggerFactory.getLogger(JavaRenderer.class);

    private static SinglePointRenderer singlePointRenderer = null;
    private static TacticalGraphicIconRenderer tacticalGraphicsRenderer = null;

    private SymbolDefTable _SymbolDefTable = null;

    //Unit 2525C sizes
    public static final int UnitSizeMedium = 40;
    public static final int UnitSizeSmall = 30;
    public static final int UnitSizeLarge = 50;
    public static final int UnitSizeXL = 60;

    //TG & unit 2525B sizes
    public static final int SymbolSizeMedium = 80;
    public static final int SymbolSizeSmall = 60;
    public static final int SymbolSizeLarge = 100;
    public static final int SymbolSizeXL = 120;

    /**
     *
     */
    public JavaRenderer() {

        try {

            singlePointRenderer = new SinglePointRenderer();
            tacticalGraphicsRenderer = new TacticalGraphicIconRenderer();

            if (singlePointRenderer == null) {
                logger.error("failed to initialize single point renderer");
            }
            if (tacticalGraphicsRenderer == null) {
                logger.error("failed to initialize tactical renderer");
            }

        } catch (Exception ex) {
            logger.error("failed to initialize", ex);
        }
    }
    
    /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 50
     */
    public void setSinglePointUnitsFontSize(int size) {
        setUnitSymbolSize(size);
    }
    
   /**
     * Determines size of the symbol assuming no pixel size is specified
     *
     * @param size default 60
     */
    public void setSinglePointTacticalGraphicFontSize(int size) {
        setSinglePointTGSymbolSize(size);
    }

    /**
     * Takes a symbol and determines if it is renderable
     *
     * @param symbol
     * @return true if symbol can be rendered based on provided information
     */
    @Override
    public Boolean canRender(MilStdSymbol symbol) {
        //String basicSymbolID =  symbol.getSymbolID();
        return canRender(symbol.getSymbolID(), symbol.getCoordinates(), symbol.getSymbologyStandard());
    }

    @Override
    public Boolean canRender(String symbolCode,
            ArrayList<Point2D.Double> coords) {
        return canRender(symbolCode,
                coords,
                RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     * takes symbol properties and determines if they can be rendered as a
     * symbol
     *
     * @param symbolCode
     * @param coords
     * @return true if symbol can be rendered based on provided information
     */
    @Override
    public Boolean canRender(String symbolCode,
            ArrayList<Point2D.Double> coords,
            int symStd) {
        String message = null;
        String basicSymbolID = symbolCode;
        basicSymbolID = SymbolUtilities.getBasicSymbolIDStrict(basicSymbolID);
        //ErrorLogger.LogMessage("TEST");
        try {
            // message = "Cannot draw: " + symbolCode + " (" + basicSymbolID + ")";
            if (SymbolUtilities.isTacticalGraphic(basicSymbolID)) {
                if (_SymbolDefTable == null) {
                    _SymbolDefTable = SymbolDefTable.getInstance();
                }

                SymbolDef sd = _SymbolDefTable.getSymbolDef(basicSymbolID, symStd);
                if (sd != null) {
                    int pointCount = 0;
                    if (coords != null) {
                        pointCount = coords.size();
                    }

                    int dc = sd.getDrawCategory();
                    if (dc == SymbolDef.DRAW_CATEGORY_POINT)//make sure we can find the character in the font.
                    {
                        int index = SinglePointLookup.getInstance().getCharCodeFromSymbol(symbolCode, symStd);
                        if (index > 0) {
                            return true;
                        } else {
                            message = "Bad font lookup for: " + symbolCode + " (" + basicSymbolID + ")";
                        }
                    } else if (dc > 0 && dc < 99) {
                        if (sd.getMinPoints() == sd.getMaxPoints()) {    //complex graphic like ambush

                            if (pointCount == sd.getMinPoints()) {
                                return true;
                            } else {
                                message = "Specific point count not met for: " + symbolCode + " (" + basicSymbolID + ") - Had: " + String.valueOf(pointCount) + " Needed: " + String.valueOf(sd.getMinPoints());
                            }
                        } else if (pointCount >= sd.getMinPoints()) {
                            return true;
                        } else if (sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POLYGON
                                && pointCount == 2 && sd.getMinPoints() == 3 && sd.getMaxPoints() > 100) {//areas with 2 points are allowable.
                            return true;
                        } else if (pointCount < sd.getMinPoints()) {
                            message = symbolCode + " had less than the required number of points. Had: " + String.valueOf(coords.size()) + " Needed: " + String.valueOf(sd.getMinPoints());
                        }
                    } else {
                        message = "Cannot draw: " + symbolCode + " (" + basicSymbolID + ")";
                    }

                } else {
                    message = "Cannot draw symbolID: " + symbolCode + " (" + basicSymbolID + ")";
                }
            } else {
                //UnitDef ud =  UnitDefTable.getInstance().getUnitDef(basicSymbolID,symStd);
                UnitFontLookupInfo ufli = UnitFontLookup.getInstance().getLookupInfo(basicSymbolID, symStd);
                if (ufli != null) {
                    return true;
                } else {
                    message = "JavaRenderer.CanRender() - Cannot draw symbolID: " + symbolCode + " (" + basicSymbolID + ")";
                }
            }

            if (message != null && !message.equals("")) {
                logger.error(message);
            }
        } catch (Exception ex) {
            logger.error("java renderer error");
        }
        return false;
    }
    
     /**
     * Takes a string and parses information to build a MilStdSymbol
     *
     * @param SymbolInfo something like
     * "SymbolID?LineColor=0x000000&FillColor=0xFFFFFF&size=35"
     * @return
     * @author Spinelli
     */
    public MilStdSymbol createMilstdSymbol(String SymbolInfo) {
        String symbolID = null;
        String parameters = null;
        String key = null;
        String value = null;
        String arrParameters[] = null;
        String arrKeyValue[] = null;
        String temp = null;

        Map<String, String> modifiers = new HashMap<String, String>();

        int questionIndex = SymbolInfo.lastIndexOf('?');
        try {
            if (questionIndex == -1) {
                symbolID = java.net.URLDecoder.decode(SymbolInfo, "UTF-8");
            } else {
                symbolID = java.net.URLDecoder.decode(SymbolInfo.substring(0, questionIndex), "UTF-8");
            }
            //if we're getting good codes, should never get here
            if (symbolID.length() < 15) {
                while (symbolID.length() < 15) {
                    symbolID += "-";
                }
            }
        } catch (Exception ex) {
            logger.error("error parsing symbolId: {}", symbolID, ex);
        }

        try {   //build a map for the other createMilstdSymbol function to use
            //to build a milstd symbol.
            if (questionIndex > 0 && (questionIndex + 1 < SymbolInfo.length())) {
                parameters = SymbolInfo.substring(questionIndex + 1, SymbolInfo.length());
                arrParameters = parameters.split("&");

                for (int i = 0; i < arrParameters.length; i++) {
                    arrKeyValue = arrParameters[i].split("=");
                    if (arrKeyValue.length == 2 && arrKeyValue[1] != null && arrKeyValue[1].equals("") == false) {

                        key = arrKeyValue[0];
                        value = arrKeyValue[1];

                        temp = java.net.URLDecoder.decode(value, "UTF-8");
                        modifiers.put(key, temp);
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error("error parsing key: {}", key, ex);
        }

        return createMilstdSymbol(symbolID, modifiers);

    }
    
    /**
     * Takes a string and parses information to build a MilStdSymbol
     *
     * @param SymbolID
     * @param params
     * @return
     * @author Spinelli
     */
    public MilStdSymbol createMilstdSymbol(String symbolID, Map<String, String> params) {
        MilStdSymbol symbol = null;
        String key = null;
        String value = null;
        String iconColor = null;
        String lineColor = null;
        String fillColor = null;
        String textColor = null;
        String textBackgroundColor = null;
        String size = null;
        String scale = null;
        String keepUnitRatio = null;
        String alpha = null;
        String symbolOutlineWidth = null;
        String symbolOutlineColor = null;
        String symbologyStandard = null;
        String temp = null;

        //ArrayList<String> tgModifier = ModifiersTG.GetModifierList();
        //ArrayList<String> feModifier = ModifiersUnits.GetModifierList();
        Map<String, String> modifiers = new HashMap<String, String>();

        try {
            if (params != null && params.isEmpty() == false) {
                for (Map.Entry<String, String> entry : params.entrySet()) {

                    key = entry.getKey();
                    value = entry.getValue();

                    if (key.equalsIgnoreCase(MilStdAttributes.LineColor)) {
                        lineColor = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.FillColor)) {
                        fillColor = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.IconColor)) {
                        iconColor = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.TextColor)) {
                        textColor = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.TextBackgroundColor)) {
                        textBackgroundColor = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.PixelSize)) {
                        size = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.Scale)) {
                        if (SymbolUtilities.isNumber(value)) {
                            scale = value;
                        }
                    } else if (key.equalsIgnoreCase(MilStdAttributes.KeepUnitRatio)) {
                        keepUnitRatio = value;
                    } else if (key.equalsIgnoreCase(MilStdAttributes.Alpha)) {
                        if (SymbolUtilities.isNumber(value)) {
                            alpha = value;
                        }
                    } else if ((key.equalsIgnoreCase(MilStdAttributes.OutlineSymbol))) {
                        symbolOutlineWidth = value;
                    } else if ((key.equalsIgnoreCase(MilStdAttributes.OutlineColor))) {
                        symbolOutlineColor = value;
                    } else if ((key.equalsIgnoreCase(MilStdAttributes.SymbologyStandard))) {
                        symbologyStandard = value;
                    }

                    //temp = value.toString();
                    modifiers.put(key, value);
                }
            }
        } catch (Exception ex) {
            logger.error("error parsing key: {}", key, ex);
        }

        try {
            //BUILD SYMBOL AND SET PROPERTIES
            ArrayList<Point2D.Double> coordinates = new ArrayList<Point2D.Double>();
            coordinates.add(new Point2D.Double(0.0, 0.0));
            //create modifiers

            symbol = new MilStdSymbol(symbolID, null, coordinates, modifiers);

            //Set Symbology Standard////////////////////////////////////////
            if (symbologyStandard != null) {
                if (symbologyStandard.equalsIgnoreCase("2525B")) {
                    symbol.setSymbologyStandard(RendererSettings.SYMBOLOGY_2525B);
                } else {
                    symbol.setSymbologyStandard(RendererSettings.SYMBOLOGY_2525C);
                }
            }

            SymbolDef sd = null;
            Boolean isMultiPoint = false;
            if (SymbolUtilities.isTacticalGraphic(symbolID)) {
                sd = SymbolDefTable.getInstance().getSymbolDef(SymbolUtilities.getBasicSymbolID(symbolID), symbol.getSymbologyStandard());
                if (sd != null && sd.getDrawCategory() != SymbolDef.DRAW_CATEGORY_POINT) {
                    if (tacticalGraphicsRenderer.CanRender(symbolID));
                    {
                        isMultiPoint = true;
                    }
                }
            }
            if (isMultiPoint == false) {
                if (canRender(symbolID, null, symbol.getSymbologyStandard()) == false) {
                    symbolID = SymbolUtilities.reconcileSymbolID(symbolID, isMultiPoint);
                    symbol.setSymbolID(symbolID);
                    symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
                    symbol.setFillColor(SymbolUtilities.getFillColorOfAffiliation(symbolID));
                }
            }

            //set image size in pixels//////////////////////////////////////
            int unitSize;
            if (size != null && SymbolUtilities.isNumber(size)) {
                unitSize = Integer.valueOf(size);
                symbol.setUnitSize(unitSize);
            } else if (SymbolUtilities.isTacticalGraphic(symbolID) == false
                    && SymbolUtilities.isWeather(symbolID) == false) {
                unitSize = 35;
                symbol.setUnitSize(unitSize);
            }

            //set scaling value for single point tactical graphics
            if (scale != null && SymbolUtilities.isNumber(scale)) {
                symbol.setScale(Double.parseDouble(scale));
                //symbol.setUnitSize(0);
            }

            //keep unit size relative to other symbols//////////////////////
            if (keepUnitRatio != null) {
                symbol.setKeepUnitRatio(Boolean.parseBoolean(keepUnitRatio));
            } else {
                //will make sure the units keep size relative to each other
                //assuming google earth doesn't resize them.
                symbol.setKeepUnitRatio(Boolean.TRUE);
            }

            if (lineColor != null) {

                try {
                    Color lc = SymbolUtilities.getColorFromHexString(lineColor);
                    symbol.setLineColor(lc);
                } catch (Exception ex) {
                    logger.error("error parsing line color: {}", lineColor, ex);
                }
            }

            if (fillColor != null) {
                try {
                    Color fc = SymbolUtilities.getColorFromHexString(fillColor);
                    symbol.setFillColor(fc);
                } catch (Exception ex) {
                    logger.error("error parsing fill color: {}", fillColor, ex);
                }
            }

            if (textColor != null) {
                try {
                    Color tc = SymbolUtilities.getColorFromHexString(textColor);
                    symbol.setTextColor(tc);
                } catch (Exception ex) {
                    logger.error("error parsing text color: {}", textColor, ex);
                }
            }

            if (iconColor != null) {
                try {
                    Color ic = SymbolUtilities.getColorFromHexString(iconColor);
                    symbol.setIconColor(ic);
                } catch (Exception ex) {
                    logger.error("error parsing icon color: {}", iconColor, ex);
                }
            }

            if (textBackgroundColor != null) {
                try {
                    Color tbc = SymbolUtilities.getColorFromHexString(textBackgroundColor);
                    symbol.setTextBackgroundColor(tbc);
                } catch (Exception ex) {
                    logger.error("error parsing text bg color: {}", textColor, ex);
                }
            }

            if (alpha != null) {
                Color temp1 = symbol.getLineColor();
                Color temp2 = symbol.getFillColor();
                if (SymbolUtilities.isNumber(alpha)) {

                    int A = Integer.parseInt(alpha);
                    if (A < 0 || A > 255) {
                        A = 255;
                    }

                    symbol.setLineColor(new Color(temp1.getRed(), temp1.getGreen(), temp1.getBlue(), A));
                    symbol.setFillColor(new Color(temp2.getRed(), temp2.getGreen(), temp2.getBlue(), A));
                }
            }

            //outline single point symbols
            if (symbolOutlineWidth != null) {
                int width = 0;
                try {
                    width = Integer.parseInt(symbolOutlineWidth);
                    if (width > 0) {
                        symbol.setOutlineEnabled(true, width);
                    } else {
                        symbol.setOutlineEnabled(false, 0);
                    }
                } catch (NumberFormatException nfe) {
                    //do nothing
                }
            }

            if (symbol.getOutlineEnabled());
            {
                if (symbolOutlineColor != null) {
                    symbol.setOutlineColor(SymbolUtilities.getColorFromHexString(symbolOutlineColor));

                }
            }

        } catch (Exception ex) {
            logger.error("error building symbol", ex);
        }

        return symbol;
    }

    /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     *
     * @param symbol
     * @param converter does point conversion between pixels & lat/lon
     * coordinates.
     * @param clipBounds dimensions of drawing surface. needed to do clipping.
     * @return drawable symbol populated with shape data
     * @throws RendererException
     */
    @Override
    public MilStdSymbol Render(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds) throws RendererException {
        ProcessSymbolGeometry(symbol, converter, clipBounds);
        return symbol;
    }

    /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     *
     * @param symbols
     * @param converter does point conversion between pixels & lat/lon
     * coordinates.
     * @param clipBounds dimensions of drawing surface. needed to do clipping.
     * @return drawable symbols populated with shape data
     */
    @Override
    public ArrayList<MilStdSymbol> Render(ArrayList<MilStdSymbol> symbols, IPointConversion converter, Rectangle2D clipBounds) throws RendererException {
        ProcessSymbolGeometryBulk(symbols, converter, clipBounds);
        return symbols;
    }

    /**
     * Populates the Symbol & Modifier Shape collection of the milstdsymbol
     *
     * @param symbolCode
     * @param UUID
     * @param coords
     * @param Modifiers
     * @param converter does point conversion between pixels & lat/lon
     * coordinates.
     * @param clipBounds dimensions of drawing surface. needed to do clipping.
     * @return drawable symbol populated with shape data
     */
    @Override
    public MilStdSymbol Render(String symbolCode, String UUID, ArrayList<Point2D.Double> coords, Map<String, String> Modifiers, IPointConversion converter, Rectangle2D clipBounds) throws RendererException {
        MilStdSymbol symbol = null;
        //try
        //{
        symbol = new MilStdSymbol(symbolCode, UUID, coords, Modifiers);
        ProcessSymbolGeometry(symbol, converter, clipBounds);
        //}
        //catch(RendererException re)
        //{
        //    throw re;
        //}

        return symbol;
    }

    @Override
    public ImageInfo RenderSinglePointAsImageInfo(String symbolCode, Map<String, String> Modifiers, int unitSize, boolean keepUnitRatio) {
        return RenderSinglePointAsImageInfo(symbolCode, Modifiers, unitSize, keepUnitRatio, RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     * @param symbolCode
     * @param Modifiers
     * @param unitSize 35 would make a an image where the core symbol is 35x35.
     * label modifiers and display modifiers may fall outside of that area and
     * final image may be bigger than 35x35. use getSinglePointTGSymbolSize or
     * getUnitSymbolSize.
     * @param keepUnitRatio Recommend setting to true when drawing on a map.
     * Only applies to force elements (units). If KeepUnitRatio is set, Symbols
     * will be drawn with respect to each other. Unknown unit is the all around
     * biggest, neutral unit is the smallest. if size is 35, neutral would be
     * (35/1.5)*1.1=25.7
     * @param symStd
     * @return ImageInfo, which has the image and all the information needed to
     * position it properly.
     */
    public ImageInfo RenderSinglePointAsImageInfo(String symbolCode, Map<String, String> Modifiers, int unitSize, boolean keepUnitRatio, int symStd) {
        ImageInfo returnVal = null;
        try {
            ArrayList<Point2D.Double> points = new ArrayList<>();
            //fake point.  cpof knows where they want to render so they don't 
            //give us a valid point.
            points.add(new Point2D.Double(0, 0));

            IPointConversion ipc = new PointConversionDummy();
            MilStdSymbol symbol = null;
            symbol = new MilStdSymbol(symbolCode, null, points, Modifiers);
            symbol.setUnitSize(unitSize);
            symbol.setKeepUnitRatio(keepUnitRatio);
            symbol.setSymbologyStandard(symStd);

            if (Modifiers.containsKey(MilStdAttributes.LineColor)) {
                symbol.setLineColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.LineColor)));
            }
            if (Modifiers.containsKey(MilStdAttributes.FillColor)) {
                symbol.setFillColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.FillColor)));
            }
            if (Modifiers.containsKey(MilStdAttributes.IconColor)) {
                symbol.setIconColor(SymbolUtilities.getColorFromHexString(Modifiers.get(MilStdAttributes.IconColor)));
            }

            ProcessSymbolGeometry(symbol, ipc, null);
            returnVal = symbol.toImageInfo();
        } catch (RendererException exc) {
                        logger.error("error rendering symbol",exc);
        }
        return returnVal;
    }

    /**
     * Given parameters, generates an ImageInfo object. Works for all symbols
     *
     * @param symbolCode
     * @param UUID
     * @param coords
     * @param Modifiers
     * @param converter
     * @param clipBounds
     * @return
     */
    public ImageInfo RenderMilStdSymbolAsImageInfo(String symbolCode, String UUID, ArrayList<Point2D.Double> coords, Map<String, String> Modifiers, IPointConversion converter, Rectangle2D clipBounds) {
        ImageInfo returnVal = null;
        try {
            MilStdSymbol symbol = new MilStdSymbol(symbolCode, UUID, coords, Modifiers);
            ProcessSymbolGeometry(symbol, converter, clipBounds);
            returnVal = symbol.toImageInfo();
        } catch (RendererException exc) {
            logger.error("error rendering symbol",exc);
        }
        return returnVal;
    }

    /**
     * Generates an imageInfo object
     *
     * @param symbol
     * @param converter
     * @param clipBounds
     * @return
     */
    public ImageInfo RenderMilStdSymbolAsImageInfo(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds) {
        ImageInfo returnVal = null;
        try {
            ProcessSymbolGeometry(symbol, converter, clipBounds);
            returnVal = symbol.toImageInfo();
        } catch (RendererException exc) {
            logger.error("error rendering symbol",exc);
        }
        return returnVal;
    }

    /**
     * Doesn't support multipoints yet. Renders using the default symbology
     * Standard specified here:
     * RendererSettings.getInstance().getSymbologyStandard());
     *
     * @param symbolID
     * @param iconSize
     * @param showDisplayModifiers
     * @return
     */
    @Override
    public BufferedImage RenderMilStdSymbolAsIcon(String symbolID, int iconSize,
            Boolean showDisplayModifiers) {
        return RenderMilStdSymbolAsIcon(symbolID, iconSize, showDisplayModifiers,
                RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     *
     * @param symbolID
     * @param iconSize
     * @param showDisplayModifiers
     * @param symStd
     * @return
     */
    public BufferedImage RenderMilStdSymbolAsIcon(String symbolID, int iconSize,
            Boolean showDisplayModifiers, int symStd) {
        BufferedImage returnVal = null;
        try {
            ImageInfo ii = null;
            SymbolDef sd = null;
            Map<String, String> Modifiers = new HashMap<>();

            ArrayList<Point2D.Double> points = new ArrayList<>();
            points.add(new Point2D.Double(0, 0));

            MilStdSymbol symbol = null;
            symbol = new MilStdSymbol(symbolID, null, points, Modifiers);
            //symbol.setUnitSize(iconSize);
            //symbol.setKeepUnitRatio(false);
            if (showDisplayModifiers) {
                symbol.setDrawAffiliationModifierAsLabel(false);
            }

            symbol.setModifier("showdisplaymodifiers", showDisplayModifiers.toString());

            if (SymbolUtilities.isTacticalGraphic(symbolID)) {
                sd = SymbolDefTable.getInstance().getSymbolDef(SymbolUtilities.getBasicSymbolID(symbolID), symStd);
                if (sd != null && (sd.getDrawCategory() != SymbolDef.DRAW_CATEGORY_POINT)) {
                    //call TG icon renderer for multipoints
                    ii = tacticalGraphicsRenderer.getIcon(symbolID, iconSize, null, symbol.getSymbologyStandard());
                } else {
                    ii = RenderSinglePointAsImageInfo(symbolID, Modifiers, iconSize, false);
                }
            } else {
                ii = RenderSinglePointAsImageInfo(symbolID, Modifiers, iconSize, false);
            }

            if (showDisplayModifiers == true) {
                returnVal = ImageInfo.getScaledInstance(ii.getImage(), iconSize, iconSize, RenderingHints.VALUE_INTERPOLATION_BILINEAR, false, true);
            } else {
                returnVal = ii.getImage();
            }

            //redraw to fit size.  at 35x35, action point may turn out like
            //15x35.  So redraw in the middle of a blank 35x35 image.
            int type = (returnVal.getTransparency() == Transparency.OPAQUE)
                    ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

            int w = iconSize;
            int h = iconSize;
            int x = 0;
            int y = 0;

            BufferedImage tmp = new BufferedImage(w, h, type);

            int ow = returnVal.getWidth();//original width
            int oh = returnVal.getHeight();//original height
            if (ow < w) {
                x = (w - ow) / 2;
                w = w - x;
            }
            if (oh < h) {
                y = (h - oh) / 2;
                h = h - y;
            }

            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(returnVal, x, y, w, h, null);
            g2.dispose();

            returnVal = tmp;
        } catch (Exception exc) {
            logger.error("java renderer - render as icon");
        }
        return returnVal;
    }

    /**
     * Does the actual drawing of the Symbol. MilstdSymbol need to be properly
     * populated via the Render call first. Draws to an offscreen image that
     * blits the result to the desination Graphics2D object.
     *
     * @param symbol
     * @param destination surface to draw to
     * @param clip Cannot be null. This function does not apply it to the
     * destination object. Clip dimesions are used to determine the size of the
     * back buffer. Also useful for making sure only an specfic area is being
     * redrawn. It shouldn't be bigger than the drawing area, but it can be a
     * section of the drawing area. like if the draw area is 400x400 and the
     * clip is x200,y200,w200,h200; the bottom right quadrant is the only part
     * that will be drawn and the back buffer will only be 200x200. Or you can
     * simply have the dimensions of the clip match the dimensions of the draw
     * area.
     * @throws RendererException
     */
    @Override
    public void DrawDB(MilStdSymbol symbol, Graphics2D destination, Rectangle clip) throws RendererException {
        ArrayList<MilStdSymbol> symbols = new ArrayList<>();
        symbols.add(symbol);
        DrawDB(symbols, destination, clip);
    }

    /**
     * Does the actual drawing of the Symbol. MilstdSymbol need to be properly
     * populated via the Render call first. Draws to an offscreen image that
     * blits the result to the destination Graphics2D object.
     *
     * @param symbols
     * @param destination surface to draw to
     * @param clip Cannot be null. This function does not apply it to the
     * destination object. Clip dimensions are used to determine the size of the
     * back buffer. Also useful for making sure only a specific area is being
     * redrawn. It shouldn't be bigger than the drawing area, but it can be a
     * section of the drawing area. like if the draw area is 400x400 and the
     * clip is x200,y200,w200,h200; the bottom right quadrant is the only part
     * that will be drawn and the back buffer will only be 200x200. Or you can
     * simply have the dimensions of the clip match the dimensions of the draw
     * area.
     * @throws RendererException
     */
    @Override
    public void DrawDB(ArrayList<MilStdSymbol> symbols, Graphics2D destination, Rectangle clip) throws RendererException {
        try {

            if (symbols != null && destination != null && clip != null && clip.width > 0 && clip.height > 0) {
                //make buffer image
                BufferedImage buffer = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = (Graphics2D) buffer.createGraphics();
                //graphics.setClip(0, 0, clip.width, clip.height);
                Draw(symbols, graphics, -clip.x, -clip.y);

                //draw offscreen image to the screen
                //synchronized(destination)
                //{
                destination.drawImage(buffer, clip.x, clip.y, null);
                //}

                graphics.dispose();
                graphics = null;
                buffer.flush();
                buffer = null;

            } else {
                //parameters are bad, throw exception
                String badValues = "Bad parameters passed: ";
                if (symbols == null) {
                    badValues += " symbols";
                }
                if (destination == null) {
                    badValues += " destination";
                }
                if (clip == null) {
                    badValues += " clip";
                } else {
                    if (clip.width < 1) {
                        badValues += " clip.width";
                    }
                    if (clip.height < 1) {
                        badValues += " clip.height";
                    }
                }

                RendererException re = new RendererException(badValues, null);
                logger.error("Error", re);
                throw re;
            }

        } catch (RendererException ex) {
            RendererException re2 = new RendererException("Draw Operation Failed", ex);
            logger.error("java renderer - draw operation failed", re2);
            throw re2;
        }
    }

    /**
     * Does the actual drawing of the Symbol. MilstdSymbol need to be properly
     * populated via the Render call first.
     *
     * @param symbol
     * @param destination surface to draw to
     * @throws RendererException
     */
    @Override
    public void Draw(MilStdSymbol symbol, Graphics2D destination) throws RendererException {
        ArrayList<MilStdSymbol> symbols = new ArrayList<>();
        symbols.add(symbol);
        Draw(symbols, destination, 0, 0);
    }

    /**
     * Does the actual drawing of the Symbol. MilstdSymbol need to be properly
     * populated via the Render call first.
     *
     * @param symbols
     * @param destination surface to draw to
     * @throws RendererException
     */
    @Override
    public void Draw(ArrayList<MilStdSymbol> symbols, Graphics2D destination) throws RendererException {
        Draw(symbols, destination, 0, 0);
    }

    /**
     * Does the actual drawing of the Symbol. MilstdSymbol need to be properly
     * populated via the Render call first.
     *
     * @param units
     * @param destination surface to draw to
     * @param offsetX usually a negative value. if your clip.X is 40, offsetX
     * should be -40
     * @param offsetY usually a negative value. if your clip.Y is 40, offsetY
     * should be -40
     */
    private void Draw(ArrayList<MilStdSymbol> symbols, Graphics2D destination, int offsetX, int offsetY) throws RendererException {
        try {

            if (symbols != null && destination != null) {

                SymbolDraw.Draw(symbols, destination, offsetX, offsetY);

            } else {
                //parameters are bad, throw exception
                String badValues = "Bad parameters passed: ";
                if (symbols == null) {
                    badValues += " symbols";
                }
                if (destination == null) {
                    badValues += " destination";
                }

                RendererException re = new RendererException(badValues, null);
                logger.error("java renderer - render error", re);
                throw re;

            }

        } catch (RendererException ex) {
            RendererException re2 = new RendererException("Draw Operation Failed", ex);
            logger.error("render exception", re2);
            throw re2;
        }
    }

    /**
     * Get a Map of the supported Unit or Force Element symbols
     *
     * @return a Map of UnitDefs keyed by symbol code.
     */
    public Map<String, UnitDef> getSupportedFETypes(int symStd) {
        return UnitDefTable.getInstance().GetAllUnitDefs(symStd);
    }

    /**
     * Get a Map of the supported Tactical Graphic symbols.
     *
     * @return a Map of SymbolDefs keyed by symbol code.
     */
    @Override
    public Map<String, SymbolDef> getSupportedTGTypes(int symStd) {

        Map<String, SymbolDef> types = new HashMap<>();
        Map<String, SymbolDef> defs = SymbolDefTable.getInstance().GetAllSymbolDefs(symStd);

        Collection<SymbolDef> symbols = defs.values();
        Iterator<SymbolDef> itr = symbols.iterator();
        SymbolDef item;

        while (itr.hasNext()) {
            item = itr.next();
            if (!SymbolUtilities.isMCSSpecificTacticalGraphic(item)) {
                types.put(item.getBasicSymbolId(), item);
            }
        }
        return types;
    }

    public int getSinglePointTGSymbolSize() {
        return singlePointRenderer.getSinglePointTGSymbolSize();
    }

    @Override
    public int getUnitSymbolSize() {
        return singlePointRenderer.getUnitSymbolSize();
    }

    @Override
    public void setSinglePointTGSymbolSize(int size) {
        singlePointRenderer.setSinglePointTGSymbolSize(size);
    }

    @Override
    public void setUnitSymbolSize(int size) {
        singlePointRenderer.setUnitSymbolSize(size);
    }

    /**
     * Set the label font to be used in the renderer Default tracking to
     * TextAttribute.TRACKING_LOOSE and kerning to off.
     *
     * @param name like "arial"
     * @param type like Font.BOLD
     * @param size like 12
     */
    @Override
    public void setModifierFont(String name, int type, int size) {
        RendererSettings.getInstance().setLabelFont(name, type, size);
        singlePointRenderer.RefreshModifierFont();
    }

    /**
     * Set the label font to be used in the renderer
     *
     * @param name like "arial"
     * @param type like Font.BOLD
     * @param size like 12
     * @param tracking like TextAttribute.TRACKING_LOOSE (0.04f)
     * @param kerning default false.
     */
    @Override
    public void setModifierFont(String name, int type, int size, float tracking, Boolean kerning) {
        RendererSettings.getInstance().setLabelFont(name, type, size, kerning, tracking);
        singlePointRenderer.RefreshModifierFont();
    }

    /**
     * Populates the Symbol with the shapes necessary to render.
     *
     * @param symbol
     * @param converter does point conversion between pixels & lat/lon
     * coordinates.
     * @param clipBounds dimensions of drawing surface. needed to do clipping.
     */
    private void ProcessSymbolGeometry(MilStdSymbol symbol, IPointConversion converter, Rectangle2D clipBounds) throws RendererException {
        ArrayList<MilStdSymbol> symbols = new ArrayList<>();
        symbols.add(symbol);
        ProcessSymbolGeometryBulk(symbols, converter, clipBounds);

    }

    /**
     * Processes multiple MilStdSymbols. Sets the Modifier Shapes and Symbol
     * Shapes ArrayLists on the MilStdSymbol.
     *
     * @param symbols ArrayList of type MilStdSymbol - symbols to get their
     * shapes
     * @param converter does point conversion between pixels & lat/lon
     * coordinates.
     * @param clipBounds dimensions of drawing surface. needed to do clipping.
     * No clipping will be done if value is NULL
     */
    private void ProcessSymbolGeometryBulk(ArrayList<MilStdSymbol> symbols, IPointConversion converter, Rectangle2D clipBounds) throws RendererException {
        try {

            String basicSymbolID = "";
            int count = symbols.size();
            String message = null;
            String symbolID = null;

            SymbolDef symbolDef = null;
            if (_SymbolDefTable == null) {
                _SymbolDefTable = SymbolDefTable.getInstance();
            }

            MilStdSymbol symbol;
            for (int lcv = 0; lcv < count; lcv++) {
                symbol = symbols.get(lcv);
                int pointCount = 0;
                if (symbol.getCoordinates() != null) {
                    pointCount = symbol.getCoordinates().size();
                }

                symbolID = symbol.getSymbolID();
                if (SymbolUtilities.isTacticalGraphic(symbolID) == true || pointCount > 1) {
                    basicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
                    symbolDef = _SymbolDefTable.getSymbolDef(basicSymbolID, symbol.getSymbologyStandard());

                    if (symbolDef == null) {//if bad symbol code, replace with action point or boundary
                        if (symbol.getCoordinates().size() <= 1) {
                            if (symbol.getModifier(ModifiersTG.H_ADDITIONAL_INFO_1) != null) {
                                symbol.setModifier(ModifiersTG.H1_ADDITIONAL_INFO_2, symbol.getModifier(ModifiersTG.H_ADDITIONAL_INFO_1));
                            }
                            symbol.setModifier(ModifiersTG.H_ADDITIONAL_INFO_1, symbolID.substring(0, 10));

                            symbol.setSymbolID("G" + SymbolUtilities.getAffiliation(symbolID)
                                    + "G" + SymbolUtilities.getStatus(symbolID) + "GPP---****X");
                            symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
                            symbol.setFillColor(SymbolUtilities.getFillColorOfAffiliation(symbolID));
                        } else {
                            symbol.setSymbolID("G" + SymbolUtilities.getAffiliation(symbolID)
                                    + "G" + SymbolUtilities.getStatus(symbolID) + "GLB---****X");
                            symbol.setLineColor(SymbolUtilities.getLineColorOfAffiliation(symbolID));
                            symbol.setFillColor(null);
                        }
                        basicSymbolID = SymbolUtilities.getBasicSymbolID(symbol.getSymbolID());
                        symbolDef = _SymbolDefTable.getSymbolDef(basicSymbolID, symbol.getSymbologyStandard());
                    }

                    if (symbolDef != null) {
                        if (symbolDef.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POINT) {
                            singlePointRenderer.ProcessSPSymbol(symbol, converter);
                        } else {
                            //send to multipointRendering
//                            _MPR.render(symbol, converter, clipBounds);
                            //ProcessTGSymbol(symbol, converter,clipBounds);
                        }
                    } else {
                        message = "Cannot draw: " + symbolID + " (" + basicSymbolID + "), lookup failed.";
                        throw new RendererException(message);
                    }
                } else// if(SymbolUtilities.isWarfighting(symbol.getSymbolID()))
                {
                    //Pass to Unit rendering
                    singlePointRenderer.ProcessUnitSymbol(symbol, converter);
                }
            }
        } catch (RendererException exc) {
            throw new RendererException(exc.getMessage(), exc);
        }
    }

}
