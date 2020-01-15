package com.max.memo3.TestSubject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.max.memo3.Confidential.Conf_Info;
import com.max.memo3.R;
import com.max.memo3.Util.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_OK;

//google sign in for android https://developers.google.com/identity/sign-in/android/start-integrating
//google sign in SCOPES from here https://developers.google.com/identity/protocols/googlescopes
public class Test4_Firestore extends Fragment {
    //var
    private Test_ViewModel viewModel;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;

    private FirebaseUser firebaseUser;

    private FirebaseFirestore firestore;

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
                .requestServerAuthCode(Conf_Info.OAuth_CLIENT_TEST4)
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"),new Scope("https://www.googleapis.com/auth/youtube.readonly"))
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
        //google login
        view.findViewById(R.id.test4_button1).setOnClickListener(this::test4_button1_onclick);
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        ((TextView)view.findViewById(R.id.test4_textView1)).setText(account==null?"Null":account.getDisplayName());
        ((SignInButton)view.findViewById(R.id.test4_google_signin)).setSize(SignInButton.SIZE_STANDARD);
        view.findViewById(R.id.test4_google_signin).setOnClickListener(this::test4_googlelogin_onclick);
        view.findViewById(R.id.test4_button2).setOnClickListener(this::test4_button2_onclick);
        view.findViewById(R.id.test4_button3).setOnClickListener(this::test4_button3_onclick);
        view.findViewById(R.id.test4_button4).setOnClickListener(this::test4_button4_onclick);
        view.findViewById(R.id.test4_button5).setOnClickListener(this::test4_button5_onclick);
        //firebase login
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ((TextView)view.findViewById(R.id.test4_textView2)).setText(firebaseUser==null?"Null":firebaseUser.getDisplayName());
        view.findViewById(R.id.test4_button6).setOnClickListener(this::test4_button6_onclick);
        view.findViewById(R.id.test4_button7).setOnClickListener(this::test4_button7_onclick);
        view.findViewById(R.id.test4_button8).setOnClickListener(this::test4_button8_onclick);
        //firestore
        firestore = FirebaseFirestore.getInstance();
        view.findViewById(R.id.test4_button9).setOnClickListener(this::test4_button9_onclick);
        view.findViewById(R.id.test4_button10).setOnClickListener(this::test4_button10_onclick);
    }

    //google login
    private void test4_button1_onclick(View view){
//        util.quickLog(getActivity().toString());
//        util.quickLog(getContext().toString());
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        util.quickLog("google not sign in ed account "+(account==null));
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
        if (requestCode == 6129){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode==RESULT_OK){
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                ((TextView)getActivity().findViewById(R.id.test4_textView2)).setText(firebaseUser.getDisplayName());
                account = GoogleSignIn.getLastSignedInAccount(getActivity());
                ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText(account.getDisplayName());
            } else if (response==null){
                util.quickLog("you cancelled it right?");
            } else {
                util.quickLog(response.getError().getErrorCode());
            }
        }
    }

    private void test4_button2_onclick(View view){
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account==null){
            util.quickLog("no google ac logined");
        } else {
            util.quickLog(".");
            util.quickLog(account.getDisplayName());
//            util.quickLog(account.getFamilyName());
//            util.quickLog(account.getGivenName());
            util.quickLog(account.getEmail());
            util.quickLog(account.getId());
            util.quickLog(account.getIdToken());
            util.quickLog(account.getServerAuthCode());
            util.quickLog(account.getAccount()==null);
//            util.quickLog(account.getAccount().name);
//            util.quickLog(account.getAccount().type);
            util.quickLog(account.getPhotoUrl().toString());
            util.quickLog(account.getRequestedScopes().size());
//            account.getRequestedScopes().forEach(scope -> util.quickLog(scope.toString()));
            util.quickLog(account.getGrantedScopes().size());
            account.getGrantedScopes().forEach(scope -> util.quickLog(scope.toString()));
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

    //firebase login
    private void test4_button5_onclick(View view){
        //using default google login setup
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.GoogleBuilder().build()
//        );
//        startActivityForResult(
//                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),6129
//        );
//        //handled above

        //using custom google login setup  (google sign in need IDtoken)
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account==null){
            util.quickLog("no google ac sign in ed, no login");
            return;
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(),null))
                .addOnCompleteListener(getActivity(),task -> {
                    if (task.isSuccessful()){
                        util.quickLog("firebase login ed");
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        ((TextView)getActivity().findViewById(R.id.test4_textView2)).setText(firebaseUser.getDisplayName());
                    } else {
                        util.quickLog("asdf");
                    }
                });
    }

    private void test4_button6_onclick(View view){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser==null){
            util.quickLog("no firebase user login ed");
        } else {
            test4_button2_onclick(view);
            util.quickLog(".");
            util.quickLog(firebaseUser.getDisplayName());
            util.quickLog(firebaseUser.getEmail());
            util.quickLog(firebaseUser.getPhoneNumber());
            util.quickLog(firebaseUser.getProviderId());
            util.quickLog(firebaseUser.getUid());
            util.quickLog(firebaseUser.getIdToken(false).getResult().getToken());
            util.quickLog(firebaseUser.getPhotoUrl().toString());
            firebaseUser.getProviderData().forEach(o -> util.quickLog(o.toString()));
            util.quickLog(firebaseUser.getMetadata().getCreationTimestamp());
            util.quickLog(firebaseUser.getMetadata().getLastSignInTimestamp());
        }
    }

    private void test4_button7_onclick(View view){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        util.quickLog(firebaseUser==null);
        AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(task -> {
            util.quickLog("firebase sign out ed");
            ((TextView)getActivity().findViewById(R.id.test4_textView2)).setText("Null");
            ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText("Null");
        });
    }

    private void test4_button8_onclick(View view){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        util.quickLog(firebaseUser==null);
        AuthUI.getInstance().delete(getActivity()).addOnCompleteListener(task -> {
            util.quickLog("firebase disconnect ed");
            ((TextView)getActivity().findViewById(R.id.test4_textView2)).setText("Null");
            ((TextView)getActivity().findViewById(R.id.test4_textView1)).setText("Null");
        });
    }

    //firestore
    private void test4_button9_onclick(View view){
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("first","Max");
        test_data.put("second","test4");
        test_data.put("third",1234);

        firestore.collection("test4")
                .add(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4 failed to add data"));
    }

    private void test4_button10_onclick(View view){

    }
}
