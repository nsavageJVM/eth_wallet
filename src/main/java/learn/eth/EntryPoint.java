package learn.eth;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
public class EntryPoint  {

	public static void main(String[] args) {  run(EntryPoint.class, args); }



}