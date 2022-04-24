package com.fatecitu.ecl_cancela_automatica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fatecitu.ecl_cancela_automatica.dal.ConectaDB;
import com.fatecitu.ecl_cancela_automatica.dal.DBdata;
import com.fatecitu.ecl_cancela_automatica.files.Arquivos;
import com.fazecast.jSerialComm.SerialPort;

public class Main {
	static ConectaDB db;
	static SerialPort portaArduino;
	static Boolean salvaPorta = false;
	static Boolean conectado = false;
	
	static JFrame janela = new JFrame("Main");
	static JPanel painel = new JPanel();
	
    public static void main(String[] args){
    	
    	janela.setLayout(new BorderLayout());
    	janela.setSize(400, 400);
    	janela.setResizable(false);
    	janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	janela.add(painel,BorderLayout.CENTER);
    	
    	{
    		DBdata DBdata = Arquivos.getDB();
    		if(DBdata!=null) {
    			db= new ConectaDB(DBdata);
    			if(db.testaConexao()) {
    				setJanelaConPorta();
    			} else {
    				setJanelaDBInfo();
    			}
    		} else {    			
    			setJanelaDBInfo();
    		}
    	}
    	
    	janela.setVisible(true);

	}
    public static void getPortas(JComboBox<String> CBPortas) {
    	
    	SerialPort[] portas = SerialPort.getCommPorts();
		
		ArrayList<String> lista= new ArrayList<String>(portas.length);
		
		for(SerialPort i : portas) {
			lista.add(i.getSystemPortName().toString()
					  + " | "
					  + i.getDescriptivePortName().toString());
		}
    	
    	for(String i :lista) {
    		CBPortas.addItem(i);
    	}
    	
    }
    public static SerialPort selecionaPorta(String porta) {
    	
    	SerialPort portaSelecionada = SerialPort.getCommPort(porta);
		portaSelecionada.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

		return portaSelecionada;
		
    }
    public static void conectaPorta(JComboBox<String> cbPortas, JButton btnConectar,JButton btnVoltar,String porta) {
    	if(!conectado) {
			portaArduino = selecionaPorta(porta);
			if(portaArduino.openPort()) {
				btnConectar.setText("Desconectar");
				cbPortas.setEnabled(false);
				btnVoltar.setEnabled(false);
				conectado = true;
			}
			
			Thread thread = new Thread() {
				@Override public void run() {
					Boolean valido= false;
					Scanner input = new Scanner(portaArduino.getInputStream());
					PrintWriter output = new PrintWriter(portaArduino.getOutputStream());
					while(conectado) {	
						if(input.hasNextLine()) {
							try {
								valido = db.verificar(input.nextLine().toString());
								if(valido) {
									output.print("ok");
									output.flush();
								}
							}catch (Exception e) {
								System.out.println(e);
							}
							
						}
					}
					input.close();
					output.close();
				}
			};
			thread.start();
			
		} else {
			portaArduino.closePort();
			btnConectar.setText("Conectar");
			cbPortas.setEnabled(true);
			btnVoltar.setEnabled(true);
			conectado = false;
		}
    }
    public static void setJanelaDBInfo() {
    	
    	painel.removeAll();
    	painel.setLayout(new GridLayout(6, 2));
    	
    	JLabel labConex = new JLabel("  URL");
    	JTextField tfStrConexao = new JTextField("jdbc:mysql://localhost:3306/<Database>", 1);
    	painel.add(labConex);
    	painel.add(tfStrConexao);
    	
    	JLabel labUser= new JLabel("  Usuario");
    	JTextField tfUser = new JTextField();
    	painel.add(labUser);
    	painel.add(tfUser);
    	
    	JLabel labPass= new JLabel("  Senha");
    	JTextField tfPass = new JTextField();
    	painel.add(labPass);
    	painel.add(tfPass);
    	
    	JLabel labTable= new JLabel("  Tabela");
    	JTextField tfTable = new JTextField();
    	painel.add(labTable);
    	painel.add(tfTable);
    	
    	JLabel labColumn= new JLabel("  Coluna");
    	JTextField tfColumn = new JTextField();
    	painel.add(labColumn);
    	painel.add(tfColumn);
    	
    	JButton btnprevDB = new JButton("Cancelar");
    	JButton btnConDB = new JButton("Salvar e Conectar");
    	painel.add(btnprevDB);
    	painel.add(btnConDB);
    	
    	janela.setSize(500, 250);
    	
    	btnprevDB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DBdata DBdata = Arquivos.getDB();
				if(DBdata != null) {
					db = new ConectaDB(DBdata);
					if(db.testaConexao()) {
						setJanelaConPorta();
						
					} else {
						JOptionPane.showMessageDialog(janela,"Não foi possivel conectar ao banco de dados anterior");
					}
				} else {
					JOptionPane.showMessageDialog(janela,"Não existem dados de conexão anteriores");
				}
				
			}
		});
    	btnConDB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DBdata DBdata = new DBdata(tfStrConexao.getText(),
										   tfUser.getText(), 
										   tfPass.getText(), 
										   tfTable.getText(), 
										   tfColumn.getText()
										  );
				db = new ConectaDB(DBdata);
				if(db.testaConexao()) {
					Arquivos.salvaDB(DBdata);
					setJanelaConPorta();
				} else {
					JOptionPane.showMessageDialog(janela, "Não foi possivel conectar");
				}
				
			}
		});
    }
    public static void setJanelaConPorta() {
    	
    	painel.removeAll();
    	painel.setLayout(new GridLayout(2,2));
    	
    	JComboBox<String> cbPortas= new JComboBox<String>();
    	JButton btnConectar= new JButton("Conectar");
    	JCheckBox chSalvaPorta = new JCheckBox("Salvar porta como padrão");
    	JButton btnVoltar = new JButton("Voltar");
    	
    	painel.add(cbPortas);
    	painel.add(btnConectar);
    	painel.add(chSalvaPorta);
    	painel.add(btnVoltar);
    	
    	getPortas(cbPortas);
    	
    	janela.pack();
    	
    	chSalvaPorta.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				salvaPorta = !salvaPorta;
				
			}
		});
    	
    	btnConectar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] porta = cbPortas.getSelectedItem().toString().split(" ");
				
				if(salvaPorta && !conectado) {
					Arquivos.salvaPorta(porta[0]);
				}
				
				conectaPorta(cbPortas, btnConectar,btnVoltar,porta[0]);
				
			}
		});
    	
    	btnVoltar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setJanelaDBInfo();
				
			}
		});
    	
    	if(!Arquivos.getPorta().equals("")) {
    		conectaPorta(cbPortas, btnConectar,btnVoltar, Arquivos.getPorta());
    	}
    }
}