/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer.common;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael.spinelli
 */
public class TacticalGraphicLookup {

    private static final Logger logger = LoggerFactory.getLogger(TacticalGraphicLookup.class);

    private final ArrayList<String> alSymbolID = new ArrayList<>();
    private final ArrayList<Integer> alMapping = new ArrayList<>();

    private final Map<String, Integer> lookup = new HashMap<>();

    private static TacticalGraphicLookup _instance = null;

    private TacticalGraphicLookup() {
        init();
    }

    public static synchronized TacticalGraphicLookup getInstance() {
        if (_instance == null) {
            _instance = new TacticalGraphicLookup();
        }
        return _instance;
    }

    /**
     * @name init
     *
     * @desc Simply calls xmlLoaded
     *
     * @return None
     */
    private void init() {
        xmlLoaded();
    }

    /**
     * @name xmlLoaded
     *
     * @desc
     *
     * @param event - IN - Not used
     * @return None
     */
    private void xmlLoaded() {
        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("XML/TacticalGraphics.xml");
        String lookupXml = FileHandler.InputStreamToString(xmlStream);
        populateLookup(lookupXml);
    }

    /**
     * @name populateLookup
     *
     * @desc
     *
     * @param xml - IN -
     * @return None
     */
    private void populateLookup(String xml) {
        ArrayList al = XMLUtil.getItemList(xml, "<SYMBOL>", "</SYMBOL>");
        for (int i = 0; i < al.size(); i++) {
            String data = (String) al.get(i);

            String basicID = XMLUtil.parseTagValue(data, "<SYMBOLID>", "</SYMBOLID>");
            //String description = XMLUtil.parseTagValue(data, "<DESCRIPTION>", "</DESCRIPTION>");
            String mapping = XMLUtil.parseTagValue(data, "<MAPPING>", "</MAPPING>");

            mapping = checkMappingIndex(mapping);
            lookup.put(basicID, Integer.parseInt(mapping));

        }
    }

    /**
     * Until XML files are updated, we need to shift the index
     *
     * @param index
     * @return
     */
    private static String checkMappingIndex(String index) {
        int i;
        if (SymbolUtilities.isNumber(index)) {
            i = Integer.valueOf(index);

            return String.valueOf(i + 57000);
        }
        return index;
    }

    /**
     * given the milstd symbol code, find the font index for the symbol.
     *
     * @param symbolCode
     * @return
     */
    public int getCharCodeFromSymbol(String symbolCode) {
        int symStd = RendererSettings.getInstance().getSymbologyStandard();

        return getCharCodeFromSymbol(symbolCode, symStd);
    }

    public int getCharCodeFromSymbol(String symbolCode, int symStd) {

        try {

            String basicID = symbolCode;
            int charCode = -1;
            if (SymbolUtilities.isWeather(symbolCode) == false) {
                basicID = SymbolUtilities.getBasicSymbolID(symbolCode);
            }
            if (lookup.containsKey(basicID)) {
                charCode = lookup.get(basicID);
                if (charCode == 59053) {
                    if (symStd == 1) {
                        charCode = 59052;
                    }
                }
            }
            return charCode;
        } catch (Exception ex) {
            logger.error("error getting char code from symbol", ex);
        }
        return -1;

    }

}
