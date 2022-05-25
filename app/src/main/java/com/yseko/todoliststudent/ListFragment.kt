package com.yseko.todoliststudent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.yseko.todoliststudent.databinding.FragmentListBinding


class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels{
        TodoViewModelFactory(
            (activity?.application as TodoApplication).database.todoDao()
        )
    }

    private val navigationArgs: ListFragmentArgs by navArgs()

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

        if(navigationArgs.filter == "All") {
            viewModel.allItems.observe(this.viewLifecycleOwner) { todos ->
                todos.let {
                    adapter.submitList(it)
                }
            }
        }else{
            viewModel.getByCategory(navigationArgs.filter).observe(this.viewLifecycleOwner){todos->
                todos.let{
                    adapter.submitList(it)
                }
            }
        }

        binding.floatingButton.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToAddFragment(getString(R.string.add_fragment_title))
            this.findNavController().navigate(action)
        }

        binding.resetBtn.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentSelf()
            this.findNavController().navigate(action)
        }


        val spinner: Spinner = binding.filter

        viewModel.allCategories.observe(this.viewLifecycleOwner){categories->
            val list = categories.toMutableList()
            list.add(0,"All")
            spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, list)
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            var count = 0
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(count >=1) {

                    val action = ListFragmentDirections.actionListFragmentSelf(
                        spinner.getItemAtPosition(position).toString(), position
                    )
                    findNavController().navigate(action)
                }
                count++
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }
}