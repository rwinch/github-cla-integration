package com.gopivotal.cla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.OrderedRequestContextFilter;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.RequestContextFilter;

@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class CliApplication {

	public static void main(String[] args) {
		SpringApplication.run(CliApplication.class, args);
	}

	@Bean
	public RequestContextFilter requestContextFilter() {
		OrderedRequestContextFilter filter = new OrderedRequestContextFilter();
		filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return filter;
	}

}