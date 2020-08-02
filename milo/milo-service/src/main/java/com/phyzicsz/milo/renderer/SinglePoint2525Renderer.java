/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer;

import com.phyzicsz.milo.renderer.plugin.ISinglePointInfo;
import com.phyzicsz.milo.renderer.plugin.ISinglePointRenderer;
import com.phyzicsz.milo.renderer.plugin.SinglePointInfo;
import com.phyzicsz.milo.renderer.common.IPointConversion;
import com.phyzicsz.milo.renderer.common.ImageInfo;
import com.phyzicsz.milo.renderer.common.MilStdAttributes;
import com.phyzicsz.milo.renderer.common.MilStdSymbol;
import com.phyzicsz.milo.renderer.common.PointConversionDummy;
import com.phyzicsz.milo.renderer.common.RendererException;
import com.phyzicsz.milo.renderer.common.RendererSettings;
import com.phyzicsz.milo.renderer.common.SymbolDef;
import com.phyzicsz.milo.renderer.common.SymbolDefTable;
import com.phyzicsz.milo.renderer.common.SymbolUtilities;
import java.awt.Color;
import java.util.Map;
import com.phyzicsz.milo.renderer.utilities.JavaRendererUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael.spinelli
 */
public class SinglePoint2525Renderer implements ISinglePointRenderer {

    private static final Logger logger = LoggerFactory.getLogger(SinglePoint2525Renderer.class);

    private static IJavaRenderer jr = null;
    private static TacticalGraphicIconRenderer tgir = null;
    public static final String RENDERER_ID = "2525";

    public SinglePoint2525Renderer() {
        jr = JavaRenderer.getInstance();
        tgir = TacticalGraphicIconRenderer.getInstance();
    }

    @Override
    public String getRendererID() {
        return RENDERER_ID;
    }

    @Override
    public Boolean canRender(String symbolID, Map<String, String> params) {
        MilStdSymbol ms = JavaRendererUtilities.createMilstdSymbol(symbolID, params);
        return jr.canRender(ms);
    }

    @Override
    public ISinglePointInfo render(String symbolID, Map<String, String> params) {

        // prepare implement IPointConversion or use our basic point
        // conversion class
        IPointConversion pConverter = new PointConversionDummy();
        ImageInfo ii = null;
        SymbolDef sd = null;
        ISinglePointInfo spi = null;
        MilStdSymbol ms = null;

        int symStd = RendererSettings.getInstance().getSymbologyStandard();

        if (params.containsKey(MilStdAttributes.SymbologyStandard)) {
            String ss = params.get(MilStdAttributes.SymbologyStandard);
            if (ss.length() != 1) {
                if (ss.startsWith("2525") && ss.length() == 5) {
                    char version = ss.charAt(4);
                    switch (version) {
                        case 'B':
                        case 'b':
                            symStd = 0;
                            break;
                        case 'C':
                        case 'c':
                            symStd = 1;
                            break;
                        case 'D':
                        case 'd':
                            symStd = 2;
                            break;
                        default:
                            symStd = RendererSettings.getInstance().getSymbologyStandard();
                            break;
                    }
                } else {
                    symStd = RendererSettings.getInstance().getSymbologyStandard();
                }
            } else {
                char version = ss.charAt(0);
                switch (version) {
                    case '0':
                    case '1':
                    case '2':
                        break;
                    default:
                        symStd = RendererSettings.getInstance().getSymbologyStandard();
                        break;
                }
            }
        }

        try {
            if (SymbolUtilities.isTacticalGraphic(symbolID)) {
                sd = SymbolDefTable.getInstance().getSymbolDef(SymbolUtilities.getBasicSymbolID(symbolID), symStd);
            }

            if (sd != null && sd.getDrawCategory() != SymbolDef.DRAW_CATEGORY_POINT) {
                int size = 35;
                Color lineColor = SymbolUtilities.getLineColorOfAffiliation(symbolID);
                if (params.containsKey(MilStdAttributes.PixelSize)) {
                    size = Integer.parseInt(params.get(MilStdAttributes.PixelSize));
                }
                if (params.containsKey(MilStdAttributes.LineColor)) {
                    lineColor = SymbolUtilities.getColorFromHexString(params.get(MilStdAttributes.LineColor));
                }
                //call TG icon renderer for multipoints
                ii = tgir.getIcon(symbolID, size, lineColor, symStd);
            } else {
                ms = JavaRendererUtilities.createMilstdSymbol(symbolID, params);
                jr.Render(ms, pConverter, null);
                ii = ms.toImageInfo();
            }

            //in case rendering on the given symbolID fails.
            if (ii == null) {
                if (ms == null) {
                    ms = JavaRendererUtilities.createMilstdSymbol(symbolID, params);
                }
                String tempID = "S" + symbolID.charAt(1) + "Z" + symbolID.charAt(3) + symbolID.substring(4);
                tempID = SymbolUtilities.reconcileSymbolID(tempID, false);
                ms.setSymbolID(tempID);
                ms.setLineColor(SymbolUtilities.getLineColorOfAffiliation(tempID));
                ms.setFillColor(SymbolUtilities.getFillColorOfAffiliation(tempID));
                ms.setOutlineEnabled(false);
                if (params.containsKey(MilStdAttributes.FillColor)) {
                    ms.setFillColor(SymbolUtilities.getColorFromHexString(params.get(MilStdAttributes.FillColor)));
                }
                if (params.containsKey(MilStdAttributes.LineColor)) {
                    ms.setLineColor(SymbolUtilities.getColorFromHexString(params.get(MilStdAttributes.LineColor)));
                }

                jr.Render(ms, pConverter, null);
                ii = ms.toImageInfo();
            }

            //ImageInfo ii = jr.RenderSinglePointAsImageInfo(symbolID, params, ms.getUnitSize(), ms.getKeepUnitRatio());
            spi = new SinglePointInfo(ii.getImage(), ii.getSymbolCenterPoint(), ii.getSymbolBounds());
        } catch (RendererException | NumberFormatException exc) {
            logger.error("error rendering symbol", exc);
        }
        return spi;

    }

}
