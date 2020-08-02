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
import com.phyzicsz.milo.renderer.common.PointConversion;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.common.UnitFontLookup;
import com.phyzicsz.milo.renderer.common.SymbolDef;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
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

    private static JavaRenderer _instance = null;
    private static String _className = "";
    private static SinglePointRenderer _SPR = null;
//    private static IMultiPointRenderer _MPR = null;
    private static TacticalGraphicIconRenderer _TGIR = null;

    private SymbolDefTable _SymbolDefTable = null;

    PointConversion _PointConverter = null;

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
    private JavaRenderer() {

        try {

            _SPR = SinglePointRenderer.getInstance();
            _TGIR = TacticalGraphicIconRenderer.getInstance();

            _className = this.getClass().getName();

            if (_SPR == null) {
                logger.error("failed to initialize single point renderer");
            }
            if (_TGIR == null) {
                logger.error("failed to initialize tactical renderer");
            }

        } catch (Exception exc) {
            logger.error("failed to initialize");
        }
    }

    /**
     * Instance of the JavaRenderer
     *
     * @return the instance
     */
    public static synchronized JavaRenderer getInstance() {
        if (_instance == null) {
            _instance = new JavaRenderer();
        }

        return _instance;
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
                    ii = _TGIR.getIcon(symbolID, iconSize, null, symbol.getSymbologyStandard());
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
        return _SPR.getSinglePointTGSymbolSize();
    }

    @Override
    public int getUnitSymbolSize() {
        return _SPR.getUnitSymbolSize();
    }

    @Override
    public void setSinglePointTGSymbolSize(int size) {
        _SPR.setSinglePointTGSymbolSize(size);
    }

    @Override
    public void setUnitSymbolSize(int size) {
        _SPR.setUnitSymbolSize(size);
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
        _SPR.RefreshModifierFont();
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
        _SPR.RefreshModifierFont();
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
                            _SPR.ProcessSPSymbol(symbol, converter);
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
                    _SPR.ProcessUnitSymbol(symbol, converter);
                }
            }
        } catch (RendererException exc) {
            throw new RendererException(exc.getMessage(), exc);
        }
    }

}
