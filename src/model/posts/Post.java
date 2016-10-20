package model.posts;

public class Post {

	private int _id;
	private String _title;
	private String _content;
	private String _login;

	public Post(String _title, String _content, String _login) {
		this._title = _title;
		this._content = _content;
		this._login = _login;
		_id = 0;
	}

	public Post(int _id, String _title, String _content, String _login) {
		this._id = _id;
		this._title = _title;
		this._content = _content;
		this._login = _login;
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
		return "Topico [_id=" + _id + ", _titulo=" + _title + ", _conteudo=" + _content + ", _login=" + _login + "]";
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
