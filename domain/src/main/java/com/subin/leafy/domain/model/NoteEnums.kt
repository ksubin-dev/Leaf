package com.subin.leafy.domain.model

enum class WeatherType {
    SUNNY, CLOUDY, RAINY, SNOWY, INDOOR
}

enum class BodyType {
    LIGHT, MEDIUM, FULL
}

enum class TeaType(val label: String) {
    GREEN("녹차"),
    MATCHA("말차"),
    BLACK("홍차"),
    OOLONG("우롱차"),
    WHITE("백차"),
    YELLOW("황차"),
    PUERH("보이차"),
    HERBAL("허브차/대용차"),
    ETC("기타")
}

enum class FlavorTag(val label: String) {
    FLORAL("꽃 향"),
    FRUITY("과일 향"),
    GREENISH("풀 향"),
    NUTTY("고소한 향"),
    WOODY("나무 향"),
    SPICY("향신료 향"),
    SMOKY("훈연 향"),
    EARTHY("흙 향"),
    CREAMY("크리미"),
    ROASTED("구운 향");
}