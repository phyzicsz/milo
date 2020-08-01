/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JavaLineArray;
import ArmyC2.C2SD.Utilities.ErrorLogger;
import ArmyC2.C2SD.Utilities.RendererException;
import ArmyC2.C2SD.Utilities.RendererSettings;
import java.awt.geom.Rectangle2D;
/**
 * A class to calculate the number of pixels based points required for a line
 * @author Michael Deutch
 */
public final class countsupport
{
    private static final double maxLength = 100;	//max arrow size
    private static final double minLength = 2.5;		//min arrow size
    private static final String _className = "countsupport";
    
//    protected static void setMinLength(double mLength)
//    {
//        minLength=mLength;
//    }
    /**
     * The main function to return the number of points needed for a symbol
     * @param vbiDrawThis the line type
     * @param vblCounter the number of client points
     * @param pLinePoints the client point array
     * @return the number of points required to draw the symbol
     */
    protected static int GetCountersDouble(int vbiDrawThis,
                                int vblCounter,
                                POINT2[] pLinePoints,
                                Rectangle2D clipBounds,
                                int rev)
    {
        int count=0;
        try
        {
            //declaration section
            int j = 0;
            int vblSaveCounter = vblCounter;
            POINT2[] pSquarePoints = new POINT2[4];
            POINT2[] pUpperLinePoints = null,
                pLowerLinePoints = null;
            int[] segments = null;
            POINT2[] pNewLinePoints = null;
            double dRadius = 0;
            POINT2[] pointsCorner = new POINT2[2];
            //double saveMaxPixels = 2000;//CELineArrayGlobals.MaxPixels2;

            pUpperLinePoints=new POINT2[vblCounter];
            pLowerLinePoints=new POINT2[vblCounter];

            for(j=0;j<vblCounter;j++)
            {
                pUpperLinePoints[j]=new POINT2(pLinePoints[j]);
                pLowerLinePoints[j]=new POINT2(pLinePoints[j]);
            }
            lineutility.InitializePOINT2Array(pointsCorner);
            lineutility.InitializePOINT2Array(pSquarePoints);
            //end delcarations
            switch (vbiDrawThis)
            {
                case TacticalLines.BS_ELLIPSE:
                case TacticalLines.PBS_ELLIPSE:
                case TacticalLines.PBS_CIRCLE:
                    count=37;
                    break;
                case TacticalLines.BS_CROSS:
                    count=4;
                    break;
//                case TacticalLines.BS_RECTANGLE:
//                case TacticalLines.BBS_RECTANGLE:
//                    count=5;
//                    break;
                case TacticalLines.OVERHEAD_WIRE:
                    count=vblCounter*15;    //15 points per segment
                    break;
                case TacticalLines.OVERHEAD_WIRE_LS:
                    count=vblCounter*2;    //15 points per segment
                    break;
                case TacticalLines.ICE_DRIFT:
                    vblCounter += 3;	//3 extra points for the arrow
                    count=vblCounter;
                    break;
                case TacticalLines.REEF:
                    vblCounter = GetReefCount(pLinePoints, vblSaveCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.RESTRICTED_AREA:
                    vblCounter = GetRestrictedAreaCount(pLinePoints, vblSaveCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.TRAINING_AREA:
                    vblCounter += 30;
                    count=vblCounter;
                    break;
                case TacticalLines.PIPE:
                    count = GetPipeCount(pLinePoints, vblSaveCounter);
                    break;
                case TacticalLines.ANCHORAGE_AREA:
                case TacticalLines.ANCHORAGE_LINE:
                    //vblSaveCounter = vblCounter;
                    count = flot.GetAnchorageCountDouble(pLinePoints, vblCounter);
                    break;
                case TacticalLines.LRO:
                    int xCount=GetXPointsCount(vbiDrawThis, pLinePoints,vblCounter);
                    int lvoCount=GetLVOCount(vbiDrawThis, pLinePoints,vblCounter);
                    count=xCount+lvoCount;
                    break;
                case TacticalLines.LVO:
                    count=GetLVOCount(vbiDrawThis, pLinePoints,vblCounter);
                    break;
                case TacticalLines.ICING:
                    vblCounter = GetIcingCount(pLinePoints, vblCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.FLOT:
                case TacticalLines.MVFR:
                case TacticalLines.UNDERCAST:
                    vblSaveCounter = vblCounter;
                    vblCounter = flot.GetFlotCountDouble(pLinePoints, vblCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.ITD:
                    vblCounter = GetITDQty(pLinePoints, vblCounter) + vblCounter;
                    count=vblCounter;
                    break;
                case TacticalLines.CONVERGANCE:
                    vblCounter = GetConverganceQty(pLinePoints, vblCounter) + vblCounter;
                    count=vblCounter;
                    break;
                case TacticalLines.RIDGE:
                    vblCounter = GetFORTLCountDouble(pLinePoints, vbiDrawThis, vblSaveCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.TROUGH:
                case TacticalLines.INSTABILITY:
                case TacticalLines.SHEAR:
                    //CELineArrayGlobals.MaxPixels2=saveMaxPixels+100;
                    vblCounter = GetSquallQty(pLinePoints, 6, 30, (int)vblSaveCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.SQUALL:
                    vblCounter = GetSquallQty(pLinePoints, 5, 30, (int)vblSaveCounter)+ 2 * vblSaveCounter;
                    count=vblCounter;
                    break;
                case TacticalLines.USF:
                case TacticalLines.SFG:
                case TacticalLines.SFY:
                case TacticalLines.SF:
                    vblCounter = flot.GetSFCountDouble(pLinePoints, vblCounter);
                    count=vblCounter;
                    break;
                case TacticalLines.OFY:
                    vblSaveCounter = vblCounter;
                    vblCounter = flot.GetOFYCountDouble(pLinePoints, vblCounter, vbiDrawThis);
                    count=vblCounter;
                    break;
                case TacticalLines.UCF:
                case TacticalLines.CF:
                case TacticalLines.CFG:
                case TacticalLines.CFY:
                    count = GetFORTLCountDouble(pLinePoints, vbiDrawThis, vblSaveCounter);
                    count += vblSaveCounter;
                    break;
                case TacticalLines.FOLLA:
                case TacticalLines.FOLSP:
                    //count = vblCounter + 14;
                    count=16;
                    break;
                case TacticalLines.ROADBLK:
                case TacticalLines.FERRY:
                    //vblCounter = vblCounter + 6;
                    count=8;
                    break;
                case TacticalLines.NAVIGATION:
                case TacticalLines.IL:
                case TacticalLines.PLANNED:
                case TacticalLines.ESR1:
                case TacticalLines.ESR2:
                case TacticalLines.FORDSITE:
                case TacticalLines.FOXHOLE:
                case TacticalLines.DECEIVE:
                    count = 4;
                    break;
                //case TacticalLines.FLOT:
                //case TacticalLines.MVFR:
                //case TacticalLines.UNDERCAST:
                    //count = flot.GetFlotCountDouble(pLinePoints, vblCounter);
                    //break;
                case TacticalLines.TRIP:
                    count = 35;
                    break;
                case TacticalLines.AMBUSH:	//extra 3 for open arrow, extra 26 for the tail arc,
                    //and an extra 22 for the tail line segments
                    count = 53;//vblCounter+51;
                    break;
                case TacticalLines.CLUSTER:
                    count = 28;
                    break;
                case TacticalLines.DUMMY:
                    //count = 33; //was 58
                    count=vblCounter+3;   //commented 5-3-10
                    break;
                case TacticalLines.CONTAIN:
                    count = 40;
                    break;
                case TacticalLines.BYIMP:
                    count = 18;
                    break;
                case TacticalLines.SPTBYFIRE:
                    count = 16;
                    break;
                case TacticalLines.BLOCK:
                case TacticalLines.MNFLDBLK:
                    count = 4;
                    break;
                case TacticalLines.PAA_RECTANGULAR:
                    count = 5;
                    break;
                case TacticalLines.PENETRATE:
                    count = 7;
                    break;
                case TacticalLines.ASLTXING:	//double for the channel type plus 4 for the hash marks
                case TacticalLines.BRIDGE:
                case TacticalLines.GAP:
                case TacticalLines.BYPASS:
                case TacticalLines.EASY:
                case TacticalLines.BREACH:
                case TacticalLines.CANALIZE:
                    count = 12;
                    break;
                case TacticalLines.MNFLDDIS:
                    count = 22;
                    break;
                case TacticalLines.WITHDRAW:
                case TacticalLines.WDRAWUP:
                case TacticalLines.DELAY:		//extra four points for hash marks on last segment
                case TacticalLines.RETIRE:
                    count = 23;
                    break;
                case TacticalLines.SEIZE:
                case TacticalLines.SEIZE_REVC:
                    count = 37;
                    break;
                case TacticalLines.RIP:
                    count = 29;
                    break;
                case TacticalLines.DIRATKSPT:
                    count = vblCounter + 3;
                    break;
                case TacticalLines.ABATIS:
                    count = vblCounter + 3;
                    break;
                case TacticalLines.FPF:	//extra two points for blocks at each end
                case TacticalLines.LINTGT:	//extra two points for blocks at each end
                case TacticalLines.LINTGTS:
                    count = vblCounter + 4;
                    break;
                case TacticalLines.CHANNEL:
                case TacticalLines.CHANNEL_FLARED:
                case TacticalLines.CHANNEL_DASHED:
                    //pvblCounters[0]=2*lElements;
                    //pvblCounters[1]=lElements;
                    count=2*vblCounter;
                    break;
                case TacticalLines.SARA:
                    count = 16;	//same for DISM
                    break;
                case TacticalLines.COVER:		//vblSaveCounter = vblCounter;
                case TacticalLines.SCREEN:
                case TacticalLines.GUARD:
                case TacticalLines.COVER_REVC:		//vblSaveCounter = vblCounter;
                case TacticalLines.SCREEN_REVC:
                case TacticalLines.GUARD_REVC:
                case TacticalLines.PDF:
                case TacticalLines.ATKBYFIRE:
                    count = 14;	//same for DISM
                    break;
                case TacticalLines.RAFT:
                case TacticalLines.MFLANE:	//extra eight points for hash marks at either end
                    //count = vblCounter + 6;
                    count = 8;
                    break;
                case TacticalLines.DIRATKGND:
                    count = vblCounter + 10;
                    break;
                case TacticalLines.DIRATKAIR:
                    count = vblCounter + 9;
                    //count = vblCounter + 11;//12-17-10
                    break;
                case TacticalLines.DISRUPT:
                case TacticalLines.CLEAR:
                    count = 20;
                    break;
                case TacticalLines.UAV_USAS:
                case TacticalLines.MRR_USAS:
                    vblCounter = 6 * (vblSaveCounter - 1);	//6 per segment
                    count = vblCounter + 26 * vblSaveCounter*2;	//26 for each circle and potentially two circles at each endpoint                        
                    break;
                case TacticalLines.UAV:
                case TacticalLines.MRR:
                    //count = 6;
                    if(rev==RendererSettings.Symbology_2525C)
                    {
                        vblCounter = 6 * (vblSaveCounter - 1);	//6 per segment
                        count = vblCounter + 26 * vblSaveCounter*2;	//26 for each circle and potentially two circles at each endpoint                        
                    }
                    else
                        count=6;
                    break;
                case TacticalLines.DIRATKFNT:	//extra three for arrow plus extra three for the feint
                    count = vblCounter + 6;
                    break;
                case TacticalLines.MSDZ:
                    count = 300;
                    break;
                case TacticalLines.CONVOY:
                case TacticalLines.HCONVOY:
                    count = 10;
                    break;
                case TacticalLines.ISOLATE:
                case TacticalLines.CORDONKNOCK:
                case TacticalLines.CORDONSEARCH:
                    count = 50;
                    break;
                case TacticalLines.OCCUPY:
                    count = 32;
                    break;
                case TacticalLines.SECURE:
                    count = 29;
                    break;
                case TacticalLines.RETAIN:
                    count = 75;
                    break;
                case TacticalLines.TURN:
                    count = 29;
                    break;
                case TacticalLines.AIRFIELD:
                    count = vblCounter + 5;
                    break;
                case TacticalLines.DMA:
                    count=vblCounter + 4;
                    break;
                case TacticalLines.DMAF:
                    count=vblCounter + 3;
                    break;
                case TacticalLines.ALT:
                    count = vblCounter * 9;
                    break;
                case TacticalLines.TWOWAY:
                    count = vblCounter * 11;
                    break;
                case TacticalLines.ONEWAY:
                    count = vblCounter * 6;
                    break;
                case TacticalLines.WF:
                case TacticalLines.UWF:
                    vblCounter = flot.GetFlotCount2Double(pLinePoints, vblCounter, vbiDrawThis);
                    vblCounter += vblSaveCounter;
                    count=vblCounter;
                    break;
                case TacticalLines.WFG:
                case TacticalLines.WFY:
                    vblCounter = flot.GetFlotCount2Double(pLinePoints, vblCounter, vbiDrawThis);
                    count=vblCounter;
                    break;
                case TacticalLines.FORDIF:
                    dRadius = lineutility.CalcDistanceToLineDouble(pLinePoints[0], pLinePoints[1], pLinePoints[2]);
                    count=(int)( (dRadius/5)*3)+6;
                    if(clipBounds != null)
                    {
                        double width=clipBounds.getWidth();
                        double height=clipBounds.getHeight();
                        dRadius=Math.sqrt(width*width+height*height);
                        count = (int)(dRadius / 5) + 6;
                    }
                    
                    
                    break;
                case TacticalLines.ATDITCH:	//call function to determine the array size
                case TacticalLines.ATDITCHC:	//call function to determine the array size
                case TacticalLines.ATDITCHM:
                    count = GetDitchCountDouble(pLinePoints, vblSaveCounter, vbiDrawThis);
                    break;
                case TacticalLines.CATK:
                case TacticalLines.AXAD:
                case TacticalLines.MAIN:
                case TacticalLines.MAIN_STRAIGHT:
                case TacticalLines.AAFNT:
                case TacticalLines.AAFNT_STRAIGHT:
                case TacticalLines.AIRAOA:
                case TacticalLines.SPT:
                case TacticalLines.SPT_STRAIGHT:
                    //points for these need not be bounded
                    //they have an extra 8 points for the arrowhead
                    //pvblCounters[0]=2*lElements+8;
                    //pvblCounters[1]=lElements;
                    count=2*vblCounter+8;
                    break;
                case TacticalLines.CATKBYFIRE:
                    //pvblCounters[0] = 2 * lElements + 17;
                    //pvblCounters[1] = lElements;
                    count=2*vblCounter+17;
                    break;
                case TacticalLines.AAAAA:
                    //pvblCounters[0] = 2 * lElements + 19;
                    //pvblCounters[1] = lElements;
                    count=2*vblCounter+19;
                    break;
                case TacticalLines.FEBA:		//double for the shortened user line plus quadruple for
                    // X's, plus 26 times for the circles
                    count = 32 * vblCounter;
                    break;
                case TacticalLines.LLTR:  //added 5-4-07
                case TacticalLines.SAAFR:
                case TacticalLines.AC:
                    vblCounter = 6 * (vblSaveCounter - 1);	//6 per segment
                    count = vblCounter + 26 * vblSaveCounter*2;	//26 for each circle and potentially two circles at each endpoint
                    break;
                case TacticalLines.ATWALL:
                //case TacticalLines.ATWALL3D:
                case TacticalLines.LINE:
                case TacticalLines.OBSAREA:
                case TacticalLines.OBSFAREA:
                case TacticalLines.STRONG:
                case TacticalLines.ZONE:
                case TacticalLines.BELT:	//add per change 2
                case TacticalLines.ENCIRCLE:
                case TacticalLines.FORT:
                case TacticalLines.FORTL:
                    count = GetFORTLCountDouble(pLinePoints, vbiDrawThis, vblSaveCounter);
                    break;
                case TacticalLines.BELT1:
                    pUpperLinePoints = new POINT2[vblCounter];
                    pLowerLinePoints = new POINT2[vblCounter];
                    POINT2[]pUpperLowerLinePoints = new POINT2[2 * vblCounter];
                    for (j = 0; j < vblCounter; j++)
                        pLowerLinePoints[j] = pLinePoints[j];
                    for (j = 0; j < vblCounter; j++)
                        pUpperLinePoints[j] = pLinePoints[j];
                    pUpperLinePoints = Channels.CoordIL2Double(1, pUpperLinePoints, 1, vblCounter, vbiDrawThis,30);
                    pLowerLinePoints = Channels.CoordIL2Double(1, pLowerLinePoints, 0, vblCounter, vbiDrawThis,30);
                    for (j = 0; j < vblCounter; j++)
                        pUpperLowerLinePoints[j] = pUpperLinePoints[j];
                    for (j = 0; j < vblCounter; j++)
                        pUpperLowerLinePoints[j + vblCounter] = pLowerLinePoints[vblCounter - j - 1];
                    vblSaveCounter = 2 * vblCounter;
                    count = GetFORTLCountDouble(pUpperLowerLinePoints, vbiDrawThis, 2 * vblCounter);
                    break;
                case TacticalLines.TRIPLE:
                case TacticalLines.DOUBLEC:
                case TacticalLines.SINGLEC:
                case TacticalLines.HWFENCE:
                case TacticalLines.LWFENCE:
                case TacticalLines.UNSP:
                case TacticalLines.DOUBLEA:
                case TacticalLines.SFENCE:
                case TacticalLines.DFENCE:
                    count = Channels.GetTripleCountDouble(pLinePoints, vblCounter, vbiDrawThis);
                    break;
                case TacticalLines.BBS_LINE:
                    count=2*vblCounter;
                    break;
                case TacticalLines.LC:
                    pUpperLinePoints = Channels.GetChannelArray2Double(1,pUpperLinePoints,1,vblCounter,vbiDrawThis,20);
                    pLowerLinePoints = Channels.GetChannelArray2Double(1,pLowerLinePoints,0,vblCounter,vbiDrawThis,20);
                    int lUpperFlotCount= flot.GetFlotCountDouble(pUpperLinePoints,vblCounter);
                    int lLowerFlotCount= flot.GetFlotCountDouble(pLowerLinePoints,vblCounter);
                    count=lUpperFlotCount+lLowerFlotCount;
                    break;
                case TacticalLines.OCCLUDED:
                case TacticalLines.UOF:
                    vblSaveCounter = vblCounter;
                    vblCounter = flot.GetOccludedCountDouble(pLinePoints, vblCounter);
                    vblCounter += vblSaveCounter;
                    count=vblCounter;
                    break;
                case TacticalLines.FIX:
                case TacticalLines.MNFLDFIX:
                    if(pLinePoints.length>1)
                        count = GetDISMFixCountDouble(pLinePoints[0], pLinePoints[1],clipBounds);
                    else count=0;
                    break;
                case TacticalLines.BYDIF:
                    //commented section 10-27-10
//                    GetByDifSegment(pLinePoints, pointsCorner);
//                    vblCounter = lineutility.BoundPointsCount(pointsCorner, 2);
//                    segments = new int[vblCounter];
//                    pNewLinePoints = new POINT2[vblCounter];
//                    for (j = 0; j < 2; j++)
//                        pNewLinePoints[j] = new POINT2(pointsCorner[j]);
//
//                    vblCounter = lineutility.BoundPoints(pNewLinePoints, 2, segments);
//                    //there is only one possible viable segment since we bounded 2 points
//                    for (j = 0; j < vblCounter - 1; j++)
//                    {
//                        if (segments[j] != 0)
//                        {
//                            count = GetDISMFixCountDouble(pNewLinePoints[j], pNewLinePoints[j + 1]);
//                            break;
//                        }
//                    }
//                    count += 12;
                    //end section
                    if(clipBounds != null)
                    {
                        GetByDifSegment(pLinePoints, pointsCorner);
                        POINT2 ul=new POINT2(clipBounds.getMinX(),clipBounds.getMinY());    //-100,1000
                        POINT2 lr=new POINT2(clipBounds.getMaxX(),clipBounds.getMaxY());  //-100,1000
                        POINT2[] ptsCorner=lineutility.BoundOneSegment(pointsCorner[0],pointsCorner[1], ul, lr);

                        if(ptsCorner != null)
                            count = GetDISMFixCountDouble(ptsCorner[0], ptsCorner[1],clipBounds);
                        else
                            count=20;
                    }
                    else
                        count = GetDISMFixCountDouble(pLinePoints[0], pLinePoints[1],clipBounds);

                    break;
                default:
                    count=vblCounter;
                    break;
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"GetCountersDouble",
                    new RendererException("Failed inside GetCountersDouble " + Integer.toString(vbiDrawThis), exc));
        }
        return count;
    }
    //for DMAF, not currently used
//    protected static int GetXPointsCount(POINT2[] pLinePoints)
//    {
//        int total=0;
//        try
//        {
//            int j=0,iterations=0;
//            double dist=0;
//            for(j=0;j<pLinePoints.length-1;j++)
//            {
//                dist=lineutility.CalcDistanceDouble(pLinePoints[j],pLinePoints[j+1]);
//                iterations=(int)(dist-5)/10;
//                total += iterations *4;
//            }
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//        return total;
//    }

    private static int GetReefCount(POINT2[] pLinePoints,
            int vblCounter) {
        int count = 0;
        try {
            double d = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                count += 5 * (int) d / 40;
            }
            count += 2 * (int) vblCounter;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetReefCount",
                    new RendererException("Failed inside GetReefCount", exc));
        }
        return count;
    }
    private static int GetRestrictedAreaCount(POINT2[] pLinePoints,
            int vblCounter) {
        int count = 0;
        try {
            double d = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                count += 4 * (int) d / 15;
            }
            count += 2 * (int) vblCounter;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetRestrictedAreaCount",
                    new RendererException("Failed inside GetRestrictedAreaCount", exc));
        }
        return count;
    }

    private static int GetPipeCount(POINT2[] pLinePoints,
            int vblCounter) {
        int count = 0;
        try {
            double d = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                count += 3 * (int) d / 20;
            }
            count += 2 * (int) vblCounter;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetPipeCount",
                    new RendererException("Failed inside GetPipeCount", exc));
            }
        return count;
    }

