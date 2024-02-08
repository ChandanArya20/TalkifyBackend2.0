package in.ineuron.dto;


import lombok.Data;

@Data
public class UserUpdateRequest {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String profileImage;

    private String about;

}
