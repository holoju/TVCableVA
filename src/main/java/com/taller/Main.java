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
   // private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        final Configuration configuracion = new Configuration(new Version("2.3.23"));
        configuracion.setClassForTemplateLoading(Main.class,"/vistas/");

        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                StringWriter writer = new StringWriter();
                try{
                    Template hola = configuracion.getTemplate("inicial.ftl");

                    Map<String,Object> datos = new HashMap<String,Object>();
                    datos.put("name","Horacio Lopez");

                    hola.process(datos,writer);

                }catch (Exception e){
                    halt(500);
                    e.printStackTrace();
                }

                return writer;
            }
        });
    }
}
