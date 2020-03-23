package com.example.jeeek.utils

import org.junit.Test

import org.junit.Assert.*

class AuthUtilsTest {

    @Test
    fun validateForm() {
        val validEmail = "test@gmail.com"
        val validPass = "666666"

        val invalidEmail = ""
        val invalidPass1 = ""
        val invalidPass2 = "55555"

        assertEquals(validateForm(validEmail, validPass), true)
        assertEquals(validateForm(invalidEmail, validPass), false)
        assertEquals(validateForm(validEmail, invalidPass1), false)
        assertEquals(validateForm(validEmail, invalidPass2), false)
    }
}
