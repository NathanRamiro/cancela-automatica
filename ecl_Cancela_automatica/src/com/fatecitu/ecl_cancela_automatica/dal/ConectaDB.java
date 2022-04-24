package com.fatecitu.ecl_cancela_automatica.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.sql.ResultSet;

public class ConectaDB {

    private String strConexao;
    private String userDB;
    private String passDB;
    private String tabela;
    private String coluna;

    public ConectaDB(){
    //
    //test data
    //
        strConexao = "jdbc:mysql://localhost:3306/test";
        userDB = "root";
        passDB = "admin";
        tabela = "testTable";
        coluna = "codigo";

    }
    //second constructor for custom parameters?
    public ConectaDB(DBdata DBdata) {
    	this.strConexao=DBdata.getStrConexao();
    	this.userDB=DBdata.getUserDB();
    	this.passDB=DBdata.getPassDB();
    	this.tabela=DBdata.getTabela();
    	this.coluna=DBdata.getColuna();
    }

    public boolean testaConexao(){

        try{
            Connection conex = DriverManager.getConnection(strConexao,userDB,passDB);
            if(conex!=null){

                System.out.println("success");
                conex.close();

                return true;
            }
        } catch(Exception e){
            System.out.println("erro conectando ao BD: ");
            System.out.println(e);
        }

        return false;

    }

    public boolean verificar(String codigo){

        try{
            Connection conex = DriverManager.getConnection(strConexao,userDB,passDB);
            if(conex!=null){
         //
         //test data
         //
                Statement st = conex.createStatement();
                ResultSet res= st.executeQuery("SELECT "+ coluna +" FROM "+ tabela +" WHERE "+ coluna +"=\""+codigo+"\" LIMIT 1");
                
                if(res.next()){
                    if(codigo.equals(res.getString(1))){
                        conex.close();
                        return true;
                    }else{
                        conex.close();
                        return false;
                    }
                }else{
                    conex.close();
                    return false;
                }
            }
        } catch(Exception e){
            System.out.println("erro ao tentar verificar: ");
            System.out.println(e.getMessage());
        }

        return false;

    }
}
