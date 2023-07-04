package com.dilkw.studycodekotlin

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dilkw.studycodekotlin.mainRecyclerView.MyItemRecyclerViewAdapter
import com.dilkw.studycodekotlin.mainRecyclerView.PlaceholderContent
import com.dilkw.studycodekotlin.media.MediaPlayerActivity
import com.dilkw.studycodekotlin.service.ServiceActivity

class ItemFragment : Fragment() {

    private var columnCount = 1
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        navController = this.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        if (view is RecyclerView) {
            val recyclerView: RecyclerView = view
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS, object:
                    MyItemRecyclerViewAdapter.ItemClickListener {
                        override fun itemOnClick(itemEnum: ItemEnum) {
                            when(itemEnum) {
                                ItemEnum.ANIMATION -> navController?.navigate(R.id.action_itemFragment_to_animationFragment)
                                ItemEnum.SERVICES -> navController?.navigate(R.id.action_itemFragment_to_animationFragment)
                                ItemEnum.WEB_VIEW -> navController?.navigate(R.id.action_itemFragment_to_webViewFragment)
                                ItemEnum.SYSTEM_BARS -> navController?.navigate(R.id.action_itemFragment_to_systemBarsFragment)
                                ItemEnum.API -> navController?.navigate(R.id.action_itemFragment_to_httpDemoFragment)
                                ItemEnum.SERVICE -> requireActivity().startActivity(Intent(requireActivity(), ServiceActivity::class.java))
                                ItemEnum.MEDIA -> requireActivity().startActivity(Intent(requireActivity(), MediaPlayerActivity::class.java))
                                ItemEnum.CUSTOMIZE_VIEW -> navController?.navigate(R.id.action_itemFragment_to_customizeViewFragment)
                                ItemEnum.BUG_FIX -> navController?.navigate(R.id.action_itemFragment_to_bugFixFragment)
                                ItemEnum.COROUTINES -> navController?.navigate(R.id.action_itemFragment_to_coroutinesFragment)
                            }
                        }
                })
            }

            // 设置recyclerView中item之间的分割线
            val itemDecoration = object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.top = 20
                    if (parent.getChildAdapterPosition(view) == (recyclerView.adapter?.itemCount ?: 0) - 1) {
                        outRect.bottom = 20
                    }
                }
            }
            recyclerView.addItemDecoration(itemDecoration)
        }
        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            com.dilkw.studycodekotlin.ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(com.dilkw.studycodekotlin.ItemFragment.Companion.ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}