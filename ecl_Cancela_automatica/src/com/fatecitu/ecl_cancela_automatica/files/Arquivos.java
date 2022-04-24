package com.fatecitu.ecl_cancela_automatica.files;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import com.fatecitu.ecl_cancela_automatica.dal.DBdata;

public class Arquivos {

	public static void salvaPorta(String porta) {
		try {
			File txtPorta = new File("porta.txt");
			if(txtPorta.exists()) {
				txtPorta.delete();
			}
			txtPorta.createNewFile();
			FileWriter print = new FileWriter("porta.txt");
			print.write("porta="+porta);
			print.close();
			 
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	public static String getPorta() {
		
		try {
			File txtPorta = new File("porta.txt");
			if(txtPorta.exists()) {
				Scanner scanner = new Scanner(txtPorta);
				String[] port = scanner.nextLine().split("=");
				scanner.close();
				return port[1];
			}
			
		}catch (Exception e) {
			System.out.println(e);
		}
		
		return"";
	}
	public static void salvaDB(DBdata DBdata) {
		try {
			File txtDB= new File("DB.txt");
			if(txtDB.exists()) {
				txtDB.delete();
			}
			txtDB.createNewFile();
			FileWriter print = new FileWriter("DB.txt");
			print.write("strConexao="+DBdata.getStrConexao()+'\n');
			print.write("userDB="+DBdata.getUserDB()+'\n');
			print.write("passDB="+DBdata.getPassDB()+'\n');
			print.write("tabela="+DBdata.getTabela()+'\n');
			print.write("coluna="+DBdata.getColuna()+'\n');
			print.close();
			 
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	public static DBdata getDB() {
		String strConexao = "";
		String userDB = "";
		String passDB = "";
		String tabela = "";
		String coluna = "";
		
		try {
			File txtDB = new File("DB.txt");
			if(txtDB.exists()) {
				Scanner scanner = new Scanner(txtDB);
				String[] info;
				while(scanner.hasNextLine()) {
					info = scanner.nextLine().split("=");
					switch (info[0]) {
					case "strConexao": {
						strConexao=info[1];
						break;
					}
					case "userDB": {
						userDB=info[1];
						break;
					}
					case "passDB": {
						passDB=info[1];
						break;
					}
					case "tabela": {
						tabela=info[1];
						break;
					}
					case "coluna": {
						coluna=info[1];
						break;
					}
				}
			}
				scanner.close();
				return new DBdata(strConexao, userDB, passDB, tabela, coluna);
			}
			
		}catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}
}
