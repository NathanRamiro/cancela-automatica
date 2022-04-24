package com.fatecitu.ecl_cancela_automatica.dal;

public class DBdata {

	private String strConexao;
	private String userDB;
	private String passDB;
	private String tabela;
	private String coluna;
	
	public DBdata(String strConexao,String userDB, String passDB,String tabela, String coluna) {
		
		this.strConexao=strConexao;
		this.userDB=userDB;
		this.passDB=passDB;
		this.tabela=tabela;
		this.coluna=coluna;
		
	}
	
	public String getStrConexao() {
		return strConexao;
	}
	public String getUserDB() {
		return userDB;
	}
	public String getPassDB() {
		return passDB;
	}
	public String getTabela() {
		return tabela;
	}
	public String getColuna() {
		return coluna;
	}
	
}
