package Myaong.Gangajikimi.member.entity;

import java.util.ArrayList;
import java.util.List;

import Myaong.Gangajikimi.auth.web.dto.SignupRequest;
import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.AccountStatus;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlostreport.entity.PostLostReport;
import Myaong.Gangajikimi.sightcard.entity.SightCard;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String memberName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column
	private String password;

	@Column(nullable = false)
	@ColumnDefault("'ROLE_USER'")
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	@ColumnDefault("'ACTIVATED'")
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;

	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostLost> postLosts = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostFound> postFounds = new ArrayList<>();

	@OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<SightCard> sightCards = new ArrayList<>();

	@OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostLostReport> postLostReports = new ArrayList<>();

	@OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<PostFoundReport> postFoundReports = new ArrayList<>();

	@OneToMany(mappedBy = "member1", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChatRoom> ChatRooms = new ArrayList<>();

	@OneToMany(mappedBy = "member2", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChatRoom> ChatRooms2 = new ArrayList<>();


	@Builder
	private Member(String memberName, String email, String password, String gender, Integer age, String address, String specAddress){

		this.memberName = memberName;
		this.email = email;
		this.password = password;
		this.role = Role.USER;
		this.accountStatus = AccountStatus.ACTIVATED;
	}

	public static Member of(SignupRequest request, String encryptedPassword){

		return Member.builder()
			.memberName(request.getMemberName())
			.email(request.getEmail())
			.password(encryptedPassword)
			.build();
	}

	public void changeStatus(String status) {
		this.accountStatus = AccountStatus.valueOf(status.toUpperCase());
	}


}
