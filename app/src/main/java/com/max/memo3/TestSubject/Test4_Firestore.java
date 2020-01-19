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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.max.memo3.Confidential.Conf_Info;
import com.max.memo3.R;
import com.max.memo3.Util.util;

import java.util.Arrays;
import java.util.Date;
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
        view.findViewById(R.id.test4_button11).setOnClickListener(this::test4_button11_onclick);
        view.findViewById(R.id.test4_button12).setOnClickListener(this::test4_button12_onclick);
        view.findViewById(R.id.test4_button13).setOnClickListener(this::test4_button13_onclick);
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
    //add
    private void test4_button9_onclick(View view){
        //1st test basic ADD
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("first","Max");
        test_data.put("second","test4");
        test_data.put("third",1234);

        firestore.collection("test4")
                .add(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_1 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_1 failed to add data"));
         */

        //2nd test basic SET
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("qwer","qwer");
        test_data.put("asdf","asdf");
        test_data.put("zxcv",1234);

        firestore.collection("test4").document("test4_2")
                .set(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_2 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_2 failed to add data"));
         */

        //3rd test all different acceptable type
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("string","qwer");
        test_data.put("bool",true);
        test_data.put("int",1000000);
        test_data.put("float",12.34);
        test_data.put("date",new Timestamp(new Date()));
        test_data.put("server timestamp", FieldValue.serverTimestamp());
        test_data.put("list",Arrays.asList(1,2,3,4,5));
        test_data.put("null",null);

        test_data.put("int2",7890);

        Map<String,Object> nest_data = new HashMap<>();
        nest_data.put("tyui","tyui");
        nest_data.put("ghjk",5678);

        test_data.put("nest",nest_data);

        firestore.collection("test4").document("test4_3")
                .set(test_data, SetOptions.merge())
//                .set(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_3_1 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_3_1 failed to add data"));
        firestore.collection("test4").document("test4_3").collection("test_coll_1").document("sub_data_1")
                .set(nest_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_3_2 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_3_2 failed to add data"));
        firestore.collection("test4").document("test4_3").collection("test_coll_2").document("sub_data_2")
                .set(nest_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_3_3 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_3_3 failed to add data"));
         */

        //4th test main not add
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("int",1000000);
        test_data.put("list",Arrays.asList(1,2,3,4,5));
        test_data.put("date",new Timestamp(new Date()));
        test_data.put("server timestamp", FieldValue.serverTimestamp());

        Map<String,Object> nest_data = new HashMap<>();
        nest_data.put("tyui","tyui");
        nest_data.put("ghjk",5678);

        test_data.put("nest",nest_data);

        DocumentReference documentReference1 = firestore.collection("test4").document("test4_4");
        documentReference1
                .set(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_4_1 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_4_1 failed to add data"));
        CollectionReference collectionReference1 = firestore.collection("test4").document("test4_4").collection("test_coll_1");
        collectionReference1
                .document("sub_data_1")
                .set(nest_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_4_2 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_4_2 failed to add data"));
        DocumentReference documentReference2 = firestore.collection("test4").document("test4_4").collection("test_coll_2").document("sub_data_2");
        documentReference2
                .set(nest_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_4_3 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_4_3 failed to add data"));
        */

        //5th test document reference
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("int1",50);
        test_data.put("int2",100);
        test_data.put("date",new Timestamp(new Date()));
        test_data.put("server timestamp", FieldValue.serverTimestamp());

        DocumentReference documentReference1 = firestore.collection("test4").document("test4_5");
        documentReference1
                .set(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_5 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_5 failed to add data"));
        */

        //6th test batch add
        /**
        Map<String,Object> test_data1 = new HashMap<>();
        test_data1.put("int",50);
        Map<String,Object> test_data2 = new HashMap<>();
        test_data2.put("int",100);
        Map<String,Object> test_data3 = new HashMap<>();
        test_data3.put("int",150);

        WriteBatch batch = firestore.batch();
        CollectionReference collectionReference = firestore.collection("test4").document("test4_6").collection("test4_6");
        batch.set(collectionReference.document("data_1"),test_data1);
        batch.set(collectionReference.document("data_2"),test_data2);
        batch.set(collectionReference.document("data_3"),test_data3);
        batch.commit().addOnSuccessListener(documentReference -> util.quickLog("test4_6 successfully add data"));
         */

        //7th test snapshot listener (monitor change realtime)
        /**
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("int",50);

        DocumentReference documentReference = firestore.collection("test4").document("test4_7");
        documentReference.set(test_data).addOnSuccessListener(aVoid -> util.quickLog("test4_7 successfully add data"));
        ListenerRegistration registration = documentReference.addSnapshotListener((documentSnapshot, e) -> {
            util.quickLog("test4_7 data changed");
            util.quickLog(documentSnapshot);
        });
        util.makeCountDownTimer(30 * 1000, 30 * 1001, new util.CountDownTimerImplementation() {
            @Override
            public void onTick(long ms_untilFinish) {
//                util.quickLog(ms_untilFinish);
            }

            @Override
            public void onFinish() {
                registration.remove();
                util.quickLog("test4_7 listener removed");
            }
        });
         */

        //8th test main not add
        /**
        Map<String,Object> test_data1 = new HashMap<>();
        test_data1.put("int",50);
        test_data1.put("int2",50);
        Map<String,Object> test_data2 = new HashMap<>();
        test_data2.put("int",100);
        test_data2.put("int2",200);
        Map<String,Object> test_data3 = new HashMap<>();
        test_data3.put("int",150);
        test_data3.put("int2",250);
        Map<String,Object> test_data4 = new HashMap<>();
        test_data4.put("int",150);
        test_data4.put("int2",190);
        Map<String,Object> test_data5 = new HashMap<>();
        test_data5.put("int",150);
        test_data5.put("int2",230);

        WriteBatch batch = firestore.batch();
        CollectionReference collectionReference = firestore.collection("test4").document("test4_8").collection("test4_8");
        batch.set(collectionReference.document("data_1"),test_data1);
        batch.set(collectionReference.document("data_2"),test_data2);
        batch.set(collectionReference.document("data_3"),test_data3);
        batch.set(collectionReference.document("data_4"),test_data4);
        batch.set(collectionReference.document("data_5"),test_data5);
        batch.commit().addOnSuccessListener(documentReference -> util.quickLog("test4_8 successfully add data"));
         */

        //9th test security rule
        Map<String,Object> test_data = new HashMap<>();
        test_data.put("first","Max");
        test_data.put("second","test4");
        test_data.put("third",1234);

        firestore.collection("test4_9").document("test4_9").collection("test4_9")
                .add(test_data)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_9 successfully add data"))
                .addOnFailureListener(e -> util.quickLog("test4_9 failed to add data"));
    }

    //read
    private void test4_button10_onclick(View view){
        //1st test basic get
        /**
        firestore.collection("test4")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        util.quickLog("test4 successfully get");
                        for (QueryDocumentSnapshot document:task.getResult()){
//                            util.quickLog(document.getData().get("first"));
                            util.quickLog(document);
                        }
                    } else {
                        util.quickLog("test4 failed to get");
                        util.quickLog(task.getException());
                    }
                });
        */

        //2nd test basic get
        /**
        firestore.collection("test4").document("test4_2")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        util.quickLog("test4 successfully get");
//                        util.quickLog(task.getResult().getData().get("zxcv"));
//                        util.quickLog(task.getResult());
                        util.quickLog(task.getResult().getData());
                    } else {
                        util.quickLog("test4 failed to get");
                        util.quickLog(task.getException());
                    }
                });
        */

        //3rd test main not get
        /**
        firestore.collection("test4").document("test4_3")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        util.quickLog("test4 successfully get");
//                        util.quickLog(task.getResult().getData().get("zxcv"));
                        util.quickLog(task.getResult());
//                        util.quickLog(task.getResult().getData());
//                        for (DocumentSnapshot document:task.getResult()){
//                            util.quickLog(document);
//                        }
                    } else {
                        util.quickLog("test4 failed to get");
                        util.quickLog(task.getException());
                    }
                });
         */

        //4th test get one field
        /**
        firestore.collection("test4").document("test4_4")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        util.quickLog("test4 successfully get");
//                        util.quickLog(task.getResult().getData().get("zxcv"));
                        util.quickLog(task.getResult());
//                        util.quickLog(task.getResult().getData());
//                        for (DocumentSnapshot document:task.getResult()){
//                            util.quickLog(document);
//                        }
                    } else {
                        util.quickLog("test4 failed to get");
                        util.quickLog(task.getException());
                    }
                });
         */

        //6th test filter get
        /**
        firestore.collection("test4").document("test4_6").collection("test4_6")
                .whereGreaterThanOrEqualTo("int",100)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            util.quickLog(documentSnapshot);
                        }
                    }
                });
         */

        //8th test deeper filter get, ignore cursor && search in array
        //https://firebase.google.com/docs/firestore/query-data/queries?authuser=0
        /**
        CollectionReference collectionReference = firestore.collection("test4").document("test4_8").collection("test4_8");
//        util.quickLog("qwer");
        Query query = collectionReference
                .whereGreaterThanOrEqualTo("int",100).orderBy("int")  //"where" and next "order by" need to do on same field
//                .whereGreaterThan("int2",200).orderBy("int2", Query.Direction.DESCENDING);
                .limit(10);
//        util.quickLog("asdf");
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()){
                            util.quickLog(snapshot);
                        }
                    }
                });
         */
    }

    //update
    private void test4_button11_onclick(View view){
        //4th test basic update, set>fieldvalue.increment
        /**
        firestore.collection("test4").document("test4_4")
                .update("int",20,"nest.tyui","qwer","list",FieldValue.arrayUnion(6),"list",FieldValue.arrayRemove(3),"nest.ghjk",FieldValue.increment(-1))
//                .update("int",FieldValue.increment(-1),"nest.tyui","qwer","list",FieldValue.arrayUnion(6),"list",FieldValue.arrayRemove(3),"int",20)
                .addOnSuccessListener(aVoid -> util.quickLog("test4_4 successfully update"));
         */
    }

    //delete
    private void test4_button12_onclick(View view){
        //5th test (1) basic delete
        /**
        firestore.collection("test4").document("test4_5")
                .delete()
                .addOnSuccessListener(documentReference -> util.quickLog("test4_5 successfully delete"))
                .addOnFailureListener(e -> util.quickLog("test4_5 failed to delete"));
         */

        //5th test (2) delete one field only, delete non-existing field will still be success, only no effect
        /**
        Map<String,Object> updates = new HashMap<>();
        updates.put("int2",FieldValue.delete());
        firestore.collection("test4").document("test4_5")
                .update(updates)
                .addOnSuccessListener(documentReference -> util.quickLog("test4_5 successfully delete"))
                .addOnFailureListener(e -> util.quickLog("test4_5 failed to delete"));
         */
    }

    //toggle online mode
    private boolean isOn = true;
    private void test4_button13_onclick(View view){
        if (isOn){
            firestore.disableNetwork()
                    .addOnCompleteListener(task -> {
                        util.quickLog("firestore disabled network");
                        isOn = false;
                    });
        } else {
            firestore.enableNetwork()
                    .addOnCompleteListener(task -> {
                        util.quickLog("firestore enabled network");
                        isOn = true;
                    });
        }
    }
}
