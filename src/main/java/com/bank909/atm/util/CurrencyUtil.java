package com.bank909.atm.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyUtil {

    public static String formatUSDCurrencyString(BigDecimal amount) {
        Locale us = new Locale("en", "US");
        Currency dollars = Currency.getInstance(us);
        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(us);
        return usdFormat.format(amount);
    }
}
