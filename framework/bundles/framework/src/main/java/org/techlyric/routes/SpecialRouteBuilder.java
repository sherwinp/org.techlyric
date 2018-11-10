package org.techlyric.routes;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

public class SpecialRouteBuilder{
// The file poller route
   public static RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
			public void configure() {
                from("file://target/input?preMove=staging&move=.processed")
                .process(new Processor() {
                    @Override
					public void process(Exchange msg) {
                        CamelContext camelContext = msg.getContext();
                        ProducerTemplate producer = camelContext.createProducerTemplate();
                        File file = msg.getIn().getBody(File.class);
                        boolean specialFile = file.getName().toString().endsWith("_SPECIAL.dat");
                        if (specialFile)
                            producer.send("direct:specialRoute", msg);
                        else
                            producer.send("direct:normalRoute", msg);
                    }
                });
            }
        };
    }
}
