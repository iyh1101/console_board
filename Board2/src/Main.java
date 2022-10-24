import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ArrayList<Post> cafe = new ArrayList<>();
		ArrayList<Like> like = new ArrayList<>();
		ArrayList<Reple> reple = new ArrayList<>();
		
		loop:
		while(true) {
			System.out.println("번호를 입력하세요 [ 1.글 작성하기 2.글 목록 3.글 읽기 4.글 삭제 5.글 수정 e:프로그램종료 ]");
			String s = sc.next();
			
			switch(s) {
			case "1" :
				System.out.println("제목을 입력하세요: ");
				String tt = sc.next();
				System.out.println("내용을 입력하세요: ");
				String ct = sc.next(); 
				System.out.println("ID를 입력하세요: ");
				String i = sc.next();
				System.out.println("PW를 입력하세요: ");
				String pw = sc.next();
				System.out.println("번호를 입력하세요: ");
				int n = sc.nextInt();
				cafe.add(new Post(tt, ct, i, pw, n) );
				like.add(new Like(0));
				System.out.println("작성완료!");
				break;
			case "2" :
				if(cafe.size() == 0) {
					System.out.println("글이없어요...");
					break;
				}
				
				System.out.println("글 목록을 확인합니다");
				System.out.println("=============================");
				for(int j = 0; j < cafe.size(); j++) {
					System.out.print("번호: " + cafe.get(j).no + " ");
					System.out.print("제목: " + cafe.get(j).title + " ");
					System.out.print("ID: "+ cafe.get(j).id);
					System.out.println(" ♥: "+ like.get(j).like );
				}
				System.out.println("=============================");
				break;
			case "3" :
				if(cafe.size() == 0) {
					System.out.println("글이없어요...");
					break;
				}
				
				System.out.println("읽으실 글의 번호를 입력하세요: ");
				System.out.println("=============================");
				for(int j = 0; j < cafe.size(); j++) {
					System.out.print("번호: " + cafe.get(j).no + " ");
					System.out.print("제목: " + cafe.get(j).title + " ");
					System.out.print("ID: "+ cafe.get(j).id);
					System.out.println(" ♥: "+ like.get(j).like );
				}
				System.out.println("=============================");
				int readNo = sc.nextInt();
				
				for(int k = 0; k < cafe.size(); k++) {
					if(cafe.get(k).no == readNo) {
						System.out.println("=============================");
						System.out.println("제목: " + cafe.get(k).title);
						System.out.println("=============================");
						System.out.println("작성자 ID: "+ cafe.get(k).id);
						System.out.println("=============================");
						System.out.println("본문: " + cafe.get(k).content);
						System.out.println("=============================");
						System.out.println("♥: " + like.get(k).like );
						
						oo:
						while(true) {
						System.out.println("[ 1.좋아요 2.좋아요 취소 3.댓글 확인 4.댓글 쓰기 5.뒤로가기]");
						String yn = sc.next();
						if(yn.equals("1") ) {
							like.get(k).like += 1;
							System.out.println(readNo + "의 글에 좋아요를 눌렀습니다");
							
						} else {
							if(yn.equals("5") ) {
								System.out.println("메인화면으로 돌아갑니다");
								break oo;
							} // 1번 2번 둘 다 아닐경우에만
							else {
								if(yn.equals("2") ) {
								System.out.println(readNo + "의 글에 좋아요를 취소합니다....");
								if(like.get(k).like == 0 ) {
									like.get(k).like = 0;
								} else {
								like.get(k).like -= 1; }
								}
								else {
									if(yn.equals("3") ) {
										for(int p = 0; p < reple.size(); p++) {
											System.out.println("ㄴ " + reple.get(p).reple );	}
									} else {
										if(yn.equals("4") ) {
											System.out.println("댓글 입력: ");
											String rep = sc.next();
											reple.add(new Reple(rep) );
											System.out.println("댓글 입력 완료");
											}
										}
									}
								}
							}
						}
					}
					// readNo 가 목록에 없는 숫자일때
					else {	System.out.println("번호를 잘못입력하셨습니다");	}
				}
				break;
			case "4" :
				if(cafe.size() == 0) {
					System.out.println("글이없어요...");
					break;
				}
				System.out.println("삭제하실 글의 번호를 입력하세요: ");
				System.out.println("=============================");
				for(int j = 0; j < cafe.size(); j++) {
					System.out.print("번호: " + cafe.get(j).no + " ");
					System.out.print("제목: " + cafe.get(j).title + " ");
					System.out.println("ID: "+ cafe.get(j).id);
				}
				System.out.println("=============================");
				int delNo = sc.nextInt();
				
				for(int z = 0; z < cafe.size(); z++) {
					if(cafe.get(z).no == delNo) {
						System.out.println("ID를 입력하세요: ");
						String cid = sc.next();
						System.out.println("PW를 입력하세요: ");
						String cpw = sc.next();
						
						if(cafe.get(z).id.equals(cid) && cafe.get(z).password.equals(cpw) ) {
							System.out.println(delNo + "번 글과 댓글이 삭제되었습니다");
							cafe.remove(z);
							reple.removeAll(reple);
							like.remove(z);
						}
					} 
				}
				break;
			case "5" :
				if(cafe.size() == 0) {
					System.out.println("글이없어요...");
					break;
				}
				
				System.out.println("수정하실 글의 번호를 입력하세요: ");
				System.out.println("=============================");
				for(int j = 0; j < cafe.size(); j++) {
					System.out.print("번호: " + cafe.get(j).no + " ");
					System.out.print("제목: " + cafe.get(j).title + " ");
					System.out.println("ID: "+ cafe.get(j).id);
				}
				System.out.println("=============================");
				int editNo = sc.nextInt();
				
				for(int z = 0; z < cafe.size(); z++) {
					if(cafe.get(z).no == editNo) {
						System.out.println("ID를 입력하세요: ");
						String cid = sc.next();
						System.out.println("PW를 입력하세요: ");
						String cpw = sc.next();
						
						if(cafe.get(z).id.equals(cid) && cafe.get(z).password.equals(cpw) ) {
							System.out.println("제목을 입력하세요: ");
							String et = sc.next();
							System.out.println("내용을 입력하세요: ");
							String ec = sc.next(); 
							System.out.println("번호를 입력하세요: ");
							int en = sc.nextInt();
							System.out.println(editNo + "번 글이 수정되었습니다");
							cafe.get(z).title = et;
							cafe.get(z).content = ec;
							cafe.get(z).no = en;
						} else {
							System.out.println("아이디 또는 비밀번호가 틀렸습니다");
						}
					}
					else {
						System.out.println("글 번호가 틀렸습니다"); }
				}
				break;
			case "e" :
				System.out.println("프로그램 종료");
				break loop;
			default :
				System.out.println("error");
				break;
			}
		}
	}
}