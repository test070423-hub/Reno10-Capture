package dev.reno10.capture;
public final class FlagPolicyTest {
    private static void check(int actual, int expected) {
        if (actual != expected) throw new AssertionError("actual=" + actual + " expected=" + expected);
    }
    public static void main(String[] args) {
        int s = FlagPolicy.SECURE;
        check(FlagPolicy.withoutSecure(s | 0x80), 0x80);
        check(FlagPolicy.withoutSecure(0x80), 0x80);
        check(FlagPolicy.withoutSecure(-1), ~s);
        check(FlagPolicy.flagsForMask(s | 0x80, s), 0x80);
        check(FlagPolicy.flagsForMask(s | 0x80, 0x80), s | 0x80);
        check(FlagPolicy.flagsForMask(0, s), 0);
        System.out.println("6 flag-policy assertions passed");
    }
}
