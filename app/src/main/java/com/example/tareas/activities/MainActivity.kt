package com.example.tareas.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tareas.R
import com.example.tareas.adapters.TaskAdapter
import com.example.tareas.data.Task
import com.example.tareas.data.TaskDAO
import com.example.tareas.databinding.ActivityMainBinding
import com.example.tareas.databinding.DialogTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: TaskAdapter

    lateinit var taskDAO: TaskDAO


    var taskList = listOf<Task>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDAO = TaskDAO(this)

        taskList = taskDAO.findAll()

        adapter = TaskAdapter(taskList, ::onItemClick, ::onItemCheck, ::onItemEdit, ::onItemDelete)


        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        binding.addTaskButton.setOnClickListener {
            val task = Task(-1, "")
            showTaskDialog(task)

        }
    }

    fun reloadData() {
        // TODO: Se puede optimizar
        taskList = taskDAO.findAll()
        adapter.updateData(taskList)

    }

    fun showTaskDialog(task: Task) {
        val dialogBinding = DialogTaskBinding.inflate(layoutInflater)

        dialogBinding.titleTextField.editText!!.setText(task.title)

        val dialogTitle = if (task.id == -1) "Nueva tarea" else "Editar tarea"

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, which ->
                // Aqui no hacemos nada, lo dejamos para despues de mortar el dialogo
                // Es para evitar que se cierre automaticamente

            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = dialogBinding.titleTextField.editText?.text.toString()
            if (title.isEmpty()) {
                dialogBinding.titleTextField.error = "No se pueden crear tareas vacías"
            } else {
                task.title = title
                taskDAO.save(task)
                reloadData()

                dialog.dismiss()
            }

        }
    }

    fun onItemClick(position: Int) {
        onItemCheck(position)

    }

    fun onItemCheck(position: Int) {
        val task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)

        reloadData()

    }

    fun onItemEdit(position: Int) {
        val task = taskList[position]
        showTaskDialog(task)

    }

    fun onItemDelete(position: Int) {
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Borrar tarea")
            .setMessage("¿Estas seguro de querer borrar la tarea?")
            .setPositiveButton("Borrar") { dialog, which ->
                val task = taskList[position]
                taskDAO.delete(task)
                reloadData()

            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }


        val dialog = builder.create()
        dialog.show()
    }
}

//Codigo para probar el DAO

/*
val taskDAO = TaskDAO(this)

val task1 = Task(-1, "Comprar leche")
val task2 = Task(-1, "Comprar alcohol")
val task3 = Task(-1, "Comprar preservativos")

taskDAO.insert(task1)
taskDAO.insert(task2)
taskDAO.insert(task3)

taskDAO.findAll().forEach {
    Log.i("TEST_DB", it.toString())
}

val task4 = taskDAO.findById(1)
if (task4 != null) {
    task4.title = "Comprar leche entera"
    taskDAO.update(task4)
}

val task5 = taskDAO.findById(2)
if (task5 != null) {
    task5.done = true
    taskDAO.update(task5)
}

taskDAO.findAll().forEach {
    Log.i("TEST_DB", it.toString())
}


taskDAO.findAll().forEach {
    taskDAO.delete(it)
}

Log.i("TEST_DB", "Numero de tareas en BD: ${taskDAO.findAll().size}")
*/