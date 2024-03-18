package in.ineuron.services.impl;

import in.ineuron.services.OTPStorageService;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OTPStorageServiceImpl implements OTPStorageService {
    private static final long CLEANUP_INTERVAL_MINUTES = 1;
    private static final long OTP_EXPIRY_DURATION_MILLIS = 600_000; // 10 minutes
    private final Map<String, OTPEntry> otpMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public OTPStorageServiceImpl() {
        // Schedule a cleanup task to remove expired OTPs every 1 minute.
        executorService.scheduleAtFixedRate(this::cleanUpExpiredOTP, CLEANUP_INTERVAL_MINUTES, CLEANUP_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    public void storeOTP(String userName, String otp) {
        OTPEntry otpEntry = new OTPEntry(otp, System.currentTimeMillis());
        otpMap.put(userName, otpEntry);
        // Log statement: System.out.println("OTP stored for user: " + userName);
    }

    public String getStoredOTP(String userName) {
        OTPEntry otpEntry = otpMap.get(userName);
        return (otpEntry != null) ? otpEntry.getOtp() : null;
    }

    @Override
    public boolean verifyOTP(String userName, String otp) {
        String storedOTP = getStoredOTP(userName);
        return otp.equals(storedOTP);

    }

    @Override
    public void removeOTP(String userName) {
        otpMap.remove(userName);
        // Log statement: System.out.println("OTP removed for user: " + userName);
    }

    private void cleanUpExpiredOTP() {
        long currentTime = System.currentTimeMillis();
        // Iterate through the OTP map and remove entries older than the expiration duration.
        otpMap.entrySet().removeIf(entry -> (currentTime - entry.getValue().getCreationTime() > OTP_EXPIRY_DURATION_MILLIS));

    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    // Inner class representing an OTP entry
    @Getter
    private static class OTPEntry {
        private final String otp;
        private final long creationTime;

        public OTPEntry(String otp, long creationTime) {
            this.otp = otp;
            this.creationTime = creationTime;
        }

    }
}
