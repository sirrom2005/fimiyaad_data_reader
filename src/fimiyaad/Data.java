/**
 *
 * @author iceman
 */
package fimiyaad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Data {
    String url = "jdbc:mysql://rohanmorris.com/mobile_apps";
    String user = "lyn-sys-2015";
    String pass = "N5ua}%1Zgho$";
    Connection connection = null;
    boolean rs = false;
    Statement stm = null;
    
    public Data(){}
         
    public void insert(String qStr){   
        Object[][] obj = null;
        openDataResource();
        try {
            stm = connection.createStatement();
            rs = stm.execute("INSERT INTO fimiyaad_gallery (country,url,img) VALUES " + qStr.replace(",END_", ""));
            
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            closeDataResource();
        }
    }
    
    private void openDataResource(){
        try {
            connection = DriverManager.getConnection(url, user, pass);           
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeDataResource(){
        try{
            if(connection != null)
                connection.close();
            if(stm != null)
                stm.close();
        }catch(SQLException ex){
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void emptyDb() {
        openDataResource();
        try {
            stm = connection.createStatement();
            rs = stm.execute("truncate fimiyaad_gallery;");          
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            closeDataResource();
        }
    }
}
