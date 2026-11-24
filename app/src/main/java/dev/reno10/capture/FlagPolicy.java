package dev.reno10.capture;

final class FlagPolicy {
    // Android WindowManager.LayoutParams.FLAG_SECURE.
    static final int SECURE = 0x00002000;
    private FlagPolicy() {}
    static int withoutSecure(int flags) { return flags & ~SECURE; }
    static int flagsForMask(int flags, int mask) {
        return (mask & SECURE) != 0 ? withoutSecure(flags) : flags;
    }
}
