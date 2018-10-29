package io.github.externschool.planner.util;

import java.text.Collator;
import java.util.Locale;

public final class CollatorHolder {

    public static Collator getUaCollator() {
        return  Collator.getInstance(new Locale("uk", "UA"));
    }
}
