package models;

import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

import play.data.validation.*;

@Entity
public class Comment extends Model {
	
	@Required
	public String author;
	
	@Required
	public Date created;
	
	@Lob
	@Required
	@MaxSize(2000)
	public String content;
	
	@Required
	@ManyToOne
	public Post post;
	
	public Comment(Post post, String author, String content) {
		this.post = post;
		this.author = author;
		this.content = content;
		this.created = new Date();
	}
}
