package fimiyaad;

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

/**
 *
 * @author iceman
 */
public class FimiYaad{
    private static final String HTML_A_HREF_TAG_PATTERN = 
		"\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";    
    private static final String IMG_SRC_TAG_PATTERN = 
		"\\s*(?i)src\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
    
    public static void main(String[] args) {
        new Data().emptyDb();
        downLoadHtml("pictures.html",    "jamaica");  
        downLoadHtml("throwback.html",   "jamaica");  
        downLoadHtml("picturesusa.html", "usa");      
        downLoadHtml("picturesuk.html",  "uk");      
    }
    
    public static void downLoadHtml(String section,String country){
        String newUrl = "",newImg = "";
        String queryStr = "";
        String line = "";
        URL url;
        InputStream in = null;
        BufferedReader bReader = null;
        try {
            url = new URL("http://fimiyaad.com/" + section);
            in = url.openStream();
            bReader = new BufferedReader(new InputStreamReader(in));
            
            System.out.println("Prepare for HTML Content!!!");
            int i=1;
            while((line = bReader.readLine()) != null){                               
                //if(line.trim().startsWith("<td width=\"148\">")){   
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
                    } 
                                      
                    if(is_OK(newUrl)){
                        System.out.println(newUrl);
                        Pattern ip = Pattern.compile(IMG_SRC_TAG_PATTERN);
                        Matcher im = ip.matcher(line);
                        if(im.find()) {
                            newImg = im.group(1).replace("\"", "");   
                        } 

                        queryStr = queryStr.concat("(\""+country+"\", \""+newUrl+"\", \""+newImg+"\"),");
                        i++;                   
                    }                  
                }
            } 
            
            new Data().insert(queryStr.concat("END_"));
            System.out.println("Content INSERTED!!!");
        } catch (MalformedURLException ex) {
            Logger.getLogger(FimiYaad.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FimiYaad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean is_OK(String newUrl) {
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
}
