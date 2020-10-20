package com.lendsumapp.lendsum.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentEditProfileBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.EditProfileInfoType
import com.lendsumapp.lendsum.util.GlobalConstants.EMAIL_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.USERNAME_KEY
import com.lendsumapp.lendsum.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    @Inject lateinit var androidUtils: AndroidUtils
    private lateinit var userCacheObserver: Observer<User>
    private lateinit var updateUserStatusObserver: Observer<Int>
    private lateinit var updateAuthEmailStatusObserver: Observer<Boolean>
    private lateinit var updateAuthPassStatusObserver: Observer<Boolean>
    private var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editProfileViewModel.getCachedUser()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.editProfilePic?.setOnClickListener(this)
        binding?.editProfileUpdatePassBtn?.setOnClickListener(this)
        binding?.editProfileBackBtn?.setOnClickListener(this)
        binding?.editProfileNameToggle?.setOnCheckedChangeListener(this)
        binding?.editProfileUsernameToggle?.setOnCheckedChangeListener(this)
        binding?.editProfileEmailToggle?.setOnCheckedChangeListener(this)

        userCacheObserver = Observer { cachedUser ->

            user = cachedUser

            binding?.editProfileNameTv?.text = cachedUser.name
            binding?.editProfileUsernameTv?.text = cachedUser.username
            binding?.editProfileEmailTv?.text = cachedUser.email

            loadProfilePic(cachedUser.profilePicUrl!!, binding?.editProfilePic!!)

        }
        editProfileViewModel.getUser().observe(viewLifecycleOwner, userCacheObserver)

        updateAuthEmailStatusObserver = Observer { isAuthEmailUpdated->
            if(isAuthEmailUpdated){
                editProfileViewModel.updateCachedUser(user)
                editProfileViewModel.updateUserValueInFirestore(EMAIL_KEY, user.email)
            }else{
                androidUtils.showSnackBar(requireActivity(), getString(R.string.sign_in_again_msg))
            }
        }

        updateAuthPassStatusObserver = Observer {   isAuthPassUpdated->
            if(isAuthPassUpdated){
                binding?.editProfilePasswordEt?.setText("")
                binding?.editProfileMatchPasswordEt?.setText("")
                androidUtils.showSnackBar(requireActivity(), getString(R.string.password_has_updated))
            }else{
                androidUtils.showSnackBar(requireActivity(), getString(R.string.sign_in_again_msg))
            }

        }

        updateUserStatusObserver = Observer { rowsUpdated->
            if(rowsUpdated > 0){
                Log.d(TAG, "User object updated in room cache")
                androidUtils.showSnackBar(requireActivity(), getString(R.string.user_profile_updated))
            }else{
                Log.d(TAG, "User object not updated in room cache")
            }
        }
        editProfileViewModel.getUpdateCacheUserStatus().observe(viewLifecycleOwner, updateUserStatusObserver)
    }

    private fun loadProfilePic(profilePicUri: String, imageView: ImageView) {
        Glide.with(this)
            .applyDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .circleCrop()
            )
            .load(profilePicUri)
            .circleCrop()
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE){

            user.profilePicUrl = data?.data.toString()

            loadProfilePic(user.profilePicUrl.toString(), binding?.editProfilePic!!)

            editProfileViewModel.updateCachedUser(user)
            editProfileViewModel.updateUserValueInFirestore(PROFILE_PIC_URI_KEY, user.profilePicUrl.toString())
            editProfileViewModel.updateFirebaseAuthProfile(PROFILE_PIC_URI_KEY, user.profilePicUrl.toString())
        }
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when(button?.id){
            R.id.edit_profile_name_toggle->{

                handleUpdateInfoUI(isChecked, binding?.editProfileNameTv!!, binding?.editProfileNameEt!!, binding?.editProfileNameToggle!!)
                handleUpdateInfoData(isChecked, binding?.editProfileNameTv!!, EditProfileInfoType.PROFILE_NAME.ordinal)

            }
            R.id.edit_profile_username_toggle->{

                handleUpdateInfoUI(isChecked, binding?.editProfileUsernameTv!!, binding?.editProfileUsernameEt!!, binding?.editProfileUsernameToggle!!)
                handleUpdateInfoData(isChecked, binding?.editProfileUsernameTv!!, EditProfileInfoType.PROFILE_USERNAME.ordinal)

            }
            R.id.edit_profile_email_toggle->{

                handleUpdateInfoUI(isChecked, binding?.editProfileEmailTv!!, binding?.editProfileEmailEt!!, binding?.editProfileEmailToggle!!)
                handleUpdateInfoData(isChecked, binding?.editProfileEmailTv!!, EditProfileInfoType.PROFILE_EMAIL.ordinal)

            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.edit_profile_pic->{
                openGalleryForImage()
            }
            R.id.edit_profile_update_pass_btn->{

                androidUtils.hideKeyboard(requireContext(), view)

                val password = binding?.editProfilePasswordEt?.text.toString().trim()
                val passwordMatch = binding?.editProfileMatchPasswordEt?.text.toString().trim()

                if(isPasswordValidated(password, passwordMatch)){
                    editProfileViewModel.getUpdateAuthPassStatus().observe(viewLifecycleOwner, updateAuthPassStatusObserver)
                    editProfileViewModel.updateAuthPass(password)
                }
            }
            R.id.edit_profile_back_btn->{
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            }
        }
    }

    private fun isPasswordValidated(password:String, passwordMatch: String):Boolean{
        when {
            TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordMatch) -> {
                binding?.editProfilePasswordEt?.error = getString(R.string.blank_pass_no_update)
                binding?.editProfileMatchPasswordEt?.error = getString(R.string.blank_pass_no_update)
                return false
            }
            !androidUtils.isValidPassword(password) -> {
                binding?.editProfilePasswordEt?.error = getString(R.string.password_param_err_msg)
                return false
            }
            password != passwordMatch -> {
                binding?.editProfilePasswordEt?.error = getString(R.string.pass_dont_match_err_msg)
                binding?.editProfileMatchPasswordEt?.error = getString(R.string.pass_dont_match_err_msg)
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun openGalleryForImage() {
        Log.d(TAG, "Open gallery")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun handleUpdateInfoUI(isEditing: Boolean, textView: TextView, editText: EditText, toggleButton: ToggleButton) {
        if(isEditing) {
            androidUtils.hideView(textView)
            androidUtils.showView(editText)
            editText.setText(textView.text)
            toggleButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryLight))
            toggleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccentBlack))
        }else{
            androidUtils.hideKeyboard(requireContext(), textView)

            textView.text = editText.text
            androidUtils.showView(textView)
            androidUtils.hideView(editText)
            toggleButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            toggleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryLight))
        }
    }

    private fun handleUpdateInfoData(isEditing: Boolean, textView: TextView, infoType: Int){
        if(!isEditing){

            when(infoType){
                EditProfileInfoType.PROFILE_NAME.ordinal->{
                    when {
                        TextUtils.isEmpty(textView.text) -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.empty_profile_name))
                        }
                        textView.text.length < 3 -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.username_too_short_err))
                        }
                        else -> {
                            user.name = textView.text.toString().trim()
                            editProfileViewModel.updateCachedUser(user)
                            editProfileViewModel.updateUserValueInFirestore(PROFILE_NAME_KEY, user.name)
                            editProfileViewModel.updateFirebaseAuthProfile(PROFILE_NAME_KEY, user.name)
                        }
                    }
                }
                EditProfileInfoType.PROFILE_USERNAME.ordinal->{
                    when {
                        TextUtils.isEmpty(textView.text) -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.empty_username))
                        }
                        !textView.text.startsWith("@") -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.no_at_symbol_err))
                        }
                        textView.text.length < 4 -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.username_too_short_err))
                        }
                        else -> {
                            user.username = textView.text.toString().trim()
                            editProfileViewModel.updateCachedUser(user)
                            editProfileViewModel.updateUserValueInFirestore(USERNAME_KEY, user.username)
                        }
                    }
                }
                EditProfileInfoType.PROFILE_EMAIL.ordinal->{
                    when {
                        androidUtils.isValidEmail(textView.text.toString().trim()) && !TextUtils.isEmpty(textView.text) -> {
                            user.email = textView.text.toString().trim()
                            editProfileViewModel.getUpdateAuthEmailStatus().observe(viewLifecycleOwner, updateAuthEmailStatusObserver)
                            editProfileViewModel.updateAuthEmail(user.email)
                        }
                        else -> {
                            androidUtils.showSnackBar(requireActivity(), getString(R.string.invalid_email_data_not_saved))
                        }
                    }
                }
            }
        }
    }




    companion object {
        private val TAG = EditProfileFragment::class.simpleName
        private const val GALLERY_REQUEST_CODE = 5340
    }

}