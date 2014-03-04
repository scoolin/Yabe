package models;

import java.util.*;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
public class Post extends Model {
	
	public String title;
	
	public Date created;
	
	@Lob
	public String content;
	
	@ManyToOne
	public User author;

	@OneToMany(mappedBy="post", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	public Post(User author, String title, String content) {
		this.author = author;
		this.title = title;
		this.content = content;
		this.created = new Date();
		this.comments = new ArrayList<Comment>();
	}
	
	public Post addComment(String author, String content) {
		Comment comment = new Comment(this, author, content).save();
		this.comments.add(comment);
		this.save();
		return this;
	}
	
	public Post next() {
		return Post.find("created > ? order by created asc", this.created).first();
	}
	
	public Post prev() {
		return Post.find("created < ? order by created desc", this.created).first();
	}
	
}
