package com.lendsumapp.lendsum.util

import com.lendsumapp.lendsum.data.model.Bundle

object TestUtil {

    private val bundle1 = Bundle(1,
        "A",
        "B",
        "Bundle title A",
        "Description A",
        null,
        12345,
        null,
        null,
        null,
        "Adays",
        null,
        null,
        null,
        false,
        true)

    fun getFirstTestBundle(): Bundle{
        return bundle1
    }

    private val bundle2 = Bundle(2,
        "C",
        "D",
        "Bundle title C",
        "Description C",
        null,
        12345,
        null,
        null,
        null,
        "Cdays",
        null,
        null,
        null,
        false,
        true)

    fun getSecondTestBundle(): Bundle{
        return bundle2
    }
}