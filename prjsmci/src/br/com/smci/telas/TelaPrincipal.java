/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.telas;

import br.com.smci.threads.GetTime;
import br.com.smci.threads.Monitorador;
import javax.swing.JOptionPane;

/**
 * Main window of the system which invokes all other ones except TelaAlerta
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
public class TelaPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Desktop = new javax.swing.JDesktopPane();
        lblUsuario = new javax.swing.JLabel();
        lblData = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        AlertList = new javax.swing.JList<>();
        Menu = new javax.swing.JMenuBar();
        MenCad = new javax.swing.JMenu();
        MenCadCli = new javax.swing.JMenuItem();
        MenCadCB = new javax.swing.JMenuItem();
        MenCadUsu = new javax.swing.JMenuItem();
        MenRel = new javax.swing.JMenu();
        MenRelHist = new javax.swing.JMenuItem();
        MenAju = new javax.swing.JMenu();
        MenAjuSob = new javax.swing.JMenuItem();
        MenOpc = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        MenOpcSai = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Monitoramento Contra IncĂȘndios");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        Desktop.setPreferredSize(new java.awt.Dimension(1000, 675));

        lblUsuario.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lblUsuario.setForeground(new java.awt.Color(52, 142, 222));
        lblUsuario.setText("UsuĂĄrio");

        lblData.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 12)); // NOI18N
        lblData.setForeground(new java.awt.Color(90, 90, 90));
        lblData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Calendar.png"))); // NOI18N
        lblData.setText("Data");

        lblHora.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 12)); // NOI18N
        lblHora.setForeground(new java.awt.Color(90, 90, 90));
        lblHora.setText("Hora");

        jLabel1.setText("Em andamento:");

        jScrollPane2.setBorder(null);

        AlertList.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        AlertList.setForeground(new java.awt.Color(51, 153, 255));
        AlertList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "id cliente: 1 | psr: 2 | level: 16" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        AlertList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        AlertList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        AlertList.setOpaque(false);
        AlertList.setVisibleRowCount(1);
        jScrollPane2.setViewportView(AlertList);

        Menu.setAutoscrolls(true);

        MenCad.setText("Cadastro");

        MenCadCli.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        MenCadCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Female Client.png"))); // NOI18N
        MenCadCli.setText("Clientes");
        MenCadCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadCliActionPerformed(evt);
            }
        });
        MenCad.add(MenCadCli);

        MenCadCB.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        MenCadCB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Fire-truck.png"))); // NOI18N
        MenCadCB.setText("Corpo de Bombeiros");
        MenCadCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadCBActionPerformed(evt);
            }
        });
        MenCad.add(MenCadCB);

        MenCadUsu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        MenCadUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Users.png"))); // NOI18N
        MenCadUsu.setText("UsuĂĄrios");
        MenCadUsu.setEnabled(false);
        MenCadUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenCadUsuActionPerformed(evt);
            }
        });
        MenCad.add(MenCadUsu);

        Menu.add(MenCad);

        MenRel.setText("RelatĂłrio");
        MenRel.setEnabled(false);

        MenRelHist.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        MenRelHist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/report.png"))); // NOI18N
        MenRelHist.setText("Historico de Sinistros");
        MenRelHist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenRelHistActionPerformed(evt);
            }
        });
        MenRel.add(MenRelHist);

        Menu.add(MenRel);

        MenAju.setText("Ajuda");

        MenAjuSob.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.ALT_MASK));
        MenAjuSob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Info.png"))); // NOI18N
        MenAjuSob.setText("Sobre");
        MenAjuSob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenAjuSobActionPerformed(evt);
            }
        });
        MenAju.add(MenAjuSob);

        Menu.add(MenAju);

        MenOpc.setText("OpĂ§Ă”es");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Logout.png"))); // NOI18N
        jMenuItem1.setText("Logout");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        MenOpc.add(jMenuItem1);

        MenOpcSai.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        MenOpcSai.setText("Sair");
        MenOpcSai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenOpcSaiActionPerformed(evt);
            }
        });
        MenOpc.add(MenOpcSai);

        Menu.add(MenOpc);

        setJMenuBar(Menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Desktop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsuario)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHora))
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblUsuario)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblData)
                    .addComponent(lblHora))
                .addGap(50, 50, 50)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Desktop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1296, 735));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that opens the TelaBombeiros window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenCadCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadCBActionPerformed
        // Chama a telaOs
        TelaBombeiros cb = new TelaBombeiros();
        cb.setVisible(true);

        try {
            if (cb.isVisible()) {
                Desktop.add(cb);
                cb.moveToFront();
                cb.setSelected(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_MenCadCBActionPerformed

    /**
     * Method that exits the system
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenOpcSaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenOpcSaiActionPerformed
        int sair;

        if (AlertList.getModel().getSize() == 0) {
            sair = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "AtenĂ§ĂŁo", JOptionPane.YES_NO_OPTION);
        } else {
            sair = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair? HĂĄ Alertas neste momento.", "AtenĂ§ĂŁo", JOptionPane.YES_NO_OPTION);
        }

        if (sair == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_MenOpcSaiActionPerformed

    /**
     * Method that opens the TelaSobre window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenAjuSobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenAjuSobActionPerformed
        // Exibe a tela Sobre
        TelaSobre sobre = new TelaSobre();
        sobre.setVisible(true);
    }//GEN-LAST:event_MenAjuSobActionPerformed

    /**
     * Method that opens the TelaUsuario window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenCadUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadUsuActionPerformed
        //As linhas abaixo abrem o form TelaUsuario dentro do desktop pane
        TelaUsuario usuario = new TelaUsuario();
        usuario.setVisible(true);

        try {
            if (usuario.isVisible()) {
                Desktop.add(usuario);
                usuario.moveToFront();
                usuario.setSelected(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_MenCadUsuActionPerformed

    /**
     * Method that opens the TelaCliente window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenCadCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenCadCliActionPerformed
        // Chamando a Tela Cliente
        TelaCliente cliente = new TelaCliente();
        cliente.setVisible(true);

        try {
            if (cliente.isVisible()) {
                Desktop.add(cliente);
                cliente.moveToFront();
                cliente.setSelected(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_MenCadCliActionPerformed

    /**
     * Method that logouts from the system
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Fecha a tela principal (logout)
        this.dispose();
        TelaLogin login = new TelaLogin();
        login.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * Method that initializes the atualizaHora and monitora Threads
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

        //Inicia uma Thread que atualiza constantemento a data e hora
        Thread atualizaHora = new Thread(new GetTime(), "T1");
        atualizaHora.setPriority(Thread.MIN_PRIORITY);
        atualizaHora.start();
        
        //Inicia Thread que monitora os Arduinos
        Thread monitora = new Thread(new Monitorador(), "T2");
        monitora.setPriority(Thread.MAX_PRIORITY);
        monitora.start();
    }//GEN-LAST:event_formWindowOpened

    /**
     * Method that opens the TelaHistorico window
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void MenRelHistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenRelHistActionPerformed
        // Chamando a Tela Relatorio
        TelaHistorico historico = new TelaHistorico();
        historico.setVisible(true);

        try {
            if (historico.isVisible()) {
                Desktop.add(historico);
                historico.moveToFront();
                historico.setSelected(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_MenRelHistActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JList<String> AlertList;
    public static javax.swing.JDesktopPane Desktop;
    private javax.swing.JMenu MenAju;
    private javax.swing.JMenuItem MenAjuSob;
    private javax.swing.JMenu MenCad;
    private javax.swing.JMenuItem MenCadCB;
    private javax.swing.JMenuItem MenCadCli;
    public static javax.swing.JMenuItem MenCadUsu;
    private javax.swing.JMenu MenOpc;
    private javax.swing.JMenuItem MenOpcSai;
    public static javax.swing.JMenu MenRel;
    private javax.swing.JMenuItem MenRelHist;
    private javax.swing.JMenuBar Menu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JLabel lblData;
    public static javax.swing.JLabel lblHora;
    public static javax.swing.JLabel lblUsuario;
    // End of variables declaration//GEN-END:variables
}
