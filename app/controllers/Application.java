package controllers;

import play.*;
import play.mvc.*;

import java.awt.Image;
import java.util.*;

import models.*;
import play.cache.Cache;
import play.data.validation.*;
import play.libs.Codec;
import play.libs.Images;

public class Application extends Controller {

	@Before
	static void addDefaults() {
	    renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
	    renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}
	
    public static void index() {
    	Post frontPost = Post.find("order by created desc").first();
    	List<Post> olderPosts = Post.find("order by created desc").from(1).fetch(10);
        render(frontPost, olderPosts);
    }
    
    public static void show(Long id) {
    	Post post = Post.findById(id);
    	String captchaId = Codec.UUID();
    	render(post, captchaId);
    }

    public static void postComment(
    		Long id, 
    		@Required(message="Author is required") String author, 
    		@Required(message="A message is required") String content,
    		@Required(message="Please type the code") String code,
    		String captchaId) {
    	
    	Post post = Post.findById(id);
    	System.out.println("post captcha: " + code);
    	validation
    		.equals(code, Cache.get(captchaId))
    		.message("Invalid code. Please type it again");
    	
    	if (validation.hasErrors()) {
    		render("Application/show.html", post);
    	}
    	
    	post.addComment(author, content);
    	flash.success("Thanks for posting %s", author);
    	Cache.delete(captchaId);
    	show(id);
    }
    
    public static void captcha(String captchaId) {
    	Images.Captcha captcha = Images.captcha();
    	String code = captcha.getText("#E4EAFD");
    	Cache.set(captchaId, code, "30s");
    	System.out.println("captcha code: " + code);
    	renderBinary(captcha);
    }
    
    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }
}