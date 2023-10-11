package com.smutsx.lbs;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 * @author bill
 *
 */
@SpringBootApplication
public class LBSApplication {

	public static void main(String[] args) {
		SpringApplication.run(LBSApplication.class, args);
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
