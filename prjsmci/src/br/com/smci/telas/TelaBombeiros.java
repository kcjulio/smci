/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.telas;

/**
 * Form that manages the firefighters records by allowing CRUD operations
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
import java.sql.*;
import br.com.smci.dal.ModuloConexao;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
//A linha abaixo importa recursos da biblioteca rs2xml.jar
import net.proteanit.sql.DbUtils;

public class TelaBombeiros extends javax.swing.JInternalFrame {

    Connection conexao = null;
    //PreparedStatement e ResultSet são Frameworks do pacote java.sql
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaBombeiros
     */
    public TelaBombeiros() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    /**
     * Method used to add a new record in the bombeiros table
     * @since SMCI 1.0
     */
    private void Adicionar() {
        String sql = "INSERT INTO bombeiros (nome, fone, email) VALUES (?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCBNome.getText());
            pst.setString(2, txtCBFone.getText());
            pst.setString(3, txtCBEmail.getText());
            //Validação dos campos obrigatórios
            if (txtCBNome.getText().isEmpty() || txtCBFone.getText().isEmpty() || txtCBCidade.getText().isEmpty() || txtCBUF.getText().isEmpty()
                    || txtCBBairro.getText().isEmpty() || txtCBRua.getText().isEmpty() || txtCBNum.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos campos obrigatórios");
            } else {
                //A linha abaixo atualiza a tabela bombeiros
                pst.executeUpdate();

                //Após inserir dados na tabela bombeiros com sucesso, insere dados na tabela endereco
                String lastID = lastAdded();
                sql = "INSERT INTO endereco_bombeiros (cidade, uf, bairro, rua, numero, apto, complemento, idbombeiros) VALUES (?,?,?,?,?,?,?,?)";

                if (txtCBApto.getText().isEmpty()) {
                    txtCBApto.setText("0");
                }

                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtCBCidade.getText());
                pst.setString(2, txtCBUF.getText());
                pst.setString(3, txtCBBairro.getText());
                pst.setString(4, txtCBRua.getText());
                pst.setString(5, txtCBNum.getText());
                pst.setString(6, txtCBApto.getText());
                pst.setString(7, txtCBComp.getText());
                pst.setString(8, lastID);

                //Validação dos campos obrigatórios
                if (lastID.equals("")) {
                    JOptionPane.showMessageDialog(null, "Falha ao cadastrar novo corpo de bombeiros");
                } else {
                    //A linha abaixo atualiza a tabela endereco_bombeiros
                    int adicionado = pst.executeUpdate();
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Corpo de Bombeiros cadastrado com sucesso!");
                        limpaCampos();
                    }
                }

            }
        } catch (SQLException | HeadlessException f) {
            JOptionPane.showMessageDialog(null, f);
        }
    }

    /**
     * Method that searches the bombeiros table for a match with txtCliPesquisar field
     * @since SMCI 1.0
     */
    private void Consultar() {
        String sql = "SELECT * FROM bombeiros WHERE nome LIKE ?";
        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteúdo da caixa de pesquisa para o ?
            //Detalhe para a "%" que vai ao final do comando
            pst.setString(1, "%" + txtCBPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml para preencher a tabela tblCB
            tblCB.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Method that fill all the fields of the form when the user click on a tblCB row
     * @since SMCI 1.0
     */
    private void preencheCampos() {
        int selectedrow = tblCB.getSelectedRow();

        String sql = "SELECT *\n"
                + "FROM bombeiros T1\n"
                + "INNER JOIN endereco_bombeiros T2 ON T2.idbombeiros = T1.idbombeiros\n"
                + "WHERE T1.idbombeiros = ?";
        try {
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, tblCB.getModel().getValueAt(selectedrow, 0).toString());
            
            rs = pst.executeQuery();
            if (rs.next()) {
                txtCBID.setText(rs.getString(1));
                txtCBNome.setText(rs.getString(2));
                txtCBFone.setText(rs.getString(3));
                txtCBEmail.setText(rs.getString(4));
                txtCBDataCadastro.setText(rs.getString(5));
                txtCBCidade.setText(rs.getString(7));
                txtCBUF.setText(rs.getString(8));
                txtCBBairro.setText(rs.getString(9));
                txtCBRua.setText(rs.getString(10));
                txtCBNum.setText(rs.getString(11));
                txtCBApto.setText(rs.getString(12));
                txtCBComp.setText(rs.getString(13));
                btnCBCreate.setEnabled(false);
                btnCBCancela.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Method used to get the latest record in the bombeiros table
     * @return String containing the ID
     * @since SMCI 1.0
     */
    private String lastAdded() {
        String sql = "SELECT * FROM bombeiros ORDER BY idbombeiros DESC LIMIT 1";
        String lastID = "";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                lastID = rs.getString(1);
            }
            return lastID;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return lastID;
        }
    }

    /**
     * Method used to update a firefighter record
     * @since SMCI 1.0
     */
    private void Alterar() {
        String sql = "UPDATE bombeiros SET nome = ?, fone = ?, email = ? WHERE idbombeiros = ?";
        try {
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtCBNome.getText());
            pst.setString(2, txtCBFone.getText());
            pst.setString(3, txtCBEmail.getText());
            pst.setString(4, txtCBID.getText());
            
            //Validação dos campos obrigatórios
            if (txtCBNome.getText().isEmpty() || txtCBFone.getText().isEmpty() || txtCBCidade.getText().isEmpty() || txtCBUF.getText().isEmpty()
                    || txtCBBairro.getText().isEmpty() || txtCBRua.getText().isEmpty() || txtCBNum.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos campos obrigatórios");
            } else {
                //A linha abaixo atualiza os dados do corpo de bombeiros
                pst.executeUpdate();

                //Após atualizar dados na tabela tbcb com sucesso, atualiza a tabela endereços
                sql = "UPDATE endereco_bombeiros SET cidade = ?, uf = ?, bairro = ?, rua = ?, numero = ?, apto = ?, complemento = ? WHERE idbombeiros = ?";
                
                if (txtCBApto.getText().isEmpty()) {
                    txtCBApto.setText("0");
                }

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCBCidade.getText());
                pst.setString(2, txtCBUF.getText());
                pst.setString(3, txtCBBairro.getText());
                pst.setString(4, txtCBRua.getText());
                pst.setString(5, txtCBNum.getText());
                pst.setString(6, txtCBApto.getText());
                pst.setString(7, txtCBComp.getText());
                pst.setString(8, txtCBID.getText());

                //A linha abaixo executa o update na tabela
                int alterado = pst.executeUpdate();
                
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do corpo de bombeiros atualizados com sucesso!");
                    limpaCampos();
                }
            }
        } catch (SQLException | HeadlessException f) {
            JOptionPane.showMessageDialog(null, f);
        }
    }

    /**
     * Method used to remove a firefighter record
     * @since SMCI 1.0
     */
    private void Remover() {
        //A estrutura abaixo confirma a remoção do corpo de bombeiros
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este cadastro?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            //Exclui primeiramente os dados da tabela que contém chave estrangeira de tbcb
            String sql = "DELETE FROM endereco_bombeiros WHERE idbombeiros = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCBID.getText());

                pst.executeUpdate();    //Executa delete

                //Após deletar os dados da tabela endereco_bombeiros deleta as informacoes da tabela bombeiros
                sql = "DELETE FROM bombeiros WHERE idbombeiros = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCBID.getText());

                int deletado = pst.executeUpdate();
                if (deletado > 0) {
                    JOptionPane.showMessageDialog(null, "Cadastro removido com sucesso!");
                    limpaCampos();
                }
            } catch (SQLException | HeadlessException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    /**
     * Method that clears all the form's fields
     * @since SMCI 1.0
     */
    private void limpaCampos() {
        txtCBID.setText(null);
        txtCBNome.setText(null);
        txtCBFone.setText(null);
        txtCBEmail.setText(null);
        txtCBDataCadastro.setText(null);
        txtCBCidade.setText(null);
        txtCBUF.setText(null);
        txtCBBairro.setText(null);
        txtCBRua.setText(null);
        txtCBNum.setText(null);
        txtCBApto.setText(null);
        txtCBComp.setText(null);
        btnCBCreate.setEnabled(true);
        btnCBCancela.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnCBCreate = new javax.swing.JButton();
        btnCBUpdate = new javax.swing.JButton();
        btnCBDelete = new javax.swing.JButton();
        txtCBNome = new javax.swing.JTextField();
        txtCBFone = new javax.swing.JTextField();
        txtCBEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCBPesquisar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCB = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtCBID = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtCBDataCadastro = new javax.swing.JTextField();
        btnCBCancela = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCBCidade = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtCBBairro = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtCBComp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtCBUF = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCBRua = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtCBNum = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCBApto = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Corpo de Bombeiros");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Firefighter.png"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 675));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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

        jLabel1.setText("*Nome:");

        jLabel3.setText("*Telefone:");

        jLabel4.setText("Email:");

        btnCBCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Create.png"))); // NOI18N
        btnCBCreate.setToolTipText("Adicionar");
        btnCBCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCBCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCBCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCBCreateActionPerformed(evt);
            }
        });

        btnCBUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Update.png"))); // NOI18N
        btnCBUpdate.setToolTipText("Alterar");
        btnCBUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCBUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCBUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCBUpdateActionPerformed(evt);
            }
        });

        btnCBDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Delete.png"))); // NOI18N
        btnCBDelete.setToolTipText("Excluir");
        btnCBDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCBDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCBDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCBDeleteActionPerformed(evt);
            }
        });

        jLabel7.setText("* Campos obrigatórios");

        txtCBPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCBPesquisarKeyReleased(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Search.png"))); // NOI18N

        tblCB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblCB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCBMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCB);

        jLabel5.setText("ID:");

        txtCBID.setEditable(false);

        jLabel10.setText("Data Inscrição:");

        txtCBDataCadastro.setEditable(false);

        btnCBCancela.setText("Cancela");
        btnCBCancela.setEnabled(false);
        btnCBCancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCBCancelaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCBPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCBNome)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtCBID, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtCBEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel10))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtCBFone, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCBDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(btnCBCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(btnCBUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(btnCBDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(84, 84, 84)
                                        .addComponent(btnCBCancela)))))
                        .addGap(0, 99, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCBPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCBID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCBNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCBFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCBEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtCBDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnCBCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCBUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCBDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCBCancela, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(32, 32, 32)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Corpo de Bombeiros", jPanel1);

        jLabel2.setText("*Cidade:");

        jLabel11.setText("*Bairro:");

        jLabel12.setText("Complemento:");

        jLabel13.setText("*UF:");

        txtCBUF.setToolTipText("");

        jLabel14.setText("*Rua:");

        jLabel15.setText("*N°:");

        jLabel16.setText("Apto:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel2)
                    .addComponent(jLabel14)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCBUF, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(154, 154, 154)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCBNum, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCBApto, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCBBairro)
                    .addComponent(txtCBCidade)
                    .addComponent(txtCBRua)
                    .addComponent(txtCBComp, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCBCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCBBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtCBRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCBComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCBUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtCBNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtCBApto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(194, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Endereço", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        setBounds(0, 0, 1000, 675);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCBDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCBDeleteActionPerformed
        // Chamando o método remover
        if (txtCBID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione um corpo de bombeiros!");
        } else {
            Remover();
        }
    }//GEN-LAST:event_btnCBDeleteActionPerformed

    private void btnCBUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCBUpdateActionPerformed
        // Chamando o método alterar
        Alterar();
    }//GEN-LAST:event_btnCBUpdateActionPerformed

    private void btnCBCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCBCreateActionPerformed
        // Chamando o método adicionar
        Adicionar();

    }//GEN-LAST:event_btnCBCreateActionPerformed

    private void txtCBPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCBPesquisarKeyReleased
        // Chamando o método Consultar
        if (!txtCBPesquisar.getText().isEmpty()) {
            Consultar();
        }
    }//GEN-LAST:event_txtCBPesquisarKeyReleased

    //Evento que será usado para setar os campos da tabela (On mouse click)
    private void tblCBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCBMouseClicked
        // Chamando o método preecheCampos
        preencheCampos();
    }//GEN-LAST:event_tblCBMouseClicked

    private void btnCBCancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCBCancelaActionPerformed
        // TODO add your handling code here:
        btnCBCreate.setEnabled(true);
        btnCBCancela.setEnabled(false);
        limpaCampos();
    }//GEN-LAST:event_btnCBCancelaActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        try {
            // Fecha conexão aberta por este form com o banco
            conexao.close();
        } catch (SQLException ex) {
            Logger.getLogger(TelaUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCBCancela;
    private javax.swing.JButton btnCBCreate;
    private javax.swing.JButton btnCBDelete;
    private javax.swing.JButton btnCBUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblCB;
    private javax.swing.JTextField txtCBApto;
    private javax.swing.JTextField txtCBBairro;
    private javax.swing.JTextField txtCBCidade;
    private javax.swing.JTextField txtCBComp;
    private javax.swing.JTextField txtCBDataCadastro;
    private javax.swing.JTextField txtCBEmail;
    private javax.swing.JTextField txtCBFone;
    private javax.swing.JTextField txtCBID;
    private javax.swing.JTextField txtCBNome;
    private javax.swing.JTextField txtCBNum;
    private javax.swing.JTextField txtCBPesquisar;
    private javax.swing.JTextField txtCBRua;
    private javax.swing.JTextField txtCBUF;
    // End of variables declaration//GEN-END:variables
}
