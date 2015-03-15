package models;
import play.db.*;

import java.text.SimpleDateFormat;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;

import com.dropbox.core.*;


public class Model {

public static Model sharedInstance = new Model ();
private HashMap<String, User> users = new HashMap<>();
public SimpleDateFormat date = new SimpleDateFormat();
public GregorianCalendar gdate = new GregorianCalendar();


private Model(){
	System.out.println("Play: " + play.core.PlayVersion.current());
	//Connection aufbauen
//	String adresse = „https://api.dropbox.com/1/team/get_info“;
//	URL url = new URL(adresse);
//	URLConnection conn = url.openConnection();
//	conn.setDoOutput(true);


}

public void removeUserFromMap(String userId){
	users.remove(userId);
	System.out.println(gdate.getTime() + ": removed from HashMap: " + userId);
}

public String getUserName(String userId){
	if(userId!=null){
	return users.get(userId).vorname;
	}else{
		return "0000";
	}
}


public Boolean regestrierung(String vorname, String name, String password, String email){

	String passworde = "";
	Connection conn = null;
	Statement stmtCheckUserIsEmpty = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
boolean userAngelegt = false;
passworde = encoding(password);
String insert = "";
//if (checkForValidRegistration(email)) { 				
			try {
				conn = DB.getConnection();
				stmtCheckUserIsEmpty = conn.createStatement();
				rs = stmtCheckUserIsEmpty.executeQuery("SELECT * FROM user");
				if (!rs.next()) {
					insert = "insert into User(userId,email,password,vorname,name)"
							+ " values(1, ?, ?, ?, ?);";
				} else {
					insert = "insert into User(userId,email,password,vorname,name)"
							+ " values((SELECT MAX (userId) FROM User)+1, "
							+ "?, ?, ?, ?);";
				}
				stmt = conn.prepareStatement(insert);
				stmt.setString(3, vorname);
				stmt.setString(4, name);
				stmt.setString(1, email);
				stmt.setString(2, passworde);
				stmt.executeUpdate();
				userAngelegt = true;

			} catch (SQLException e) {
				System.out.println("Fehler in der Registrierung");
				e.printStackTrace();
			} finally {

				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
					}
				}
				if (stmtCheckUserIsEmpty != null) {
					try {
						stmtCheckUserIsEmpty.close();
					} catch (SQLException e) {
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}
	//	}
return userAngelegt;
}



//public boolean checkForValidRegistration(String email) {
//		boolean registrationIsValid = true;

//		String regexEmail = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		
//		if (!email.matches(regexEmail)) {
//			registrationIsValid = false;
//		}
//		return registrationIsValid;
//	}

public static String encoding(String passwort) {
		String passwortString = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(passwort.getBytes());

			for (byte b : md.digest()) {
				passwortString += Byte.toString(b);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return passwortString;
	}


public User login (String password, String email){
Connection conn = null;
PreparedStatement pStmt = null;
ResultSet rs = null;
String passworde = "";
passworde = encoding(password);
User usLog = null;
String select = "SELECT * FROM user WHERE email = ? AND password = ?;";

try {
 conn = DB.getConnection();
 pStmt = conn.prepareStatement(select);
 pStmt.setString(1, email);
 pStmt.setString(2, passworde);
 rs= pStmt.executeQuery();
 if(rs.next()){
 System.out.println("password richtig");
 usLog = new User(rs.getString("userId"),rs.getString("name"),rs.getString("vorname"));
 users.put(rs.getString("userId"), usLog);
 }
 else{
  System.out.println("password falsch");
 }
}catch(SQLException ex){
ex.printStackTrace();
}
finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pStmt != null) {
				try {
					pStmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return usLog;

}

public static void dropboxlogin (){
	String clientIdentifier="VerwaltungsCloud";
	String userLocale =Locale.getDefault().toString();
	try {
	DbxAppInfo appInfo = new DbxAppInfo("85squj8jmqz0j94","2yuuc90unks8z58");
	DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier,userLocale);
	DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
	
	Desktop.getDesktop().browse(new URI(webAuth.start()));
	
	
	
	} catch (IOException | URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

public static TreeSet dropboxAuth (String code){
	
	TreeSet<String> dateinstruktur=new TreeSet<String>();
	String clientIdentifier="VerwaltungsCloud";
	String userLocale =Locale.getDefault().toString();
	try {
	DbxAppInfo appInfo = new DbxAppInfo("85squj8jmqz0j94","2yuuc90unks8z58");
	DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier,userLocale);
	DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
	String authCode=webAuth.finish(code).accessToken;
	System.out.println(authCode);
	System.out.println("*********************************************************************");
	System.out.println("authentifiziert");
	System.out.println("*********************************************************************");
	DbxClient client = new DbxClient(requestConfig,authCode);
	DbxEntry.WithChildren files = client.getMetadataWithChildren("/");
	empfangen();
	for (DbxEntry file : files.children) {
		if(file.isFolder()){
			String fileString=file.path;
			dateinstruktur.add(fileString);
			System.out.println(file.path);
		}
	}

	} catch ( DbxException e) {
		if(e!=null){
			dateinstruktur.add("fehler");
			e.printStackTrace();
			return dateinstruktur;
		}
	}
	return dateinstruktur;

}

public static void empfangen(){
	try {
	URL url = new URL("https://dropbox.com/");         
	URLConnection conn = url.openConnection();
	
	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 
	String line;
	StringBuffer sb = new StringBuffer ();
		while ((line = rd.readLine()) != null) {
		sb.append(line + "\n");
		}
		System.out.println(line);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

}