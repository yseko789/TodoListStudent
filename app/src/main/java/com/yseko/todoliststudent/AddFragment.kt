package com.yseko.todoliststudent

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.yseko.todoliststudent.data.Todo
import com.yseko.todoliststudent.databinding.FragmentAddBinding
import java.text.SimpleDateFormat
import java.util.*


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

    val cal = Calendar.getInstance()


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

//        val datePicker = binding.dateInput
//        val today = Calendar.getInstance()
//        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
//            today.get(Calendar.DAY_OF_MONTH)
//
//        ) { view, year, month, day ->
//            val month = month + 1
//            val msg = "You Selected: $day/$month/$year"
//            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
//        }

        val dateButton = binding.showDate
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        dateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        val spinner: Spinner = binding.categoryInput

        viewModel.allCategories.observe(this.viewLifecycleOwner){categories->
            spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        }
        spinner.setSelection(0)
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

        binding.addNewCategory.setOnClickListener{
            showDialog()
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
                    dateInput.text = todo.todoDate

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

    private fun showDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter new category")

        val input = EditText(requireContext())
        input.hint = "New category..."
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            viewModel.addCategory(input.text.toString())
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{dialog, which ->
            dialog.cancel()
        })
        builder.show()
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

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.dateInput.text = sdf.format(cal.time)
    }




}