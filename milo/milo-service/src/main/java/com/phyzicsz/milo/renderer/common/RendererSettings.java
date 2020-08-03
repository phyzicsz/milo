/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static class that holds the setting for the JavaRenderer. Allows different
 * parts of the renderer to know what values are being used.
 *
 * @author michael.spinelli
 */
public class RendererSettings {

    private static final Logger logger = LoggerFactory.getLogger(RendererSettings.class);

    private static RendererSettings INSTANCE = null;

    //public CONSTANTS
    //--------------------------------------------------------------------------
    /**
     * There will be no background for text
     */
    public static final int TEXT_BACKGROUND_METHOD_NONE = 0;

    /**
     * There will be a colored box behind the text
     */
    public static final int TEXT_BACKGROUND_METHOD_COLOR_FILL = 1;

    /**
     * There will be an adjustable outline around the text (expensive) Outline
     * width of 4 is recommended.
     */
    public static final int TEXT_BACKGROUND_METHOD_OUTLINE = 2;

    /**
     * A different approach for outline which is quicker and seems to use less
     * memory. Also, you may do well with a lower outline thickness setting
     * compared to the regular outlining approach. Outline Width of 2 is
     * recommended. Only works with RenderMethod_NATIVE.
     */
    public static final int TEXT_BACKGROUND_METHOD_OUTLINE_QUICK = 3;

    /**
     * Everything that comes back from the Renderer is a Java Shape. Simpler,
     * but can be slower when rendering modifiers or a large number of single
     * point symbols. Not recommended
     */
    public static final int RENDER_METHOD_SHAPES = 0;
    /**
     * Adds a level of complexity to the rendering but is much faster for
     * certain objects. Modifiers and single point graphics will render faster.
     * MultiPoints will still be shapes. Recommended
     */
    public static final int RENDER_METHOD_NATIVE = 1;

    /**
     * 2525Bch2 and USAS 11-12 symbology
     */
    public static final int SYMBOLOGY_2525B = 0;

    /**
     * 2525C, which includes 2525Bch2 & USAS 13/14
     */
    public static final int SYMBOLOGY_2525C = 1;
    /**
     * 2525D, not support yet so defaults to 2525C is selected
     */
    public static final int SYMBOLOGY_2525D = 1;

    public static int OPERATIONAL_CONDITION_MODIFIER_TYPE_BAR = 1;

    //private fields
    //--------------------------------------------------------------------------
    //outline approach.  none, filled rectangle, outline (default),
    //outline quick (outline will not exceed 1 pixels).
    private static int textBackgroundMethod = 3;

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     */
    private static int textBackgroundAutoColorThreshold = 160;

    //if TextBackgroundMethod_OUTLINE is set, This value determnies the width of that outline.
    private static int textOutlineWidth = 2;

    //label foreground color, uses line color of symbol if null.
    private static Color colorLabelForeground = null; //Color.BLACK;
    //label background color, used if TextBackGroundMethod = TextBackgroundMethod_COLORFILL && not null
    private static Color colorLabelBackground = Color.WHITE;

    private static int symbolRenderMethod = 1;
    private static int unitRenderMethod = 1;
    private static int textRenderMethod = 1;
    /**
     * Collapse labels for fire support areas when the symbol isn't large enough
     * to show all the labels.
     */
    private static boolean autoCollpaseModifiers = true;

    private static int symbolOutlineWidth = 1;

    /**
     * If true (default), when HQ Staff is present, location will be indicated
     * by the free end of the staff
     */
    private static Boolean centerOnHQStaff = true;

    private static int symbologyStandard = 0;

    private static int ocmType = 1;

    private static boolean useLineInterpolation = true;

    private static String modifierFontName = "arial";
    private static int modifierFontType = Font.BOLD;
    private static int modifierFontSize = 12;
    private static int modifierFontKerning = 0;//0=off, 1=on (TextAttribute.KERNING_ON)
    private static float modifierFontTracking = 0;
    private boolean scaleEchelon = false;
    private boolean drawAffiliationModifierAsLabel = true;

    private static int DPI = 90;

    private boolean twoLabelOnly = true;

