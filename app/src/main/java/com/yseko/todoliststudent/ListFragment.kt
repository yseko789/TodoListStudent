package com.yseko.todoliststudent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yseko.todoliststudent.data.Todo
import com.yseko.todoliststudent.databinding.FragmentListBinding


class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels{
        TodoViewModelFactory(
            (activity?.application as TodoApplication).database.todoDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TodoAdapter(
            {
            val action = ListFragmentDirections.actionListFragmentToAddFragment(getString(R.string.edit_fragment_title),it.todoId)
            this.findNavController().navigate(action)
            },
            {
                viewModel.removeTodo(it.todoId, it.todoTitle, it.todoDate,it.todoCategory)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        // attach observer here to all the todo
        viewModel.allItems.observe(this.viewLifecycleOwner) { todos ->
            todos.let{
                adapter.submitList(it)
            }
        }

        binding.floatingButton.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToAddFragment(getString(R.string.add_fragment_title))
            this.findNavController().navigate(action)
        }
    }
}