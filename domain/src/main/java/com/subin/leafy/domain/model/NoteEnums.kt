package com.subin.leafy.domain.model

enum class WeatherType {
    SUNNY, CLOUDY, RAINY, SNOWY, INDOOR
}

enum class BodyType {
    LIGHT, MEDIUM, FULL
}

enum class TeaType(val label: String) {
    GREEN("녹차"),
    BLACK("홍차"),
    OOLONG("우롱차"),
    WHITE("백차"),
    YELLOW("황차"),
    PUERH("보이차"),
    HERBAL("허브차/대용차"),
    ETC("기타")
}