    private Color friendlyUnitFillColor = AffiliationColors.FriendlyUnitFillColor;
    private Color hostileUnitFillColor = AffiliationColors.HostileUnitFillColor;
    private Color neutralUnitFillColor = AffiliationColors.NeutralUnitFillColor;
    private Color unknownUnitFillColor = AffiliationColors.UnknownUnitFillColor;
    private Color friendlyGraphicFillColor = AffiliationColors.FriendlyGraphicFillColor;
    private Color hostileGraphicFillColor = AffiliationColors.HostileGraphicFillColor;
    private Color neutralGraphicFillColor = AffiliationColors.NeutralGraphicFillColor;
    private Color unknownGraphicFillColor = AffiliationColors.UnknownGraphicFillColor;
    private Color friendlyUnitLineColor = AffiliationColors.FriendlyUnitLineColor;
    private Color hostileUnitLineColor = AffiliationColors.HostileUnitLineColor;
    private Color neutralUnitLineColor = AffiliationColors.NeutralUnitLineColor;
    private Color unknownUnitLineColor = AffiliationColors.UnknownUnitLineColor;
    private Color friendlyGraphicLineColor = AffiliationColors.FriendlyGraphicLineColor;
    private Color hostileGraphicLineColor = AffiliationColors.HostileGraphicLineColor;
    private Color neutralGraphicLineColor = AffiliationColors.NeutralGraphicLineColor;
    private Color unknownGraphicLineColor = AffiliationColors.UnknownGraphicLineColor;

    private RendererSettings() {
        Init();

    }

