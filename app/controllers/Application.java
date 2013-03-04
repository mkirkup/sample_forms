package controllers;


import java.util.*;

import models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.*;
import play.cache.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.*;
import static play.data.Form.*;

import views.html.*;

public class Application extends Controller {

	private static final String ADMIN_EMAIL_ADDRESS = "velocity@uwaterloo.ca";

    /**
     * Defines a form wrapping the User class.
     */ 
    final static Form<User> loginForm = form(User.class);

    /**
     * Defines the logger that will be used for logging across the system.
     */
    private static Logger _logger = LoggerFactory.getLogger("controllers.Application");    
  
    public static Result index() {
        return ok(index.render());
    }

    /**
     * Handle the form submission.
     */
    public static Result submit() {
        Form<User> filledForm = loginForm.bindFromRequest();
        
        // Check for errors and if there are any errors spit them out into the log for future debugging
		if(filledForm.hasErrors()){
		  	for(String key : filledForm.errors().keySet()){
				List<ValidationError> currentError = filledForm.errors().get(key);
				for(ValidationError error : currentError){
			  		_logger.debug( "Key: " + key + " Message: " + error.message() );
				}
		  	}
		  	return TODO;
            //return badRequest(index.render(filledForm));
		}

		// Capture and check email address
    	String email = filledForm.field("email").value();
    	if( email == null || email.length() == 0 ) {
    		_logger.debug( "Login failed - email address was null or empty" );
    		filledForm.reject( "email", "Invalid email address");
			return TODO;
			//return badRequest(index.render(filledForm));
    	}
    	_logger.debug( "Login - Email: " + email );

    	// Lookup user in our database using the email address from the previous step
		User existingUser = User.find( email );
		if( existingUser == null ) { 
    		_logger.debug( "Login failed - email address was not signed up" );
    		filledForm.reject( "email", "Invalid email address");
			return TODO;
			//return badRequest(index.render(filledForm));
		}
		_logger.debug( "Existing User? " + (existingUser != null ? "Yes" : "No" ));

        // Check the password to login
        String password = filledForm.field("password").value();
        if( password == null || !existingUser.checkPassword( password )) {
        	_logger.debug( "Login failed - invalid password" );
            filledForm.reject("password", "Invalid password");
            return TODO;
            //return badRequest(index.render(filledForm));
        }
        
        // User login was successfully.  
    	// If the email address is velocity@uwaterloo.ca then we should show the admin panel instead
    	// If not the admin email address then show the Status Entry page.
    	if( email.equals( ADMIN_EMAIL_ADDRESS )) {
			return TODO;
			//return ok(
			//	views.html.admin.render( User.all() )
			//);
    	} else {
            // We have success!  Let them into the site.
			// Place the existingUser in the Cache for access on the other side of the redirect
			Cache.set("Status.existingUser", existingUser, 300);
            //return redirect( routes.Status.blank());
            return TODO;
    	}
    }

  
}