package in.ineuron.services;

public interface OTPStorageService {
    void storeOTP(String userName, String otp);

    String getStoredOTP(String userName);

    boolean verifyOTP(String userName, String otp);

    void removeOTP(String userName);
}

