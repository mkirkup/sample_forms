package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    /**
     * Defines the logger that will be used for logging across the system.
     */
    private static Logger _logger = LoggerFactory.getLogger("controllers.SignUp");    
  
    public static Result index() {
        return ok(index.render());
    }
  
}