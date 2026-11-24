package dev.reno10.capture;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import java.util.concurrent.atomic.AtomicBoolean;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/** App-local compatibility only. No system-server hooks or detector suppression. */
public final class CaptureModule implements IXposedHookLoadPackage {
    private static final String TAG = "[Reno10Capture] ";
    private static final AtomicBoolean INSTALLED = new AtomicBoolean();
    private static final AtomicBoolean CHANGED = new AtomicBoolean();

    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p) {
        // Require Android 15 and a real third-party app process selected in LSPosed.
        if (Build.VERSION.SDK_INT != 35 || p.appInfo == null || p.packageName == null) return;
        if (Process.myUid() < 10000 || p.appInfo.uid < 10000) return;
        if ((p.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) return;
        if (p.packageName.equals("dev.reno10.capture") || !INSTALLED.compareAndSet(false, true)) return;
        log("loaded " + p.packageName + " / " + p.processName + "; SDK=" + Build.VERSION.SDK_INT + "; model=" + Build.MODEL + "; build=" + Build.DISPLAY);

        install("Window.setFlags", () -> XposedHelpers.findAndHookMethod(Window.class,
            "setFlags", int.class, int.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    int original = (Integer) p.args[0];
                    int filtered = FlagPolicy.flagsForMask(original, (Integer) p.args[1]);
                    if (filtered != original) { p.args[0] = filtered; changed("Window.setFlags"); }
                }
            }));
        install("Window.setAttributes", () -> XposedHelpers.findAndHookMethod(Window.class,
            "setAttributes", WindowManager.LayoutParams.class, attributesHook(0, "Window.setAttributes")));

        // Optional ordinary view-window boundary; independent failures do not stop other hooks.
        install("WindowManagerImpl.addView", () -> XposedHelpers.findAndHookMethod(
            "android.view.WindowManagerImpl", null, "addView", View.class,
            ViewGroup.LayoutParams.class, attributesHook(1, "WindowManagerImpl.addView")));
        install("WindowManagerImpl.updateViewLayout", () -> XposedHelpers.findAndHookMethod(
            "android.view.WindowManagerImpl", null, "updateViewLayout", View.class,
            ViewGroup.LayoutParams.class, attributesHook(1, "WindowManagerImpl.updateViewLayout")));
    }

    private static XC_MethodHook attributesHook(final int index, final String source) {
        return new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                Object value = p.args[index];
                if (!(value instanceof WindowManager.LayoutParams)) return;
                WindowManager.LayoutParams attrs = (WindowManager.LayoutParams) value;
                int filtered = FlagPolicy.withoutSecure(attrs.flags);
                if (filtered != attrs.flags) { attrs.flags = filtered; changed(source); }
            }
        };
    }

    private interface Registration { void register() throws Throwable; }
    private static void install(String name, Registration registration) {
        try { registration.register(); log("hook OK: " + name); }
        catch (Throwable e) { log("hook FAILED: " + name + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")"); }
    }
    private static void changed(String source) {
        if (CHANGED.compareAndSet(false, true)) log("first ordinary window flag change: " + source);
    }
    private static void log(String message) { XposedBridge.log(TAG + message); }
}
