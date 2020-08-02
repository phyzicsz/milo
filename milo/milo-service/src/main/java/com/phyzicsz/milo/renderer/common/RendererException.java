/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phyzicsz.milo.renderer.common;

/**
 *
 * @author michael.spinelli
 */
public class RendererException extends Exception {

    public RendererException(String message)
    {
        super(message);
    }
    
    public RendererException(String message, Throwable cause)
    {
        super(cause.getMessage() + " - " + message, cause);
    }
    
    public RendererException(Throwable cause)
    {
        super(cause);
    }

}
