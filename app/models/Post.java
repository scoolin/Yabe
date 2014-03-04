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
	
	@ManyToMany(cascade=CascadeType.PERSIST)
	public Set<Tag> tags;
	
	public Post(User author, String title, String content) {
		this.author = author;
		this.title = title;
		this.content = content;
		this.created = new Date();
		this.comments = new ArrayList<Comment>();
		this.tags = new TreeSet<Tag>();
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
	
	public Post tagItWith(String name) {
		this.tags.add(Tag.findOrCreateByName(name));
		return this;
	}
	
	public static List<Post> findTaggedWith(String tag) {
		return Post.find(
					"select distinct p from Post p join p.tags as t where t.name = ?",
					tag
				).fetch();
	}
	
	// JPQL query for several tags 
	public static List<Post> findTaggedWith(String... tags) {
	    return Post.find(
	            "select distinct p from Post p join p.tags as t where t.name in (:tags)" +
	            " group by p.id, p.author, p.title, p.content,p.created having count(t.id) = :size"
	    ).bind("tags", tags).bind("size", tags.length).fetch();
	}
	
}
