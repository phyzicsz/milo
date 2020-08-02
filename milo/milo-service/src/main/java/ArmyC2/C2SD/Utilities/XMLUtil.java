package ArmyC2.C2SD.Utilities;

import java.util.ArrayList;

/**
 *
 * @author michael.spinelli
 */
public class XMLUtil {

    public static final String SymbolConstantsB = "xml/symbolconstantsb.xml";
    public static final String SymbolConstantsC = "xml/symbolconstantsc.xml";

    public static final String SinglePointB = "xml/singlepointb.xml";
    public static final String SinglePointC = "xml/singlepointc.xml";

    public static final String UnitConstantsB = "xml/unitconstantsb.xml";
    public static final String UnitConstantsC = "xml/unitconstantsc.xml";

    public static final String UnitFontMappingsB = "xml/unitfontmappingsb.xml";
    public static final String UnitFontMappingsC = "xml/unitfontmappingsc.xml";

    public static final String TacticalGraphics = "xml/tacticalgraphics.xml";

    /**
     * This method returns the list of objects in a list form.
     *
     * @param xml
     * @param startTag
     * @param endTag
     * @return
     */
    public static ArrayList<String> getItemList(String xml, String startTag, String endTag) {
        ArrayList<String> list = new ArrayList<String>();

        String value = null;
        try {
            int index = xml.indexOf(startTag);
            int index2 = 0;
            while (xml != null && index > -1 && index2 > -1) {
                index2 = xml.indexOf(endTag, index);
                if (index2 > -1) {
                    value = xml.substring(index, index2 + endTag.length());
                    list.add(value);
                    xml = xml.substring(index2 + endTag.length());
                    index = xml.indexOf(startTag);
                }
            }
        } catch (Throwable thrown) {
        }
        return list;
    }

    /**
     * This method parse the xml tag value.
     *
     * @param xml
     * @param tag
     * @param endTag
     * @return
     */
    public static String parseTagValue(String xml, String tag, String endTag) {
        String val = "";
        try {
            int index1 = xml.indexOf(tag) + tag.length();
            int index2 = xml.indexOf(endTag, index1);
            if (index1 > -1 && index2 > -1) {
                val = xml.substring(index1, index2);
            }
        } catch (Throwable thrown) {
            val = null;
        }
        return val;
    }

    /**
     * This method changes the value of the xml.
     *
     * @param xml
     * @param tag
     * @param endTag
     * @param value
     * @return
     */
    public static String changeTagValue(String xml, String tag, String endTag, String value) {
        int offset = 0;
        String v = value;
        while (xml.indexOf(tag, offset) > -1) {
            offset = xml.indexOf(tag, offset);
            int endoffset = xml.indexOf(endTag, offset);
            // Replace value
            xml = xml.substring(0, offset + tag.length()) + v + xml.substring(endoffset);
            offset = endoffset;
        }
        return xml;
    }

}
