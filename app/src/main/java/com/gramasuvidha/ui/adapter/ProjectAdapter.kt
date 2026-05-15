package com.gramasuvidha.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.gramasuvidha.R
import com.gramasuvidha.data.model.Project
import com.gramasuvidha.utils.Constants

/**
 * RecyclerView Adapter for displaying project cards.
 * Supports both Citizen view (read-only) and Admin view (edit/delete).
 */
class ProjectAdapter(
    private val isAdmin: Boolean,
    private val onItemClick: (Project) -> Unit,
    private val onEditClick: ((Project) -> Unit)?,
    private val onDeleteClick: ((Project) -> Unit)?
) : ListAdapter<Project, ProjectAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_card, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project)
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_project_name)
        private val tvType: TextView = itemView.findViewById(R.id.tv_project_type)
        private val tvBudget: TextView = itemView.findViewById(R.id.tv_budget)
        private val tvEndDate: TextView = itemView.findViewById(R.id.tv_end_date)
        private val tvProgress: TextView = itemView.findViewById(R.id.tv_progress)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val chipStatus: Chip = itemView.findViewById(R.id.chip_status)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(project: Project) {
            tvName.text = project.name
            tvType.text = Constants.getLocalizedType(itemView.context, project.type)
            tvBudget.text = Constants.formatBudget(project.budget)
            tvEndDate.text = project.endDate
            tvProgress.text = "${project.progress}%"
            tvLocation.text = project.location

            // Progress bar reflects exact database percentage
            progressBar.max = 100
            progressBar.progress = project.progress

            // Status chip styling
            chipStatus.text = Constants.getLocalizedStatus(itemView.context, project.status)
            chipStatus.setChipBackgroundColorResource(
                when (project.status) {
                    "Completed" -> R.color.status_completed
                    "In Progress" -> R.color.status_in_progress
                    else -> R.color.status_not_started
                }
            )

            // Admin actions visibility
            if (isAdmin) {
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                btnEdit.setOnClickListener { onEditClick?.invoke(project) }
                btnDelete.setOnClickListener { onDeleteClick?.invoke(project) }
            } else {
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(project) }
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }
}
