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

import static spark.Spark.*;


/**
 * Clase principal del proyecto
 * Created by taller on 05/08/2015.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class); //para logers
    public static Utilitarios util = new Utilitarios();


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


        //temporal
        Document horacio = new Document("nombre", "Horacio Lopez Justiniano")
                .append("edad", 25)
                .append("usuario", "holoju")
                .append("password", "horacio");
        //coleccion.insertOne(horacio); //inserta un usuario a la base de datos


        //GET
        //probar que esta funcionando
        get("/ping", (req, res) -> "pong\n");
        get("/test", (req, res) -> "La aplicacion funciona correctamente");
        get("/saludar/:algo", (request, response) -> "Hola " + request.params(":algo"));


        //primer pagina o inicio
        get("/", (request, response) -> {
            return new ModelAndView(null, "/pages/examples/login.html");
        }, fremarkerConfiguracion);

        get("/principal", (request, response) -> {
            if (util.estaLogueado(request)) {
                return new ModelAndView(null, "/web/index.html");
            } else {
                return new ModelAndView(null, "/pages/examples/sesionperdida.html");
            }
        }, fremarkerConfiguracion);


        //ejemplo para redireccionar
        get("/index", (request, response) -> {
            response.redirect("/");
            halt();
            return null;
        });

        //para cualquier otra cosa redireccionar a la pagina 404
        get(":otraCosa", (request, response) -> {
            //0log.error("404 pagina no encontrada!!");
            HashMap<String, Object> datos = new HashMap<>();
            datos.put("pagina", request.params(":otraCosa"));
            return new ModelAndView(datos, "/pages/examples/404.html");
        }, fremarkerConfiguracion);

        get("logout", (request, response) -> {
            request.session().removeAttribute("usuario");
            return new ModelAndView(null, "/");
        }, fremarkerConfiguracion);

        //POST
        post("/login", ((request, response) -> {
            //se recupera los datos del formulario
            String user = request.queryParams("usuario");
            String pass = request.queryParams("password");

            HashMap<String, Object> datos = new HashMap<>();
            String url = "/";

            if (user.isEmpty() || pass.isEmpty()) {
                log.debug("usuario o password son vacios");
                response.redirect("/");
            } else {
                //validar el password y el usuario con la base
                Bson filtro = new Document("usuario", user).append("password", pass);
                Document usuarioEncontrado = coleccion.find(filtro).first();


                if (usuarioEncontrado != null) {
                    printJson(usuarioEncontrado);
                    System.out.println("Usuario encontrado");
                    //redireccionar a pagina principal
                    //response.redirect("/principal");
                    datos.put("usuario", usuarioEncontrado);


                    request.session().attribute("usuario", user);

                    System.out.println("Recuperando de la sesion:: '" + (String) request.session().attribute("usuario") + "'");
                    response.redirect("/principal");

                } else {
                    System.out.println("NO SE ENCONTRO EL USUARIO");
                    //mandar mensaje de error a la pagina de login
                    datos.put("mensaje", "Usuario no encontrado");
                    url = "/";
                    response.redirect(url);
                }
            }
            return null;

        }));



    }

    public static void printJson(Document document) {
        JsonWriter jsonWriter = new JsonWriter(new StringWriter(), new JsonWriterSettings(JsonMode.SHELL, true));
        new DocumentCodec().encode(jsonWriter, document, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
        System.out.println(jsonWriter.getWriter());
        System.out.flush();
    }

}
