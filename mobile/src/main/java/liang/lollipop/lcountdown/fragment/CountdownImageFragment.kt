package liang.lollipop.lcountdown.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseHolder
import liang.lollipop.lcountdown.base.LSimpleAdapter
import liang.lollipop.lcountdown.bean.PhotoInfo
import liang.lollipop.lcountdown.databinding.FragmentCountdownImageBinding
import liang.lollipop.lcountdown.utils.PhotoAlbumHelper
import liang.lollipop.lcountdown.utils.lazyBind
import liang.lollipop.lcountdown.utils.onUI

/**
 * @author lollipop
 * @date 2020/5/3 16:10
 */
class CountdownImageFragment : LTabFragment() {

    companion object {
        private const val REQUEST_PERMISSION_READ = 233
    }

    private val binding: FragmentCountdownImageBinding by lazyBind()

    override fun getTitleId(): Int {
        return R.string.title_image_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_image_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.imageTabSelected
    }

    private var callback: Callback? = null
    private val photoAlbumHelper = PhotoAlbumHelper().onComplete {
        onLoadComplete()
    }.onError {
        onUI {
            onLoadError()
        }
    }
    private val adapter = ImageAdapter(photoAlbumHelper.data) {
        callback?.onImageSelected(photoAlbumHelper.get(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageListView.adapter = adapter
        binding.imageListView.layoutManager = GridLayoutManager(context, 4)
        bindClick(binding.permissionBtn, binding.ignoreBtn)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
        }
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    private fun initData() {
        requestPermissions()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.permissionBtn -> {
                if (!requestPermissions()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity?.requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_PERMISSION_READ
                        )
                    }
                }
            }

            binding.ignoreBtn -> {
                activity?.let {
                    binding.imageListView.visibility = View.VISIBLE
                    setBtnVisible(View.INVISIBLE)
                    photoAlbumHelper.initData(it)
                }
            }
        }
        super.onClick(v)
    }

    private fun requestPermissions(): Boolean {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (it.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    onLoadError()
                    return false
                }
            }
            binding.imageListView.visibility = View.VISIBLE
            setBtnVisible(View.INVISIBLE)
            photoAlbumHelper.initData(it)
        }
        return true
    }

    private fun setBtnVisible(value: Int) {
        binding.permissionBtn.visibility = value
        binding.ignoreBtn.visibility = value
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onLoadComplete() {
        photoAlbumHelper.data.add(0, PhotoInfo.Empty)
        adapter.notifyDataSetChanged()
    }

    private fun onLoadError() {
        binding.imageListView.visibility = View.INVISIBLE
        setBtnVisible(View.VISIBLE)
    }

    private class ImageHolder
    private constructor(
        view: View,
        private val clickCallback: (Int) -> Unit
    ) : BaseHolder<PhotoInfo>(view) {
        companion object {
            fun create(viewGroup: ViewGroup, clickCallback: (Int) -> Unit): ImageHolder {
                return ImageHolder(
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_photo, viewGroup, false), clickCallback
                )
            }
        }

        init {
            itemView.setOnClickListener {
                clickCallback.invoke(this.adapterPosition)
            }
        }

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        override fun onBind(bean: PhotoInfo) {
            bean.loadTo(imageView)
        }
    }

    private class ImageAdapter(
        data: ArrayList<PhotoInfo>,
        private val clickCallback: (Int) -> Unit
    ) : LSimpleAdapter<ImageHolder, PhotoInfo>(data) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            return ImageHolder.create(parent, clickCallback)
        }
    }

    interface Callback {
        fun onImageSelected(info: PhotoInfo)
    }

}