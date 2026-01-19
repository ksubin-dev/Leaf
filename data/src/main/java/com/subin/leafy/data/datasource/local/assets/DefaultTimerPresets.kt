package com.subin.leafy.data.datasource.local.assets

import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.TimerPreset

object DefaultTimerPresets {
    val list = listOf(
        TimerPreset(
            id = "DEFAULT_GREEN",
            name = "표준 녹차",
            teaType = TeaType.GREEN,
            isDefault = true,
            recipe = BrewingRecipe(
                waterTemp = 75,
                brewTimeSeconds = 180, // 3분
                leafAmount = 3f,
                waterAmount = 150,
                infusionCount = 1,
                teaware = TeawareType.KYUSU
            )
        ),
        TimerPreset(
            id = "DEFAULT_BLACK",
            name = "진한 홍차",
            teaType = TeaType.BLACK,
            isDefault = true,
            recipe = BrewingRecipe(
                waterTemp = 95,
                brewTimeSeconds = 180, // 3분
                leafAmount = 3f,
                waterAmount = 150,
                infusionCount = 1,
                teaware = TeawareType.MUG
            )
        ),
        TimerPreset(
            id = "DEFAULT_OOLONG",
            name = "향긋한 우롱차",
            teaType = TeaType.OOLONG,
            isDefault = true,
            recipe = BrewingRecipe(
                waterTemp = 90,
                brewTimeSeconds = 120,
                leafAmount = 4f,
                waterAmount = 150,
                infusionCount = 1,
                teaware = TeawareType.GAIWAN
            )
        )
    )
}