/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.telas;

import br.com.smci.dal.ModuloConexao;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Form that shows up when a fire starts and provides useful information to do the right procedures
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class TelaAlerta extends javax.swing.JInternalFrame {

    Connection conexao = null;
    //PreparedStatement e ResultSet são Frameworks do pacote java.sql
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public int idcli;
    public int idard;

    /**
     * Constructor method of the class
     * @param idsensores Integer id of the sensor node
     * @param data_hora String wich contais the date and time of the occurrence
     * @param gps String with the GPS information
     * @param temperatura Float indication of the temperature in graus Celsius
     * @param humidade Float indicating the humidity percentage
     * @param gases Float indicating the gas level in the ambient
     * @param chamas Float indicating the flame level in the ambient
     * @param tensao_bateria Float indicating the remaining battery voltage of the Arduino
     * @param intensidade Integer witch gives an ideia of the situation seriousness
     * @param idarduino Integer id of the Arduino
     * @param idcliente Integer id of the client
     * @since SMCI 1.0
     */
    public TelaAlerta(int idsensores, String data_hora, String gps, float temperatura, float humidade, float gases, float chamas, float tensao_bateria, int intensidade,
            int idarduino, int idcliente) {

        initComponents();

        conexao = ModuloConexao.conector();

        idcli = idcliente;
        idard = idarduino;

        txtIdCli.setText(String.valueOf(idcliente));
        txtIdArd.setText(String.valueOf(idarduino));
        txtInten.setText(String.valueOf(intensidade));
        txtDataHr.setText(data_hora);
        txtGps.setText(gps);
        txtBat.setText(String.valueOf(tensao_bateria) + " V");
        txtTemp.setText(String.valueOf(temperatura) + "°C");
        txtHumid.setText(String.valueOf(humidade) + " %");
        txtGas.setText(String.valueOf(gases));
        txtFlam.setText(String.valueOf(chamas));

        fetchData();
    }

    /**
     * Method that updates the history table
     * @since SMCI 1.0
     */
    private void AtualizaHistorico() {
        // Salva o alerta na tabela historico se não houver registro naquela hora
        String sql = "SELECT * FROM historico WHERE data_hora = ? AND idarduino = ? AND idcliente = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtDataHr.getText());
            pst.setString(2, txtIdArd.getText());
            pst.setString(3, txtIdCli.getText());

            rs = pst.executeQuery();

            if (!rs.next()) {   //Se não houver registro de alerta na data e hora recebida para o dado arduino e cliente especificados então adiciona linha no historico
                sql = "INSERT INTO historico (data_hora, gps, temperatura, humidade, gases, chamas, tensao_bateria, intensidade, idarduino, idcliente) VALUES (?,?,?,?,?,?,?,?,?,?)";

                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtDataHr.getText());
                pst.setString(2, txtGps.getText());
                pst.setString(3, txtTemp.getText().replaceAll("[^0-9?!\\.]", ""));
                pst.setString(4, txtHumid.getText().replaceAll("[^0-9?!\\.]", ""));
                pst.setString(5, txtGas.getText());
                pst.setString(6, txtFlam.getText());
                pst.setString(7, txtBat.getText().replaceAll("[^0-9?!\\.]", ""));
                pst.setString(8, txtInten.getText());
                pst.setString(9, txtIdArd.getText());
                pst.setString(10, txtIdCli.getText());

                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Method that fetches data of the client and firefighters when a fire event is happening
     * @since SMCI 1.0
     */
    private void fetchData() {
        String sql = "SELECT c.*, e.*, GROUP_CONCAT(t.fone SEPARATOR ', ') AS telefones\n" +
                    "FROM clientes c\n" +
                    "INNER JOIN endereco e ON e.idcliente = c.idcliente\n" +
                    "INNER JOIN telefones t ON t.idcliente = c.idcliente\n" +
                    "WHERE c.idcliente = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtIdCli.getText());

            rs = pst.executeQuery();

            if (rs.next()) {
                txtNomeCli.setText(rs.getString(2));
                txtCidade.setText(rs.getString(7));
                txtUF.setText(rs.getString(8));
                txtBairro.setText(rs.getString(9));
                txtRua.setText(rs.getString(10));
                txtNum.setText(rs.getString(11));
                txtApto.setText(rs.getString(12));
                txtComp.setText(rs.getString(13));
                txtFoneCli.setText(rs.getString(15));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        sql = "SELECT * FROM arduino WHERE idcliente = ? AND idarduino = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtIdCli.getText());
            pst.setString(2, txtIdArd.getText());

            rs = pst.executeQuery();

            if (rs.next()) {
                txtLocation.setText(rs.getString(2));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        sql = "SELECT * FROM bombeiros, endereco_bombeiros \n"
                + "WHERE bombeiros.idbombeiros = endereco_bombeiros.idbombeiros AND cidade = ?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtCidade.getText());

            rs = pst.executeQuery();

            if (rs.next()) {
                txtNomeCb.setText(rs.getString(2));
                txtFoneCb.setText(rs.getString(3));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    /**
     * Method that updates the fields in the form
     * @param gps String with the GPS information
     * @param temperatura Float indication of the temperature in graus Celsius
     * @param humidade Float indicating the humidity percentage
     * @param gases Float indicating the gas level in the ambient
     * @param chamas Float indicating the flame level in the ambient
     * @param tensao_bateria Float indicating the remaining battery voltage of the Arduino
     * @param intensidade Integer witch gives an ideia of the situation seriousness
     * @since SMCI 1.0
     */
    public void AtualizaFormulario(String gps, float temperatura, float humidade, float gases, float chamas, float tensao_bateria, int intensidade) {
        txtGps.setText(gps);
        txtTemp.setText(String.valueOf(temperatura) + "°C");
        txtHumid.setText(String.valueOf(humidade) + " %");
        txtGas.setText(String.valueOf(gases));
        txtFlam.setText(String.valueOf(chamas));
        txtBat.setText(String.valueOf(tensao_bateria) + " V");
        txtInten.setText(String.valueOf(intensidade));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNomeCli = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtFoneCli = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCidade = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtIdCli = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtRua = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtNum = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtApto = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtComp = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtIdArd = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtInten = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtDataHr = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtGps = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtBat = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtTemp = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtHumid = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtGas = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtFlam = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtNomeCb = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtFoneCb = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jLabel8.setText("jLabel8");

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Alerta");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nome do cliente:");

        txtNomeCli.setEditable(false);

        jLabel2.setText("Telefones:");

        txtFoneCli.setEditable(false);

        jLabel3.setText("Cidade:");

        txtCidade.setEditable(false);

        jLabel4.setText("ID:");

        txtIdCli.setEditable(false);

        jLabel5.setText("UF:");

        txtUF.setEditable(false);

        jLabel6.setText("Bairro:");

        txtBairro.setEditable(false);

        jLabel7.setText("Rua:");

        txtRua.setEditable(false);

        jLabel9.setText("N°:");

        txtNum.setEditable(false);

        jLabel10.setText("Apto:");

        txtApto.setEditable(false);

        jLabel11.setText("Complemento:");

        txtComp.setEditable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Info:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel12.setText("Arduino:");

        txtIdArd.setEditable(false);

        jLabel13.setText("Intensidade:");

        txtInten.setEditable(false);

        jLabel14.setText("Data e Hora:");

        txtDataHr.setEditable(false);

        jLabel15.setText("GPS:");

        txtGps.setEditable(false);

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Battery.png"))); // NOI18N
        jLabel16.setText("Tensão da Bateria:");

        txtBat.setEditable(false);

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Termometro.png"))); // NOI18N
        jLabel17.setText("Temperatura:");

        txtTemp.setEditable(false);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/drop.png"))); // NOI18N
        jLabel18.setText("Humidade:");

        txtHumid.setEditable(false);

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/co2.png"))); // NOI18N
        jLabel19.setText("Nível de Gás:");

        txtGas.setEditable(false);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Chama.png"))); // NOI18N
        jLabel20.setText("Nível de Chamas:");

        txtFlam.setEditable(false);

        jLabel23.setText("Localização:");

        txtLocation.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel15)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel23)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGps, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtIdArd, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel13)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtHumid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(236, 236, 236)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtInten)
                            .addComponent(txtBat)
                            .addComponent(txtGas, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)))
                    .addComponent(txtLocation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDataHr, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFlam, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txtTemp))
                        .addGap(110, 110, 110))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtIdArd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtInten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtDataHr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtGps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtBat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtHumid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtGas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtFlam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel21.setText("Nome do Corpo de Bombeiros:");

        txtNomeCb.setEditable(false);

        jLabel22.setText("Telefone:");

        txtFoneCb.setEditable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/printer.png"))); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNomeCli)
                            .addComponent(txtCidade)
                            .addComponent(txtRua)
                            .addComponent(txtComp, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                        .addGap(103, 103, 103)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtIdCli)
                                    .addComponent(txtUF, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNum)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel10)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBairro)
                            .addComponent(txtApto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFoneCli, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNomeCb, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFoneCb, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNomeCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtFoneCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtIdCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtApto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtNomeCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtFoneCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBounds(0, 0, 1000, 675);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        AtualizaHistorico();

    }//GEN-LAST:event_formInternalFrameActivated

    /**
     * Method that is called when the print button is pressed and opens a report window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        double gpslat = 0;
        double gpslon = 0;

        Scanner sc1 = new Scanner(txtGps.getText()).useDelimiter(",\\s");

        try {
            gpslat = Double.parseDouble(sc1.next());
            gpslon = Double.parseDouble(sc1.next());
        } catch (Exception e) {
        }

        Map parameters = new HashMap();

        parameters.put("nomecli", txtNomeCli.getText());
        parameters.put("fonecli", txtFoneCli.getText());
        parameters.put("cidade", txtCidade.getText());
        parameters.put("uf", txtUF.getText());
        parameters.put("bairro", txtBairro.getText());
        parameters.put("rua", txtRua.getText());
        parameters.put("numero", txtNum.getText());
        parameters.put("apto", txtApto.getText());
        parameters.put("comp", txtComp.getText());
        parameters.put("arduino", txtIdArd.getText());
        parameters.put("level", txtInten.getText());
        parameters.put("data_hora", txtDataHr.getText());
        parameters.put("gpslat", gpslat);
        parameters.put("gpslon", gpslon);
        parameters.put("bat", txtBat.getText());
        parameters.put("temp", txtTemp.getText());
        parameters.put("humid", txtHumid.getText());
        parameters.put("loc", txtLocation.getText());
        parameters.put("gas", txtGas.getText());
        parameters.put("flam", txtFlam.getText());
        parameters.put("nomecb", txtNomeCb.getText());
        parameters.put("fonecb", txtFoneCb.getText());

        try {
            JasperPrint print = JasperFillManager.fillReport("src/br/com/smci/reports/alertreport.jasper", parameters, conexao);

            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Uma falha ocorreu ao renderizar o relatorio. Error:\n" + ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtApto;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtBat;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtComp;
    private javax.swing.JTextField txtDataHr;
    private javax.swing.JTextField txtFlam;
    private javax.swing.JTextField txtFoneCb;
    private javax.swing.JTextField txtFoneCli;
    private javax.swing.JTextField txtGas;
    private javax.swing.JTextField txtGps;
    private javax.swing.JTextField txtHumid;
    private javax.swing.JTextField txtIdArd;
    private javax.swing.JTextField txtIdCli;
    private javax.swing.JTextField txtInten;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtNomeCb;
    private javax.swing.JTextField txtNomeCli;
    private javax.swing.JTextField txtNum;
    private javax.swing.JTextField txtRua;
    private javax.swing.JTextField txtTemp;
    private javax.swing.JTextField txtUF;
    // End of variables declaration//GEN-END:variables
}
