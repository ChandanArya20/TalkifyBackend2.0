package in.ineuron.dto;


import lombok.Data;

@Data
public class UserUpdateRequest {

    private String id;

    private String name;

    private String userid;

//    private String email;

    private String phone;

    private String profileImage;

    private String about;

}
