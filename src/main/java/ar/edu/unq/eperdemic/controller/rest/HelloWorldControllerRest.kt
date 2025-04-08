package ar.edu.unq.eperdemic.controller


import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/hello")
class HelloWorldControllerRest() {

    @GetMapping
    fun helloWorld() = "Hello world!"
}