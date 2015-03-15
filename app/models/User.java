
package models;


public class User{

public String userId;
public String email;
public String password;
public String vorname;
public String name;

public User(){

}

public User(String userId, String name, String vorname){
this.userId=userId;
this.name=name;
this.vorname=vorname;
}

public String getPassword() {
		return password;
	}
public String getEmail() {
		return email;
	}
public String getVorname() {
		return vorname;
	}
public String getName() {
		return name;
	}
public String getUserId(){
return userId;	
	}
}