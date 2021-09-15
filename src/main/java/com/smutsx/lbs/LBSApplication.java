package com.smutsx.lbs;

import com.smutsx.lbs.LBS.controller.LbsProfileController;
import com.smutsx.lbs.LBS.server.AlgorithmSelect;
import com.smutsx.lbs.LBS.server.socket.SocketServer;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 * @author bill
 *
 */
@SpringBootApplication
public class LBSApplication {

	private static ApplicationContext run;
	public static void main(String[] args) {
		run = SpringApplication.run(LBSApplication.class, args);
		run.getBean(SocketServer.class).start();
	}
	public static Object getBean(Class<LbsProfileController> name){
		return getApplicationContext().getBean(name);
	}

	private static ApplicationContext getApplicationContext() {
		return run;
	}

	@Bean
	public TomcatServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers((Connector connector) -> {
			connector.setProperty("relaxedPathChars", "<>[\\]^`{|}");
			connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}");
		});
		return factory;
	}
	
}
