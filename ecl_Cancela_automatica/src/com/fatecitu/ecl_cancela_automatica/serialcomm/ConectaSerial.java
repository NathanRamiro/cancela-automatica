package com.fatecitu.ecl_cancela_automatica.serialcomm;

import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;

public class ConectaSerial {
	
	public ConectaSerial(){
		
	}

	public ArrayList<String> getPortas() {
		
		SerialPort[] portas = SerialPort.getCommPorts();
		
		ArrayList<String> lista= new ArrayList<String>(portas.length);
		
		for(SerialPort i : portas) {
			lista.add(i.getSystemPortName().toString()
					  + " | "
					  + i.getDescriptivePortName().toString());
		}
		
		return lista;
	}
	
	public SerialPort conectaPorta(String porta) {
		
		SerialPort portaSelecionada = SerialPort.getCommPort(porta);
		portaSelecionada.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		
		while(true) {			
			if(portaSelecionada.openPort()) {
				//System.out.wait(4000);
				return portaSelecionada;
			}
		}	
	}
}
