package com.bytepace.dimusco.ui.settings.general

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.score.ScoreRepository
//import com.bytepace.dimusco.navigation.SettingsNavigator
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class SettingsGeneralViewModel(aid: String) : ViewModel() {

    val settings: LiveData<Settings>
    val showPageSequence = ObservableBoolean()
    val sequence = ObservableField<String>()

    private val settingsRepository = SettingsRepository.get()
    private val scoreRepository = ScoreRepository.get()

    private var scoreLiveData: LiveData<Score>? = null
    private val scoreObserver = Observer<Score> { score = it }
    var score: Score? = null

    val haveSequence: Boolean
        get() {
            return !scoreRepository.currentScorePageSequence.isNullOrEmpty()
        }

    init {
        if (aid.isNotEmpty()) {
            scoreLiveData = scoreRepository.getScore(aid)
        }
        scoreLiveData?.observeForever(scoreObserver)
        settings = settingsRepository.settings
        val stringToSet = scoreRepository.currentScoreRawPageSequence
        sequence.set(stringToSet)
        this.showPageSequence.set(aid.isNotEmpty())
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun setScrollDirection(isHorizontal: Boolean) {
        viewModelScope.launch { settingsRepository.updateScrollDirection(isHorizontal) }
    }

    fun onPageSequenceButtonClick(): String {
        if (haveSequence) {
            if (scoreRepository.currentScoreRawPageSequence == sequence.get()) {
                scoreRepository.currentScorePageSequence = null
                scoreRepository.currentScoreRawPageSequence = null
                return ""
            }
        }
        try {
            scoreRepository.currentScoreRawPageSequence = sequence.get()
            val parsedSequence = parsePageSequenceString(sequence.get() ?: "")
            scoreRepository.currentScorePageSequence =
                if (parsedSequence.isNotEmpty()) parsedSequence else null
            return ""
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    private fun parsePageSequenceString(text: String): List<Int> {
        val scoreSize = score?.pages?.size ?: 0

        if (text.isEmpty() || text == "-")
            return (1..scoreSize).toList()

        val result = ArrayList<Int>()

        val nums = text.split(",")
        nums[0].isInt().let {
            if (nums.size == 1 && it != null && it > 0)
                return listOf(max(1, min(scoreSize, it)))
        }
        nums.forEach { s ->
            s.isInt().also {
                if (it != null && it > 0) {
                    result.add(max(1, min(scoreSize, it)))
                } else {
                    val range = s.split("-")
                    if (range.size == 1 || range.size > 2)
                        throw Exception("Wrong number")
                    else {
                        val num1 = range[0].let { if (it.isEmpty()) 1 else it.isInt() }
                        val num2 = range[1].let { if (it.isEmpty()) scoreSize else it.isInt() }
                        if (num1 == null || num2 == null)
                            throw Exception("Wrong number")
                        else if (num1 == num2)
                            throw Exception("Wrong array: $num1-$num2")
                        else result.addAll(
                            if (num1 < num2)
                                (num1..num2).toList()
                            else
                                (num1 downTo num2).toList()
                        )
                    }
                }
            }
        }

        return result.toList()
    }

    private fun String.isInt(): Int? = try {
        toInt()
    } catch (e: Exception) {
        null
    }

    override fun onCleared() {
        super.onCleared()
        scoreLiveData?.removeObserver(scoreObserver)
    }



}