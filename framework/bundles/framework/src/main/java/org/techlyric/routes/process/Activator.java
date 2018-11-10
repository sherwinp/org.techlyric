package org.techlyric.routes.process;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.FileNotFoundException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jpy.PyInputMode;
import org.jpy.PyLib;
import org.jpy.PyObject;

public final class Activator extends AbstractVerticle {
	private final static Logger logger = LogManager.getLogManager().getLogger(Activator.class.toString());

	@Override
	public void init(Vertx vertx, Context context) {

	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
			String[] args = {};
	        if(!PyLib.isPythonRunning())
	        	PyLib.startPython();

	    	try {
				PyObject.executeScript("uiview.py", PyInputMode.SCRIPT, PyLib.getMainGlobals(), null );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        PyLib.stopPython();
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		if(!PyLib.isPythonRunning())
        	PyLib.stopPython();
	}
}