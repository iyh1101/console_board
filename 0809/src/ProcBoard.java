import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProcBoard {
	Connection con = null;
	Statement st = null;
	ResultSet result = null;
	// 로그인 하기 y.n 해서 비회원으로 들어오면 글 읽기 댓글쓰기까지 가능 수정
	// 회원 1 2 3 4 5 좋아요 가능 댓글 가능
	// 비회원 1 (아이디는 입력안하고 비번만 입력해서 수정 삭제는 되게끔) 2 3 4 5 대신 좋아요 불가능 댓글 가능한데 글쓰기처럼 비번만입력
	void run() {
		lp:
		while(true) {
		dbInit();
		System.out.println("로그인화면 [ 1.로그인 2.비회원 접속 e.접속종료 ]");
		Scanner sc = new Scanner(System.in);
		String c = sc.next();
		switch(c) {
		case "1" :
			System.out.println(" id: ");
			String lgId = sc.next();
			System.out.println(" pw: ");
			String lgPw = sc.next();
			String login = checkId("select id from mk1 where not id = '익명' and id = '" + lgId + "' and pw = '" + lgPw + "';");
			String loginPw = checkPw("select pw from mk1 where not id = '익명' and id = '" + lgId + "' and pw = '" + lgPw + "';");
			
			if(lgId.equals(login) && lgPw.equals(loginPw) ) {
				System.out.println("로그인 성공");
				System.out.println("### 추천기능 활성화, 익명활동을 이용하실 수 없습니다 ###");
					loop:
					while(true) {
						System.out.println("🐈1.글작성 2.글리스트 3.글읽기 4.글 수정 5.글 삭제 e.로그아웃🐈");
						String cmd = sc.next();
					switch(cmd) {
					case "1" :
						System.out.println("글제목을 입력해주세요");
						String title = sc.next();
						System.out.println("본문 내용을 입력해주세요");
						String body = sc.next();
						dbExecuteUpdate("insert into mk1 values (0, '" + title + "', '" + lgId + "', now(), '" + body + "', 0, 0, 0, null, 0, '" + lgPw +"', '+');");
						String num = checkNum("select num from mk1 order by num desc limit 1;");
						try {
						int nn = Integer.parseInt(num);
						dbExecuteUpdate("update mk1 set icode = num where num = " + nn + ";");
						break;
						} catch(Exception e) {
							e.getMessage();
							System.out.println("error code 1");
						}
					case "2" :
						dbExecuteQuery("select title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where not body='빈 내용' order by tm desc;");
						break;
					case "3" :
						System.out.println("글번호를 선택하세요");
						listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where not body='빈 내용' order by tm desc;");
						cmd = sc.next();
						try {
						int n = Integer.parseInt(cmd);
						dbExecuteUpdate("update mk1 set count = count + 1 where num = " + n + ";");
						read("select title, id, body, tm, count, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where num =" + n +";");
						System.out.println("============================================");
						System.out.println("댓글 :");
						readReple("select id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and body = '빈 내용';");
						System.out.println("입력해주세요 [ 1: 본문 추천 / 2: 본문 추천 취소 / 3: 댓글 추천 / 4: 댓글 추천 취소\n 5: 댓글 작성 / 6: 댓글 수정 / d: 댓글 삭제 / n: 메인화면 이동 ]");
						cmd = sc.next();
						//추천기능 변경
						if(cmd.equals("1") ) {
							String clcl = checklcode("select (length(lcode)) - length(replace(lcode,'+"+lgId+"]+','')) as lcode from mk1 where num = "+n+";");
							int cln = Integer.parseInt(clcl);
							if(cln == 0) {
								dbExecuteUpdate("update mk1 set lcode = concat(lcode, '"+lgId+"]+') where num = " + n + ";");
								System.out.println("추천하기를 눌렀습니다");
								break;
							} else {
								System.out.println("이미 추천하신 글입니다");
								break;
							}
						} else if(cmd.equals("2") ) {
							String clcl = checklcode("select (length(lcode)) - length(replace(lcode,'+"+lgId+"]+','')) as lcode from mk1 where num = "+n+";");
							int cln = Integer.parseInt(clcl);
							if(cln == 0) {
								System.out.println("추천한적이 없어요");
								break;
							} else {
								dbExecuteUpdate("update mk1 set lcode = replace(lcode, '+"+lgId+"]+', '+') where lcode like '%+"+lgId+"]%';");
								System.out.println("추천을 취소했습니다...");
								break;
							}
						} else if(cmd.equals("n") ) {
							System.out.println("누르지 않고 돌아갑니다");
							break;
						} else if(cmd.equals("5") ) {
								System.out.println("댓글을 입력해주세요");
								String rb = sc.next();
								dbExecuteUpdate("insert into mk1 values (0, default, '" + lgId + "', now(), default, 0, 0, " + n + ", '" + rb + "', 0, '" + lgPw + "', '+');");
								System.out.println("댓글입력 완료");
								break;
							} else if(cmd.equals("6") ) {
							// 수정
							rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
							System.out.println("수정하고자 하는 댓글의 번호를 입력해주세요");
							cmd = sc.next();
							//try
							int i = Integer.parseInt(cmd);
							String edID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
							if(edID.equals(lgId) ) {
								System.out.println("댓글내용을 입력해주세요");
								String rb = sc.next();
								dbExecuteUpdate("update mk1 set rpbody = '" + rb + "' where num = " + i + ";");
								System.out.println("수정완");
								break;
							} else {
									System.out.println("아이디 불일치");
									break;
								}
							} else if(cmd.equals("d") ) {
							rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
							System.out.println("삭제하고자 하는 댓글의 번호를 입력해주세요");
							cmd = sc.next();
							int i = Integer.parseInt(cmd);
							//try
							String edID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
							if(edID.equals(lgId) ) {
								dbExecuteUpdate("delete from mk1 where num =" + i + ";");
								System.out.println("삭제완");
								break;
							} else {
									System.out.println("아이디 불일치");
									break;
								}
							} else if(cmd.equals("4") ) {
							rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
							System.out.println("취소하고자 하는 댓글의 번호를 입력해주세요");
							cmd = sc.next();
							int i = Integer.parseInt(cmd);
							try {
								String clcl = checklcode("select (length(lcode)) - length(replace(lcode,'+"+lgId+"]+','')) as lcode from mk1 where num = "+i+" and icode = " + n + ";");
								int cln = Integer.parseInt(clcl);
								if(cln == 0) {
									System.out.println("추천한적이 없어요");
									break;
								} else {
									dbExecuteUpdate("update mk1 set lcode = replace(lcode, '+"+lgId+"]+', '+') where lcode like '%+"+lgId+"]%' and num = " + i + " and icode = " + n + ";");
									System.out.println("추천을 취소했습니다...");
									break;
								}
//							dbExecuteUpdate("update mk1 set lcode = replace(lcode, '+"+lgId+"]+', '+') where lcode like '%+"+lgId+"]%' and num = " + i + " and icode = " + n + ";");
							} catch(Exception e) {
								e.getMessage();
								System.out.println("번호를 잘못입력");
								break;
							}
						} else if(cmd.equals("3") ) {
							rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
							System.out.println("원하시는 댓글의 번호를 입력해주세요");
							cmd = sc.next();
							int i = Integer.parseInt(cmd);
							try {
								String clcl = checklcode("select (length(lcode)) - length(replace(lcode,'+"+lgId+"]+','')) as lcode from mk1 where num = "+i+";");
								int cln = Integer.parseInt(clcl);
								if(cln == 0) {
									dbExecuteUpdate("update mk1 set lcode = concat(lcode, '"+lgId+"]+') where num = " + i + ";");
									System.out.println("추천하기를 눌렀습니다");
									break;
								} else {
									System.out.println("이미 추천하신 글입니다");
									break;
								}
							} catch(Exception e) {
								e.getMessage();
								System.out.println("번호를 잘못입력");
								break;
							}
						} else {
							System.out.println("메뉴에 해당하는 번호나 문자를 눌러주세요");
							break;
							}
						} catch(Exception e) {
							e.getMessage();
							System.out.println("잘못입력하셨습니다");
							break;
						}
						
						//좋아요 취소할때 경우 추가
						
						//회원 글수정
					case "4" :
						listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where not id='익명' and rpbody is null order by tm desc;");
						System.out.println("수정할 글번호를 입력해주세요:");
						String editNo = sc.next();
						try {
						int eN = Integer.parseInt(editNo);
						String edId = checkId("select id from mk1 where num = " + eN + ";");
						
						if(edId.equals(lgId) ) {
								System.out.println("글제목을 입력해주세요");
								String edT = sc.next();
								System.out.println("본문 내용을 입력해주세요");
								String edB = sc.next();
								dbExecuteUpdate("update mk1 set title = '" + edT + "' where num = " + eN + ";");
								dbExecuteUpdate("update mk1 set body = '" + edB + "' where num = " + eN + ";");
								break;
							} else {
							System.out.println("아이디 불일치");
							break;
							}
						} catch(Exception e) {
							e.getMessage();
							System.out.println("error code 4");
							break;
						}
						
						//회원 글삭제
					case "5" :
						listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where id='익명' and rpbody is null order by tm desc;");
						System.out.println("삭제할 글번호를 입력해주세요:");
						String delNo = sc.next();
						int dN = Integer.parseInt(delNo);
						try {
						String delId = checkId("select id from mk1 where num = " + dN + ";");
						if(delId.equals(lgId) ) {
							try {
								System.out.println("글을 삭제합니다");
								dbExecuteUpdate("delete from mk1 where num = '" + dN + "';");
								break;
							} catch(Exception e) {
								e.getMessage();
								System.out.println("error code 5");
								break;
							}
						} else {
						System.out.println("번호를 잘못입력하셨거나 다른 사람의 글입니다");
						break;
							}
						} catch(Exception e) {
							e.getMessage();
							System.out.println("error code 5");
							break;
						}
					case "e" :
						System.out.println("로그아웃 \n메인화면으로 돌아갑니다\n==================================================");
						break loop;
					default :
						System.out.println("error");
						break;
						}
					}
				break;
			} else {
				System.out.println("아이디 또는 비밀번호 불일치");
				break;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			//비회원
		case "2" :
			System.out.println("비회원으로 접속하셨습니다 두둥..");
			System.out.println("### 추천 기능을 이용하실 수 없습니다 ###");
				loop:
				while(true) {
					System.out.println("🐈1.글작성 2.글리스트 3.글읽기 4.글 수정 5.글 삭제 e.로그인 화면🐈");
					String cmd = sc.next();
				switch(cmd) {
				case "1" :
					System.out.println("글제목을 입력해주세요");
					String title = sc.next();
					System.out.println("본문 내용을 입력해주세요");
					String body = sc.next();
					System.out.println("pw를 입력해주세요");
					String pw = sc.next();
					dbExecuteUpdate("insert into mk1 values (0, '" + title + "', default, now(), '" + body + "', 0, 0, 0, null, 0, '" + pw +"', '+');");
					dbExecuteUpdate("update mk1 set icode = num where title = '" + title + "' and pw = '" + pw + "';");
					break;
				case "2" :
					dbExecuteQuery("select title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where not body='빈 내용' order by tm desc;");
					break;
				case "3" :
					System.out.println("글번호를 선택하세요");
					listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where not body='빈 내용' order by tm desc;");
					cmd = sc.next();
					try {
					int n = Integer.parseInt(cmd);
					dbExecuteUpdate("update mk1 set count = count + 1 where num = " + n + ";");
					read("select title, id, body, tm, count, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where num =" + n +";");
					System.out.println("============================================");
					System.out.println("댓글 :");
					readReple("select id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and body = '빈 내용';");
					
					System.out.println("입력해주세요 [ 1: 댓글 작성 / 2: 댓글 수정 / 3: 댓글 삭제 / n: 메인화면 이동 ]");
					cmd = sc.next();
					
					if(cmd.equals("1") ) {
							System.out.println("댓글을 입력해주세요");
							String rb = sc.next();
							System.out.println("댓글pw를 입력해주세요");
							String rpw = sc.next();
							dbExecuteUpdate("insert into mk1 values (0, default, default, now(), default, 0, 0, " + n + ", '" + rb + "', 0, '" + rpw + "', '+');");
							System.out.println("댓글입력 완료");
							break;
						} else if(cmd.equals("2") ) {
						// 수정
						rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
						System.out.println("수정하고자 하는 댓글의 번호를 입력해주세요");
						cmd = sc.next();
						int i = Integer.parseInt(cmd);
						//try?
						String edID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
						if(edID.equals("익명") ) {
							System.out.println("댓pw를 입력해주세요");
							String rpw = sc.next();
							String cpw = checkPw("select pw from mk1 where num = " + i + ";");
							if(rpw.equals(cpw) ) {
							System.out.println("댓글내용을 입력해주세요");
							String rb = sc.next();
							dbExecuteUpdate("update mk1 set rpbody = '" + rb + "' where num = " + i + ";");
							System.out.println("수정완");
							break;
							} else {
								System.out.println("비번 불일치");
								break;
								}
							} else {
								System.out.println("비회원은 접근할 수 없거나 잘못입력한 부분적인 부분");
								break;
							}
						} else if(cmd.equals("n") ) {
							System.out.println("취소하고 메인화면으로 돌아가요");
							break;
						} else if(cmd.equals("3") ) {
							rpListUp("select num, id, rpbody, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
							System.out.println("삭제하고자 하는 댓글의 번호를 입력해주세요");
							cmd = sc.next();
							int i = Integer.parseInt(cmd);
							String delID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
							if(delID.equals("익명") ) {
								System.out.println("댓pw를 입력해주세요");
								String rpw = sc.next();
								String cpw = checkPw("select pw from mk1 where num = " + i + ";");
								if(rpw.equals(cpw) ) {
								dbExecuteUpdate("delete from mk1 where num = " + i + ";");
								System.out.println("삭제완료");
								break;
								} else {
									System.out.println("비번 불일치");
									break;
									}
								} else {
									System.out.println("비회원은 접근할 수 없는 댓글이거나 번호를 잘못입력하셨습니다");
								}
						}
					} catch(Exception e) {
							e.getMessage();
							System.out.println("error code 3");
							break;
						}
					break;
					
				case "4" :
					listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where id='익명' and rpbody is null order by tm desc;");
					System.out.println("수정할 글번호를 입력해주세요:");
					String editNo = sc.next();
					int eN = Integer.parseInt(editNo);
					try {
					String edId = checkId("select id from mk1 where num = " + eN + ";");
					
					if(edId.equals("익명") ) {
						System.out.println("pw를 입력하세요");
						String cpw = sc.next();
						String ppp = checkPw("select pw from mk1 where num = '" + eN + "';");
						
						if(cpw.equals(ppp) ) {
							System.out.println("비번일치");
							try {
								System.out.println("글제목을 입력해주세요");
								String edT = sc.next();
								System.out.println("본문 내용을 입력해주세요");
								String edB = sc.next();
								dbExecuteUpdate("update mk1 set title = '" + edT + "' where num = " + eN + ";");
								dbExecuteUpdate("update mk1 set body = '" + edB + "' where num = " + eN + ";");
								break;
							} catch(Exception e) {
								e.getMessage();
								System.out.println("error code 4");
								break;
							}
						} else {
							System.out.println("비번 오류");
							break;
						}
					} else {
						System.out.println("번호를 잘못입력하셨거나 비회월이 접근할 수 없는 글입니다");
						break;
						}
					} catch(Exception e) {
						e.getMessage();
						System.out.println("error code 4");
						break;
					}
				
				case "5" :
					listUp("select num, title , id, count, c_rp, tm, (length(lcode)) - length(replace(lcode,']','')) as lcode from mk1 where id='익명' and rpbody is null order by tm desc;");
					System.out.println("삭제할 글번호를 입력해주세요:");
					String delNo = sc.next();
					int dN = Integer.parseInt(delNo);
					try {
					String delId = checkId("select id from mk1 where num = " + dN + ";");
					if(delId.equals("익명") ) {
					System.out.println("pw를 입력하세요");
					String cpw = sc.next();
					String ppp = checkPw("select pw from mk1 where num = '" + dN + "';");
					
					if(cpw.equals(ppp) ) {
						try {
							System.out.println("비번일치 및 삭제 완료");
							dbExecuteUpdate("delete from mk1 where num = '" + dN + "';");
							break;
						} catch(Exception e) {
							e.getMessage();
							System.out.println("error code 5");
							break;
						}
					} else {
						System.out.println("비번 오류");
						break;
					}
				} else {
					System.out.println("번호를 잘못입력하셨거나 비회월이 접근할 수 없는 글입니다");
					break;
					}
				} catch(Exception e) {
					e.getMessage();
					System.out.println("error code 5");
					break;
				}
				case "e" :
					System.out.println("로그인 화면으로 이동\n==================================================");
					break loop;
				default :
					System.out.println("error");
					break;
					}
				}
			break;
		case "e" :
			System.out.println("다음에 또 만나요 ヾ(•ω•`)o");
			break lp;
		default :
			System.out.println("잘못입력했다우");
			break;
		}
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	
	private void dbInit() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_cat", "root", "admin");
			st = con.createStatement();	// Statement는 정적 SQL문을 실행하고 결과를 반환받기 위한 객체다. Statement하나당 한개의 ResultSet 객체만을 열 수있다.
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	
	private String checklcode(String query) {
		try {
			result = st.executeQuery(query);
			result.next(); 	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
			String name = result.getString("lcode");
			return name;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return null;
		}
	}
	
	private String checkNum(String query) {
		try {
			result = st.executeQuery(query);
			result.next(); 	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
			String name = result.getString("num");
			return name;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return null;
		}
	}	
	
	private String checkPw(String query) {
		try {
			result = st.executeQuery(query);
			result.next(); 	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
			String name = result.getString("pw");
			return name;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return null;
		}
	}	
	private String checkId(String query) {
		try {
			result = st.executeQuery(query);
			result.next(); 	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
			String name = result.getString("id");
			return name;
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return null;
		}
	}	
	private void read(String query) {
		try {
			result = st.executeQuery(query);
			while (result.next() ) {	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
				String name = "제목: " + result.getString("title"); 
				name += "아이디: " + result.getString("id");
				name += "\n내용: " + result.getString("body");
				name += " " + result.getString("tm");
				name += " 🧿: " + result.getString("count");
				name += " 추천: [" + result.getString("lcode") + "]\n";
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}	
	
	private void readReple(String query) {
		try {
			result = st.executeQuery(query);
			while (result.next() ) {	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
				String name = "아이디: " + result.getString("id");
				name += " 댓: " + result.getString("rpbody");
				name += " " + result.getString("tm");
				name += " 추천: [" + result.getString("lcode") + "]\n";
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}	
	
	private void rpListUp(String query) {
		try {
			result = st.executeQuery(query);
			while (result.next() ) {	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
				String name = result.getString("num") + ". 아이디: " + result.getString("id") + "\n내용: " + result.getString("rpbody");
				name += " " + result.getString("tm");
				name += " 추천수: [" + result.getString("lcode") + "]";
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	
	private void listUp(String query) {
		try {
			result = st.executeQuery(query);
			while (result.next() ) {	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
				String name = result.getString("num") + ". 제목: " + result.getString("title") + "\n아이디: " + result.getString("id");
				name += " 조회수: " + result.getString("count");
				name += " [" + result.getString("c_rp");
				name += "] " + result.getString("tm");
				name += " 추천수: [" + result.getString("lcode") + "]";
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	
	private void dbExecuteQuery(String query) {
		try {
			result = st.executeQuery(query);
			while (result.next() ) {	// 결과를 하나씩 빼기. 더 이상 없으면(행 수가 끝나면) false 리턴됨.
				String name = "제목: " + result.getString("title") + "\n아이디: " + result.getString("id");
				name += " 조회수: " + result.getString("count");
				name += " [" + result.getString("c_rp");
				name += "] " + result.getString("tm");
				name += " 추천수: [" + result.getString("lcode") + "]";
				System.out.println(name);
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}	
	private void dbExecuteUpdate(String query) {
		try {
			int resultCount = st.executeUpdate(query);
			System.out.println("처리된 행 수:"+resultCount);
		} catch (SQLException e) {
			e.printStackTrace();
//			System.out.println("SQLException: " + e.getMessage());
//			System.out.println("SQLState: " + e.getSQLState());
		}
	}	
}


//loop:
//while(true) {
//	System.out.println("🐈1.글작성 2.글리스트 3.글읽기 4.글 수정 5.글 삭제 e.프로그램 종료🐈");
//	String cmd = sc.next();
//switch(cmd) {
//case "3" :
//	System.out.println("글번호를 선택하세요");
//	listUp("select num, title , id, count, c_rp, tm, likes from mk1 where not body='빈 내용' order by tm desc;");
//	cmd = sc.next();
//	try {
//	int n = Integer.parseInt(cmd);
//	dbExecuteUpdate("update mk1 set count = count + 1 where num = " + n + ";");
//	read("select title, id, body, tm, count, likes from mk1 where num =" + n +";");
//	System.out.println("============================================");
//	System.out.println("댓글 :");
//	readReple("select id, rpbody, tm, likes from mk1 where icode = " + n + " and body = '빈 내용';");
//	System.out.println("입력해주세요 [ 1: 본문 추천 / 2: 본문 추천 취소 / 3: 댓글 추천 / 4: 댓글 추천 취소\n 5: 댓글 작성 / 6: 댓글 수정 / d: 댓글 삭제 / n: 메인화면 이동 ]");
//	cmd = sc.next();
//	
//	if(cmd.equals("1") ) {
//		dbExecuteUpdate("update mk1 set likes = likes + 1 where num = " + n + ";");
//		System.out.println("추천하기를 눌렀습니다");
//	} else if(cmd.equals("2") ) {
//		dbExecuteUpdate("update mk1 set likes = likes - 1 where num = " + n + ";");
//		dbExecuteUpdate("update mk1 set likes = 0 where likes <= 0;");
//		System.out.println("추천을 취소했습니다...");
//	} else if(cmd.equals("n") ) {
//		System.out.println("누르지 않고 돌아갑니다");
//		break;
//	} else if(cmd.equals("5") ) {
//		System.out.println("입력해주세요 [ x: 익명으로 댓글작성 / z: 익명x 댓글작성]");
//		cmd = sc.next();
//		if(cmd.equals("x") ) {
//			System.out.println("댓글을 입력해주세요");
//			String rb = sc.next();
//			dbExecuteUpdate("insert into mk1 values (0, default, default, now(), default, 0, 0, " + n + ", '" + rb + "', 0, '');");
//			System.out.println("댓글입력 완료");
//			break;
//		} else if(cmd.equals("z") ) {
//			System.out.println("댓id를 입력해주세요");
//			String rid = sc.next();
//			System.out.println("댓pw를 입력해주세요");
//			String rpw = sc.next();
//			System.out.println("댓글을 입력해주세요");
//			String rb = sc.next();
//			dbExecuteUpdate("insert into mk1 values (0, default, '" + rid + "', now(), default, 0, 0, " + n + ", '" + rb + "', 0, '" + rpw + "');");
//			break;
//		}
//	} else if(cmd.equals("6") ) {
//		// 수정
//		rpListUp("select num, id, rpbody, tm, likes from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
//		System.out.println("수정하고자 하는 댓글의 번호를 입력해주세요");
//		cmd = sc.next();
//		int i = Integer.parseInt(cmd);
//		String edID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
//		if(edID.equals("익명") ) {
//			System.out.println("댓글내용을 입력해주세요");
//			String rb = sc.next();
//			dbExecuteUpdate("update mk1 set rpbody = '" + rb + "' where num = " + i + ";");
//			System.out.println("수정완");
//			break;
//		} else if(!(edID.equals("익명")) ) {
//			System.out.println("댓id를 입력해주세요");
//			String rid = sc.next();
//			String cid = checkId("select id from mk1 where num = " + i + ";");
//			System.out.println("댓pw를 입력해주세요");
//			String rpw = sc.next();
//			String cpw = checkPw("select pw from mk1 where num = " + i + ";");
//			if(rid.equals(cid) && rpw.equals(cpw) ) {
//				System.out.println("아이디,비번 일치\n댓글을 입력해주세요");
//				String erb = sc.next();
//				dbExecuteUpdate("update mk1 set rpbody = '" + erb + "' where num = " + i + ";");
//				break;
//			} else {
//				System.out.println("아이디 또는 비번 불일치");
//				break;
//			}
//		}
//	} else if(cmd.equals("d") ) {
//		rpListUp("select num, id, rpbody, tm, likes from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
//		System.out.println("삭제하고자 하는 댓글의 번호를 입력해주세요");
//		cmd = sc.next();
//		int i = Integer.parseInt(cmd);
//		String edID = checkId("select id from mk1 where num = " + i + " and icode = " + n + ";");
//		if(edID.equals("익명") ) {
//			dbExecuteUpdate("delete from mk1 where num =" + i + ";");
//			System.out.println("삭제완");
//			break;
//		} else if(!(edID.equals("익명")) ) {
//			System.out.println("댓id를 입력해주세요");
//			String rid = sc.next();
//			String cid = checkId("select id from mk1 where num = " + i + ";");
//			System.out.println("댓pw를 입력해주세요");
//			String rpw = sc.next();
//			String cpw = checkPw("select pw from mk1 where num = " + i + ";");
//			if(rid.equals(cid) && rpw.equals(cpw) ) {
//				System.out.println("아이디,비번 일치\n댓글을 삭제합니다");
//				dbExecuteUpdate("delete from mk1 where num =" + i + ";");
//				break;
//			} else {
//				System.out.println("아이디 또는 비번 불일치");
//				break;
//			}
//		}
//	} else if(cmd.equals("4") ) {
//		rpListUp("select num, id, rpbody, tm, likes from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
//		System.out.println("취소하고자 하는 댓글의 번호를 입력해주세요");
//		cmd = sc.next();
//		int i = Integer.parseInt(cmd);
//		try {
//		dbExecuteUpdate("update mk1 set likes = likes - 1 where num = " + i + " and icode = " + n + ";");
//		dbExecuteUpdate("update mk1 set likes = 0 where likes <= 0;");
//		System.out.println("추천취소완료");
//		} catch(Exception e) {
//			e.getMessage();
//			System.out.println("번호를 잘못입력");
//			break;
//		}
//	} else if(cmd.equals("3") ) {
//		rpListUp("select num, id, rpbody, tm, likes from mk1 where icode = " + n + " and rpbody is not null order by tm desc;");
//		System.out.println("원하시는 댓글의 번호를 입력해주세요");
//		cmd = sc.next();
//		int i = Integer.parseInt(cmd);
//		try {
//		dbExecuteUpdate("update mk1 set likes = likes + 1 where num = " + i + " and icode = " + n + ";");
//		} catch(Exception e) {
//			e.getMessage();
//			System.out.println("번호를 잘못입력");
//			break;
//		}
//	} else {
//		System.out.println("메뉴에 해당하는 번호나 문자를 눌러주세요");
//		break;
//		}
//	} catch(Exception e) {
//		e.getMessage();
//		System.out.println("잘못입력하셨습니다");
//		break;
//	}
//	break;
//	
//	
//case "4" :
//	listUp("select num, title , id, count, c_rp, tm, likes from mk1 where not id='익명' order by tm desc;");
//	
//	System.out.println("수정할 글번호를 입력해주세요:");
//	String editNo = sc.next();
//	System.out.println("id를 입력하세요");
//	String cid1 = sc.next();
//	String sss1 = checkId("select id from mk1 where id = '" + cid1 + "';");
//	System.out.println("pw를 입력하세요");
//	String cpw1 = sc.next();
//	String ppp1 = checkPw("select pw from mk1 where pw = '" + cpw1 + "';");
//	
//	if(cid1.equals(sss1) && cpw1.equals(ppp1) ) {
//		System.out.println("수정할 제목을 입력해주세요:");
//		String edTitle = sc.next();
//		System.out.println("수정하실 작성자id를 입력해주세요:");
//		String edId = sc.next();				
//		System.out.println("새로운 글내용을 입력해주세요:");
//		String edContent = sc.next();
//		
//		dbExecuteUpdate("update mk1 set title='" + edTitle + "', id='"+edId+"', tm=now(), body='"+edContent+"' where num="+editNo);
//		break;
//	} else {
//		System.out.println("id 또는 pw 불일치");
//		break;
//	}
//
//case "5" :
//	System.out.println("id를 입력하세요");
//	String cid = sc.next();
//	String sss = checkId("select id from mk1 where id = '" + cid + "';");
//	
//	System.out.println("pw를 입력하세요");
//	String cpw = sc.next();
//	String ppp = checkPw("select pw from mk1 where pw = '" + cpw + "';");
//	
//	if(cid.equals(sss) && cpw.equals(ppp) ) {
//		try {
//			dbExecuteUpdate("delete from mk1 where id = '" + cid + "' and pw = '" + cpw + "';");
//			break;
//		} catch(Exception e) {
//			e.getMessage();
//			System.out.println("잘못입력하셨습니다");
//			break;
//		}
//	} else {
//		System.out.println("잘못입력하셨습니다");
//		break;
//	}
//case "e" :
//	System.out.println("프로그램 종료");
//	break loop;
//default :
//	System.out.println("error");
//	break;
//	}
//}