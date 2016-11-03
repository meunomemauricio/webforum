package model.posts;

import java.util.List;

public interface PostDAO {

	public void insert(Post post);

	public Post retrieve(int id) throws InvalidPost;

	public List<Post> listPosts();

	public void addVotes(int id, int ammount) throws InvalidPost;
}