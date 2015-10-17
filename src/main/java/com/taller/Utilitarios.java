package com.taller;

import spark.Request;

/**
 * Created by taller on 17/10/2015.
 */
public class Utilitarios {
    public boolean estaLogueado(Request request) {
        String usuarioLogueado = (String) request.session().attribute("usuario");
        System.out.println("Llega estaLogueado: " + usuarioLogueado);
        return usuarioLogueado != null;//return false;
    }
}
