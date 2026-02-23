import java.util.HashMap;

class Student {
    String uid;
    String name;
    int fineAmount;
    int currentBorrowCount;

    public Student(String uid, String name, int fineAmount, int currentBorrowCount) {
        this.uid = uid;
        this.name = name;
        this.fineAmount = fineAmount;
        this.currentBorrowCount = currentBorrowCount;
    }
}

class Asset {
    String assetId;
    String assetName;
    boolean available;
    int securityLevel;

    public Asset(String assetId, String assetName, boolean available, int securityLevel) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.available = available;
        this.securityLevel = securityLevel;
    }
}

class CheckoutRequest {
    String uid;
    String assetId;
    int hoursRequested;

    public CheckoutRequest(String uid, String assetId, int hoursRequested) {
        this.uid = uid;
        this.assetId = assetId;
        this.hoursRequested = hoursRequested;
    }
}

class ValidationUtil {

    public static void validateUid(String uid) {
        if (uid == null || uid.length() < 8 || uid.length() > 12 || uid.contains(" ")) {
            throw new IllegalArgumentException("Invalid UID");
        }
    }

    public static void validateAssetId(String assetId) {
        if (assetId == null || !assetId.startsWith("LAB-")) {
            throw new IllegalArgumentException("Invalid Asset ID");
        }

        String part = assetId.substring(4);

        for (int i = 0; i < part.length(); i++) {
            if (!Character.isDigit(part.charAt(i))) {
                throw new IllegalArgumentException("Invalid Asset ID");
            }
        }
    }

    public static void validateHours(int hrs) {
        if (hrs < 1 || hrs > 6) {
            throw new IllegalArgumentException("Invalid hours (1–6 allowed)");
        }
    }
}

class AssetStore {

    HashMap<String, Asset> map = new HashMap<>();

    public void addAsset(Asset a) {
        map.put(a.assetId, a);
    }

    public Asset findAsset(String assetId) {
        Asset a = map.get(assetId);

        if (a == null) {
            throw new NullPointerException("Asset not found: " + assetId);
        }

        return a;
    }

    public void markBorrowed(Asset a) {
        if (!a.available) {
            throw new IllegalStateException("Asset already borrowed");
        }

        a.available = false;
    }
}

class AuditLogger {

    public static void log(String msg) {
        System.out.println("LOG: " + msg);
    }

    public static void logError(Exception e) {
        System.out.println("ERROR: " + e.getMessage());
    }
}

class CheckoutService {

    HashMap<String, Student> studentMap;
    AssetStore store;

    public CheckoutService(HashMap<String, Student> studentMap, AssetStore store) {
        this.studentMap = studentMap;
        this.store = store;
    }

    public String checkout(CheckoutRequest req)
            throws IllegalArgumentException,
                   IllegalStateException,
                   SecurityException,
                   NullPointerException {

        ValidationUtil.validateUid(req.uid);
        ValidationUtil.validateAssetId(req.assetId);
        ValidationUtil.validateHours(req.hoursRequested);

        Student s = studentMap.get(req.uid);

        if (s == null) {
            throw new NullPointerException("Student not found");
        }

        if (s.fineAmount > 0) {
            throw new IllegalStateException("Pending fine exists");
        }

        if (s.currentBorrowCount >= 2) {
            throw new IllegalStateException("Borrow limit reached");
        }

        Asset a = store.findAsset(req.assetId);

        if (!a.available) {
            throw new IllegalStateException("Asset not available");
        }

        if (a.securityLevel == 3 && !s.uid.startsWith("KRG")) {
            throw new SecurityException("High security asset. Access denied.");
        }

        if (req.hoursRequested == 6) {
            System.out.println("Note: Max duration selected. Return strictly on time.");
        }

        if (a.assetName.contains("Cable") && req.hoursRequested > 3) {
            req.hoursRequested = 3;
            System.out.println("Policy applied: Cables can be issued max 3 hours. Updated to 3.");
        }

        store.markBorrowed(a);
        s.currentBorrowCount++;

        String receipt = "TXN-20260221-" + a.assetId + "-" + s.uid;

        return receipt;
    }
}

public class Main {

    public static void main(String[] args) {

        Student s1 = new Student("KRG20281", "Gagan", 0, 0);
        Student s2 = new Student("ABC12345", "Rahul", 100, 0);
        Student s3 = new Student("KRG11111", "Aman", 0, 2);

        HashMap<String, Student> studentMap = new HashMap<>();
        studentMap.put(s1.uid, s1);
        studentMap.put(s2.uid, s2);
        studentMap.put(s3.uid, s3);

        AssetStore store = new AssetStore();
        store.addAsset(new Asset("LAB-101", "HDMI Cable", true, 1));
        store.addAsset(new Asset("LAB-102", "Oscilloscope", true, 3));
        store.addAsset(new Asset("LAB-103", "LAN Cable", false, 1));

        CheckoutService service = new CheckoutService(studentMap, store);

        CheckoutRequest r1 = new CheckoutRequest("KRG20281", "LAB-101", 5);
        CheckoutRequest r2 = new CheckoutRequest("KRG20281", "LAB-XYZ", 4);
        CheckoutRequest r3 = new CheckoutRequest("ABC12345", "LAB-102", 2);

        CheckoutRequest[] requests = {r1, r2, r3};

        for (int i = 0; i < requests.length; i++) {

            CheckoutRequest req = requests[i];

            try {
                String receipt = service.checkout(req);
                System.out.println("SUCCESS: " + receipt);
            }
            catch (IllegalArgumentException e) {
                AuditLogger.logError(e);
            }
            catch (NullPointerException e) {
                AuditLogger.logError(e);
            }
            catch (SecurityException e) {
                AuditLogger.logError(e);
            }
            catch (IllegalStateException e) {
                AuditLogger.logError(e);
            }
            finally {
                AuditLogger.log("Audit: attempt finished for UID="
                        + req.uid + ", asset=" + req.assetId);
                System.out.println("----------------------------------");
            }
        }
    }
}