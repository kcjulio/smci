/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.threads;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import br.com.smci.telas.TelaPrincipal;

/**
 * Class that implements a Runnable method and is able to get the date and time of the host system every second
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class GetTime implements Runnable {

    @Override
    /**
     * Method that overrides the default Run method
     * @since SMCI 1.0
     */
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SetaData();
                SetaHora();
                Thread.sleep(1 * 1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** 
     * Method that sets the lblData Label to the current system Date
     * @since SMCI 1.0
     */
    public void SetaData() {
        // As linhas abaixo substituem a label lblData pela data atual do sistema
        Date data = new Date();
        DateFormat formatador = DateFormat.getDateInstance(DateFormat.SHORT);
        TelaPrincipal.lblData.setText(formatador.format(data));
    }

    /**
     * Method that sets the lblHora Label to the current system time
     * @since SMCI 1.0
     */
    public void SetaHora() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String hora;
        hora = dateFormat.format(date);
        TelaPrincipal.lblHora.setText(hora);
    }
}
