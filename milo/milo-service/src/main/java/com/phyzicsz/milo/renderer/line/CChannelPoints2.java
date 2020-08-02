/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phyzicsz.milo.renderer.line;

/**
 * A class for channel points used by clsChannelUtility
 * @author Michael Deutch
 */
public class CChannelPoints2
{
    protected POINT2 m_Line1;
    protected POINT2 m_Line2;
    protected CChannelPoints2()
    {
        m_Line1=new POINT2();
        m_Line2=new POINT2();
    }
    protected CChannelPoints2(CChannelPoints2 pts)
    {
        m_Line1=new POINT2(pts.m_Line1);
        m_Line2=new POINT2(pts.m_Line2);
    }
}
