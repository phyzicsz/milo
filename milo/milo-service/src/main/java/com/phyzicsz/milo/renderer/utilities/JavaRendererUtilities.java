package com.phyzicsz.milo.renderer.utilities;

import com.phyzicsz.milo.renderer.JavaRenderer;
import com.phyzicsz.milo.renderer.TacticalGraphicIconRenderer;
import com.phyzicsz.milo.renderer.common.MilStdAttributes;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.common.SymbolDef;
import com.phyzicsz.milo.renderer.common.SymbolDefTable;
import com.phyzicsz.milo.renderer.common.SymbolUtilities;
import com.phyzicsz.milo.renderer.line.countsupport;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author stephen.pinizzotto
 */
@SuppressWarnings("unused")
public class JavaRendererUtilities {

    private static final Logger logger = LoggerFactory.getLogger(JavaRendererUtilities.class);

    public static String HOSTILE_FILL_COLOR = "FFFF8080";
    public static String FRIENDLY_FILL_COLOR = "FF80E0FF";
    public static String NEUTRAL_FILL_COLOR = "FFAAFFAA";
    public static String UNKNOWN_FILL_COLOR = "FFFFFF80";

    /**
     * Returns the default MIL-STD-2525C fill color for a symbol code in ARGB
     * format. The string is formated AARRGGBB in hex values.
     *
     * @param symbolCode A 15 character MIL-STD-2525C symbol ID
     * @return the default "light" color of the symbol as specified by the
     * MIL-STD-2525C. Returns null if it does not recognize the affiliation.
     */
//    public static String getAffiliationFillColor(String symbolCode)
//    {
//        String color; 
//        
//        //if no color has been assigned use black, although, we should
//        // probably check affiliation here.
//        String affiliation = SymbolUtilities.getAffiliation(symbolCode);
//        if (affiliation.equals("F") || affiliation.equals("M") || 
//                affiliation.equals("D") || affiliation.equals("M"))                        
//        {
//            color = JavaRendererUtilities.FRIENDLY_FILL_COLOR;
//        }
//        else if (affiliation.equals("H") || affiliation.equals("S") || 
//                affiliation.equals("J") || affiliation.equals("K"))
//        {
//            color = JavaRendererUtilities.HOSTILE_FILL_COLOR;
//        }
//        else if (affiliation.equals("N") || affiliation.equals("L"))
//        {   
//            color = JavaRendererUtilities.NEUTRAL_FILL_COLOR;
//        }
//        else if (affiliation.equals("U") || affiliation.equals("P") || 
//                affiliation.equals("G") || affiliation.equals("W"))
//        {
//            color = JavaRendererUtilities.UNKNOWN_FILL_COLOR;
//        }
//        else
//        {   
//            color = null;
//        }                    
//        
//        return color;
//    }
    /**
     * Converts ARGB string format to the Google used ABGR string format. Google
     * reverses the blue and red positioning.
     *
     * @param rgbString A color string of the format AARRGGBB in hex value.
     * @return the reverse of the input string in hex. The format should now be
     * AABBGGRR
     */
    public static String ARGBtoABGR(String rgbString) {
        if (rgbString.length() == 6) {
            String s = "FF";
            rgbString = s.concat(rgbString);
        }
        char[] c = rgbString.toCharArray();

        char temp1 = c[2];
        char temp2 = c[3];
        c[2] = c[6];
        c[3] = c[7];
        c[6] = temp1;
        c[7] = temp2;

        String bgrString = new String(c);

        return bgrString;
    }

