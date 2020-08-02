package ev.aykhan.textrecognizer.ui.fragment.home

import androidx.recyclerview.widget.DiffUtil
import ev.aykhan.textrecognizer.R
import ev.aykhan.textrecognizer.model.entity.ExtractedText
import ev.aykhan.textrecognizer.utils.adapter.DataBindingRecyclerAdapter

class ExtractedTextsRecyclerViewAdapter(
    clickListener: (item: ExtractedText, position: Int) -> Unit,
    holdListener: (item: ExtractedText, position: Int) -> Unit
) : DataBindingRecyclerAdapter<ExtractedText>(
    ExtractedTextsDiffCallback,
    clickListener,
    holdListener
) {

    object ExtractedTextsDiffCallback : DiffUtil.ItemCallback<ExtractedText>() {
        override fun areItemsTheSame(oldItem: ExtractedText, newItem: ExtractedText): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExtractedText, newItem: ExtractedText): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_extracted_text

}