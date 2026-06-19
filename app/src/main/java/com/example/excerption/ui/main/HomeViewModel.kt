package com.example.excerption.ui.main

import androidx.lifecycle.*
import com.example.excerption.data.repository.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: BookRepository) : ViewModel() {

    // DB의 데이터를 Flow에서 LiveData로 변환하여 UI에서 관찰 가능하게 함
    val bookList = repository.getAllBooks().asLiveData()

    // 화면 진입 시 알라딘에서 베스트셀러를 가져오는 함수
    fun loadBestsellers() {
        viewModelScope.launch {
            repository.fetchAndSaveAladinBooks("베스트셀러")
        }
    }
}