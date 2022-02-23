package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.text.format.DateUtils.formatElapsedTime
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel: ViewModel() {
    companion object {
        private const val DONE = 0L

        private const val COUNTDOWN_PANIC_SECONDS = 10L

        private const val ONE_SECOND = 1000L

        private const val COUNTDOWN_TIME = 10000L

        private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)

        private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)

        private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)

        private val NO_BUZZ_PATTERN = longArrayOf(0)
    }

    enum class BuzzType(val pattern: LongArray){
        CORRECT(CORRECT_BUZZ_PATTERN),
        PANIC(PANIC_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private val timer: CountDownTimer
    // The current word
    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    private var _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    // The current score
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    private var _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz
    //List of Words
    private lateinit var wordList: MutableList<String>

    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }


    init {
        Timber.i("ViewModel Initialized")
        resetList()
        nextWord()
        _score.value = 0
        _eventGameFinished.value = false

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millsUntilFinished: Long) {
                _currentTime.value = millsUntilFinished/ ONE_SECOND
                if(millsUntilFinished/ ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinished.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }

        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("GameViewModel Cleared!")
        timer.cancel()
    }

    fun onGameFinishComplete() {
        _eventGameFinished.value = false
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}