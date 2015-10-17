package com.taller;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Version;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
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

        // Configuracion para la carpeta de archivos estaticos es la carpeta resources
        staticFileLocation("/");

        final Configuration configuracion = new Configuration(new Version("2.3.23")); //configuracion para los templates
        configuracion.setClassForTemplateLoading(Main.class, "/"); //direcciona la carpeta de vistas

        FreeMarkerEngine fremarkerConfiguracion = new FreeMarkerEngine(configuracion);


        //configuracion para MongoDB
        //aqui puede ir MongoClientOptions para personalizar las opciones d coneccion
        MongoClient clienteBase = new MongoClient("localhost", 27017);
        MongoDatabase db = clienteBase.getDatabase("test");
        MongoCollection<Document> coleccion = db.getCollection("names");  //trae documentos
        //MongoCollection<BsonDocument> collection = db.getCollection("names",BsonDocument.class);  //trae bsondocuments que son safe type


        Document horacio = new Document("nombre", "Horacio Lopez Justiniano")
                .append("edad", 25)
                .append("usuario", "holoju")
                .append("password", "horacio");


        //coleccion.insertOne(horacio); //inserta un usuario a la base de datos


        //probar que esta funcionando
        get("/ping", (req, res) -> "pong\n");
        get("/test", (req, res) -> "La aplicacion funciona correctamente");
        get("/saludar/:algo", (request, response) -> "Hola " + request.params(":algo"));


        //primer pagina o inicio
        get("/", (request, response) -> {
            return new ModelAndView(null, "/pages/examples/login.html");
        }, fremarkerConfiguracion);


        post("/login", ((request, response) -> {
            //se recupera los datos del formulario
            String user = request.queryParams("usuario");
            String pass = request.queryParams("password");
            if (user.isEmpty() || pass.isEmpty()) {
                log.debug("usuario o password son vacios");
                response.redirect("/");
            } else {
                //validar el password y el usuario con la base
                Bson filtro = new Document("usuario", user).append("password", pass);
                Document usuarioEncontrado = coleccion.find(filtro).first();


                if (usuarioEncontrado != null) {
                    //redireccionar a la pagina principal
                    System.out.println("Usuario encontrado");
                    printJson(usuarioEncontrado);
                    //redireccionar a pagina principal
                } else {
                    //mandar mensaje de error a la pagina de login
                    System.out.println("no hay el usuario");
                    response.redirect("/");
                }

            }
            return "logueado correctamente " + user;
        }));


        //ejemplo para redireccionar
        get("/index", (request, response) -> {
            response.redirect("/");
            halt();
            return null;
        });


        //otros ejemplos, despues borrar
        get("/hello", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            attributes.put("name", "Horacio Lopez Justiniano");

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(attributes, "/pages/examples/register.html");
        }, fremarkerConfiguracion);

        get("/template/:name", (request, response) -> {
            HashMap<String, Object> model = new HashMap<>();
            model.put("name", request.params(":name"));
            return new ModelAndView(model, "hello.ftl");
        }, new FreeMarkerEngine(configuracion));


        //para cualquier otra cosa redireccionar a la pagina 404
        get(":otraCosa", (request, response) -> {
            //0log.error("404 pagina no encontrada!!");
            HashMap<String, Object> datos = new HashMap<>();
            datos.put("pagina", request.params(":otraCosa"));

            return new ModelAndView(datos, "/pages/examples/404.html");
        }, fremarkerConfiguracion);










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

    }

    private static void inesertaUsuario() {

    }


    public static void printJson(Document document) {
        JsonWriter jsonWriter = new JsonWriter(new StringWriter(), new JsonWriterSettings(JsonMode.SHELL, true));
        new DocumentCodec().encode(jsonWriter, document, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
        System.out.println(jsonWriter.getWriter());
        System.out.flush();
    }

}
