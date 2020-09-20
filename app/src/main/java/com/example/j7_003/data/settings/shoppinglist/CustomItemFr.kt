package com.example.j7_003.data.settings.shoppinglist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.j7_003.R
import com.example.j7_003.data.shoppinglist.ItemTemplate
import com.example.j7_003.data.shoppinglist.UserItemTemplateList
import com.example.j7_003.data.todolist.Task
import com.example.j7_003.data.todolist.TodoFr
import kotlinx.android.synthetic.main.dialog_add_task.view.*
import kotlinx.android.synthetic.main.fragment_custom_item.view.*
import kotlinx.android.synthetic.main.row_custom_item.view.*
import kotlinx.android.synthetic.main.row_task.view.tvName

class CustomItemFr : Fragment() {

    companion object{
        lateinit var myFragment: CustomItemFr
        lateinit var myAdapter: CustomItemAdapter
        lateinit var myRecycler: RecyclerView

        lateinit var userItemTemplateList: UserItemTemplateList

        var deletedItem: ItemTemplate? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myView = inflater.inflate(R.layout.fragment_custom_item, container, false)
        myRecycler = myView.recycler_view_customItems
        myFragment = this

        userItemTemplateList = UserItemTemplateList()

        /**
         * Adding Task via floating action button
         * Onclick-Listener opening the add-task dialog
         */
        myView.btnAddCustomItem.setOnClickListener {
            //todo change this into an add custom item dialog
            //inflate the dialog with custom view
            val myDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_task, null)

            //AlertDialogBuilder
            val myBuilder = activity?.let { it1 -> AlertDialog.Builder(it1).setView(myDialogView) }
            myBuilder?.setCustomTitle(layoutInflater.inflate(R.layout.title_dialog_add_task, null))

            //show dialog
            val myAlertDialog = myBuilder?.create()
            myAlertDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            myAlertDialog?.show()

            //adds listeners to confirmButtons in addTaskDialog
            val taskConfirmButtons = arrayListOf<Button>(
                myDialogView.btnConfirm1,
                myDialogView.btnConfirm2,
                myDialogView.btnConfirm3
            )

            taskConfirmButtons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    myAlertDialog?.dismiss()
                    val title = myDialogView.etxTitleAddTask.text.toString()
                    if(title.isEmpty()){

                    }else{
                        myRecycler.adapter?.notifyItemInserted(
                            TodoFr.todoListInstance.addFullTask(
                            Task(title, index+1, false)
                        ))
                    }
                }
            }

            myDialogView.etxTitleAddTask.requestFocus()
        }

        /**
         * Connecting Adapter, Layout-Manager and Swipe Detection to UI elements
         */

        myAdapter = CustomItemAdapter()
        myRecycler.adapter = myAdapter
        myRecycler.layoutManager = LinearLayoutManager(activity)
        myRecycler.setHasFixedSize(true)


        val swipeHelperLeft = ItemTouchHelper(SwipeToDeleteCustomItem(ItemTouchHelper.LEFT, myAdapter))
        swipeHelperLeft.attachToRecyclerView(myRecycler)
        val swipeHelperRight = ItemTouchHelper(SwipeToDeleteCustomItem(ItemTouchHelper.RIGHT, myAdapter))
        swipeHelperRight.attachToRecyclerView(myRecycler)


        return myView
    }

    //Deletes all checked tasks and animates the deletion

}
class SwipeToDeleteCustomItem(direction: Int,  val adapter: CustomItemAdapter): ItemTouchHelper
.SimpleCallback(0, direction){
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val parsed = viewHolder as CustomItemAdapter.CustomItemViewHolder
        CustomItemFr.userItemTemplateList.removeItem(parsed.itemView.tvName.text.toString())
        CustomItemFr.myAdapter.notifyItemRemoved(viewHolder.adapterPosition)
    }
}

class CustomItemAdapter :
    RecyclerView.Adapter<CustomItemAdapter.CustomItemViewHolder>(){

    override fun getItemCount() = CustomItemFr.userItemTemplateList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_item, parent, false)
        return CustomItemViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onBindViewHolder(holder: CustomItemViewHolder, position: Int) {

        val currentItem = CustomItemFr.userItemTemplateList[holder.adapterPosition]

        //changes design of task based on priority and being checked
        holder.itemView.tvName.text = currentItem.n
        holder.itemView.tvCategory.text = currentItem.c.name

        //User Interactions with Task List Item below
        /**
         * EDITING task
         * Onclick-Listener on List items, opening the edit-task dialog
         */

//        holder.itemView.tvName.setOnClickListener {
//
//            if(!TodoFragment.allowSwipe){
//                return@setOnClickListener
//            }
//            //inflate the dialog with custom view
//            val myDialogView = LayoutInflater.from(activity).inflate(
//                R.layout.dialog_add_task,
//                null)
//
//            //AlertDialogBuilder
//            val myBuilder = AlertDialog.Builder(activity).setView(myDialogView)
//            val editTitle = LayoutInflater.from(activity).inflate(
//                R.layout.title_dialog_add_task,
//                null)
//            editTitle.tvDialogTitle.text = "Edit task"
//            myBuilder.setCustomTitle(editTitle)
//
//            //show dialog
//            val myAlertDialog = myBuilder.create()
//            myAlertDialog.window?.setSoftInputMode(WindowManager
//                .LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//            myAlertDialog.show()
//
//            //write current task to textField
//            myDialogView.etxTitleAddTask.requestFocus()
//            myDialogView.etxTitleAddTask.setText(Database.getTask(holder.adapterPosition).title)
//            myDialogView.etxTitleAddTask.setSelection(myDialogView.etxTitleAddTask.text.length)
//
//            //adds listeners to confirmButtons in addTaskDialog
//            val taskConfirmButtons = arrayListOf<Button>(
//                myDialogView.btnConfirm1,
//                myDialogView.btnConfirm2,
//                myDialogView.btnConfirm3
//            )
//
//            //Three buttons to create tasks with priorities 1-3
//            taskConfirmButtons.forEachIndexed { index, button ->
//                button.setOnClickListener {
//                    myAlertDialog.dismiss()
//                    val newPos = Database.editTask(holder.adapterPosition, index + 1,
//                        myDialogView.etxTitleAddTask.text.toString(),
//                        Database.getTask(holder.adapterPosition).isChecked)
//                    this.notifyItemChanged(holder.adapterPosition)
//                    this.notifyItemMoved(holder.adapterPosition, newPos)
//                }
//            }
//
//        }
//
    }



    class CustomItemViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView)
}