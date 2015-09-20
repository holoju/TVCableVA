package com.taller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;


/**
 * Clase principal del proyecto
 * Created by taller on 05/08/2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class); //para logers


    public static void main(String[] args) {

        // Configuracion para la carpeta de archivos estaticos
        staticFileLocation("/");

        final Configuration configuracion = new Configuration(new Version("2.3.23")); //configuracion para los templates
        configuracion.setClassForTemplateLoading(Main.class, "/"); //direcciona la carpeta de vistas




     /*   Spark.get(new Route("/") {  //la ruta es la url del navegador
            @Override
            public Object handle(Request request, Response response) {
                StringWriter writer = new StringWriter();
                try {
                    Template hola = configuracion.getTemplate("login.ftl"); //se carga el template

                    Map<String, Object> datos = new HashMap<String, Object>(); //se crea un map para enviar datos al template
                    datos.put("nombre", "Oscar"); //se adiciona valores al map de datos
                    datos.put("ap", "Lopez"); //se adiciona valores

                    hola.process(datos, writer); //se pocesa el template y se obtiene el writer completo

                } catch (Exception e) {
                    halt(500);
                    e.printStackTrace();
                }
                log.info("LLamando a la primer pantalla");
                return writer; //se retorna el writer con todos el codigo
            }
        });

        Spark.get(new Route("/nuevo") {  //la ruta es la url del navegador
            @Override
            public Object handle(Request request, Response response) {
                //response.type("text/html");
                StringWriter writer = new StringWriter();
                try {
                    Template hola = configuracion.getTemplate("pages/examples/login.ftl"); //se carga el template

                    Map<String, Object> datos = new HashMap<String, Object>(); //se crea un map para enviar datos al template
                    datos.put("nombre", "Oscar"); //se adiciona valores al map de datosl
                    datos.put("ap", "Lopez"); //se adiciona valores

                    hola.process(datos, writer); //se pocesa el template y se obtiene el writer completo

                } catch (Exception e) {
                    halt(500);
                    e.printStackTrace();
                }
                log.info("LLamando a la primer pantalla");
                return writer; //se retorna el writer con todos el codigo
            }
        });
*/


        get("/ping", (req, res) -> "pong\n");
        get("/test", (req, res) -> "La aplicacion funciona correctamente");

        get("/saludar/:algo", (request, response) -> "Hola " + request.params(":algo"));

        post("/loguear", ((request, response) -> {
            String user = request.queryParams("user");
            String pass = request.queryParams("pass");
            return "logueado correctamente " + user;
        }));

        get("/hello", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            attributes.put("name", "Horacio Lopez Justiniano");

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "hello.html");
        }, new FreeMarkerEngine(configuracion));

        get("/template/:name", (request, response) -> {
            HashMap<String, Object> model = new HashMap<>();
            model.put("name", request.params(":name"));
            return new ModelAndView(model, "hello.ftl");
        }, new FreeMarkerEngine(configuracion));

        get("/index", (request, response) -> {
            HashMap<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "index.html");
        }, new FreeMarkerEngine(configuracion));

    }

}
