/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.smci.telas;

/**
 * Form that manages the clients records by allowing CRUD operations
 * @author julio
 * @version 1.0
 * @since SMCI 1.0
 */
import java.sql.*;
import br.com.smci.dal.ModuloConexao;
import br.com.smci.misc.CPF;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
//A linha abaixo importa recursos da biblioteca rs2xml.jar
import net.proteanit.sql.DbUtils;

public class TelaCliente extends javax.swing.JInternalFrame {

    Connection conexao = null;
    //PreparedStatement e ResultSet são Frameworks do pacote java.sql
    PreparedStatement pst = null;
    ResultSet rs = null;
    DefaultTableModel dtm1;
    DefaultTableModel dtm2;

    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    /**
     * Method used to add a new record in the clients table
     * @return Boolean true if the client was successfully added
     * @since SMCI 1.0
     */
    private boolean Adicionar() {
        String sql = "INSERT INTO clientes (nome, data_nascimento, cpf, email) VALUES (?,?,?,?)";
        try {
            //Validação dos campos obrigatórios
            if (txtCliNome.getText().isEmpty() || txtCliCPF.getText().isEmpty() || txtCliFone.getText().isEmpty()
                    || txtCliCidade.getText().isEmpty() || txtCliUF.getText().isEmpty() || txtCliBairro.getText().isEmpty()
                    || txtCliRua.getText().isEmpty() || txtCliNumero.getText().isEmpty()) {
                return false;
            } else {
                CPF cpf = new CPF(txtCliCPF.getText());

                if (!cpf.validaCPF()) {     //Faz a validacao do CPF
                    JOptionPane.showMessageDialog(null, "Número de CPF inválido. Por favor, verifique o mesmo.");
                    return false;
                }

                if (tblArduinos.getRowCount() == 0) {
                    return false;
                }

                for (int i = 0; i < tblArduinos.getRowCount(); i++) {   //Verifica se as linhas da tabela Arduinos estão preenchidas corretamente
                    if (tblArduinos.getValueAt(i, 1).toString().isEmpty()) {
                        return false;
                    }
                }

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliNome.getText());
                pst.setString(2, txtCliDataNascimento.getText());
                pst.setString(3, txtCliCPF.getText());
                pst.setString(4, txtCliEmail.getText());

                //A linha abaixo atualiza a tabela clientes (cria um novo cliente)
                pst.executeUpdate();

                //Após inserir dados na tabela de clientes com sucesso, insere dados na tabela telefones
                String lastID = lastClientAdded();    //Pega Id do cliente cadastrado no passo anterior gerado pelo MySQL
                sql = "INSERT INTO telefones (fone, tipo, idcliente) VALUES (?,?,?)";

                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtCliFone.getText());
                pst.setString(2, "Residencial");
                pst.setString(3, lastID);

                //A linha abaixo atualiza a tabela telefones (Linka com o novo cliente)
                pst.executeUpdate();

                if (!txtCliCel.getText().isEmpty()) {       //Se o campo txtCliCel não estiver vazio...
                    sql = "INSERT INTO telefones (fone, tipo, idcliente) VALUES (?,?,?)";

                    pst = conexao.prepareStatement(sql);

                    pst.setString(1, txtCliCel.getText());
                    pst.setString(2, "Celular");
                    pst.setString(3, lastID);

                    pst.executeUpdate();
                }

                //Insere dados na tabela endereco
                sql = "INSERT INTO endereco (cidade, uf, bairro, rua, numero, apto, complemento, idcliente) VALUES (?,?,?,?,?,?,?,?)";

                if (txtCliApto.getText().isEmpty()) {
                    txtCliApto.setText("0");
                }

                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtCliCidade.getText());
                pst.setString(2, txtCliUF.getText());
                pst.setString(3, txtCliBairro.getText());
                pst.setString(4, txtCliRua.getText());
                pst.setString(5, txtCliNumero.getText());
                pst.setString(6, txtCliApto.getText());
                pst.setString(7, txtCliComp.getText());
                pst.setString(8, lastID);

                //A linha abaixo atualiza a tabela endereco
                pst.executeUpdate();

