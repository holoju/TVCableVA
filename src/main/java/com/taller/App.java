package com.taller;


import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class App {
    public static void main( String[] args ) {
        System.out.println( "Hola OTTY" );
		System.out.println("esta sera la forma de trabajar");

        Route ruta = new Route("/") {
            public Object handle(Request request, Response response){
                return "Hola mundo desde spark";
            }
        };

        Spark.get(ruta);

    }


}
