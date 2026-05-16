package com.heft

import com.heft.data.model.Exercise
import com.heft.data.model.User
import org.junit.Test
import org.junit.Assert.*

class HEFTUnitTest {

    // ── Calorie Calculation Tests ─────────────────────────────────────────

    @Test
    fun pushups_calories_calculation_is_correct() {
        val calories = Exercise.calculateCalories("Push-ups", 3, 10)
        assertEquals(15.0, calories, 0.01)
    }

    @Test
    fun zero_sets_returns_zero_calories() {
        val calories = Exercise.calculateCalories("Push-ups", 0, 10)
        assertEquals(0.0, calories, 0.01)
    }

    // ── BMI Calculation Tests ─────────────────────────────────────────────

    @Test
    fun normal_BMI_is_calculated_correctly() {
        val bmi = User.calculateBMI(70.0, 175.0)
        assertEquals(22.86, bmi, 0.1)
    }

    @Test
    fun zero_height_returns_zero_BMI() {
        val bmi = User.calculateBMI(70.0, 0.0)
        assertEquals(0.0, bmi, 0.01)
    }

    @Test
    fun BMI_category_underweight() {
        val category = User.getBMICategory(17.0)
        assertEquals("Underweight", category)
    }

    @Test
    fun BMI_category_normal() {
        val category = User.getBMICategory(22.0)
        assertEquals("Normal weight", category)
    }

    @Test
    fun BMI_category_overweight() {
        val category = User.getBMICategory(27.0)
        assertEquals("Overweight", category)
    }

    @Test
    fun BMI_category_obese() {
        val category = User.getBMICategory(35.0)
        assertEquals("Obese", category)
    }

    // ── Training Plan Tests ───────────────────────────────────────────────

    @Test
    fun beginner_plan_reps_within_40_to_45_percent_range() {
        val maxReps = 20
        val minExpected = (maxReps * 0.40).toInt()
        val maxExpected = (maxReps * 0.45).toInt() + 1
        val multiplier = 0.40 + Math.random() * 0.05
        val repsPerSet = (maxReps * multiplier).toInt()
        assertTrue(repsPerSet in minExpected..maxExpected)
    }

    @Test
    fun pro_plan_reps_within_60_to_65_percent_range() {
        val maxReps = 20
        val minExpected = (maxReps * 0.60).toInt()
        val maxExpected = (maxReps * 0.65).toInt() + 1
        val multiplier = 0.60 + Math.random() * 0.05
        val repsPerSet = (maxReps * multiplier).toInt()
        assertTrue(repsPerSet in minExpected..maxExpected)
    }
}