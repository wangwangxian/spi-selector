package com.spiselector.context;

import org.springframework.util.StringUtils;

import java.util.concurrent.Callable;

public class SpiApplicationContext {

    private static ThreadLocal<String> spiCodeLocal = new ThreadLocal();

    public SpiApplicationContext() {
    }

    public static String getSpiCode() {
        return (String) spiCodeLocal.get();
    }

    public static void setSpiCode(String spiCode) {
        spiCodeLocal.set(spiCode);
    }

    public static String cleanCode() {
        String code = getSpiCode();
        spiCodeLocal.remove();
        return code;
    }

    public static Runnable spiWrap(Runnable delegete) {
        String spiCode = getSpiCode();
        return StringUtils.isEmpty(spiCode) ? delegete : () -> {
            try {
                setSpiCode(spiCode);
                delegete.run();
            } finally {
                cleanCode();
            }

        };
    }

    public <V> Callable<V> spiWrap(Callable<V> delegete) {
        String spiCode = getSpiCode();
        return StringUtils.isEmpty(spiCode) ? delegete : () -> {
            Object var2;
            try {
                setSpiCode(spiCode);
                var2 = delegete.call();
            } finally {
                cleanCode();
            }

            return (V) var2;
        };
    }
}
