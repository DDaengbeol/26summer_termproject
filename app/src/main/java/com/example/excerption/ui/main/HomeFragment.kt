package com.example.excerption.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.ui.book.BookAdapter
import com.example.excerption.ui.book.BookDetailActivity

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels {
        BookViewModelFactory(AppContainer.bookRepository)
    }
    private lateinit var bookAdapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.bookRecyclerView)

        // 클릭 리스너를 전달하여 BookAdapter 초기화
        bookAdapter = BookAdapter { clickedBook ->
            // 책 클릭 시 상세 화면으로 이동
            val intent = Intent(requireContext(), BookDetailActivity::class.java).apply {
                putExtra(IntentKeys.BOOK_ID, clickedBook.id)
            }
            startActivity(intent)
        }

        recyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.bookList.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
        }

        viewModel.loadBestsellers()
    }
}