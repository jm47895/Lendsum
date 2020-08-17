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
        2345,
        null,
        null,
        "Adays",
        null,
        null,
        null,
        false,
        true,
        false)

    fun getFirstLendTestBundle(): Bundle{
        return bundle1
    }

    private val bundle2 = Bundle(2,
        "C",
        "D",
        "Bundle title C",
        "Description C",
        null,
        12345,
        3456,
        null,
        null,
        "Cdays",
        null,
        null,
        null,
        false,
        true,
        false)

    fun getSecondLendTestBundle(): Bundle{
        return bundle2
    }

    private val bundle3 = Bundle(3,
        "E",
        "F",
        "Bundle title E",
        "Description E",
        null,
        12345,
        4567,
        null,
        null,
        "Edays",
        null,
        null,
        null,
        false,
        false,
        false)

    fun getThirdBorrowTestBundle(): Bundle{
        return bundle3
    }

    private val bundle4 = Bundle(4,
        "G",
        "H",
        "Bundle title G",
        "Description G",
        null,
        12345,
        5678,
        null,
        null,
        "Gdays",
        null,
        null,
        null,
        false,
        false,
        false)

    fun getFourthBorrowTestBundle(): Bundle{
        return bundle4
    }
}