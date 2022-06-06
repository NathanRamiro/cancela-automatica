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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.fatecitu.ecl_cancela_automatica.dal.consultaAPI;
import com.fatecitu.ecl_cancela_automatica.files.Arquivos;
import com.fazecast.jSerialComm.SerialPort;

public class Main {

	static SerialPort portaArduino;
	static Boolean salvaPorta = false;
	static Boolean conectado = false;
	
	static JFrame janela = new JFrame("Main");
	static JPanel painel = new JPanel();
	static JTextArea infoText = new JTextArea("");
	
	//static Scanner input;
	//static PrintWriter output;
	
    public static void main(String[] args){
    	
    	infoText.setEditable(false);

    	janela.setLayout(new BorderLayout());
    	janela.setSize(400, 400);

    	janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	janela.add(painel,BorderLayout.CENTER);
    	
    	if(!Arquivos.getPorta().equals("")) {
    		conectaPorta(Arquivos.getPorta());
    			//setJanelaInfo();
    	}else {    		
    		setJanelaConPorta();
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
    public static void conectaPorta(String porta) {
    	
    	infoText.setText("");
    	portaArduino = selecionaPorta(porta);
    	
    	if(portaArduino.openPort()) {
    		conectado = true;
    		setJanelaInfo();

    	}
    	Thread thread = new Thread() {
    		@Override public void run() {

    			String code = "";
    			int i =0;
    			Scanner input = new Scanner(portaArduino.getInputStream());
    			PrintWriter output = new PrintWriter(portaArduino.getOutputStream());
    			
    			while(conectado) {

    				i=i++;
    				if(input.hasNextLine()) {
    					try {
    							System.out.println("inside try");
    						code = consultaAPI.verificar(input.nextLine(), true);
    						
    						//dev
    							infoText.insert('\n'+'['+i+']'+"[[DEV]] codigo recebido:"+code, 0);
    						
    						switch (code) {
							case "109": //success
								
								output.print("ok");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Verificado com sucesso", 0);
								
								break;
							case "502": //deactivated
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" O usuário recebido esta desativado", 0);
								
								break;
							case "503": //not found
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Usuário não encontrado", 0);
								
								break;
							case "504": //Tag ID não pôde ser obtida
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Server:Tag ID não pôde ser obtida", 0);
								
								break;
							case "505": //Erro na chave da API
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Server:Erro na chave da API", 0);
								
								break;
							case "not200": //não pode conectar com o serv
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Não foi possivel conectar ao servidor", 0);
								
								break;
							default:  //
								
								output.print("nope");
    							output.flush();
    							
    							infoText.insert('\n'+'['+i+']'+" Erro desconhecido:\""+code+'"', 0);
								
								break;
							}

    					}catch (Exception e) {
    						System.out.println(e);
    					}
    				}
    				
    			}
    				
    			input.close();
    			output.close();
    			portaArduino.closePort();
    			setJanelaConPorta();
    		}
    	};
    	if(conectado) {	
    		thread.start();
    	} else {
    		setJanelaConPorta();
    	}
    }
    
    public static void setJanelaConPorta() {
    	
    	painel.removeAll();
    	painel.setLayout(new GridLayout(2,2));
    	
    	JComboBox<String> cbPortas= new JComboBox<String>();
    	JButton btnConectar= new JButton("Conectar");
    	JCheckBox chSalvaPorta = new JCheckBox("Salvar porta como padrão");
    	
    	painel.add(cbPortas);
    	painel.add(btnConectar);
    	painel.add(chSalvaPorta);
    	
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
				
				if(salvaPorta) {
					Arquivos.salvaPorta(porta[0]);
				}
				
				conectaPorta(porta[0]);
					//setJanelaInfo();
				
			}
		});
    	
    }
    public static void setJanelaInfo() {
    	painel.removeAll();
    	painel.setLayout(new BorderLayout());
    	
    	JButton btnDesconectar = new JButton("Desconectar");
    	JScrollPane scrollPane = new JScrollPane(infoText);
    	
    	painel.add(btnDesconectar,BorderLayout.NORTH);
    	painel.add(scrollPane,BorderLayout.CENTER);
    	
    	btnDesconectar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				conectado = false;
					//setJanelaConPorta();
				//input.close();
				//output.close();
				portaArduino.closePort();
			}
		});
    	
    	janela.setSize(250, 250);
    }
}