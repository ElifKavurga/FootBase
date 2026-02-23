package com.footbase.patterns.observer;

public interface Konu {

    void ekle(Gozlemci gozlemci);

    void cikar(Gozlemci gozlemci);

    void gozlemcileriBilgilendir();
}
