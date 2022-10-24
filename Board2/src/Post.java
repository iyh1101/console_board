
public class Post {
	//제목 글내용 아이디 패스워드 글번호
	String title;
	String content;
	String id;
	String password;
	int no;
	
	Post() {
		
	}
	
	public Post(String title, String content, String id, String password, int no) {
		this.title = title;
		this.content = content;
		this.id = id;
		this.no = no;
		this.password = password;
	}
	
	
}
