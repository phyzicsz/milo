/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phyzicsz.milo.renderer.common;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for loading the single point & unit fonts into memory
 * @author michael.spinelli
 */
public class SinglePointFont {
    private static final Logger logger = LoggerFactory.getLogger(SinglePointFont.class);
    


    static SinglePointFont _instance = null;
    //static InputStream _unitFontStream = null;
    //static InputStream _spFontStream = null;

    private SinglePointFont()
    {
        //Init();

    }

    public static synchronized SinglePointFont getInstance()
    {
        if(_instance == null)
        {
            _instance = new SinglePointFont();
        }


        return _instance;
    }

    /**
     * Font used to render force elements (units).
     * @param size
     * @return 
     */
    public Font getUnitFont(float size)
    {
        //load font from resource
        InputStream fontStream = _instance.getClass().getClassLoader().getResourceAsStream("fonts/UnitFont.ttf");
        //InputStream fontStream = _unitFontStream;

        Font newFont = null;
        try
        {
            //create font
            newFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        }
        catch(FontFormatException | IOException ex)
        {
            logger.error("failed to get font", ex);
        }

        //resize font
        newFont = newFont.deriveFont(Font.TRUETYPE_FONT, size);
        //return font
        return newFont;

    }

    /**
     * Font used to render single point tactical graphics
     * @param size
     * @return 
     */
    public Font getSPFont(float size)
    {
        //load font from resource
        InputStream fontStream = _instance.getClass().getClassLoader().getResourceAsStream("fonts/SinglePoint.ttf");
        //InputStream fontStream = _spFontStream;

        Font newFont = null;
        try
        {
            //create font
            newFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        }
        catch(FontFormatException | IOException ex)
        {
            logger.error("failed to get font", ex);
          
        }
        

        //resize font
        newFont = newFont.deriveFont(Font.TRUETYPE_FONT, size);
        //return font
        return newFont;

    }
    
    /**
     * Font used to make icons of multipoint tactical graphics
     * @param size
     * @return 
     */
    public Font getTGFont(float size)
    {
        //load font from resource
        InputStream fontStream = _instance.getClass().getClassLoader().getResourceAsStream("fonts/TacticalGraphics.ttf");
        //InputStream fontStream = _spFontStream;

        Font newFont = null;
        try
        {
            //create font
            newFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        }
        catch(FontFormatException | IOException ex)
        {
            logger.error("failed to get font", ex);
           
        }
        

        //resize font
        newFont = newFont.deriveFont(Font.TRUETYPE_FONT, size);
        //return font
        return newFont;

    }

}
