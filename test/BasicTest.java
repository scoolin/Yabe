import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }
    
    @Test
    public void createUserTest() {
    	// create a new user and save it
    	new User("bobo@gmail.com", "secret", "Bob").save();
    	
    	// test
    	assertNotNull(User.connect("bobo@gmail.com", "secret"));
    	assertNull(User.connect("bob@gmail.com", "secret"));
    	assertNull(User.connect("bobo@gmail.com", "notpassword"));
    }
    
    @Test
    public void createPostTest() {
    	User bob = new User("bobo@gmail.com", "password", "Bob").save();
    	
    	new Post(bob, "hello world", "Hello World!").save();
    	
    	assertEquals(1, Post.count());
    	
    	List<Post> posts = Post.find("byAuthor", bob).fetch();
    	
    	assertEquals(1, posts.size());
    	Post firstPost = posts.get(0);
    	assertNotNull(firstPost);
    	assertEquals("hello world",firstPost.title);
    	assertEquals("Hello World!", firstPost.content);
    	assertNotNull(firstPost.created);
    }
    
    @Test
    public void createCommentTest() {
    	User bob = new User("bobo@gmail.com", "password", "Bob").save();
    	
    	Post post = new Post(bob, "hello world", "Hello World!").save();
    	
    	post.addComment("Jane", "hello1");
    	post.addComment("Tom", "hello2");
    	
    	assertEquals(1, User.count());
    	assertEquals(1, Post.count());
    	assertEquals(2, Comment.count());
    	
    	Post bobPost = Post.find("byAuthor", bob).first();
    	assertNotNull(bobPost);
    	
    	assertEquals(2, bobPost.comments.size());
    	assertEquals("Tom", bobPost.comments.get(1).author);
    	
    	bobPost.delete();
    	
    	assertEquals(1, User.count());
    	assertEquals(0, Post.count());
    	assertEquals(0, Comment.count());
    }
    
    
    @Test
    public void fullTest() {
        Fixtures.loadModels("data.yml");
     
        // Count things
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());
     
        // Try to connect as users
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("jeff@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
     
        // Find all of Bob's posts
        List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, bobPosts.size());
     
        // Find all comments related to Bob's posts
        List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, bobComments.size());
     
        // Find the most recent post
        Post frontPost = Post.find("order by created desc").first();
        assertNotNull(frontPost);
        assertEquals("About the model layer", frontPost.title);
     
        // Check that this post has two comments
        assertEquals(2, frontPost.comments.size());
     
        // Post a new comment
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());
    }
    
    @Test
    public void testTags() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
     
        // Create a new post
        Post bobPost = new Post(bob, "My first post", "Hello world").save();
        Post anotherBobPost = new Post(bob, "Hop", "Hello world").save();
     
        // Well
        assertEquals(0, Post.findTaggedWith("Red").size());
     
        // Tag it now
        bobPost.tagItWith("Red").tagItWith("Blue").save();
        anotherBobPost.tagItWith("Red").tagItWith("Green").save();
     
        // Check
        assertEquals(2, Post.findTaggedWith("Red").size());
        assertEquals(1, Post.findTaggedWith("Blue").size());
        assertEquals(1, Post.findTaggedWith("Green").size());
        // check several tags
        assertEquals(1, Post.findTaggedWith("Red", "Blue").size());
        assertEquals(1, Post.findTaggedWith("Red", "Green").size());
        assertEquals(0, Post.findTaggedWith("Red", "Green", "Blue").size());
        assertEquals(0, Post.findTaggedWith("Green", "Blue").size());
        // test for tag cloud
        List<Map> cloud = Tag.getCloud();
        assertEquals(
            "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]",
            cloud.toString()
        );
    }

}
