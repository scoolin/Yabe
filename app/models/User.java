package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class User extends Model {
	
	@Email
	@Required
	@MaxSize(100)
	public String email;
	
	@Required
	@MaxSize(100)
	public String password;
	
	@Required
	@MaxSize(100)
	public String fullname;
	
	public boolean isAdmin;
	
	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
	}
	
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	public String toString() {
		return this.email;
	}
}
