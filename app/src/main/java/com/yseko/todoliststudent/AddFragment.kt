package com.yseko.todoliststudent

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.yseko.todoliststudent.data.Todo
import com.yseko.todoliststudent.databinding.FragmentAddBinding


class AddFragment : Fragment() {

    private val navigationArgs: AddFragmentArgs by navArgs()


    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    lateinit var todo: Todo
    lateinit var selectedCategory: String
    var selectedCategoryPos: Int = 0
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
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.categoryInput
        if(spinner!=null){
            viewModel.allCategories.observe(this.viewLifecycleOwner){categories->
                spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            }
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = spinner.getItemAtPosition(position) as String
                    selectedCategoryPos = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }


        if(navigationArgs.todoId == -1) {
            binding.addBtn.setOnClickListener {
                addNewTodo()
            }
        }
        else{
            viewModel.getTodo(navigationArgs.todoId).observe(this.viewLifecycleOwner) { selectedTodo ->
                todo = selectedTodo

                binding.apply {
                    titleInput.setText(todo.todoTitle)
                    dateInput.setText(todo.todoDate)

                    //--might need to change
                    categoryInput.setSelection(selectedCategoryPos)
                    //----

                    addBtn.text = "Complete"
                    addBtn.setOnClickListener {
                        editTodo()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun addNewTodo(){
        if(isInputValid()){
            viewModel.addTodo(
                binding.titleInput.text.toString(),
                binding.dateInput.text.toString(),
                selectedCategory
            )
            val action = AddFragmentDirections.actionAddFragmentToListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isInputValid():Boolean{
        return viewModel.isInputValid(
            binding.titleInput.text.toString(),
            binding.dateInput.text.toString()
        )
    }

    private fun editTodo(){
        if(isInputValid()){
            viewModel.editTodo(
                navigationArgs.todoId,
                binding.titleInput.text.toString(),
                binding.dateInput.text.toString(),
                selectedCategory
            )
            val action = AddFragmentDirections.actionAddFragmentToListFragment()
            findNavController().navigate(action)
        }
    }




}