    public static synchronized RendererSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RendererSettings();
        }

        return INSTANCE;
    }

    private void Init() {

    }

    /**
     * None, outline (default), or filled background.If set to OUTLINE,
     * TextOutlineWidth changed to default of 4.If set to OUTLINE_QUICK,
     * TextOutlineWidth changed to default of 2. Use setTextOutlineWidth if
     * you'd like a different value.
     *
     * @param textBackgroundMethod
     */
    synchronized public void setTextBackgroundMethod(int textBackgroundMethod) {
        RendererSettings.textBackgroundMethod = textBackgroundMethod;
        if (textBackgroundMethod == TEXT_BACKGROUND_METHOD_OUTLINE) {
            textOutlineWidth = 4;
        } else if (textBackgroundMethod == TEXT_BACKGROUND_METHOD_OUTLINE_QUICK) {
            textOutlineWidth = 2;
        }
    }

    /**
     * None, outline (default), or filled background.
     *
     * @return method like RenderSettings.TextBackgroundMethod_NONE
     */
    synchronized public int getTextBackgroundMethod() {
        return textBackgroundMethod;
    }

    /**
     * determines what kind of java objects will be generated when processing a
     * symbol.RenderMethod_SHAPES is simpler as everything is treated the
     * same.RenderMethod_NATIVE is faster but, in addition to shapes, uses
     * GlyphVectors and TextLayouts.
     *
     * @param symbolRenderMethod
     */
    public void setSymbolRenderMethod(int symbolRenderMethod) {
        RendererSettings.symbolRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     *
     * @return method like RendererSetting.RenderMethod_NATIVE
     */
    public int getSymbolRenderMethod() {
        return symbolRenderMethod;
    }

    /**
     * Controls what symbols are supported.Set this before loading the renderer.
     *
     * @param standard
     */
    public void setSymbologyStandard(int standard) {
        symbologyStandard = standard;
    }

    /**
     * Current symbology standard
     *
     * @return symbologyStandard Like
     * RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public int getSymbologyStandard() {
        return symbologyStandard;
    }

    /**
     * Set the operational condition modifier to be slashes or bars
     *
     * @param value like RendererSettings.OperationalConditionModifierType_SLASH
     */
    public void setOperationalConditionModifierType(int value) {
        ocmType = value;
    }

    public int getOperationalConditionModifierType() {
        return ocmType;
    }

    /**
     * For lines symbols with "decorations" like FLOT or LOC, when points are
     * too close together, we will start dropping points until we get enough
     * space between 2 points to draw the decoration. Without this, when points
     * are too close together, you run the chance that the decorated line will
     * look like a plain line because there was no room between points to draw
     * the decoration.
     *
     * @param value
     */
    public void setUseLineInterpolation(boolean value) {
        useLineInterpolation = value;
    }

    /**
     * Returns the current setting for Line Interpolation.
     *
     * @return
     */
    public boolean getUseLineInterpolation() {
        return useLineInterpolation;
    }

    /**
     * set the screen DPI so the renderer can take DPI into account when
     * rendering for things like dashed lines and decorated lines.
     *
     * @param value
     */
    public void setDeviceDPI(int value) {
        DPI = value;
    }

    public int getDeviceDPI() {
        return DPI;
    }

    /**
     * Collapse Modifiers for fire support areas when the symbol isn't large
     * enough to show all the labels. Identifying label will always be visible.
     * Zooming in, to make the symbol larger, will make more modifiers visible.
     * Resizing the symbol can also make more modifiers visible.
     *
     * @param value
     */
    public void setAutoCollapseModifiers(boolean value) {
        autoCollpaseModifiers = value;
    }

    public boolean getAutoCollapseModifiers() {
        return autoCollpaseModifiers;
    }

    /**
     * determines what kind of java objects will be generated when processing a
     * symbol. RenderMethod_SHAPES is simpler as everything is treated the same.
     * RenderMethod_NATIVE is faster but, in addition to shapes, uses
     * GlyphVectors and TextLayouts.
     *
     * @param method like RendererSetting.RenderMethod_SHAPES
     */
    /**
     * determines what kind of java objects will be generated when processing a
     * symbol.RenderMethod_SHAPES is simpler as everything is treated the
     * same.RenderMethod_NATIVE is faster but, in addition to shapes, uses
     * GlyphVectors and TextLayouts.
     *
     * @param symbolRenderMethod
     */
    public void setUnitRenderMethod(int symbolRenderMethod) {
        unitRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     *
     * @return method like RendererSetting.RenderMethod_NATIVE
     */
    public int getUnitRenderMethod() {
        return unitRenderMethod;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated
     * by the free end of the staff
     *
     * @param value
     */
    public void setCenterOnHQStaff(Boolean value) {
        centerOnHQStaff = value;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated
     * by the free end of the staff
     *
     * @return
     */
    public Boolean getCenterOnHQStaff() {
        return centerOnHQStaff;
    }

    /**
     * determines what kind of java objects will be generated when processing a
     * symbol.RenderMethod_SHAPES is simpler as everything is treated the
     * same.RenderMethod_NATIVE is faster but, in addition to shapes, uses
     * GlyphVectors and TextLayouts. In the case of text, NATIVE tends to render
     * sharper and clearer text.
     *
     * @param symbolRenderMethod
     */
    public void setTextRenderMethod(int symbolRenderMethod) {
        textRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     *
     * @return
     */
    public int getTextRenderMethod() {
        return textRenderMethod;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used, the outline will
     * be this many pixels wide.
     *
     * @param width
     */
    synchronized public void setTextOutlineWidth(int width) {
        textOutlineWidth = width;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used, the outline will
     * be this many pixels wide.
     *
     * @return
     */
    synchronized public int getTextOutlineWidth() {
        return textOutlineWidth;
    }

    /**
     * Refers to text color of modifier labels
     *
     * @return
     *
     */
    public Color getLabelForegroundColor() {
        return colorLabelForeground;
    }

    /**
     * Refers to text color of modifier labels Default Color is Black. If NULL,
     * uses line color of symbol
     *
     * @param value
     *
     */
    synchronized public void setLabelForegroundColor(Color value) {
        colorLabelForeground = value;
    }

    /**
     * Refers to background color of modifier labels
     *
     * @return
     *
     */
    public Color getLabelBackgroundColor() {
        return colorLabelBackground;
    }

    /**
     * Refers to text color of modifier labels Default Color is White. Null
     * value means the optimal background color (black or white) will be chose
     * based on the color of the text.
     *
     * @param value
     *
     */
    synchronized public void setLabelBackgroundColor(Color value) {
        colorLabelBackground = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     *
     * @param value
     */
    public void setTextBackgroundAutoColorThreshold(int value) {
        textBackgroundAutoColorThreshold = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     *
     * @return
     */
    public int getTextBackgroundAutoColorThreshold() {
        return textBackgroundAutoColorThreshold;
    }

    /**
     * This applies to Single Point Tactical Graphics. Setting this will
     * determine the default value for milStdSymbols when created. 0 for no
     * outline, 1 for outline thickness of 1 pixel, 2 for outline thickness of 2
     * pixels, greater than 2 is not currently recommended.
     *
     * @param width
     */
    synchronized public void setSinglePointSymbolOutlineWidth(int width) {
        symbolOutlineWidth = width;
    }

    /**
     * This applies to Single Point Tactical Graphics.
     *
     * @return
     */
    synchronized public int getSinglePointSymbolOutlineWidth() {
        return symbolOutlineWidth;
    }

    /**
     * false to use label font size true to scale it using symbolPixelBounds /
     * 3.5
     *
     * @param value
     */
    public void setScaleEchelon(boolean value) {
        scaleEchelon = value;
    }

    /**
     * Returns the value determining if we scale the echelon font size or just
     * match the font size specified by the label font.
     *
     * @return true or false
     */
    public boolean getScaleEchelon() {
        return scaleEchelon;
    }

    /**
     * Determines how to draw the Affiliation modifier.True to draw as modifier
     * label in the "E/F" location. False to draw at the top right corner of the
     * symbol
     *
     * @param value
     */
    public void setDrawAffiliationModifierAsLabel(boolean value) {
        drawAffiliationModifierAsLabel = value;
    }

    /**
     * True to draw as modifier label in the "E/F" location.False to draw at the
     * top right corner of the symbol
     *
     * @return
     */
    public boolean getDrawAffiliationModifierAsLabel() {
        return drawAffiliationModifierAsLabel;
    }

    /**
     * Sets the font to be used for modifier labels
     *
     * @param name Like "arial"
     * @param type Like Font.TRUETYPE_FONT
     * @param size Like 12
     */
    public void setLabelFont(String name, int type, int size) {
        modifierFontName = name;
        modifierFontType = type;
        modifierFontSize = size;
        modifierFontKerning = 0;
        modifierFontTracking = TextAttribute.TRACKING_LOOSE;
    }

    /**
     *
     * @param name Like "arial"
     * @param type Like Font.BOLD
     * @param size Like 12
     * @param kerning - default false. The default advances of single characters
     * are not appropriate for some character sequences, for example "To" or
     * "AWAY". Without kerning the adjacent characters appear to be separated by
     * too much space. Kerning causes selected sequences of characters to be
     * spaced differently for a more pleasing visual appearance.
     * @param tracking
     */
    public void setLabelFont(String name, int type, int size, Boolean kerning, float tracking) {
        modifierFontName = name;
        modifierFontType = type;
        modifierFontSize = size;
        if (kerning == false) {
            modifierFontKerning = 0;
        } else {
            modifierFontKerning = TextAttribute.KERNING_ON;
        }
        modifierFontTracking = tracking;
    }

    /**
     * the font name to be used for modifier labels
     *
     * @return name of the label font
     */
    public String getLabelFontName() {
        return modifierFontName;
    }

    /**
     * Like Font.BOLD
     *
     * @return type of the label font
     */
    public int getLabelFontType() {
        return modifierFontType;
    }

    /**
     * get font point size
     *
     * @return size of the label font
     */
    public int getLabelFontSize() {
        return modifierFontSize;
    }

    /**
     *
     * @return 0=off, 1=on.
     */
    public int getLabelFontKerning() {
        return modifierFontKerning;
    }

    /**
     *
     * @return
     */
    public float getLabelFontTracking() {
        return modifierFontTracking;
    }

    /**
     * get font object used for labels
     *
     * @return Font object
     */
    public Font getLabelFont() {
        try {
            Map<TextAttribute, Object> map = new HashMap<>();
            map.put(TextAttribute.KERNING, modifierFontKerning);
            map.put(TextAttribute.TRACKING, modifierFontTracking);

            Font temp = new Font(modifierFontName, modifierFontType, modifierFontSize);

            return temp.deriveFont(map);
        } catch (Exception ex) {
            String message = "font creation error, returning \"" + modifierFontName + "\" font, " + modifierFontSize + "pt. Check font name and type.";
            logger.error(message, ex);
            return new Font("arial", Font.BOLD, 12);
        }
    }

    /**
     ** Get a boolean indicating between the use of ENY labels in all segments
     * (false) or to only set 2 labels one at the north and the other one at the
     * south of the graphic (true).
     *
     * @return {boolean}
     */
    public boolean getTwoLabelOnly() {
        return twoLabelOnly;
    }

    /**
     * Set a boolean indicating between the use of ENY labels in all segments
     * (false) or to only set 2 labels one at the north and the other one at the
     * south of the graphic (true).
     *
     * @param TwoLabelOnly
     */
    public void setTwoLabelOnly(boolean TwoLabelOnly) {
        twoLabelOnly = TwoLabelOnly;
    }

    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getFriendlyUnitFillColor() {
        return friendlyUnitFillColor;
    }

    /**
     * Set the preferred fill affiliation color for units
     *
     * @param friendlyUnitFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setFriendlyUnitFillColor(Color friendlyUnitFillColor) {
        if (friendlyUnitFillColor != null) {
            this.friendlyUnitFillColor = friendlyUnitFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getHostileUnitFillColor() {
        return hostileUnitFillColor;
    }

    /**
     * Set the preferred fill affiliation color for units
     *
     * @param hostileUnitFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setHostileUnitFillColor(Color hostileUnitFillColor) {
        if (hostileUnitFillColor != null) {
            this.hostileUnitFillColor = hostileUnitFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getNeutralUnitFillColor() {
        return neutralUnitFillColor;
    }

    /**
     * Set the preferred line affiliation color for units
     *
     * @param neutralUnitFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setNeutralUnitFillColor(Color neutralUnitFillColor) {
        if (neutralUnitFillColor != null) {
            this.neutralUnitFillColor = neutralUnitFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getUnknownUnitFillColor() {
        return unknownUnitFillColor;
    }

    /**
     * Set the preferred fill affiliation color for units
     *
     * @param unknownUnitFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setUnknownUnitFillColor(Color unknownUnitFillColor) {
        if (unknownUnitFillColor != null) {
            this.unknownUnitFillColor = unknownUnitFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getHostileGraphicFillColor() {
        return hostileGraphicFillColor;
    }

    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param hostileGraphicFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setHostileGraphicFillColor(Color hostileGraphicFillColor) {
        if (hostileGraphicFillColor != null) {
            this.hostileGraphicFillColor = hostileGraphicFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getFriendlyGraphicFillColor() {
        return friendlyGraphicFillColor;
    }

    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param friendlyGraphicFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setFriendlyGraphicFillColor(Color friendlyGraphicFillColor) {
        if (friendlyGraphicFillColor != null) {
            this.friendlyGraphicFillColor = friendlyGraphicFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getNeutralGraphicFillColor() {
        return neutralGraphicFillColor;
    }

    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param neutralGraphicFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setNeutralGraphicFillColor(Color neutralGraphicFillColor) {
        if (neutralGraphicFillColor != null) {
            this.neutralGraphicFillColor = neutralGraphicFillColor;
        }
    }

    /**
     * get the preferred fill affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getUnknownGraphicFillColor() {
        return unknownGraphicFillColor;
    }

    /**
     * Set the preferred fill affiliation color for graphics
     *
     * @param unknownGraphicFillColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setUnknownGraphicFillColor(Color unknownGraphicFillColor) {
        if (unknownGraphicFillColor != null) {
            this.unknownGraphicFillColor = unknownGraphicFillColor;
        }
    }

    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getFriendlyUnitLineColor() {
        return friendlyUnitLineColor;
    }

    /**
     * Set the preferred line affiliation color for units
     *
     * @param friendlyUnitLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setFriendlyUnitLineColor(Color friendlyUnitLineColor) {
        if (friendlyUnitLineColor != null) {
            this.friendlyUnitLineColor = friendlyUnitLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getHostileUnitLineColor() {
        return hostileUnitLineColor;
    }

    /**
     * Set the preferred line affiliation color for units
     *
     * @param hostileUnitLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setHostileUnitLineColor(Color hostileUnitLineColor) {
        if (hostileUnitLineColor != null) {
            this.hostileUnitLineColor = hostileUnitLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getNeutralUnitLineColor() {
        return neutralUnitLineColor;
    }

    /**
     * Set the preferred line affiliation color for units
     *
     * @param neutralUnitLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setNeutralUnitLineColor(Color neutralUnitLineColor) {
        if (neutralUnitLineColor != null) {
            this.neutralUnitLineColor = neutralUnitLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for units.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getUnknownUnitLineColor() {
        return unknownUnitLineColor;
    }

    /**
     * Set the preferred line affiliation color for units
     *
     * @param unknownUnitLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setUnknownUnitLineColor(Color unknownUnitLineColor) {
        if (unknownUnitLineColor != null) {
            this.unknownUnitLineColor = unknownUnitLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getFriendlyGraphicLineColor() {
        return friendlyGraphicLineColor;
    }

    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param friendlyGraphicLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setFriendlyGraphicLineColor(Color friendlyGraphicLineColor) {
        if (friendlyGraphicLineColor != null) {
            this.friendlyGraphicLineColor = friendlyGraphicLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getHostileGraphicLineColor() {
        return hostileGraphicLineColor;
    }

    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param hostileGraphicLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setHostileGraphicLineColor(Color hostileGraphicLineColor) {
        if (hostileGraphicLineColor != null) {
            this.hostileGraphicLineColor = hostileGraphicLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getNeutralGraphicLineColor() {
        return neutralGraphicLineColor;
    }

    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param neutralGraphicLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setNeutralGraphicLineColor(Color neutralGraphicLineColor) {
        if (neutralGraphicLineColor != null) {
            this.neutralGraphicLineColor = neutralGraphicLineColor;
        }
    }

    /**
     * get the preferred line affiliation color for graphics.
     *
     * @return Color like Color(255, 255, 255)
     *
     *
     */
    public Color getUnknownGraphicLineColor() {
        return unknownGraphicLineColor;
    }

    /**
     * Set the preferred line affiliation color for graphics
     *
     * @param unknownGraphicLineColor Color like Color(255, 255, 255)
     *
     *
     */
    public void setUnknownGraphicLineColor(Color unknownGraphicLineColor) {
        if (unknownGraphicLineColor != null) {
            this.unknownGraphicLineColor = unknownGraphicLineColor;
        }
    }

    /**
     * Set the preferred line and fill affiliation color for tactical graphics.
     *
     * @param friendlyGraphicLineColor Color
     * @param hostileGraphicLineColor Color
     * @param neutralGraphicLineColor Color
     * @param unknownGraphicLineColor Color
     * @param friendlyGraphicFillColor Color
     * @param hostileGraphicFillColor Color
     * @param neutralGraphicFillColor Color
     * @param unknownGraphicFillColor Color
     */
    public void setGraphicPreferredAffiliationColors(Color friendlyGraphicLineColor,
            Color hostileGraphicLineColor,
            Color neutralGraphicLineColor,
            Color unknownGraphicLineColor,
            Color friendlyGraphicFillColor,
            Color hostileGraphicFillColor,
            Color neutralGraphicFillColor,
            Color unknownGraphicFillColor) {

        setFriendlyGraphicLineColor(friendlyGraphicLineColor);
        setHostileGraphicLineColor(hostileGraphicLineColor);
        setNeutralGraphicLineColor(neutralGraphicLineColor);
        setUnknownGraphicLineColor(unknownGraphicLineColor);
        setFriendlyGraphicFillColor(friendlyGraphicFillColor);
        setHostileGraphicFillColor(hostileGraphicFillColor);
        setNeutralGraphicFillColor(neutralGraphicFillColor);
        setUnknownGraphicFillColor(unknownGraphicFillColor);
    }

    /**
     * Set the preferred line and fill affiliation color for units and tactical
     * graphics.
     *
     * @param friendlyUnitLineColor Color like Color(255, 255, 255). Set to null
     * to ignore setting
     * @param hostileUnitLineColor Color
     * @param neutralUnitLineColor Color
     * @param unknownUnitLineColor Color
     * @param friendlyUnitFillColor Color
     * @param hostileUnitFillColor Color
     * @param neutralUnitFillColor Color
     * @param unknownUnitFillColor Color
     */
    public void setUnitPreferredAffiliationColors(Color friendlyUnitLineColor,
            Color hostileUnitLineColor,
            Color neutralUnitLineColor,
            Color unknownUnitLineColor,
            Color friendlyUnitFillColor,
            Color hostileUnitFillColor,
            Color neutralUnitFillColor,
            Color unknownUnitFillColor) {

        setFriendlyUnitLineColor(friendlyUnitLineColor);
        setHostileUnitLineColor(hostileUnitLineColor);
        setNeutralUnitLineColor(neutralUnitLineColor);
        setUnknownUnitLineColor(unknownUnitLineColor);
        setFriendlyUnitFillColor(friendlyUnitFillColor);
        setHostileUnitFillColor(hostileUnitFillColor);
        setNeutralUnitFillColor(neutralUnitFillColor);
        setUnknownUnitFillColor(unknownUnitFillColor);
    }

}
