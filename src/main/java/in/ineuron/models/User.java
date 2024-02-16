package in.ineuron.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;


@Data
@Entity
public class User {

	@Id
//	@GenericGenerator(name = "gen",strategy = "in.ineuron.idgenerator.IdGenerator")
//	@GeneratedValue(generator = "gen")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(unique = true)
	private String phone;

	private String profileImage;

	private String about;

	@Column(nullable = false)
	private String password;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(phone, user.phone) && Objects.equals(password, user.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, email, phone, password);
	}
}





