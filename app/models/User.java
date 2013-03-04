package models;


import java.security.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

public class User extends Model {

    public static final String COMPANY_TYPE_GARAGE = "GARAGE";
    public static final String COMPANY_TYPE_RESIDENCE = "RESIDENCE";

    private byte[] salt;
    private byte[] passwordHash;

    public String name;

    @Id   
    @Required
    @Email
    public String email;
    
    @Required
    @MinLength(value = 6)
    public String password;

    public String companyName;

    public String companyType;     // Must be one of the constants outlined above
    
    private static Logger _logger = LoggerFactory.getLogger("models.Users");    

   
    public interface All {}
    public interface Step1{}    
	public interface Step2{}    

    @Required(groups = {All.class, Step1.class})
    @MinLength(value = 4, groups = {All.class, Step1.class})
    public String username;
    
    @Valid
    public Profile profile;
    
    public User() {
        // Establish the salt for this user.
        SecureRandom random = new SecureRandom();
        salt = random.generateSeed( 128 );
    }

    public User(String email, String password, String name, String companyName, String companyType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.companyName = companyName;
        this.companyType = companyType;
    }
    
    public User(String username, String email, String password, Profile profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }
    
    public static class Profile {
        
        @Required(groups = {All.class, Step2.class})
        public String country;
        
        public String address;
        
        @Min(value = 18, groups = {All.class, Step2.class}) @Max(value = 100, groups = {All.class, Step2.class})
        public Integer age;
        
        public Profile() {}
        
        public Profile(String country, String address, Integer age) {
            this.country = country;
            this.address = address;
            this.age = age;
        }
        
    }

    public byte[] hashPassword( String newPassword )
    {
        _logger.debug( "Hashing Password" );
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch( NoSuchAlgorithmException e ) {
            // Fall back to standard SHA if necessary
            _logger.warn("SHA-512 digest is not available");
            try {
                md = MessageDigest.getInstance( "SHA" );
            } catch( NoSuchAlgorithmException f ) {
                _logger.error( "No Hash Digest avaialble!!!" );
            }
        }

        md.update( this.salt );
        md.update( this.email.getBytes() );
        md.update( this.name.getBytes() );
        md.update( this.companyName.getBytes() );
        md.update( newPassword.getBytes() );

        return md.digest();
    }

    // Implementation of the find helper to initiate queries
    public static Finder<String,User> find = new Finder(
        String.class, User.class
    );

    public static List<User> all() 
    {
        return find.all();
    }

    public boolean checkPassword( String password ) 
    {
        byte[] newPassword = hashPassword( password );
        return Arrays.equals( newPassword, passwordHash );
    }


    public static User find( String email ) 
    {
        return find.byId( email );
    }

    
}