    protected static int GetXPointsCount(int linetype, POINT2[] pOriginalLinePoints, int vblCounter)
    {
        int xCounter=0;
        try
        {
            int j=0;
            double d=0;
            //POINT2 pt0,pt1,pt2,pt3=new POINT2(),pt4=new POINT2(),pt5=new POINT2(),pt6=new POINT2();
            int numThisSegment=0;
            for(j=0;j<vblCounter-1;j++)
            {
                d=lineutility.CalcDistanceDouble(pOriginalLinePoints[j],pOriginalLinePoints[j+1]);
                numThisSegment=(int)((d-10)/20);
                if(linetype==TacticalLines.LRO)
                    numThisSegment=(int)((d-15)/30);
                xCounter += 4*numThisSegment;
            }
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"GetXPointsCount",
                    new RendererException("Failed inside GetXPointsCount", exc));
        }
        return xCounter;
    }

    protected static int GetLVOCount(int linetype, POINT2[] pOriginalLinePoints, int vblCounter)
    {
        int lEllipseCounter = 0;
        try {
            double d = 0;
            int lHowManyThisSegment = 0, j = 0;
            //end declarations
            for (j = 0; j < vblCounter - 1; j++)
            {
                d = lineutility.CalcDistanceDouble(pOriginalLinePoints[j], pOriginalLinePoints[j + 1]);
                //lHowManyThisSegment = (int) ((d - 20) / 20);
                lHowManyThisSegment = (int) ((d - 20) / 20)+1;
                if(linetype==TacticalLines.LRO)
                    lHowManyThisSegment = (int) ((d - 30) / 30)+1;
                lEllipseCounter += lHowManyThisSegment*37;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetLVOCount",
                    new RendererException("Failed inside GetLVOCount", exc));
        }
        return lEllipseCounter;
    }

    private static int GetIcingCount(POINT2[] points, int vblCounter) {
        int total = 2 * vblCounter;
        try {
            double length = 0;
            for (int j = 0; j < vblCounter - 1; j++) {
                length = lineutility.CalcDistanceDouble(points[j], points[j + 1]);
                length = (length / 15) * 4;
                total += length;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetIcingCount",
                    new RendererException("Failed inside GetIcingCount", exc));
            }
        return total;
    }

    protected static int GetITDQty(POINT2[] pLinePoints, int vblCounter) {
        int total = 0;
        try {
            int j = 0;
            double d = 0;
            int n=0;
            for (j = 0; j < vblCounter - 1; j++) {
                d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                n= 2 * (int) (d / 15);
                if(n<2)
                    n=2;
                //total += 2 * (int) (d / 15);
                total += n;
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetITDQty",
                    new RendererException("Failed inside GetITDQty", exc));
            }
        return total;
        }

    protected static int GetConverganceQty(POINT2[] pLinePoints, int vblCounter) {
        int total = vblCounter;
        try
        {
            int j = 0;
            double d = 0;
            for (j = 0; j < vblCounter - 1; j++)
            {
                d = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                total += 4 * (int) (d / 10);
            }
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetConverganceQty",
                    new RendererException("Failed inside GetConverganceQty", exc));
        }
        return total;
    }

    /**
     * Calculates the points for ATDITCH, ATDITCHC, ATDITCHM
     * @param pLinePoints the client point array
     * @param vblCounter the number of client points
     * @param vbiDrawThis the line type
     * @return
     */
    private static int GetDitchCountDouble(POINT2[] pLinePoints,
            int vblCounter,
            int vbiDrawThis) {
        int vblXCounter = 0;
        try {
            //declarations
            int j = 0;
            int nHowManyThisSegment = 0;
            double dHowFar = 0;
                    //dPrinter = (double) nPrinter;

            vblXCounter = vblCounter;

            for (j = 0; j < vblCounter - 1; j++) {
                dHowFar = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                //nHowManyThisSegment = (int) ((dHowFar - 3) / 12);
                nHowManyThisSegment = (int) ( (dHowFar-1) / 12);
                if (dHowFar > 24) {
                    switch (vbiDrawThis) {
                        //case TacticalLines.FORT:
                        //    break;
                        case TacticalLines.ATDITCHM:
                            vblXCounter += 5 * nHowManyThisSegment+1;//was 4 * nHowManyThisSegment
                            break;
                        default:
                            vblXCounter += 4 * nHowManyThisSegment;//was 3 * nHowManyThisSegment
                            break;
                    }	//end switch
                    } //end if
                else {
                    vblXCounter += 2;
                }
            }	//end for
            } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDitchcountDouble",
                    new RendererException("Failed inside GetDitchCountDouble " + Integer.toString(vbiDrawThis), exc));
        }
        return vblXCounter;
    }
    protected static int GetSquallQty(POINT2[] pLinePoints,
            int quantity,
            int length,
            int numPoints) {
        int counter = 0;
        try {
            int j = 0;
            double dist = 0;
            int numCurves = 0;
            //end declarations

            for (j = 0; j < numPoints-1; j++) {
                dist = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);
                numCurves = (int) (dist / (double) length);
                counter += numCurves * quantity;
                if (numCurves == 0) {
                    counter += 2;
                }
            }

            if (counter < numPoints) {
                counter = numPoints;
            }

            //clean up
            } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetSquallQty",
                    new RendererException("Failed inside GetSquallQty", exc));
        }
        return counter;
    }
    
    protected static int GetSquallSegQty(POINT2 StartPt,
            POINT2 EndPt,
            int quantity,
            int length) {
        int qty = 0;
        try {
            double dist = lineutility.CalcDistanceDouble(StartPt, EndPt);
            int numCurves = (int) (dist / (double) length);
            qty = numCurves * quantity;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetSquallSegQty",
                    new RendererException("Failed inside GetSquallSegQty", exc));
        }
        return qty;
    }

    /**
     * returns number of points required for ATWALL, FORT and other symbols
     * @param pLinePoints the client points
     * @param linetype the line type
     * @param vblCounter the number of client points
     * @return
     */
    protected static int GetFORTLCountDouble(POINT2[] pLinePoints,
            int linetype,
            int vblCounter) {
        int lCounter = 0;
        try {
            //declarations
            int j = 0;
            double dCounter = 0, dIncrement = 0;
            //end declarations

            switch (linetype) {
                    case TacticalLines.UCF:
                    case TacticalLines.CF:
                    case TacticalLines.CFG:
                    case TacticalLines.CFY:
                        dIncrement = 60;
                        break;
                    case TacticalLines.RIDGE:
                        dIncrement = 20;
                        break;
                default:
                    dIncrement = 20;
                    break;
            }

            for (j = 0; j < vblCounter - 1; j++) {
                dCounter = lineutility.CalcDistanceDouble(pLinePoints[j], pLinePoints[j + 1]);

                switch (linetype) {
                        case TacticalLines.CFG:
                            dCounter = (dCounter / dIncrement) * 13;
                            break;
                        case TacticalLines.CFY:
                            dCounter = (dCounter / dIncrement) * 17;
                            break;
                    default:
                        dCounter = (dCounter / dIncrement) * 10;
                        break;
                }

                if (dCounter < 4) {
                    dCounter = 4;
                }
                lCounter += (long) dCounter;
            }
            lCounter += 10 + vblCounter;

        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetFORTLCountDouble",
                    new RendererException("Failed inside GetFORTLCountDouble", exc));
            }
        return lCounter;
    }
    
    private static void GetByDifSegment(POINT2[] points, POINT2[] pointsCorner) {
        try {
            // draw open-ended rectangle
            POINT2 point_mid = new POINT2();
            //int j=0;
            //	POINT1 pts[4];
            if (pointsCorner == null) {
                pointsCorner = new POINT2[2];
                lineutility.InitializePOINT2Array(pointsCorner);
            }
            point_mid.x = (points[0].x + points[1].x) / 2;
            point_mid.y = (points[0].y + points[1].y) / 2;
            pointsCorner[0].x = points[0].x - point_mid.x + points[2].x;
            pointsCorner[0].y = points[0].y - point_mid.y + points[2].y;
            pointsCorner[1].x = points[1].x - point_mid.x + points[2].x;
            pointsCorner[1].y = points[1].y - point_mid.y + points[2].y;
            return;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetByDifSegment",
                    new RendererException("Failed inside GetByDifSegment", exc));
        }
    }
    /**
     * clipBounds is used because of the glyphs on one segment
     * @param FirstLinePoint
     * @param LastLinePoint
     * @param clipBounds
     * @return
     */
    protected static int GetDISMFixCountDouble(POINT2 FirstLinePoint,
            POINT2 LastLinePoint,
            Rectangle2D clipBounds) {
        int counter = 0;
        try {
            POINT2[] savepoints = new POINT2[2];
            //double dAngle1 = 0;
            double dLength = 0;
            double dJaggyHalfAmp = 0;
            double dJaggyHalfPeriod = 0;
            int iNumJaggies = 0;

            savepoints[0] = new POINT2(FirstLinePoint);
            savepoints[1] = new POINT2(LastLinePoint);
            
            //Boolean drawJaggies=true;
            if(clipBounds != null)
            {
                POINT2 ul=new POINT2(clipBounds.getMinX(),clipBounds.getMinY());
                POINT2 lr=new POINT2(clipBounds.getMaxX(),clipBounds.getMaxY());
                savepoints=lineutility.BoundOneSegment(FirstLinePoint, LastLinePoint, ul, lr);
            }

//            if(savepoints==null)
//            {
//                //savepoints[0] = new POINT2(FirstLinePoint);
//                //savepoints[1] = new POINT2(LastLinePoint);
//                //drawJaggies=false;
//                return 20;
//            }
            if(savepoints==null)
                return 0;

            dLength = Math.sqrt((savepoints[1].x - savepoints[0].x) * (savepoints[1].x - savepoints[0].x) +
                    (savepoints[1].y - savepoints[0].y) * (savepoints[1].y - savepoints[0].y));
            dJaggyHalfAmp = dLength / 15; // half the amplitude of the "jaggy function"

            if (dJaggyHalfAmp > maxLength) {
                dJaggyHalfAmp = maxLength;
            }
            if (dJaggyHalfAmp < minLength) {
                dJaggyHalfAmp = minLength;
            }

            dJaggyHalfPeriod = dJaggyHalfAmp / 1.5; // half the period of the "jaggy function"
            iNumJaggies = (int) (dLength / dJaggyHalfPeriod) - 3;
            if (iNumJaggies < 0) {
                iNumJaggies = 0;
            }

            savepoints = null;
            counter = 20 + iNumJaggies * 3;
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"GetDISMFixCount",
                    new RendererException("Failed inside GetDISMFixCount", exc));
        }
        return counter;
    }

}
