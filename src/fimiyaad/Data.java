/**
 *
 * @author iceman
 */
package fimiyaad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Data {
    /*
    CREATE TABLE `fimiyaad_gallery` (        
                    `id` int(20) NOT NULL AUTO_INCREMENT,  
                    `key_` tinytext NOT NULL,              
                    `country` tinytext NOT NULL,           
                    `url` tinytext NOT NULL,               
                    `img` tinytext NOT NULL,               
                    `date` date DEFAULT NULL,              
                    PRIMARY KEY (`id`)                     
                  ) ENGINE=MyISAM DEFAULT CHARSET=latin1 
    
    String url = "jdbc:mysql://rohanmorris.com/mobile_apps";
    String user = "DB_USER";
    String pass = "DB_PASS";*/
    String url = "jdbc:mysql://localhost:3306/mobile_apps";
    String user = "root";
    String pass = "";
    Connection connection = null;
    boolean rs = false;
    Statement stm = null;
    
    public Data(){}
         
    public void insert(String qStr){   
        openDataResource();
        try {
            stm = connection.createStatement();
            rs = stm.execute("INSERT INTO fimiyaad_gallery (key_,country,url,img) VALUES " + qStr.replace(",END_", ""));
            
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            closeDataResource();
        }
    }
    
    public Object[][] getGalleryList(String country){   
        openDataResource();
        ResultSet r = null;
        Object[][] data = null;
        try {
            stm = connection.createStatement();
            r = stm.executeQuery("SELECT * FROM fimiyaad_gallery WHERE country = '" + country + "'");   
            
            if(r.last()){ 
                data = new Object[r.getRow()][3];
            }
            
            r.beforeFirst();
            int i = 0;
            while(r.next()){                
                data[i][0] = r.getString("url"); 
                data[i][1] = r.getString("img"); 
                i++;
            } 
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            closeDataResource();
        }
        return data;
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
