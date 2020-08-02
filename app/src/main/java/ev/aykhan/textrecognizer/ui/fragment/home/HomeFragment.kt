package ev.aykhan.textrecognizer.ui.fragment.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.theartofdev.edmodo.cropper.CropImage
import ev.aykhan.textrecognizer.R
import ev.aykhan.textrecognizer.databinding.FragmentHomeBinding
import ev.aykhan.textrecognizer.ui.dialog.loading.LoadingDialogFragment
import ev.aykhan.textrecognizer.utils.LOADING_TAG
import ev.aykhan.textrecognizer.utils.copyToClipboard
import ev.aykhan.textrecognizer.utils.states.UiState
import ev.aykhan.textrecognizer.viewModel.fragment.home.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = ExtractedTextsRecyclerViewAdapter(
        clickListener = { item, _ ->
            MaterialAlertDialogBuilder(context)
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message))
                .setNeutralButton(getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.delete)) { dialog, _ ->
                    viewModel.deleteExtractedText(item)
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.share)) { dialog, _ ->
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, item.content)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(sendIntent, null))
                    dialog.dismiss()
                }
                .show()
        },
        holdListener = { item, _ -> requireContext().copyToClipboard(item.id, item.content) }
    )

    private val loadingDialog = LoadingDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        configureRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeData()
        observeStates()
    }

    private fun bindUI(): Unit = with(binding) {
        lifecycleOwner = this@HomeFragment
        fab.setOnClickListener(::openImageCroppingActivity)
    }

    private fun configureRecyclerView(): Unit = with(binding) {
        recyclerViewExtractedTexts.adapter = adapter
        recyclerViewExtractedTexts.scheduleLayoutAnimation()
    }

    private fun observeData(): Unit = with(viewModel) {
        listOfExtractedTexts.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
    }

    private fun observeStates(): Unit = with(viewModel) {
        uiState.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    UiState.Loading -> loadingDialog.show(
                        requireActivity().supportFragmentManager,
                        LOADING_TAG
                    )
                    UiState.Finished -> loadingDialog.dismiss()
                }
            }
        })
    }

    private fun openImageCroppingActivity(view: View?) {
        CropImage.activity().start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                viewModel.processImage(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e(HomeFragment::class.java.name, error.message, error)
            }
        }
    }

}