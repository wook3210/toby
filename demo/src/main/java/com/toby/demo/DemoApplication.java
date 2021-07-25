package com.toby.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@SpringBootApplication
@EnableAsync
public class DemoApplication {

	@Component
	public static class MyService{
		@Async(value = "tp1")
		public ListenableFuture<String> hello() throws InterruptedException{
			log.info("hello()");
			Thread.sleep(2000);
			return new AsyncResult<String>("hello");
		}
	}

	/***
	 * SimpleAsyncTaskExecutor 절대 사용 금지
	 * 스프링 ThreadPoolTaskExecutor 사용 권장
	 * @return
	 */
	@Bean
	ThreadPoolTaskExecutor tp1(){
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(10);	//기본 10개
		te.setQueueCapacity(100); // 큐가 꽉차면
		te.setMaxPoolSize(100);	// MAX 추가
		te.setThreadNamePrefix("mythread");
		te.initialize();
		return te;
	}

	public static void main(String[] args) {
		try(ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class,args)){
		}
	}

	@Autowired
	MyService myService;

	@Bean
	ApplicationRunner run(){
		return args -> {

			log.info("run");
			ListenableFuture<String> f = myService.hello();
			f.addCallback(s-> System.out.print(s), e-> System.out.print(e));

			log.info("exit");
		};
	}

}
