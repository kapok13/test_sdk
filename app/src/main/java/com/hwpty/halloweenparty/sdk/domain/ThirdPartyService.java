package com.hwpty.halloweenparty.sdk.domain;

import com.hwpty.halloweenparty.sdk.domain.entity.InputData;
import com.hwpty.halloweenparty.sdk.domain.entity.OutputData;

import org.jetbrains.annotations.Nullable;

public interface ThirdPartyService<IN extends InputData, OUT extends OutputData> {

    interface InitializationCallback<OUT extends OutputData> {

        void onInitialized(OUT data);

    }

    void initialize(IN input, @Nullable InitializationCallback<OUT> callback);

}

