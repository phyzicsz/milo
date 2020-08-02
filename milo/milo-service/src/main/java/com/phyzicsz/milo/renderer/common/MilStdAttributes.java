/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer.common;

import java.util.ArrayList;

/**
 * Symbol attributes for use as url parameters
 * @author michael.spinelli
 */
public class MilStdAttributes {
     
    /*
     * Line color of the symbol. hex value.
     */
    public static final String LineColor = "LINECOLOR";
    
    /*
     * Fill color of the symbol. hex value
     */
    public static final String FillColor = "FILLCOLOR";
    
    /**
     * Used to change the icon from its normally black color.
     */
    public static final String IconColor = "ICONCOLOR";
    
    /*
     * Fill color of the symbol. hex value
     */
    public static final String TextColor = "TEXTCOLOR";
    
    /*
     * Fill color of the symbol. hex value
     */
    public static final String TextBackgroundColor = "TEXTBACKGROUNDCOLOR";
    
    /*
     * size of the single point image
     */
    public static final String PixelSize = "SIZE";
    
    /*
     * scale value to grow or shrink single point tactical graphics.
     */
    public static final String Scale = "SCALE";
    
    /**
     * defaults to true
     */
    public static final String KeepUnitRatio = "KEEPUNITRATIO";
    
    /*
     * transparency value of the symbol. values from 0-255
     */
    public static final String Alpha = "ALPHA";
    
    /*
     * outline the symbol, true/false
     */
    public static final String OutlineSymbol = "OUTLINESYMBOL";
    
    /*
     * specify and outline color rather than letting renderer picking 
     * the best contrast color. hex value
     */
    public static final String OutlineColor = "OUTLINECOLOR";
    
    /*
     * 2525B vs 2525C. 
     * like:
     * RendererSettings.Symbology_2525Bch2_USAS_13_14
     * OR
     * RendererSettings.Symbology_2525C
     */
    public static final String SymbologyStandard = "SYMSTD";
    
    public static final String Renderer = "RENDERER";
    
    
   
    
    
    /**
     * for singlepoints, if set to "true", no labels will be drawn and you
     * will just get the core symbol.
     */
    public static final String DrawAsIcon = "ICON";

}
