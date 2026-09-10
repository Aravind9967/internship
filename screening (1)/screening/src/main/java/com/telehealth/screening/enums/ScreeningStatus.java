package com.telehealth.screening.enums;

public enum ScreeningStatus {
    UPLOADED,        // ---nurse uploaded image
    CLASSIFIED,      //--- AI gave risk score
    WITH_SPECIALIST, //--- waiting in specialist queue
    CONFIRMED,       //--- specialist confirmed
    OVERRIDDEN,      //--- specialist overrode AI
    REJECTED         //---- bad image / rejected
}


