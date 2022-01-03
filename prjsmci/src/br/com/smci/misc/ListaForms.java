/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.misc;

import br.com.smci.telas.TelaAlerta;

/**
 * Class that implements a custom linked list
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class ListaForms {

    private Nodo inicio;
    private Nodo ultimo;

    /**
     * Constructor method of the class
     * @since SMCI 1.0
     */
    public void Lista() {
        inicio = null;
        ultimo = null;
    }

    /**
     * Method that adds a form object to the list
     * @param form TelaAlerta to add in the list
     * @param f Boolean that indicates whether the Alert is obsolete or not
     * @since SMCI 1.0
     */
    public void Insert(TelaAlerta form, boolean f) {
        Nodo n = new Nodo();
        n.flag = f;
        n.obj = form;
        n.prox = null;
        n.ant = ultimo;

        if (isEmpty()) {
            inicio = n;
            ultimo = n;
        } else {
            ultimo.prox = n;
            ultimo = n;
        }
    }

    /**
     * Method that checks if the list is empty
     * @return Boolean true if the list is empty
     * @since SMCI 1.0
     */
    public boolean isEmpty() {
        return inicio == null && ultimo == null;
    }

    /**
     * Method that get a specific Node from the list
     * @param position Integer of the Nth object in the list
     * @return Nodo in the Nth position
     * @since SMCI 1.0
     */
    public Nodo getNode(int position) {
        Nodo n = inicio;

        for (int i = 0; i < position; i++) {
            n = n.prox;
        }
        
        return n;
    }

    /**
     * Method that returns the number of elements in the list 
     * @return Integer representing the size of the list
     * @since SMCI 1.0
     */
    public int Size() {
        Nodo n = inicio;
        int cont = 0;
        
        while (n != null) {
            cont++;
            n = n.prox;
        }
        
        return cont;
    }
    
    /**
     * Method that removes a specic element from the list
     * @param position Integer of the Nth object in the list to be removed
     * Integer of the Nth object in the list
     */
    public void removeNode(int position) {
        Nodo n = inicio;

        for (int i = 0; i < position; i++) {
            n = n.prox;
        }
        
        if (n.prox != null) {
            n.prox.ant = n.ant;
        }
        
        if (n.ant != null) {
            n.ant.prox = n.prox;
        }
        
        if (n.ant == null && n.prox == null) {
            inicio = null;
            ultimo = null;
        }
    }
}