    /**
     * Returns a symbolId with just the identifiable symbol Id pieces. All
     * variable information is returned as '*'. For example, a boundary,
     * "GFGPGLB----KUSX" returns "G*G*GLB---****X";
     *
     * @param symbolCode A 15 character symbol ID.
     * @return The normalized SymbolCode.
     */
    public static String normalizeSymbolCode(String symbolCode) {

        String newSymbolCode = symbolCode;

        if (symbolCode.startsWith("G") || symbolCode.startsWith("S")) {
            // Remove Affiliation
            newSymbolCode = newSymbolCode.substring(0, 1) + '*' + newSymbolCode.substring(2);
            // Remove planned/present field
            newSymbolCode = newSymbolCode.substring(0, 3) + '*' + newSymbolCode.substring(4);
            // Remove echelon, special code and country codes
            newSymbolCode = newSymbolCode.substring(0, 10) + "****" + newSymbolCode.substring(14);
        }

        // If a unit replace last character with *.
        if (symbolCode.startsWith("S")) {
            newSymbolCode = newSymbolCode.substring(0, 14) + '*';
        }

        return newSymbolCode;
    }

    /**
     * Takes a string and parses information to build a MilStdSymbol
     *
     * @param SymbolInfo something like
     * "SymbolID?LineColor=0x000000&FillColor=0xFFFFFF&size=35"
     * @return
     * @author Spinelli
     */
    public static MilStdSymbol createMilstdSymbol(String SymbolInfo) {
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
    public static MilStdSymbol createMilstdSymbol(String symbolID, Map<String, String> params) {
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
                    if (TacticalGraphicIconRenderer.getInstance().CanRender(symbolID));
                    {
                        isMultiPoint = true;
                    }
                }
            }
            if (isMultiPoint == false) {
                if (JavaRenderer.getInstance().canRender(symbolID, null, symbol.getSymbologyStandard()) == false) {
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
     * Determines if a String represents a valid number
     *
     * @param text
     * @return "1.56" == true, "1ab" == false
     */
    public static boolean isNumber(String text) {
        if (text != null && text.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
            return true;
        } else {
            return false;
        }
    }

    public static Point2D getEndPointWithAngle(Point2D ptStart,
            //Point2D pt1,
            //Point2D pt2,
            double angle,
            double distance) {
        double newX = 0;
        double newY = 0;
        Point2D pt = new Point2D.Double();
        try {
            //first get the angle psi between pt0 and pt1
            double psi = 0;//Math.atan((pt1.y - pt0.y) / (pt1.x - pt0.x));
            //double psi = Math.atan((ptStart.getY() - ptStart.getY()) / (ptStart.getX() - (ptStart.getX()+100)));
            //convert alpha to radians
            double alpha1 = Math.PI * angle / 180;

            //theta is the angle of extension from the x axis
            double theta = psi + alpha1;
            //dx is the x extension from pt2
            double dx = distance * Math.cos(theta);
            //dy is the y extension form pt2
            double dy = distance * Math.sin(theta);
            newX = ptStart.getX() + dx;
            newY = ptStart.getY() + dy;

            pt.setLocation(newX, newY);
        } catch (Exception ex) {
            logger.error("error", ex);
        }
        return pt;
    }

    /**
     *
     * @param latitude1
     * @param longitude1
     * @param latitude2
     * @param longitude2
     * @param unitOfMeasure meters, kilometers, miles, feet, yards, nautical,
     * nautical miles.
     * @return
     */
    public static double measureDistance(double latitude1, double longitude1, double latitude2, double longitude2, String unitOfMeasure) {
        // latitude1,latitude2 = latitude, longitude1,longitude2 = longitude
        //Radius is 6378.1 (km), 3963.1 (mi), 3443.9 (nm

        double distance = -1,
                rad;
        //if((validateCoordinate(latitude1,longitude1) == true)&&(validateCoordinate(latitude2,longitude2) == true))
        //{

        String uom = unitOfMeasure.toLowerCase();

        if (uom.equals("meters")) {
            rad = 6378137;
        } else if (uom.equals("kilometers")) {
            rad = 6378.137;
        } else if (uom.equals("miles")) {
            rad = 3963.1;
        } else if (uom.equals("feet")) {
            rad = 20925524.9;
        } else if (uom.equals("yards")) {
            rad = 6975174.98;
        } else if (uom.equals("nautical")) {
            rad = 3443.9;
        } else if (uom.equals("nautical miles")) {
            rad = 3443.9;
        } else {
            return -1.0;
        }

        latitude1 = latitude1 * (Math.PI / 180);
        latitude2 = latitude2 * (Math.PI / 180);
        longitude1 = longitude1 * (Math.PI / 180);
        longitude2 = longitude2 * (Math.PI / 180);
        distance = (Math.acos(Math.cos(latitude1) * Math.cos(longitude1) * Math.cos(latitude2) * Math.cos(longitude2) + Math.cos(latitude1) * Math.sin(longitude1) * Math.cos(latitude2) * Math.sin(longitude2) + Math.sin(latitude1) * Math.sin(latitude2)) * rad);

        return distance;
    }

    public static String generateLookAtTag(ArrayList<Point2D.Double> geoCoords, ArrayList<Double> modsAM) {
        //add <LookAt> tag//////////////////////////////////////////////
        Boolean doLookAt = true;
        Rectangle2D controlPointBounds = null;//armyc2.c2sd.renderer.so.Rectangle();
        Point2D tempPt = null;
        StringBuilder LookAtTag = new StringBuilder("<LookAt>");
        if (doLookAt) {
            for (int j = 0; j < geoCoords.size(); j++) {
                tempPt = geoCoords.get(j);
                if (controlPointBounds != null) {
                    Rectangle2D.union(controlPointBounds, new Rectangle2D.Double(tempPt.getX(), tempPt.getY(), 0.00000000000001, 0.00000000000001), controlPointBounds);
                } else {
                    controlPointBounds = new Rectangle2D.Double(tempPt.getX(), tempPt.getY(), 0.00000000000001, 0.00000000000001);
                }
            }
            double distance = 0;
            //if 1 point circle with width
            if (geoCoords.size() == 1 && modsAM != null && modsAM.size() > 0) {
                distance = (modsAM.get(modsAM.size() - 1) * 2);
            } else {
                distance = measureDistance(controlPointBounds.getMinY(),
                        controlPointBounds.getMinX(),
                        controlPointBounds.getMaxY(),
                        controlPointBounds.getMaxX(),
                        "meters");
            }
            distance = distance * 1.1;

            double lon = controlPointBounds.getCenterX();
            double lat = controlPointBounds.getCenterY();
            LookAtTag.append("<longitude>" + lon + "</longitude>");
            LookAtTag.append("<latitude>" + lat + "</latitude>");
            //LookAtTag += "<altitude>" + number + "</altitude>";
            LookAtTag.append("<heading>" + 0 + "</heading>");
            LookAtTag.append("<tilt>" + 0 + "</tilt>");
            LookAtTag.append("<range>" + distance + "</range>");
            LookAtTag.append("<altitudeMode>" + "absolute" + "</altitudeMode>");
            LookAtTag.append("</LookAt>");

        }
        //add <LookAt> tag//////////////////////////////////////////////
        return LookAtTag.toString();
    }

    /**
     * we only have font lookups for F,H,N,U. But the shapes match one of these
     * four for the remaining affiliations. So we convert the string to a base
     * affiliation before we do the lookup.
     *
     * @param symbolID
     * @return
     */
    public static String sanitizeSymbolID(String symbolID) {
        String code = symbolID;
        char affiliation = symbolID.charAt(1);

        if (SymbolUtilities.isWeather(symbolID) == false) {
            if (affiliation == 'F'
                    ||//friendly
                    affiliation == 'H'
                    ||//hostile
                    affiliation == 'U'
                    ||//unknown
                    affiliation == 'N')//neutral
            {
                //code = code;
            } else if (affiliation == 'S')//suspect
            {
                code = code.charAt(0) + "H" + code.substring(2, 15);
            } else if (affiliation == 'L')//exercise neutral
            {
                code = code.charAt(0) + "N" + code.substring(2, 15);
            } else if (affiliation == 'A'
                    ||//assumed friend
                    affiliation == 'D'
                    ||//exercise friend
                    affiliation == 'M'
                    ||//exercise assumed friend
                    affiliation == 'K'
                    ||//faker
                    affiliation == 'J')//joker
            {
                code = code.charAt(0) + "F" + code.substring(2, 15);
            } else if (affiliation == 'P'
                    ||//pending
                    affiliation == 'G'
                    ||//exercise pending
                    affiliation == 'O'
                    ||//? brought it over from mitch's code
                    affiliation == 'W')//exercise unknown
            {
                code = code.charAt(0) + "U" + code.substring(2, 15);
            } else {
                code = code.charAt(0) + "U" + code.substring(2, 15);
            }

            code = code.substring(0, 10) + "-----";
        }

        return code;
    }

    ;
    
    
    public static Map<String, String> parseIconParameters(String symbolId, Map<String, String> params) {
        Map<String, String> iconInfo = new HashMap<String, String>();
        //if icon == true, make sure keepUnitRatio defaults to false.
        iconInfo.put(MilStdAttributes.KeepUnitRatio, "false");

        if (SymbolUtilities.isWarfighting(symbolId)) {
            Color fillColor = SymbolUtilities.getFillColorOfAffiliation(symbolId);
            iconInfo.put(MilStdAttributes.FillColor, SymbolUtilities.colorToHexString(fillColor, Boolean.TRUE));
        }
        if (params.containsKey(MilStdAttributes.FillColor)) {
            iconInfo.put(MilStdAttributes.FillColor, params.get(MilStdAttributes.FillColor));
        }
        if (symbolId.substring(0, 1).equals("G")) {
            Color fillColor = SymbolUtilities.getLineColorOfAffiliation(symbolId);
            iconInfo.put(MilStdAttributes.LineColor, SymbolUtilities.colorToHexString(fillColor, Boolean.TRUE));
        }
        if (params.containsKey(MilStdAttributes.LineColor)) {
            iconInfo.put(MilStdAttributes.LineColor, params.get(MilStdAttributes.LineColor));
        }
        if (params.containsKey(MilStdAttributes.SymbologyStandard)) {
            String symStd = params.get(MilStdAttributes.SymbologyStandard);
            if (symStd.length() != 1) {
                if (symStd.startsWith("2525") && symStd.length() == 5) {
                    char version = symStd.charAt(4);
                    switch (version) {
                        case 'B':
                        case 'b':
                            symStd = "0";
                            break;
                        case 'C':
                        case 'c':
                            symStd = "1";
                            break;
                        case 'D':
                        case 'd':
                            symStd = "2";
                            break;
                        default:
                            symStd = String.valueOf(RendererSettings.getInstance().getSymbologyStandard());
                            break;
                    }
                } else {
                    symStd = String.valueOf(RendererSettings.getInstance().getSymbologyStandard());
                }
            } else {
                char version = symStd.charAt(0);
                switch (version) {
                    case '0':
                    case '1':
                    case '2':
                        break;
                    default:
                        symStd = String.valueOf(RendererSettings.getInstance().getSymbologyStandard());
                        break;
                }
            }
            iconInfo.put(MilStdAttributes.SymbologyStandard, symStd);
        }
        if (params.containsKey(MilStdAttributes.KeepUnitRatio)) {
            iconInfo.put(MilStdAttributes.KeepUnitRatio, params.get(MilStdAttributes.KeepUnitRatio));
        }
        if (params.containsKey(MilStdAttributes.Alpha)) {
            iconInfo.put(MilStdAttributes.Alpha, params.get(MilStdAttributes.Alpha));
        }
        if (params.containsKey(MilStdAttributes.Renderer)) {
            iconInfo.put(MilStdAttributes.Renderer, params.get(MilStdAttributes.Renderer));
        }
        if (params.containsKey(MilStdAttributes.PixelSize)) {
            iconInfo.put(MilStdAttributes.PixelSize, params.get(MilStdAttributes.PixelSize));
        }
        if (params.containsKey(MilStdAttributes.OutlineColor)) {
            iconInfo.put(MilStdAttributes.OutlineColor, params.get(MilStdAttributes.OutlineColor));
        }
        iconInfo.put(MilStdAttributes.OutlineSymbol, "0");
        iconInfo.put(MilStdAttributes.DrawAsIcon, "true");
        return iconInfo;

    }

}
