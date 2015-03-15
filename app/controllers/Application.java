package controllers;

import play.mvc.*;
import play.data.*;
import views.html.*;
import models.*;

public class Application extends Controller {
	public static Form <User> formUser = Form.form(User.class).bindFromRequest();
    public static Form<Authent> formAuth=Form.form(Authent.class).bindFromRequest();
	
	public static Result index() {
       Form <User> form = formUser.bindFromRequest();
	return ok(start.render(form));
    }
	
	public static Result dropbox() {
		Form <Authent> form = formAuth.bindFromRequest();
		Model.dropboxlogin();
		String userId=session("userId");
		return ok(mainpage.render(Model.sharedInstance.getUserName(userId),form));
	}
	
	public static Result mainpage(){
		Form <Authent> form = formAuth.bindFromRequest();
		String userId=session("userId");
		return ok(mainpage.render(Model.sharedInstance.getUserName(userId),form));
	}
	public static Result logout() {
	   String userId = session("userId");
	   Form <User> form = formUser.bindFromRequest();
	   Model.sharedInstance.removeUserFromMap(userId);
		session().clear();
	return ok(start.render(form));
	}
	
	public static Result authentification(){
		Form <Authent> form = formAuth.bindFromRequest();
		System.out.println(form);
		String auth=form.data().get("authe");
		String userId=session("userId");
		return ok(mainpage2.render(Model.sharedInstance.getUserName(userId),form,Model.dropboxAuth(auth))); 
	}
	
	public static Result registriert(){
	Form <User> form = formUser.bindFromRequest();
	   if(form.hasErrors()){
	System.out.print("Fehler in der Form");
	}else{
	System.out.println(form);
	   boolean reg = Model.sharedInstance.regestrierung(form.data().get("vorname"),form.data().get("name"),
														form.data().get("pw"),form.data().get("email"));
	   if(reg==true){
	   return ok(start.render(form));
	   }
	   }
	   return ok(start.render(form));
	}
	
	public static Result start(){
	Form <User> form = formUser.bindFromRequest();
	Form <Authent> formA = formAuth.bindFromRequest();
	session().clear();
	if(form.hasErrors()){
	System.out.print("Fehler in der Form");
	}else{
		User us = Model.sharedInstance.login(form.data().get("password"),form.data().get("email"));
		if(us != null){
		session("userId", us.getUserId());
		return ok(mainpage.render(Model.sharedInstance.getUserName(us.userId),formA));
		}else{
		return ok(start.render(form));
	}
	}
	return ok(start.render(null));
	}

}
