package com.m2brcorp.geostatus.Core;


import com.parse.ParseUser;

public abstract class ParseAuthUtils  {

    public static ParseUser obterUsuarioLogado() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser;
        }
        return null;
    }

    public static void deslogarUsuario() {
        ParseUser.logOut();
    }



}
