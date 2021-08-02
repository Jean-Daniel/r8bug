package com.xooloo.r8bug.main

import com.xooloo.r8bug.chats.ChatsFragment
import com.xooloo.r8bug.core.ui.SimpleFragmentActivity

class MainActivity : SimpleFragmentActivity<ChatsFragment>() {
    override fun onCreateFragment(): ChatsFragment {
        return ChatsFragment()
    }
}
