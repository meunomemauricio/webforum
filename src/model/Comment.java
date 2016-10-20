package model;

public class Comment {

	private int _id;
	private String _content;
	private String _login;
	private int _postId;

	public Comment(String content, String login, int postId) {
		this._id = 0;
		this._content = content;
		this._login = login;
		this._postId = postId;
	}

	public Comment(int id, String content, String login, int postId) {
		this._id = id;
		this._content = content;
		this._login = login;
		this._postId = postId;
	}

	public int getId() {
		return _id;
	}

	public String getContent() {
		return _content;
	}

	public String getLogin() {
		return _login;
	}

	public int getPostId() {
		return _postId;
	}

}
