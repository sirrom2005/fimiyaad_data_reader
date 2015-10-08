/**
 * @author iceman
 * Write image data for fimiyaad.com photo gallery.
 */
package fimiyaad;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FimiYaad implements Runnable{
    private static final String HTML_A_HREF_TAG_PATTERN = 
		"\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";    
    private static final String IMG_SRC_TAG_PATTERN = 
		"\\s*(?i)src\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
    private final JTextArea mTextArea;
    private final String endLn = "\n";
    private Thread t;
    
    FimiYaad(JTextArea textArea) {
        mTextArea = textArea;   
        start();
    }
    
    public void start(){
        if(t==null){
            t = new Thread(this);
            t.start(); 
        }           
    }
    
    private void downLoadHtml(String section,String country){
        String newUrl = "",newImg = "";
        String queryStr = "";
        String tmp, line = "";
        String[] key = null;
        URL url;
        InputStream in = null;
        BufferedReader bReader = null;
        mTextArea.setForeground(Color.BLUE);
        mTextArea.append("Preparing for HTML Content\nSection: " + country + "\nLink: " + section + "!!!" + endLn);
        try {
            url = new URL("http://fimiyaad.com/" + section);
            in = url.openStream();
            bReader = new BufferedReader(new InputStreamReader(in));
            
            while((line = bReader.readLine()) != null){                               
                if(line.contains("width=\"150\" height=\"200\"  alt=\"\"")){
                    Pattern p = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
                    Matcher m = p.matcher(line);
                    if(m.find()) {
                        newUrl = m.group(1).replace("\"", "");                         
                        
                        newUrl = newUrl.replace("2014.fimiyaad.com", "http://fimiyaad.com");
                        newUrl = newUrl.replace("http://http://fimiyaad.com", "http://fimiyaad.com");
                                               
                        if(!(newUrl.startsWith("http://0363c6a.netsolhost.com") || newUrl.startsWith("http://fimiyaad.com"))){
                            newUrl = "http://fimiyaad.com/" + newUrl;
                        }
                        
                        key = newUrl.split("/");
                    } 
                                      
                    if(is_OK(newUrl)){
                        mTextArea.setForeground(Color.BLACK);
                        mTextArea.append(newUrl + endLn);
                        Pattern ip = Pattern.compile(IMG_SRC_TAG_PATTERN);
                        Matcher im = ip.matcher(line);
                        if(im.find()) {
                            newImg = im.group(1).replace("\"", "");   
                        } 
                        
                        tmp = "(md5(\"" + key[key.length-2] + "\"), \""+country+"\", \""+newUrl+"\", \""+newImg+"\"),";
                        
                        if(!queryStr.contains(tmp)){
                            System.out.println(tmp);
                            queryStr = queryStr.concat(tmp);
                        } 
                    }                  
                }
            } 
            
            
            new Data().insert(queryStr.concat("END_"));
            mTextArea.append("Content INSERTED!!!" + endLn+endLn);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FimiYaad.class.getName()).log(Level.SEVERE, null, ex);
            mTextArea.setForeground(Color.RED);
            mTextArea.append(endLn + ex.getMessage().toUpperCase() + endLn + endLn);
        } catch (IOException ex) {
            Logger.getLogger(FimiYaad.class.getName()).log(Level.SEVERE, null, ex);
            mTextArea.setForeground(Color.RED);
            mTextArea.append(endLn + ex.getMessage().toUpperCase() + endLn + endLn);
        }
    }
    
    private boolean is_OK(String newUrl) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(newUrl.replace(" ", "%20"));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setConnectTimeout(3000);
            urlConnection.connect();
            
            return "OK".equals(urlConnection.getResponseMessage());

        } catch(SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } 
        return false;
    }     

    @Override
    public void run() {
        new Data().emptyDb();
        downLoadHtml("pictures.html",    "jamaica");  
        downLoadHtml("throwback.html",   "jamaica");  
        downLoadHtml("picturesusa.html", "usa");      
        downLoadHtml("picturesuk.html",  "uk" );
        JOptionPane.showMessageDialog(null, "Task Complete", "Task Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}
