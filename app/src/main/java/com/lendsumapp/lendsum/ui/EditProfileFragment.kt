package com.lendsumapp.lendsum.ui

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentEditProfileBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.EditProfileInfoType
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_EMAIL_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_IS_PROFILE_PUBLIC_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USERNAME_KEY
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private lateinit var updateUserStatusObserver: Observer<Int>
    private lateinit var updateAuthEmailStatusObserver: Observer<Boolean>
    private lateinit var updateAuthPassStatusObserver: Observer<Boolean>
    private var user: User = User()
    private val registerOnActivityResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri -> updateProfilePic(uri) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfilePic.setOnClickListener(this)
        binding.editProfileUpdatePassBtn.setOnClickListener(this)
        binding.editProfileBackBtn.setOnClickListener(this)
        binding.editProfileNameToggle.setOnCheckedChangeListener(this)
        binding.editProfileUsernameToggle.setOnCheckedChangeListener(this)
        binding.editProfileEmailToggle.setOnCheckedChangeListener(this)
        binding.editProfileVisibilityToggle.setOnCheckedChangeListener(this)


        editProfileViewModel.getCachedUser().observe(viewLifecycleOwner, Observer { cachedUser ->

            user = cachedUser

            loadCachedUserProfile(user)
        })

        updateAuthEmailStatusObserver = Observer { isAuthEmailUpdated ->
            if(isAuthEmailUpdated){
                editProfileViewModel.updateLocalCachedUser(user)
                editProfileViewModel.updateUserValueInFirestore(FIRESTORE_EMAIL_KEY, user.email)
            }else{
                AndroidUtils.showSnackBar(requireActivity(), getString(R.string.sign_in_again_msg))
            }
        }

        updateAuthPassStatusObserver = Observer { isAuthPassUpdated ->
            if(isAuthPassUpdated){
                binding.editProfilePasswordEt.setText("")
                binding.editProfileMatchPasswordEt.setText("")
                AndroidUtils.showSnackBar(requireActivity(), getString(R.string.password_has_updated))
            }else{
                AndroidUtils.showSnackBar(requireActivity(), getString(R.string.sign_in_again_msg))
            }

        }

        updateUserStatusObserver = Observer { rowsUpdated->
            if(rowsUpdated > 0){
                Log.d(TAG, "User object updated in room cache")
                AndroidUtils.showSnackBar(requireActivity(), getString(R.string.user_profile_updated))
            }else{
                Log.d(TAG, "User object not updated in room cache")
            }
        }
        editProfileViewModel.getUpdateCacheUserStatus().observe(viewLifecycleOwner, updateUserStatusObserver)
    }

    private fun loadCachedUserProfile(user: User?) {

        user?.let {
            if(!user.isProfilePublic){
                binding.editProfileVisibilityToggle.setOnCheckedChangeListener(null)
                binding.editProfileVisibilityToggle.isChecked = true
                handleUpdateInfoUI(binding.editProfileVisibilityToggle.isChecked, null, null, binding.editProfileVisibilityToggle)
                binding.editProfileVisibilityToggle.setOnCheckedChangeListener(this)
            }

            binding.editProfileNameTv.text = it.name
            binding.editProfileUsernameTv.text = it.username
            binding.editProfileEmailTv.text = it.email

            loadProfilePic(it.profilePicUri!!, binding.editProfilePic)
        }
    }

    private fun loadProfilePic(profilePicUri: String, imageView: ImageView) {
        Glide.with(this)
            .load(profilePicUri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .error(R.drawable.com_facebook_profile_picture_blank_portrait))
            .circleCrop()
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when(button?.id){
            R.id.edit_profile_name_toggle -> {

                handleUpdateInfoUI(isChecked, binding.editProfileNameTv, binding.editProfileNameEt, binding.editProfileNameToggle)
                handleUpdateInfoData(isChecked, binding.editProfileNameTv, EditProfileInfoType.PROFILE_NAME.ordinal)

            }
            R.id.edit_profile_username_toggle -> {

                handleUpdateInfoUI(isChecked, binding.editProfileUsernameTv, binding.editProfileUsernameEt, binding.editProfileUsernameToggle)
                handleUpdateInfoData(isChecked, binding.editProfileUsernameTv, EditProfileInfoType.PROFILE_USERNAME.ordinal)

            }
            R.id.edit_profile_email_toggle -> {

                handleUpdateInfoUI(isChecked, binding.editProfileEmailTv, binding.editProfileEmailEt, binding.editProfileEmailToggle)
                handleUpdateInfoData(isChecked, binding.editProfileEmailTv, EditProfileInfoType.PROFILE_EMAIL.ordinal)

            }
            R.id.edit_profile_visibility_toggle -> {

                handleUpdateInfoUI(isChecked, null, null, binding.editProfileVisibilityToggle)
                handleUpdateInfoData(isChecked, binding.editProfileVisibilityTv, EditProfileInfoType.PROFILE_VISIBILITY.ordinal)
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.edit_profile_pic -> {
                openGalleryForImage()
            }
            R.id.edit_profile_update_pass_btn -> {

                AndroidUtils.hideKeyboard(requireActivity())

                val password = binding.editProfilePasswordEt.text.toString().trim()
                val passwordMatch = binding.editProfileMatchPasswordEt.text.toString().trim()

                if(NetworkUtils.isNetworkAvailable(requireContext())) {
                    if (isPasswordValidated(password, passwordMatch)) {
                        editProfileViewModel.getUpdateAuthPassStatus()
                            .observe(viewLifecycleOwner, updateAuthPassStatusObserver)
                        editProfileViewModel.updateAuthPass(password)
                    }
                }else{
                    AndroidUtils.showSnackBar(requireActivity(), getString(R.string.not_connected_internet))
                }
            }
            R.id.edit_profile_back_btn -> {
                clearEditTextFocus()
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            }
        }
    }

    private fun clearEditTextFocus(){
        binding.editProfileEmailEt.clearFocus()
        binding.editProfileNameEt.clearFocus()
        binding.editProfilePasswordEt.clearFocus()
        binding.editProfileMatchPasswordEt.clearFocus()
    }

    private fun isPasswordValidated(password: String, passwordMatch: String):Boolean{
        when {
            TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordMatch) -> {
                binding.editProfilePasswordEt.error = getString(R.string.blank_pass_no_update)
                binding.editProfileMatchPasswordEt.error = getString(R.string.blank_pass_no_update)
                return false
            }
            !AndroidUtils.isValidPassword(password) -> {
                binding.editProfilePasswordEt.error = getString(R.string.password_param_err_msg)
                return false
            }
            password != passwordMatch -> {
                binding.editProfilePasswordEt.error = getString(R.string.pass_dont_match_err_msg)
                binding.editProfileMatchPasswordEt.error = getString(R.string.pass_dont_match_err_msg)
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun openGalleryForImage() {
        registerOnActivityResult.launch("image/*")
    }

    private fun updateProfilePic(it: Uri) {
        loadProfilePic(it.toString(), binding.editProfilePic)

        uploadProfilePicToFirebaseStorage(it)

        user.profilePicUri = it.toString()
        editProfileViewModel.updateLocalCachedUser(user)
    }

    private fun uploadProfilePicToFirebaseStorage(uri: Uri) {
        editProfileViewModel.uploadProfilePhoto(uri)
    }

    private fun handleUpdateInfoUI(isChecked: Boolean, textView: TextView?, editText: EditText?, toggleButton: ToggleButton) {
        if(isChecked) {
            textView?.let { AndroidUtils.hideView(it) }
            editText?.let { AndroidUtils.showView(it) }
            editText?.setText(textView?.text)
            toggleButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryLight))
            toggleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccentBlack))
        }else{
            textView?.let { AndroidUtils.hideKeyboard(requireActivity()) }

            textView?.text = editText?.text
            textView?.let { AndroidUtils.showView(it) }
            editText?.let { AndroidUtils.hideView(it) }
            toggleButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            toggleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryLight))
        }
    }

    private fun handleUpdateInfoData(isChecked: Boolean, textView: TextView, infoType: Int){
        if(!isChecked){
            when(infoType){
                EditProfileInfoType.PROFILE_NAME.ordinal -> {
                    when {
                        TextUtils.isEmpty(textView.text) -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.empty_profile_name))
                        }
                        textView.text.length < 3 -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.username_too_short_err))
                        }
                        else -> {
                            user.name = textView.text.toString().trim()
                            editProfileViewModel.updateLocalCachedUser(user)
                            editProfileViewModel.updateUserValueInFirestore(FIRESTORE_PROFILE_NAME_KEY, user.name)
                            editProfileViewModel.updateFirebaseAuthProfile(FIRESTORE_PROFILE_NAME_KEY, user.name)
                        }
                    }
                }
                EditProfileInfoType.PROFILE_USERNAME.ordinal -> {
                    when {
                        TextUtils.isEmpty(textView.text) -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.empty_username))
                        }
                        !textView.text.startsWith("@") -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.no_at_symbol_err))
                        }
                        textView.text.length < 4 -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.username_too_short_err))
                        }
                        else -> {
                            user.username = textView.text.toString().trim()
                            editProfileViewModel.updateLocalCachedUser(user)
                            editProfileViewModel.updateUserValueInFirestore(FIRESTORE_USERNAME_KEY, user.username)
                        }
                    }
                }
                EditProfileInfoType.PROFILE_EMAIL.ordinal -> {
                    when {
                        AndroidUtils.isValidEmail(textView.text.toString().trim()) && !TextUtils.isEmpty(textView.text) -> {
                            user.email = textView.text.toString().trim()
                            editProfileViewModel.getUpdateAuthEmailStatus().observe(viewLifecycleOwner, updateAuthEmailStatusObserver)
                            editProfileViewModel.updateAuthEmail(user.email)
                        }
                        else -> {
                            AndroidUtils.showSnackBar(requireActivity(), getString(R.string.invalid_email_data_not_saved))
                        }
                    }
                }
                EditProfileInfoType.PROFILE_VISIBILITY.ordinal -> {
                    user.isProfilePublic = true
                    editProfileViewModel.updateLocalCachedUser(user)
                    editProfileViewModel.updateUserValueInFirestore(FIRESTORE_IS_PROFILE_PUBLIC_KEY, true)
                }
            }
        }else{
            when(infoType){
                EditProfileInfoType.PROFILE_VISIBILITY.ordinal -> {
                    user.isProfilePublic = false
                    editProfileViewModel.updateLocalCachedUser(user)
                    editProfileViewModel.updateUserValueInFirestore(FIRESTORE_IS_PROFILE_PUBLIC_KEY, false)
                }
            }
        }
    }




    companion object {
        private val TAG = EditProfileFragment::class.simpleName
    }

}