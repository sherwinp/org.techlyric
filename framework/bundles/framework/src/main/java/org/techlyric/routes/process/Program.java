package org.techlyric.routes.process;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.techlyric.routes.SpecialRouteBuilder;


import org.jpy.*;


public final class Program extends Main implements SubscriberExceptionHandler{
	private static Logger logger = LogManager.getLogManager().getLogger(Program.class.toString());
	private static Program __program__ = new Program();
	private static EventBus eventBus = new EventBus(__program__);
    static {
    	__program__.getCamelContexts().clear();

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("eventBus", eventBus);

        eventBus.register(new Object(){
        	  @Subscribe
        	  public void messageHander(String message) {
        	    System.out.println("Message received from the Camel: " + message);
        	  }
        });

        DefaultCamelContext context = new DefaultCamelContext(registry);
        __program__.getCamelContexts().add(context);
    }
    public static EventBus getEventBus() { return eventBus; }

    public static void post(final Object event) {
		getEventBus().post(event);;
	}
	public static void register(final Object object) {
		getEventBus().register(object);
	}
	public static void unregister(final Object object) {
		getEventBus().unregister(object);
	}
	public static void main(String[] args) throws Exception {

		__program__.addRouteBuilder(SpecialRouteBuilder.createRouteBuilder());

        if(!PyLib.isPythonRunning())
        	PyLib.startPython();

    	PyModule sys = PyModule.importModule("sys");
    	PyModule os = PyModule.importModule("os");
    	String[] argv = sys.getAttribute("argv", String[].class);

        PyObject globals = PyLib.getMainGlobals();

        globals.asDict().putObject("eventBus", eventBus);

    	PyObject.executeScript("uiview.py", PyInputMode.SCRIPT, globals, null );

    	__program__.shutdown();
        PyLib.stopPython();
	}

	@Override
	public void handleException(Throwable exception, SubscriberExceptionContext context) {
		exception.printStackTrace();

	}

}