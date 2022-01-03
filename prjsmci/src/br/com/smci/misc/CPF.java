/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.misc;

/**
 * Class that validates a CPF number
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class CPF {

    String cpf;
    int digits[] = new int[11];
    int dv1 = 0;
    int dv2 = 0;

    /**
     * Constructor method of the class
     * @param numero String number of the CPF
     * @since SMCI 1.0
     */
    public CPF(String numero) {
        cpf = numero.replaceAll("[^0-9]", "");

        if (cpf.length() == 11) {
            for (int i = 0; i < 9; i++) {
                digits[i] = Character.getNumericValue(cpf.charAt(i));
            }
        }
    }
    
    /**
     * Method that validates a CPF number
     * @return Boolean true number is valid false otherwise
     * @since SMCI 1.0
     */
    public boolean validaCPF() {

        if (cpf.length() == 11) {
            for (int i = 1; i < 10; i++) {
                dv1 = dv1 + digits[i - 1] * i;
            }

            dv1 = dv1 % 11;

            if (dv1 == 10) {
                dv1 = 0;
            }

            digits[9] = dv1;

            for (int i = 0; i < 10; i++) {
                dv2 = dv2 + digits[i] * i;
            }

            dv2 = dv2 % 11;

            if (dv2 == 10) {
                dv2 = 0;
            }

            digits[10] = dv2;

            return (Character.getNumericValue(cpf.charAt(9)) == dv1 && Character.getNumericValue(cpf.charAt(10)) == dv2);
        }
        return false;
    }
}
