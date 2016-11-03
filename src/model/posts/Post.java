package model.posts;

public class Post {

	private int _id;
	private String _title;
	private String _content;
	private String _login;
	private int _votes;

	public Post(String title, String content, String login) {
		this._title = title;
		this._content = content;
		this._login = login;
		_id = 0;
		_votes = 0;
	}

	public Post(int id, String title, String content, String login, int votes) {
		this._id = id;
		this._title = title;
		this._content = content;
		this._login = login;
		this._votes = votes;
	}

	public int getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public String getContent() {
		return _content;
	}

	public String getLogin() {
		return _login;
	}

	public int getVotes() {
		return _votes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_content == null) ? 0 : _content.hashCode());
		result = prime * result + ((_login == null) ? 0 : _login.hashCode());
		result = prime * result + ((_title == null) ? 0 : _title.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Topico [_id=" + _id + ", _title=" + _title + ", _content=" + _content + ", _login=" + _login + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		if (_content == null) {
			if (other._content != null)
				return false;
		} else if (!_content.equals(other._content))
			return false;
		if (_login == null) {
			if (other._login != null)
				return false;
		} else if (!_login.equals(other._login))
			return false;
		if (_title == null) {
			if (other._title != null)
				return false;
		} else if (!_title.equals(other._title))
			return false;
		return true;
	}

}
