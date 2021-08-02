package com.xooloo.r8bug.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.xooloo.r8bug.core.ext.collect
import com.xooloo.r8bug.core.ui.BindingFragment
import com.xooloo.r8bug.data.ChatRepository
import com.xooloo.r8bug.databinding.ChatsFragmentBinding
import com.xooloo.r8bug.util.ListEntryAdapter
import com.xooloo.r8bug.util.ScopedViewBindingHolder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BindingFragment<ChatsFragmentBinding>() {

    private val viewModel by viewModels<Model>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = Adapter(viewLifecycleOwner.lifecycleScope)

        viewModel.chats.collect(viewLifecycleOwner) { data ->
            if (data.isEmpty()) {
                // show error indicator
            }

            Timber.i("submit data: %s", data.size)
            adapter.submitList(data)
        }

        binding.chats.adapter = adapter
        binding.chats.setHasFixedSize(true)
    }

    @HiltViewModel
    internal class Model @Inject constructor(
        private val repository: ChatRepository
    ) : ViewModel() {

        val chats: Flow<List<ChatListEntry>> by lazy {
            repository.chats().map {
                it
                    .filter { chat -> chat.chat.lastMessageDate > 0 }
                    .map { chat -> ChatListEntry(chat, repository) }
            }
        }
    }

    private class Adapter(scope: LifecycleCoroutineScope) : ListEntryAdapter<ChatListEntry>(scope) {

        override fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int,
            scope: LifecycleCoroutineScope
        ): ScopedViewBindingHolder<*> {
            return ChatListEntry.onCreateViewHolder(inflater, parent, scope)
        }

        override fun onBindViewHolder(holder: ScopedViewBindingHolder<*>, position: Int) {
            getItem(position).onBind(holder)
        }

    }
}
