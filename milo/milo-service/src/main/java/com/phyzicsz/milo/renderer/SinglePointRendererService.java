/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phyzicsz.milo.renderer;

import com.phyzicsz.milo.renderer.plugin.ISinglePointRenderer;
import com.phyzicsz.milo.renderer.plugin.ISinglePointInfo;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael.spinelli
 */
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class SinglePointRendererService {

    private static final Logger logger = LoggerFactory.getLogger(SinglePointRendererService.class);

    private static SinglePointRendererService service;
//    private static ServiceLoader<ISinglePointRenderer> loader;
    private static Map<String, ISinglePointRenderer> spRenderers = new HashMap<String, ISinglePointRenderer>();

    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);

    private SinglePointRendererService() {
        try {
            SinglePoint2525Renderer renderer = new SinglePoint2525Renderer();
            spRenderers.put(SinglePoint2525Renderer.RENDERER_ID, renderer);
//            loader = ServiceLoader.load(com.phyzicsz.milo.renderer.plugin.ISinglePointRenderer.class);
        } catch (Exception ex) {
            logger.error("error creating rendering serice", ex);

        }
    }

    public static synchronized SinglePointRendererService getInstance() {
        if (service == null) {
            service = new SinglePointRendererService();
        }
        return service;
    }

    public ISinglePointInfo render(String rendererID, String symbolID, Map<String, String> params) {
        ISinglePointInfo returnVal = null;
        try {
            rwl.readLock().lock();
            ISinglePointRenderer renderer = spRenderers.get(rendererID);

            if (renderer != null) {  
                try {
                    returnVal = renderer.render(symbolID, params);
                } catch (Exception ex) {
                    //using Level.FINER because a null value will cause the
                    //milstd2525 renderer to draw an unknown symbol.
                    String message = "Plugin \""
                            + rendererID
                            + "\" failed to produce an image for symboldID \""
                            + symbolID + "\"";
                    logger.error("render error: {}", message,ex);
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error("render error", ex);
        } finally {
            rwl.readLock().unlock();
            return returnVal;
        }

    }


    public Boolean hasRenderer(String rendererID) {
        try {
            if (spRenderers != null && spRenderers.containsKey(rendererID)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            logger.error("error checking renderer", ex);
        }
        return false;
    }
}
