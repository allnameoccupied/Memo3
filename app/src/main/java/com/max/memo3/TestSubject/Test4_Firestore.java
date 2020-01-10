package com.max.memo3.TestSubject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.max.memo3.Confidential.Conf_Info;
import com.max.memo3.R;
import com.max.memo3.Util.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

//google sign in for android https://developers.google.com/identity/sign-in/android/start-integrating
public class Test4_Firestore extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;

    //func
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Test_ViewModel.class);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
//                .requestIdToken(Conf_Info.WEB_CLIENT_GOOGLE_SIGNIN)  //work   //OAuth 2.0 client IDs && type==web application will work
//                .requestIdToken(Conf_Info.WEB_CLIENT_GOOGLE_SERVICE)  //work
//                .requestIdToken(Conf_Info.OAuth_CLIENT)  //not work
                .requestIdToken(Conf_Info.OAuth_CLIENT_TEST4)  //work
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test4_frag,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setName("Test4");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.test4_button1).setOnClickListener(this::test4_button1_onclick);
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        ((TextView)view.findViewById(R.id.test4_textView1)).setText(account==null?"Null":account.getDisplayName());
        ((SignInButton)view.findViewById(R.id.test4_google_signin)).setSize(SignInButton.SIZE_STANDARD);
        view.findViewById(R.id.test4_google_signin).setOnClickListener(this::test4_googlelogin_onclick);
        view.findViewById(R.id.test4_button2).setOnClickListener(this::test4_button2_onclick);
        view.findViewById(R.id.test4_button3).setOnClickListener(this::test4_button3_onclick);
        view.findViewById(R.id.test4_button4).setOnClickListener(this::test4_button4_onclick);
    }

    private void test4_button1_onclick(View view){
//        util.quickLog(getActivity().toString());
//        util.quickLog(getContext().toString());
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        util.quickLog(account==null);
    }

    private void test4_googlelogin_onclick(View view){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,6128);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6128){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                util.quickLog(task.isSuccessful());
                account = task.getResult(ApiException.class);
            } catch (ApiException e){
                util.quickLog(e.getMessage());
            }
            ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText(account==null?"Null":account.getDisplayName());
        }
    }

    private void test4_button2_onclick(View view){
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account==null){
            util.quickLog("no google ac logined");
        } else {
            util.quickLog(account.getDisplayName());
//            util.quickLog(account.getFamilyName());
//            util.quickLog(account.getGivenName());
            util.quickLog(account.getEmail());
            util.quickLog(account.getId());
            util.quickLog(account.getIdToken());
//            util.quickLog(account.getServerAuthCode());
//            util.quickLog(account.getAccount()==null?"Null":"XNull");
//            util.quickLog(account.getAccount().name);
//            util.quickLog(account.getAccount().type);
//            util.quickLog(account.getPhotoUrl().toString());
            util.quickLog(account.getRequestedScopes().size());
//            account.getRequestedScopes().forEach(scope -> util.quickLog(scope.toString()));
            util.quickLog(account.getGrantedScopes().size());
//            account.getGrantedScopes().forEach(scope -> util.quickLog(scope.toString()));
        }
    }

    private void test4_button3_onclick(View view){
//        util.quickLog("asdf");
        googleSignInClient.signOut().addOnCompleteListener(getActivity(),task -> util.quickLog("sign out ed"));
        ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText("Null");
    }

    private void test4_button4_onclick(View view){
//        util.quickLog("asdf");
        googleSignInClient.revokeAccess().addOnCompleteListener(getActivity(),task -> util.quickLog("disconnect ed"));
        ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText("Null");
    }
}
