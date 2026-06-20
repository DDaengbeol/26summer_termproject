package com.example.excerption.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.ui.book.BookAdapter
import com.example.excerption.ui.book.BookDetailActivity
import com.example.excerption.ui.book.BookSearchActivity
import android.widget.ImageButton
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = BookAdapter { book ->
            val intent = Intent(requireContext(), BookDetailActivity::class.java)
                .putExtra(IntentKeys.BOOK_ID, book.id)
            startActivity(intent)
        }

        view.findViewById<RecyclerView>(R.id.bookRecyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            this.adapter = adapter
        }

        view.findViewById<ImageButton>(R.id.searchBookButton).setOnClickListener {
            startActivity(Intent(requireContext(), BookSearchActivity::class.java))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppContainer.bookRepository.observeBooks().collect { books ->
                    adapter.submitList(books)
                }
            }
        }
    }
}
