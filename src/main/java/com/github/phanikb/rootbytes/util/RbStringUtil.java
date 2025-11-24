/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import java.text.Collator;
import java.util.Locale;

public final class RbStringUtil {

    public static final Locale ROOT_LOCALE = Locale.ROOT;
    private static final Collator ROOT_COLLATOR = Collator.getInstance(ROOT_LOCALE);

    static {
        ROOT_COLLATOR.setStrength(Collator.PRIMARY);
    }

    private RbStringUtil() {}

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }

        return ROOT_COLLATOR.compare(str1, str2) == 0;
    }

    public static int compare(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return 0;
        }
        if (str1 == null) {
            return -1;
        }
        if (str2 == null) {
            return 1;
        }

        return ROOT_COLLATOR.compare(str1, str2);
    }
}
