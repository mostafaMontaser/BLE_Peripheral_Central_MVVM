package com.swenson.blechat.extention

import android.util.Patterns
import com.swenson.blechat.util.parsing.ParsingHelper
import java.lang.reflect.Type

fun <T> String.convertToModel(type: Type): T? {
    return ParsingHelper.gson?.fromJson<T>(this, type)}

fun String?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()



