package com.example.reconocimientofacialuteq.Clase;

import java.io.Serializable;

public class Logearse  implements Serializable {
    String Usuario, clave;

    public Logearse(String usuario, String clave) {

        Usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
