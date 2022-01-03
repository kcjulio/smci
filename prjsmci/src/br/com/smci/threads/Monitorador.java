/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.threads;

import br.com.smci.misc.ListaForms;
import br.com.smci.dal.ModuloConexao;
import br.com.smci.telas.TelaAlerta;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import br.com.smci.telas.TelaPrincipal;
import java.util.Scanner;
import javax.swing.DefaultListModel;
import static br.com.smci.telas.TelaPrincipal.AlertList;

/**
 * Class that is constantly monitoring the activity of fires
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class Monitorador implements Runnable {

    DefaultListModel lm1;
    //Prepara conexão com banco
    Connection conexao = null;
    //PreparedStatement e ResultSet são Frameworks do pacote java.sql
    PreparedStatement pst = null;
    ResultSet rs = null;
    String sql;

    //Prepara sample de audio usado para o alerta
    File soundFile = new File("alarm.wav");
    AudioInputStream audioIn;
    Clip clip;

    boolean existsCli = false;
    boolean existsCod = false;
    int selectedListIndex;

    ListaForms l1 = new ListaForms();

    int idsensores;
    String data_hora;
    String gps;
    float temperatura;
    float humidade;
    float gases;
    float chamas;
    float tensao_bateria;
    int intensidade;
    int idarduino;
    int idcliente;

    /**
     * Constructor method of the class
     * Initializes the AlertList with an empty model (default)
     * Initializes the audio by getting an input stream of it
     */
    public Monitorador() {
        conexao = ModuloConexao.conector();

        //Cria modelo da lista
        lm1 = new DefaultListModel();
        AlertList.setModel(lm1);

        //Inicializa audio
        try {
            audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    @Override
    /**
     * Method that overrides the default Run method
     * @since SMCI 1.0
     */
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Monitora();
                Thread.sleep(1000);     //Pausa por 1 segundo
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                JOptionPane.showMessageDialog(null, "O monitoramento parou inesperadamente. Reinicie o software. Info do Erro:\n" + ex);
            }
        }
    }

    /**
     * Method that creates a Form every time a new fire event occurs
     * @since SMCI 1.0
     */
    public void CreateForm() {
        //Oculta qualquer outro form TelaAlerta aberto
        for (int i = 0; i < l1.Size(); i++) {
            if (l1.getNode(i).obj.isShowing()) {
                l1.getNode(i).obj.setVisible(false);
            }
        }

        //Cria uma nova TelaAlerta que será exibida em seguida no JDesktop
        TelaAlerta alerta;
        alerta = new TelaAlerta(idsensores, data_hora, gps, temperatura, humidade, gases, chamas, tensao_bateria, intensidade, idarduino, idcliente);

        lm1.addElement("Id: " + idcliente + " | " + "Arduino: " + idarduino + " | " + "Level: " + intensidade);
        TelaPrincipal.AlertList.setModel(lm1);
        TelaPrincipal.AlertList.setSelectedValue(lm1.lastElement(), true);
        this.selectedListIndex = lm1.size() - 1;

        l1.Insert(alerta, true);     //Salva objeto na lista ListaForms

        alerta.setVisible(true);

        try {
            TelaPrincipal.Desktop.add(alerta);
            //Centraliza no JDesktop
            alerta.setLocation(TelaPrincipal.Desktop.getWidth() / 2 - alerta.getWidth() / 2, TelaPrincipal.Desktop.getHeight() / 2 - alerta.getHeight() / 2);
            alerta.moveToFront();
            alerta.setSelected(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    /**
     * Main method of this class which is constantly querying the sensors table looking for any warning
     * @since SMCI 1.0
     */
    public void Monitora() {

        for (int i = 0; i < l1.Size(); i++) {
            l1.getNode(i).flag = false;      //Clear all flags
        }

        //A estrutura abaixo exibe o form selecionado na lista listAlert da TelaPrincipal
        if (TelaPrincipal.AlertList.getSelectedIndex() > -1) {
            this.selectedListIndex = TelaPrincipal.AlertList.getSelectedIndex();
            selectForm(TelaPrincipal.AlertList.getSelectedValue());
        }

        if (l1.isEmpty()) {   //Se não houver alertas na lista então pare de reproduzir o audio
            if (clip.isRunning()) {
                clip.stop();
            }
        } else {
            try {
                if (!clip.isOpen()) {
                    clip.open(audioIn);
                    clip.loop(-1);
                    clip.start();
                } else {
                    clip.loop(-1);
                    clip.start();
                }
            } catch (LineUnavailableException | IOException e) {
            }
        }

        sql = "SELECT * FROM sensores WHERE intensidade > 3";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                do {
                    idsensores = rs.getInt(1);
                    data_hora = rs.getString(2);
                    gps = rs.getString(3);
                    temperatura = rs.getFloat(4);
                    humidade = rs.getFloat(5);
                    gases = rs.getFloat(6);
                    chamas = rs.getFloat(7);
                    tensao_bateria = rs.getFloat(8);
                    intensidade = rs.getInt(9);
                    idarduino = rs.getInt(10);
                    idcliente = rs.getInt(11);

                    for (int i = 0; i < l1.Size(); i++) {   //Verifica se o id do cliente já existe na lista ListaForms
                        if (l1.getNode(i).obj.idcli == this.idcliente) {
                            existsCli = true;
                        }
                    }

                    if (!existsCli) {
                        CreateForm();      //Cria Form para o alerta
                    } else {
                        //Verifica se uma TelaAlerta já está aberta para o idarduino
                        for (int i = 0; i < l1.Size(); i++) {
                            if (l1.getNode(i).obj.idcli == idcliente) {
                                if (l1.getNode(i).obj.idard == this.idarduino) {
                                    l1.getNode(i).flag = true;  //Seta flag

                                    l1.getNode(i).obj.AtualizaFormulario(gps, temperatura, humidade, gases, chamas, tensao_bateria, intensidade);    //Atualiza Form

                                    for (int j = 0; j < lm1.size(); j++) {      //Atualiza Info da listAlert
                                        Scanner sc1 = new Scanner(lm1.get(j).toString()).useDelimiter("[^\\d]+");
                                        int id = sc1.nextInt();
                                        int cod = sc1.nextInt();
                                        if (id == l1.getNode(i).obj.idcli && cod == l1.getNode(i).obj.idard) {
                                            lm1.setElementAt("Id: " + idcliente + " | " + "Arduino: " + idarduino + " | " + "Level: " + intensidade, j);
                                            TelaPrincipal.AlertList.setModel(lm1);
                                            TelaPrincipal.AlertList.setSelectedIndex(this.selectedListIndex);
                                        }
                                    }

                                    existsCod = true;
                                }
                            }
                        }

                        if (!existsCod) {   //Se não houver uma TelaAlerta para o idarduino então cria uma
                            CreateForm();
                        }
                        existsCod = false;
                    }
                    existsCli = false;
                } while (rs.next());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        //Estrutura para remover os alertas obsoletos
        for (int i = 0; i < l1.Size(); i++) {
            if (!l1.getNode(i).flag) {
                for (int j = 0; j < lm1.size(); j++) {
                    Scanner sc1 = new Scanner(lm1.get(j).toString()).useDelimiter("[^\\d]+");
                    int id = sc1.nextInt();
                    int cod = sc1.nextInt();
                    if (id == l1.getNode(i).obj.idcli && cod == l1.getNode(i).obj.idard) {
                        lm1.remove(j);
                    }
                }
                TelaPrincipal.AlertList.setModel(lm1);
                if (!lm1.isEmpty()) {
                    TelaPrincipal.AlertList.setSelectedValue(lm1.lastElement(), true);
                    this.selectedListIndex = lm1.size() - 1;
                }
                l1.getNode(i).obj.dispose();
                l1.removeNode(i);
            }
        }
    }

    /**
     * Method that selects the form according to the AlertList selection
     * @param str String containing the value of the AlertList selection
     * @since SMCI 1.0
     */
    public void selectForm(String str) {
        Scanner sc2 = new Scanner(str).useDelimiter("[^\\d]+");

        int id = sc2.nextInt();
        int cod = sc2.nextInt();

        for (int i = 0; i < l1.Size(); i++) {
            if (l1.getNode(i).obj.idcli == id) {
                if (l1.getNode(i).obj.idard == cod) {
                    l1.getNode(i).obj.setVisible(true);
                } else {
                    l1.getNode(i).obj.setVisible(false);
                }
            } else {
                l1.getNode(i).obj.setVisible(false);
            }
        }
    }
}
