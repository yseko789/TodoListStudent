package com.yseko.todoliststudent

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yseko.todoliststudent.data.Todo
import com.yseko.todoliststudent.databinding.FragmentAddBinding


class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    lateinit var todo: Todo

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

        binding.addBtn.setOnClickListener{
            addNewTodo()
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
            viewModel.addItem(
                binding.titleInput.text.toString(),
                binding.dateInput.text.toString(),
                binding.categoryInput.text.toString()
            )
            val action = AddFragmentDirections.actionAddFragmentToListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isInputValid():Boolean{
        return viewModel.isInputValid(
            binding.titleInput.text.toString(),
            binding.dateInput.text.toString(),
            binding.categoryInput.text.toString()
        )
    }


}