                //Por último cadastra todos Arduinos
                for (int i = 0; i < tblArduinos.getRowCount(); i++) {
                    sql = "INSERT INTO arduino (localizacao, idcliente) VALUES (?,?)";
                    try {
                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, tblArduinos.getValueAt(i, 1).toString());
                        pst.setString(2, lastID);

                        //A linha abaixo atualiza a tabela arduino
                        //e finaliza o cadastro do cliente
                        pst.executeUpdate();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }
        } catch (SQLException | HeadlessException f) {
            JOptionPane.showMessageDialog(null, f);
        }
        return true;
    }

    /**
     * Method that searches the clients table for a match with txtCliPesquisar field
     * @since SMCI 1.0
     */
    private void Consultar() {
        String sql = "SELECT idcliente AS ID, nome AS Nome, data_nascimento AS Nascimento, cpf AS CPF, email AS Email, data_cadastro AS Inscrição FROM clientes WHERE nome LIKE ?";
        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteúdo da caixa de pesquisa para o ?
            //Detalhe para a "%" que vai ao final do comando
            pst.setString(1, "%" + txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml para preencher a tabela tblClientes
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Method that fill all the fields of the form when the user click on a tblClientes row
     * @since SMCI 1.0
     */
    private void fillFields() {
        int selectedrow = tblClientes.getSelectedRow();

        String sql = "SELECT *\n"
                + "FROM clientes T1\n"
                + "INNER JOIN endereco T2 ON T1.idcliente = T2.idcliente\n"
                + "INNER JOIN telefones T3 ON T1.idcliente = T3.idcliente\n"
                + "WHERE T1.idcliente = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tblClientes.getModel().getValueAt(selectedrow, 0).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                txtCliID.setText(rs.getString(1));
                txtCliNome.setText(rs.getString(2));
                txtCliDataNascimento.setText(rs.getString(3));
                txtCliCPF.setText(rs.getString(4));
                txtCliEmail.setText(rs.getString(5));
                txtCliDataCadastro.setText(rs.getString(6));
                txtCliCidade.setText(rs.getString(8));
                txtCliUF.setText(rs.getString(9));
                txtCliBairro.setText(rs.getString(10));
                txtCliRua.setText(rs.getString(11));
                txtCliNumero.setText(rs.getString(12));
                txtCliApto.setText(rs.getString(13));
                txtCliComp.setText(rs.getString(14));
                txtCliFone.setText(rs.getString(17));
                btnCliCreate.setEnabled(false);
                btnCliCancela.setEnabled(true);
            }

            if (rs.next()) {
                txtCliCel.setText(rs.getString(17));     //Se houver numero de celular então recebe ele
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        sql = "SELECT * FROM arduino WHERE idcliente = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliID.getText());
            rs = pst.executeQuery();
            //A linha abaixo usa a biblioteca rs2xml para preencher a tabela tblArduinos
            tblArduinos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Method used to get the latest record in the client table
     * @return String containing the ID
     * @since SMCI 1.0
     */
    private String lastClientAdded() {
        String sql = "SELECT * FROM clientes ORDER BY idcliente DESC LIMIT 1";
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
     * Method used to update a client record
     * @return Boolean true if the update succeeded
     * @since SMCI 1.0
     */
    private boolean Alterar() {
        int confirma = JOptionPane.showConfirmDialog(null, "Esta ação irá alterar permanentemente alguns registros do cliente.\nDeseja proceder"
                + " mesmo assim?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {

            String sql = "UPDATE clientes SET nome = ?, email = ? WHERE idcliente = ?";
            try {
                //Validação dos campos obrigatórios
                if (txtCliNome.getText().isEmpty() || txtCliFone.getText().isEmpty() || txtCliCidade.getText().isEmpty() || txtCliUF.getText().isEmpty()
                        || txtCliBairro.getText().isEmpty() || txtCliRua.getText().isEmpty() || txtCliNumero.getText().isEmpty()) {
                    return false;
                } else {
                    if (tblArduinos.getRowCount() == 0) {
                        return false;
                    }

                    for (int i = 0; i < tblArduinos.getRowCount(); i++) {
                        if (tblArduinos.getValueAt(i, 1).toString().isEmpty()) {
                            return false;
                        }
                    }

                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliNome.getText());
                    pst.setString(2, txtCliEmail.getText());
                    pst.setString(3, txtCliID.getText());

                    //A linha abaixo atualiza os dados do cliente
                    pst.executeUpdate();

                    //Após atualizar os dados na tabela de clientes com sucesso, atualiza os dados de telefone
                    sql = "UPDATE telefones SET fone = ?, tipo = ? WHERE idcliente = ? AND tipo = 'Residencial'";

                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliFone.getText());
                    pst.setString(2, "Residencial");
                    pst.setString(3, txtCliID.getText());

                    //A linha abaixo executa o update na tabela
                    pst.executeUpdate();

                    if (!txtCliCel.getText().isEmpty()) {       //Se o campo txtCliCel não estiver nulo então atualiza tambem
                        sql = "UPDATE telefones SET fone = ?, tipo = ? WHERE idcliente = ? AND tipo = 'Celular'";

                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, txtCliCel.getText());
                        pst.setString(2, "Celular");
                        pst.setString(3, txtCliID.getText());

                        //A linha abaixo executa o update na tabela
                        if (pst.executeUpdate() == 0) {     //Se não existia registro de celular anteriormente então adiciona um
                            sql = "INSERT INTO telefones (fone, tipo, idcliente) VALUES (?,?,?)";

                            pst = conexao.prepareStatement(sql);
                            pst.setString(1, txtCliCel.getText());
                            pst.setString(2, "Celular");
                            pst.setString(3, txtCliID.getText());

                            pst.executeUpdate();
                        }
                    }

                    //Atualiza os dados de endereco
                    sql = "UPDATE endereco SET cidade = ?, uf = ?, bairro = ?, rua = ?, numero = ?, apto = ?, complemento = ? WHERE idcliente = ?";

                    if (txtCliApto.getText().isEmpty()) {
                        txtCliApto.setText("0");
                    }

                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliCidade.getText());
                    pst.setString(2, txtCliUF.getText());
                    pst.setString(3, txtCliBairro.getText());
                    pst.setString(4, txtCliRua.getText());
                    pst.setString(5, txtCliNumero.getText());
                    pst.setString(6, txtCliApto.getText());
                    pst.setString(7, txtCliComp.getText());
                    pst.setString(8, txtCliID.getText());

                    //A linha abaixo executa o update na tabela
                    pst.executeUpdate();

                    //Para atualizar a tabela arduinos e preciso excluir os dados dela e das tabelas vinculadas
                    sql = "DELETE FROM sensores WHERE idcliente = ?";   //Deleta dados dos sensores de cada arduino

                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliID.getText());
                    pst.executeUpdate();    //Executa update

                    //Remove todos arduinos
                    sql = "DELETE FROM arduino WHERE idcliente = ?";

                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliID.getText());
                    pst.executeUpdate();

                    //Por ultimo insere o que esta na nova tabela de arduinos (tblArduinos)
                    for (int i = 0; i < tblArduinos.getRowCount(); i++) {
                        sql = "INSERT INTO arduino (localizacao, idcliente) VALUES (?,?)";

                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, tblArduinos.getValueAt(i, 1).toString());
                        pst.setString(2, tblArduinos.getValueAt(i, 2).toString());

                        //A linha abaixo atualiza a tabela arduino
                        pst.executeUpdate();
                    }
                    //Os arduinos terão novo ID devido ao campo 'idarduino' ser auto-increment
                }
            } catch (SQLException | HeadlessException f) {
                JOptionPane.showMessageDialog(null, f);
            }
        }
        return true;
    }

    /**
     * Method used to remove a client record
     * @return Boolean true if the deletion succeeded
     * @since SMCI 1.0
     */
    private boolean Remover() {
        //A estrutura abaixo confirma a remoção do cliente
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este cadastro?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                //Exclui dados das tabelas que contem foreign keys de tbclientes

                String sql = "DELETE FROM endereco WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());
                pst.executeUpdate();    //Exclui dados da tabela endereco

                sql = "DELETE FROM telefones WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());
                pst.executeUpdate();    //Exlui dados da tabela telefones

                sql = "DELETE FROM historico WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());
                pst.executeUpdate();    //Exlui dados da tabela historico

                sql = "DELETE FROM sensores WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());
                pst.executeUpdate();    //Exlui dados da tabela sensores

                sql = "DELETE FROM arduino WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());
                pst.executeUpdate();    //Exlui dados da tabela arduino

                //Exclui cadastro do cliente
                sql = "DELETE FROM clientes WHERE idcliente = ?";

                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliID.getText());

                int deleted = pst.executeUpdate();  //Exlui dados da tabela clientes

                return deleted > 0;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        return false;
    }

    /**
     * Method that clears all the form's fields
     * @since SMCI 1.0
     */
    private void limpaCampos() {
        txtCliID.setText(null);
        txtCliNome.setText(null);
        txtCliDataNascimento.setText(null);
        txtCliCPF.setText(null);
        txtCliEmail.setText(null);
        txtCliFone.setText(null);
        txtCliCel.setText(null);
        txtCliDataCadastro.setText(null);
        txtCliCidade.setText(null);
        txtCliUF.setText(null);
        txtCliBairro.setText(null);
        txtCliRua.setText(null);
        txtCliNumero.setText(null);
        txtCliApto.setText(null);
        txtCliComp.setText(null);
        txtCliArduino.setText(null);
        txtCliLI.setText(null);
        tblClientes.setModel(dtm1);
        tblArduinos.setModel(dtm2);
        btnCliCreate.setEnabled(true);
        btnCliCancela.setEnabled(false);
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
        btnCliCreate = new javax.swing.JButton();
        btnCliUpdate = new javax.swing.JButton();
        btnCliDelete = new javax.swing.JButton();
        txtCliNome = new javax.swing.JTextField();
        txtCliFone = new javax.swing.JTextField();
        txtCliEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtCliID = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCliDataNascimento = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtCliCPF = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtCliDataCadastro = new javax.swing.JTextField();
        btnCliCancela = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        txtCliCel = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCliCidade = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtCliBairro = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtCliComp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtCliUF = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCliRua = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtCliNumero = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCliApto = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        btnCliCreate1 = new javax.swing.JButton();
        btnCliUpdate1 = new javax.swing.JButton();
        btnCliDelete1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblArduinos = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        txtCliArduino = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtCliLI = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Clientes");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Client.png"))); // NOI18N
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
                formInternalFrameOpened(evt);
            }
        });

        jLabel1.setText("*Nome:");

        jLabel3.setText("Telefones:");

        jLabel4.setText("Email:");

        btnCliCreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Create.png"))); // NOI18N
        btnCliCreate.setToolTipText("Adicionar");
        btnCliCreate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliCreate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliCreateActionPerformed(evt);
            }
        });

        btnCliUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Update.png"))); // NOI18N
        btnCliUpdate.setToolTipText("Alterar");
        btnCliUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliUpdate.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliUpdateActionPerformed(evt);
            }
        });

        btnCliDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Delete.png"))); // NOI18N
        btnCliDelete.setToolTipText("Excluir");
        btnCliDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliDelete.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliDeleteActionPerformed(evt);
            }
        });

        txtCliFone.setToolTipText("Telefone residencial");
        txtCliFone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCliFoneKeyPressed(evt);
            }
        });

        jLabel7.setText("* Campos obrigatórios");

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Search.png"))); // NOI18N

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        jLabel5.setText("ID:");

        txtCliID.setEditable(false);

        jLabel8.setText("Data de Nascimento:");

        txtCliDataNascimento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCliDataNascimentoKeyPressed(evt);
            }
        });

        jLabel9.setText("*CPF:");

        txtCliCPF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCliCPFKeyPressed(evt);
            }
        });

        jLabel10.setText("Data do Cadastro:");

        txtCliDataCadastro.setEditable(false);

        btnCliCancela.setText("Cancela");
        btnCliCancela.setEnabled(false);
        btnCliCancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliCancelaActionPerformed(evt);
            }
        });

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Phone.png"))); // NOI18N
        jLabel22.setText("*");

        txtCliCel.setToolTipText("Celular");
        txtCliCel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCliCelKeyPressed(evt);
            }
        });

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Mobile.png"))); // NOI18N

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
                                .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtCliID, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCliDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(btnCliCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(btnCliUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(btnCliDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(84, 84, 84)
                                        .addComponent(btnCliCancela))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel22)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtCliCel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel23)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel10))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel9)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtCliCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCliDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 103, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCliID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtCliDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCliCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCliCel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtCliDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnCliCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCliUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCliDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCliCancela, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(32, 32, 32)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        txtCliFone.getAccessibleContext().setAccessibleName("");
        txtCliFone.getAccessibleContext().setAccessibleDescription("");

        jTabbedPane1.addTab("Cliente", jPanel1);

        jLabel2.setText("*Cidade:");

        jLabel11.setText("*Bairro:");

        jLabel12.setText("Complemento:");

        jLabel13.setText("*UF:");

        txtCliUF.setToolTipText("");

        jLabel14.setText("*Rua:");

        jLabel15.setText("*N°:");

        txtCliNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliNumeroKeyReleased(evt);
            }
        });

        jLabel16.setText("Apto:");

        txtCliApto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliAptoKeyReleased(evt);
            }
        });

        jLabel20.setText("* Campos obrigatórios");

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
                        .addComponent(txtCliUF, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(154, 154, 154)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliApto, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCliBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliRua, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliComp, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(142, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCliCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCliBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtCliRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCliComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCliUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtCliNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtCliApto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Endereço", jPanel2);

        jLabel21.setText("* Campos obrigatórios");

        btnCliCreate1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Create.png"))); // NOI18N
        btnCliCreate1.setToolTipText("Adicionar");
        btnCliCreate1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliCreate1.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliCreate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliCreate1ActionPerformed(evt);
            }
        });

        btnCliUpdate1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Update.png"))); // NOI18N
        btnCliUpdate1.setToolTipText("Alterar");
        btnCliUpdate1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliUpdate1.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliUpdate1ActionPerformed(evt);
            }
        });

        btnCliDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Delete.png"))); // NOI18N
        btnCliDelete1.setToolTipText("Excluir");
        btnCliDelete1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliDelete1.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliDelete1ActionPerformed(evt);
            }
        });

        tblArduinos.setModel(new javax.swing.table.DefaultTableModel(
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
        tblArduinos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblArduinosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblArduinos);

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/smci/icones/Arduino.png"))); // NOI18N
        jLabel17.setText("Id:");

        txtCliArduino.setEditable(false);

        jLabel18.setText("*Local da Instalação:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(275, 275, 275)
                                .addComponent(btnCliCreate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85)
                                .addComponent(btnCliUpdate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85)
                                .addComponent(btnCliDelete1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(113, 113, 113)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel18)
                                            .addComponent(jLabel17))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCliArduino, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCliLI, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(0, 101, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel21)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtCliArduino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtCliLI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCliCreate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliUpdate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliDelete1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jLabel21)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Arduinos", jPanel3);

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

    private void btnCliDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliDeleteActionPerformed
        // Chamando o método remover
        if (!txtCliID.getText().isEmpty()) {
            if (Remover()) {
                JOptionPane.showMessageDialog(null, "Cadastro removido com sucesso!");
                limpaCampos();
            }
        }
    }//GEN-LAST:event_btnCliDeleteActionPerformed

    private void btnCliUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliUpdateActionPerformed
        // Chamando o método alterar
        if (!txtCliID.getText().isEmpty()) {
            if (Alterar()) {
                JOptionPane.showMessageDialog(null, "Dados do cliente atualizados com sucesso!");
                limpaCampos();
            }
        }
    }//GEN-LAST:event_btnCliUpdateActionPerformed

    private void btnCliCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliCreateActionPerformed
        // Chamando o método adicionar
        if (Adicionar()) {
            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
            limpaCampos();
        }
    }//GEN-LAST:event_btnCliCreateActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // Chamando o método Consultar
        if (!txtCliPesquisar.getText().isEmpty()) {
            Consultar();
        } else {
            tblClientes.setModel(dtm1);
            tblArduinos.setModel(dtm2);
            limpaCampos();
        }
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    //Evento que será usado para setar os campos da tabela (On mouse click)
    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // Chamando o método setarCampos
        fillFields();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void btnCliCancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliCancelaActionPerformed
        // TODO add your handling code here:
        btnCliCreate.setEnabled(true);
        btnCliCancela.setEnabled(false);
        limpaCampos();
    }//GEN-LAST:event_btnCliCancelaActionPerformed

    private void btnCliCreate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliCreate1ActionPerformed
        // Insere dados na próxima linha da tabela Arduinos
        if (txtCliLI.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha todos campos obrigatórios.");
        } else {
            DefaultTableModel model = (DefaultTableModel) tblArduinos.getModel();

            model.addRow(new Object[]{" ", txtCliLI.getText(), txtCliID.getText()});

            txtCliLI.setText(null);
        }
    }//GEN-LAST:event_btnCliCreate1ActionPerformed

    /**
     * Method that updates the tblArduinos table rows but doesnt update them in the database
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void btnCliUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliUpdate1ActionPerformed
        // Atualiza os dados da linha selecionada
        int selectedrow = tblArduinos.getSelectedRow();

        if (selectedrow != -1) {
            DefaultTableModel model = (DefaultTableModel) tblArduinos.getModel();

            String data[] = new String[3];
            data[0] = (tblArduinos.getModel().getValueAt(selectedrow, 0).toString());
            data[1] = (txtCliLI.getText());
            data[2] = (tblArduinos.getModel().getValueAt(selectedrow, 2).toString());

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equals(data[0])) {
                    for (int j = 0; j < data.length; j++) {
                        model.setValueAt(data[j], i, j);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnCliUpdate1ActionPerformed

    /**
     * Method that removes a row from the tblArduinos table but doesnt delete them in the database
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void btnCliDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliDelete1ActionPerformed
        // Deleta os dados da linha selecionada
        int selectedrow = tblArduinos.getSelectedRow();

        if (selectedrow != -1) {
            DefaultTableModel model = (DefaultTableModel) tblArduinos.getModel();
            model.removeRow(selectedrow);
            txtCliArduino.setText(null);
            txtCliLI.setText(null);
        }
    }//GEN-LAST:event_btnCliDelete1ActionPerformed

    private void tblArduinosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblArduinosMouseClicked
        // TODO add your handling code here:
        int selectedrow = tblArduinos.getSelectedRow();

        txtCliArduino.setText(tblArduinos.getModel().getValueAt(selectedrow, 0).toString());
        txtCliLI.setText(tblArduinos.getModel().getValueAt(selectedrow, 1).toString());
    }//GEN-LAST:event_tblArduinosMouseClicked

    /**
     * Method that initializes the TableModels when the Internal Frame opens
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        //Cria objeto do modelo da tabela
        dtm1 = new DefaultTableModel(0, 0);

        //Cria cabeçalho da tabela
        String header[] = new String[]{"ID", "Nome", "Nascimento", "CPF", "Email", "Inscrição"};

        //Adiciona cabeçalho na tabela   
        dtm1.setColumnIdentifiers(header);
        //set model into the table object
        tblClientes.setModel(dtm1);

        dtm2 = new DefaultTableModel(0, 0);

        //Cria cabeçalho da tabela
        String header1[] = new String[]{"ID Arduino", "Local da Instalação", "ID Cliente"};

        //Adiciona cabeçalho na tabela   
        dtm2.setColumnIdentifiers(header1);
        //set model into the table object
        tblArduinos.setModel(dtm2);
    }//GEN-LAST:event_formInternalFrameOpened

    /**
     * Method that formats the CPF field
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliCPFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliCPFKeyPressed
        if (evt.getKeyCode() != 8) {    //Se a tecla pressionada não for a Backspace
            // Formata o campo CPF
            if (txtCliCPF.getText().length() == 3 || txtCliCPF.getText().length() == 7) {
                txtCliCPF.setText(txtCliCPF.getText() + ".");
            }

            if (txtCliCPF.getText().length() == 11) {
                txtCliCPF.setText(txtCliCPF.getText() + "-");
            }
        }
    }//GEN-LAST:event_txtCliCPFKeyPressed

    /**
     * Method that formats the DataNascimento field
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliDataNascimentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliDataNascimentoKeyPressed
        if (evt.getKeyCode() != 8) {    //Se a tecla pressionada não for a Backspace
            // Formata o campo DataNasc
            if (txtCliDataNascimento.getText().length() == 2 || txtCliDataNascimento.getText().length() == 5) {
                txtCliDataNascimento.setText(txtCliDataNascimento.getText() + "/");
            }
        }
    }//GEN-LAST:event_txtCliDataNascimentoKeyPressed

     /**
     * Method that formats the Fone field
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliFoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliFoneKeyPressed
        if (evt.getKeyCode() != 8) {    //Se a tecla pressionada não for a Backspace
            // Formata o campo Fone
            if (txtCliFone.getText().length() == 1) {
                txtCliFone.setText("(" + txtCliFone.getText());
            }

            if (txtCliFone.getText().length() == 3) {
                txtCliFone.setText(txtCliFone.getText() + ")");
            }

            if (txtCliFone.getText().length() == 8) {
                txtCliFone.setText(txtCliFone.getText() + "-");
            }
        }
    }//GEN-LAST:event_txtCliFoneKeyPressed

     /**
     * Method that closes the connection just before the Internal Frame closes
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        try {
            // Fecha conexão aberta por este form com o banco
            conexao.close();
        } catch (SQLException ex) {
            Logger.getLogger(TelaUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameClosing

    /**
     * Method that formats the mobile phone field
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliCelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliCelKeyPressed
        if (evt.getKeyCode() != 8) {    //Se a tecla pressionada não for a Backspace
            // Formata o campo Celular
            if (txtCliCel.getText().length() == 1) {
                txtCliCel.setText("(" + txtCliCel.getText());
            }

            if (txtCliCel.getText().length() == 3) {
                txtCliCel.setText(txtCliCel.getText() + ")");
            }

            if (txtCliCel.getText().length() == 8) {
                txtCliCel.setText(txtCliCel.getText() + "-");
            }
        }
    }//GEN-LAST:event_txtCliCelKeyPressed

    /**
     * Method that removes all the non numbers from the adress number of the client
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliNumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliNumeroKeyReleased
        //Remove qualquer caracter que não seja um dígito ao soltar uma tecla
        txtCliNumero.setText(txtCliNumero.getText().replaceAll("[^0-9]", ""));
    }//GEN-LAST:event_txtCliNumeroKeyReleased

     /**
     * Method that removes all the non numbers from the apartment number of the client
     * @param evt ActionEvent provides information about the event
     * @since SMCI 1.0
     */
    private void txtCliAptoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliAptoKeyReleased
        //Remove qualquer caracter que não seja um dígito ao soltar uma tecla
        txtCliApto.setText(txtCliApto.getText().replaceAll("[^0-9]", ""));
    }//GEN-LAST:event_txtCliAptoKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliCancela;
    private javax.swing.JButton btnCliCreate;
    private javax.swing.JButton btnCliCreate1;
    private javax.swing.JButton btnCliDelete;
    private javax.swing.JButton btnCliDelete1;
    private javax.swing.JButton btnCliUpdate;
    private javax.swing.JButton btnCliUpdate1;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblArduinos;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliApto;
    private javax.swing.JTextField txtCliArduino;
    private javax.swing.JTextField txtCliBairro;
    private javax.swing.JTextField txtCliCPF;
    private javax.swing.JTextField txtCliCel;
    private javax.swing.JTextField txtCliCidade;
    private javax.swing.JTextField txtCliComp;
    private javax.swing.JTextField txtCliDataCadastro;
    private javax.swing.JTextField txtCliDataNascimento;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliFone;
    private javax.swing.JTextField txtCliID;
    private javax.swing.JTextField txtCliLI;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliNumero;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtCliRua;
    private javax.swing.JTextField txtCliUF;
    // End of variables declaration//GEN-END:variables
}
