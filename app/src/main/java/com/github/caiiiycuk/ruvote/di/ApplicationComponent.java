package com.github.caiiiycuk.ruvote.di;

import java.util.concurrent.Executor;

import dagger.Component;

@Component(modules = {
        ApplicationModule.class,
})
@ApplicationScope
public interface ApplicationComponent {

    int roiSizePercent();

    Executor executor();

}