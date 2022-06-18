package tutorial.board.domain.account;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorial.board.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    // cascade.ALL: 연쇄적으로 저장되거나 제거될 수 있도록
    // orphanRemoval: Member가 제거되거나 연관 관계가 끊어져서 MemberRole이 고아 객체가 되었을 때, MemberRole 제거
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberRole> roles;

    public String[] getRolesInString() {
        ArrayList<String> string_roles = new ArrayList<String>();
        for (MemberRole role : roles){
            string_roles.add(role.getRole().getRoleType().toString());
        }
        return string_roles.toArray(new String[string_roles.size()]);
    }

    //== 생성자 ==//
    @Builder
    public Member(Long id, String username, String password, String nickname, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles.stream().map(r -> new MemberRole(this, r)).collect(toSet());
    }

    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

}