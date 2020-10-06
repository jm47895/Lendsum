package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentEditProfileBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.EditProfileInfoType
import com.lendsumapp.lendsum.util.GlobalConstants.EMAIL_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.PROFILE_NAME
import com.lendsumapp.lendsum.util.GlobalConstants.USERNAME_KEY
import com.lendsumapp.lendsum.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    @Inject lateinit var androidUtils: AndroidUtils
    private lateinit var userObserver: Observer<User>
    private lateinit var updateUserStatusObserver: Observer<Int>
    private lateinit var updateAuthEmailStatusObserver: Observer<Boolean>
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

        binding?.editProfileNameToggle?.setOnCheckedChangeListener(this)
        binding?.editProfileUsernameToggle?.setOnCheckedChangeListener(this)
        binding?.editProfileEmailToggle?.setOnCheckedChangeListener(this)

        userObserver = Observer { cachedUser ->

            user = cachedUser

            binding?.editProfileNameTv?.text = cachedUser.name
            binding?.editProfileUsernameTv?.text = cachedUser.username
            binding?.editProfileEmailTv?.text = cachedUser.email

            Glide.with(this)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .circleCrop()
                )
                .load(cachedUser.profilePicUrl)
                .circleCrop()
                .into(binding?.editProfilePic!!)
        }
        editProfileViewModel.getUser().observe(viewLifecycleOwner, userObserver)

        updateAuthEmailStatusObserver = Observer { isAuthEmailUpdated->
            if(isAuthEmailUpdated){
                editProfileViewModel.updateCachedUser(user)
                editProfileViewModel.updateUserValueInFirestore(EMAIL_KEY, user.email)
            }else{
                androidUtils.showSnackBar(requireActivity(), getString(R.string.sign_in_again_msg))
            }
        }

        updateUserStatusObserver = Observer { rowsUpdated->
            if(rowsUpdated > 0){
                Log.d(TAG, "User object updated in room cache")
                androidUtils.showSnackBar(requireActivity(), getString(R.string.user_info_updated))
            }else{
                Log.d(TAG, "User object not updated in room cache")
            }
        }
        editProfileViewModel.getUpdateCacheUserStatus().observe(viewLifecycleOwner, updateUserStatusObserver)
    }

    override fun onStop() {
        super.onStop()
        editProfileViewModel.getUser().removeObserver(userObserver)
        editProfileViewModel.getUpdateCacheUserStatus().removeObserver(updateUserStatusObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        when(button?.id){
            R.id.edit_profile_name_toggle->{

                handleEditInfoAnimationAndData(isChecked, binding?.editProfileNameTv!!, binding?.editProfileNameEt!!, binding?.editProfileNameToggle!!, EditProfileInfoType.PROFILE_NAME.ordinal)

            }
            R.id.edit_profile_username_toggle->{

                handleEditInfoAnimationAndData(isChecked, binding?.editProfileUsernameTv!!, binding?.editProfileUsernameEt!!, binding?.editProfileUsernameToggle!!, EditProfileInfoType.PROFILE_USERNAME.ordinal)

            }
            R.id.edit_profile_email_toggle->{

                handleEditInfoAnimationAndData(isChecked, binding?.editProfileEmailTv!!, binding?.editProfileEmailEt!!, binding?.editProfileEmailToggle!!, EditProfileInfoType.PROFILE_EMAIL.ordinal)

            }
        }
    }

    private fun handleEditInfoAnimationAndData(isChecked: Boolean, textView: TextView, editText: EditText, toggleButton: ToggleButton, infoType: Int){
        if(isChecked){
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
                            editProfileViewModel.updateUserValueInFirestore(PROFILE_NAME, user.name)
                            editProfileViewModel.updateFirebaseAuthDisplayName(user.name)
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
    }
}