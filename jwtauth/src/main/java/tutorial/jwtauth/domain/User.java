package tutorial.jwtauth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity @Table(name="users")
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore private Long userId;

    @Column(length=30, unique=true)
    private String username;

    @JsonIgnore private String password;

    @Column(length=30)
    private String nickname;

    @JsonIgnore private boolean activated;

    @ManyToMany
    @JoinTable(
            name="user_authority",
            joinColumns = @JoinColumn(name="user_id"), // 설정 안하면 테이블에 user_user_id 로 컬럼명 생성
            inverseJoinColumns = @JoinColumn(name="authority_name")
    )
    private Set<Authority> authorities;
}
