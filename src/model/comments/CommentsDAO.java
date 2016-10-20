package model.comments;

import java.util.List;

public interface CommentsDAO {

	void addComment(Comment cmnt);

	List<Comment> retrieveComments(int postId);

}
