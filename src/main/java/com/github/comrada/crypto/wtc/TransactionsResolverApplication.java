package com.github.comrada.crypto.wtc;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.MethodHint;
import org.springframework.nativex.hint.TypeHint;

@SpringBootApplication
@TypeHint(types = Queue.class, methods = @MethodHint(name = "getName"))
public class TransactionsResolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsResolverApplication.class, args);
	}

}
