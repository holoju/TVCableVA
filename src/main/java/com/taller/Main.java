package com.taller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Clase principal del proyecto
 * Created by taller on 05/08/2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class); //para logers


    public static void main(String[] args) {
        final Configuration configuracion = new Configuration(new Version("2.3.23")); //configuracion para los templates
        configuracion.setClassForTemplateLoading(Main.class,"/vistas/"); //direcciona la carpeta de vistas

        Spark.get(new Route("/") {  //la ruta es la url del navegador
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
                return writer; //se retorna el writer con todo el codigo
            }
        });



        Spark.get(new Route("/test") {
            @Override
            public Object handle(Request request, Response response) {
                return "La aplicacion funciona correctamente";
            }
        });



        Spark.get(new Route("/saludar/:algo") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hola "+request.params(":algo");
            }
        });

        Spark.post(new Route("/logear") {
            @Override
            public Object handle(Request request, Response response) {
                String user=request.queryParams("user");
                String pass = request.queryParams("pass");
                return "logueado correctamente "+user;
            }
        });
    }
}
