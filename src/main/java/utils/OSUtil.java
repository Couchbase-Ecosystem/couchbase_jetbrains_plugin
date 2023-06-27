package utils;

public class OSUtil {

    public static final String MACOS_ARM = "macos-arm";
    public static final String MACOS_64 = "macos-64";
    public static final String WINDOWS_ARM = "windows-arm";
    public static final String WINDOWS_64 = "windows-64";
    public static final String LINUX_ARM = "linux-arm";
    public static final String LINUX_64 = "linux-64";
    private static String usersOS;

    public static String getOSArch() {
        if (usersOS == null) {
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch");

            if (os.contains("mac")) {
                if (arch.contains("arm")) {
                    usersOS = MACOS_ARM;
                } else {
                    usersOS = MACOS_64;
                }
            } else if (os.contains("windows")) {
                if (arch.contains("arm")) {
                    usersOS = WINDOWS_ARM;
                } else {
                    usersOS = WINDOWS_64;
                }
            } else {
                if (arch.contains("arm")) {
                    usersOS = LINUX_ARM;
                } else {
                    usersOS = LINUX_64;
                }
            }
        }
        return usersOS;
    }

    public static boolean isMacOS() {
        return (getOSArch().equals(MACOS_64) || getOSArch().equals(MACOS_ARM));
    }

    public static boolean isWindows() {
        return (getOSArch().equals(WINDOWS_64) || getOSArch().equals(WINDOWS_ARM));
    }
}
