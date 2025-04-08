package ar.edu.unq.eperdemic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories

@SpringBootApplication
@EnableNeo4jRepositories // Reemplaza "com.tu.paquete" con el paquete base de tu aplicación

class SpringApplication

fun main(args: Array<String>) {
	runApplication<SpringApplication>(*args)
}

